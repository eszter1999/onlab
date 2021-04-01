package com.company.data;

public class Rooms {
    private final int id;
    private final RoomType type;          //some lessons only can be held in a specific type of room
    private final String name;        //unique, refers to location in the school
    private final int capacity;       //capacity

    public Rooms(int id, RoomType t, String n, int s) {
        this.id = id;
        type = t;
        name = n;
        capacity = s;
    }
    public String getName(){
        return name;
    }
    public String getType(){
        return type.toString();
    }
    public int getCapacity() { return capacity; }
    public int getId() { return id; }
}
