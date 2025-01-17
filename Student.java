package org.example;

import java.util.Arrays;

// Student.java
public class Student {
    private int id;
    private String[] preferences;
    private double[] scores;

    public Student(int id, String[] preferences, double[] scores) {
        this.id = id;
        this.preferences = preferences;
        this.scores = scores;
    }


    public void setId(int id) {
        this.id = id;
    }
    public void setPreferences(String[] preferences) {
        this.preferences = preferences;
    }
    public void setScores(double[] scores) {
        this.scores = scores;
    }

    public int getId() {
        return id;
    }

    public String[] getPreferences() {
        return preferences;
    }

    public double[] getScores() {
        return scores;
    }

    public double getHighestScore() {
        double highest = Double.NEGATIVE_INFINITY; // Start with the smallest possible value
        for (double score : scores) {
            if (score > highest) {
                highest = score;
            }
        }
        return highest;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", preferences=" + Arrays.toString(preferences) +
                ", scores=" + Arrays.toString(scores) +
                '}';
    }
}
