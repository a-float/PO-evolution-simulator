package me.evolutionSimulator;

import me.utils.DataPair;

import java.util.*;


public class SimulationManager implements IClock{
    me.evolutionSimulator.Map map;
    int startEnergy;
    int moveEnergy;
    int plantEnergy;
    int currentGen = 0;
    private final StatsManager statManager;
    List<DataPair<ISleeper, Integer>> alarmSchedule = new LinkedList<>();

    public SimulationManager(me.evolutionSimulator.Map map, int startEnergy, int moveEnergy, int plantEnergy){
        this.map = map;
        this.startEnergy = startEnergy;
        this.moveEnergy = moveEnergy;
        this.plantEnergy = plantEnergy;
        statManager = new StatsManager(this);
    }
    public void setUpMap(int startAnimalCount, int startPlantCount){
        for(int i = 0; i < startAnimalCount; i++){
            map.placeAnimalAtRandom(startEnergy);   //all the tiles are empty
        }
        for(int i = 0; i < startPlantCount; i++){
            map.spawnTwoPlants();                   //plants can only be spawned on an empty tile
        }
    }
    public void simulateGen(){
        map.killOrMoveAnimals(moveEnergy);
        map.feedAnimals(plantEnergy);
        map.breedAnimals(startEnergy);
        currentGen++;
        statManager.updateCurrGenData();
        checkAlarmSchedule();
        map.spawnTwoPlants();
    }

    public void startDataSave(){
        statManager.startCollectingDataForSave();
    }
    public void endDataSave(){
        statManager.stopCollectingDataAndSave();
    }
    public Map getMap(){
        return map;
    }
    public List<Animal> getAnimalsByGenome(Genome genome){
        return map.getAnimalsByGenome(genome);
    }
    public int getCurrentGen(){return currentGen;}

    public List<DataPair<Genome, Integer>> getDominantGenomesData(){
        return statManager.getCurrGenDominantGenomesData();
    }

    @Override
    public void addAlarm(ISleeper sleeper, int timeToWakeUp) {
        alarmSchedule.add(new DataPair<>(sleeper, currentGen+timeToWakeUp));
    }

    @Override
    public void checkAlarmSchedule() {
        Iterator<DataPair<ISleeper,Integer>> iter = alarmSchedule.iterator();
        while(iter.hasNext()){
            DataPair<ISleeper, Integer> dp = iter.next();
            if(dp.getSecond() == currentGen){
                dp.getFirst().wakeUp();
                System.out.println("Woke up "+dp.getFirst()+" at gen no"+dp.getSecond());
                iter.remove();
            }
        }
    }
    public void fireAlarmEarly(ISleeper sleeper){
        Iterator<DataPair<ISleeper,Integer>> iter = alarmSchedule.iterator();
        while(iter.hasNext()){
            DataPair<ISleeper, Integer> dp = iter.next();
            if(dp.getFirst() == sleeper){
                dp.getFirst().wakeUp();
                iter.remove();
                break;
            }
        }
    }

    public IObserver getStatManager() {
        return statManager;
    }

    public Iterable<DataPair<String, String>> getCurrStatData() {
        return statManager.getCurrGenDataInOrder();
    }
}
