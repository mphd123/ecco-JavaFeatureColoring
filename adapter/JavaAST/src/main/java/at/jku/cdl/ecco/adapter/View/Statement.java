package at.jku.cdl.ecco.adapter.View;

import at.jku.isse.ecco.tree.Node;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class Statement extends AbstractJavaNode{

    public Statement(Node javaTypeDecNode, Node nodeToHighlight, Color backgroundColor, int depth) {
        super(javaTypeDecNode, backgroundColor, depth, nodeToHighlight);
    }

    @Override
    public VBox getCellContent() {
        VBox content = setUpVBox();
        String intededText  = processTextIndents(text); // note was if isintended
        content.getChildren().add( setupLabel(intededText));
        return content;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
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
        return resultBuilder.toString();
    }
}
