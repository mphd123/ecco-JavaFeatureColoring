package at.jku.cdl.ecco.adapter.View;

import at.jku.cdl.ecco.adapter.artifactData.JavaASTData;
import at.jku.isse.ecco.tree.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class DoWhileBlock extends GenericNestedNode{

    public DoWhileBlock(Node javaTypeDecNode, Node nodeToHighlight, JavaViewer javaViewer, int depthOfParent) {
        super(javaTypeDecNode, nodeToHighlight, javaViewer, depthOfParent);
    }

    @Override
    public VBox getCellContent() {
        VBox content = setUpVBox();

        Label doLabel = setupLabel(getIndentation() + "do{");
        content.getChildren().add(doLabel);
        for (JavaBlockInterface childNode : childInterfaces) {
            content.getChildren().add(childNode.getCellContent());
        }
        String whilePart = getIndentation() + "} while(" +
                text.substring(text.indexOf("(") + 1, text.indexOf(")")) +
                ");";
        Label rbrace = setupLabel(whilePart);
        content.getChildren().add(rbrace);
        return content;
    }
}
