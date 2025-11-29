/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.lanyastarhotelreservation;
import com.mycompany.lanyastarhotelreservation.util.DBConnection;
import com.mycompany.lanyastarhotelreservation.view.BookingForm;
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
                new BookingForm().setVisible(true);
            }
        });
    }
}
