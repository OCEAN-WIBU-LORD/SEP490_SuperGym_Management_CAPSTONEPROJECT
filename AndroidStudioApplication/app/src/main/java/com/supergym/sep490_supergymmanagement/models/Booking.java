package com.supergym.sep490_supergymmanagement.models;

public class Booking {
    private String customerId;    // ID của khách hàng
    private String ptId;          // ID của huấn luyện viên cá nhân (PT)
    private String startDate;     // Ngày bắt đầu lịch tập (dạng chuỗi yyyy-MM-dd)
    private int sessionCount;     // Số buổi tập
    private String timeSlotId;    // ID của khung giờ tập
    private String trainerName;   // Tên của huấn luyện viên
    private String trainerType;   // Loại hình huấn luyện (Gym, Boxing, v.v.)
    private String sessionTime;   // Khung giờ cho buổi tập (dạng chuỗi, ví dụ: "14:00-15:00")
    private String notes;         // Ghi chú thêm từ khách hàng

    // Default constructor (needed for Firebase)
    public Booking() {}

    // Constructor with parameters
    public Booking(String customerId, String ptId, String startDate, int sessionCount, String timeSlotId,
                   String trainerName, String trainerType, String sessionTime, String notes) {
        this.customerId = customerId;
        this.ptId = ptId;
        this.startDate = startDate;
        this.sessionCount = sessionCount;
        this.timeSlotId = timeSlotId;
        this.trainerName = trainerName;
        this.trainerType = trainerType;
        this.sessionTime = sessionTime;
        this.notes = notes;
    }

    // Getter and Setter for customerId
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    // Getter and Setter for ptId
    public String getPtId() {
        return ptId;
    }

    public void setPtId(String ptId) {
        this.ptId = ptId;
    }

    // Getter and Setter for startDate
    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    // Getter and Setter for sessionCount
    public int getSessionCount() {
        return sessionCount;
    }

    public void setSessionCount(int sessionCount) {
        this.sessionCount = sessionCount;
    }

    // Getter and Setter for timeSlotId
    public String getTimeSlotId() {
        return timeSlotId;
    }

    public void setTimeSlotId(String timeSlotId) {
        this.timeSlotId = timeSlotId;
    }

    // Getter and Setter for trainerName
    public String getTrainerName() {
        return trainerName;
    }

    public void setTrainerName(String trainerName) {
        this.trainerName = trainerName;
    }

    // Getter and Setter for trainerType
    public String getTrainerType() {
        return trainerType;
    }

    public void setTrainerType(String trainerType) {
        this.trainerType = trainerType;
    }

    // Getter and Setter for sessionTime
    public String getSessionTime() {
        return sessionTime;
    }

    public void setSessionTime(String sessionTime) {
        this.sessionTime = sessionTime;
    }

    // Getter and Setter for notes
    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
