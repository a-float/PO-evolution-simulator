package sample;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StatsManager implements IObserver{
    SimulationManager simManager;
    int sumOfAnimals = 0;
    int sumOfPlants = 0;
    HashMap<Genome, Integer> allGenomes = new HashMap<>();  //TODO do the genome stats
    float sumOfAvgEnergy = 0f;
    float sumOfAvgBabies = 0f;
    float sumOfAvgLifespans = 0f;
    float sumOfAvgAge = 0f;
    boolean collecting = false;
    int allDeadAnimals = 0;
    int allLifespans = 0;
    int collectingStartDate;

    @Override
    public void notify(Animal parent, Animal newborn) {
        if(parent == null){
            allLifespans+=newborn.age;
            allDeadAnimals++;
        }
    }
    enum currGenDataKeys {ANIMALS, PLANTS, AVG_ENERGY, AVG_LIFESPAN, AVG_BABIES, AVG_AGE}
    HashMap<currGenDataKeys, DataPair> currGenData = new HashMap<>();

    public StatsManager(SimulationManager simManager){
        this.simManager = simManager;
        this.simManager.getMap().addObserver(this); //tracking map for lifespans TODO maybe shouldn't?
        currGenData.put(currGenDataKeys.ANIMALS, new DataPair("Animal count","?"));
        currGenData.put(currGenDataKeys.PLANTS, new DataPair("Plant count","?"));
        currGenData.put(currGenDataKeys.AVG_ENERGY, new DataPair("Average energy","?"));
        currGenData.put(currGenDataKeys.AVG_LIFESPAN, new DataPair("Average lifespan","?"));
        currGenData.put(currGenDataKeys.AVG_BABIES, new DataPair("Avg. children per animal","?"));
        currGenData.put(currGenDataKeys.AVG_AGE, new DataPair("Avg. animal age","?"));
    }

    public void updateCurrGenData(){
        Map map = this.simManager.getMap();
        float energySum = 0;
        float babySum = 0;
        float ageSum = 0;
        int animalCount = map.animals.size();
        int plantCount = map.mapHeight*map.mapWidth- map.noHasPlant.size();
        for(Animal animal : map.animals) {
            babySum += animal.babyCount;
            ageSum += animal.age;
            energySum += animal.energy;
        }
        float avgEnergy = energySum/animalCount;
        float avgLifespan = (float)allLifespans/allDeadAnimals;
        float avgBabies = babySum/animalCount;
        float avgAge = ageSum/animalCount;
        setNewValueAtKey(currGenDataKeys.ANIMALS, Integer.toString(animalCount));
        setNewValueAtKey(currGenDataKeys.PLANTS, Integer.toString(plantCount));
        setNewValueAtKey(currGenDataKeys.AVG_ENERGY, (String.format("%.2f",avgEnergy)));
        setNewValueAtKey(currGenDataKeys.AVG_LIFESPAN, (String.format("%.2f",avgLifespan)));
        setNewValueAtKey(currGenDataKeys.AVG_BABIES, (String.format("%.2f",avgBabies)));
        setNewValueAtKey(currGenDataKeys.AVG_AGE, (String.format("%.2f",avgAge)));

        if(collecting){
            sumOfAnimals+=animalCount;
            sumOfPlants+=plantCount;
            sumOfAvgEnergy+=avgEnergy;
            sumOfAvgLifespans+=avgLifespan;
            sumOfAvgBabies+=avgBabies;
            sumOfAvgAge+=avgAge;
        }
    }

    private void setNewValueAtKey(currGenDataKeys key, String value){
        currGenData.get(key).setValue(value);
    }

    public void startCollectingDataForSave(){
        sumOfAnimals = 0;
        sumOfPlants = 0;
//        HashMap<Genome, Integer> allGenomes = new HashMap<>();
        sumOfAvgEnergy = 0f;
        sumOfAvgBabies = 0f;
        sumOfAvgLifespans = 0f;
        sumOfAvgAge = 0f;
        collecting = true;
        collectingStartDate = simManager.getCurrentGen();
    }

    public void stopCollectingDataAndSave(){    //TODO needs some buttons
        int collectingTime = simManager.getCurrentGen()-collectingStartDate;
        String data = "Data from gen no " + collectingStartDate + " to gen no " + simManager.getCurrentGen() + "\n" +
                "Data has been collected for " + collectingTime + " generations." + "\n" +
                String.format("%-45s %.2f", "Average animal count per generation:", (float) sumOfAnimals / collectingTime) + "\n" +
                String.format("%-45s %.2f", "Average plant count per generation:", (float) sumOfPlants / collectingTime) + "\n" +
                String.format("%-45s %.2f", "Average animal energy per generation:", sumOfAvgEnergy / collectingTime) + "\n" +
                String.format("%-45s %.2f", "Average dead animal lifespan per generation:", sumOfAvgLifespans / collectingTime) + "\n" +
                String.format("%-45s %.2f", "Average babies per animal per generation:", sumOfAvgBabies / collectingTime) + "\n" +
                String.format("%-45s %.2f", "Average alive animal age per generation:", sumOfAvgAge / collectingTime) + "\n";
        String pathname = "output.txt";
        createFile(pathname);
        writeUsingFileWriter(data,pathname);
    }
    private static void writeUsingFileWriter(String data, String pathname) {
        File file = new File(pathname);
        FileWriter fr = null;
        try {
            fr = new FileWriter(file);
            fr.write(data);
            System.out.println("Succesfully saved the data to "+pathname);
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            //close resources
            try {
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void createFile(String pathname){
        try {
            File myObj = new File(pathname);
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public List<DataPair> getCurrGenDataInOrder(){
        ArrayList<DataPair> data = new ArrayList<>(8);
        for(currGenDataKeys key: currGenDataKeys.values()){
            data.add(currGenData.get(key));
        }
        return data;
    }
}
