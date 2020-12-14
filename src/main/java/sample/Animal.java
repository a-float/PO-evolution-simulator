package sample;

import javafx.scene.paint.Color;

import java.util.UUID;

public class Animal extends AbstractDrawable implements Comparable<Animal> {
    Genome genome;
    int energy, babyCount, age;
    String uniqueId;
    Direction currDirection;

    public Animal(Vector2 pos, Map map, int startEnergy){
        super(pos, map);
        color = Color.CORAL;
        this.genome = new Genome(32);
        this.energy = startEnergy;
        this.currDirection = pickDirection();
        this.babyCount = 0;
        this.age = 0;
        uniqueId = UUID.randomUUID().toString();
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
            //TODO move some of it to animal mate method
            mama.babyCount++;
            papa.babyCount++;
            mama.energy *= 0.75;
            papa.energy *= 0.75;
            int babyEnergy = (int) Math.round(mama.energy * 0.25 + papa.energy * 0.25);
            Animal baby = new Animal(mama.position, mama.map, babyEnergy);
            baby.genome = Genome.mixGenomes(mama.genome, papa.genome);
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

    public void eat(){

    }

    private Direction pickDirection(){
        return Direction.fromInt(genome.chooseGene());
    }

    //this too, im not using TreeSets anymore
    public int compareTo(Animal other){
        int energyCompRes = Math.round(-this.energy + other.energy);
        if(energyCompRes != 0) return energyCompRes;
        else return uniqueId.compareTo(other.uniqueId);
    }
//
    //dunno if i need it
    public boolean equals(Object obj){
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Animal other = (Animal) obj;
        if(!uniqueId.equals(other.uniqueId)){
            return false;
        }
        if(age != other.age || babyCount != other.babyCount){
            return false;
        }
        if (!position.equals(other.position) || energy != other.energy){
            return false;
        }
        return currDirection == other.currDirection && genome.equals(other.genome);
    }

    public String toString(){
        return String.format("Animal (pos = (%d, %d) energy = %d, dir = %s", position.x, position.y, energy, currDirection);
    }
}