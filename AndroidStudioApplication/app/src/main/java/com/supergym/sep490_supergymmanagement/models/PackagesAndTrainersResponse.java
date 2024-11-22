package com.supergym.sep490_supergymmanagement.models;

import java.util.List;

public class PackagesAndTrainersResponse {
    private List<PackageDetails> packages;
    private List<TrainerDetails> trainers;

    public PackagesAndTrainersResponse() {
    }

    public PackagesAndTrainersResponse(List<PackageDetails> packages, List<TrainerDetails> trainers) {
        this.packages = packages;
        this.trainers = trainers;
    }

    public List<PackageDetails> getPackages() {
        return packages;
    }

    public void setPackages(List<PackageDetails> packages) {
        this.packages = packages;
    }

    public List<TrainerDetails> getTrainers() {
        return trainers;
    }

    public void setTrainers(List<TrainerDetails> trainers) {
        this.trainers = trainers;
    }

    public static class PackageDetails {
        private String packageId;
        private String description;
        private int sessions;
        private int months;
        private int memberCount;
        private double totalPrice;

        public PackageDetails() {
        }

        public PackageDetails(String packageId, String description, int sessions, int months, int memberCount, double totalPrice) {
            this.packageId = packageId;
            this.description = description;
            this.sessions = sessions;
            this.months = months;
            this.memberCount = memberCount;
            this.totalPrice = totalPrice;
        }

        // Getters and Setters

        public String getPackageId() {
            return packageId;
        }

        public void setPackageId(String packageId) {
            this.packageId = packageId;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getSessions() {
            return sessions;
        }

        public void setSessions(int sessions) {
            this.sessions = sessions;
        }

        public int getMonths() {
            return months;
        }

        public void setMonths(int months) {
            this.months = months;
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

    public static class TrainerDetails {
        private String trainerId;
        private String name;
        private String userId;
        private String specialization;

        public TrainerDetails() {
        }

        public TrainerDetails(String trainerId, String name, String userId, String specialization) {
            this.trainerId = trainerId;
            this.name = name;
            this.userId = userId;
            this.specialization = specialization;
        }

        // Getters and Setters

        public String getTrainerId() {
            return trainerId;
        }

        public void setTrainerId(String trainerId) {
            this.trainerId = trainerId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getSpecialization() {
            return specialization;
        }

        public void setSpecialization(String specialization) {
            this.specialization = specialization;
        }
    }
}
