package com.supergym.sep490_supergymmanagement.models;

public class Transaction {
    private String packageName;    // Tên gói tập
    private double amount;         // Số tiền thanh toán
    private String paymentStatus;  // Trạng thái thanh toán
    private String startDate;      // Ngày bắt đầu
    private String endDate;        // Ngày kết thúc

    public Transaction() {}

    public Transaction(String packageName, double amount, String paymentStatus, String startDate, String endDate) {
        this.packageName = packageName;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters and Setters
    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
}
