package com.company.data;

public class Timeslots {
    private final int id;
    private final String time;

    public Timeslots(int timeslotId, String time) {
        this.id = timeslotId;
        this.time = time;
    }

    public int getId(){
        return this.id;
    }

    public String getTime(){
        return this.time;
    }
}
