/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.lanyastarhotelreservation.model;
import java.time.LocalDateTime;
/**
 *
 * @author johnm
 */
public class Payment {
    private int paymentId;
    private int bookingId;
    private double totalAmount;
    private String paymentMethod;
    private String paymentStatus;
    private String cardLastFour;
    private LocalDateTime transactionDate;
    
    // Processing fields (not stored in DB)
    private String cardNumber;
    private String ccv;
    private double cashReceived;
    
    // Constructors
    public Payment() {
        this.transactionDate = LocalDateTime.now();
        this.paymentStatus = "PENDING";
    }
    
    public Payment(double totalAmount, double cashReceived, int bookingId) {
        this();
        this.bookingId = bookingId;
        this.totalAmount = totalAmount;
        this.cashReceived = cashReceived;
        this.paymentMethod = "CASH";
    }
    
    public Payment(double totalAmount, String cardNumber, String ccv, int bookingId) {
        this();
        this.bookingId = bookingId;
        this.totalAmount = totalAmount;
        this.cardNumber = cardNumber;
        this.ccv = ccv;
        this.paymentMethod = "CARD";
        
        // Store last 4 digits of card
        if (cardNumber != null && cardNumber.length() >= 4) {
            String cleanCard = cardNumber.replaceAll("\\s+", "");
            this.cardLastFour = cleanCard.substring(cleanCard.length() - 4);
        }
    }
    
    // Validation method
    public String validate() {
        if (bookingId <= 0) {
            return "Booking ID is required";
        }
        
        if (totalAmount <= 0) {
            return "Amount must be greater than 0";
        }
        
        if ("CARD".equals(paymentMethod)) {
            return validateCard();
        } else if ("CASH".equals(paymentMethod)) {
            return validateCash();
        }
        
        return "Invalid payment method";
    }
    
    private String validateCard() {
        if (cardNumber == null || cardNumber.trim().isEmpty()) {
            return "Card number is required";
        }
        
        String cleanCard = cardNumber.replaceAll("\\s+", "");
        if (cleanCard.length() != 16) {
            return "Card number must be 16 digits";
        }
        
        if (!cleanCard.matches("\\d+")) {
            return "Card number must contain only numbers";
        }
        
        if (ccv == null || ccv.trim().isEmpty()) {
            return "CCV is required";
        }
        
        if (!ccv.matches("\\d{3,4}")) {
            return "CCV must be 3 or 4 digits";
        }
        
        return "VALID";
    }
    
    private String validateCash() {
        if (cashReceived <= 0) {
            return "Cash received must be greater than 0";
        }
        
        if (cashReceived < totalAmount) {
            return String.format("Insufficient cash. Need: P %,.2f more", totalAmount - cashReceived);
        }
        
        return "VALID";
    }
    
    public String processPayment() {
        String validation = validate();
        if (!"VALID".equals(validation)) {
            return "VALIDATION_FAILED: " + validation;
        }
        
        if ("CARD".equals(paymentMethod)) {
            // Simulate card processing
            try {
                Thread.sleep(1000);
                
                String cleanCard = cardNumber.replaceAll("\\s+", "");
                char lastDigit = cleanCard.charAt(cleanCard.length() - 1);
                
                if (Character.getNumericValue(lastDigit) % 2 == 0) {
                    this.paymentStatus = "COMPLETED";
                    return "CARD_PAYMENT_SUCCESS";
                } else {
                    this.paymentStatus = "DECLINED";
                    return "CARD_PAYMENT_DECLINED";
                }
            } catch (InterruptedException e) {
                this.paymentStatus = "FAILED";
                return "CARD_PAYMENT_INTERRUPTED";
            }
        } else {
            // Cash payment always succeeds if validated
            this.paymentStatus = "COMPLETED";
            return "CASH_PAYMENT_SUCCESS";
        }
    }
    
    public double calculateChange() {
        if ("CASH".equals(paymentMethod) && cashReceived >= totalAmount) {
            return cashReceived - totalAmount;
        }
        return 0;
    }
    
    public String getMaskedCardNumber() {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "";
        }
        String cleanCard = cardNumber.replaceAll("\\s+", "");
        return "**** **** **** " + cleanCard.substring(cleanCard.length() - 4);
    }
    
    // Getters and Setters
    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }
    
    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }
    
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    
    public String getCardLastFour() { return cardLastFour; }
    public void setCardLastFour(String cardLastFour) { this.cardLastFour = cardLastFour; }
    
    public LocalDateTime getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }
    
    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { 
        this.cardNumber = cardNumber;
        if (cardNumber != null && cardNumber.length() >= 4) {
            String cleanCard = cardNumber.replaceAll("\\s+", "");
            this.cardLastFour = cleanCard.substring(cleanCard.length() - 4);
        }
    }
    
    public String getCcv() { return ccv; }
    public void setCcv(String ccv) { this.ccv = ccv; }
    
    public double getCashReceived() { return cashReceived; }
    public void setCashReceived(double cashReceived) { this.cashReceived = cashReceived; }
}
