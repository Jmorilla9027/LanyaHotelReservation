/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.lanyastarhotelreservation.model;

/**
 *
 * @author johnm
 */
public class Addon {
    private String name;
    private double rate;
    private String description;
    private int quantity;
    private boolean selected;
    private int maxQuantity;
    private int discountCount;

    // Constants for addon names
    private static final String BED = "Bed";
    private static final String BLANKET = "Blanket";
    private static final String PILLOWS = "Pillows";
    private static final String TOILETRIES = "Toiletries";

    public Addon(String name, double rate, String description) {
        this.name = name;
        this.rate = rate;
        this.description = description;
        this.quantity = 0;
        this.selected = false;
        this.discountCount = 0;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getRate() { return rate; }
    public void setRate(double rate) { this.rate = rate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }

    public double getTotalPrice() {
        return rate * quantity;
    }

    public void setMaxQuantity(int maxQuantity) {
        this.maxQuantity = maxQuantity;
    }
    
    public int getDiscountCount() {
        return discountCount;
    }

    public void setDiscountCount(int discountCount) {
        this.discountCount = discountCount;
    }
    
    // FIXED VALIDATION METHOD using constants
    public String validateQuantity(int guestCount, int nightsStay) {
        if (quantity < 0) {
            return name + " quantity cannot be negative";
        }
        
        // If quantity is 0, it's valid (not selected)
        if (quantity == 0) {
            return "VALID";
        }
        
        // Bed-specific validation using constant
        if (BED.equals(name)) {
            if (quantity > guestCount) {
                return "Number of beds cannot exceed number of guests (" + guestCount + ")";
            }
        }
        
        // General validation for other addons
        if (!BED.equals(name) && maxQuantity > 0 && quantity > maxQuantity) {
            return name + " quantity cannot exceed " + maxQuantity;
        }
        
        return "VALID";
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


