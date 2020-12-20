package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SaveStatControl extends Pane implements Initializable, ISleeper {
    @FXML
    TextField dataCollectionTimeTextField;
    @FXML
    Label saveStatLogLabel;

    SimulationManager simManager;
    public void setManager(SimulationManager simManager){
        this.simManager = simManager;
    }

    @FXML
    private void startDataSave(ActionEvent event){
        int timeToCollect;
        try{
            timeToCollect = Integer.parseInt(dataCollectionTimeTextField.getText());
        }
        catch(NumberFormatException e){
            //logging labels could be a separate component but javafx sceneBuilder in IntelliJ does not show the
            //views which include other views and its a component too small to be worth fixing it.
            saveStatLogLabel.setTextFill(Color.RED);
            saveStatLogLabel.setText("Invalid argument. Input a number.");
            return;
        }
        if(timeToCollect <= 0){
            saveStatLogLabel.setTextFill(Color.RED);
            saveStatLogLabel.setText("Invalid argument. Input a positive integer.");
            return;
        }
        saveStatLogLabel.setTextFill(Color.BLACK);
        saveStatLogLabel.setText("Stats will be saved in generation no "+(simManager.getCurrentGen()+timeToCollect));
        simManager.startDataSave();
        simManager.addAlarm(this, timeToCollect);
    }

    public SaveStatControl(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/saveStat.fxml"));
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
    public void wakeUp() {
        simManager.endDataSave();
        saveStatLogLabel.setTextFill(Color.GREEN);  //TODO add an error log or smth
        saveStatLogLabel.setText("Data has been saved successfully");
//        else{
//            saveDataLogLabel.setTextFill(Color.RED);
//            saveDataLogLabel.setText("An error while saving occurred.");
//        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {    //TODO should it be empty?
    }
}
