package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main.fxml"));
        Parent root = loader.load();
//        SimulationController controller = loader.getController();
//        controller.setSize(400, 400); //TODO not square sizes
//        SimulationControl simController = new SimulationControl();
//        Map map = new Map(20,20,0.4f);
//        SimulationManager simManager = new SimulationManager(map, 50, 100,50,1,7);
//        simController.setManager(simManager);

//        simController.showMap();
        primaryStage.setTitle("Evolution Simulation");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
