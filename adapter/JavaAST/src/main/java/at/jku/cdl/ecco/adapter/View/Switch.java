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

public class Switch extends AbstractNodeWithNestedNodes {
    Map<JavaBlockInterface, List<JavaBlockInterface>> switchEntries = new HashMap<>();

    public Switch(Node javaTypeDecNode, Node nodeToHighlight, JavaViewer javaViewer, int depth) {
        super(javaTypeDecNode, nodeToHighlight, javaViewer, depth);
        parseChildren();

    }

    @Override
    protected void handleChild(JavaASTData childData, Node node) {
        int childDepth = depth + 1;
        if(childData.getType().equals(ASTNodeType.SWITCH_ENTRIES)) {
            List<JavaBlockInterface> entryStatements = new ArrayList<>();
            for(Node switchEntryLine : node.getChildren()) {
                if (switchEntryLine.getArtifact().getData() instanceof JavaASTData switchEntryLineData){
                    entryStatements.add(GenericNestedNode.getBlock(switchEntryLineData,switchEntryLine, nodeToHighlight,childDepth +1,javaViewer));
                }
            }

            Statement switchEntry = new Statement(node, nodeToHighlight,javaViewer.getColorForNode(node),childDepth);
            if (switchEntry.getText().startsWith("DEFAULT")) {
                switchEntry.setText("default :");
            } else switchEntry.setText("case " + switchEntry.getText() + ":");
            childInterfaces.add(switchEntry);
            switchEntries.put(switchEntry,entryStatements);
        }
    }

    @Override
    public VBox getCellContent() {
        VBox content = setUpVBox();
        StringBuilder sb = new StringBuilder();
        sb.append(getIndentation());
        sb.append("switch (").append(text).append(") ").append(" {");
        Label head = setupLabel(sb.toString());
        content.getChildren().add(head);

        for (JavaBlockInterface childNode : childInterfaces) {
            content.getChildren().add(childNode.getCellContent());
            for (JavaBlockInterface switchStatement : switchEntries.get(childNode)) {
                content.getChildren().add(switchStatement.getCellContent());
            }
        }

        String brace = getIndentation() + "}";
;
        Label rbrace = setupLabel(brace);
        content.getChildren().add(rbrace);

        return content;
    }

    @Override
    public void setBackGroundColor(String aId, Color newColor) {
        super.setBackGroundColor(aId, newColor);

        for (List<JavaBlockInterface> list : switchEntries.values()) {
            for (JavaBlockInterface block : list) {
                block.setBackGroundColor(aId, newColor);
            }
        }
    }
}
