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
import java.time.LocalDate;
/**
 *
 * @author johnm
 */
public class BookingDAO {
    
    public int saveBooking(Booking booking) throws SQLException {
       String sql = "INSERT INTO bookings (destination_type, destination, check_in_date, " +
                   "check_out_date, lead_guest_age, number_of_adults, number_of_children, " +
                   "total_guests, nights_stay, booking_date, status, season) " +
                   "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), 'CONFIRMED', ?)";

       try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

           stmt.setString(1, booking.getDestinationType());
           stmt.setString(2, booking.getDestination());

           // Set dates
           LocalDate checkIn = booking.getCheckInDate();
           LocalDate checkOut = booking.getCheckOutDate();

           stmt.setDate(3, checkIn != null ? Date.valueOf(checkIn) : null);
           stmt.setDate(4, checkOut != null ? Date.valueOf(checkOut) : null);

           stmt.setInt(5, booking.getLeadGuestAge());
           stmt.setInt(6, booking.getNumberOfAdults());
           stmt.setInt(7, booking.getNumberOfChildren());
           stmt.setInt(8, booking.calculatePayingGuests());
           stmt.setInt(9, booking.calculateNights());
           stmt.setString(10, booking.getSeason()); // This should now never be null

           int affectedRows = stmt.executeUpdate();

           if (affectedRows == 0) {
               throw new SQLException("Creating booking failed, no rows affected.");
           }

           try (ResultSet rs = stmt.getGeneratedKeys()) {
               if (rs.next()) {
                   return rs.getInt(1); // Return booking_id
               } else {
                   throw new SQLException("Creating booking failed, no ID obtained.");
               }
           }
       }
   }
    
    public Booking getBookingById(int bookingId) throws SQLException {
        String sql = "SELECT * FROM bookings WHERE booking_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookingId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Booking booking = new Booking();
                booking.setDestinationType(rs.getString("destination_type"));
                booking.setDestination(rs.getString("destination"));
                
                Date checkInDate = rs.getDate("check_in_date");
                Date checkOutDate = rs.getDate("check_out_date");
                
                if (checkInDate != null) {
                    booking.setCheckInDate(checkInDate.toLocalDate());
                }
                if (checkOutDate != null) {
                    booking.setCheckOutDate(checkOutDate.toLocalDate());
                }
                
                booking.setLeadGuestAge(rs.getInt("lead_guest_age"));
                booking.setNumberOfAdults(rs.getInt("number_of_adults"));
                booking.setNumberOfChildren(rs.getInt("number_of_children"));
                booking.setSeason(rs.getString("season"));
                
                return booking;
            }
        }
        return null;
    }
    
    public void saveBookingAddons(int bookingId, List<Addon> addons) throws SQLException {
        if (addons == null || addons.isEmpty()) return;
        
        String sql = "INSERT INTO booking_addons (booking_id, addon_name, quantity, rate, discount_count, discount_amount) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            for (Addon addon : addons) {
                if (addon.isSelected() && addon.getQuantity() > 0) {
                    stmt.setInt(1, bookingId);
                    stmt.setString(2, addon.getName());
                    stmt.setInt(3, addon.getQuantity());
                    stmt.setDouble(4, addon.getRate());
                    stmt.setInt(5, addon.getDiscountCount());
                    stmt.setDouble(6, addon.calculateDiscountAmount());
                    stmt.addBatch();
                }
            }
            stmt.executeBatch();
        }
    }
    
    public void saveBookingServices(int bookingId, List<Services> services) throws SQLException {
        if (services == null || services.isEmpty()) return;
        
        String sql = "INSERT INTO booking_services (booking_id, service_name, quantity, rate, discount_count, discount_amount) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            for (Services service : services) {
                if (service.isSelected() && service.getQuantity() > 0) {
                    stmt.setInt(1, bookingId);
                    stmt.setString(2, service.getName());
                    stmt.setInt(3, service.getQuantity());
                    stmt.setDouble(4, service.getRate());
                    stmt.setInt(5, service.getDiscountCount());
                    stmt.setDouble(6, service.calculateDiscountAmount());
                    stmt.addBatch();
                }
            }
            stmt.executeBatch();
        }
    }
}

