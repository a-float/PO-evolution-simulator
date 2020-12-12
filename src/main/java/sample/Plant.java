package sample;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Plant extends AbstractDrawable{

    public Plant(Vector2 pos, Map map) {
        super(pos, map);
        color = Color.DARKOLIVEGREEN;
    }
}
