package sample;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


public abstract class AbstractDrawable extends Pane {
    Color color;
    Vector2 position;
    Map map;

    public AbstractDrawable(Vector2 pos, Map m){
        position = pos;
        map = m;
    }
    public void show(GraphicsContext gc) {
        gc.setFill(color);
        gc.fillRect(position.x*map.cellSize, position.y* map.cellSize, map.cellSize, map.cellSize);
    }
}
