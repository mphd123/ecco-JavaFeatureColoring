package at.jku.cdl.ecco.adapter.View;

import at.jku.cdl.ecco.adapter.artifactData.ASTNodeType;
import at.jku.cdl.ecco.adapter.artifactData.JavaASTData;
import at.jku.isse.ecco.tree.Node;
import javafx.scene.layout.VBox;

public class EnumBlock extends AbstractNodeWithNestedNodes {
    public EnumBlock(Node javaTypeDecNode, Node nodeToHighlight, JavaViewer javaViewer, int depth) {
        super(javaTypeDecNode, nodeToHighlight, javaViewer, depth);
        parseChildren();
        Statement lastStatement = (Statement) childInterfaces.getLast();
        lastStatement.setText(lastStatement.getText().substring(0, lastStatement.getText().length() - 2));
        text = text.substring(0,text.length() - 3); // remove the unnecessary {} that is added since they are done with labels
    }

    @Override
    protected void handleChild(JavaASTData childData, Node node) {
        if (childData.getType().equals(ASTNodeType.ENUM_CONSTANTS)){
            Statement newNode = new Statement(node, nodeToHighlight,javaViewer.getColorForNode(node),depth+1);
            newNode.setText(newNode.getText() + ",");
            childInterfaces.add(newNode);
        }else childInterfaces.add(new ErrorNode(node));
    }

    @Override
    public VBox getCellContent() {
        return super.getCellContent();
    }
}
