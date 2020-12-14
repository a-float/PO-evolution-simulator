package sample;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;


//TODO plants map not needed rn
public class Map {
    int mapWidth;
    int mapHeight;
    HashMap<Vector2, IAnimalCollection> animalMap = new HashMap<>();
    List<Animal> animals = new ArrayList<>();
    HashMap<Vector2, Plant> plants = new HashMap<>();
    Set<Vector2> noHasPlant = new HashSet<>(); //used to avoid plant collisions
    Set<Vector2> hasAnimal = new HashSet<>(); //used to keep track of where the animals are
    float jungleRatio;  //TODO change all the ints below?
    final int xJungleStart;
    final int xJungleEnd;
    final int yJungleStart;
    final int yJungleEnd;

    public Map(int width, int height, float jglRatio){
        mapWidth = width;
        mapHeight = height;
        jungleRatio = jglRatio;
        xJungleStart = (int)Math.round(mapWidth*(1-jungleRatio)*0.5);
        xJungleEnd   = (int)Math.round(mapWidth*(1-(1-jungleRatio)*0.5));
        yJungleStart = (int)Math.round(mapHeight*(1-jungleRatio)*0.5);
        yJungleEnd   = (int)Math.round(mapHeight*(1-(1-jungleRatio)*0.5));
        System.out.println("Jungle coords: "+xJungleStart+" "+ xJungleEnd+" "+ yJungleStart+" "+  yJungleEnd);
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
    public void killOrMoveAnimals(int moveCost){    //TODO its a pretty big function ngl
        Iterator<Animal> iter = animals.iterator();
        while(iter.hasNext()){
            Animal currAnimal = iter.next();
//          System.out.println(currAnimal);
            if (currAnimal.energy <= 0) { //animal dies [*]
                //remove from the AnimalCollection
                removeAnimal(currAnimal);
                iter.remove();  //ded
//                System.out.println("is ded");
            } else {
                Vector2 newPosition = currAnimal.getNewRawPosition();   //could be oob
                newPosition.x = Math.floorMod(newPosition.x, mapWidth);
                newPosition.y = Math.floorMod(newPosition.y, mapHeight);
                //remove from the previous collection
                moveAnimal(currAnimal, newPosition, moveCost);
            }
        }
    }
        //TODO change energy to float/double?
    public void feedAnimals(float plantEnergy){
        //if there is an animal and there ~(noHasPlant) <=> hasPlant, feed
        for (Vector2 currPos : hasAnimal) {
            if(!noHasPlant.contains(currPos)) {// has a plant
//                System.out.printf("feeding someone at %s%n",currPos);
                List<Animal> animalsToFeed = animalMap.get(currPos).getAllStrongest();
                for(Animal animalToFeed: animalsToFeed){    //actual feeding
//                    System.out.println(animalToFeed.energy);
                    animalToFeed.energy += plantEnergy/animalsToFeed.size();
//                    System.out.println(animalToFeed.energy);
                }
                plants.remove(currPos); //remove the plant
                noHasPlant.add(currPos); //there is no plant anymore
            }
        }
    }
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

    private void placeNewAnimal(Animal animal){
        animals.add(animal);
        animalMap.get(animal.position).add(animal);
        hasAnimal.add(animal.position);
    }

    private void moveAnimal(Animal animal, Vector2 dest, int moveCost){
        animalMap.get(animal.position).remove(animal);
        //AnimalCollection is empty after this animal leaves
        if(animalMap.get(animal.position).size() == 0){
            hasAnimal.remove(animal.position);
        }
        animalMap.get(dest).add(animal); //add to the new one
//      System.out.printf("moved from %s to %s%n", currAnimal.position, newPosition);
        animal.move(dest, moveCost);
        hasAnimal.add(dest); //has at least one animal now
    }

    private void removeAnimal(Animal animal){
        animalMap.get(animal.position).remove(animal);
        //AnimalCollection is empty after this animal leaves
        if(animalMap.get(animal.position).size() == 0){
            hasAnimal.remove(animal.position);
        }
    }

    //on a tile with no other Animal
    public void placeAnimalAtRandom(int startEnergy){
        Set<Vector2> noAnimalSet = animalMap.keySet().stream()
                .filter(vec -> !hasAnimal.contains(vec)) //is not in hasAnimals
                .collect(Collectors.toSet());
        //picking a random position from the set of available ones
        Animal animalToAdd = new Animal(getRandomVecFromSet(noAnimalSet),this, startEnergy);
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
        if(vec.x < xJungleStart)return false;
        if(vec.x > xJungleEnd)return false;
        if(vec.y < yJungleStart)return false;
        if(vec.y > yJungleEnd)return false;
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
}
