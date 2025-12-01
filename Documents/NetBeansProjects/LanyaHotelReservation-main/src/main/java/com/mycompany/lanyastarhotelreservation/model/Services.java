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
        private int serviceId;
        private String name;
        private double rate;
        private String description;
        private String duration;
        private String pricingUnit;
        private boolean isDailyService;
        private int quantity;
        private boolean selected;
        private int maxQuantity; // Add this back if needed
        private int discountCount;

        // Keep both constructors
        public Services() {
            // Empty constructor for database loading
        }

        public Services(String name, double rate, String description, String duration) {
            this.name = name;
            this.rate = rate;
            this.description = description;
            this.duration = duration;
            this.quantity = 0;
            this.selected = false;
            this.maxQuantity = 0;
            this.discountCount = 0;
        }

        public Services(int serviceId, String name, double rate, String description, 
                       String duration, String pricingUnit, boolean isDailyService) {
            this.serviceId = serviceId;
            this.name = name;
            this.rate = rate;
            this.description = description;
            this.duration = duration;
            this.pricingUnit = pricingUnit;
            this.isDailyService = isDailyService;
            this.quantity = 0;
            this.selected = false;
            this.maxQuantity = 0;
            this.discountCount = 0;
        }

    public String validateQuantity(int guestCount, int daysAvailed, int nightsStay) {
        if (quantity < 0) {
            return name + " quantity cannot be negative";
        }
        if (quantity == 0) {
            return "VALID";
        }
        
        // Max quantity validation
        if (maxQuantity > 0 && quantity > maxQuantity) {
            return name + " quantity cannot exceed maximum allowed (" + maxQuantity + ")";
        }
        
        // Guest count validation
        if (quantity > guestCount) {
            return name + " quantity cannot exceed number of guests (" + guestCount + ")";
        }
        
        // Days validation for daily services
        if (requiresDaysInput() && daysAvailed > nightsStay) {
            return name + " days cannot exceed stay duration (" + nightsStay + " nights)";
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
    
    public boolean requiresDaysInput() {
           return isDailyService;
       }

    // Getters and Setters
    public int getServiceId() { return serviceId; }
    public void setServiceId(int serviceId) { this.serviceId = serviceId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public double getRate() { return rate; }
    public void setRate(double rate) { this.rate = rate; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
    
    public String getPricingUnit() { return pricingUnit; }
    public void setPricingUnit(String pricingUnit) { this.pricingUnit = pricingUnit; }
    
    public boolean isDailyService() { return isDailyService; }
    public void setDailyService(boolean dailyService) { isDailyService = dailyService; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }
    
    public int getMaxQuantity() { return maxQuantity; }
    public void setMaxQuantity(int maxQuantity) { this.maxQuantity = maxQuantity; }
    
    public int getDiscountCount() { return discountCount; }
    public void setDiscountCount(int discountCount) { this.discountCount = discountCount; }

}