package com.supergym.sep490_supergymmanagement.models;

import java.util.List;


// QrCodeResponse class
// QrCodeResponse class
public class QrCodeRentalResponse {
    private List<QrItem> qrList;

    // Getter and setter
    public List<QrItem> getQrList() {
        return qrList;
    }

    public void setQrList(List<QrItem> qrList) {
        this.qrList = qrList;
    }

    // TrainerRentalPlan class
    public class TrainerRentalPlan {
        private String trainerId;
        private String description;
        private double pricePerPersonPerSession;
        private double pricePerPersonPerMonth;
        private Integer memberCount;
        private double totalPrice;

        // Getters and setters
        public String getTrainerId() {
            return trainerId;
        }

        public void setTrainerId(String trainerId) {
            this.trainerId = trainerId;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public double getPricePerPersonPerSession() {
            return pricePerPersonPerSession;
        }

        public void setPricePerPersonPerSession(double pricePerPersonPerSession) {
            this.pricePerPersonPerSession = pricePerPersonPerSession;
        }

        public double getPricePerPersonPerMonth() {
            return pricePerPersonPerMonth;
        }

        public void setPricePerPersonPerMonth(double pricePerPersonPerMonth) {
            this.pricePerPersonPerMonth = pricePerPersonPerMonth;
        }

        public Integer getMemberCount() {
            return memberCount;
        }

        public void setMemberCount(Integer memberCount) {
            this.memberCount = memberCount;
        }

        public double getTotalPrice() {
            return totalPrice;
        }

        public void setTotalPrice(double totalPrice) {
            this.totalPrice = totalPrice;
        }
    }

    // Details class
    public class Details {
        private TrainerRentalPlan trainerRentalPlan;

        // Getter and setter
        public TrainerRentalPlan getTrainerRentalPlan() {
            return trainerRentalPlan;
        }

        public void setTrainerRentalPlan(TrainerRentalPlan trainerRentalPlan) {
            this.trainerRentalPlan = trainerRentalPlan;
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
