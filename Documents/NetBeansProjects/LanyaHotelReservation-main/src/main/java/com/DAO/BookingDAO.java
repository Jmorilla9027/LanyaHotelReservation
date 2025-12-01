/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.DAO;
import com.mycompany.lanyastarhotelreservation.util.DBConnection;
import com.mycompany.lanyastarhotelreservation.model.Booking;
import com.mycompany.lanyastarhotelreservation.model.Addon;
import com.mycompany.lanyastarhotelreservation.model.Services;
import java.sql.*;
import java.util.List;
/**
 *
 * @author johnm
 */
public class BookingDAO {
     public int saveBooking(Booking booking) throws SQLException {
        String sql = "INSERT INTO bookings (destination_type, destination, check_in_date, check_out_date, " +
                    "lead_guest_age, number_of_adults, number_of_children, total_guests, nights_stay) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, booking.getDestinationType());
            stmt.setString(2, booking.getDestination());
            stmt.setDate(3, Date.valueOf(booking.getCheckInDate()));
            stmt.setDate(4, Date.valueOf(booking.getCheckOutDate()));
            stmt.setInt(5, booking.getLeadGuestAge());
            stmt.setInt(6, booking.getNumberOfAdults());
            stmt.setInt(7, booking.getNumberOfChildren());
            stmt.setInt(8, booking.calculatePayingGuests());
            stmt.setInt(9, booking.calculateNights());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating booking failed, no rows affected.");
            }
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new SQLException("Creating booking failed, no ID obtained.");
                }
            }
        }
    }
    
    public void saveBookingAddons(int bookingId, List<Addon> addons) throws SQLException {
        if (addons == null || addons.isEmpty()) return;

        String sql = "INSERT INTO booking_addons (booking_id, addon_id, addon_name, quantity, rate, discount_count) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (Addon addon : addons) {
                if (addon.isSelected() && addon.getQuantity() > 0) {
                    stmt.setInt(1, bookingId);
                    stmt.setInt(2, addon.getAddonId()); // Save the ID
                    stmt.setString(3, addon.getName());
                    stmt.setInt(4, addon.getQuantity());
                    stmt.setDouble(5, addon.getRate());
                    stmt.setInt(6, addon.getDiscountCount());
                    stmt.addBatch();
                }
            }
            stmt.executeBatch();
        }
    }
    
    public void saveBookingServices(int bookingId, List<Services> services) throws SQLException {
        if (services == null || services.isEmpty()) return;

        String sql = "INSERT INTO booking_services (booking_id, service_id, service_name, quantity, rate, discount_count) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (Services service : services) {
                if (service.isSelected() && service.getQuantity() > 0) {
                    stmt.setInt(1, bookingId);
                    stmt.setInt(2, service.getServiceId()); // Save the ID
                    stmt.setString(3, service.getName());
                    stmt.setInt(4, service.getQuantity());
                    stmt.setDouble(5, service.getRate());
                    stmt.setInt(6, service.getDiscountCount());
                    stmt.addBatch();
                }
            }
            stmt.executeBatch();
        }
    }
}

