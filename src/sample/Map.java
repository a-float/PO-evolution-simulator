package sample;

import com.sun.prism.Graphics;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Map {
    int mapWidth;
    int mapHeight;
    int cellSize;
    List<Animal> animals = new ArrayList<>();
    List<Plant> plants = new ArrayList<>();
    float jungleRatio;
    Canvas canvas;

    public Map(int width, int height, float jglRatio, Canvas canv){
        mapWidth = width;
        mapHeight = height;
        jungleRatio = jglRatio;
        canvas = canv;
        cellSize = (int) Math.min(canvas.getWidth()/(double)mapWidth, canvas.getHeight()/(double)mapHeight);
    }

    public void placeAnimal(Animal animal){
        animals.add(animal);
    }

    public void spawnGrass(){
        for(int i = 0; i < 2; i++){
            Vector2 plantPos = new Vector2(new Random().nextInt(mapWidth),new Random().nextInt(mapHeight));
            plants.add(new Plant(plantPos, this));
        }
    }
    public void showMap(){
        for (Animal a: animals) {
            a.show(canvas.getGraphicsContext2D());
        }
        for (Plant p: plants) {
            p.show(canvas.getGraphicsContext2D());
        }
    }

}
