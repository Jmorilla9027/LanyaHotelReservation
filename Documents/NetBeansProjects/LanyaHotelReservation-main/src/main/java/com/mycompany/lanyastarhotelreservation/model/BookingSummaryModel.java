/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.lanyastarhotelreservation.model;

import java.util.List;

/**
 *
 * @author johnm
 */
public class BookingSummaryModel {
    private Booking booking;
    private Room selectedRoom;
    private List<Addon> selectedAddons;
    private List<Services> selectedServices;
    private double roomTotal;
    private double addonsTotal;
    private double servicesTotal;
    private double discountTotal;
    private double grandTotal;
    
    // Constructor
    public BookingSummaryModel(Booking booking, Room selectedRoom, 
                         List<Addon> selectedAddons, List<Services> selectedServices) {
        this.booking = booking;
        this.selectedRoom = selectedRoom;
        this.selectedAddons = selectedAddons;
        this.selectedServices = selectedServices;
        calculateTotals();
    }
    
    private void calculateTotals() {
        // Calculate room total (room price × nights)
        String season = determineSeason(booking.getCheckInDate());
        double roomRate = selectedRoom.getPrice(booking.getDestinationType(), season);
        int nights = booking.calculateNights();
        roomTotal = roomRate * nights;
        
        // Calculate addons total
        addonsTotal = 0;
        for (Addon addon : selectedAddons) {
            double addonRate = addon.getRate();
            int quantity = addon.getQuantity();
            int discounted = addon.getDiscountCount();
            int regular = quantity - discounted;
            
            // Apply 20% discount for PWD/Senior
            double discountMultiplier = 0.80; // 20% off
            
            double addonTotal = (regular * addonRate) + (discounted * addonRate * discountMultiplier);
            
            // If it's a bed addon and needs per-night calculation
            if ("Bed".equals(addon.getName()) && addon.getUnit().toLowerCase().contains("night")) {
                addonTotal *= nights;
            }
            addonsTotal += addonTotal;
        }
        
        // Calculate services total
        servicesTotal = 0;
        for (Services service : selectedServices) {
            double serviceRate = service.getRate();
            int quantity = service.getQuantity();
            int discounted = service.getDiscountCount();
            int regular = quantity - discounted;
            
            // Apply 20% discount for PWD/Senior
            double discountMultiplier = 0.80; // 20% off
            
            double serviceTotal = (regular * serviceRate) + (discounted * serviceRate * discountMultiplier);
            
            // If it's a daily service (like swimming pool, gym), multiply by days
            if (service.requiresDaysInput()) {
                // For daily services, quantity is per person per day
                serviceTotal *= nights;
            }
            servicesTotal += serviceTotal;
        }
        
        // Calculate discount total
        discountTotal = 0;
        for (Addon addon : selectedAddons) {
            if (addon.getDiscountCount() > 0) {
                discountTotal += addon.getDiscountCount() * addon.getRate() * 0.20; // 20% discount amount
            }
        }
        for (Services service : selectedServices) {
            if (service.getDiscountCount() > 0) {
                discountTotal += service.getDiscountCount() * service.getRate() * 0.20; // 20% discount amount
            }
        }
        
        // Calculate grand total
        grandTotal = roomTotal + addonsTotal + servicesTotal;
    }
    
    private String determineSeason(java.time.LocalDate checkInDate) {
        if (checkInDate == null) return "Lean";
        int month = checkInDate.getMonthValue();
        if (month >= 3 && month <= 5) return "Lean";
        else if (month >= 6 && month <= 8) return "High";
        else if (month >= 9 && month <= 11) return "Peak";
        else return "Super Peak";
    }
    
    // Getters
    public Booking getBooking() { return booking; }
    public Room getSelectedRoom() { return selectedRoom; }
    public List<Addon> getSelectedAddons() { return selectedAddons; }
    public List<Services> getSelectedServices() { return selectedServices; }
    public double getRoomTotal() { return roomTotal; }
    public double getAddonsTotal() { return addonsTotal; }
    public double getServicesTotal() { return servicesTotal; }
    public double getDiscountTotal() { return discountTotal; }
    public double getGrandTotal() { return grandTotal; }
    
    // Helper method to format currency
    public String formatCurrency(double amount) {
        return String.format("₱ %,.2f", amount);
    }
}