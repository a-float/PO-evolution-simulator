package sample;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


public abstract class AbstractDrawable extends Pane {
    Color color;
    Vector2 position;

    public Color getColor(){return color;}

    public AbstractDrawable(Vector2 pos){
        position = pos;
    }
}
