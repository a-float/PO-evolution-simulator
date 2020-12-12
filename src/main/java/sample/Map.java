package sample;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;


//TODO plants map not needed rn
public class Map {
    int mapWidth;
    int mapHeight;
    HashMap<Vector2, AnimalCollection> animalsMap = new HashMap<>();
    List<Animal> animals = new ArrayList<>();
    HashMap<Vector2, Plant> plants = new HashMap<>();
    Set<Vector2> noHasPlant = new HashSet<>(); //used to avoid plant collisions
    Set<Vector2> hasAnimal = new HashSet<>(); //used to keep track of where are the animals
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
        System.out.println(xJungleStart+" "+ xJungleEnd+" "+ yJungleStart+" "+  yJungleEnd);
        //the board is empty so no tiles have a plant on them
        for(int x = 0; x < mapWidth; x++){
            for(int y = 0; y < mapHeight; y++){
                Vector2 tmpVector = new Vector2(x,y);
                //there are no plants so all tiles don't have plants on them
                noHasPlant.add(tmpVector);
                //fills animalsMap with empty AnimalsCollections
                animalsMap.put(tmpVector, new AnimalCollection());
            }
        }
    }
    public void killOrMoveAnimals(int moveCost){    //TODO its a pretty big function ngl
        Iterator<Animal> iter = animals.iterator();
        while(iter.hasNext()){
            Animal currAnimal = iter.next();
//            System.out.println(currAnimal);
            if (currAnimal.energy <= 0) { //animal dies [*]
                //remove from the AnimalCollection
                animalsMap.get(currAnimal.position).remove(currAnimal);
                //AnimalCollection is empty after this animal dies
                if(animalsMap.get(currAnimal.position).size() == 0){
                    hasAnimal.remove(currAnimal.position);
                }
                iter.remove();  //ded
//                System.out.println("is ded");
            } else {
                Vector2 newPosition = currAnimal.getNewRawPosition();
                newPosition.x = Math.floorMod(newPosition.x, mapWidth);
                newPosition.y = Math.floorMod(newPosition.y, mapHeight);
                //remove from the previous collection
                animalsMap.get(currAnimal.position).remove(currAnimal);
                //AnimalCollection is empty after this animal leaves
                if(animalsMap.get(currAnimal.position).size() == 0){
                    hasAnimal.remove(currAnimal.position);
                }

                animalsMap.get(newPosition).add(currAnimal); //add to the new one
//                System.out.printf("moved from %s to %s%n", currAnimal.position, newPosition);
                currAnimal.move(newPosition, moveCost);
                hasAnimal.add(newPosition); //has at least one animal now
            }
        }
    }
        //TODO change energy to float/double?
    public void feedAnimals(float plantEnergy){
        //if there is an animal and there ~(noHasPlant) <=> hasPlant, feed
        for (Vector2 currPos : hasAnimal) {
            if(!noHasPlant.contains(currPos)) {// has a plant
//                System.out.printf("feeding someone at %s%n",currPos);
                List<Animal> animalsToFeed = animalsMap.get(currPos).getAllStrongest();
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
            AnimalCollection animCol = animalsMap.get(currPos);
            if(animCol.size() >= 2){    //enough animals to mate
                List<Animal> parents = animCol.getTwoStrongest();
                //the weaker parent needs to have energy larger than half of the staring energy
                if(parents.get(1).energy >= startEnergy*0.5) {
                    Animal baby = Animal.deliverBaby(parents.get(0), parents.get(1)); //TODO more randomness?
                    if(baby != null) {
                        animals.add(baby);
                        animalsMap.get(baby.position).add(baby);
//                    System.out.printf("new animal at %s%n",baby.position);
                        //no need for hasAnimal update beacuse parents are here as well
                        //TODO add hasAnimal and fix animal spawning place
                    }
                }
            }
        }
    }

    private void placeAnimal(Animal animal){
        animals.add(animal);
        animalsMap.get(animal.position).add(animal);
        hasAnimal.add(animal.position);
    }

    public void placeAnimalAtRandom(int startEnergy){//TODO cant spawn more animals than there are tiles
        Set<Vector2> noAnimalSet = animalsMap.keySet().stream()
                .filter(vec -> !hasAnimal.contains(vec)) //is not in hasAnimals
                .collect(Collectors.toSet());   //TODO similar to the code in the pickPlantFreeTile method
        //picking a random position from the set of availalbe ones
        int index = new Random().nextInt(noAnimalSet.size());
        Iterator<Vector2> iter = noAnimalSet.iterator();
        for (int i = 0; i < index; i++) {
            iter.next();
        }
        Animal animalToAdd = new Animal(iter.next(), this, startEnergy);
        placeAnimal(animalToAdd);
    }

    public void placeAnimalAt(Vector2 pos, int startEnergy){
        Animal animalToAdd = new Animal(pos, this, startEnergy);
        placeAnimal(animalToAdd);
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
        int index = new Random().nextInt(diffSet.size());
        Iterator<Vector2> iter = diffSet.iterator();
        for (int i = 0; i < index; i++) {
            iter.next();
        }
        return iter.next();
    }

    public boolean isInJungle(Vector2 vec){
        if(vec.x < xJungleStart)return false;
        if(vec.x > xJungleEnd)return false;
        if(vec.y < yJungleStart)return false;
        if(vec.y > yJungleEnd)return false;
        return true;
    }

}
