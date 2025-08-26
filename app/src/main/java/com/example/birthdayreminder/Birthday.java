package com.example.birthdayreminder;

public class Birthday {
    private int id;
    private String name;
    private int month;
    private int day;

    public Birthday(int id, String name, int month, int day) {
        this.id = id;
        this.name = name;
        this.month = month;
        this.day = day;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    @Override
    public String toString() {
        return name + " - " + String.format("%02d/%02d", month, day);
    }
}