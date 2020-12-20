package sample;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class Animal extends AbstractDrawable implements ISubject{   //TODO can't extend AbstractSubject :c
    Genome genome;
    int energy, babyCount, age;
    Direction currDirection;
    List<IObserver> observers = new ArrayList<>();
    Map map;

    public Animal(Vector2 pos, Map map, Genome genome, int startEnergy){
        super(pos);
        this.map = map;
        color = Color.CORAL;
        this.genome = genome;
        //TODO add event here as well //TODO store less genome objects. No identical genomes. All should be in map genomeMap
        this.energy = startEnergy;      //TODO add map and tracking optional observers (no notification iif no observer)
        this.currDirection = pickDirection();   //TODO also setMap, so that they can be separated and testes more easily
        this.babyCount = 0;
        this.age = 0;
    }

    @Override
    public Color getColor(){ //the lighter the less energy it has
        return color.interpolate(Color.BROWN, energy/100f); //TODO hardcoded colors
    }

    //returns a new animal if there is an empty tile around the parents, returns null otherwise
    public static Animal deliverBaby(Animal mama, Animal papa) {
        Vector2 babyPosition = null;
        for (Vector2 pos : mama.position.getAdjecentPositions()) {
            if (!mama.map.hasAnimal.contains(pos)) {
                babyPosition = pos;
            }
        }
        if (babyPosition == null) {
            return null;    //no place for a child //TODO there should always be place
        }
        else{
            mama.babyCount++;
            papa.babyCount++;
            int babyEnergy = (int) Math.round(mama.energy * 0.25 + papa.energy * 0.25); //this needs to be
            mama.energy *= 0.75;        //before these
            papa.energy *= 0.75;
            Genome babyGenome = Genome.mixGenomes(mama.genome, papa.genome);
            Animal baby = new Animal(mama.position, mama.map, babyGenome, babyEnergy);
            mama.notifyObservers(AnimalEvent.NEW_CHILD, baby);
            papa.notifyObservers(AnimalEvent.NEW_CHILD, baby);
            //baby start direction is set in its constructor
            return baby;
        }
    }

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

    private Direction pickDirection(){
        return Direction.fromInt(genome.chooseGene());
    }

    public String toString(){
        return String.format("Animal (pos = (%d, %d) energy = %d, dir = %s", position.x, position.y, energy, currDirection);
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
    public String toShortString(){
        return String.format("Animal [energy=%d, dir=%s]", energy, currDirection);
    }
}
