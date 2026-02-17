package at.jku.isse.ecco.adapter.java.View;

import at.jku.isse.ecco.adapter.java.JavaTreeArtifactData;  // note important these classes should probably be in the java8 adapter but im not sure since the java one doesnt seem to be complete nor in use

import at.jku.isse.ecco.core.Association;
import at.jku.isse.ecco.tree.Node;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;

public class CodeLine {
    private final Node node;
    private final Association association;
    private final String text;

    private final ObjectProperty<Background> background = new SimpleObjectProperty<>();
    private final ObjectProperty<Color> backgroundColor = new SimpleObjectProperty<>();

    public CodeLine(Node node, Color backgroundColor) {
        this.node = node;
        if (node.getArtifact().getData() instanceof JavaTreeArtifactData data) {
            text = data.getDataAsString();
        } else text = "not TreeArtifactData instead it was " + node.getArtifact().getClass().getName();

        association = node.getArtifact().getContainingNode() != null ? node.getArtifact().getContainingNode().getContainingAssociation() : null;
        setupListeners();
        this.backgroundColor.set(backgroundColor);
    }


    public String getText() {
        return text;
    }

    private void setupListeners() {
        backgroundColor.addListener( (o, oldVal, newVal) -> {
            if (newVal == null || newVal == Color.TRANSPARENT){
                newVal = Color.WHITE;
            }
            background.set(new Background(new BackgroundFill(newVal,null,null)));
        });
    }

    public ObjectProperty<Background> backgroundProperty() {
        return background;
    }

    public Association getAssociation() {
        return association;
    }

    public Color getBackgroundColor() {
        return backgroundColor.get();
    }

    public ObjectProperty<Color> backgroundColorProperty() {
        return backgroundColor;
    }
}
