/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.DAO;
import com.mycompany.lanyastarhotelreservation.model.Room;
import com.mycompany.lanyastarhotelreservation.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author johnm
 */
public class RoomDAO {
    private Connection connection;

    public RoomDAO() {
        this.connection = DBConnection.getConnection();
    }

public List<Room> getAvailableRooms(String destinationType, String season, int totalGuests) {
    List<Room> perfectFitRooms = new ArrayList<>();
    List<Room> requiresExtraBeds = new ArrayList<>();

    try {
        // Add null check for destinationType
        if (destinationType == null) {
            System.err.println("ERROR: destinationType is null in getAvailableRooms()");
            return new ArrayList<>(); // Return empty list
        }
        
        String tableName = "local".equalsIgnoreCase(destinationType) ? "local_rooms" : "international_rooms";
        String priceColumn = getPriceColumnName(season);
        
        String sql = "SELECT *, " + priceColumn + " AS selected_price FROM " + tableName + 
                    " WHERE available_rooms > 0 AND (capacity + extra_bed_count) >= ? " +
                    "ORDER BY capacity ASC";
        
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, totalGuests);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Room room = extractRoomFromResultSet(rs, destinationType);
            double price = rs.getDouble("selected_price");
            
            if (price > 0) {
                if (room.getCapacity() >= totalGuests) {
                    perfectFitRooms.add(room);
                } else {
                    requiresExtraBeds.add(room);
                }
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    List<Room> allRooms = new ArrayList<>();
    allRooms.addAll(perfectFitRooms);
    allRooms.addAll(requiresExtraBeds);
    return allRooms;
}

    public Room getRoomByType(String roomType, String destinationType) {
        try {
            String tableName = "local".equalsIgnoreCase(destinationType) ? "local_rooms" : "international_rooms";
            String sql = "SELECT * FROM " + tableName + " WHERE room_type = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, roomType);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractRoomFromResultSet(rs, destinationType);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateRoomAvailability(String roomType, String destinationType) {
        try {
            String tableName = "local".equalsIgnoreCase(destinationType) ? "local_rooms" : "international_rooms";
            String sql = "UPDATE " + tableName + " SET available_rooms = available_rooms - 1 " +
                        "WHERE room_type = ? AND available_rooms > 0";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, roomType);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String getPriceColumnName(String season) {
        if (season == null) {
            return "lean_price"; // Default
        }

        switch (season) {
            case "Lean": return "lean_price";
            case "High": return "high_price";
            case "Peak": return "peak_price";
            case "Super Peak": return "super_peak_price";
            default: return "lean_price";
        }
    }

    private Room extractRoomFromResultSet(ResultSet rs, String destinationType) throws SQLException {
        Room room = new Room();
        room.setRoomId(rs.getInt("room_id"));
        room.setRoomType(rs.getString("room_type"));
        room.setCapacity(rs.getInt("capacity"));
        room.setExtraBedCount(rs.getInt("extra_bed_count"));
        room.setAvailableRooms(rs.getInt("available_rooms"));


        // Add null check for destinationType
        if (destinationType != null && "Local".equalsIgnoreCase(destinationType)) {
            room.setLocalLeanPrice(rs.getDouble("lean_price"));
            room.setLocalHighPrice(rs.getDouble("high_price"));
            room.setLocalPeakPrice(rs.getDouble("peak_price"));
            room.setLocalSuperPeakPrice(rs.getDouble("super_peak_price"));
            // Set international prices to 0
            room.setInternationalLeanPrice(0);
            room.setInternationalHighPrice(0);
            room.setInternationalPeakPrice(0);
            room.setInternationalSuperPeakPrice(0);
        } else if (destinationType != null) {
            // Assume International if not null and not Local
            room.setInternationalLeanPrice(rs.getDouble("lean_price"));
            room.setInternationalHighPrice(rs.getDouble("high_price"));
            room.setInternationalPeakPrice(rs.getDouble("peak_price"));
            room.setInternationalSuperPeakPrice(rs.getDouble("super_peak_price"));
            // Set local prices to 0
            room.setLocalLeanPrice(0);
            room.setLocalHighPrice(0);
            room.setLocalPeakPrice(0);
            room.setLocalSuperPeakPrice(0);
        }

        return room;
    }
}
