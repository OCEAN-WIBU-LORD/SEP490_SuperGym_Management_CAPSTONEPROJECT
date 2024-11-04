package com.example.sep490_supergymmanagement.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Transaction {
    private String transactionId;       // ID của giao dịch
    private String userId;              // ID người dùng thực hiện giao dịch
    private String courseId;            // ID của gói tập đã thanh toán
    private double amount;              // Số tiền thanh toán
    private String description;         // Mô tả giao dịch
    private String transactionDate;     // Ngày giao dịch (được lưu dưới dạng String)
    private String paymentMethod;       // Phương thức thanh toán
    private String status;              // Trạng thái giao dịch

    // Constructor
    public Transaction() {}

    public Transaction(String transactionId, String userId, String courseId, double amount, String description,
                       String transactionDate, String paymentMethod, String status) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.courseId = courseId;
        this.amount = amount;
        this.description = description;
        this.transactionDate = transactionDate;
        this.paymentMethod = paymentMethod;
        this.status = status;
    }

    // Getters and Setters
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTransactionDate() { return transactionDate; }
    public void setTransactionDate(String transactionDate) { this.transactionDate = transactionDate; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // Method to parse transactionDate String to Date object
    public Date getFormattedTransactionDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        try {
            return dateFormat.parse(transactionDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
