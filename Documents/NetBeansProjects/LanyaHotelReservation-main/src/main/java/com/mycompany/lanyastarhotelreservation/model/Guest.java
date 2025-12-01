/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.lanyastarhotelreservation.model;

/**
 *
 * @author johnm
 */
public class Guest {
    private int guestId;
    private String name;
    private String email;
    private String contactNumber;
    private int age;
    
    // Constructors
    public Guest() {}
    
    public Guest(String name, String email, String contactNumber, int age) {
        this.name = name;
        this.email = email;
        this.contactNumber = contactNumber;
        this.age = age;
    }
    
    // Getters and Setters
    public int getGuestId() { return guestId; }
    public void setGuestId(int guestId) { this.guestId = guestId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
}
