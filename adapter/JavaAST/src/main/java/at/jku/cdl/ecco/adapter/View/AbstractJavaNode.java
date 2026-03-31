package at.jku.cdl.ecco.adapter.View;

import at.jku.cdl.ecco.adapter.artifactData.JavaASTData;
import at.jku.isse.ecco.core.Association;
import at.jku.isse.ecco.tree.Node;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public abstract class AbstractJavaNode implements JavaBlockInterface{
    public final static Color defaultColor = Color.WHITE;
    protected final Node node;
    protected final Node nodeToHighlight;
    protected final Association association;
    protected String text;
    protected int depth;
    protected static final char indentationSymbol ='\t';

    protected final ObjectProperty<Background> background = new SimpleObjectProperty<>();
    protected final ObjectProperty<Color> backgroundColor = new SimpleObjectProperty<>();

    public AbstractJavaNode(Node node, Node nodeToHighlight, Color backgroundColor, int depth) {
        this.node = node;
        this.nodeToHighlight = nodeToHighlight;
        this.depth = depth;
        StringBuilder sb = new StringBuilder();
        if (node.getArtifact().getData() instanceof JavaASTData data)  sb.append(data);
        text = sb.toString();
        association = node.getArtifact().getContainingNode() != null ? node.getArtifact().getContainingNode().getContainingAssociation() : null;
        setupListeners();
        this.backgroundColor.set(backgroundColor);
    }


    public AbstractJavaNode(Node node, Color backgroundColor, int depth,  Node nodeToHighlight) {
        this(node, nodeToHighlight, backgroundColor, depth);
    }

    public AbstractJavaNode(Node node, Color backgroundColor) {
        this(node,null,backgroundColor,0 );
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

    protected VBox setUpVBox(){
        VBox vBox = new VBox();
        if (NodeEquals(nodeToHighlight)) JavaViewer.highlightBox(vBox);
        return vBox;
    }

    // if depth is zero this returns an empty String
    public String getIndentation(){
        return String.valueOf(indentationSymbol).repeat(Math.max(0, depth));
    }

    @Override
    public boolean NodeEquals(Node nodeToCompare) {
        return node.equals(nodeToCompare);
    }

    public Association getAssociation() {
        return association;
    }


}
