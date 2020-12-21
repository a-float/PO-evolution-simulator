package evolutionSimulator;

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

public class AnimalTrackControl extends Pane implements Initializable, IObserver, ISleeper {
    @FXML
    TextField timeInput;
    @FXML
    Label trackingOutput;
    @FXML
    ListView<DataPair> trackingListView;
    @FXML
    ListView<Animal> selectedAnimalsListView;

    //has the data changed after the last showTrackData(), slightly decreases the amount of trackingListView updates
    private boolean dataHasChanged = true;
    private SimulationManager simManager;

    private enum trackingDataKeys {DEATH, CHILDREN, DESCENDANTS}
    private final HashMap<trackingDataKeys, DataPair> trackingData = new HashMap<>();
    private Animal trackedAnimal = null;
    //used to remove itself from observers of the animals after finishing tracking
    private final List<Animal> observed = new ArrayList<>();  //tracked animals descendants + tracked animal

    public AnimalTrackControl(){
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
        trackingData.put(trackingDataKeys.DEATH, new DataPair<>("Died in gen no", "???"));
        trackingData.put(trackingDataKeys.CHILDREN, new DataPair<>("Children count", 0));
        trackingData.put(trackingDataKeys.DESCENDANTS, new DataPair<>("Descendant count", 0));  //TODO add 's'?
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
        if(trackedAnimal != null){
            logToUser("Already tracking. Stop first.", Color.RED);
            return;
        }
        trackedAnimal = animalToTrack;
        trackedAnimal.addObserver(this);
        observed.add(trackedAnimal);
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
        trackingData.get(trackingDataKeys.DEATH).setSecond("???");
        trackingData.get(trackingDataKeys.CHILDREN).setSecond(0);
        trackingData.get(trackingDataKeys.DESCENDANTS).setSecond(0);
        dataHasChanged = true;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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
            for (trackingDataKeys key : trackingDataKeys.values()) {    //order like in the enum
                trackingListView.getItems().add(trackingData.get(key));
            }
            dataHasChanged = false;
        }
    }

    /**
     * is triggered by both parent animals when they have a child
     * used to count the number or trackedAnimal descendants
     * @param event describes what has happened
     * @param subject animal that has been observed (parent or deceased)
     * @param newborn its child - will be observed as well
     */
    @Override
    public void notify(AnimalEvent event, Animal subject, Animal newborn) {
        //there should be notifications while the AnimalTrackControl is not tracking.
        //AnimalTrackControl is removed as an observed from all subjects in StopTracking method
        if(event == AnimalEvent.DEATH && subject == trackedAnimal){
            trackingData.get(trackingDataKeys.DEATH).setSecond(Integer.toString(simManager.getCurrentGen()));
            dataHasChanged = true;
        }
        else if(event == AnimalEvent.NEW_CHILD){
            if(newborn == null) {
                throw new IllegalArgumentException("There can't be a NEW_CHILD event with null newborn");
            }
            if(!newborn.isObservedBy(this)) {   //it hasn't been counted yet
                if (subject == trackedAnimal) {   //its a new direct child
                    incrementValue(trackingData.get(trackingDataKeys.CHILDREN));
                }
                //every child is also a descendant
                //else its some further descendant
                incrementValue(trackingData.get(trackingDataKeys.DESCENDANTS));
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

    @Override
    public void wakeUp() {
        stopTracking();
    }

    public void clearAnimalSelection(){
        this.selectedAnimalsListView.getItems().clear();
    }

    private void stopTracking(){
        trackedAnimal = null;
        for(Animal subject : observed){
            subject.removeObserver(this);
        }
        observed.clear();
        logToUser("Tracking finished at gen no."+this.simManager.getCurrentGen(), Color.GREEN);
    }

    public void setManager(SimulationManager simManager){
        this.simManager = simManager;
    }

    private void incrementValue(DataPair<String, Integer> dataPair){
        dataPair.setSecond(dataPair.getSecond()+1);
    }
}
