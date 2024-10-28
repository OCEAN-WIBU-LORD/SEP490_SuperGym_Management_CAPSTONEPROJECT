package com.example.sep490_supergymmanagement.models;

public class Course {
    private String courseId;
    private String courseName;
    private String courseContent;
    private String courseDuration;
    private int coursePrice;

    // Default constructor
    public Course() {}

    // Constructor
    public Course(String courseId, String courseName, String courseContent, String courseDuration, int coursePrice) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.courseContent = courseContent;
        this.courseDuration = courseDuration;
        this.coursePrice = coursePrice;
    }

    // Getters
    public String getCourseId() { return courseId; }
    public String getCourseName() { return courseName; }
    public String getCourseContent() { return courseContent; }
    public String getCourseDuration() { return courseDuration; }
    public int getCoursePrice() { return coursePrice; }
}
