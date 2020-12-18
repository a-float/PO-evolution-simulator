package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ChartControl extends Pane implements Initializable {

    @FXML
    AreaChart areaChart;
    @FXML
    private NumberAxis xAxis;

    private int xSeriesData = 0;
    private static final int MAX_DATA_POINTS = 60;
    private final XYChart.Series<Number, Number> animalSeries = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> plantSeries = new XYChart.Series<>();

    public ChartControl(){
        System.out.println("ChartControl constructed.");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/chart.fxml"));
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
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setUpChart();
        System.out.println("chart initialised");
    }

    private void setUpChart(){
        xAxis.setTickUnit(10f);
        xAxis.setAutoRanging(false);
        areaChart.setTitle("Animated Line Chart");
        areaChart.setHorizontalGridLinesVisible(true);

        animalSeries.setName("Animal count");
        plantSeries.setName("Plant count");
        areaChart.getData().addAll(animalSeries, plantSeries);
    }

    public void updateChart(int newAnimalCount, int newPlantCount){
//        System.out.println("updating chart");
        animalSeries.getData().add(new XYChart.Data<>(xSeriesData++, newAnimalCount));
        plantSeries.getData().add(new XYChart.Data<>(xSeriesData++, newPlantCount));

        // remove points to keep us at no more than MAX_DATA_POINTS //TODO did not use capslock anywhere else
        if (animalSeries.getData().size() > MAX_DATA_POINTS) {
            animalSeries.getData().remove(0, 1);
        }
        if (plantSeries.getData().size() > MAX_DATA_POINTS) {
            plantSeries.getData().remove(0, 1);
        }
        // update
        xAxis.setLowerBound(xSeriesData - MAX_DATA_POINTS);
        xAxis.setUpperBound(xSeriesData - 1);
    }
}
