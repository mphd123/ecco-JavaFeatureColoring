package at.jku.cdl.ecco.adapter.View;

import at.jku.isse.ecco.tree.Node;
import javafx.scene.layout.Border;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public interface JavaBlockInterface {
    VBox getCellContent();
    void setBackGroundColor(String aId, Color newColor);
    String getIndentation();
    boolean NodeEquals(Node nodeToCompare);

}
