package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainControl implements Initializable {

    @FXML
    ArrayList<SimulationControl> simulationList;
    public MainControl(){
        System.out.println("main controller constructor here");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        for (SimulationControl sim : simulationList) { //TODO pass varables as parameters somehwere
            System.out.println("setting up controller "+sim);
            sim.setSize(400,400);
            Map map = new Map(20,20,0.4f);
            SimulationManager simManager = new SimulationManager(map, 50, 100,50,1,7);
            sim.setManager(simManager);
        }
    }
}
