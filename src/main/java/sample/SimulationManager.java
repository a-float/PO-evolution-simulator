package sample;

import java.util.*;


public class SimulationManager implements IClock{
    Map map;
    int startEnergy;
    int moveEnergy;
    int plantEnergy;
    int currentGen = 0;
    StatsManager statManager;   //TODO make it private?
    List<DataPair<ISleeper, Integer>> alarmSchedule = new LinkedList<>();

    //TODO create a map in constructor?
    public SimulationManager(Map map, int startEnergy, int moveEnergy, int plantEnergy){
        this.map = map;
        this.startEnergy = startEnergy;
        this.moveEnergy = moveEnergy;
        this.plantEnergy = plantEnergy;
        statManager = new StatsManager(this);       //TODO fix crash on too many animals/plants at the start
    }
    public void setUpMap(int startAnimalCount, int startPlantCount){
        for(int i = 0; i < startAnimalCount; i++){
            map.placeAnimalAtRandom(startEnergy);
        }
        for(int i = 0; i < startPlantCount; i++){
            map.spawnPlant();
        }
    }
    public void simulateGen(){
        map.spawnPlant();
        map.killOrMoveAnimals(moveEnergy);
        map.feedAnimals(plantEnergy);
        map.breedAnimals(startEnergy);
        currentGen++;
        statManager.updateCurrGenData();
//        System.out.println("Advancing to gen "+currentGen+".");
        checkAlarmSchedule();
        //TODO remove it
//        map.genomeMap.forEach((key, value) -> System.out.println(key + " " + value));
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
    public int getCurrentGen(){return currentGen;}

    public HashMap<Genome, Integer> getGenomeData(){
        return statManager.allGenomes; //TODO maybe a getter in statManager //TODO should pick the most dominant ones
    }

    @Override
    public void addAlarm(ISleeper sleeper, int timeToWakeUp) {
        alarmSchedule.add(new DataPair<ISleeper, Integer>(sleeper, currentGen+timeToWakeUp));
    }

    @Override
    public void checkAlarmSchedule() {
        Iterator<DataPair<ISleeper,Integer>> iter = alarmSchedule.iterator();
        while(iter.hasNext()){
            DataPair<ISleeper, Integer> dp = iter.next();
            if(dp.getSecond() == currentGen){
                dp.getFirst().wakeUp();
                System.out.println(dp);
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
}
