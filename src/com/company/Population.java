package com.company;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class Population {
    private Individual[] population;
    private double populationFitness = -1;

    public Population(int populationSize, Timetable timetable) {
        // Initial population
        this.population = new Individual[populationSize];

        // Loop over population size
        for (int individualCount = 0; individualCount < populationSize; individualCount++) {
            // Create individual
            Individual individual = new Individual(timetable);
            // Add individual to population
            this.population[individualCount] = individual;
        }
    }

    public Population(int populationSize) {
        // Initial population
        this.population = new Individual[populationSize];
    }

    public void shuffle() {
        Random rnd = new Random();
        for (int i = population.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            Individual a = population[index];
            population[index] = population[i];
            population[i] = a;
        }
    }

    public Individual setIndividual(int offset, Individual individual) {
        return population[offset] = individual;
    }
    public void setPopulationFitness(double fitness) {
        this.populationFitness = fitness;
    }


    //getters
    public int size() {
        return this.population.length;
    }
    public Individual[] getIndividuals() {
        return this.population;
    }
    public Individual getIndividual(int offset) {
        return population[offset];
    }
    public Individual getFittest(int offset) {
        // Order population by fitness
        Arrays.sort(this.population, new Comparator<Individual>() {
            @Override
            public int compare(Individual o1, Individual o2) {
                if (o1.getFitness() > o2.getFitness()) {
                    return -1;
                } else if (o1.getFitness() < o2.getFitness()) {
                    return 1;
                }
                return 0;
            }
        });

        // Return the fittest individual
        return this.population[offset];
    }
}
