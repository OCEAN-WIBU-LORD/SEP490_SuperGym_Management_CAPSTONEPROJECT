package com.supergym.sep490_supergymmanagement.models;

import java.util.ArrayList;
import java.util.List;

public class Session {
    private String id;
    private String month;
    private String day;
    private String name;
    private String muscleGroups;
    private List<Exercise> exercises; // Danh sách bài tập trong buổi tập
    private String startTime; // Thời gian bắt đầu
    private String endTime;   // Thời gian kết thúc
    private String userId;    // Thêm userId để liên kết buổi tập với người dùng

    public Session() {
        this.exercises = new ArrayList<>(); // Khởi tạo danh sách bài tập rỗng
    }

    public Session(String id, String month, String day, String name, String muscleGroups, List<Exercise> exercises, String startTime, String endTime, String userId) {
        this.id = id;
        this.month = month;
        this.day = day;
        this.name = name;
        this.muscleGroups = muscleGroups;
        this.exercises = exercises != null ? exercises : new ArrayList<>();
        this.startTime = startTime;
        this.endTime = endTime;
        this.userId = userId;  // Gán userId
    }

    // Getters và Setters cho userId
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    // Getters và Setters khác
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }

    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getMuscleGroups() { return muscleGroups; }
    public void setMuscleGroups(String muscleGroups) { this.muscleGroups = muscleGroups; }

    public List<Exercise> getExercises() { return exercises; }
    public void setExercises(List<Exercise> exercises) { this.exercises = exercises; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
}

