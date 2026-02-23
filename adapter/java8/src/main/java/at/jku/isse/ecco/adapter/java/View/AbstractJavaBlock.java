package at.jku.isse.ecco.adapter.java.View;

import at.jku.isse.ecco.adapter.java.JavaTreeArtifactData;
import at.jku.isse.ecco.core.Association;
import at.jku.isse.ecco.tree.Node;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;

public abstract class AbstractJavaBlock implements JavaBlockInterface{
    protected final static Color defaultColor = Color.WHITE;
    protected final Node node;
    protected final Association association;
    protected String text;

    protected final ObjectProperty<Background> background = new SimpleObjectProperty<>();
    protected final ObjectProperty<Color> backgroundColor = new SimpleObjectProperty<>();

    public AbstractJavaBlock(Node node,Color backgroundColor,String additionalText,boolean additionalTextBefore) {
        this.node = node;
        StringBuilder sb = new StringBuilder();
        if (node.getArtifact().getData() instanceof JavaTreeArtifactData data) {
            sb.append(data.getDataAsString());
        }
        if (additionalText != null) {
            if (additionalTextBefore) sb.insert(0, additionalText + " ");
            else sb.append(additionalText).append(" ");
        }

        text = sb.toString();

        association = node.getArtifact().getContainingNode() != null ? node.getArtifact().getContainingNode().getContainingAssociation() : null;
        setupListeners();
        this.backgroundColor.set(backgroundColor);
    }

    public AbstractJavaBlock(Node node,Color backgroundColor) {
        this(node,backgroundColor,null,false);
    }


    private void setupListeners() {
        backgroundColor.addListener( (o, oldVal, newVal) -> {
            if (newVal == null || newVal == Color.TRANSPARENT){
                newVal = defaultColor;
            }
            background.set(new Background(new BackgroundFill(newVal,null,null)));
        });
    }

    @Override
    public void setBackGroundColor(String aId, Color newColor) {
        if(aId.equals(association.getId())){
            backgroundColor.set(newColor);
        }
    }

    protected Label setupLabel(String text){
        Label l = new Label(text);
        l.backgroundProperty().set(background.getValue());
        l.backgroundProperty().bind(background);
        return l;
    }

    public Association getAssociation() {
        return association;
    }
}
