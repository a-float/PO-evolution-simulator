package sample;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;


//TODO plants map not needed rn
public class Map {
    int mapWidth;
    int mapHeight;
    HashMap<Vector2, AnimalCollectionList> animalMap = new HashMap<>();
    List<Animal> animals = new ArrayList<>();
    HashMap<Vector2, Plant> plants = new HashMap<>();
    HashMap<Genome, Integer> genomeMap = new HashMap<>();   //maps every animal genome on the map to the number of its occurances
    Set<Vector2> noHasPlant = new HashSet<>(); //used to avoid plant collisions
    Set<Vector2> hasAnimal = new HashSet<>(); //used to keep track of where the animals are
//    float jungleRatio;  //TODO change all the ints below?
    Vector2 jungleStartPos;
    Vector2 jungleEndPos;


    public Map(int width, int height, float jungleRatio){
        mapWidth = width;
        mapHeight = height;
        int xJungleStart = (int)Math.round(mapWidth*(1-jungleRatio)*0.5);
        int xJungleEnd   = (int)Math.round(mapWidth*(1-(1-jungleRatio)*0.5));
        int yJungleStart = (int)Math.round(mapHeight*(1-jungleRatio)*0.5);
        int yJungleEnd   = (int)Math.round(mapHeight*(1-(1-jungleRatio)*0.5));
        jungleStartPos = new Vector2(xJungleStart, yJungleStart);
        jungleEndPos = new Vector2(xJungleEnd, yJungleEnd);
        //the board is empty so no tiles have a plant on them
        for(int x = 0; x < mapWidth; x++){
            for(int y = 0; y < mapHeight; y++){
                Vector2 tmpVector = new Vector2(x,y);
                //there are no plants so all tiles don't have plants on them
                noHasPlant.add(tmpVector);
                //fills animalsMap with empty AnimalsCollections
                animalMap.put(tmpVector, new AnimalCollectionList(3));
            }
        }
    }

    /**
     * Iterates over all the animals. If an animal has energy <= its removed
     * Otherwise, it moves, losing energy equal to moveCost
     * @param moveCost
     */
    public void killOrMoveAnimals(int moveCost){
        Iterator<Animal> iter = animals.iterator();
        while(iter.hasNext()){
            Animal currAnimal = iter.next();
            if (currAnimal.energy <= 0) { //animal dies [*]
                //remove from the AnimalCollection
                removeAnimal(currAnimal);
                removeGenome(currAnimal.genome);
                iter.remove();  //removing from animals
            } else {
                Vector2 newPosition = currAnimal.getNewRawPosition();   //could be oob
                newPosition.x = Math.floorMod(newPosition.x, mapWidth);
                newPosition.y = Math.floorMod(newPosition.y, mapHeight);
                moveAnimal(currAnimal, newPosition, moveCost);
            }
        }
    }

    /**
     * Check al the tiles with plants and animals
     * Feeds all the strongest animals standing on a plant.
     * @param plantEnergy
     */
    public void feedAnimals(float plantEnergy){
        //if there is an animal and there ~(noHasPlant) <=> hasPlant, feed
        for (Vector2 currPos : hasAnimal) {
            if(!noHasPlant.contains(currPos)) {// has a plant
//                System.out.printf("feeding someone at %s%n",currPos);
                List<Animal> animalsToFeed = animalMap.get(currPos).getAllStrongest();
                for(Animal animalToFeed: animalsToFeed){    //actual feeding
                    animalToFeed.eat((int)plantEnergy/animalsToFeed.size());
                }
                plants.remove(currPos); //remove the plant
                noHasPlant.add(currPos); //there is no plant anymore
            }
        }
    }

    /**
     * Checks for tiles where there are animals fulfilling the breeding requirements
     * (at least two animals on the tile and both of them have energy >= 0.5 startEnergy
     * Creates babies where possible.
     * @param startEnergy
     */
    public void breedAnimals(int startEnergy){
        for (Vector2 currPos : hasAnimal) {
            List<Animal> parents = animalMap.get(currPos).getTwoStrongest();
            if(parents == null)return;
            //the weaker parent needs to have energy larger than half of the staring energy
            if(parents.get(1).energy >= startEnergy*0.5) {
                Animal baby = Animal.deliverBaby(parents.get(0), parents.get(1)); //TODO more randomness?
                if(baby == null)return;
                animals.add(baby);
                animalMap.get(baby.position).add(baby);
//                      System.out.printf("new animal at %s%n",baby.position);
                //no need for hasAnimal update because parents are here as well
                //TODO add hasAnimal and make animal not spawn on the parents tile
            }
        }
    }

    /**
     * Adds the new animal to the animals list, animalMap and hasAnimal
     * @param animal
     */
    private void placeNewAnimal(Animal animal){
        animals.add(animal);
        animalMap.get(animal.position).add(animal);
        hasAnimal.add(animal.position);
    }

    private void moveAnimal(Animal animal, Vector2 dest, int moveCost){
        removeAnimal(animal);   //removed from the hasMap and animalMap
        animal.move(dest, moveCost);    //moving the animal
        animalMap.get(dest).add(animal); //adding back to both
        hasAnimal.add(dest); //has at least one animal now
    }

    /**
     * Removes the animal from animalMap and updates the hasAnimal set
     * Doesn't modify animals list.
     * @param animal
     */
    private void removeAnimal(Animal animal){
        animalMap.get(animal.position).remove(animal);
        //AnimalCollection is empty after this animal leaves
        if(animalMap.get(animal.position).size() == 0){
            hasAnimal.remove(animal.position);
        }
//        animals.remove(animal);
    }

    //on a tile with no other Animal
    public void placeAnimalAtRandom(int startEnergy){
        Set<Vector2> noAnimalSet = animalMap.keySet().stream()
                .filter(vec -> !hasAnimal.contains(vec)) //is not in hasAnimals
                .collect(Collectors.toSet());
        //picking a random position from the set of available ones
        Animal animalToAdd = new Animal(getRandomVecFromSet(noAnimalSet),this, new Genome(32), startEnergy);
        placeNewAnimal(animalToAdd);
    }

    public void spawnGrass(){
        //0 for false and 1 for true. Planting outside the jungle first, then inside it
        //kind of hacky, but results in a loop
        for(int i = 0; i <= 1; i++) {
            Vector2 plantPos = pickPlantFreeTile(i==1);
            if (plantPos != null) {
                plants.put(plantPos, new Plant(plantPos, this));
                //System.out.printf("planting a grass on %s%n",plantPos);
                noHasPlant.remove(plantPos);
            }
        }
    }

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
        return getRandomVecFromSet(diffSet);
    }

    public boolean isInJungle(Vector2 vec){
        if(vec.x < jungleStartPos.x)return false;
        if(vec.x > jungleEndPos.x)return false;
        if(vec.y < jungleStartPos.x)return false;
        if(vec.y > jungleEndPos.y)return false;
        return true;
    }

    private Vector2 getRandomVecFromSet(Set<Vector2> set){
        int index = new Random().nextInt(set.size());
        Iterator<Vector2> iter = set.iterator();
        for (int i = 0; i < index; i++) {
            iter.next();
        }
        return iter.next();
    }

    //TODO finish implementing genomeMap
    public void addGenome(Genome genome){
        if(!genomeMap.containsKey(genome)) genomeMap.put(genome, 1);
        else genomeMap.put(genome, genomeMap.get(genome)+1);
    }
    private void removeGenome(Genome genome){
        int newCount = genomeMap.get(genome)-1;
        if(newCount == 0)genomeMap.remove(genome);
        else genomeMap.put(genome, newCount);
    }
}
