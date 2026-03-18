package at.jku.cdl.ecco.adapter.View;

import at.jku.cdl.ecco.adapter.artifactData.ASTNodeType;
import at.jku.cdl.ecco.adapter.artifactData.JavaASTData;
import at.jku.isse.ecco.tree.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TryBlock extends AbstractNodeWithNestedNodes {

    Map<JavaBlockInterface, List<JavaBlockInterface>> clauseEntries = new HashMap<>();

    private int  clauseCount = 0;
    private List<JavaBlockInterface> tryLines = new ArrayList<>();


    public TryBlock(Node javaTypeDecNode, Node nodeToHighlight, JavaViewer javaViewer, int depth) {
        super(javaTypeDecNode, nodeToHighlight, javaViewer, depth);
        parseChildren();
    }

    @Override
    protected void handleChild(JavaASTData childData, Node node) {
        int childDepth = depth + 1;

        if (childData.getType().equals(ASTNodeType.CATCHCLAUSE)) {
            List<JavaBlockInterface> entryStatements = new ArrayList<>();
            for(Node switchEntryLine : node.getChildren()) {
                if (switchEntryLine.getArtifact().getData() instanceof JavaASTData switchEntryLineData){
                    entryStatements.add(GenericNestedNode.getBlock(switchEntryLineData,switchEntryLine,nodeToHighlight,childDepth,javaViewer));
                }
            }

            Statement clauseEntry = new Statement(node,nodeToHighlight,javaViewer.getColorForNode(node),depth);
            clauseEntry.setText( " } catch (" + clauseEntry.text + ") {");
            childInterfaces.add(clauseEntry);
            clauseEntries.put(clauseEntry,entryStatements);
            clauseCount++;
        } else {
            tryLines.add(GenericNestedNode.getBlock(childData,node,nodeToHighlight,childDepth ,javaViewer));
        }
    }

    @Override
    public VBox getCellContent() {

        VBox content = setUpVBox();
        StringBuilder sb = new StringBuilder();
        sb.append(getIndentation());
        sb.append("try {");
        Label head = setupLabel(sb.toString());
        content.getChildren().add(head);

        for (JavaBlockInterface lines : tryLines) {
            content.getChildren().add(lines.getCellContent());
        }

        for (JavaBlockInterface childNode : childInterfaces) {
            content.getChildren().add(childNode.getCellContent());
            for (JavaBlockInterface switchStatement : clauseEntries.get(childNode)) {
                content.getChildren().add(switchStatement.getCellContent());
            }
        }

        if (!childInterfaces.isEmpty()) {
            String catchBrace  = childInterfaces.getLast().getIndentation() + "}";
            Label lcatchBrace = setupLabel(catchBrace);
            content.getChildren().add(lcatchBrace);
        }

        return content;
    }

    @Override
    public void setBackGroundColor(String aId, Color newColor) {
        super.setBackGroundColor(aId, newColor);
        for (JavaBlockInterface lines : tryLines) {
            lines.setBackGroundColor(aId, newColor);
        }
    }
}
