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
    private int maxQuantity; // Maximum allowed based on guest count
    private int maxDays; // Maximum days based on stay duration


    public Addon(String name, double rate, String description) {
        this.name = name;
        this.rate = rate;
        this.description = description;
        this.quantity = 0;
        this.selected = false;
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

    // VALIDATION METHODS
public String validateQuantity(int guestCount, int nightsStay) {
    if (quantity < 0) {
        return name + " quantity cannot be negative";
    }
    
    // If quantity is 0, it's valid (not selected or no quantity needed)
    if (quantity == 0) {
        return "VALID";
    }
    
    if (maxQuantity > 0 && quantity > maxQuantity) {
        return name + " quantity cannot exceed " + maxQuantity + " (based on guest count)";
    }
    
    // Bed-specific validation
    if ("Bed".equals(name)) {
        if (quantity > guestCount) {
            return "Number of beds cannot exceed number of guests (" + guestCount + ")";
        }
    }
    
    // General validation for other addons
    if (!"Bed".equals(name) && quantity > guestCount * 2) {
        return name + " quantity seems excessive for " + guestCount + " guests";
    }
    
    return "VALID";
}
}

