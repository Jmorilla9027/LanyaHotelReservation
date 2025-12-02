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
        String sql = "INSERT INTO payments (booking_id, total_amount, vat_amount, final_amount, " +
                    "payment_method, payment_status, card_last_four, transaction_date, transaction_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, payment.getBookingId());
            stmt.setDouble(2, payment.getTotalAmount());
            stmt.setDouble(3, payment.getVatAmount());
            stmt.setDouble(4, payment.getFinalAmount());
            stmt.setString(5, payment.getPaymentMethod());
            stmt.setString(6, payment.getPaymentStatus());
            stmt.setString(7, payment.getCardLastFour());
            
            // Handle transaction date - use current timestamp if null
            LocalDateTime transactionDate = payment.getTransactionDate();
            if (transactionDate == null) {
                stmt.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
            } else {
                stmt.setTimestamp(8, Timestamp.valueOf(transactionDate));
            }
            
            stmt.setString(9, payment.getTransactionId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating payment failed, no rows affected.");
            }
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int paymentId = rs.getInt(1);
                    payment.setPaymentId(paymentId);
                    return paymentId; // Return payment_id
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
                payment.setVatAmount(rs.getDouble("vat_amount"));
                payment.setFinalAmount(rs.getDouble("final_amount"));
                payment.setPaymentMethod(rs.getString("payment_method"));
                payment.setPaymentStatus(rs.getString("payment_status"));
                payment.setCardLastFour(rs.getString("card_last_four"));
                
                Timestamp timestamp = rs.getTimestamp("transaction_date");
                if (timestamp != null) {
                    payment.setTransactionDate(timestamp.toLocalDateTime());
                }
                
                payment.setTransactionId(rs.getString("transaction_id"));
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
    
    public boolean updatePaymentWithTransaction(int paymentId, String transactionId, String status) throws SQLException {
        String sql = "UPDATE payments SET transaction_id = ?, payment_status = ? WHERE payment_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, transactionId);
            stmt.setString(2, status);
            stmt.setInt(3, paymentId);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }
}
