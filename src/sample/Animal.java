package sample;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Animal extends AbstractDrawable{
    Genome genome;
    int energy;
    Direction currDirection;

    public Animal(Vector2 pos, Map map, int startEnergy){
        super(pos, map);
        color = Color.CORAL;
    }

    public void move(){

    }
    public void eat(){

    }

    public static void breed(){

    }
}
