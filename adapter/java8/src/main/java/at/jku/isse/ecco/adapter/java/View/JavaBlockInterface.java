package at.jku.isse.ecco.adapter.java.View;

import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public interface JavaBlockInterface {
    public VBox getCellContent();
    public void setBackGroundColor( String aId,Color newColor);

}
