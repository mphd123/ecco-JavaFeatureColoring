package at.jku.isse.ecco.adapter.java.View;

import at.jku.isse.ecco.adapter.AssociationInfo;
import at.jku.isse.ecco.adapter.AssociationInfoArtifactViewer;
import at.jku.isse.ecco.adapter.java.JavaFileArtifactData;
import at.jku.isse.ecco.adapter.java.JavaPlugin;
import at.jku.isse.ecco.adapter.java.JavaTreeArtifactData;
import at.jku.isse.ecco.core.Association;
import at.jku.isse.ecco.tree.Node;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class JavaViewer extends BorderPane implements AssociationInfoArtifactViewer {
    private final HashMap<String, AssociationInfo> associationInfos;
    private final HashMap<String, PropertyChangeListener> associationListeners ;

    ObservableList<JavaBlockInterface> javaBlocks = FXCollections.observableArrayList();
    ListView<JavaBlockInterface> listView;

    public JavaViewer() {
        associationInfos = new HashMap<>();
        associationListeners = new HashMap<>();
        listView = new ListView<>(javaBlocks);
        listView.setFocusTraversable(false);

        listView.setCellFactory(new Callback<ListView<JavaBlockInterface>, ListCell<JavaBlockInterface>>() {
            @Override
            public ListCell<JavaBlockInterface> call(ListView<JavaBlockInterface> param) {
                ListCell<JavaBlockInterface> cell = new ListCell<>() {
                    @Override
                    protected void updateItem(JavaBlockInterface block, boolean empty) {
                        VBox old = (VBox) getGraphic();
                        if (old != null) {
                            for (javafx.scene.Node n : old.getChildren()) {
                                if (n instanceof Label l) {
                                    l.backgroundProperty().unbind();
                                }
                            }
                        }

                        super.updateItem(block, empty);

                        if (empty || null == block) {
                            setGraphic(null);
                        } else {
                            setGraphic(block.getCellContent());
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

    @Override
    public void showTree(Node node) {
        javaBlocks.clear();
        if (node.getArtifact().getData() instanceof JavaFileArtifactData) {
            for (Node child : node.getChildren()) {
                if (child.getArtifact().getData() instanceof JavaTreeArtifactData data) {
                    System.out.println(child.getArtifact().getData());

                    if (data.getType().equals(JavaTreeArtifactData.NodeType.SIMPLE_JUST_A_STRING)) {
                        // under JavaFileArtifactData there should either be imports (just a simple String) or Type Declaration
                        javaBlocks.add(new SimpleBlock(child,getColorForNode(child)));
                    }else if (data.getType().equals(JavaTreeArtifactData.NodeType.TYPE_DECLARATION)) {
                        javaBlocks.add(new TypeDecBlock(child,getColorForNode(child)));
                    }
                }

            }
        }
        this.setCenter(listView);
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

    private Color getColorForNode(Node child ) {
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

    private PropertyChangeListener getColorPropertyListener() {
        return evt -> {
            if (evt.getPropertyName().equals("color")) {
                String aId = ((AssociationInfo)evt.getSource()).getAssociation().getId();
                for (JavaBlockInterface block : javaBlocks) {
                    block.setBackGroundColor(aId,(Color)evt.getNewValue());
                }
                listView.refresh();
            }
        };
    }


}
