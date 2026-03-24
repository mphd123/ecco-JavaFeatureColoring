package at.jku.cdl.ecco.adapter.View;

import at.jku.cdl.ecco.adapter.artifactData.JavaASTData;
import at.jku.isse.ecco.tree.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractNodeWithNestedNodes extends AbstractJavaNode {

    protected JavaViewer javaViewer;
    protected List<JavaBlockInterface> childInterfaces = new ArrayList<>();

    public AbstractNodeWithNestedNodes(Node javaTypeDecNode, Node nodeToHighlight, JavaViewer javaViewer, int depth) {
        super(javaTypeDecNode,javaViewer.getColorForNode(javaTypeDecNode),depth,nodeToHighlight);
        this.javaViewer = javaViewer;
    }

    protected void parseChildren() {
        for (Node child : node.getChildren()){
            if (child.getArtifact().getData() instanceof JavaASTData childData){
                handleChild(childData, child);
            } else {
                // add exception node here
            }
        }
    }

    protected abstract void handleChild(JavaASTData childData, Node node);

    @Override
    public VBox getCellContent() {
        VBox content = setUpVBox();
        StringBuilder sb = new StringBuilder();
        sb.append(getIndentation());


        sb.append(text.substring(0,text.length()-1)).append(" {"); // substring to remove the ; that is appended after )
        Label signature = setupLabel(sb.toString());
        content.getChildren().add(signature);

        for (JavaBlockInterface childNode : childInterfaces) {
            content.getChildren().add(childNode.getCellContent());
        }
        String brace = "}";

        brace = getIndentation() + brace;
        Label rbrace = setupLabel(brace);
        content.getChildren().add(rbrace);
        return content;
    }

    @Override
    public void setBackGroundColor(String aId, Color newColor) {
        super.setBackGroundColor(aId, newColor);
        for (JavaBlockInterface childNode : childInterfaces) {
            childNode.setBackGroundColor(aId, newColor);
        }
    }
}
