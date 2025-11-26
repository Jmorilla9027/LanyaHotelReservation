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
    private String firstName;
    private String lastName;
    private int age;
    private String email;
    private String contactNumber;
    private String address;

    public Guest(String firstName, String lastName, int age, String email, String contactNumber, String address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.email = email;
        this.contactNumber = contactNumber;
        this.address = address;
    }

    public int getGuestId() {
        return guestId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getAge() {
        return age;
    }

    public String getEmail() {
        return email;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getAddress() {
        return address;
    }

    // --- SETTER for ID (assigned by database) ---
    public void setGuestId(int guestId) {
        this.guestId = guestId;
    }
}
