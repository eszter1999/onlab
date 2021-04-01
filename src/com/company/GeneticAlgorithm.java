package com.company;

public class GeneticAlgorithm {
    private int populationSize;
    private double mutationRate;
    private double crossoverRate;
    private int elitismCount;
    protected int tournamentSize;
    private final double min_fittness = 1.0;

    //constructor
    public GeneticAlgorithm(int populationSize, double mutationRate, double crossoverRate, int elitismCount,
                            int tournamentSize) {

        this.populationSize = populationSize;
        this.mutationRate = mutationRate;
        this.crossoverRate = crossoverRate;
        this.elitismCount = elitismCount;
        this.tournamentSize = tournamentSize;
    }

    public Population initPopulation(Timetable timetable) {
        // Initialize population
        Population population = new Population(this.populationSize, timetable);
        return population;
    }

    //terminate conditions
    public boolean isTerminationConditionMet(int generationsCount, int maxGenerations) {
        return (generationsCount > maxGenerations);
    }

    public boolean isTerminationConditionMet(Population population) {
        return population.getFittest(0).getFitness() == min_fittness;
    }

    //
    public double calcFitness(Individual individual, Timetable timetable) {

        // Create new timetable object to use -- cloned from an existing timetable
        Timetable threadTimetable = new Timetable(timetable);
        threadTimetable.createClasses(individual);

        // Calculate fitness
        int clashes = threadTimetable.calcClashes()[0];
        double fitness = 1 / (double) (clashes + 1);

        individual.setFitness(fitness);

        return fitness;
    }
}
