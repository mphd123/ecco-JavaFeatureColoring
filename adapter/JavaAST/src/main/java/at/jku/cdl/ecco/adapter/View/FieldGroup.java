package at.jku.cdl.ecco.adapter.View;

import at.jku.cdl.ecco.adapter.artifactData.ASTNodeType;
import at.jku.cdl.ecco.adapter.artifactData.JavaASTData;
import at.jku.isse.ecco.tree.Node;
import javafx.scene.layout.VBox;

public class FieldGroup extends AbstractNodeWithNestedNodes {

    public FieldGroup(Node javaTypeDecNode, Node nodeToHighlight, JavaViewer javaViewer, int depth ) {
        super(javaTypeDecNode, nodeToHighlight, javaViewer, depth);
        parseChildren();
    }

    @Override
    protected void handleChild(JavaASTData childData, Node node) {
        if(childData.getType().equals(ASTNodeType.FIELD_DECLARATION)) {
            childInterfaces.add(new Statement(node,nodeToHighlight,javaViewer.getColorForNode(node),depth));
        }else childInterfaces.add(new ErrorNode(node));
    }

    @Override
    public VBox getCellContent() {
        VBox content = setUpVBox();
        for (JavaBlockInterface childInterface : childInterfaces) {
            content.getChildren().add(childInterface.getCellContent());
        }
        return content;
    }
}
