/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.lanyastarhotelreservation.model;

/**
 *
 * @author johnm
 */

public class Services {
    private String name;
    private double rate;
    private String description;
    private String duration;
    private int quantity;
    private boolean selected;
    private int maxQuantity;
    private int maxDays;
    private int discountCount;

    // Constants for service names
    private static final String SWIMMING_POOL = "Swimming Pool";
    private static final String GYM = "Gym";
    private static final String BED = "Bed"; // For consistency if needed elsewhere

    public Services(String name, double rate, String description, String duration) {
        this.name = name;
        this.rate = rate;
        this.description = description;
        this.duration = duration;
        this.quantity = 0;
        this.selected = false;
        this.maxQuantity = 0;
        this.maxDays = 0;
        this.discountCount = 0;    
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getRate() { return rate; }
    public void setRate(double rate) { this.rate = rate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }

    public int getMaxQuantity() { return maxQuantity; }
    public void setMaxQuantity(int maxQuantity) { this.maxQuantity = maxQuantity; }

    public int getMaxDays() { return maxDays; }
    public void setMaxDays(int maxDays) { this.maxDays = maxDays; }

    public double getTotalPrice() {
        return rate * quantity;
    }
    
    public int getDiscountCount() {
        return discountCount;
    }

    public void setDiscountCount(int discountCount) {
        this.discountCount = discountCount;
    }

    // FIXED VALIDATION METHOD using constants
    public String validateQuantity(int guestCount, int daysAvailed, int nightsStay) {
        if (quantity < 0) {
            return name + " quantity cannot be negative";
        }
        
        // If quantity is 0, it's valid (not selected)
        if (quantity == 0) {
            return "VALID";
        }
        
        // General quantity validation
        if (maxQuantity > 0 && quantity > maxQuantity) {
            return name + " quantity cannot exceed " + maxQuantity;
        }
        
        // Daily services validation using constants
        if (requiresDaysInput()) {
            if (daysAvailed < 0) {
                return name + " days cannot be negative";
            }
            if (daysAvailed > nightsStay) {
                return name + " days (" + daysAvailed + ") cannot exceed stay duration (" + nightsStay + " nights)";
            }
            if (quantity > guestCount) {
                return name + " quantity cannot exceed number of guests (" + guestCount + ")";
            }
        } else {
            // Spa services validation
            if (quantity > guestCount) {
                return name + " quantity cannot exceed number of guests (" + guestCount + ")";
            }
        }
        
        return "VALID";
    }

    public void calculateMaxQuantity(int guestCount, int nightsStay) {
        this.maxQuantity = guestCount;
        
        // Set max days for daily services using constants
        if (SWIMMING_POOL.equals(name) || GYM.equals(name)) {
            this.maxDays = nightsStay;
        }
    }

    public boolean requiresDaysInput() {
        // Using constants instead of string literals
        return SWIMMING_POOL.equals(name) || GYM.equals(name);
    }

    // NEW DISCOUNT VALIDATION METHOD
    public String validateDiscount(int numAdults) {
        // If discount count is 0 or empty (blank), it's valid
        if (discountCount == 0) {
            return "VALID";
        }
        
        // Check if negative
        if (discountCount < 0) {
            return name + " discount count cannot be negative";
        }
        
        // Check if exceeds number of adults
        if (discountCount > numAdults) {
            return name + " discount count cannot exceed number of adults (" + numAdults + ")";
        }
        
        // Check if discount count exceeds quantity
        if (discountCount > quantity) {
            return name + " discount count cannot exceed quantity (" + quantity + ")";
        }
        
        return "VALID";
    }

    // Helper method to parse discount from text field
    public void setDiscountCountFromTextField(String discountText) {
        if (discountText == null || discountText.trim().isEmpty()) {
            this.discountCount = 0;
        } else {
            try {
                this.discountCount = Integer.parseInt(discountText.trim());
            } catch (NumberFormatException e) {
                this.discountCount = 0; // Default to 0 if invalid
            }
        }
    }
}