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

public class TrackingControl extends Pane implements Initializable, IObserver, ISleeper {
    @FXML
    TextField timeInput;
    @FXML
    Label trackingOutput;
    @FXML
    ListView<DataPair> trackingListView;
    @FXML
    ListView<Animal> selectedAnimalsListView, trackedAnimListView;   //TODO change it to animal Collection somehow?

    //has the data changed after the last showTrackData(), slightly decreases the amount of trackingListView updates
    private boolean dataHasChanged = true;
    SimulationManager simManager;

    @Override
    public void wakeUp() {
        stopTracking();
    }

    enum dataKey {DEATH, CHILDREN, DESCENDANTS}
    HashMap<dataKey, DataPair> trackingData = new HashMap<>();
    Animal trackedAnimal = null;
    //used to remove itself from observers of the animals after finishing tracking
    List<Animal> observed = new ArrayList<>();  //tracked animals descendants + tracked animal
    boolean tracking = false;   //TODO unnecessary variable?

    public TrackingControl(){
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
        trackingData.put(dataKey.DEATH, new DataPair<>("Died in gen no", "???"));
        trackingData.put(dataKey.CHILDREN, new DataPair<>("Children count", 0));
        trackingData.put(dataKey.DESCENDANTS, new DataPair<>("Descendant count", 0));  //TODO add 's'?
//        trackingData.put(dataKey., new DataPair("Gens left to track:", "0"));
    }

    @FXML
    private void startTracking(ActionEvent event){
        clearUserLog();
        Animal animalToTrack = selectedAnimalsListView.getSelectionModel().getSelectedItem();
        System.out.println("Starting tracking!");
        if(animalToTrack == null){
            logToUser("No animal selected. Select one from list on the left.", Color.RED);
            return;
        }
        int timeToTrack;
        try{
            timeToTrack = Integer.parseInt(timeInput.getText());
        }
        catch(NumberFormatException e){
            logToUser("Invalid observing time. Please input a valid number", Color.RED);
            return;
        }
        if(timeToTrack <= 0){
            logToUser("Invalid observing time. Please input a positive number", Color.RED);
            return;
        }
        if(tracking){
            logToUser("Aleady tracking. Stop first.", Color.RED);
            return;
        }
        trackedAnimal = animalToTrack;
        trackedAnimal.addObserver(this);
        tracking = true;
        simManager.addAlarm(this, timeToTrack);
        resetDataPairs();
        showTrackingData();
        logToUser("Tracking unit gen no "+(simManager.getCurrentGen()+timeToTrack), Color.BLACK);
    }

    private void logToUser(String message, Color color){
        trackingOutput.setText(message);
        trackingOutput.setTextFill(color);
    }
    private void clearUserLog(){
        trackingOutput.setText("");
        trackingOutput.setTextFill(Color.BLACK);
    }

    public void resetDataPairs(){
        trackingData.get(dataKey.DEATH).setSecond("???");
        trackingData.get(dataKey.CHILDREN).setSecond(0);
        trackingData.get(dataKey.DESCENDANTS).setSecond(0);
        dataHasChanged = true;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("chart initialised");
        selectedAnimalsListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Animal animal, boolean empty) {
                super.updateItem(animal, empty);

                if (empty || animal == null || animal.toShortString() == null) {
                    setText(null);
                } else {
                    setText(animal.toShortString());
                }
            }
        });
        trackingListView.setCellFactory(param -> new ListCell<>() {
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
        showTrackingData();
    }

    public void showTrackingData() {
        if(dataHasChanged) {
            trackingListView.getItems().clear();
            for (dataKey key : dataKey.values()) {    //order like in the enum
                trackingListView.getItems().add(trackingData.get(key));
            }
            dataHasChanged = false;
        }
    }

    /**
     * is triggered by both parent animals when they have a child
     * used to count the number or trackedAnimal descendants
     * @param event describes what has happened
     * @param parent animal that has been observed
     * @param newborn its child - will be observed as well
     */
    @Override
    public void notify(AnimalEvent event, Animal parent, Animal newborn) {
        if(!tracking) System.out.println("NOTIFIED WHILE NOT TRACKING?!");
        if(event == AnimalEvent.DEATH && parent == trackedAnimal){
            trackingData.get(dataKey.DEATH).setSecond(Integer.toString(simManager.getCurrentGen()));
            dataHasChanged = true;
        }
        else if(event == AnimalEvent.NEW_CHILD){
            if(newborn == null) {
                throw new IllegalArgumentException("There can't be a NEW_CHILD with null newborn");
            }
            if(!newborn.isObservedBy(this)) {   //it hasn't been counted yet
                if (parent == trackedAnimal) {   //its a new direct child
                    incrementValue(trackingData.get(dataKey.CHILDREN));
                }
                //every child is also a descendant
                //else its some further descendant
                incrementValue(trackingData.get(dataKey.DESCENDANTS));
                newborn.addObserver(this);
                observed.add(newborn);
                dataHasChanged = true;
            }
        }
    }

    @FXML
    public void cancelAlarm(){
        simManager.fireAlarmEarly(this);
    }

    public void clearAnimalSelection(){
        this.selectedAnimalsListView.getItems().clear();
    }
    private void stopTracking(){
        tracking = false;
        trackedAnimal = null;
        for(Animal subject : observed){
            subject.removeObserver(this);
        }
        logToUser("Tracking finished at gen no."+this.simManager.getCurrentGen(), Color.GREEN);
    }

    public void setManager(SimulationManager simManager){
        this.simManager = simManager;
    }

    private void incrementValue(DataPair<String, Integer> dataPair){
        dataPair.setSecond(dataPair.getSecond()+1);
    }
}
