package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class TrackingControl extends Pane implements Initializable, IObserver {
    @FXML
    TextField timeInput;
    @FXML
    Label trackingOutput;
    @FXML
    ListView<DataPair> trackingList;
    @FXML
    ListView<Animal> selectedAnimalsListView;   //TODO change it to animal Collection somehow?

    SimulationManager simManager;
    enum dataKey {DEATH, CHILDREN, DESCENDANTS, TIME_LEFT};
    HashMap<dataKey, DataPair> trackingData = new HashMap<>();
    Animal trackedAnimal = null;
    List<Animal> observed = new ArrayList<>();  //tracked animals descendants + tracked animal
    boolean tracking = false;

    public TrackingControl(){
        System.out.println("TrackingControl constructed.");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/tracking.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException exc) {
            exc.printStackTrace();
            // this is pretty much fatal, so:
            System.exit(1);
        }
        trackingData.put(dataKey.DEATH, new DataPair("Died in gen nr:", "???"));
        trackingData.put(dataKey.CHILDREN, new DataPair("Children count:", "0"));
        trackingData.put(dataKey.DESCENDANTS, new DataPair("Descendants count:", "0"));
//        trackingData.put(dataKey., new DataPair("Gens left to track:", "0"));
    }

    @FXML
    private void startTracking(ActionEvent event){
        clearUserLog();
        Animal animalToTrack = selectedAnimalsListView.getSelectionModel().getSelectedItem();
        System.out.println("Starting tracking!");
        if(animalToTrack == null){
            logToUser("No animal selected. Select one from list on the left.");
            return;
        }
        int timeToTrack;
        try{
            timeToTrack = Integer.parseInt(timeInput.getText());
        }
        catch(NumberFormatException e){
            logToUser("Invalid observing time. Please input a valid number");
            return;
        }
        if(timeToTrack <= 0){
            logToUser("Invalid observing time. Please input a positive number");
            return;
        }
        trackedAnimal = animalToTrack;
        trackedAnimal.addObserver(this);
        tracking = true;
        simManager.stopAfterXGens(timeToTrack, this);
        resetDataPairs();
        showTrackingData();
    }

    private void logToUser(String message){
        trackingOutput.setText(message);
//        trackingOutput.setTextFill(Color.RED);
    }
    private void clearUserLog(){
        trackingOutput.setText("");
//        trackingOutput.setTextFill(Color.BLACK);
    }

    public void resetDataPairs(){
        trackingData.get(dataKey.DEATH).setValue("???");
        trackingData.get(dataKey.CHILDREN).setValue("0");
        trackingData.get(dataKey.DESCENDANTS).setValue("0");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("chart initialised");
        selectedAnimalsListView.setCellFactory(param -> new ListCell<Animal>() {
            @Override
            protected void updateItem(Animal animal, boolean empty) {
                super.updateItem(animal, empty);

                if (empty || animal == null || animal.toString() == null) {
                    setText(null);
                } else {
                    setText(animal.toString());
                }
            }
        });
        trackingList.setCellFactory(param -> new ListCell<DataPair>() {
            @Override
            protected void updateItem(DataPair data, boolean empty) {
                super.updateItem(data, empty);
                if (empty || data == null || data.getPair() == null) {
                    setText(null);
                } else {
                    setText(data.getPair());
                }
            }
        });
        trackingOutput.setTextFill(Color.RED);
        showTrackingData();
    }

    private void showTrackingData() {
        trackingList.getItems().clear();
        for(DataPair dp: trackingData.values()) {
            trackingList.getItems().add(dp);
        }
    }

    @Override
    public void notify(Animal parent, Animal newborn) {
        if (parent == trackedAnimal && newborn != null){    //parent is calling - new child
            DataPair dp = trackingData.get(dataKey.CHILDREN);
            int newChildCount = Integer.parseInt(dp.getValue())+1;
            dp.setValue(Integer.toString(newChildCount));
            newborn.addObserver(this);
            observed.add(newborn);
        }
        else if (newborn != null){    //some descended had a new baby
            DataPair dp = trackingData.get(dataKey.DESCENDANTS);
            int newDescCount = Integer.parseInt(dp.getValue())+1;
            dp.setValue(Integer.toString(newDescCount));
            newborn.addObserver(this);
            observed.add(newborn);
        }
        else if (parent == trackedAnimal) { //parent = trackedAnimal has died [*] (newborn is null)
            trackingData.get(dataKey.DEATH).setValue(Integer.toString(simManager.getCurrentGen()));
        }
        showTrackingData();  //TODO updates too often but oh well
    }

    public void stopTracking(){
        tracking = false;
        trackedAnimal = null;
        for(Animal subject : observed){
            subject.removeObserver(this);
        }
        logToUser("Tracking finished at gen no."+this.simManager.getCurrentGen());
    }

    public void setManager(SimulationManager simManager){
        this.simManager = simManager;
//        System.out.println("manager is set");
//        System.out.printf(simManager.toString());
    }
}
