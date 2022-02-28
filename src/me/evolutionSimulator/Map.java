package me.evolutionSimulator;

import me.utils.Vector2;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Map{
    private final int width;
    private final int height;
    public final HashMap<Vector2, AnimalCollectionList> animalMap = new HashMap<>();
    public final HashMap<Vector2, Plant> plants = new HashMap<>();   //needed for drawing plants
    public final List<Animal> animals = new ArrayList<>();
    final Set<Vector2> noHasPlant = new HashSet<>(); //used to avoid plant collisions
    final Set<Vector2> hasAnimal = new HashSet<>(); //used to keep track of where the animals are
    public final Vector2 jungleStartPos; //included in the jungle
    public final Vector2 jungleEndPos;   //not included in the jungle
    SimulationManager simManger;

    public void setSimManager(SimulationManager simManager){
        this.simManger = simManager;
    }

    public Map(int width, int height, float jungleRatio){
        this.width = width;
        this.height = height;
        int xJungleStart = (int)Math.round(this.width *(1-jungleRatio)*0.5);
        int xJungleEnd   = (int)Math.round(this.width *(1-(1-jungleRatio)*0.5));
        int yJungleStart = (int)Math.round(this.height *(1-jungleRatio)*0.5);
        int yJungleEnd   = (int)Math.round(this.height *(1-(1-jungleRatio)*0.5));
        jungleStartPos = new Vector2(xJungleStart, yJungleStart);
        jungleEndPos = new Vector2(xJungleEnd, yJungleEnd);
        //the board is empty so no tiles have a plant on them
        for(int x = 0; x < this.width; x++){
            for(int y = 0; y < this.height; y++){
                //there are no plants -> all animals spawn on tiles with no plants
                noHasPlant.add(new Vector2(x,y));
                //fills animalsMap with empty AnimalsCollections
                animalMap.put(new Vector2(x,y), new AnimalCollectionList(3));
            }
        }
    }

    /**
     * Iterates over all the animals. If an animal has energy <= its removed
     * Otherwise, it moves, losing energy equal to moveCost
     * @param moveCost how much energy animal loses on making a move
     */
    public void killOrMoveAnimals(int moveCost){
        Iterator<Animal> iter = animals.iterator();
        while(iter.hasNext()){
            Animal currAnimal = iter.next();
            if (currAnimal.energy <= 0) { //animal dies [*]
                removeAnimal(currAnimal);
                currAnimal.die();
                iter.remove();  //removing from animals
            }
            else {
                Vector2 newPosition = parseAnimalPosition(currAnimal.getNewRawPosition());   //could be oob
                moveAnimal(currAnimal, newPosition, moveCost);
            }
        }
    }

    /**
     * Check al the tiles with plants and animals
     * Feeds all the strongest animals standing on a plant.
     * @param plantEnergy how much energy gives eating a plant
     */
    public void feedAnimals(float plantEnergy){
        //if there is an animal and there ~(noHasPlant) <=> hasPlant, feed
        for (Vector2 currPos : hasAnimal) {
            if(!noHasPlant.contains(currPos)) {// has a plant
                List<Animal> animalsToFeed = animalMap.get(currPos).getAllStrongest();
                for(Animal animalToFeed: animalsToFeed){    //actual feeding
                    animalToFeed.eat((int)plantEnergy/animalsToFeed.size());
                }
                plants.remove(currPos);
                noHasPlant.add(currPos); //there is no plant anymore
            }
        }
    }

    /**
     * Checks for tiles where there are animals fulfilling the breeding requirements
     * (at least two animals on the tile and both of them have energy >= 0.5 startEnergy
     * Creates babies where possible.
     * @param startEnergy double the minimal amount of energy animals need to breed
     */
    public void breedAnimals(int startEnergy){
        List<Animal> newBabies = new LinkedList<>();
        for (Vector2 currPos : hasAnimal) {
            List<Animal> parents = animalMap.get(currPos).getTwoStrongest();
            if(parents.size() == 2) {
                //the weaker parent needs to have energy larger than half of the staring energy
                if (parents.get(1).energy >= startEnergy * 0.5) {
                    Animal baby = Animal.deliverBaby(parents.get(0), parents.get(1));
                    baby.position = parseAnimalPosition(baby.position);
                    newBabies.add(baby);
                }
            }
        }
        for(Animal baby: newBabies){
            placeNewAnimal(baby);
        }
    }

    /**
     * Adds the new animal to the animals list, animalMap and hasAnimal
     * Used only once per animal.
     * Adds a statManager observer to the animal - every animal is tracked.
     * Sets animal map to itself.
     * @param animal animal to add
     */
    private void placeNewAnimal(Animal animal){
        animals.add(animal);
        animalMap.get(animal.position).add(animal);
        hasAnimal.add(animal.position);
        animal.setMap(this);
        animal.addObserver(simManger.getStatManager());  //all animals are tracked by the stat manager
        animal.notifyObservers(AnimalEvent.NEW_ANIMAL, null);
    }

    private void moveAnimal(Animal animal, Vector2 dest, int moveCost){
        removeAnimal(animal);   //removed from the hasMap and animalMap
        animal.move(dest, moveCost);    //moving the animal itself
        animalMap.get(dest).add(animal); //adding back to both
        hasAnimal.add(dest); //has at least one animal now
    }

    /**
     * Removes the animal from animalMap and updates the hasAnimal set
     * Doesn't modify the animals list.
     * @param animal animal to remove
     */
    private void removeAnimal(Animal animal){
        animalMap.get(animal.position).remove(animal);
        //AnimalCollection is empty after this animal leaves
        if(animalMap.get(animal.position).size() == 0){
            hasAnimal.remove(animal.position);
        }
    }

    /**
     * Spawn an animal on a random tile not occupied by another animal
     * @param startEnergy energy given to animal upon spawning
     */
    public void placeAnimalAtRandom(int startEnergy){
        Set<Vector2> noAnimalSet = animalMap.keySet().stream()
                .filter(vec -> !hasAnimal.contains(vec)) //is not in hasAnimals
                .collect(Collectors.toSet());
        //picking a random position from the set of available ones
        Animal animalToAdd = new Animal(getRandomVectorFromSet(noAnimalSet), new Genome(32), startEnergy);
        placeNewAnimal(animalToAdd);
    }

    /**
     * spawns two plants, one in the jungle and one outside the jungle
     */
    public void spawnTwoPlants(){
        //0 for false and 1 for true. Planting outside the jungle first, then inside it
        //kind of hacky, but results in a loop
        for(int i = 0; i <= 1; i++) {
            Vector2 plantPos = pickPlantFreeTile(i==1);
            if (plantPos != null) {
                //System.out.printf("planting a grass on %s%n",plantPos);
                plants.put(plantPos, new Plant(plantPos));
                noHasPlant.remove(plantPos);
            }
        }
    }

    /**
     * return a position of the tile in/outside the Jungle, not occupied by an animal nor another plant.
     * @param inJungle  should the returned tile be in the jungle
     * @return  tile to spawn an plant on
     */
    public Vector2 pickPlantFreeTile(boolean inJungle){
        //cant grow plant on a tile occupied by an animal
        Set<Vector2> diffSet;
        if(inJungle) {
            diffSet = noHasPlant.stream()
                    .filter(vec -> !hasAnimal.contains(vec))    //is not in hasAnimals
                    .filter(this::isInJungle)
                    .collect(Collectors.toSet());
        }
        else{
            diffSet = noHasPlant.stream()
                    .filter(vec -> !hasAnimal.contains(vec))    //is not in hasAnimals
                    .filter(vec -> !isInJungle(vec))
                    .collect(Collectors.toSet());
        }
        if(diffSet.size() == 0)return null; //no place for a plant
        return getRandomVectorFromSet(diffSet);
    }

    /**
     * @param vec position to check
     * @return  True if this tile belongs to the jungle, false otherwise.
     */
    public boolean isInJungle(Vector2 vec){
        if(vec.getX() < jungleStartPos.getX())return false;
        if(vec.getX() >= jungleEndPos.getX())return false;
        if(vec.getY() < jungleStartPos.getY())return false;
        if(vec.getY() >= jungleEndPos.getY())return false;
        return true;
    }


    /**
     * @param set set of Vector2
     * @return  returns a random element from the set
     */
    private Vector2 getRandomVectorFromSet(Set<Vector2> set){
        int index = new Random().nextInt(set.size());
        Iterator<Vector2> iter = set.iterator();
        for (int i = 0; i < index; i++) {
            iter.next();
        }
        return iter.next();
    }

    /**
     * @param pos animal created position
     * @return  Vector2 where 0<=x<mapWidth and 0<=y<mapHeight
     */
    private Vector2 parseAnimalPosition(Vector2 pos){
        return new Vector2(Math.floorMod(pos.getX(), width) ,Math.floorMod(pos.getY(), height));
    }

    /**
     * Returns the positions of animals with genome equal to the argument.
     * @param genome Genome to look for in animals.
     * @return  List of found animals positions. Can contain duplicates.
     */
    public List<Animal> getAnimalsByGenome(Genome genome) {
        //no random access needed, just adding and looping over. LinkedList should be better.
        //Set would prevent duplicates in the result, but its not necessary for showing them on the map
        List<Animal> foundAnimals = new LinkedList<>();
        for(Animal animal: animals){
            if(animal.genome.equals(genome)){
                foundAnimals.add(animal);
            }
        }
        return foundAnimals;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
