/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.lanyastarhotelreservation;
import com.mycompany.lanyastarhotelreservation.util.DBConnection;
import java.sql.Connection;

/**
 *
 * @author johnm
 */
public class LanyaStarHotelReservation {

    public static void main(String[] args) {
        // Launch the GUI on the Event Dispatch Thread
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                // Establish database connection
                Connection conn = DBConnection.getConnection();
                
                // You can now use the `conn` to interact with the database if needed
                if (conn != null) {
                    // Example: Your database-related code goes here
                    // E.g., create a statement, execute queries, etc.
                }
            }
        });
    }
}
