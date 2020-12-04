package sample;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.scene.shape.*;
import javafx.scene.paint.*;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
//        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        Canvas canvas = new Canvas(300, 250);
        canvas.setHeight(400);
        canvas.setWidth(400);
        Map map = new Map(10,10,0.5f, canvas);
        for(int i = 0; i < 5; i++){
            map.placeAnimal(new Animal(new Vector2(i*3, i*2), map, 30));
        }
        for(int i = 0; i < 5; i++){
            map.spawnGrass();
        }
        map.showMap();
        Group root = new Group();
        root.getChildren().add(canvas);
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
