package com.supergym.sep490_supergymmanagement.models;

public class Course {
    private String courseId;           // ID của gói tập
    private String courseName;         // Tên gói tập
    private String courseContent;      // Nội dung của gói tập
    private double coursePrice;        // Giá của gói tập
    private int courseDuration;        // Thời gian hiệu lực (tính theo ngày)

    // Constructor
    public Course() {}

    public Course(String courseId, String courseName, String courseContent, double coursePrice, int courseDuration) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.courseContent = courseContent;
        this.coursePrice = coursePrice;
        this.courseDuration = courseDuration;
    }

    // Getters and Setters
    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseContent() {
        return courseContent;
    }

    public void setCourseContent(String courseContent) {
        this.courseContent = courseContent;
    }

    public double getCoursePrice() {
        return coursePrice;
    }

    public void setCoursePrice(double coursePrice) {
        this.coursePrice = coursePrice;
    }

    public int getCourseDuration() {
        return courseDuration;
    }

    public void setCourseDuration(int courseDuration) {
        this.courseDuration = courseDuration;
    }
}
