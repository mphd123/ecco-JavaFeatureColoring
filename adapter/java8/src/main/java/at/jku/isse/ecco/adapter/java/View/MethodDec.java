package at.jku.isse.ecco.adapter.java.View;

import at.jku.isse.ecco.adapter.java.JavaTreeArtifactData;
import at.jku.isse.ecco.tree.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class MethodDec extends AbstractDecBlock{
    private final List<JavaBlockInterface> bodyInterfaces = new ArrayList<>();

    public MethodDec(Node javaTypeDecNode, JavaViewer javaViewer, int depthOfParent, boolean isIndented) {
        super(javaTypeDecNode, javaViewer,depthOfParent,isIndented);
        parseChildren();
    }

    public MethodDec(Node javaTypeDecNode, JavaViewer javaViewer) {
        this(javaTypeDecNode,javaViewer,0,false);
    }

    // not sure how parameters are handle since there is a node for it, but it is also in the main node as a string
    @Override
    protected void handleSpecificNodes(Node child) {
        if (child.getArtifact().getData() instanceof JavaTreeArtifactData childData) {
            if (childData.getType().equals(JavaTreeArtifactData.NodeType.BLOCK)) {
                handleBlock( child);
            }
        }
    }

    @Override
    public VBox getCellContent() {
        boolean emptyBody = bodyInterfaces.isEmpty();
        VBox content = super.getCellContent();

        if (isIndented) content.getChildren().add(new Label()); // this is used as Filler and is only used when it is indented to avoid having the space when it is selected

        if (modifierCount == 0) mainSignature.getChildren().add(setupLabel(getIndentation()));
        Label typeNameLabel = setupLabel(text);
        mainSignature.getChildren().add(typeNameLabel);

        Label lbrace = setupLabel(" {");
        if (emptyBody) lbrace.setText(" { }");
        mainSignature.getChildren().add(lbrace);

        content.getChildren().add(mainSignature);

        for (JavaBlockInterface bodyNode : bodyInterfaces) {
            content.getChildren().add(bodyNode.getCellContent());
        }
        if (!emptyBody) {
            Label rbrace = setupLabel(getIndentation() + "}");
            content.getChildren().add(rbrace);
        }
        return content;
    }

    @Override
    public void setBackGroundColor(String aId, Color newColor) {
        super.setBackGroundColor(aId, newColor);
        for (JavaBlockInterface bodyInterface : bodyInterfaces) {
            bodyInterface.setBackGroundColor(aId, newColor);
        }
    }

    private void handleBlock(Node blockNode) {
        int childDepth = depth +1;
        for (Node child : blockNode.getChildren()) {
            if (child.getArtifact().getData() instanceof JavaTreeArtifactData childData) {
                if (childData.getType().equals(JavaTreeArtifactData.NodeType.SIMPLE_JUST_A_STRING)) {
                    bodyInterfaces.add(new SimpleBlock(child,javaViewer.getColorForNode(child),childDepth,true));
                }
            }
        }
    }
}
