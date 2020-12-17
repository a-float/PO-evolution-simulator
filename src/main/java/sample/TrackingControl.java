package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class TrackingControl extends Pane implements Initializable {
    @FXML
    TextField timeInput;
    @FXML
    ListView<DataPair> trackingList;
    @FXML
    ListView<Animal> selectedAnimalsListView;   //TODO change it to animal Collection somehow?

    ArrayList<DataPair> trackingData = new ArrayList<>();

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

        trackingData.add(new DataPair("Died in gen nr:", "???"));
        trackingData.add(new DataPair("Children count:", "0"));
        trackingData.add(new DataPair("Descendants count:", "0"));
        trackingData.add(new DataPair("Gens left to track:", "0"));
    }

    @FXML
    private void startTracking(ActionEvent event){
        System.out.println("starting");
        Animal animalToChase = selectedAnimalsListView.getSelectionModel().getSelectedItem();
        System.out.println(animalToChase);
    }


    public void resetDataPairs(){
        for(DataPair dp : trackingData){
            dp.setValue("???");
        }
    }

    //TODO shouldnt acces them by the numbers
    public void updateDataPairs(int diedAt, int children, int descendants, int gensLeft){
        if(diedAt != -1) {
            trackingData.get(0).setValue(Integer.toString(diedAt));
        }
        trackingData.get(1).setValue(Integer.toString(children));
        trackingData.get(2).setValue(Integer.toString(descendants));
        trackingData.get(3).setValue(Integer.toString(gensLeft));
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
        applyDataChanges();
    }

    private void applyDataChanges() {
        trackingList.getItems().clear();
        for(DataPair dp: trackingData)
        trackingList.getItems().add(dp);
    }

}
