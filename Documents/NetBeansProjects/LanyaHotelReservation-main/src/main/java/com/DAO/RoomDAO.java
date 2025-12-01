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
            String sql = "SELECT * FROM rooms WHERE available_rooms > 0 AND (capacity + extra_bed_count) >= ? ORDER BY capacity ASC";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, totalGuests);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Room room = extractRoomFromResultSet(rs);
                if (room.getCapacity() >= totalGuests) {
                    perfectFitRooms.add(room);
                } else {
                    requiresExtraBeds.add(room);
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

    public Room getRoomByType(String roomType) {
        try {
            String sql = "SELECT * FROM rooms WHERE room_type = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, roomType);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractRoomFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateRoomAvailability(String roomType) {
        try {
            String sql = "UPDATE rooms SET available_rooms = available_rooms - 1 WHERE room_type = ? AND available_rooms > 0";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, roomType);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Room extractRoomFromResultSet(ResultSet rs) throws SQLException {
        Room room = new Room();
        room.setRoomId(rs.getInt("room_id"));
        room.setRoomType(rs.getString("room_type"));
        room.setCapacity(rs.getInt("capacity"));
        room.setExtraBedCount(rs.getInt("extra_bed_count"));
        room.setAvailableRooms(rs.getInt("available_rooms"));
        room.setExtraBedPrice(rs.getDouble("extra_bed_price"));
        
        room.setLocalLeanPrice(rs.getDouble("local_lean_price"));
        room.setLocalHighPrice(rs.getDouble("local_high_price"));
        room.setLocalPeakPrice(rs.getDouble("local_peak_price"));
        room.setLocalSuperPeakPrice(rs.getDouble("local_super_peak_price"));
        
        room.setInternationalLeanPrice(rs.getDouble("international_lean_price"));
        room.setInternationalHighPrice(rs.getDouble("international_high_price"));
        room.setInternationalPeakPrice(rs.getDouble("international_peak_price"));
        room.setInternationalSuperPeakPrice(rs.getDouble("international_super_peak_price"));
        
        return room;
    }
}
