package sample;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Animal extends AbstractDrawable implements ISubject{
    Genome genome;
    int energy, babyCount, age;
    Direction currDirection;
    List<IObserver> observers = new ArrayList<>();

    public Animal(Vector2 pos, Map map, Genome genome, int startEnergy){
        super(pos, map);
        color = Color.CORAL;
        this.genome = genome;
        this.map.addGenome(genome);     //TODO store less genome objects. No identical genomes. All should be in map genomeMap
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
            return null;
        }
        else{
            mama.babyCount++;
            papa.babyCount++;
            mama.energy *= 0.75;
            papa.energy *= 0.75;
            int babyEnergy = (int) Math.round(mama.energy * 0.25 + papa.energy * 0.25);
            Genome babyGenome = Genome.mixGenomes(mama.genome, papa.genome);
            Animal baby = new Animal(mama.position, mama.map, babyGenome, babyEnergy);
            baby.currDirection = Direction.getRandomDirection();
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

    public void eat(int eatEnergy){
        energy += eatEnergy;
    }

    private Direction pickDirection(){
        return Direction.fromInt(genome.chooseGene());
    }

    public String toString(){
        return String.format("Animal (pos = (%d, %d) energy = %d, dir = %s", position.x, position.y, energy, currDirection);
    }

    @Override
    public void addObserver(IObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(IObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(Animal animal) {
        for(IObserver observer: observers){
            observer.notify();
        }
    }
}
