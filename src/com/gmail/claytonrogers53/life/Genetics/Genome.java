package com.gmail.claytonrogers53.life.Genetics;

import com.gmail.claytonrogers53.life.Log.Log;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores the Genome for a single thing. Supports the operations performed on it by the GenePool object.
 *
 * Created by Clayton on 15/11/2014.
 */
public class Genome implements Comparable<Genome> {
    /**
     * Implementation of comparison of genomes. Compares the genomes based on their fitness.
     *
     * @param o
     *        The genome to compare to.
     *
     * @return 1 if this is fitter, 0 if they are equal, or -1 if the other genome is fitter.
     */
    @Override
    public int compareTo(@NotNull Genome o) {
        if (this.fitness > o.fitness) {
            return 1;
        } else if (this.fitness == o.fitness) {
            return 0;
        } else if (this.fitness < o.fitness) {
            return -1;
        }

        Log.error("Something has gone terribly wrong while comparing two genomes' fitness.");
        return 0;
    }

    /** The fitness which every new genome starts with. */
    public final static int DEFAULT_GENOME_FITNESS = 0;

    /** Each gene is represented as an integer in the geneticCode. The organism may use the code any way they wish. It
     * is required that no values are invalid. Ex. if there are four possible choices, the 0-1/INT_MAX would be
     * choice one, 1/4*INT_MAX - 2/4*INT_MAX will be option 2, etc. This is because when the next generation is
     * calculated by the gene pool, any gene could end up with any value.
     */
    private final List<Integer> geneticCode;

    /** The fitness of the particular organism, used by the genePool selection algorithm. It is set to default until
     *  the organism is evaluated. */
    private int fitness = DEFAULT_GENOME_FITNESS;

    /**
     * Create a new genome with an empty genetic code. Genes can be added/set using the setGene method.
     *
     * @see #setGene
     */
    public Genome () {
        geneticCode = new ArrayList<>();
    }

    /**
     * Creates a new genome using the provided genetic code.
     *
     * @param geneticCode The genetic code this genome should have.
     */
    public Genome (List<Integer> geneticCode) {
        this();
        for (int i : geneticCode) {
            this.geneticCode.add(i);
        }
    }

    /**
     * Copy constructor. Creates a deep copy of the whole genome and fitness.
     *
     * @param genome The genome to be copied.
     */
    public Genome (Genome genome) {
        this.geneticCode = new ArrayList<>(genome.geneticCode);
        this.fitness = genome.fitness;
    }

    /**
     * Clears ever gene from the genome and clears the fitness. Generally you should not use this method, instead you
     * should simply create a new genome object.
     */
    public void clear () {
        Log.info("Wiping an entire genome.");
        geneticCode.clear();
        fitness = DEFAULT_GENOME_FITNESS;
    }

    /**
     * Allows the user to query a particular gene in the genetic code. The user should check whether that gene exists
     * first by calling get geneticCodeSize.
     *
     * @param location
     *        The location of the gene to be retrieved.
     *
     * @return The value of the gene at that location.
     *
     * @throws ArrayIndexOutOfBoundsException
     *         When the gene requested does not exist.
     */
    public int getGene (int location) throws ArrayIndexOutOfBoundsException {
        return geneticCode.get(location);
    }

    /**
     * Allows the user to query the size of the genetic code, so that they can not request a gene that doesn't exist.
     *
     * @return The size of the genetic code.
     */
    public int getGeneticCodeSize () {
        return geneticCode.size();
    }

    /**
     * Allows the user to set the value of a particular gene. This should generally not be required as the next
     * generation method of the GenePool will generally create all the new genomes. If the current genetic code is
     * smaller than the location specified, then the genetic code will create zero genes up to the location and then
     * add the specified gene. If the location is set to less than zero, the statement will have no effect.
     *
     * @param gene
     *        The value to set the gene to.
     *
     * @param location
     *        The location of the gene to be set.
     */
    public void setGene (int gene, int location) {
        if (location < 0) return;

        while (geneticCode.size() < location+1) {
            geneticCode.add(0);
        }

        try {
            geneticCode.set(location, gene);
        } catch (ArrayIndexOutOfBoundsException e) {
            // Since we have specifically added elements until we have enough to set the required one, the array should
            // never throw out of bounds exception.
            e.printStackTrace();
            System.exit(12);
        }
    }

    /**
     * Allows the user to query the fitness of the genome. If this has not been set previously, it will simply return
     * the default genome fitness.
     *
     * @return The fitness of the genome.
     */
    public int getFitness() {
        return fitness;
    }

    /**
     * Allows the user to set the fitness of the genome. The is typically done after simulating the organism and
     * evaluating its fitness.
     *
     * @param fitness
     *        The value to set the genome's fitness to.
     */
    public void setFitness(int fitness) {
        this.fitness = fitness;
    }


    /**
     * Used by the GenePool to perform the genetic operations to create the next generation.
     *
     * @return The genetic code of the genome in the form of a list.
     */
    List<Integer> getGeneticCode() {
        return geneticCode;
    }

    /**
     * Returns a string representation of the genetic code. The code is printed in HEX with a space between each gene.
     *
     * @return The genetic code as a string.
     */
    public String toString () {
        String output = "";

        for (int gene : geneticCode) {
            output += Integer.toHexString(gene) + " ";
        }

        return output;
    }
}
