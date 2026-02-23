package at.jku.isse.ecco.adapter.java.View;

import at.jku.isse.ecco.adapter.java.JavaTreeArtifactData;
import at.jku.isse.ecco.tree.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class TypeDecBlock extends AbstractJavaBlock implements JavaBlockInterface{


    private List<JavaBlockInterface> BodyInterfaces = new ArrayList<>();
    private List<SimpleBlock> annotationBlocks = new ArrayList<>();
    private List<SimpleBlock> modiferBlocks = new ArrayList<>();
    private SimpleBlock extendsBlock;
    private List<SimpleBlock> implementationBlocks = new ArrayList<>();
    private List<Label> decLabels = new ArrayList<>();
    private String typeName; // also has "class " or others before

    public TypeDecBlock(Node javaTypeDecNode, Color backgroundColor) {
        super(javaTypeDecNode,backgroundColor);
        if (text != null) {
            typeName = text;
        }

        StringBuilder sb = new StringBuilder(typeName);
        for (Node child : node.getChildren()) {
            if (child.getArtifact().getData() instanceof JavaTreeArtifactData childData){
                if (childData.getType().equals(JavaTreeArtifactData.NodeType.MODIFIER)){
                    handleModifierNode(sb, child);
                } else if (childData.getType().equals(JavaTreeArtifactData.NodeType.DECLARATION_EXTENDS)){
                    handleDecExtendsNode(sb, child);
                } else if  (childData.getType().equals(JavaTreeArtifactData.NodeType.DECLARATION_IMPLEMENTS)){
                    handleImplementsNode(sb, child);
                } else if  (childData.getType().equals(JavaTreeArtifactData.NodeType.AFTER)){
                    //ToDo
                }
            }
        }
        text = sb.toString();
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
        extendsBlock.setBackGroundColor(aId, newColor);

        for (SimpleBlock simpleBlock : implementationBlocks) {
            simpleBlock.setBackGroundColor(aId, newColor);
        }
        for (JavaBlockInterface javaBlockInterface : BodyInterfaces) {
            javaBlockInterface.setBackGroundColor(aId, newColor);
        }
    }

    @Override
    public VBox getCellContent() {
        VBox content = new VBox();
        for (SimpleBlock anno : annotationBlocks) {
            content.getChildren().add( anno.getCellContent());
        }

        HBox mainSignature = new HBox();
        for (SimpleBlock mod : modiferBlocks) {
            mainSignature.getChildren().add( mod.getCellContent());
        }

        Label typeNameLabel = setupLabel(typeName);
        decLabels.add(typeNameLabel);
        mainSignature.getChildren().add(typeNameLabel);

        if (extendsBlock != null) {
            mainSignature.getChildren().add(extendsBlock.getCellContent());
        }
        if (implementationBlocks.size() > 0) {
            mainSignature.getChildren().add(setupLabel(" implements "));
            for (SimpleBlock block : implementationBlocks) {
                mainSignature.getChildren().add(block.getCellContent());
            }
        }
        Label lbrace = setupLabel("{");
        decLabels.add(lbrace);
        mainSignature.getChildren().add(lbrace);
        content.getChildren().add(mainSignature);

        Label rbrace = setupLabel("}");
        decLabels.add(rbrace);
        content.getChildren().add(rbrace);


        return content;
    }


    private void handleModifierNode(StringBuilder sb, Node ModiferNode){
        StringBuilder annotations =new StringBuilder();
        StringBuilder modifiers =new StringBuilder();
        for (Node child : ModiferNode.getChildren()) {
            if(child.getArtifact().getData() instanceof JavaTreeArtifactData data ){
                if(data.getDataAsString().startsWith("@")){
                    annotations.append(child.getArtifact().getData()).append("\n");
                    annotationBlocks.add(new SimpleBlock(child,defaultColor));
                }else{
                    modifiers.append(child.getArtifact().getData()).append(" ");
                    modiferBlocks.add(new SimpleBlock(child,defaultColor, " ", false));
                }
            }
        }
        sb.insert(0, annotations.append(modifiers));
    }

    private void handleDecExtendsNode(StringBuilder sb, Node ExtendsNode){
        if (ExtendsNode.getChildren().isEmpty()) return;
        String extendedClassName = ((JavaTreeArtifactData) ExtendsNode.getChildren().getFirst().getArtifact().getData()).getDataAsString();
        sb.append(" extends ").append(extendedClassName);
        extendsBlock = new SimpleBlock(ExtendsNode,defaultColor,"extends ", true);
    }
    private void handleImplementsNode(StringBuilder sb, Node ImplNode){
        if (ImplNode.getChildren().isEmpty()) return;
        sb.append(" implements ");
        int count = 0;
        for (Node child : ImplNode.getChildren()) {
            implementationBlocks.add(new SimpleBlock(child,defaultColor));
            String extendedClassName = ((JavaTreeArtifactData) child.getArtifact().getData()).getDataAsString();
            if (count > 0){sb.append(", ");}
            sb.append(extendedClassName);
            count++;
        }

        implementationBlocks.add(new SimpleBlock(ImplNode,defaultColor));
    }
}
