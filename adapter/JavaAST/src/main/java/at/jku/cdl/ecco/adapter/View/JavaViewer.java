package at.jku.cdl.ecco.adapter.View;

import at.jku.cdl.ecco.adapter.JavaASTPlugin;
import at.jku.cdl.ecco.adapter.artifactData.ASTNodeType;
import at.jku.cdl.ecco.adapter.artifactData.JavaASTData;
import at.jku.isse.ecco.adapter.AssociationInfo;
import at.jku.isse.ecco.adapter.AssociationInfoArtifactViewer;
import at.jku.isse.ecco.adapter.dispatch.PluginArtifactData;

import at.jku.isse.ecco.core.Association;
import at.jku.isse.ecco.tree.Node;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Callback;

import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class JavaViewer extends BorderPane implements AssociationInfoArtifactViewer {
    private final HashMap<String, AssociationInfo> associationInfos;
    private final HashMap<String, PropertyChangeListener> associationListeners ;
    private ObservableList<JavaBlockInterface> javaBlocks = FXCollections.observableArrayList();
    public ListView<JavaBlockInterface> listView;

    public static Border highlightBorder = new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT));;

    private Node selectedNode;

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

        selectedNode = node;
        setupTopShowNode(node);
        listView.getItems().clear();
        try {
            if (node.getArtifact().getData() instanceof PluginArtifactData) {
                for (Node child : node.getChildren()) {
                    if (child.getArtifact().getData() instanceof JavaASTData childData) {
                        if(childData.getType().equals(ASTNodeType.IMPORT_DECLARATION) || childData.getType().equals(ASTNodeType.PACKAGEDECLARATION)) {
                            javaBlocks.add( new Statement(child,selectedNode,getColorForNode(child),0));
                        }else if(childData.getType().equals(ASTNodeType.TYPE_DECLARATION)) {
                            javaBlocks.add(new TypeDec(child,selectedNode, this));
                        }else if(childData.getType().equals(ASTNodeType.ENUM_DECLARATION) ) {
                                javaBlocks.add(new EnumBlock(child, selectedNode, this,  0));
                            }
                    }
                }
            }else if (node.getArtifact().getData() instanceof JavaASTData) showTreeRecursive(node);

            listView.setPadding(new Insets(0,0,0,0));

        } catch (Exception e) {
            javaBlocks.clear();
            javaBlocks.add(new ErrorNode(node,"exception was " + e));
        }
        this.setCenter(listView);
    }

    private void showTreeRecursive(Node node){
        javaBlocks.clear();
         handleTreeNode(node);
    }

    private void setupTopShowNode(Node node){
        HBox nodeInfoBox = new HBox();
        Label NodeinfoLabel = new Label("The selected Node is : " );
        Label NodeDescLabel = new Label(node.toString());
        NodeDescLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        nodeInfoBox.getChildren().addAll(NodeinfoLabel,NodeDescLabel);
        this.setTop(nodeInfoBox);
    }


    private void handleTreeNode(Node node) {
        JavaASTData data = (JavaASTData) node.getArtifact().getData();
        if (data.getType().equals(ASTNodeType.TYPE_DECLARATION) ) {
            javaBlocks.add(new TypeDec(node,selectedNode, this));

        }else if (data.getType().equals(ASTNodeType.FIELD_GROUP)) {
            javaBlocks.add(new FieldGroup(node, selectedNode,  this,  0));

        } else if (data.getType().equals(ASTNodeType.METHOD_DECLARATION)) {
            javaBlocks.add(new GenericNestedNode(node, selectedNode,this,0));
        }else if(data.getType().equals(ASTNodeType.ENUM_DECLARATION)) {
            javaBlocks.add(new EnumBlock(node, selectedNode, this,  0));
        }else if(data.getType().equals(ASTNodeType.IMPORT_DECLARATION) || data.getType().equals(ASTNodeType.PACKAGEDECLARATION)) {
            javaBlocks.add(new Statement(node,selectedNode, getColorForNode(node), 0));
        }else showTreeRecursive(node.getParent());
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
        return JavaASTPlugin.class.getName();
    }

    public static void highlightBox(VBox box) {
        box.setBorder(highlightBorder);
    }


}
