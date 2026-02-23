package at.jku.isse.ecco.adapter.java.View;

import at.jku.isse.ecco.tree.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class SimpleBlock extends AbstractJavaBlock implements JavaBlockInterface {


    public SimpleBlock(Node node, Color backgroundColor) {
        super(node,backgroundColor);

    }

    public SimpleBlock(Node child, Color defaultColor, String additionalText, boolean additionalTextBefore) {
        super(child,defaultColor,additionalText,additionalTextBefore);
    }

    @Override
    public VBox getCellContent() {
        VBox content = new VBox();
        content.getChildren().add( setupLabel(text));
        return content;
    }
}
