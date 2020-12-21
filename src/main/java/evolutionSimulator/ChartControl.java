package evolutionSimulator;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ChartControl extends Pane implements Initializable {

    @FXML
    AreaChart areaChart;
    @FXML
    private NumberAxis xAxis;

    private static final int MAX_DATA_POINTS = 60;
    private final XYChart.Series<Integer, Integer> animalSeries = new XYChart.Series<>();
    private final XYChart.Series<Integer, Integer> plantSeries = new XYChart.Series<>();

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
    }

    private void setUpChart(){
        xAxis.setTickUnit(10f);
        xAxis.setAutoRanging(false);
//        areaChart.setTitle("Animated Line Chart");
        areaChart.setHorizontalGridLinesVisible(true);

        animalSeries.setName("Animal count");
        plantSeries.setName("Plant count");
        areaChart.getData().addAll(animalSeries, plantSeries);
    }

    /**
     * Add new data to the chart, and deletes the old one
     * @param currGen new x value for both series
     * @param newAnimalCount new y value for animal series
     * @param newPlantCount new y value for the plants series
     */
    public void updateChart(int currGen, int newAnimalCount, int newPlantCount){
        animalSeries.getData().add(new XYChart.Data<>(currGen, newAnimalCount));
        plantSeries.getData().add(new XYChart.Data<>(currGen, newPlantCount));

        if (animalSeries.getData().size() > MAX_DATA_POINTS) {
            animalSeries.getData().remove(0, 1);
        }
        if (plantSeries.getData().size() > MAX_DATA_POINTS) {
            plantSeries.getData().remove(0, 1);
        }
        // update
        xAxis.setLowerBound(currGen - MAX_DATA_POINTS+1);
        xAxis.setUpperBound(currGen);
    }
}
