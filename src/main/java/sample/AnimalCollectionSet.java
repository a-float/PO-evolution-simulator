package sample;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

//Requires Animal to implement Comparable, equals and have an unique id
public class AnimalCollectionSet implements IAnimalCollection{
    TreeSet<Animal> animalSet = new TreeSet<>();

    @Override
    public void add(Animal animal) {
        animalSet.add(animal);
    }

    @Override
    public List<Animal> getTwoStrongest() {
        if(animalSet.size()<2)return null;
        List<Animal> result = new ArrayList<>(2);
        Iterator<Animal> iter = animalSet.iterator();
        result.add(iter.next());
        result.add(iter.next());
//        result.forEach(System.out::println);
        return result;
    }

    @Override
    public List<Animal> getAllStrongest() {
        List<Animal> result = new ArrayList<>(5);
        if(animalSet.isEmpty())return result;
        Iterator<Animal> iter = animalSet.iterator();
        Animal currAnimal = iter.next();
        result.add(currAnimal);
        while(iter.hasNext()){
            currAnimal = iter.next();
            if(currAnimal.energy == result.get(result.size()-1).energy){
                result.add(currAnimal);
            }
            else break;
        }
//        result.forEach(System.out::println);
        return result;
    }

    @Override
    public int size() {
        return animalSet.size();
    }

    @Override
    public boolean remove(Animal animalToRemove){
        return animalSet.remove(animalToRemove);
    }
}
