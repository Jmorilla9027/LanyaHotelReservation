/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.DAO;
import com.mycompany.lanyastarhotelreservation.util.DBConnection;
import com.mycompany.lanyastarhotelreservation.model.Payment;
import java.sql.*;
import java.time.LocalDateTime;

/**
 *
 * @author johnm
 */
public class PaymentDAO {
    
    public int savePayment(Payment payment) throws SQLException {
        String sql = "INSERT INTO payments (booking_id, total_amount, payment_method, " +
                    "payment_status, card_last_four, transaction_date) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, payment.getBookingId());
            stmt.setDouble(2, payment.getTotalAmount());
            stmt.setString(3, payment.getPaymentMethod());
            stmt.setString(4, payment.getPaymentStatus());
            stmt.setString(5, payment.getCardLastFour());
            
            // Handle transaction date
            LocalDateTime transactionDate = payment.getTransactionDate();
            if (transactionDate == null) {
                stmt.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
            } else {
                stmt.setTimestamp(6, Timestamp.valueOf(transactionDate));
            }
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating payment failed, no rows affected.");
            }
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int paymentId = rs.getInt(1);
                    payment.setPaymentId(paymentId);
                    return paymentId;
                } else {
                    throw new SQLException("Creating payment failed, no ID obtained.");
                }
            }
        }
    }
    
    public Payment getPaymentByBookingId(int bookingId) throws SQLException {
        String sql = "SELECT * FROM payments WHERE booking_id = ? ORDER BY transaction_date DESC LIMIT 1";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookingId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Payment payment = new Payment();
                payment.setPaymentId(rs.getInt("payment_id"));
                payment.setBookingId(rs.getInt("booking_id"));
                payment.setTotalAmount(rs.getDouble("total_amount"));
                payment.setPaymentMethod(rs.getString("payment_method"));
                payment.setPaymentStatus(rs.getString("payment_status"));
                payment.setCardLastFour(rs.getString("card_last_four"));
                
                Timestamp timestamp = rs.getTimestamp("transaction_date");
                if (timestamp != null) {
                    payment.setTransactionDate(timestamp.toLocalDateTime());
                }
                
                return payment;
            }
        }
        return null;
    }
    
    public boolean updatePaymentStatus(int paymentId, String status) throws SQLException {
        String sql = "UPDATE payments SET payment_status = ? WHERE payment_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setInt(2, paymentId);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }
}
