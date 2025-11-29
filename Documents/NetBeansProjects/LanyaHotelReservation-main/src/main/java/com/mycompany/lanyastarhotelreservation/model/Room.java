/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.lanyastarhotelreservation.model;

public class Room {
    private int roomId;
    private String roomType;
    private int capacity;
    private int extraBedCount; // Number of extra beds available
    private int availableRooms;
    private double extraBedPrice;
    
    // Local destination prices
    private double localLeanPrice;
    private double localHighPrice;
    private double localPeakPrice;
    private double localSuperPeakPrice;
    
    // International destination prices  
    private double internationalLeanPrice;
    private double internationalHighPrice;
    private double internationalPeakPrice;
    private double internationalSuperPeakPrice;

    public Room() {}

    // Getters and Setters
    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public int getExtraBedCount() { return extraBedCount; }
    public void setExtraBedCount(int extraBedCount) { this.extraBedCount = extraBedCount; }

    public int getAvailableRooms() { return availableRooms; }
    public void setAvailableRooms(int availableRooms) { this.availableRooms = availableRooms; }

    public double getExtraBedPrice() { return extraBedPrice; }
    public void setExtraBedPrice(double extraBedPrice) { this.extraBedPrice = extraBedPrice; }

    // Local prices
    public double getLocalLeanPrice() { return localLeanPrice; }
    public void setLocalLeanPrice(double localLeanPrice) { this.localLeanPrice = localLeanPrice; }

    public double getLocalHighPrice() { return localHighPrice; }
    public void setLocalHighPrice(double localHighPrice) { this.localHighPrice = localHighPrice; }

    public double getLocalPeakPrice() { return localPeakPrice; }
    public void setLocalPeakPrice(double localPeakPrice) { this.localPeakPrice = localPeakPrice; }

    public double getLocalSuperPeakPrice() { return localSuperPeakPrice; }
    public void setLocalSuperPeakPrice(double localSuperPeakPrice) { this.localSuperPeakPrice = localSuperPeakPrice; }

    // International prices
    public double getInternationalLeanPrice() { return internationalLeanPrice; }
    public void setInternationalLeanPrice(double internationalLeanPrice) { this.internationalLeanPrice = internationalLeanPrice; }

    public double getInternationalHighPrice() { return internationalHighPrice; }
    public void setInternationalHighPrice(double internationalHighPrice) { this.internationalHighPrice = internationalHighPrice; }

    public double getInternationalPeakPrice() { return internationalPeakPrice; }
    public void setInternationalPeakPrice(double internationalPeakPrice) { this.internationalPeakPrice = internationalPeakPrice; }

    public double getInternationalSuperPeakPrice() { return internationalSuperPeakPrice; }
    public void setInternationalSuperPeakPrice(double internationalSuperPeakPrice) { this.internationalSuperPeakPrice = internationalSuperPeakPrice; }

    // Method to get price based on destination type and season
    public double getPrice(String destinationType, String season) {
        if ("Local".equals(destinationType)) {
            switch (season) {
                case "Lean": return localLeanPrice;
                case "High": return localHighPrice;
                case "Peak": return localPeakPrice;
                case "Super Peak": return localSuperPeakPrice;
                default: return localLeanPrice;
            }
        } else { // International
            switch (season) {
                case "Lean": return internationalLeanPrice;
                case "High": return internationalHighPrice;
                case "Peak": return internationalPeakPrice;
                case "Super Peak": return internationalSuperPeakPrice;
                default: return internationalLeanPrice;
            }
        }
    }

    // Method to get capacity description including extra beds
    public String getCapacityDescription() {
        if (extraBedCount > 0) {
            return capacity + " guests with " + extraBedCount + " extra bed" + (extraBedCount > 1 ? "s" : "");
        } else {
            return capacity + " guests";
        }
    }

    // Method to check if room can accommodate extra beds needed
    public boolean canAccommodateExtraBeds(int extraBedsNeeded) {
        return extraBedsNeeded <= extraBedCount;
    }
}