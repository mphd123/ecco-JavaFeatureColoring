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

    public SimpleBlock(Node node, Color backgroundColor, String additionalText, boolean additionalTextBefore) {
        super(node,defaultColor,additionalText,additionalTextBefore);
    }
    public SimpleBlock(Node node,Color backgroundColor,int depthOfParent,boolean isIndented) {
        super(node,backgroundColor,depthOfParent, isIndented);
    }

    @Override
    public VBox getCellContent() {
        VBox content = new VBox();
        String intededText  = text;
        if (isIndented) intededText = getIndentation() + text;
        content.getChildren().add( setupLabel(intededText));
        return content;
    }
}
