package at.jku.isse.ecco.adapter.java.View;

import at.jku.isse.ecco.adapter.java.JavaTreeArtifactData;
import at.jku.isse.ecco.tree.Node;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class FieldDec extends AbstractDecBlock{
    private SimpleBlock fieldType;
    private SimpleBlock fieldInit;

    public FieldDec(Node javaTypeDecNode, JavaViewer javaViewer,int depthOfParent,boolean isIndented) {
        super(javaTypeDecNode,javaViewer,depthOfParent,isIndented);
        parseChildren();
    }

    @Override
    protected void handleSpecificNodes( Node child) {
        if (child.getArtifact().getData() instanceof JavaTreeArtifactData childData) {
            if (childData.getType().equals(JavaTreeArtifactData.NodeType.FIELD_TYPE)) {
                handleFieldType( child);
            } else if (childData.getType().equals(JavaTreeArtifactData.NodeType.FIELD_INIT)) {
                handleFieldInit(child);
            }
        }
    }


    @Override
    public void setBackGroundColor(String aId, Color newColor) {
        super.setBackGroundColor(aId, newColor);
        fieldType.setBackGroundColor(aId, newColor);
        if (fieldInit != null) fieldInit.setBackGroundColor(aId, newColor);
    }

    @Override
    public VBox getCellContent() {
        VBox content = super.getCellContent();
        if (modifierCount == 0) mainSignature.getChildren().add(setupLabel(getIndentation()));
        mainSignature.getChildren().add(fieldType.getCellContent());

        Label typeNameLabel = setupLabel(text);
        decLabels.add(typeNameLabel);
        mainSignature.getChildren().add(typeNameLabel);

        if (fieldInit != null) mainSignature.getChildren().add(fieldInit.getCellContent());

        Label end = setupLabel(";");
        decLabels.add(end);
        mainSignature.getChildren().add(end);
        content.getChildren().add(mainSignature);

        return content;
    }

    private void handleFieldType(Node child){
        fieldType = new SimpleBlock(child,javaViewer.getColorForNode(child)," ", false);
    }
    private void handleFieldInit(Node child){
        fieldInit = new SimpleBlock(child.getChildren().getFirst(),javaViewer.getColorForNode(child)," = ", true);
    }
}
