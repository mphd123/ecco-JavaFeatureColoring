package at.jku.isse.ecco.adapter.java.View;

import at.jku.isse.ecco.adapter.java.JavaTreeArtifactData;
import at.jku.isse.ecco.tree.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class MethodDec extends AbstractDecBlock{
    private final List<JavaBlockInterface> bodyInterfaces = new ArrayList<>();
    private final List<JavaBlockInterface> parameterInterfaces = new ArrayList<>(); // again not sure how i should handle parameters from the example
    public MethodDec(Node javaTypeDecNode, JavaViewer javaViewer,int depthOfParent,boolean isIndented) {
        super(javaTypeDecNode, javaViewer,depthOfParent,isIndented);
        parseChildren();

    }


    // not sure how parameters are handle since there is a node for it but it is also in the main node as a string
    @Override
    protected void handleSpecificNodes(Node child) {
        if (child.getArtifact().getData() instanceof JavaTreeArtifactData childData) {
            if (childData.getType().equals(JavaTreeArtifactData.NodeType.BLOCK)) {
                handleBlock( child);
            } else if (childData.getType().equals(JavaTreeArtifactData.NodeType.BEFORE)) {
                //not sure what the node is used for since the information is in the method dec node
            }
        }
    }


    @Override
    public VBox getCellContent() {
        VBox content = super.getCellContent();

        if (modifierCount == 0) mainSignature.getChildren().add(setupLabel(getIndentation()));
        Label typeNameLabel = setupLabel(text);
        decLabels.add(typeNameLabel);
        mainSignature.getChildren().add(typeNameLabel);

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

    @Override
    public void setBackGroundColor(String aId, Color newColor) {
        super.setBackGroundColor(aId, newColor);
        for (JavaBlockInterface bodyInterface : bodyInterfaces) {
            bodyInterface.setBackGroundColor(aId, newColor);
        }

        for (JavaBlockInterface parameterInterface : parameterInterfaces) {
            parameterInterface.setBackGroundColor(aId, newColor);
        }
    }

    private void handleBlock(Node blockNode) {
        int childDepth = depth +1;
        for (Node child : blockNode.getChildren()) {
            if (child.getArtifact().getData() instanceof JavaTreeArtifactData childData) {
                if (childData.getType().equals(JavaTreeArtifactData.NodeType.SIMPLE_JUST_A_STRING)) {
                    bodyInterfaces.add(new SimpleBlock(child,javaViewer.getColorForNode(child),childDepth,isIndented));
                    // it seems that there are mod enode types but they are not used
                }
            }
        }
    }
}
