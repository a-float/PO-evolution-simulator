package evolutionSimulator;

import java.util.Arrays;
import java.util.Random;

public class Genome {
    int[] genes;

    /**
     * Creates a Genome from specified genes. Checks if the length and numbers are valid.
     * Doesn't check if every gene occurs at least once
     * @param genes - array of int in range [0,7]
     * return new Genome object with its genes set to the param
     */
    public Genome(int[] genes){
        if(genes.length < 8){
            throw new IllegalArgumentException("can have a genome this short");
        }
        this.genes = genes;
        for (int gene : genes) {
            if (gene < 0 || gene > 7) {
                throw new IllegalArgumentException("gene with no corresponding direction in genome");
            }
        }
    }

    /**
     * creates a valid genome of specified length
     * @param length length of the genome
     */
    public Genome(int length){
        if(length < 8){
            throw new IllegalArgumentException("can have a genome this short");
        }
        genes = new int[length];
        for(int i = 0; i < length; i++){
            genes[i] = getRandom(8);
        }
        this.repair();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Genome genome = (Genome) o;
        return Arrays.equals(genes, genome.genes);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(genes);
    }

    /**
     * modifies the genome so that every number appears at least once
     */
    public void repair(){
        int[] geneCount = new int[8];
        //geneCount array holds the number of occurrences of each direction in the genome
        for(int g: genes)geneCount[g]++;
        boolean repaired = false;
        while(!repaired) {
            repaired = true;
            for (int i = 0; i < 8; i++) {
                if (geneCount[i] <= 0) {
                    repaired = false;                                   //at least one change has been made
                    geneCount[(i + 1)% 8] -= Math.abs(geneCount[i])+1;  //change enough genes of the next direction
                    geneCount[i] = 1;                                   //so that the count of the previous one becomes 1
                }
            }
        }
        int iter = 0;
        for(int gene = 0; gene < 8; gene++){        //recreating the genes;
            for(int i = 0; i < geneCount[gene]; i++){
                genes[iter] = gene;
                iter++;
            }
        }
        Arrays.sort(genes);     //just to make debugging easier
    }

    /**
     * Given two genomes creates a third one, by slicing the given ones in two places and then sticking the randomly choosen parts together
     * @param main genome from which 2 parts will be taken
     * @param other genome from which 1 part will be taken
     * @return  a valid genome - product of mixing the arguments
     */
    public static Genome mixGenomes(Genome main, Genome other){
        int genomeLength = main.genes.length;
        int[] result = new int[genomeLength];
        int cut1 = getRandom(1+genomeLength-2);       //two different places to cut the genome
        int cut2 =  getRandom(1+genomeLength-2);
        while(cut2 == cut1)cut2 = getRandom(1+genomeLength-2);
        if(cut2 < cut1){
            int tmp = cut1; //swap the cuts so that cut1<=cut2
            cut1 = cut2;
            cut2 = tmp;
        }
        int otherPart = getRandom(3);   //the part that will copied from the other genome

        System.arraycopy(main.genes,0, result, 0, genomeLength);
        switch (otherPart) {
            case 0 -> System.arraycopy(other.genes, 0, result, 0, cut1);
            case 1 -> System.arraycopy(other.genes, cut1, result, cut1, cut2 - cut1);
            case 2 -> System.arraycopy(other.genes, cut2, result, cut2, genomeLength - cut2);
        }
        Arrays.sort(result);
        Genome newGenome = new Genome(result);
        newGenome.repair();
        return newGenome;
    }

    /**
     * @return true if the genome has only elements from 0 to 7 and every number appears at least once, false otherwise
     */
    public boolean isValid(){
        int[] geneCount = new int[8];
        //geneCount array holds the number of occurrences of each direction in the genome
        for(int g: genes){
            if(g < 0 || g > 7)return false;
            geneCount[g]++;
        }
        for(int gc : geneCount){
            if(gc == 0) return false;
        }
        return true;
    }

    /**
     * @return random gene from the genome
     */
    public int chooseGene(){
        return genes[getRandom(genes.length)];
    }

    /**
     * @param length max random value
     * @return returns a random number in the range [0, length]
     */
    private static int getRandom(int length) {
        return new Random().nextInt(length);
    }


    @Override
    public String toString(){
        int[] geneCount = new int[8];
        //geneCount array holds the number of occurrences of each direction in the genome
        for(int g: genes)geneCount[g]++;
        StringBuilder res = new StringBuilder("[").append(String.format("%d:%d", 0, geneCount[0]));
        for(int i = 1; i < 8; i++){
//            res.append(String.format("%2d:%-2d", i, geneCount[i]));
            res.append(String.format(" %d:%d", i, geneCount[i]));
        }
        res.append("]");
        return res.toString();
    }
}
