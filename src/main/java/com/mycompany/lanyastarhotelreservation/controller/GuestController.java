/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.lanyastarhotelreservation.controller;
import com.mycompany.lanyastarhotelreservation.model.Guest;
import com.mycompany.lanyastarhotelreservation.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
/**
 *
 * @author johnm
 */
public class GuestController {
     public boolean addGuest(Guest guest) {
        String sql = "INSERT INTO guests (first_name, last_name, age, email, contact_number, address) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, guest.getFirstName());
            ps.setString(2, guest.getLastName());
            ps.setInt(3, guest.getAge());
            ps.setString(4, guest.getEmail());
            ps.setString(5, guest.getContactNumber());
            ps.setString(6, guest.getAddress());

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error inserting guest:");
            e.printStackTrace();
            return false;
        }
    }
}
