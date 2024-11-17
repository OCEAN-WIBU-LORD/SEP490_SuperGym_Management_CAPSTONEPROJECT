package com.supergym.sep490_supergymmanagement.models;

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
        private Details details;

        // Getters and Setters
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

        // Nested classes for possible membership details
        public static class Details {
            private GymMembership gymMembership;
            private TrainerRentalPlan trainerRentalPlan;
            private BoxingMembershipPlan boxingMembershipPlan;

            // Getters and Setters for each membership type
            public GymMembership getGymMembership() {
                return gymMembership;
            }

            public void setGymMembership(GymMembership gymMembership) {
                this.gymMembership = gymMembership;
            }

            public TrainerRentalPlan getTrainerRentalPlan() {
                return trainerRentalPlan;
            }

            public void setTrainerRentalPlan(TrainerRentalPlan trainerRentalPlan) {
                this.trainerRentalPlan = trainerRentalPlan;
            }

            public BoxingMembershipPlan getBoxingMembershipPlan() {
                return boxingMembershipPlan;
            }

            public void setBoxingMembershipPlan(BoxingMembershipPlan boxingMembershipPlan) {
                this.boxingMembershipPlan = boxingMembershipPlan;
            }
        }

        public static class GymMembership {
            private String name;
            private int durationMonths;
            private int sessionCount;
            private double totalPrice;

            // Getters and Setters
            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getDurationMonths() {
                return durationMonths;
            }

            public void setDurationMonths(int durationMonths) {
                this.durationMonths = durationMonths;
            }

            public int getSessionCount() {
                return sessionCount;
            }

            public void setSessionCount(int sessionCount) {
                this.sessionCount = sessionCount;
            }

            public double getTotalPrice() {
                return totalPrice;
            }

            public void setTotalPrice(double totalPrice) {
                this.totalPrice = totalPrice;
            }
        }

        public static class TrainerRentalPlan {
            private String trainerId;
            private String description;
            private double pricePerPersonPerSession;
            private double pricePerPersonPerMonth;
            private int memberCount;
            private double totalPrice;

            // Getters and Setters
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

            public int getMemberCount() {
                return memberCount;
            }

            public void setMemberCount(int memberCount) {
                this.memberCount = memberCount;
            }

            public double getTotalPrice() {
                return totalPrice;
            }

            public void setTotalPrice(double totalPrice) {
                this.totalPrice = totalPrice;
            }
        }

        public static class BoxingMembershipPlan {
            private String boxingTrainerId;
            private String description;
            private double totalPrice;
            private int sessions;
            private int memberCount;
            private int months;

            // Getters and Setters
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
    }
}