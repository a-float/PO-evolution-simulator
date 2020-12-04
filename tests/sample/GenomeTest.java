package sample;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class GenomeTest {
    Genome g0 = new Genome(new int[]{0,1,2,3,4,5,6,7});
    Genome g1 = new Genome(new int[]{0,2,3,1,1,1,1,1,1,1,1,1,1});
    Genome g10 = new Genome(10);
    Genome g20 = new Genome(20);
    Genome g21 = new Genome(20);

    boolean isValid(Genome genome){
        int[] geneCount = new int[8];
        //geneCount array holds the number of occurrences of each direction in the genome
        for(int g: genome.genes)geneCount[g]++;
        for(int gc : geneCount){
            if(gc == 0) return false;
        }
        return true;
    }

    @Test
    void Genome(){
        assertThrows(IllegalArgumentException.class, () -> new Genome(new int[]{1,2}));
        assertThrows(IllegalArgumentException.class, () -> new Genome(new int[]{1,2,3,4,5,6,7,8,9,10}));
    }

    @Test
    void testToString() {
        assertEquals("Genome([0, 1, 2, 3, 4, 5, 6, 7])", g0.toString());
    }

    @Test
    void repair() {
        assertTrue(isValid(g0));
        assertFalse(isValid(g1));
        g1.repair();
        assertTrue(isValid(g1));
        g10.repair();
        assertTrue(isValid(g10));
        g20.repair();
        assertTrue(isValid(g20));
        g21.repair();
        assertTrue(isValid(g21));
    }

    @Test
    void mixGenomes() {
        g20.repair();
        g21.repair();
        System.out.println(g20);
        System.out.println(g21);
        Genome newGenome = Genome.mixGenomes(g20,g21);
        System.out.println(newGenome);
        assertTrue(isValid(newGenome));
    }

    @Test
    void chooseGene() {
        int[] geneCount = new int[8];
        for(int i = 0; i < 1000; i++){
            geneCount[g0.chooseGene()]++;
        }
        boolean atLeastOneZero = false;
        for(int gc : geneCount){
            if (gc == 0) {
                atLeastOneZero = true;
                break;
            }
        }
        System.out.println(Arrays.toString(geneCount));
        assertFalse(atLeastOneZero);
    }
}