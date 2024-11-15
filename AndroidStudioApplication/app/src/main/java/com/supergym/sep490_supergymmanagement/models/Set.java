package com.supergym.sep490_supergymmanagement.models;


public class Set {
    private String id;
    private int repetitions;
    private double weight;

    // Default constructor for Firebase deserialization
    public Set() {}

    // Constructor to initialize repetitions and weight
    public Set(int repetitions, double weight) {
        this.repetitions = repetitions;
        this.weight = weight;
    }

    // Full constructor with id
    public Set(String id, int repetitions, double weight) {
        this.id = id;
        this.repetitions = repetitions;
        this.weight = weight;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public int getRepetitions() { return repetitions; }
    public void setRepetitions(int repetitions) { this.repetitions = repetitions; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }
}

