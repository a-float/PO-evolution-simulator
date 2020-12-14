package sample;

import java.util.List;

public interface IAnimalCollection {
    void add(Animal animal);
    List<Animal> getTwoStrongest();
    List<Animal> getAllStrongest();
    int size();
    boolean remove(Animal animalToRemove);
}
