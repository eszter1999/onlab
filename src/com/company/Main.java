package com.company;

import com.company.data.RoomType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Main {
    static final int
            population_size = 101,
            elitism_count = 2,
            tournament_size = 5,
            max_generation = 1200;
    static final double
            mutation_rate = 0.002,
            crossover_rate = 0.95;

    public static void main(String[] args) {
	    Timetable timetable = initializeTimetable();
        GeneticAlgorithm ga = new GeneticAlgorithm(population_size, mutation_rate, crossover_rate, elitism_count, tournament_size);
        Population population = ga.initPopulation(timetable);

        // Keep track of current generation
        int generation = 1;
        while (!ga.isTerminationConditionMet(generation, max_generation)
                && !ga.isTerminationConditionMet(population)) {

        }
    }

    private static Timetable initializeTimetable() {
        Timetable timetable = new Timetable();
        inputReader("teachers", timetable);
        inputReader("lessons", timetable);
        inputReader("groups", timetable);
        inputReader("rooms", timetable);
        inputReader("timeslot", timetable);
        timetable.assortRooms();

        return timetable;
    }

    private static void inputReader(String type, Timetable timetable) {
        try {
            //fontos hogy a res-ben kell tárolni a fileokat
            int num = 0;
            InputStream in = Main.class.getResourceAsStream("/" + type + ".txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
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
                        timetable.addTeacher(num, line[0], Integer.parseInt(line[1]));
                        num++;
                        break;
                    case "lessons":
                        for (int i = 0; i < Integer.parseInt(line[2]); i++) {
                            timetable.addLesson(num, line[0], line[1], Integer.parseInt(line[2]), timetable.getTeacher(line[3]), RoomType.valueOf(line[4]));
                            num++;
                        }
                        break;
                    case "timeslot":
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
