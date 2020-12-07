package sample;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;


class AnimalCollectionTest {
    Integer[] e0 = new Integer[]{23};
    Integer[] e1 = new Integer[]{1,2,3,4,5,6,7,8,9,10};
    Integer[] e2 = new Integer[]{4,4,4,2,1,3,4,4,4};
    Integer[] e3 = new Integer[]{2,3};
    Integer[] e4 = new Integer[]{0,0,0,0,0};

    //return an AnimalCollection with animals of energies from energies
    AnimalCollection createAnimalColl(Integer[] energies){
        AnimalCollection result = new AnimalCollection();
        for (Integer energy : energies) {
            result.add(new Animal(Vector2.createRandom(100, 100), null, energy));
        }
        return result;
    }

    Integer[] listToEnergyArray(List<Animal> animals){
        return animals.stream()
                .map(a -> a.energy)
                .toArray(Integer[]::new);
    }


    @Test
    void getAllStrongest() {

        assertArrayEquals(new Integer[]{23}, listToEnergyArray(createAnimalColl(e0).getAllStrongest()));
        assertArrayEquals(new Integer[]{10}, listToEnergyArray(createAnimalColl(e1).getAllStrongest()));
        assertArrayEquals(new Integer[]{4,4,4,4,4,4}, listToEnergyArray(createAnimalColl(e2).getAllStrongest()));
        assertArrayEquals(new Integer[]{3}, listToEnergyArray(createAnimalColl(e3).getAllStrongest()));
        assertArrayEquals(new Integer[]{0,0,0,0,0}, listToEnergyArray(createAnimalColl(e4).getAllStrongest()));
    }

    @Test
    void getTwoStrongest() {
        assertArrayEquals(new Integer[]{10,9}, listToEnergyArray(createAnimalColl(e1).getTwoStrongest()));
        assertArrayEquals(new Integer[]{4,4}, listToEnergyArray(createAnimalColl(e2).getTwoStrongest()));
        assertArrayEquals(new Integer[]{3,2}, listToEnergyArray(createAnimalColl(e3).getTwoStrongest()));
        assertArrayEquals(new Integer[]{0,0}, listToEnergyArray(createAnimalColl(e4).getTwoStrongest()));
    }

    @Test
    void addRemoveTest(){
        AnimalCollection ac = new AnimalCollection();
        assertEquals(0, ac.size());
        Animal firstAdded = new Animal(Vector2.createRandom(10,10),null, 30);
        ac.add(firstAdded);
        assertEquals(1, ac.size());
        ac.add(new Animal(Vector2.createRandom(10,10),null, 30));
        assertEquals(2, ac.size());
        ac.add(new Animal(Vector2.createRandom(10,10),null, 30));
        assertEquals(3, ac.size());
        ac.remove(firstAdded);
        assertEquals(2, ac.size());
    }
}