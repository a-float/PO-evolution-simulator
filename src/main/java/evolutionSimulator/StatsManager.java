package evolutionSimulator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StatsManager implements IObserver{
    SimulationManager simManager;
    private long sumOfAnimals = 0;
    private long sumOfPlants = 0;
    //stores all occurrences of a genome in the history
    private final HashMap<Genome, Integer> allGenomes = new HashMap<>();
    //stores the count of each genome in the current generation, updated via the notify method
    private final HashMap<Genome, Integer> currGenGenomes = new HashMap<>();
    private float sumOfAvgEnergy = 0f;
    private float sumOfAvgBabies = 0f;
    private float sumOfAvgLifespans = 0f;
    private float sumOfAvgAge = 0f;
    boolean nowCollecting = false;
    private long allDeadAnimals = 0;
    private long allLifespans = 0;  //this can be the biggest number here
    private int collectingStartDate;

    private enum currGenDataKeys {CURR_GEN, ANIMALS, PLANTS, AVG_ENERGY, AVG_LIFESPAN, AVG_BABIES, AVG_AGE}
    private final HashMap<currGenDataKeys, DataPair<String, String>> currGenData = new HashMap<>();

    /**
     * Tracks all animals on the simManagers map.
     * @param event expected AnimalEvent.DEATH or AnimalEvent.NEW_ANIMAL
     * @param subject  the deceased or the parent
     * @param newborn  null or baby
     */
    @Override
    public void notify(AnimalEvent event, Animal subject, Animal newborn) { //gets notified by the map it observes when an animal dies
        if(event == AnimalEvent.DEATH){
            allLifespans+=subject.age;
            allDeadAnimals++;
            removeGenome(subject.genome);
        }
        else if(event == AnimalEvent.NEW_ANIMAL){
            addGenome(subject.genome);
        }
    }

    /**
     * Used for displaying the dominant genomes in the list view.
     * @return a list of data pairs of Most dominant Genomes with their number of occurrences in the living animals.
     */
    public List<DataPair<Genome, Integer>> getCurrGenDominantGenomesData(){
        return getDominantGenomesData(currGenGenomes);
    }

    /**
     * @param map map to scan for animals
     * @return List DataPairs of first=dominant genome, second=its number of occurrences
     */
    private List<DataPair<Genome, Integer>> getDominantGenomesData(HashMap<Genome,Integer> map) {
        final List<DataPair<Genome, Integer>> resultList = new ArrayList<>();
        int currentMaxValue = Integer.MIN_VALUE;
        for (java.util.Map.Entry<Genome, Integer> entry : map.entrySet()){
            if (entry.getValue() > currentMaxValue){    //if we found a more dominant genome
                resultList.clear();                     //clear results, add the new one, update the currentMax value
                resultList.add(new DataPair<>(entry.getKey(), entry.getValue()));
                currentMaxValue = entry.getValue();
            } else if (entry.getValue() == currentMaxValue){    //if its as dominant as the previous one, just add it
                resultList.add(new DataPair<>(entry.getKey(), entry.getValue()));
            }
            //else it is weaker, ignore it
        }
        return resultList;
    }



    public StatsManager(SimulationManager simManager){
        this.simManager = simManager;
        currGenData.put(currGenDataKeys.CURR_GEN, new DataPair<>("Current generation","?"));
        currGenData.put(currGenDataKeys.ANIMALS, new DataPair<>("Animal count","?"));
        currGenData.put(currGenDataKeys.PLANTS, new DataPair<>("Plant count","?"));
        currGenData.put(currGenDataKeys.AVG_ENERGY, new DataPair<>("Average energy","?"));
        currGenData.put(currGenDataKeys.AVG_LIFESPAN, new DataPair<>("Average lifespan","?"));
        currGenData.put(currGenDataKeys.AVG_BABIES, new DataPair<>("Avg. children per animal","?"));
        currGenData.put(currGenDataKeys.AVG_AGE, new DataPair<>("Avg. animal age","?"));
    }

    /**
     * updates data needed for displaying current gen statistic
     * and if is collecting for a save, data for the save output.
     */
    public void updateCurrGenData(){
        /////////////////////////////////////////////current stats
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
        setNewValueAtKey(currGenDataKeys.CURR_GEN, Integer.toString(simManager.getCurrentGen()));
        setNewValueAtKey(currGenDataKeys.ANIMALS, Integer.toString(animalCount));
        setNewValueAtKey(currGenDataKeys.PLANTS, Integer.toString(plantCount));
        setNewValueAtKey(currGenDataKeys.AVG_ENERGY, (String.format("%.2f",avgEnergy)));
        setNewValueAtKey(currGenDataKeys.AVG_LIFESPAN, (String.format("%.2f",avgLifespan)));
        setNewValueAtKey(currGenDataKeys.AVG_BABIES, (String.format("%.2f",avgBabies)));
        setNewValueAtKey(currGenDataKeys.AVG_AGE, (String.format("%.2f",avgAge)));

        /////////////////////////////////////////////stats to save file
        if(nowCollecting){
            sumOfAnimals+=animalCount;
            sumOfPlants+=plantCount;
            sumOfAvgEnergy+=avgEnergy;
            sumOfAvgLifespans+=avgLifespan;
            sumOfAvgBabies+=avgBabies;
            sumOfAvgAge+=avgAge;
            currGenGenomes.forEach((key, value) -> {
                //if the genome is already in the map, increment it count by the value in currGenGenomes map
                if (allGenomes.containsKey(key)) {
                    allGenomes.put(key, allGenomes.get(key) + value);
                }
                //otherwise add the entry to the map
                else {
                    allGenomes.put(key, value);
                }
            });
        }
    }
    private void setNewValueAtKey(currGenDataKeys key, String value){
        currGenData.get(key).setSecond(value);
    }

    public void startCollectingDataForSave(){
        sumOfAnimals = 0;
        sumOfPlants = 0;
//        HashMap<Genome, Integer> allGenomes = new HashMap<>();
        sumOfAvgEnergy = 0f;
        sumOfAvgBabies = 0f;
        sumOfAvgLifespans = 0f;
        sumOfAvgAge = 0f;
        nowCollecting = true;
        collectingStartDate = simManager.getCurrentGen();
    }

    public void stopCollectingDataAndSave(){
        int collectingTime = simManager.getCurrentGen()-collectingStartDate;
        StringBuilder data = new StringBuilder("Data from gen no " + collectingStartDate + " to gen no " + simManager.getCurrentGen() + "\n" +
                "Data has been collected for " + collectingTime + " generations." + "\n" +
                String.format("%-45s %.2f", "Average animal count per generation:", (float) sumOfAnimals / collectingTime) + "\n" +
                String.format("%-45s %.2f", "Average plant count per generation:", (float) sumOfPlants / collectingTime) + "\n" +
                String.format("%-45s %.2f", "Average animal energy per generation:", sumOfAvgEnergy / collectingTime) + "\n" +
                String.format("%-45s %.2f", "Average dead animal lifespan per generation:", sumOfAvgLifespans / collectingTime) + "\n" +
                String.format("%-45s %.2f", "Average babies per animal per generation:", sumOfAvgBabies / collectingTime) + "\n" +
                String.format("%-45s %.2f", "Average alive animal age per generation:", sumOfAvgAge / collectingTime) + "\n" +
                String.format("%-45s", "The most dominant genomes were:\n"));
        for(DataPair<Genome, Integer> dp : getDominantGenomesData(allGenomes)){
            data.append(String.format("Genome %10s with %d occurrences.\n", dp.getFirst(), dp.getSecond()));
        }
        data.append("\n\n");
        String pathname = "output.txt";
        createFile(pathname);
        writeUsingFileWriter(data.toString(), pathname);
    }

    private static void writeUsingFileWriter(String data, String pathname) {
        File file = new File(pathname);
        FileWriter fr = null;
        try {
            fr = new FileWriter(file, true);
            fr.write(data);
            System.out.println("Successfully saved the data to "+pathname+".");
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            //close resources
            try {
                if(fr != null) fr.close();
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
            System.out.println("An error occurred while saving.");
            e.printStackTrace();
        }
    }

    public List<DataPair<String, String>> getCurrGenDataInOrder(){
        ArrayList<DataPair<String, String>> data = new ArrayList<>(8);
        for(currGenDataKeys key: currGenDataKeys.values()){
            data.add(currGenData.get(key));
        }
        return data;
    }

    public void addGenome(Genome genome){
        if(!currGenGenomes.containsKey(genome)) currGenGenomes.put(genome, 1);
        else currGenGenomes.put(genome, currGenGenomes.get(genome)+1);
    }
    private void removeGenome(Genome genome){
        //genome should always be in allGenomes
        int newCount = currGenGenomes.get(genome)-1;
        if(newCount == 0)currGenGenomes.remove(genome);
        else currGenGenomes.put(genome, newCount);
    }
}
