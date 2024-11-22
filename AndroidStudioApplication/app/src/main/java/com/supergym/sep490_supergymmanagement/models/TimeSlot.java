package com.supergym.sep490_supergymmanagement.models;


public class TimeSlot {
    private String timeSlotId; // ID của TimeSlot
    private String time;       // Chuỗi thời gian, ví dụ: "06:00 - 07:00"

    // Constructor mặc định
    public TimeSlot() {
    }

    // Constructor đầy đủ
    public TimeSlot(String timeSlotId, String time) {
        this.timeSlotId = timeSlotId;
        this.time = time;
    }

    // Getter và Setter
    public String getTimeSlotId() {
        return timeSlotId;
    }

    public void setTimeSlotId(String timeSlotId) {
        this.timeSlotId = timeSlotId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    // Override toString (tùy chọn, dùng cho debug)
    @Override
    public String toString() {
        return "TimeSlot{" +
                "timeSlotId='" + timeSlotId + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}

