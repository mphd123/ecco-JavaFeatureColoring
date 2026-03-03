package at.jku.isse.ecco.adapter.java.View;

import at.jku.isse.ecco.adapter.AssociationInfo;
import at.jku.isse.ecco.adapter.AssociationInfoArtifactViewer;
import at.jku.isse.ecco.adapter.dispatch.PluginArtifactData;
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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
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
    public void showTree(Node node) {
        HBox nodeInfoBox = new HBox();
        Label NodeinfoLabel = new Label("The selected Node is : " );
        Label NodeDescLabel = new Label(node.toString());
        NodeDescLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        nodeInfoBox.getChildren().addAll(NodeinfoLabel,NodeDescLabel);
        this.setTop(nodeInfoBox);
        
        showTreeRecursive(node);
        this.setCenter(listView);
    }

    private void showTreeRecursive(Node node){
        javaBlocks.clear();
        if (node.getArtifact().getData() instanceof PluginArtifactData) {
            node = node.getChildren().getFirst();
        }
        if (node.getArtifact().getData() instanceof JavaFileArtifactData) handleFileNode(node);
        else if (node.getArtifact().getData() instanceof JavaTreeArtifactData) handleTreeNode(node);
        else javaBlocks.add(new NotImplementedNode(node));

    }

    private void handleFileNode(Node node) {
        for (Node child : node.getChildren()) {
            if (child.getArtifact().getData() instanceof JavaTreeArtifactData data) {
                if (data.getType().equals(JavaTreeArtifactData.NodeType.SIMPLE_JUST_A_STRING)) {
                    // under JavaFileArtifactData there should either be imports (just a simple String) or Type Declaration
                    javaBlocks.add(new SimpleBlock(child, getColorForNode(child)));
                } else if (data.getType().equals(JavaTreeArtifactData.NodeType.TYPE_DECLARATION)) {
                    javaBlocks.add(new TypeDecBlock(child, this, 0, true));
                }
            }
        }
    }

    private void handleTreeNode(Node node) {
        JavaTreeArtifactData data = (JavaTreeArtifactData) node.getArtifact().getData();
        if (data.getType().equals(JavaTreeArtifactData.NodeType.TYPE_DECLARATION) ) {
            javaBlocks.add(new TypeDecBlock(node, this));
        }
        else if (data.getType().equals(JavaTreeArtifactData.NodeType.FIELD_DECLARATION)) {
            javaBlocks.add(new FieldDec(node,this));
        } else if  (data.getType().equals(JavaTreeArtifactData.NodeType.METHOD_DECLARATION)) {
            javaBlocks.add(new MethodDec(node,this));
        } else if (data.getType().equals(JavaTreeArtifactData.NodeType.SIMPLE_JUST_A_STRING)
                &&  (node.getParent().getArtifact().getData() instanceof JavaTreeArtifactData parentData
                && parentData.getType().equals(JavaTreeArtifactData.NodeType.METHOD_DECLARATION))
                || node.getParent().getArtifact().getData() instanceof JavaFileArtifactData
        ) {
            // this is here to filter out other SIMPLE_JUST_A_STRING String nodes as displaying them by themselves only makes sense for Statements and Imports
            javaBlocks.add(new SimpleBlock(node,getColorForNode(node)));
        } else showTreeRecursive(node.getParent());
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

    public Color getColorForNode(Node child) {
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

    @Override
    public String getPluginId() {
        return JavaPlugin.class.getName();
    }


}
