package at.jku.isse.ecco.adapter.java.View;

import at.jku.isse.ecco.tree.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class NotImplementedNode implements JavaBlockInterface {
    private Node node;
    public NotImplementedNode(Node node) {
        this.node = node;
    }

    @Override
    public VBox getCellContent() {
        VBox content = new VBox();
        content.getChildren().add( new Label("There is no implementation for Node: " + node));
        return content;
    }

    @Override
    public void setBackGroundColor(String aId, Color newColor) {
    }
}
