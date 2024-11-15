package com.supergym.sep490_supergymmanagement.models;

public class TrainingLession {
    private String trainingLessionId;
    private String name;
    private String description;

    public TrainingLession() {
    }

    public TrainingLession(String trainingLessionId, String description, String name) {
        this.trainingLessionId = trainingLessionId;
        this.description = description;
        this.name = name;
    }

    public String getTrainingLessionId() {
        return trainingLessionId;
    }

    public void setTrainingLessionId(String trainingLessionId) {
        this.trainingLessionId = trainingLessionId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}