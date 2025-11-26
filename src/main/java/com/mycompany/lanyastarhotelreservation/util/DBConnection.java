/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.lanyastarhotelreservation.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 *
 * @author johnm
 */
public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/gmotu_hotel_db";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // XAMPP default

    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Database connected successfully!");
            return conn;
        } catch (SQLException e) {
            System.out.println(" Database connection failed!");
            e.printStackTrace();
            return null;
        }
    }
}
