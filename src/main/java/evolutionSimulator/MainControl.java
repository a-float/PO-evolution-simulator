package evolutionSimulator;

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
    int mapWidth, mapHeight, startAnimalCount, startPlantCount, startEnergy, moveEnergy, plantEnergy;
    float jungleRatio;
    @FXML
    ArrayList<SimulationControl> simulationList;

    public MainControl(){
        loadDataFromJSON();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        for (SimulationControl simControl : simulationList) { //TODO pass varables as parameters somehwere
            if(mapWidth*mapHeight < startAnimalCount){
                throw new IllegalArgumentException("Invalid data in parameters.json. Too many animals.");
            }
            if(mapWidth*mapHeight < startPlantCount){
                throw new IllegalArgumentException("Invalid data in parameters.json. Too many plants.");
            }
            Map map = new Map(mapWidth, mapHeight, jungleRatio);
            SimulationManager simManager = new SimulationManager(map,startEnergy, moveEnergy, plantEnergy);
            map.setSimManager(simManager);  //TODO maybe fix setting up simManagers
            simManager.setUpMap(startAnimalCount, startPlantCount);
            simControl.setManager(simManager);
            double cellSize = simControl.getCellSize();
            //sets canvas size according to its map dimensions.
            //allows for centering of non square maps.
            simControl.setSize((int)Math.round(mapWidth*cellSize), (int)Math.round(mapHeight*cellSize));

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
            mapWidth = (int)(long) data.get("width");
            mapHeight = (int)(long) data.get("height");
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
