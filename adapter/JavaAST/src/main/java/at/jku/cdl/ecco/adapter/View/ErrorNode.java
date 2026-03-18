package at.jku.cdl.ecco.adapter.View;

import at.jku.isse.ecco.tree.Node;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class ErrorNode extends AbstractJavaNode{
    public ErrorNode(Node node) {
        super(node, Color.RED);
    }

    String additionalInfoText;

    public ErrorNode(Node node, String additionalInfoText) {
        this(node);
        this.additionalInfoText = additionalInfoText;
    }

    @Override
    public VBox getCellContent() {
        VBox content = setUpVBox();;
        content.getChildren().add( setupLabel("-".repeat(depth) + "Error invalid node at this location Node" + node.toString() + additionalInfoText));
        return content;
    }
}
