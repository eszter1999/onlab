package com.company;

import com.company.data.RoomType;

public class GeneticAlgorithm {
    private int populationSize;
    private double mutationRate;
    private double crossoverRate;
    private int elitismCount;
    protected int tournamentSize;
    private final double min_fittness = 1000;


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


    //base functions
    public Population mutatePopulation(Population population, Timetable timetable) {
        // Initialize new population
        Population newPopulation = new Population(this.populationSize);

        // Loop over current population by fitness
        for (int populationIndex = 0; populationIndex < population.size(); populationIndex++) {
            Individual individual = population.getFittest(populationIndex);

            //int[] clashes

            if(this.elitismCount < populationIndex)
                individual = mutateRandom(individual, mutationRate, timetable);

            for(int i = 0; i < 5; i++) {
                if (mutationRate * 1.5 > Math.random())
                    individual = mutateTimeClash(individual, timetable);
                if (mutationRate * 1.5 > Math.random())
                    individual = mutateRoomClash(individual, timetable);
                if (mutationRate * 1.1 > Math.random())
                    individual = mutateTeacherClash(individual, timetable);
            }

            newPopulation.setIndividual(populationIndex, individual);
        }

        // Return mutated population
        return newPopulation;
    }

    private Individual mutateRandom(Individual individual, double rate, Timetable timetable){
        Individual rndIndividual = new Individual(timetable);
        for(int geneIndex = 0; geneIndex < individual.getChromosomeLength(); geneIndex++){
            if (rate > Math.random())
                individual.setGene(geneIndex, rndIndividual.getGene(geneIndex));
        }
        return individual;
    }

    //ez most random
    private Individual mutateTimeClash(Individual individual, Timetable timetable){
        //az osztályok létrehozása egy ideiglenes órarendbe a vizsgálathoz
        Timetable temp = new Timetable(timetable);
        temp.createClasses(individual);
        int classId = temp.getTimeClash();
        if (classId != -1)
            individual.setGene(classId*2, timetable.getRandomTimeslot().getId());
        return individual;
    }
    private Individual mutateRoomClash(Individual individual, Timetable timetable){
        Timetable temp = new Timetable(timetable);
        temp.createClasses(individual);
        int classId = temp.getRoomClash();
        if (classId != -1){
            int roomId = temp.getClass(classId).getRoomId();
            individual.setGene(classId*2+1, timetable.getRandomRoom(timetable.getRoom(roomId).getType()).getId());
        }
        return individual;
    }
    private Individual mutateTeacherClash(Individual individual, Timetable timetable){
        Timetable temp = new Timetable(timetable);
        temp.createClasses(individual);
        int classId = temp.getTeacherCrash();
        if (classId != -1){
            individual.setGene(classId*2, timetable.getRandomTimeslot().getId());
            int roomId = temp.getClass(classId).getRoomId();
            individual.setGene(classId*2+1, timetable.getRandomRoom(timetable.getRoom(roomId).getType()).getId());
        }
        return individual;
    }

    public Population crossoverPopulation(Population population) {
        // Create new population
        Population newPopulation = new Population(population.size());

        // Loop over current population by fitness
        for (int populationIndex = 0; populationIndex < population.size(); populationIndex++) {
            Individual parent1 = population.getFittest(populationIndex);

            // Apply crossover to this individual?
            if (this.crossoverRate > Math.random() && populationIndex >= this.elitismCount) {
                // Initialize offspring
                Individual offspring = new Individual(parent1.getChromosomeLength());

                // Find second parent
                Individual parent2 = selectParent(population);

                // Loop over genome
                for (int geneIndex = 0; geneIndex < parent1.getChromosomeLength(); geneIndex++) {
                    // Use half of parent1's genes and half of parent2's genes
                    if (0.5 > Math.random()) {
                        offspring.setGene(geneIndex, parent1.getGene(geneIndex));
                    } else {
                        offspring.setGene(geneIndex, parent2.getGene(geneIndex));
                    }
                }

                // Add offspring to new population
                newPopulation.setIndividual(populationIndex, offspring);
            } else {
                // Add individual to new population without applying crossover
                newPopulation.setIndividual(populationIndex, parent1);
            }
        }

        return newPopulation;
    }


    //for fittness
    public double calcFitness(Individual individual, Timetable timetable) {

        // Create new timetable object to use -- cloned from an existing timetable
        Timetable threadTimetable = new Timetable(timetable);
        threadTimetable.createClasses(individual);

        // Calculate fitness
        int clashes = threadTimetable.calcClashes()[0];
        double fitness = 1000 - 100 * clashes;

        individual.setFitness(fitness);

        return fitness;
    }

    public void evalPopulation(Population population, Timetable timetable) {
        double populationFitness = 0;

        // Loop over population evaluating individuals and summing population
        // fitness
        for (Individual individual : population.getIndividuals()) {
            populationFitness += this.calcFitness(individual, timetable);
        }

        population.setPopulationFitness(populationFitness);
    }


    //for crossover
    public Individual selectParent(Population population) {
        // Create tournament
        Population tournament = new Population(this.tournamentSize);

        // Add random individuals to the tournament
        population.shuffle();
        for (int i = 0; i < this.tournamentSize; i++) {
            Individual tournamentIndividual = population.getIndividual(i);
            tournament.setIndividual(i, tournamentIndividual);
        }

        // Return the best
        return tournament.getFittest(0);
    }

}
