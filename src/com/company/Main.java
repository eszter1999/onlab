package com.company;

import com.company.data.RoomType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Main {
    static final int
            population_size = 290,  /*101*/
            elitism_count = 2,
            tournament_size = 5,
            max_generation = 1200;
    static final double
            mutation_rate = 0.002,
            crossover_rate = 0.95;

    public static void main(String[] args) throws IOException {
	    Timetable timetable = initializeTimetable();
        GeneticAlgorithm ga = new GeneticAlgorithm(population_size, mutation_rate, crossover_rate, elitism_count, tournament_size);
        Population population = ga.initPopulation(timetable);

        // Keep track of current generation
        int generation = 1;
        while (!ga.isTerminationConditionMet(generation, max_generation)
                && !ga.isTerminationConditionMet(population)) {
            // Print fitness
            System.out.println("G" + generation + " Best fitness: " + population.getFittest(0).getFitness());

            // Apply crossover
            population = ga.crossoverPopulation(population);

            // Apply mutation
            population = ga.mutatePopulation(population, timetable);

            // Evaluate population
            ga.evalPopulation(population, timetable);

            // Increment the current generation
            generation++;
        }

        timetable.createClasses(population.getFittest(0));
        System.out.println();
        System.out.println("Solution found in " + generation + " generations");
        System.out.println("Final solution fitness: " + population.getFittest(0).getFitness());
        System.out.println("Clashes: " + timetable.calcClashes()[0]);
        System.out.println("RoomClashes: " + timetable.calcClashes()[1]);
        System.out.println("TeacherClashes: " + timetable.calcClashes()[2]);
        System.out.println("GroupClashes: " + timetable.calcClashes()[3]);

        new ExcelExport(timetable);
    }

    private static Timetable initializeTimetable() {
        Timetable timetable = new Timetable();
        inputReader("teachers", timetable);
        inputReader("lessons", timetable);
        inputReader("groups", timetable);
        inputReader("rooms", timetable);
        inputReader("timeslots", timetable);
        timetable.assortRooms();

        return timetable;
    }

    private static void inputReader(String type, Timetable timetable) {
        try {
            //fontos hogy a res-ben kell tárolni a fileokat
            int num = 0;
            InputStream inputStream = Main.class.getResourceAsStream("/" + type + ".txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String currentLine = reader.readLine();
            while (currentLine != null) {
                String[] line = currentLine.split("\t");
                //megnézi hogy miket olvasunk be
                switch (type) {
                    case "groups":
                        timetable.addGroup(num, line[0], Integer.parseInt(line[1]), timetable.getLessons(line[0]));
                        num++;
                        break;
                    case "teachers":
                        timetable.addTeacher(num, line[0]);
                        num++;
                        break;
                    case "lessons":
                        for (int i = 0; i < Integer.parseInt(line[2]); i++) {
                            timetable.addLesson(num, line[0], line[1], Integer.parseInt(line[2]), timetable.getTeacher(line[3]), RoomType.valueOf(line[4]));
                            num++;
                        }
                        break;
                    case "timeslots":
                        timetable.addTimeslot(num, line[0]);
                        num++;
                        break;
                    case "rooms":
                        timetable.addRoom(num, RoomType.valueOf(line[0]), line[1], Integer.parseInt(line[2]));
                        num++;
                        break;
                    default:

                        break;
                }

                currentLine = reader.readLine();
            }
            reader.close();
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }
}
