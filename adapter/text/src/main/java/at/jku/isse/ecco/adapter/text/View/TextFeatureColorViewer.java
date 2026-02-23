package at.jku.isse.ecco.adapter.text.View;

import at.jku.isse.ecco.adapter.AssociationInfo;
import at.jku.isse.ecco.adapter.AssociationInfoArtifactViewer;
import at.jku.isse.ecco.adapter.dispatch.PluginArtifactData;
import at.jku.isse.ecco.adapter.text.TextPlugin;
import at.jku.isse.ecco.core.Association;
import at.jku.isse.ecco.tree.Node;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TextFeatureColorViewer extends BorderPane implements AssociationInfoArtifactViewer {
    private final HashMap<String, AssociationInfo> associationInfos;
    private final HashMap<String, PropertyChangeListener> associationListeners ;

    ObservableList<TextLine> textLines = FXCollections.observableArrayList();
    ListView<TextLine> listView;

    public TextFeatureColorViewer() {
        associationInfos = new HashMap<>();
        associationListeners = new HashMap<>();
        listView = new ListView<>(textLines);
        listView.setFocusTraversable(false);

        listView.setCellFactory(new Callback<ListView<TextLine>, ListCell<TextLine>>() {
            @Override
            public ListCell<TextLine> call(ListView<TextLine> param) {
                ListCell<TextLine> cell = new ListCell<>() {
                    @Override
                    protected void updateItem(TextLine line, boolean empty) {
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
        return TextPlugin.class.getName();
    }

    @Override
    public void showTree(Node node) {
        textLines.clear();
        if (node.getArtifact().getData() instanceof PluginArtifactData) {
            for (Node child : node.getChildren()) {
                TextLine line = new TextLine(child, getColorForNode(child));
                textLines.add(line);
            }
        } else {
            // TextLine creates an error text in the case that node does not contain a LineArtifactData data
            TextLine line = new TextLine(node, getColorForNode(node));
            textLines.add(line);
        }

        this.setCenter(listView);
    }

    private Color getColorForNode(Node child ){
        Color color = Color.WHITE;
        Association assoc = child.getArtifact().getContainingNode() != null ? child.getArtifact().getContainingNode().getContainingAssociation() : null;
        if (assoc != null && associationInfos.containsKey(assoc.getId())) {
            Object val = associationInfos.get(assoc.getId()).getPropertyValue("color");
            if (val instanceof Color col) {
                color = col;
            }
        }
        return color;
    }

    private Label getCellContent(TextLine line){
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
                for (TextLine line : textLines) {
                    if (line.getAssociation() != null && aId.equals(line.getAssociation().getId())) {
                        line.backgroundColorProperty().set((Color) evt.getNewValue());
                    }
                }
                listView.refresh();
            }
        };
    }
}


