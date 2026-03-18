package at.jku.cdl.ecco.adapter.View;

import at.jku.cdl.ecco.adapter.artifactData.ASTNodeType;
import at.jku.cdl.ecco.adapter.artifactData.JavaASTData;
import at.jku.isse.ecco.tree.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class TypeDec extends AbstractNodeWithNestedNodes {



    public TypeDec(Node javaTypeDecNode, Node nodeToHighlight , JavaViewer javaViewer, int depthOfParent) {
        super(javaTypeDecNode, nodeToHighlight, javaViewer, depthOfParent);
        parseChildren();
    }

    public TypeDec(Node javaTypeDecNode,Node nodeToHighlight, JavaViewer javaViewer){
        this(javaTypeDecNode,nodeToHighlight,javaViewer,0);
    }

    @Override
    protected void handleChild(JavaASTData childData, Node node) {
        int childDepth = depth + 1;
        if(childData.getType().equals(ASTNodeType.METHOD_DECLARATION)) {
            childInterfaces.add(new GenericNestedNode(node,nodeToHighlight,javaViewer,childDepth));
        }else if(childData.getType().equals(ASTNodeType.FIELD_GROUP)) {
            childInterfaces.add(new FieldGroup(node, nodeToHighlight, javaViewer,  childDepth));
        }else if (childData.getType().equals(ASTNodeType.CONSTRUCTOR_DECLARATION)) {
            childInterfaces.add(new Constructor(node,nodeToHighlight,javaViewer,childDepth));
        }else if (childData.getType().equals(ASTNodeType.ENUM_DECLARATION)){
            childInterfaces.add(new EnumBlock(node, nodeToHighlight, javaViewer,  childDepth));
        }else {
            childInterfaces.add(new ErrorNode(node));
        }
    }

    @Override
    public void setBackGroundColor(String aId, Color newColor) {
        super.setBackGroundColor(aId, newColor);
        for (JavaBlockInterface javaBlockInterface : childInterfaces) {
            javaBlockInterface.setBackGroundColor(aId, newColor);
        }
    }

    @Override
    public VBox getCellContent() {
        VBox content = setUpVBox();

        Label signature = setupLabel(text);
        content.getChildren().add(signature);

        for (int i = 0; i < childInterfaces.size(); i++) {
            content.getChildren().add(childInterfaces.get(i).getCellContent());
            if (i < childInterfaces.size() -1)content.getChildren().add(new Label()); // used for spacing
        }
        Label rbrace = setupLabel(getIndentation() + "}");
        content.getChildren().add(rbrace);
        return content;
    }


}
