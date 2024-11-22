package com.supergym.sep490_supergymmanagement.models;

import java.util.List;


// QrCodeResponse class
public class QrCodeResponse {
    private List<QrItem> qrList;

    // Getter and setter
    public List<QrItem> getQrList() { return qrList; }
    public void setQrList(List<QrItem> qrList) { this.qrList = qrList; }


    // GymMembership class
    public class GymMembership {
        private String name;
        private Integer durationMonths;
        private Integer sessionCount;
        private Integer totalPrice;

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public Integer getDurationMonths() { return durationMonths; }
        public void setDurationMonths(Integer durationMonths) { this.durationMonths = durationMonths; }

        public Integer getSessionCount() { return sessionCount; }
        public void setSessionCount(Integer sessionCount) { this.sessionCount = sessionCount; }

        public Integer getTotalPrice() { return totalPrice; }
        public void setTotalPrice(Integer totalPrice) { this.totalPrice = totalPrice; }
    }

    // Details class
    public class Details {
        private GymMembership gymMembership;

        // Getter and setter
        public GymMembership getGymMembership() { return gymMembership; }
        public void setGymMembership(GymMembership gymMembership) { this.gymMembership = gymMembership; }
    }

    // QrItem class
    public class QrItem {
        private String qrDataUrl;
        private Details details;

        // Getters and setters
        public String getQrDataUrl() { return qrDataUrl; }
        public void setQrDataUrl(String qrDataUrl) { this.qrDataUrl = qrDataUrl; }

        public Details getDetails() { return details; }
        public void setDetails(Details details) { this.details = details; }
    }
}
