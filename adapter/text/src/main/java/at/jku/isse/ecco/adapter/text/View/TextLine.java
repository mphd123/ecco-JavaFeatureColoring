package at.jku.isse.ecco.adapter.text.View;

import at.jku.isse.ecco.adapter.text.LineArtifactData;
import at.jku.isse.ecco.core.Association;
import at.jku.isse.ecco.tree.Node;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;

public class TextLine {
    private final Node node;
    private final Association association;
    private final String text;

    private final ObjectProperty<Background> background = new SimpleObjectProperty<>();
    private final ObjectProperty<Color> backgroundColor = new SimpleObjectProperty<>();

    public TextLine(Node node, Color backgroundColor) {
        this.node = node;
        if (node.getArtifact().getData() instanceof LineArtifactData data) {
            text = data.getLine();
        } else text = "node did not have LineArtifactData instead the node class was " + node.getArtifact().getClass().getName();

        association = node.getArtifact().getContainingNode() != null ? node.getArtifact().getContainingNode().getContainingAssociation() : null;
        setupListeners();
        this.backgroundColor.set(backgroundColor);
    }


    public String getText() {
        return text;
    }
    public ObjectProperty<Background> backgroundProperty() {
        return background;
    }
    public Association getAssociation() {
        return association;
    }
    public ObjectProperty<Color> backgroundColorProperty() {
        return backgroundColor;
    }

    private void setupListeners() {
        backgroundColor.addListener( (o, oldVal, newVal) -> {
            if (newVal == null || newVal == Color.TRANSPARENT){
                newVal = Color.WHITE;
            }
            background.set(new Background(new BackgroundFill(newVal,null,null)));
        });
    }
}
