package sample;

import javafx.fxml.FXML;

import javax.sound.midi.Track;
import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;

public class SimulationManager {
    Map map;
    int startEnergy;
    int moveEnergy;
    int plantEnergy;
    int currentGen = 0;
    TrackingControl trackControl;
    StatsManager statManager;

    //TODO create a map in constructor?
    public SimulationManager(Map map, int startAnimalCount, int startPlantCount, int startEnergy, int moveEnergy, int plantEnergy){
        this.map = map;
        this.startEnergy = startEnergy;
        this.moveEnergy = moveEnergy;
        this.plantEnergy = plantEnergy;
        statManager = new StatsManager(this);       //TODO fix crash on too many animals/plants at the start
        for(int i = 0; i < startAnimalCount; i++){
            map.placeAnimalAtRandom(startEnergy);
        }
        for(int i = 0; i < startPlantCount; i++){
            map.spawnGrass();
        }
    }
    public void simulateGen(){
        map.spawnGrass();
        map.killOrMoveAnimals(moveEnergy);
        map.feedAnimals(plantEnergy);
        map.breedAnimals(startEnergy);
        currentGen++;
        statManager.updateCurrGenData();
        System.out.println("Advancing to gen "+currentGen+".");
        if(currentGen == stopTrackingTime){
            trackControl.stopTracking();
        }
        //TODO remove it
        if(currentGen == 100)statManager.startCollectingDataForSave();
        if(currentGen == 400)statManager.stopCollectingDataAndSave();
//        map.genomeMap.forEach((key, value) -> System.out.println(key + " " + value));
    }

    public Map getMap(){
        return map;
    }
    public int getCurrentGen(){return currentGen;}
    int stopTrackingTime;       //TODO move variables up


    public void setTrackingStopTime(int trackingTime, TrackingControl trackControl){   //TODO change this to something smarter
        stopTrackingTime = currentGen + trackingTime;
        this.trackControl = trackControl;           //TODO happens only once anyway
    }
    public void cancelTrackingStopTime(){   //TODO change this to something smarter
        stopTrackingTime = -1;
    }

//    public void setTrackControl(TrackingControl trackControl){
//        this.trackControl = trackControl;
//    }



}
