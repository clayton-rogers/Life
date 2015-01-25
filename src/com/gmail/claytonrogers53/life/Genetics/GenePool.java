package com.gmail.claytonrogers53.life.Genetics;

import com.gmail.claytonrogers53.life.Log.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Contains a large number of genetic individuals. Controls the operations which can be run on those individuals.
 *
 * Created by Clayton on 15/11/2014.
 */
public final class GenePool {

    /** Where all the genomes are stored. */
    private List<Genome> genePool = new ArrayList<>();

    /** The number of next generations that have been calculated. */
    private int generation = 0;

    /** Add a particular genome to the genePool */
    /**
     * Adds a particular genome to the GenePool.
     *
     * @param genome
     *        The genome to be added to the GenePool.
     */
    public void addGenome (Genome genome) {
        genePool.add(genome);
        Log.info("Added a genome to the gene pool.");
    }

    /**
     * Removes a particular genome from the GenePool.
     *
     * @param genome
     *        The genome to be removed.
     */
    public void removeGenome (Genome genome) {
        boolean didRemoveDoAnything = genePool.add(genome);
        if (!didRemoveDoAnything) {
            Log.warning("Attempted to remove and gene pool member which didn't exist.");
        }
    }

    /**
     * Allows the user to query for every individual in the GenePool. The individuals are then evaluated and their
     * fitness is set. The list of genome should not be otherwise modified.
     *
     * @return The list of genome currently in the GenePool.
     */
    public List<Genome> getGenomes () {
        return genePool;
    }

    /**
     * Calculates using genetic algorithms what the individuals in the next generation should be, based on genes of
     * the current generation and their fitness. The algorithm is subject to change in the future, more customization
     * options will likely be added.
     */
    public void nextGeneration () {
        generation++;
        Log.info("Generating generation " + String.valueOf(generation) + ".");

        int populationFitness = 0;
        for (Genome g : genePool) {
            populationFitness += g.getFitness();
        }

        ArrayList<Genome> nextGeneration = new ArrayList<>(genePool.size());

        // Sorting the genePool from most fit to least.
        Collections.sort(genePool);

        // For every member of the new population, figure out what that new member is.
        for (int i = 0; i < genePool.size(); i++) {
            nextGeneration.add(
                    combineGenomes(
                            getWhichGenome(populationFitness),
                            getWhichGenome(populationFitness)
                    )
            );
        }

        // Move the next generation to the old generation's spot. The old generation is discarded.
        genePool = nextGeneration;
    }


    /**
     * Allows the user to completely clear the genomes from the gene pool and reset the generation number. This should
     * generally not be used. Instead a new gene pool should just be created.
     */
    public void clear () {
        Log.info("Entire gene pool cleared.");
        genePool.clear();
        generation = 0;
    }

    /**
     * Internal method which, given two parents, randomly calculates the offspring's genetic code.
     *
     * @param input1
     *        The first parent.
     *
     * @param input2
     *        The second parent.
     *
     * @return The offspring of the two parents.
     */
    private Genome combineGenomes (Genome input1, Genome input2) {

        Genome returnGenome;

        // We will mix either the first half of the first input with the second half of the second input, or we will
        // combine the first half of the second input with the second half of the first input.
        if ((int) (Math.random()*2) == 1) {
            // First half of the first input.

            int cutLocation = (int) (Math.random() * input1.getGeneticCodeSize());
            List<Integer> firstSlice = input1.getGeneticCode().subList(0, cutLocation);

            cutLocation = (int) (Math.random() * input2.getGeneticCodeSize());
            List<Integer> secondSlice = input2.getGeneticCode().subList(cutLocation, input2.getGeneticCode().size());

            firstSlice.addAll(secondSlice);
            returnGenome = new Genome(firstSlice);

        } else {
            // First half of the second input.

            int cutLocation = (int) (Math.random() * input2.getGeneticCodeSize());
            List<Integer> firstSlice = input2.getGeneticCode().subList(0, cutLocation);

            cutLocation = (int) (Math.random() * input1.getGeneticCodeSize());
            List<Integer> secondSlice = input1.getGeneticCode().subList(cutLocation, input1.getGeneticCode().size());

            firstSlice.addAll(secondSlice);
            returnGenome = new Genome(firstSlice);

        }
        return returnGenome;
    }

    /**
     * Steps through the population till it finds the member defined by a random number. This is where the fitness
     * proportional selection is done.
     *
     * @param populationFitness
     *        The total fitness of the entire population.
     *
     * @return A reference to the selected genome.
     */
    private Genome getWhichGenome (int populationFitness) {
        int desiredFitness = (int) (Math.random() * populationFitness);

        for (Genome g : genePool) {
            desiredFitness -= g.getFitness();

            if (desiredFitness <= 0) {
                return g;
            }
        }

        return genePool.get(genePool.size()-1);
    }

    /**
     * Allows the user to query the number of generations that have been calculated.
     *
     * @return The generation number of the GenePool.
     */
    public int getGeneration () {
        return generation;
    }
}
