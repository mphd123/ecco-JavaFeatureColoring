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

public class IfBlock extends AbstractNodeWithNestedNodes {
    Map<JavaBlockInterface, List<JavaBlockInterface>> ifLines = new HashMap<>();

    private int ifCount;

    public IfBlock(Node javaTypeDecNode, Node nodeToHighlight, JavaViewer javaViewer, int depth) {
        super(javaTypeDecNode, nodeToHighlight, javaViewer, depth);
        parseChildren();
    }

    @Override
    protected void handleChild(JavaASTData childData, Node node) {
        int childDepth = depth + 1;
        if(childData.getType().equals(ASTNodeType.IF_CONDITION)) {
            List<JavaBlockInterface> lineList = new ArrayList<>();
            Statement ifEntry = new Statement(node, nodeToHighlight,javaViewer.getColorForNode(node),childDepth);
            String ifPart;
            if (ifCount == 0) {
                ifPart = "if (" + childData + ") {";

            }else ifPart = "} else if (" + childData + ")  {";

            ifEntry.setText(ifPart);
            childInterfaces.add(ifEntry);

            for(Node ifConditionLines : node.getChildren()) {
                if (ifConditionLines.getArtifact().getData() instanceof JavaASTData switchEntryLineData){

                    if (switchEntryLineData.getType().equals(ASTNodeType.ELSE_BRANCH)) {
                        handleElseBranch(childDepth, ifConditionLines);
                        continue;
                    }
                    lineList.add(GenericNestedNode.getBlock(switchEntryLineData,ifConditionLines,nodeToHighlight,childDepth +1,javaViewer));
                }
            }

            ifLines.put(ifEntry,lineList);
            ifCount++;
        }
    }

    private void handleElseBranch( int childDepth, Node elseBranchNode) {
        Statement elseEntry = new Statement(elseBranchNode,nodeToHighlight,javaViewer.getColorForNode(node),childDepth);
        elseEntry.setText(" } else {");
        childInterfaces.add(elseEntry);
        List<JavaBlockInterface> elseList = new ArrayList<>();

        for (Node elseLine : elseBranchNode.getChildren()) {
            if (elseLine.getArtifact().getData() instanceof JavaASTData switchEntryLineData){
                elseList.add(GenericNestedNode.getBlock(switchEntryLineData,elseLine,nodeToHighlight,childDepth +1,javaViewer));
            }
        }
        Statement rbrace = new Statement(elseBranchNode,nodeToHighlight,javaViewer.getColorForNode(node),childDepth);
        rbrace.setText("}");
        elseList.add(rbrace);

        ifLines.put(elseEntry,elseList);
    }

    @Override
    public VBox getCellContent() {
        VBox content = setUpVBox();

        for (JavaBlockInterface childNode : childInterfaces) {
            content.getChildren().add(childNode.getCellContent());
            for (JavaBlockInterface switchStatement : ifLines.get(childNode)) {
                content.getChildren().add(switchStatement.getCellContent());
            }
        }

        String brace = getIndentation() + "}";
        Label rbrace = setupLabel(brace);
        content.getChildren().add(rbrace);
        return content;
    }

    @Override
    public void setBackGroundColor(String aId, Color newColor) {
        super.setBackGroundColor(aId, newColor);

        for (List<JavaBlockInterface> list : ifLines.values()) {
            for (JavaBlockInterface block : list) {
                block.setBackGroundColor(aId, newColor);
            }
        }
    }
}
