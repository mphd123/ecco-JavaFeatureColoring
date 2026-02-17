package at.jku.isse.ecco.adapter.java.View;

import at.jku.isse.ecco.adapter.AssociationInfo;
import at.jku.isse.ecco.adapter.AssociationInfoArtifactViewer;
import at.jku.isse.ecco.adapter.java.JavaPlugin;
import at.jku.isse.ecco.core.Association;
import at.jku.isse.ecco.tree.Node;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.ObservableFaceArray;
import javafx.util.Callback;

import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class JavaFeatureColorView extends BorderPane implements AssociationInfoArtifactViewer {
    private final HashMap<String, AssociationInfo> associationInfos;
    private final HashMap<String, PropertyChangeListener> associationListeners ;

    ObservableList<CodeLine> codeLines = FXCollections.observableArrayList();
    ListView<CodeLine> listView;

    public JavaFeatureColorView() {
        associationInfos = new HashMap<>();
        associationListeners = new HashMap<>();
        listView = new ListView<>(codeLines);
        listView.setFocusTraversable(false);
        this.setCenter(listView);
        listView.setCellFactory(new Callback<ListView<CodeLine>, ListCell<CodeLine>>() {
            @Override
            public ListCell<CodeLine> call(ListView<CodeLine> param) {
                ListCell<CodeLine> cell = new ListCell<>() {
                    @Override
                    protected void updateItem(CodeLine line, boolean empty) {
                        Label old = (Label) getGraphic();
                        if (old != null) {
                            old.backgroundProperty().unbind();
                        }

                        super.updateItem(line, empty);

                        if (empty || null == line) {
                            setGraphic(null);
                        } else {
                            setGraphic(getCellContent(line));
                        }
                    }
                };
                return cell;
            }
        });
    }

    @Override
    public String getPluginId() {
        return JavaPlugin.class.getName();
    }

    /*
        note : nodes for java are made up of one class and the rest as simple line by line as child nodes
        although by using V1 and V2 as commits there seems to be a line missing in the example
     */

    @Override
    public void showTree(Node node) {
        codeLines.clear();

        for (Node child : node.getChildren()) {

            Association assoc = child.getArtifact().getContainingNode() != null ? child.getArtifact().getContainingNode().getContainingAssociation() : null;
            Color initialColor = Color.WHITE;
            if (assoc != null && associationInfos.containsKey(assoc.getId())) {
                Object val = associationInfos.get(assoc.getId()).getPropertyValue("color");
                if (val instanceof Color col) {
                    initialColor = col;
                }
            }

            CodeLine line = new CodeLine(child, initialColor);
            codeLines.add(line);
        }
    }

    private Label getCellContent(CodeLine line){
        Label l = new Label(line.getText());
        l.backgroundProperty().set(line.backgroundProperty().getValue());
        l.backgroundProperty().bind(line.backgroundProperty());
        return l;
    }

    @Override
    public void setAssociationInfos(Collection<AssociationInfo> associationInfos) {
        for (Map.Entry<String, AssociationInfo> entry : this.associationInfos.entrySet()) {
            entry.getValue().removePropertyChangeListener(associationListeners.get(entry.getKey()));
        }

        this.associationInfos.clear();
        associationListeners.clear();

        if (associationInfos == null) {
            return;
        }

        for (AssociationInfo ai : associationInfos) {
            this.associationInfos.put(ai.getAssociation().getId(), ai);
        }

        for (AssociationInfo ai : this.associationInfos.values()) {
            final PropertyChangeListener pcl = getColorPropertyListener();
            ai.addPropertyChangeListener(pcl);
            associationListeners.put(ai.getAssociation().getId(), pcl);
        }
        listView.refresh();
    }

    private PropertyChangeListener getColorPropertyListener() {
        return evt -> {
            if (evt.getPropertyName().equals("color")) {
                String aId = ((AssociationInfo) evt.getSource()).getAssociation().getId();
                for (CodeLine line : codeLines) {
                    if (line.getAssociation() != null && aId.equals(line.getAssociation().getId())) {
                        line.backgroundColorProperty().set((Color) evt.getNewValue());
                    }
                }
                listView.refresh();
            }
        };
    }
}


