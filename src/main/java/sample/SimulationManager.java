package sample;

public class SimulationManager {
    Map map;
    int startEnergy;
    int moveEnergy;
    int plantEnergy;

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
        System.out.println("new gen");
        map.genomeMap.forEach((key, value) -> System.out.println(key + " " + value));
    }
    public Map getMap(){
        return map;
    }
}
