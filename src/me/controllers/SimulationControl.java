package me.controllers;

import me.evolutionSimulator.Animal;
import me.evolutionSimulator.AnimalCollectionList;
import me.evolutionSimulator.Map;
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
import me.evolutionSimulator.SimulationManager;
import me.utils.Vector2;
import me.utils.DataPair;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Manages the simulation. Draws the map on the canvas, updates the current generation statistics, and contains the area chart.
 * Controls play speed of the simulation.
 */
public class SimulationControl extends VBox implements Initializable{
    @FXML
    Canvas canvas;
    @FXML
    Slider speedSlider; //addSpeedControl?
    @FXML
    ChartControl chartControl;
    @FXML
    AnimalTrackControl animalTrackControl;
    @FXML
    SaveStatControl saveStatControl;
    @FXML
    ListView<DataPair> currGenStatsListView;
    @FXML
    GenomeTrackControl genomeTrackControl;

    private final Color GRASS_COLOR = Color.LIGHTGREEN;
    private final Color JUNGLE_COLOR = Color.DARKSEAGREEN;
    private final Color SINGLE_SELECT_COLOR = Color.BLACK;
    private final Color SELECT_ANIMALS_BY_GENOME_COLOR = Color.BLUE;
    private Vector2 selectedPos = null; //position of the selected tile on the map. Used for drawing.

    public SimulationManager simManager;
    boolean isPlaying = false;
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
        // This will now be called after the @FXML-annotated fields are initialized.
        createTimer(0.1f/speedSlider.getValue());
        speedSlider.valueProperty().addListener((obs, oldValue, newValue) -> changeSimulationSpeed(newValue.doubleValue()));

        currGenStatsListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(DataPair data, boolean empty) {
                super.updateItem(data, empty);
                if (empty || data == null || data.getStringPair() == null) {
                    setText(null);
                } else {
                    setText(data.getStringPair());
                }
            }
        });

        Platform.runLater(this::showMap);
        Platform.runLater(this::showCurrGenData);
        Platform.runLater(() -> animalTrackControl.setManager(simManager));
        Platform.runLater(() -> saveStatControl.setManager(simManager));
        Platform.runLater(() -> genomeTrackControl.setManager(simManager));
        Platform.runLater(() -> genomeTrackControl.setSimControl(this));
    }

    /**
     * Handles selecting a tile from the map.
     * Passes the animals from the selected tile to the tracking control.
     * @param event not used
     */
    @FXML
    private void canvasClicked(MouseEvent event){
        if(!isPlaying) {
            Map map = simManager.getMap();
            Bounds bounds = canvas.getBoundsInLocal();
            double cellSize = Math.min(bounds.getWidth() / map.getWidth(), bounds.getHeight() / map.getHeight());
            int x = (int) Math.floor(event.getX() / cellSize);
            int y = (int) Math.floor(event.getY() / cellSize);
            selectedPos = new Vector2(x, y);

            animalTrackControl.selectedAnimalsListView.getItems().clear();
            AnimalCollectionList selectedAnimalCollection = simManager.getMap().animalMap.get(selectedPos);
            for (Animal animal : selectedAnimalCollection.getAnimals()) {
                animalTrackControl.selectedAnimalsListView.getItems().add(animal);
            }
            showMap();
        }
    }

    /**
     * generate one step of the simulation
     * cannot step while the simulation is not paused
     * @param event not used
     */
    @FXML
    private void doStep (ActionEvent event) {
        if(!isPlaying)nextGen();
    }

    @FXML
    private void toggleSimulationPlaying(ActionEvent event) {
        if(isPlaying)timer.pause();
        else timer.play();
        isPlaying = !isPlaying;
    }

    @FXML
    private void changeSimulationSpeed (double speed) {
        createTimer(0.1/speed);
    }

    private void nextGen(){
        selectedPos = null; //this too
//        selectedGenomeAnimals.clear();
        animalTrackControl.clearAnimalSelection();
        simManager.simulateGen();
        showMap();
        showCurrGenData();
        animalTrackControl.showTrackingData();
        Map map = simManager.getMap();
        chartControl.updateChart(simManager.getCurrentGen(), map.animals.size(), map.plants.size());
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

    public void showCurrGenData(){
        currGenStatsListView.getItems().clear();
        simManager.getCurrStatData().forEach(dp -> currGenStatsListView.getItems().add(dp));

        genomeTrackControl.updateListView(simManager.getDominantGenomesData());
    }

    public void showMap() { //TODO hardcoded colors
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Bounds bounds = canvas.getBoundsInLocal();
        Map map = simManager.getMap();
        double cellSize = Math.min(bounds.getWidth()/map.getWidth(), bounds.getHeight()/map.getHeight());

        gc.clearRect(0, 0, bounds.getWidth(), bounds.getHeight());
        Vector2 jungleSize = Vector2.subtract(map.jungleEndPos, map.jungleStartPos);
        double jungleWidth  = jungleSize.getX()*cellSize;
        double jungleHeight = jungleSize.getY()*cellSize;
        //draw background
        gc.setFill(GRASS_COLOR);
        gc.fillRect(0, 0, map.getWidth()*cellSize, map.getHeight()*cellSize);
        //draw jungle
        gc.setFill(JUNGLE_COLOR);
        gc.fillRect(map.jungleStartPos.getX()*cellSize, map.jungleStartPos.getY()*cellSize, jungleWidth, jungleHeight);
        //draw plants
        map.plants.values().forEach(plant -> {
            gc.setFill(plant.getColor());
            gc.fillRect(plant.getPosition().getX()*cellSize, plant.getPosition().getY()* cellSize, cellSize, cellSize);
        });
        //draw animals
        map.animals.forEach(animal -> {
            gc.setFill(animal.getColor());
            gc.fillRect(animal.getPosition().getX()*cellSize, animal.getPosition().getY()*cellSize, cellSize, cellSize);
        });
        //draw selected square
        if(selectedPos!=null) {
            gc.setStroke(SINGLE_SELECT_COLOR);
            gc.strokeRect(selectedPos.getX() * cellSize, selectedPos.getY() * cellSize, cellSize, cellSize);
        }
        //draw animals tracked by their dominant genome
        genomeTrackControl.getAnimalsWithDominantGenome().forEach(animal -> {
            gc.setFill(SELECT_ANIMALS_BY_GENOME_COLOR);
            gc.fillRect(animal.getPosition().getX() * cellSize, animal.getPosition().getY() * cellSize, cellSize, cellSize);
        });
    }

    public double getCellSize(int size){
        Map map = simManager.getMap();
        return Math.min(size/map.getWidth(), size/map.getHeight());
    }

    public void setSize(int width, int height) {
        canvas.setWidth(width);
        canvas.setHeight(height);
    }
}
