package me.evolutionSimulator;

import org.junit.jupiter.api.RepeatedTest;
import me.utils.Vector2;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


class AnimalCollectionListTest {
    Integer[] e0 = new Integer[]{};
    Integer[] e1 = new Integer[]{23};
    Integer[] e2 = new Integer[]{2,3};
    Integer[] e3 = new Integer[]{0,0,0,0,0};
    Integer[] e5 = new Integer[]{1,2,3,4,5,6,7,8,9,10};
    Integer[] e4 = new Integer[]{4,4,4,2,1,3,4,4,4};

    //return an AnimalCollection with animals of energies from energies
    AnimalCollectionList createAnimalColl(Integer[] energies){
        AnimalCollectionList result = new AnimalCollectionList();
        for (Integer energy : energies) {
            result.add(new Animal(Vector2.zero(), new Genome(32), energy));
        }
        return result;
    }

    Integer[] listToEnergyArray(List<Animal> animals){
        if(animals.size() == 0)return new Integer[]{};
        return animals.stream()
                .map(a -> a.energy)
                .toArray(Integer[]::new);
    }

    @RepeatedTest(1)
    void getAllStrongest() {
        assertArrayEquals(new Integer[]{}, listToEnergyArray(createAnimalColl(e0).getAllStrongest()));
        assertArrayEquals(new Integer[]{23}, listToEnergyArray(createAnimalColl(e1).getAllStrongest()));
        assertArrayEquals(new Integer[]{3}, listToEnergyArray(createAnimalColl(e2).getAllStrongest()));
        assertArrayEquals(new Integer[]{0,0,0,0,0}, listToEnergyArray(createAnimalColl(e3).getAllStrongest()));
        assertArrayEquals(new Integer[]{4,4,4,4,4,4}, listToEnergyArray(createAnimalColl(e4).getAllStrongest()));
        assertArrayEquals(new Integer[]{10}, listToEnergyArray(createAnimalColl(e5).getAllStrongest()));
    }

    @RepeatedTest(1)
    void getTwoStrongest() {
        assertArrayEquals(new Integer[]{},     listToEnergyArray(createAnimalColl(e0).getTwoStrongest()));
        assertArrayEquals(new Integer[]{},     listToEnergyArray(createAnimalColl(e1).getTwoStrongest()));
        assertArrayEquals(new Integer[]{3,2},  listToEnergyArray(createAnimalColl(e2).getTwoStrongest()));
        assertArrayEquals(new Integer[]{0,0},  listToEnergyArray(createAnimalColl(e3).getTwoStrongest()));
        assertArrayEquals(new Integer[]{4,4},  listToEnergyArray(createAnimalColl(e4).getTwoStrongest()));
        assertArrayEquals(new Integer[]{10,9}, listToEnergyArray(createAnimalColl(e5).getTwoStrongest()));
    }

    @RepeatedTest(1)
    void addRemoveTest(){
        AnimalCollectionList ac = new AnimalCollectionList();
        assertEquals(0, ac.size());
        Animal firstAdded = new Animal(Vector2.zero(),new Genome(32), 30);
        ac.add(firstAdded);
        assertEquals(1, ac.size());
        ac.add(new Animal(Vector2.zero(), new Genome(32), 30));
        assertEquals(2, ac.size());
        ac.add(new Animal(Vector2.zero(), new Genome(32), 30));
        assertEquals(3, ac.size());
        ac.remove(firstAdded);
        assertEquals(2, ac.size());
    }
}