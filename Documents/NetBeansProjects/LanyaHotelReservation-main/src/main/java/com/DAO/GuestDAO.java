/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.DAO;
import com.mycompany.lanyastarhotelreservation.model.Guest;
import com.mycompany.lanyastarhotelreservation.util.DBConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author johnm
 */
public class GuestDAO {
    
    // Save a new guest to database
    public int saveGuest(Guest guest) throws SQLException {
        String validation = guest.validate();
        if (!"VALID".equals(validation)) {
            throw new IllegalArgumentException("Guest validation failed: " + validation);
        }
        
        String sql = "INSERT INTO customers (name, email, phone, created_date) VALUES (?, ?, ?, NOW())";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, guest.getName());
            stmt.setString(2, guest.getEmail() != null ? guest.getEmail().trim() : "");
            stmt.setString(3, guest.getPhone() != null ? guest.getPhone().trim() : "");
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating guest failed, no rows affected.");
            }
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    guest.setCustomerId(generatedId);
                    return generatedId;
                } else {
                    throw new SQLException("Creating guest failed, no ID obtained.");
                }
            }
        }
    }
    
    // Save or update guest (check if exists by email/phone)
    public int saveOrUpdateGuest(Guest guest) throws SQLException {
        String validation = guest.validate();
        if (!"VALID".equals(validation)) {
            throw new IllegalArgumentException("Guest validation failed: " + validation);
        }
        
        // Check if guest already exists
        Guest existingGuest = findExistingGuest(guest.getEmail(), guest.getPhone());
        
        if (existingGuest != null) {
            // Update existing guest
            existingGuest.setName(guest.getName());
            if (guest.getEmail() != null && !guest.getEmail().isEmpty()) {
                existingGuest.setEmail(guest.getEmail());
            }
            if (guest.getPhone() != null && !guest.getPhone().isEmpty()) {
                existingGuest.setPhone(guest.getPhone());
            }
            
            updateGuest(existingGuest);
            return existingGuest.getCustomerId();
        } else {
            // Create new guest
            return saveGuest(guest);
        }
    }
    
    // Find existing guest by email or phone
    private Guest findExistingGuest(String email, String phone) throws SQLException {
        String sql = "SELECT customer_id, name, email, phone, created_date " +
                    "FROM customers " +
                    "WHERE (email = ? AND email != '') OR (phone = ? AND phone != '') " +
                    "LIMIT 1";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String emailParam = (email != null && !email.trim().isEmpty()) ? email.trim() : "";
            String phoneParam = (phone != null && !phone.trim().isEmpty()) ? phone.trim() : "";
            
            stmt.setString(1, emailParam);
            stmt.setString(2, phoneParam);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractGuestFromResultSet(rs);
            }
        }
        return null;
    }
    
    // Update existing guest
    public void updateGuest(Guest guest) throws SQLException {
        String sql = "UPDATE customers SET name = ?, email = ?, phone = ? WHERE customer_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, guest.getName());
            stmt.setString(2, guest.getEmail() != null ? guest.getEmail().trim() : "");
            stmt.setString(3, guest.getPhone() != null ? guest.getPhone().trim() : "");
            stmt.setInt(4, guest.getCustomerId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Updating guest failed, no rows affected.");
            }
        }
    }
    
    // Get guest by ID
    public Guest getGuestById(int customerId) throws SQLException {
        String sql = "SELECT customer_id, name, email, phone, created_date " +
                    "FROM customers WHERE customer_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractGuestFromResultSet(rs);
            }
        }
        return null;
    }
    
    // Get guest by booking ID
    public Guest getGuestByBookingId(int bookingId) throws SQLException {
        String sql = "SELECT c.customer_id, c.name, c.email, c.phone, c.created_date " +
                    "FROM customers c " +
                    "JOIN bookings b ON c.customer_id = b.customer_id " +
                    "WHERE b.booking_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookingId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractGuestFromResultSet(rs);
            }
        }
        return null;
    }
    
    // Search guests by name
    public List<Guest> searchGuestsByName(String searchTerm) throws SQLException {
        List<Guest> guests = new ArrayList<>();
        String sql = "SELECT customer_id, name, email, phone, created_date " +
                    "FROM customers WHERE name LIKE ? ORDER BY name LIMIT 20";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + searchTerm + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                guests.add(extractGuestFromResultSet(rs));
            }
        }
        return guests;
    }
    
    // Helper method to extract Guest from ResultSet
    private Guest extractGuestFromResultSet(ResultSet rs) throws SQLException {
        Guest guest = new Guest();
        guest.setCustomerId(rs.getInt("customer_id"));
        guest.setName(rs.getString("name"));
        guest.setEmail(rs.getString("email"));
        guest.setPhone(rs.getString("phone"));
        
        Timestamp createdTimestamp = rs.getTimestamp("created_date");
        if (createdTimestamp != null) {
            guest.setCreatedDate(createdTimestamp.toLocalDateTime());
        }
        
        return guest;
    }
}