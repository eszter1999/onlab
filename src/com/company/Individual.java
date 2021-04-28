package com.company;

import com.company.data.Groups;
import com.company.data.Lessons;

public class Individual {
    private final int[] chromosome;
    private double fitness = -1;

    //constructors
    public Individual(Timetable timetable) {
        int numClasses = timetable.getNumClasses();
        int chromosomeLength = numClasses * 2;
        int[] newChromosome = new int[chromosomeLength];
        int chromosomeIndex = 0;
        for (Groups group : timetable.getGroupsAsArray()) {
            for (int LessonId : group.getLessonsIds()) {

                // Add random time
                int timeslotId = timetable.getRandomTimeslot().getId();
                newChromosome[chromosomeIndex] = timeslotId;
                chromosomeIndex++;

                // Add random room
                int roomId = timetable.getRandomRoom(timetable.getLesson(LessonId).getType().toString()).getId();
                newChromosome[chromosomeIndex] = roomId;
                chromosomeIndex++;
            }
        }
        this.chromosome = newChromosome;
    }

    public Individual(int chromosomeLength) {
        int[] individual;
        individual = new int[chromosomeLength];

        //filling up the chromosomes
        for (int gene = 0; gene < chromosomeLength; gene++) {
            individual[gene] = gene;
        }

        this.chromosome = individual;
    }

    public String toString() {
        String output = "";
        for (int gene = 0; gene < this.chromosome.length; gene++) {
            output += this.chromosome[gene] + ",";
        }
        return output;
    }

    //setters
    public void setFitness(double fitness) {
        this.fitness = fitness;
    }
    public void setGene(int offset, int gene) {
        this.chromosome[offset] = gene;
    }


    //getters
    public double getFitness() {
        return this.fitness;
    }
    public int getGene(int offset) {
        return this.chromosome[offset];
    }
    public int getChromosomeLength() {
        return this.chromosome.length;
    }
    public int[] getChromosome() {
        return this.chromosome;
    }
}
