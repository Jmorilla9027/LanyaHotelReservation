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

    public Services(String name, double rate, String description, String duration) {
        this.name = name;
        this.rate = rate;
        this.description = description;
        this.duration = duration;
        this.quantity = 0;
        this.selected = false;
        this.maxQuantity = 0;
        this.maxDays = 0;
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

    // VALIDATION METHODS
    public String validateQuantity(int guestCount, int daysAvailed, int nightsStay) {
        if (quantity < 0) {
            return name + " quantity cannot be negative";
        }
        
        // If quantity is 0, it's valid (not selected or no quantity needed)
        if (quantity == 0) {
            return "VALID";
        }
        
        if (maxQuantity > 0 && quantity > maxQuantity) {
            return name + " quantity cannot exceed " + maxQuantity + " guests";
        }
        
        // Daily services validation (Pool, Gym)
        if ("Swimming Pool".equals(name) || "Gym".equals(name)) {
            if (daysAvailed < 0) {
                return "Days for " + name + " cannot be negative";
            }
            if (daysAvailed > 0 && daysAvailed > nightsStay) {
                return name + " days (" + daysAvailed + ") cannot exceed stay duration (" + nightsStay + " nights)";
            }
            if (quantity > guestCount) {
                return name + " quantity cannot exceed number of guests (" + guestCount + ")";
            }
        }
        
        // Spa services validation
        if ("Foot Spa".equals(name) || "Aroma Facial Massage".equals(name) || "Thai Massage".equals(name)) {
            if (quantity > guestCount) {
                return name + " quantity cannot exceed number of guests (" + guestCount + ")";
            }
        }
        
        return "VALID";
    }

    public void calculateMaxQuantity(int guestCount, int nightsStay) {
        this.maxQuantity = guestCount;
        
        // Set max days for daily services
        if ("Swimming Pool".equals(name) || "Gym".equals(name)) {
            this.maxDays = nightsStay;
        }
    }

    public boolean requiresDaysInput() {
        return "Swimming Pool".equals(name) || "Gym".equals(name);
    }
}
