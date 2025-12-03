package com.mycompany.lanyastarhotelreservation.model;

import com.DAO.BookingDAO;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Booking {
    private String destinationType;
    private String destination;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int numberOfAdults;
    private int numberOfChildren;
    private int leadGuestAge;
    private List<Integer> childrenAges;
    private String season; // Add season field
    
    private static final int MIN_LEAD_GUEST_AGE = 18;
    private static final int MIN_CHILD_AGE = 0;
    private static final int MAX_CHILD_AGE = 17;
    
    public Booking() {
        this.childrenAges = new ArrayList<>();
    }
    
    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        
        if (destinationType == null || destinationType.equals("--Select Type--")) {
            errors.add("Please select destination type.");
        }
        if (destination == null || destination.equals("--Select Location--")) {
            errors.add("Please select destination.");
        }
        if (checkInDate == null) {
            errors.add("Please enter check-in date.");
        }
        if (checkOutDate == null) {
            errors.add("Please enter check-out date.");
        }
        if (checkInDate != null && checkOutDate != null) {
            LocalDate today = LocalDate.now();
            if (!checkInDate.isAfter(today)) {
                errors.add("Check-in date must be a future date.");
            }
            if (!checkOutDate.isAfter(checkInDate)) {
                errors.add("Check-out must be AFTER check-in.");
            }
        }
        if (numberOfAdults < 1) {
            errors.add("Number of adults must be at least 1.");
        }
        if (numberOfChildren < 0) {
            errors.add("Number of children cannot be negative.");
        }
        if (leadGuestAge < MIN_LEAD_GUEST_AGE) {
            errors.add("Lead guest must be " + MIN_LEAD_GUEST_AGE + " or older.");
        }
        if (childrenAges.size() != numberOfChildren) {
            errors.add("Please enter age for all " + numberOfChildren + " children.");
        }
        
        for (int i = 0; i < childrenAges.size(); i++) {
            int childAge = childrenAges.get(i);
            if (childAge < MIN_CHILD_AGE || childAge > MAX_CHILD_AGE) {
                errors.add("Child " + (i + 1) + " must be between " + MIN_CHILD_AGE + " and " + MAX_CHILD_AGE + " years old.");
            }
        }
        
        if (checkInDate != null && checkOutDate != null) {
        LocalDate today = LocalDate.now();
        
        if (!checkInDate.isAfter(today)) {
            errors.add("Check-in date must be a future date.");
        }
        if (!checkOutDate.isAfter(checkInDate)) {
            errors.add("Check-out must be AFTER check-in.");
        }
        
        // Add maximum 1-year stay validation
        long daysBetween = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        if (daysBetween > 365) {
            errors.add("Booking cannot exceed 1 year (365 days).");
        }
    }
    
    // ... rest of existing validations ...
    
    return errors;
}
    
    public int calculatePayingGuests() {
        int payingChildren = 0;
        for (int age : childrenAges) {
            if (age >= 8) {
                payingChildren++;
            }
        }
        return numberOfAdults + payingChildren;
    }

    public int calculateNights() {
        if (checkInDate != null && checkOutDate != null && checkOutDate.isAfter(checkInDate)) {
            return (int) ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        }
        return 0;
    }

    public int saveToDatabase() throws SQLException {
        BookingDAO dao = new BookingDAO();
        
        // Calculate season before saving
        this.season = calculateSeason();
        
        return dao.saveBooking(this);
    }
    

    public String calculateSeason() {
        if (checkInDate == null) {
            return "Lean"; // Default season
        }

        int month = checkInDate.getMonthValue();
        int day = checkInDate.getDayOfMonth();
        int year = checkInDate.getYear();

        // ============================================
        // 1. SPECIAL EVENTS (Treat as Super Peak)
        // ============================================
        if (isSpecialEvent(year, month, day)) {
            return "Super Peak";
        }

        // ============================================
        // 2. SEASON LOGIC (Based on your inclusive dates)
        // ============================================

        // A) SUPER PEAK: December 20 to January 5
        if ((month == 12 && day >= 20) || (month == 1 && day <= 5)) {
            return "Super Peak";
        }

        // B) HIGH SEASON: 
        //    - November 1 to December 19
        //    - January 6 to February 28
        if (month == 11) { // November 1-30
            return "High";
        }
        if (month == 12 && day <= 19) { // December 1-19
            return "High";
        }
        if (month == 1 && day >= 6) { // January 6-31
            return "High";
        }
        if (month == 2) { // February 1-28/29
            return "High";
        }

        // C) PEAK SEASON: March 1 to May 31
        if (month >= 3 && month <= 5) {
            return "Peak";
        }

        // D) LEAN SEASON: June 1 to October 31 (default)
        //    - June, July, August, September, October
        return "Lean";
    }
    private boolean isSpecialEvent(int year, int month, int day) {
    // ============================================
    // HOLY WEEK DATES (Update yearly!)
    // ============================================
    if (year == 2024 && month == 3) { // March 2024
        // Holy Week 2024: March 24-30
        return (day >= 24 && day <= 30);
    }
    if (year == 2025 && month == 4) { // April 2025
        // Holy Week 2025: April 13-19 (example - check actual dates)
        return (day >= 13 && day <= 19);
    }
    
    // ============================================
    // CHINESE NEW YEAR DATES (Update yearly!)
    // ============================================
    if (year == 2024 && month == 2 && day == 10) { // Feb 10, 2024
        return true;
    }
    if (year == 2025 && month == 1 && day == 29) { // Jan 29, 2025 (example)
        return true;
    }
    
    // ============================================
    // ADD MORE SPECIAL EVENTS AS NEEDED
    // ============================================
    
    return false;
}
    public static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr.trim());
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    // Getters and Setters
    public String getDestinationType() { return destinationType; }
    public void setDestinationType(String destinationType) { this.destinationType = destinationType; }
    
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    
    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }
    
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }
    
    public int getNumberOfAdults() { return numberOfAdults; }
    public void setNumberOfAdults(int numberOfAdults) { this.numberOfAdults = numberOfAdults; }
    
    public int getNumberOfChildren() { return numberOfChildren; }
    public void setNumberOfChildren(int numberOfChildren) { this.numberOfChildren = numberOfChildren; }
    
    public int getLeadGuestAge() { return leadGuestAge; }
    public void setLeadGuestAge(int leadGuestAge) { this.leadGuestAge = leadGuestAge; }
    
    public List<Integer> getChildrenAges() { return childrenAges; }
    public void setChildrenAges(List<Integer> childrenAges) { this.childrenAges = childrenAges; }
    
    public String getSeason() { 
        if (season == null) {
            season = calculateSeason();
        }
        return season; 
    }
    public void setSeason(String season) { this.season = season; }
    
    public void addChildAge(int age) {
        if (this.childrenAges == null) {
            this.childrenAges = new ArrayList<>();
        }
        this.childrenAges.add(age);
    }
}
