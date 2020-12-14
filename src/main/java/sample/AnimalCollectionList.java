package sample;

import java.util.*;

public class AnimalCollectionList implements IAnimalCollection{
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

    public List<Animal> getAllStrongest(){
        ArrayList<Animal> result = new ArrayList<>();
        if(animalList.size() == 0)return result;
        result.add(animalList.get(0));
        if(animalList.size() == 1)return result;
        Iterator<Animal> iter = animalList.iterator();
        //intentionally skipping the first animal as its already in the result list
       Animal currAnimal = iter.next();
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
//        System.out.println("the result:::");
//        result.stream().forEach(System.out::println);
        return result;
    }

    public List<Animal> getTwoStrongest(){
        if(animalList.size()<2)return null;
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
}
