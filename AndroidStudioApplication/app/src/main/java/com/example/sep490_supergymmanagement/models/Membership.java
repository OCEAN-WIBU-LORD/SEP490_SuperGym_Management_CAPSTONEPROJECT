package com.example.sep490_supergymmanagement.models;

import java.util.Date;

public class Membership {
    private String membershipId;       // ID của membership
    private String userId;             // ID người dùng sở hữu gói tập
    private String courseId;           // ID của gói tập đã mua
    private Date startDate;            // Ngày bắt đầu gói tập
    private Date endDate;              // Ngày kết thúc gói tập
    private String status;             // Trạng thái của gói tập ("active", "expired")

    // Constructor
    public Membership() {}

    public Membership(String membershipId, String userId, String courseId, Date startDate, Date endDate, String status) {
        this.membershipId = membershipId;
        this.userId = userId;
        this.courseId = courseId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    // Getters and Setters
    public String getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(String membershipId) {
        this.membershipId = membershipId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
