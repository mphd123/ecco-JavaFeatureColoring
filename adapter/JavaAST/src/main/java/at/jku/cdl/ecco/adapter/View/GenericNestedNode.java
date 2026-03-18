package at.jku.cdl.ecco.adapter.View;

import at.jku.cdl.ecco.adapter.artifactData.ASTNodeType;
import at.jku.cdl.ecco.adapter.artifactData.JavaASTData;
import at.jku.isse.ecco.tree.Node;

public class GenericNestedNode extends AbstractNodeWithNestedNodes {
    public GenericNestedNode(Node javaTypeDecNode, Node nodeToHighlight, JavaViewer javaViewer, int depthOfParent) {
        super(javaTypeDecNode, nodeToHighlight, javaViewer, depthOfParent);
        parseChildren();
    }

    @Override
    protected void handleChild(JavaASTData childData, Node node) {
        int childDepth = depth + 1;
        childInterfaces.add(getBlock(childData,node,nodeToHighlight,childDepth,javaViewer));
    }


    public static JavaBlockInterface getBlock(JavaASTData childData, Node node, Node nodeToHighlight,int childDepth,JavaViewer javaViewer) {
        if(childData.getType().equals(ASTNodeType.STATEMENT)) {
           return new Statement(node, nodeToHighlight,javaViewer.getColorForNode(node),childDepth);
        }else if (childData.getType().equals(ASTNodeType.SWITCH_STATEMENT)){
            return new Switch(node, nodeToHighlight,javaViewer,childDepth);
        }else if (childData.getType().equals(ASTNodeType.IF_STATEMENT)){
            return new IfBlock(node, nodeToHighlight,javaViewer,childDepth);
        }else if (childData.getType().equals(ASTNodeType.TRYBLOCK)){
        return new TryBlock(node, nodeToHighlight,javaViewer,childDepth);
        }
        else if(!node.getChildren().isEmpty() ) {
            return new GenericNestedNode(node, nodeToHighlight,javaViewer,childDepth);
        }
        return new ErrorNode(node);
    }
}

