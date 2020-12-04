package sample;

import java.util.Arrays;
import java.util.Random;

public class Genome {
    int[] genes;

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
    public Genome(int length){
        if(length < 8){
            throw new IllegalArgumentException("can have a genome this short");
        }
        genes = new int[length];
        for(int i = 0; i < length; i++){
            genes[i] = getRandom(8);
        }
    }

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
                    geneCount[(i + 1)% 8] -= Math.abs(geneCount[i])+1; //change enough genes of the next direction
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
    public static Genome mixGenomes(Genome main, Genome other){
        int genomeLength = main.genes.length;
        int[] result = new int[genomeLength];
        int cut1 = getRandom(1+genomeLength-2);       //two different places to cut the genome
        int cut2 =  getRandom(1+genomeLength-2);
        while(cut2 == cut1)cut2 =  getRandom(1+genomeLength-2);
        if(cut2 < cut1){
            int tmp = cut1; //swap the cuts so that cut1<=cut2
            cut1 = cut2;
            cut2 = tmp;
        }
        int otherPart = getRandom(3);   //the part that will copied from the other genome
        System.out.printf("%d, %d, %d%n",cut1, cut2, otherPart);

        System.arraycopy(main.genes,0, result, 0, genomeLength);
        switch (otherPart) {
            case 0 -> System.arraycopy(other.genes, 0, result, 0, cut1);
            case 1 -> System.arraycopy(other.genes, cut1, result, cut1, cut2 - cut1);
            case 2 -> System.arraycopy(other.genes, cut2, result, cut2, genomeLength - cut2);
        }
        return new Genome(result);
    }

    public int chooseGene(){
        return genes[getRandom(genes.length)];
    }

    private static int getRandom(int length) {
        return new Random().nextInt(length);
    }

    @Override
    public String toString(){
        return "Genome("+ Arrays.toString(genes) +")";
    }
}
