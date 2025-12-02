/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.DAO;
import com.mycompany.lanyastarhotelreservation.model.Services;
import com.mycompany.lanyastarhotelreservation.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author johnm
 */
public class ServicesDAO {
    
    public List<Services> getAllServices() {
        List<Services> services = new ArrayList<>();
        String sql = "SELECT * FROM services WHERE is_active = TRUE";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Services service = extractServiceFromResultSet(rs);
                services.add(service);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return services;
    }
    public void saveBookingServices(int bookingId, List<Services> services) throws SQLException {
        if (services == null || services.isEmpty()) return;

        // Omit service_id if it's auto-increment
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
    
    public Services getServiceByName(String name) {
        String sql = "SELECT * FROM services WHERE name = ? AND is_active = TRUE";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractServiceFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private Services extractServiceFromResultSet(ResultSet rs) throws SQLException {
        Services service = new Services();
        service.setServiceId(rs.getInt("service_id"));
        service.setName(rs.getString("name"));
        service.setRate(rs.getDouble("rate"));
        service.setDescription(rs.getString("description"));
        service.setDuration(rs.getString("duration"));
        service.setPricingUnit(rs.getString("pricing_unit"));
        service.setDailyService(rs.getBoolean("is_daily_service"));
        return service;
    }
}
