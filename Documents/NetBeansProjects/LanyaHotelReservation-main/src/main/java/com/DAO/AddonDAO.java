/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.DAO;
import com.mycompany.lanyastarhotelreservation.model.Addon;
import com.mycompany.lanyastarhotelreservation.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author johnm
 */
public class AddonDAO {
    
    public List<Addon> getAllAddons() {
        List<Addon> addons = new ArrayList<>();
        String sql = "SELECT * FROM addons WHERE is_active = TRUE";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Addon addon = extractAddonFromResultSet(rs);
                addons.add(addon);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return addons;
    }
    
    public Addon getAddonByName(String name) {
        String sql = "SELECT * FROM addons WHERE name = ? AND is_active = TRUE";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractAddonFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private Addon extractAddonFromResultSet(ResultSet rs) throws SQLException {
        Addon addon = new Addon();
        addon.setAddonId(rs.getInt("addon_id"));
        addon.setName(rs.getString("name"));
        addon.setRate(rs.getDouble("rate"));
        addon.setDescription(rs.getString("description"));
        addon.setUnit(rs.getString("unit"));
        return addon;
    }
}
