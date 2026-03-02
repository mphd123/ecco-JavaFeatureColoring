package at.jku.isse.ecco.adapter.java.View;

import at.jku.isse.ecco.adapter.java.JavaTreeArtifactData;
import at.jku.isse.ecco.tree.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDecBlock extends AbstractJavaBlock {
    private List<SimpleBlock> annotationBlocks = new ArrayList<>();
    private List<SimpleBlock> modiferBlocks = new ArrayList<>();
    protected int  modifierCount = 0;
    protected List<Label> decLabels = new ArrayList<>();
    protected HBox mainSignature = new HBox();
    protected static List<JavaTreeArtifactData.NodeType> handledNoteTypes  = new ArrayList<>();
    protected JavaViewer javaViewer;

    public AbstractDecBlock(Node javaTypeDecNode, JavaViewer javaViewer,int depthOfParent,boolean isIndented) {
        super(javaTypeDecNode,javaViewer.getColorForNode(javaTypeDecNode),depthOfParent,isIndented);
        handledNoteTypes.add(JavaTreeArtifactData.NodeType.MODIFIER);
        this.javaViewer = javaViewer;
    }

    public AbstractDecBlock(Node javaTypeDecNode, JavaViewer javaViewer) {
        this(javaTypeDecNode,javaViewer,0,false);
    }

    protected abstract void handleSpecificNodes(Node node);

    protected void parseChildren() {
        for (Node child : node.getChildren()){
            if (child.getArtifact().getData() instanceof JavaTreeArtifactData childData){
                if (childData.getType().equals(JavaTreeArtifactData.NodeType.MODIFIER)) {
                    handleModifierNode(child);
                }else handleSpecificNodes(child);
            }
        }
    }

    private void handleModifierNode( Node ModiferNode){
        for (Node child : ModiferNode.getChildren()) {
            if(child.getArtifact().getData() instanceof JavaTreeArtifactData data ){
                if(data.getDataAsString().startsWith("@")){
                    annotationBlocks.add(new SimpleBlock(child,javaViewer.getColorForNode(child),depth,isIndented));
                }else{
                    if (modifierCount == 0) modiferBlocks.add(new SimpleBlock(child,javaViewer.getColorForNode(child), depth,isIndented));
                    else modiferBlocks.add(new SimpleBlock(child,javaViewer.getColorForNode(child), " ", false));
                    modifierCount++;
                }
            }
        }
    }

    @Override
    public VBox getCellContent() {
        mainSignature.getChildren().clear();
        VBox content = new VBox();
        for (SimpleBlock anno : annotationBlocks) {
            content.getChildren().add( anno.getCellContent());
        }
        for (SimpleBlock mod : modiferBlocks) {
            mainSignature.getChildren().add( mod.getCellContent());
        }
        return content;
    }

    @Override
    public void setBackGroundColor(String aId, Color newColor) {
        super.setBackGroundColor(aId, newColor);

        for (SimpleBlock simpleBlock : annotationBlocks) {
            simpleBlock.setBackGroundColor(aId, newColor);
        }
        for (SimpleBlock simpleBlock : modiferBlocks) {
            simpleBlock.setBackGroundColor(aId, newColor);
        }
    }
}
