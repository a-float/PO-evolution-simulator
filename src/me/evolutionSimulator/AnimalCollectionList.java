package me.evolutionSimulator;

import java.util.*;

public class AnimalCollectionList {
    ArrayList<Animal> animalList;
    AnimalCollectionList(int size){
        animalList = new ArrayList<>(size);
    }
    AnimalCollectionList(){
        animalList = new ArrayList<>();
    }

    public void add(Animal animal){
        animalList.add(animal);
    }

    public boolean remove(Animal animalToRemove){
        return animalList.remove(animalToRemove);
    }

    /**
     * @return a list of animals with largest energy stored in this collection
     * an empty list if the collection is empty
     */
    public List<Animal> getAllStrongest(){
        ArrayList<Animal> result = new ArrayList<>();
        if(animalList.size() == 0)return result;
        Iterator<Animal> iter = animalList.iterator();
        result.add(iter.next());
        if(animalList.size() == 1)return result;
        Animal currAnimal;
        do{
            currAnimal=iter.next();
//            System.out.println(currAnimal);
            if(currAnimal.energy == result.get(result.size() - 1).energy){
                result.add(currAnimal);
            }
            else if(currAnimal.energy > result.get(result.size() - 1).energy){
                result.clear();
                result.add(currAnimal);
            }
        }while(iter.hasNext());
        return result;
    }

    /**
     * @return a list of two strongest animals in the collection.
     * an empty list if the collection size is smaller than 2
     */
    public List<Animal> getTwoStrongest(){
        if(animalList.size()<2) return new ArrayList<>(0);
        Animal[] result = new Animal[2];
        Iterator<Animal> iter = animalList.iterator();
        result[0] = iter.next();
        result[1] = iter.next();
        //result[0] holds the strongest animal
        if(result[0].energy < result[1].energy){
            Animal tmp = result[1];
            result[1] = result[0];
            result[0] = tmp;
        }
        if(iter.hasNext()) {    //more than 2 elements
            Animal currAnimal;
            do {
                currAnimal = iter.next();
                if (currAnimal.energy > result[0].energy) {
                    result[1] = result[0];
                    result[0] = currAnimal;
                } else if (currAnimal.energy > result[1].energy) {
                    result[1] = currAnimal;
                }
            } while (iter.hasNext());
        }
        return Arrays.asList(result);
    }

    public int size(){
        return animalList.size();
    }

    public List<Animal> getAnimals() {
        return animalList;
    }
}
