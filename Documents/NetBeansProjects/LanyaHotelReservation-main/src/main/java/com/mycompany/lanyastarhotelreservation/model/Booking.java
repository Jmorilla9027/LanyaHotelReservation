package com.mycompany.lanyastarhotelreservation.model;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Booking {
    // Private attributes
    private String destinationType;
    private String destination;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int numberOfAdults;
    private int numberOfChildren;
    private int leadGuestAge;
    private List<Integer> childrenAges;
    
    // Constants
    private static final int MIN_LEAD_GUEST_AGE = 18;
    private static final int MIN_CHILD_AGE = 0;
    private static final int MAX_CHILD_AGE = 17;
    
    // Constructors
    public Booking() {
        this.childrenAges = new ArrayList<>();
    }
    
    public Booking(String destinationType, String destination, LocalDate checkInDate, 
                  LocalDate checkOutDate, int leadGuestAge, int numberOfAdults, 
                  int numberOfChildren, List<Integer> childrenAges) {
        this.destinationType = destinationType;
        this.destination = destination;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.leadGuestAge = leadGuestAge;
        this.numberOfAdults = numberOfAdults;
        this.numberOfChildren = numberOfChildren;
        this.childrenAges = childrenAges != null ? childrenAges : new ArrayList<>();
    }

    // Validation Methods
    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        
        validateDestination(errors);
        validateDates(errors);
        validateGuestInfo(errors);
        validateChildrenAges(errors);
        
        return errors;
    }
    
    private void validateDestination(List<String> errors) {
        if (destinationType == null || destinationType.equals("--Select Type--")) {
            errors.add("Please select destination type.");
        }
        if (destination == null || destination.equals("--Select Destination--")) {
            errors.add("Please select destination.");
        }
    }
    
    private void validateDates(List<String> errors) {
        if (checkInDate == null) {
            errors.add("Please enter check-in date in YYYY-MM-DD format.");
        }
        if (checkOutDate == null) {
            errors.add("Please enter check-out date in YYYY-MM-DD format.");
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
    }
    
    private void validateGuestInfo(List<String> errors) {
        if (numberOfAdults < 1) {
            errors.add("Number of adults must be at least 1.");
        }
        if (numberOfChildren < 0) {
            errors.add("Number of children cannot be negative.");
        }
        if (leadGuestAge < MIN_LEAD_GUEST_AGE) {
            errors.add("Lead guest must be " + MIN_LEAD_GUEST_AGE + " or older.");
        }
    }
    
    private void validateChildrenAges(List<String> errors) {
        // Check if number of children ages matches the number of children
        if (childrenAges.size() != numberOfChildren) {
            errors.add("Please enter age for all " + numberOfChildren + " children.");
            return;
        }
        
        // Validate each child's age
        for (int i = 0; i < childrenAges.size(); i++) {
            int childAge = childrenAges.get(i);
            if (childAge < MIN_CHILD_AGE || childAge > MAX_CHILD_AGE) {
                errors.add("Child " + (i + 1) + " must be between " + MIN_CHILD_AGE + " and " + MAX_CHILD_AGE + " years old.");
            }
        }
    }
    
    // Business Logic Methods
    public int calculatePayingGuests() {
        int payingChildren = 0;

        for (int age : childrenAges) {
            if (age >= 8) { // Children 8-17 are considered paying guests
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

    public int getFreeChildrenCount() {
        int freeChildren = 0;

        for (int age : childrenAges) {
            if (age >= 0 && age <= 7) {
                freeChildren++;
            }
        }

        return freeChildren;
    }

    public int getChildrenNeedingBedCount() {
        int childrenNeedingBed = 0;

        for (int age : childrenAges) {
            if (age >= 8 && age <= 17) {
                childrenNeedingBed++;
            }
        }

        return childrenNeedingBed;
    }
    
    // Utility Methods
    public static boolean isValidChildAge(int age) {
        return age >= MIN_CHILD_AGE && age <= MAX_CHILD_AGE;
    }
    
    public static String getChildAgeValidationMessage() {
        return "Child age must be between " + MIN_CHILD_AGE + " and " + MAX_CHILD_AGE + " years old.";
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
    
    public boolean hasAllChildAges() {
        return childrenAges != null && childrenAges.size() == numberOfChildren;
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
    
    public void addChildAge(int age) {
        if (this.childrenAges == null) {
            this.childrenAges = new ArrayList<>();
        }
        this.childrenAges.add(age);
    }
}