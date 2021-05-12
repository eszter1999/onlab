package com.company.data;

public class Teachers {
    final int id;
    private final String name;        //unique

    public Teachers(int i, String n){
        id = i;
        name = n;
    }

    public String getName(){
        return name;
    }

    public int getId(){return id;}

}
