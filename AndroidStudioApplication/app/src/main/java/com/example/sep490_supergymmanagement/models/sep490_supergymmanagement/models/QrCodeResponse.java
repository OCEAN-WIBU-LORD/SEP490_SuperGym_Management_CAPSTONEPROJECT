package com.example.sep490_supergymmanagement.models.sep490_supergymmanagement.models;

import java.util.List;

public class QrCodeResponse {
    private List<QrItem> qrList;

    // Getters and setters
    public List<QrItem> getQrList() {
        return qrList;
    }

    public void setQrList(List<QrItem> qrList) {
        this.qrList = qrList;
    }

    public static class QrItem {
        private String qrDataUrl;
        private CourseDetails courseDetails;

        // Getters and setters
        public String getQrDataUrl() {
            return qrDataUrl;
        }

        public void setQrDataUrl(String qrDataUrl) {
            this.qrDataUrl = qrDataUrl;
        }

        public CourseDetails getCourseDetails() {
            return courseDetails;
        }

        public void setCourseDetails(CourseDetails courseDetails) {
            this.courseDetails = courseDetails;
        }

        public static class CourseDetails {
            private String courseName;
            private String courseContent;
            private String courseDuration;
            private int coursePrice;

            // Getters and setters
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

            public String getCourseDuration() {
                return courseDuration;
            }

            public void setCourseDuration(String courseDuration) {
                this.courseDuration = courseDuration;
            }

            public int getCoursePrice() {
                return coursePrice;
            }

            public void setCoursePrice(int coursePrice) {
                this.coursePrice = coursePrice;
            }
        }
    }
}
