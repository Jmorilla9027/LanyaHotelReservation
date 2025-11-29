/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.DAO;
import com.mycompany.lanyastarhotelreservation.model.Room;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author johnm
 */
public class RoomDAO {
    private Connection connection;

    public RoomDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Room> getAvailableRooms(String destinationType, String destination, String season, int totalGuests) {
    List<Room> availableRooms = new ArrayList<>();
    
    try {
        String sql = "SELECT * FROM rooms WHERE available_rooms > 0 AND capacity >= ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, totalGuests);
        
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
            Room room = new Room();
            room.setRoomId(rs.getInt("room_id"));
            room.setRoomType(rs.getString("room_type"));
            room.setCapacity(rs.getInt("capacity"));
            room.setExtraBedCount(rs.getInt("extra_bed_count"));
            room.setAvailableRooms(rs.getInt("available_rooms"));
            room.setExtraBedPrice(rs.getDouble("extra_bed_price"));
            
            // Set local prices
            room.setLocalLeanPrice(rs.getDouble("local_lean_price"));
            room.setLocalHighPrice(rs.getDouble("local_high_price"));
            room.setLocalPeakPrice(rs.getDouble("local_peak_price"));
            room.setLocalSuperPeakPrice(rs.getDouble("local_super_peak_price"));
            
            // Set international prices
            room.setInternationalLeanPrice(rs.getDouble("international_lean_price"));
            room.setInternationalHighPrice(rs.getDouble("international_high_price"));
            room.setInternationalPeakPrice(rs.getDouble("international_peak_price"));
            room.setInternationalSuperPeakPrice(rs.getDouble("international_super_peak_price"));
            
            availableRooms.add(room);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    
    return availableRooms;
    }
}
