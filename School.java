package org.example;

// School.java
public class School {
    private String name;
    private int capacity;
    private int currentCapacity;

    public School(String name, int capacity) {
        this.name = name;
        this.capacity = capacity;
        this.currentCapacity = 0;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setCurrentCapacity(int currentCapacity) {
        this.currentCapacity = currentCapacity;
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getCurrentCapacity() {
        return currentCapacity;
    }

    public void incrementCurrentCapacity() {
        currentCapacity++;
    }

    public boolean hasCapacity() {
        return currentCapacity < capacity;
    }
}