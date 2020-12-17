package sample;

public class SimulationManager {
    Map map;
    int startEnergy;
    int moveEnergy;
    int plantEnergy;
    int currentGen = 0;

    //TODO create a map in constructor?
    public SimulationManager(Map map, int startAnimalCount, int startGrassCount, int startEnergy, int moveEnergy, int plantEnergy){
        this.map = map;
        this.startEnergy = startEnergy;
        this.moveEnergy = moveEnergy;
        this.plantEnergy = plantEnergy;
        for(int i = 0; i < startAnimalCount; i++){
            map.placeAnimalAtRandom(startEnergy);
        }
        for(int i = 0; i < startGrassCount; i++){
            map.spawnGrass();
        }
    }
    public void simulateGen(){
        map.spawnGrass();
        map.killOrMoveAnimals(moveEnergy);
        map.feedAnimals(plantEnergy);
        map.breedAnimals(startEnergy);
        currentGen++;
        System.out.println("Advancing to gen "+currentGen+".");
        if(currentGen == stopTrackingTime){
            trackControl.stopTracking();
        }
//        map.genomeMap.forEach((key, value) -> System.out.println(key + " " + value));
    }

    public Map getMap(){
        return map;
    }
    public int getCurrentGen(){return currentGen;}
    int stopTrackingTime;       //TODO move variables up
    TrackingControl trackControl;

    public void stopAfterXGens(int trackingTime, TrackingControl trackControl){   //TODO change this to something smarter
        stopTrackingTime = currentGen + trackingTime;
        this.trackControl = trackControl;
    }

//    public void setSelectedAnimal(Animal selected, int timeToObserve){
//        if(selectedAnimal != null){
//            selectedAnimal = new Animal()
//        }
//    }
}
