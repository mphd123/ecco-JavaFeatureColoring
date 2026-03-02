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
        if (isIndented) intededText = processTextIndents(text);
        content.getChildren().add( setupLabel(intededText));
        return content;
    }


    private String processTextIndents(String text){
        StringBuilder resultBuilder = new StringBuilder(getIndentation());
        StringBuilder intededStringbuilder = new StringBuilder(getIndentation());
        for (int i = 0; i < text.length();i++){
            char c = text.charAt(i);
            resultBuilder.append(c);
            if(c == '\n') {
                intededStringbuilder.append(indentationSymbol);
                resultBuilder.append(intededStringbuilder);
            }
        }
        System.out.println(resultBuilder.toString());

        return resultBuilder.toString();


    }
}
