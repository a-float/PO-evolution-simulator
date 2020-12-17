package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainControl implements Initializable {
    int width, height, startAnimalCount, startPlantCount, startEnergy, moveEnergy, plantEnergy;
    float jungleRatio;
    @FXML
    ArrayList<SimulationControl> simulationList;

    public MainControl(){
        loadDataFromJSON();
        System.out.println("MainControl constructed");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        for (SimulationControl sim : simulationList) { //TODO pass varables as parameters somehwere
            System.out.println("setting up controller "+sim);
            sim.setSize(400,400);
            Map map = new Map(width ,height,jungleRatio);
            SimulationManager simManager = new SimulationManager(map, startAnimalCount, startPlantCount, startEnergy, moveEnergy, plantEnergy);
            sim.setManager(simManager);
        }
    }

    private void loadDataFromJSON(){
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader("parameters.json"))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);

            JSONObject data = (JSONObject) obj;
            System.out.println("Loading the starting parameters: "+data);
            width = (int)(long) data.get("width");
            height = (int)(long) data.get("height");
            jungleRatio = (float)(double) data.get("jungleRatio");
            startAnimalCount = (int)(long) data.get("startAnimalCount");
            startPlantCount = (int)(long) data.get("startPlantCount");
            startEnergy = (int)(long) data.get("startEnergy");
            moveEnergy = (int)(long) data.get("moveEnergy");
            plantEnergy = (int)(long) data.get("plantEnergy");

        } catch (ParseException | IOException e) {  //TODO handle error
            System.out.println("errror reading json");
            e.printStackTrace();
//            throw new IOException("Error while handling the json file.");
        }
    }
}
