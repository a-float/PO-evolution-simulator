package evolutionSimulator;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Animal extends AbstractDrawable implements ISubject{
    Genome genome;
    int energy, babyCount, age;
    Direction currDirection;
    List<IObserver> observers = new ArrayList<>();
    Map map;
    static Random random = ThreadLocalRandom.current();

    public void setMap(Map map){
        this.map = map;
    }

    public Animal(Vector2 pos, Genome genome, int startEnergy){
        super(pos, Color.CORAL, Color.BROWN);
        this.genome = genome;
        this.energy = startEnergy;
        this.currDirection = pickDirection();
        this.babyCount = 0;
        this.age = 0;
    }

    public Color getColor(){    //the lighter the less energy the animal has
        return getInterpolatedColor(energy/100f);
    }

    /**
     *
     * @param mama first parent
     * @param papa second parent
     * @return new animal with genome created by mixing parents genomes
     */
    public static Animal deliverBaby(Animal mama, Animal papa) {
        Vector2 babyPosition = null;
        Vector2[] possiblePositions = mama.position.getAdjacentPositions();
        shuffleVectorArray(possiblePositions);
        for (Vector2 pos : possiblePositions){
            if (!mama.map.hasAnimal.contains(pos)) {    //if possible don't spawn on an occupied tile
                babyPosition = pos;
            }
        }
        if (babyPosition == null) { //all the tiles around are occupied
            //no place for a child -> choose a random one
            babyPosition = mama.position.getAdjacentPositions()[random.nextInt(8)];
        }
        mama.babyCount++;
        papa.babyCount++;
        int babyEnergy = (int) Math.round(mama.energy * 0.25 + papa.energy * 0.25); //this needs to be
        mama.energy *= 0.75;        //before these
        papa.energy *= 0.75;
        Genome babyGenome = Genome.mixGenomes(mama.genome, papa.genome);
        Animal baby = new Animal(babyPosition, babyGenome, babyEnergy);
        mama.notifyObservers(AnimalEvent.NEW_CHILD, baby);
        papa.notifyObservers(AnimalEvent.NEW_CHILD, baby);
        //baby's start direction is set in its constructor
        return baby;
    }

    /**
     * @return sum of animal position and its direction's unit vector. May be out of map bounds.
     */
    public Vector2 getNewRawPosition(){
        return Vector2.add(position,currDirection.toUnitVector());
    }


    public void move(Vector2 newPos, int moveCost){
        position = newPos;
        energy -= moveCost;
        currDirection = pickDirection();
        age++;
    }

    public void die(){
        notifyObservers(AnimalEvent.DEATH,null);
    }

    public void eat(int eatEnergy){
        energy += eatEnergy;
    }

    /**
     * @return a random direction from animals genome
     */
    private Direction pickDirection(){
        return Direction.fromInt(genome.chooseGene());
    }

    public String toString(){
        return String.format("Animal (pos = %s energy = %d, dir = %s)", position, energy, currDirection);
    }

    public String toShortString(){
        return String.format("Animal [energy=%d, genome=%s dir=%s]", energy, genome, currDirection);
    }

    /**
     * shuffles array contents
     * used to randomize baby animal spawning tile
     * @param arr array to shuffle
     */
    static void shuffleVectorArray(Vector2[] arr)
    {
        for (int i = arr.length - 1; i > 0; i--)
        {
            int index = random.nextInt(i + 1);
            // Simple swap
            Vector2 a = arr[index];
            arr[index] = arr[i];
            arr[i] = a;
        }
    }

    //////ISubject code
    @Override
    public void addObserver(IObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(IObserver observer) {
        observers.remove(observer);
    }

    @Override
    public boolean isObservedBy(IObserver observer) {
        for(IObserver obs: observers){
            if(obs == observer)return true; //checking just the references
        }
        return false;
    }

    @Override
    public void notifyObservers(AnimalEvent event, Animal newborn) {
        for(IObserver observer: observers){
            observer.notify(event, this, newborn);
        }
    }
}
