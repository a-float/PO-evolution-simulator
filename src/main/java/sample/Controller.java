package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.concurrent.ConcurrentLinkedQueue;

public class Controller {
    @FXML
    Pane pane;
    @FXML
    Canvas canvas;
    @FXML
    Slider speedSlider;
    @FXML
    AreaChart<Number, Number> areaChart;
    @FXML
    Label label1, label2, label3, label4, label5;

    public SimulationManager simManager;
    boolean isPlaying = false;
    Timeline timer;

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
        System.out.println(speed);
        createTimer(0.1/speed);
    }

    private void nextGen(){
        simManager.simulateGen();
        showMap();
        updateStatLabels();
        updateChart();
    }

    public void setManager(SimulationManager simManager) {
        this.simManager = simManager;
    }

    @FXML
    void createTimer(double seconds) {
        if (timer != null) {
            timer.stop();
        }
        timer = new Timeline(new KeyFrame(Duration.seconds(seconds), e -> nextGen()));
        timer.setCycleCount(Timeline.INDEFINITE);
        if(isPlaying)timer.play();
    }
    @FXML
    void initialize() {
        Platform.runLater(this::setUpChart);
        Platform.runLater(this::showMap);
        createTimer(1f/speedSlider.getValue());
        speedSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
            System.out.println(newValue.doubleValue());
            changeSimulationSpeed(newValue.doubleValue());
        });
    }
    //TODO multiple getMaps in this class?
    void updateStatLabels(){
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

        label1.setText(Integer.toString(animalCount));
        label2.setText(Integer.toString(map.mapHeight*map.mapWidth- map.noHasPlant.size()));
        label3.setText(String.format("%.2f",energySum/animalCount));
        label4.setText(String.format("%.2f",ageSum/animalCount));
        label5.setText(String.format("%.2f",babySum/animalCount));
    }

    public void showMap() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Map map = simManager.getMap();
        Bounds bounds = canvas.getBoundsInLocal();
        double cellSize = Math.min(bounds.getWidth()/map.mapWidth, bounds.getHeight()/map.mapHeight);

        gc.clearRect(0, 0, bounds.getWidth(), bounds.getHeight());
        double jungleWidth  = (map.xJungleEnd - map.xJungleStart)*cellSize;
        double jungleHeight = (map.yJungleEnd - map.yJungleStart)*cellSize;
        //draw background
        gc.setFill(Color.LIGHTGREEN);
        gc.fillRect(0, 0, bounds.getWidth(), bounds.getHeight());
        //draw jungle
        gc.setFill(Color.DARKSEAGREEN);
        gc.fillRect(map.xJungleStart*cellSize, map.yJungleStart*cellSize, jungleWidth, jungleHeight);
        //draw plants
        map.plants.values().forEach(plant -> {
            gc.setFill(plant.color);
            gc.fillRect(plant.position.x*cellSize, plant.position.y* cellSize, cellSize, cellSize);
        });
        //draw animals
        map.animals.forEach(animal -> {
            gc.setFill(animal.color);
            gc.fillRect(animal.position.x*cellSize, animal.position.y*cellSize, cellSize, cellSize);
        });
    }
    //TODO maybe remove setSize
    public void setSize(int width, int height) {
        canvas.setWidth(width);
        canvas.setHeight(height);
    }

    //chart things///////////////////////////////////////////////////////////////
    private int xSeriesData = 0;
    private static final int MAX_DATA_POINTS = 60;
    private final XYChart.Series<Number, Number> animalSeries = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> plantSeries = new XYChart.Series<>();
    private final ConcurrentLinkedQueue<Number> animalCountQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Number> plantCountQueue = new ConcurrentLinkedQueue<>();
    @FXML
    private NumberAxis xAxis;
    private void setUpChart(){
//        xAxis = new NumberAxis(0, MAX_DATA_POINTS, MAX_DATA_POINTS / 10f);
//        xAxis.setForceZeroInRange(false);
        xAxis.setTickUnit(10f);
        xAxis.setAutoRanging(false);
//        xAxis.setTickLabelsVisible(false);
//        xAxis.setTickMarkVisible(false);
//        xAxis.setMinorTickVisible(false);
//        areaChart.setAnimated(false);
        areaChart.setTitle("Animated Line Chart");
        areaChart.setHorizontalGridLinesVisible(true);

        animalSeries.setName("Animal count");
        plantSeries.setName("Plant count");
        areaChart.getData().addAll(animalSeries, plantSeries);
    }
    private void addNewDataToChartQueues(){
        //TODO too many getMaps()
        Map map = simManager.getMap();
        animalCountQueue.add(map.animals.size());
        plantCountQueue.add(map.mapHeight*map.mapWidth- map.noHasPlant.size());
    }
    private void addDataToSeries() {
        if (!animalCountQueue.isEmpty()) {
            animalSeries.getData().add(new XYChart.Data<>(xSeriesData++, animalCountQueue.remove()));
            plantSeries.getData().add(new XYChart.Data<>(xSeriesData++, plantCountQueue.remove()));
        }
        // remove points to keep us at no more than MAX_DATA_POINTS
        if (animalSeries.getData().size() > MAX_DATA_POINTS) {
            animalSeries.getData().remove(0, animalSeries.getData().size() - MAX_DATA_POINTS);
        }
        if (plantSeries.getData().size() > MAX_DATA_POINTS) {
            plantSeries.getData().remove(0, plantSeries.getData().size() - MAX_DATA_POINTS);
        }
        // update
        xAxis.setLowerBound(xSeriesData - MAX_DATA_POINTS);
        xAxis.setUpperBound(xSeriesData - 1);
    }
    private void updateChart(){
        addNewDataToChartQueues();
        addDataToSeries();
    }

}
