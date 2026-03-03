package at.jku.isse.ecco.adapter.java.View;

import at.jku.isse.ecco.adapter.java.JavaTreeArtifactData;
import at.jku.isse.ecco.tree.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;

public class TypeDecBlock extends AbstractDecBlock {


    private List<JavaBlockInterface> bodyInterfaces = new ArrayList<>();
    private SimpleBlock extendsBlock;
    private List<SimpleBlock> implementationBlocks = new ArrayList<>();

    public TypeDecBlock(Node javaTypeDecNode, JavaViewer javaViewer,int depthOfParent,boolean isIndented) {
        super(javaTypeDecNode,javaViewer,depthOfParent,isIndented);
        parseChildren();

        for (int i = 0; i < implementationBlocks.size(); i++) {
            if (i == 0 && i != implementationBlocks.size() -1 ) implementationBlocks.get(i).insertText(", ", false);
        }
    }


    @Override
    protected void handleSpecificNodes( Node child) {

        if (child.getArtifact().getData() instanceof JavaTreeArtifactData childData) {
            if (childData.getType().equals(JavaTreeArtifactData.NodeType.DECLARATION_EXTENDS)) {
                handleDecExtendsNode(child);
            } else if (childData.getType().equals(JavaTreeArtifactData.NodeType.DECLARATION_IMPLEMENTS)) {
                handleImplementsNode(child);
            } else if (childData.getType().equals(JavaTreeArtifactData.NodeType.AFTER)) {
                handleClassBody(child);
            }
        }
    }

    @Override
    public void setBackGroundColor(String aId, Color newColor) {
        super.setBackGroundColor(aId, newColor);
        extendsBlock.setBackGroundColor(aId, newColor);
        for (SimpleBlock simpleBlock : implementationBlocks) {
            simpleBlock.setBackGroundColor(aId, newColor);
        }
        for (JavaBlockInterface javaBlockInterface : bodyInterfaces) {
            javaBlockInterface.setBackGroundColor(aId, newColor);
        }
    }

    @Override
    public VBox getCellContent() {
        VBox content = super.getCellContent();

        Label typeNameLabel = setupLabel(text);
        decLabels.add(typeNameLabel);
        mainSignature.getChildren().add(typeNameLabel);

        if (extendsBlock != null)  mainSignature.getChildren().add(extendsBlock.getCellContent());

        if (!implementationBlocks.isEmpty()) {
            mainSignature.getChildren().add(setupLabel(" implements "));
            for (SimpleBlock block : implementationBlocks) {
                mainSignature.getChildren().add(block.getCellContent());
            }
        }
        Label lbrace = setupLabel("{");
        decLabels.add(lbrace);
        mainSignature.getChildren().add(lbrace);
        content.getChildren().add(mainSignature);

        for (JavaBlockInterface bodyNode : bodyInterfaces) {
            content.getChildren().add(bodyNode.getCellContent());
        }

        Label rbrace = setupLabel(getIndentation() + "}");
        decLabels.add(rbrace);
        content.getChildren().add(rbrace);
        return content;
    }

    private void handleDecExtendsNode( Node ExtendsNode){
        if (ExtendsNode.getChildren().isEmpty()) return;
        extendsBlock = new SimpleBlock(ExtendsNode.getChildren().getFirst(),javaViewer.getColorForNode(ExtendsNode)," extends ", true);
    }
    private void handleImplementsNode( Node ImplNode){
        if (ImplNode.getChildren().isEmpty()) return;
        for (Node child : ImplNode.getChildren()) {
            implementationBlocks.add(new SimpleBlock(child,javaViewer.getColorForNode(child)));
        }
    }
    private void handleClassBody(Node afterNode){
        int childDepth = depth +1;
        for (Node child : afterNode.getChildren()) {
            if (child.getArtifact().getData() instanceof JavaTreeArtifactData childData) {
                if (childData.getType().equals(JavaTreeArtifactData.NodeType.FIELD_DECLARATION)) {
                    bodyInterfaces.add(new FieldDec(child,javaViewer,childDepth,isIndented));
                } else if  (childData.getType().equals(JavaTreeArtifactData.NodeType.METHOD_DECLARATION)) {
                    bodyInterfaces.add(new MethodDec(child,javaViewer,childDepth,isIndented));
                }
            }
        }
    }
}
