/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.lanyastarhotelreservation.view;
import com.mycompany.lanyastarhotelreservation.model.Payment;
import com.DAO.PaymentDAO;
import javax.swing.JOptionPane;
import java.sql.*;
/**
 *
 * @author johnm
 */
public class CardPayment extends javax.swing.JFrame {
    private double totalAmount;
    private int bookingId; // Add bookingId field
    /**
     * Creates new form CardPayment
     */
    public CardPayment() {
        initComponents();
        this.totalAmount = 0;
        this.bookingId = 0;
        initializeForm();
    }
    
    public CardPayment(double totalAmount, int bookingId) {
        initComponents();
        this.totalAmount = totalAmount;
        this.bookingId = bookingId;
        initializeForm();
    }
    
    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }
    
    private void initializeForm() {
        // Add action listener to DONE button
        jBtnDone.addActionListener(e -> processPayment());
        
        // Optional: Add input formatters
        jTxtCardNumber.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!Character.isDigit(c)) {
                    evt.consume();
                }
            }
        });
        
        jTxtCCV.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!Character.isDigit(c)) {
                    evt.consume();
                }
            }
        });
    }
    
    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    private void processPayment() {
        try {
            // 1. Get card details from text fields
            String cardNumber = jTxtCardNumber.getText().trim();
            String ccv = jTxtCCV.getText().trim();
            
            // 2. Validate required fields
            if (cardNumber.isEmpty() || ccv.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Please fill in all card details", 
                    "Missing Information", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // 3. Check if total amount is set
            if (totalAmount <= 0) {
                JOptionPane.showMessageDialog(this, 
                    "Invalid payment amount", 
                    "Amount Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // 4. Check if bookingId is set
            if (bookingId <= 0) {
                JOptionPane.showMessageDialog(this, 
                    "Booking ID is missing", 
                    "Booking Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // 5. Create Payment object with bookingId
            Payment payment = new Payment(totalAmount, cardNumber, ccv, bookingId);
            
            // 6. Validate using Payment model
            String validationResult = payment.validate();
            
            if (!"VALID".equals(validationResult)) {
                JOptionPane.showMessageDialog(this, 
                    validationResult, 
                    "Card Validation Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // 7. Show processing message
            JOptionPane.showMessageDialog(this, 
                "Processing card payment...\nPlease wait.",
                "Processing", JOptionPane.INFORMATION_MESSAGE);
            
            // 8. Process payment using Payment model
            String paymentResult = payment.processPayment();
            
            if ("CARD_PAYMENT_SUCCESS".equals(paymentResult)) {
                // Save payment to database
                try {
                    PaymentDAO paymentDAO = new PaymentDAO();
                    int paymentId = paymentDAO.savePayment(payment);

                    // Show success message WITHOUT transaction ID
                    String message = String.format(
                        "✅ CARD PAYMENT SUCCESSFUL!\n\n" +
                        "Payment ID:        %d\n" +
                        "Amount Charged:    P %,.2f\n" +
                        "Card Used:         %s\n\n" +
                        "Thank you for your payment!\n" +
                        "A receipt has been sent to your email.",
                        paymentId, totalAmount, payment.getMaskedCardNumber()
                    );

                    JOptionPane.showMessageDialog(this, message, 
                        "Payment Approved", JOptionPane.INFORMATION_MESSAGE);

                    // Clear sensitive data
                    jTxtCardNumber.setText("");
                    jTxtExpiryDate.setText("");
                    jTxtCCV.setText("");

                    // Close window after successful payment
                    this.dispose();

                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to save payment record: " + e.getMessage(), 
                        "Database Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
                
            } else if ("CARD_PAYMENT_DECLINED".equals(paymentResult)) {
                JOptionPane.showMessageDialog(this, 
                    "❌ Card payment was declined.\n" +
                    "Please check your card details or try another card.",
                    "Payment Declined", JOptionPane.ERROR_MESSAGE);
                    
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Payment processing failed: " + paymentResult, 
                    "Payment Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "System Error: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    // Helper method to format card number with spaces
    private String formatCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() != 16) {
            return cardNumber;
        }
        return cardNumber.replaceAll("(.{4})", "$1 ").trim();
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTxtCardNumber = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTxtExpiryDate = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTxtCCV = new javax.swing.JTextField();
        jBtnDone = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        jLabel1.setText("CARD PAYMENT");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 64, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel2.setText("CARD INFORMATION");

        jLabel3.setText("CARD NUMBER");

        jLabel4.setText("EXPIRY DATE");

        jLabel5.setText("CCV");

        jBtnDone.setText("DONE");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jTxtCardNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jTxtExpiryDate, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
                                .addComponent(jTxtCCV, javax.swing.GroupLayout.Alignment.LEADING))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(129, 129, 129)
                        .addComponent(jBtnDone, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTxtCardNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTxtExpiryDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTxtCCV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBtnDone, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(CardPayment.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CardPayment.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CardPayment.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CardPayment.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CardPayment().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBtnDone;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField jTxtCCV;
    private javax.swing.JTextField jTxtCardNumber;
    private javax.swing.JTextField jTxtExpiryDate;
    // End of variables declaration//GEN-END:variables
}
