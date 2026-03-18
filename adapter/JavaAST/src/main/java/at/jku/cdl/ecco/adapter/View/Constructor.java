package at.jku.cdl.ecco.adapter.View;

import at.jku.cdl.ecco.adapter.artifactData.JavaASTConstructorData;
import at.jku.isse.ecco.tree.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class Constructor extends GenericNestedNode{
    public Constructor(Node javaTypeDecNode,Node nodeToHighlight, JavaViewer javaViewer, int depth) {
        super(javaTypeDecNode, nodeToHighlight, javaViewer, depth);
    }


    @Override
    public VBox getCellContent() {
        JavaASTConstructorData data = (JavaASTConstructorData) node.getArtifact().getData();
        VBox content = setUpVBox();


        for (String s : data.getAnnotations()) {
            content.getChildren().add(setupLabel(getIndentation() + s));
        }

        StringBuilder sb  = new StringBuilder(getIndentation());


        for (String s : data.getModifiers()) {
            sb.append(s).append(" ");
        }

        if (!data.getTypeParameters().isEmpty()) {
            sb.append("< ");
            for (String s : data.getTypeParameters()) {
                sb.append(s).append(" ");
            }
            sb.append("> ");
        }

        sb.append(data.getName()).append(" (");

        for (String s : data.getParameters()) {
            sb.append(s).append(", ");
        }

        if (!data.getParameters().isEmpty()) {
            sb.delete(sb.length() - 2,sb.length());
        }

        sb.append(")");

        if ( !data.getThrowExceptions().isEmpty()) {
            sb.append ("throws ");
        }

        for (String s : data.getThrowExceptions()) {
            sb.append(s).append(", ");
        }

        if (!data.getThrowExceptions().isEmpty()) {
            sb.delete(sb.length() - 2,sb.length());
        }
        sb.append(" {");
        content.getChildren().add(setupLabel(sb.toString()));

        for (JavaBlockInterface childNode : childInterfaces) {
            content.getChildren().add(childNode.getCellContent());
        }
        String brace = getIndentation() + "}";
        Label rbrace = setupLabel(brace);
        content.getChildren().add(rbrace);
        return content;
    }
}
