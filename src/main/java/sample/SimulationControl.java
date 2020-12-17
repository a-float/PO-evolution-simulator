package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class SimulationControl extends VBox implements Initializable {
    //TODO tidy up the whole controller, maybe break it up into smaller pieces
    @FXML
    Canvas canvas;
    @FXML
    Slider speedSlider;
    @FXML
    ChartControl chartControl;
    @FXML
    Label label1, label2, label3, label4, label5, label6;
    @FXML
    TrackingControl trackControl;

    Vector2 selectedPos = null;

    public SimulationManager simManager;
    boolean isPlaying = false;
    double cellSize;
    Timeline timer;

    public SimulationControl() {
        System.out.println("SimulationController constructed.");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/simulation.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException exc) {
            exc.printStackTrace();
            // this is pretty much fatal, so:
            System.exit(1);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("SimulationController initialized.");
        // This will now be called after the @FXML-annotated fields are initialized.
        createTimer(1f/speedSlider.getValue());
        speedSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
            changeSimulationSpeed(newValue.doubleValue());
        });

        Platform.runLater(this::showMap);
        Platform.runLater(this::updateStatLabels);
    }


    @FXML
    private void canvasClicked(MouseEvent event){   //TODO its bad rn
        if(!isPlaying) {
            Map map = simManager.getMap();
            Bounds bounds = canvas.getBoundsInLocal();
            cellSize = Math.min(bounds.getWidth() / map.mapWidth, bounds.getHeight() / map.mapHeight);
            int x = (int) Math.floor(event.getX() / cellSize);
            int y = (int) Math.floor(event.getY() / cellSize);
            selectedPos = new Vector2(x, y);

            trackControl.selectedAnimalsListView.getItems().clear();
            AnimalCollectionList selectedAnimalCollection = simManager.getMap().animalMap.get(selectedPos);
//            if (selectedAnimalCollection.size() == 0) {
//                selectedAnimalsListView.getItems().add("There are no animals on this tile.");
//            }
            for (Animal animal : selectedAnimalCollection.animalList) {   //TODO change .animalList to a getAll() or smth?
                trackControl.selectedAnimalsListView.getItems().add(animal);
            }
            showMap();
        }
    }

    //cannot step while the simulation is not paused
    @FXML
    private void doStep (ActionEvent event) {
        if(!isPlaying)nextGen();
    }

    @FXML
    private void startOrStopSimulation (ActionEvent event) {
        if(isPlaying)timer.pause();
        else timer.play();
        isPlaying = !isPlaying;
    }

    @FXML
    private void changeSimulationSpeed (double speed) {
//        System.out.println(speed);
        createTimer(0.1/speed);
    }

    private void nextGen(){
        selectedPos = null; //this too
        trackControl.selectedAnimalsListView.getItems().clear();     //TODO this should be somewhere else
        simManager.simulateGen();
        showMap();
        updateStatLabels();
        Map map = simManager.getMap();
        chartControl.updateChart(map.animals.size(), map.mapHeight*map.mapWidth- map.noHasPlant.size());
    }

    public void setManager(SimulationManager simManager) {
        this.simManager = simManager;
    }

    @FXML
    private void createTimer(double seconds) {
        if (timer != null) {
            timer.stop();
        }
        timer = new Timeline(new KeyFrame(Duration.seconds(seconds), e -> nextGen()));
        timer.setCycleCount(Timeline.INDEFINITE);
        if(isPlaying)timer.play();
    }

    //TODO multiple getMaps in this class?
    public void updateStatLabels(){
        Map map = this.simManager.getMap();
        float energySum = 0;
        float babySum = 0;
        float ageSum = 0;
        int animalCount = map.animals.size();
        for(Animal animal : map.animals) {
            babySum += animal.babyCount;
            ageSum += animal.age;
            energySum += animal.energy;
        }
        //TODO change all these labels? can be changed to a TableView, more like a ListView
        label1.setText(Integer.toString(animalCount));
        label2.setText(Integer.toString(map.mapHeight*map.mapWidth- map.noHasPlant.size()));
        label3.setText(String.format("%.2f",energySum/animalCount));
        label4.setText(String.format("%.2f",ageSum/animalCount));
        label5.setText(String.format("%.2f",babySum/animalCount));
        if(map.genomeMap.size() > 0) {
            label6.setText(getKeysWithMaxValue(map.genomeMap).get(0).toString());
        }
        else{
            label6.setText("none");
        }
    }


    public void showMap() {
        GraphicsContext gc = canvas.getGraphicsContext2D(); //TODO setting cellSize and these variables is similar
        Bounds bounds = canvas.getBoundsInLocal();
        Map map = simManager.getMap();
        cellSize = Math.min(bounds.getWidth()/map.mapWidth, bounds.getHeight()/map.mapHeight);

        gc.clearRect(0, 0, bounds.getWidth(), bounds.getHeight());
        Vector2 jungleSize = Vector2.subtract(map.jungleEndPos, map.jungleStartPos);
        double jungleWidth  = jungleSize.x*cellSize;
        double jungleHeight = jungleSize.y*cellSize;
        //draw background
        gc.setFill(Color.LIGHTGREEN);
        gc.fillRect(0, 0, bounds.getWidth(), bounds.getHeight());
        //draw jungle
        gc.setFill(Color.DARKSEAGREEN);
        gc.fillRect(map.jungleStartPos.x*cellSize, map.jungleStartPos.y*cellSize, jungleWidth, jungleHeight);
        //draw selected square
        //draw plants
        map.plants.values().forEach(plant -> {
            gc.setFill(plant.getColor());
            gc.fillRect(plant.position.x*cellSize, plant.position.y* cellSize, cellSize, cellSize);
        });
        //draw animals
        map.animals.forEach(animal -> {
            gc.setFill(animal.getColor());
            gc.fillRect(animal.position.x*cellSize, animal.position.y*cellSize, cellSize, cellSize);
        });
        if(selectedPos!=null) {
            gc.setFill(Color.BLACK);
            gc.strokeRect(selectedPos.x * cellSize, selectedPos.y * cellSize, cellSize, cellSize);
        }
    }

    //TODO maybe remove setSize
    public void setSize(int width, int height) {
        canvas.setWidth(width);
        canvas.setHeight(height);
    }

    //TODO move it somwhere else, maybe separate class
    //TODO also, its hacked to return the biggest va
    public static List<Genome> getKeysWithMaxValue(HashMap<Genome, Integer> map){
        final List<Genome> resultList = new ArrayList<>();
        int currentMaxValuevalue = Integer.MIN_VALUE;
        for (java.util.Map.Entry<Genome, Integer> entry : map.entrySet()){
            if (entry.getValue() > currentMaxValuevalue){
                resultList.clear();
                resultList.add(entry.getKey());
                currentMaxValuevalue = entry.getValue();
            } else if (entry.getValue() == currentMaxValuevalue){
                resultList.add(entry.getKey());
            }
        }
        return resultList;
    }

}
