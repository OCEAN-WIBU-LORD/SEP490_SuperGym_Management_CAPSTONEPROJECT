package com.supergym.sep490_supergymmanagement.models;

public class Transaction {
    private String packageName;  // Tên gói tập
    private double amount;       // Số tiền thanh toán
    private String paymentStatus; // Trạng thái thanh toán

    public Transaction() {}

    public Transaction(String packageName, double amount, String paymentStatus) {
        this.packageName = packageName;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
    }

    // Getters and Setters
    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
}
