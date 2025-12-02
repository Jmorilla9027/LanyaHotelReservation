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
public class Guest {
    private int customerId;
    private String name;
    private String email;
    private String phone;
    private LocalDateTime createdDate;
    
    // Constructors
    public Guest() {}
    
    public Guest(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }
    
    // Getters and setters
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
    
    // Validation method
    public String validate() {
        if (name == null || name.trim().isEmpty()) {
            return "Guest name is required";
        }
        if (name.length() > 100) {
            return "Name cannot exceed 100 characters";
        }
        if (email != null && !email.isEmpty() && !email.contains("@")) {
            return "Invalid email format";
        }
        if (phone != null && !phone.isEmpty() && !phone.matches("^[0-9\\+\\-\\(\\)\\s]{7,15}$")) {
            return "Invalid phone number format";
        }
        return "VALID";
    }
    
    // Helper method to check if guest has email
    public boolean hasEmail() {
        return email != null && !email.trim().isEmpty();
    }
    
    // Helper method to check if guest has phone
    public boolean hasPhone() {
        return phone != null && !phone.trim().isEmpty();
    }
    
    @Override
    public String toString() {
        return String.format("%s (ID: %d)", name, customerId);
    }
}
