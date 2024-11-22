package com.supergym.sep490_supergymmanagement.models;

import java.util.List;

public class QrCodeBoxingResponse {
    private List<QrItem> qrList;

    // Getter and setter
    public List<QrItem> getQrList() {
        return qrList;
    }

    public void setQrList(List<QrItem> qrList) {
        this.qrList = qrList;
    }

    // BoxingMembershipPlan class
    public class BoxingMembershipPlan {
        private String boxingTrainerId;
        private String description;
        private double totalPrice;
        private int sessions;
        private int memberCount;
        private int months;

        // Getters and setters
        public String getBoxingTrainerId() {
            return boxingTrainerId;
        }

        public void setBoxingTrainerId(String boxingTrainerId) {
            this.boxingTrainerId = boxingTrainerId;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public double getTotalPrice() {
            return totalPrice;
        }

        public void setTotalPrice(double totalPrice) {
            this.totalPrice = totalPrice;
        }

        public int getSessions() {
            return sessions;
        }

        public void setSessions(int sessions) {
            this.sessions = sessions;
        }

        public int getMemberCount() {
            return memberCount;
        }

        public void setMemberCount(int memberCount) {
            this.memberCount = memberCount;
        }

        public int getMonths() {
            return months;
        }

        public void setMonths(int months) {
            this.months = months;
        }
    }

    // Details class
    public class Details {
        private BoxingMembershipPlan boxingMembershipPlan;

        // Getter and setter
        public BoxingMembershipPlan getBoxingMembershipPlan() {
            return boxingMembershipPlan;
        }

        public void setBoxingMembershipPlan(BoxingMembershipPlan boxingMembershipPlan) {
            this.boxingMembershipPlan = boxingMembershipPlan;
        }
    }

    // QrItem class
    public class QrItem {
        private String qrDataUrl;
        private Details details;

        // Getters and setters
        public String getQrDataUrl() {
            return qrDataUrl;
        }

        public void setQrDataUrl(String qrDataUrl) {
            this.qrDataUrl = qrDataUrl;
        }

        public Details getDetails() {
            return details;
        }

        public void setDetails(Details details) {
            this.details = details;
        }
    }
}
