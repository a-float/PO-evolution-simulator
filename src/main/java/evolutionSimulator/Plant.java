package evolutionSimulator;

import javafx.scene.paint.Color;

public class Plant extends AbstractDrawable{
    public Plant(Vector2 pos){
        super(pos, Color.DARKOLIVEGREEN);
    }
    public Color getColor(){
        return getMainColor();
    }
}
