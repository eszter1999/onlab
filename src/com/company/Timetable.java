package com.company;

import com.company.data.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Timetable {

    private final HashMap<Integer, Groups> groups;
    private final HashMap<Integer, Teachers> teachers;
    private final HashMap<Integer, Lessons> lessons;
    private final HashMap<Integer, Timeslots> timeslots;
    private final HashMap<Integer, Rooms> rooms;

    private ArrayList<Integer> pe;
    private ArrayList<Integer> it;
    private ArrayList<Integer> normal;
    private ArrayList<Integer> bio;
    private ArrayList<Integer> phy;
    private ArrayList<Integer> chem;

    private Class classes[];
    private int numClasses = 0;

    //constructor
    public Timetable() {
        this.rooms = new HashMap<>();
        this.groups = new HashMap<>();
        this.teachers = new HashMap<>();
        this.lessons = new HashMap<>();
        this.timeslots = new HashMap<>();
        this.pe = new ArrayList<>();
        this.it = new ArrayList<>();
        this.normal = new ArrayList<>();
        this.bio = new ArrayList<>();
        this.phy = new ArrayList<>();
        this.chem = new ArrayList<>();
    }
    public Timetable(Timetable cloneable) {
        this.rooms = cloneable.getRooms();
        this.teachers = cloneable.getTeachers();
        this.lessons = cloneable.getLessons();
        this.groups = cloneable.getGroups();
        this.timeslots = cloneable.getTimeslots();
        this.pe = cloneable.getRooms(RoomType.PE);
        this.it = cloneable.getRooms(RoomType.IT);
        this.normal = cloneable.getRooms(RoomType.NORMAL);
        this.bio = cloneable.getRooms(RoomType.BIO);
        this.phy = cloneable.getRooms(RoomType.PHY);
        this.chem = cloneable.getRooms(RoomType.CHEM);
    }
    //innitialize room types
    public void assortRooms(){
        this.pe = getRooms(RoomType.PE);
        this.it = getRooms(RoomType.IT);
        this.normal = getRooms(RoomType.NORMAL);
        this.bio = getRooms(RoomType.BIO);
        this.phy = getRooms(RoomType.PHY);
        this.chem = getRooms(RoomType.CHEM);
    }

    public int[] calcClashes() {
        int clashes = 0;
        int teacher = 0;
        int group = 0;
        int room = 0;

        for (Class classA : this.classes) {
            // Check room capacity
            int roomCapacity = this.getRoom(classA.getRoomId()).getCapacity();
            int groupSize = this.getGroup(classA.getGroupId()).getSize();

            if (roomCapacity < groupSize) {
                clashes++;
                room++;
            }

            // Check if room is taken
            for (Class classB : this.classes) {
                if (classA.getRoomId() == classB.getRoomId()
                        && classA.getTimeslotId() == classB.getTimeslotId()
                        && classA.getId() != classB.getId()) {
                    clashes++;
                    room++;
                    break;
                }
            }

            // Check if professor is available
            for (Class classB : this.classes) {
                if (classA.getTeacherId() == classB.getTeacherId()
                        && classA.getTimeslotId() == classB.getTimeslotId()
                        && classA.getId() != classB.getId()) {
                    clashes++;
                    teacher++;
                    break;
                }
            }

            // Check if group is available
            for (Class classB : this.classes) {
                if (classA.getGroupId() == classB.getGroupId()
                        && classA.getTimeslotId() == classB.getTimeslotId()
                        && classA.getId() != classB.getId()) {
                    clashes++;
                    group++;
                    break;
                }
            }
        }

        int[] cl = new int[4];
        cl[0] = clashes;
        cl[1] = room ;
        cl[2] = teacher;
        cl[3] = group;
        return cl;
    }

    public Groups getGroupClash(){
        for (Class classA : this.classes) {
            // Check if group is available
            for (Class classB : this.classes) {
                if (classA.getGroupId() == classB.getGroupId()
                        && classA.getTimeslotId() == classB.getTimeslotId()
                        && classA.getId() != classB.getId()) {
                        return groups.get(classA.getGroupId());
                }
            }
        }
        return null;
    }

    public void createClasses(Individual individual) {
        // Init classes
        Class[] classes = new Class[this.getNumClasses()];

        int[] chromosome = individual.getChromosome();
        int chromosomePos = 0;
        int classIndex = 0;

        for (Groups group : this.getGroupsAsArray()) {
            int[] lessonsIds = group.getLessonsIds();
            for (int lessonId : lessonsIds) {
                classes[classIndex] =
                        new Class(classIndex, group.getId(), lessonId, lessons.get(lessonId).getTeacher());

                // Add timeslot
                classes[classIndex].addTimeslot(chromosome[chromosomePos]);
                chromosomePos++;

                // Add room
                classes[classIndex].setRoomId(chromosome[chromosomePos]);
                chromosomePos++;

                classIndex++;
            }
        }

        this.classes = classes;
    }

    //randomizer
    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
    public Rooms getRandomRoom(String type) {
        int low = 0;
        int high;
        switch (type) {
            case "IT":
                high = this.it.size()-1;
                return getRoom(this.it.get(getRandomNumber(low, high)));
            case "NORMAL":
                high = this.normal.size()-1;
                return getRoom(this.normal.get(getRandomNumber(low, high)));
            case "PHY":
                high = this.phy.size()-1;
                return getRoom(this.phy.get(getRandomNumber(low, high)));
            case "BIO":
                high = this.bio.size()-1;
                return getRoom(this.bio.get(getRandomNumber(low, high)));
            case "PE":
                high = this.pe.size()-1;
                return getRoom(this.pe.get(getRandomNumber(low, high)));
            case "CHEM":
                high = this.chem.size()-1;
                return getRoom(this.chem.get(getRandomNumber(low, high)));
            default:
                return null;
        }
    }
    public Timeslots getRandomTimeslot() {
        int low = 0;
        int high = timeslots.size()-1;
        return timeslots.get(getRandomNumber(low,high));
    }


    //specific getters
    public int getNumClasses() {
        if (this.numClasses > 0) {
            return this.numClasses;
        }

        int numClasses = 0;
        Groups groups[] = this.groups.values().toArray(new Groups[this.groups.size()]);
        for (Groups group : groups) {
            numClasses += group.getLessonsIds().length;
        }
        this.numClasses = numClasses;

        return this.numClasses;
    }
    public ArrayList<Integer> getRooms(RoomType room) {
        ArrayList<Integer> r = new ArrayList<>();
        for (Map.Entry<Integer, Rooms> entry : rooms.entrySet()) {
            Rooms value = entry.getValue();
            if (value.getType().equals(room.toString())) {
                r.add(value.getId());
            }
        }
        return r;
    }
    public Groups[] getGroupsAsArray() {
        return this.groups.values().toArray(new Groups[0]);
    }
    public int[] getLessons(String name){
        ArrayList<Integer> lesson = new ArrayList<>();
        for (Map.Entry<Integer, Lessons> entry : lessons.entrySet()) {
            Lessons value = entry.getValue();
            if (value.getGroup().equals(name)) {
                lesson.add(value.getId());
            }
        }
        int[] l = new int[lesson.size()];
        for(int i = 0; i < lesson.size(); i++)
            l[i] = lesson.get(i);
        return l;
    }
    public Lessons getLesson(int lessonid){
        for (Map.Entry<Integer, Lessons> entry : lessons.entrySet()) {
            Lessons value = entry.getValue();
            if(value.getId() == lessonid)
                return value ; //value.getType()
        }
        return null;
    }
    public int getTeacher(String name){
        for (Map.Entry<Integer, Teachers> entry : teachers.entrySet()) {
            Teachers value = entry.getValue();
            if (value.getName().equals(name)) {
                return entry.getKey();
            }
        }
        return 0;
    }
    public Rooms getRoom(int id){
        for (Map.Entry<Integer, Rooms> entry : rooms.entrySet()) {
            Rooms value = entry.getValue();
            if (value.getId() == id){
                return value;
            }
        }
        return null;
    }
    public Groups getGroup(int groupId) {
        return this.groups.get(groupId);
    }

    //gettes for variables
    private HashMap<Integer, Lessons> getLessons() {
        return this.lessons;
    }
    private HashMap<Integer, Teachers> getTeachers() {
        return this.teachers;
    }
    private HashMap<Integer, Groups> getGroups() {
        return this.groups;
    }
    private HashMap<Integer, Rooms> getRooms() {
        return this.rooms;
    }
    private HashMap<Integer, Timeslots> getTimeslots() {
        return this.timeslots;
    }

    //adders for the input
    public void addRoom(int roomId, RoomType type, String roomName, int capacity) {
        this.rooms.put(roomId, new Rooms(roomId, type, roomName, capacity));
    }
    public void addTeacher(int teacherId, String teacherName, int working_hour) {
        this.teachers.put(teacherId, new Teachers(teacherId, teacherName, working_hour));
    }
    public void addLesson(int lessonId, String group, String lessonName, int LessonNPW, int teacher, RoomType type){
        this.lessons.put(lessonId, new Lessons(lessonId, group, lessonName, LessonNPW, teacher, type));
    }
    public void addGroup(int classID, String className, int classCapacity, int[] lessonsIds){
        this.groups.put(classID, new Groups(classID, className, classCapacity, lessonsIds));
        //this.numClasses = 0;
    }
    public void addTimeslot(int timeslotId, String timeslot) {
        this.timeslots.put(timeslotId, new Timeslots(timeslotId, timeslot));
    }

}
