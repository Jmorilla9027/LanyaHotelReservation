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
    private int addonId;
    private String name;
    private double rate;
    private String description;
    private String unit; 
    private int quantity;
    private boolean selected;
    private int discountCount;

    public Addon() {} // Add empty constructor

    public Addon(String name, double rate, String description) {
        this.name = name;
        this.rate = rate;
        this.description = description;
        this.quantity = 0;
        this.selected = false;
        this.discountCount = 0;
    }

    public Addon(int addonId, String name, double rate, String description, String unit) {
        this.addonId = addonId;
        this.name = name;
        this.rate = rate;
        this.description = description;
        this.unit = unit;
        this.quantity = 0;
        this.selected = false;
        this.discountCount = 0;
    }

    public String validateQuantity(int guestCount, int nightsStay) {
        if (quantity < 0) {
            return name + " quantity cannot be negative";
        }
        if (quantity == 0) {
            return "VALID";
        }
        if ("Bed".equals(name) && quantity > guestCount) {
            return "Number of beds cannot exceed number of guests (" + guestCount + ")";
        }
        return "VALID";
    }

    public String validateDiscount(int numAdults) {
        if (discountCount == 0) {
            return "VALID";
        }
        if (discountCount < 0) {
            return name + " discount count cannot be negative";
        }
        if (discountCount > numAdults) {
            return name + " discount count cannot exceed number of adults (" + numAdults + ")";
        }
        if (discountCount > quantity) {
            return name + " discount count cannot exceed quantity (" + quantity + ")";
        }
        return "VALID";
    }

    public void setDiscountCountFromTextField(String discountText) {
        if (discountText == null || discountText.trim().isEmpty()) {
            this.discountCount = 0;
        } else {
            try {
                this.discountCount = Integer.parseInt(discountText.trim());
            } catch (NumberFormatException e) {
                this.discountCount = 0;
            }
        }
    }

    // Getters and Setters
    public int getAddonId() { return addonId; }
    public void setAddonId(int addonId) { this.addonId = addonId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public double getRate() { return rate; }
    public void setRate(double rate) { this.rate = rate; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }
    
    public int getDiscountCount() { return discountCount; }
    public void setDiscountCount(int discountCount) { this.discountCount = discountCount; }
}


