package me.evolutionSimulator;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import me.utils.Vector2;


public abstract class AbstractDrawable extends Pane {
    Color color1;
    Color color2;
    Vector2 position;

    public Vector2 getPosition(){return this.position;}
    public abstract Color getColor();
    public Color getMainColor(){
        return color1;
    }
    public Color getInterpolatedColor(float interpolationValue){
        return color1.interpolate(color2,interpolationValue);
    }

    public AbstractDrawable(Vector2 pos, Color color1, Color color2){
        position = pos;
        this.color1 = color1;
        this.color2 = color2;
    }

    public AbstractDrawable(Vector2 pos, Color color1){
        position = pos;
        this.color1 = color1;
    }
}
