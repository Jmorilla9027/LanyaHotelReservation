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
public class CashPayment extends javax.swing.JFrame {
    private double totalAmount;
    private int bookingId; // Add bookingId field
    
    public CashPayment() {
        initComponents();
        this.bookingId = 0;
        initializeForm();
    }
    public CashPayment(double totalAmount, int bookingId) {
        initComponents();
        this.totalAmount = totalAmount;
        this.bookingId = bookingId;
        initializeForm();
    }
    
    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }
    
    private void initializeForm() {
        // Set total amount in the text field
        if (totalAmount > 0) {
            jTxtCashToPay.setText(String.format("P %,.2f", totalAmount));
        }
        jTxtCashToPay.setEditable(false);
        
        // Add action listener to DONE button
        jBtnDone.addActionListener(e -> processPayment());
    }
    
    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
        if (jTxtCashToPay != null) {
            jTxtCashToPay.setText(String.format("P %,.2f", totalAmount));
        }
    }
    
    private void processPayment() {
        try {
            // 1. Get cash amount from text field
            String cashStr = jTxtCashPayment.getText().trim();
            
            // Check if empty
            if (cashStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter cash amount", 
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // 2. Parse cash amount
            cashStr = cashStr.replace("P", "").replace(",", "").trim();
            double cashReceived = Double.parseDouble(cashStr);
            
            // 3. Get total amount from field or stored value
            double amountToPay = totalAmount;
            if (amountToPay <= 0 && jTxtCashToPay.getText() != null) {
                String totalStr = jTxtCashToPay.getText().replace("P", "").replace(",", "").trim();
                amountToPay = Double.parseDouble(totalStr);
            }
            
            // 4. Check if bookingId is set
            if (bookingId <= 0) {
                JOptionPane.showMessageDialog(this, 
                    "Booking ID is missing", 
                    "Booking Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // 5. Create Payment object with bookingId
            Payment payment = new Payment(amountToPay, cashReceived, bookingId);
            
            // 6. Validate using Payment model
            String validationResult = payment.validate();
            
            if (!"VALID".equals(validationResult)) {
                JOptionPane.showMessageDialog(this, 
                    validationResult, 
                    "Payment Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // 7. Process payment using Payment model
            String paymentResult = payment.processPayment();
            
            if ("CASH_PAYMENT_SUCCESS".equals(paymentResult)) {
                // 8. Save payment to database
                try {
                    PaymentDAO paymentDAO = new PaymentDAO();
                    int paymentId = paymentDAO.savePayment(payment);
                    
                    // 9. Calculate change using Payment model
                    double change = payment.calculateChange();
                    
                    // 10. Show success message
                    String message = String.format(
                        "✅ CASH PAYMENT SUCCESSFUL!\n\n" +
                        "Payment ID:        %d\n" +
                        "Total Amount:     P %,.2f\n" +
                        "Cash Received:    P %,.2f\n" +
                        "Change Due:       P %,.2f\n" +
                        "Transaction ID:   %s\n\n" +
                        "Thank you for your payment!",
                        paymentId, amountToPay, cashReceived, change, payment.getTransactionId()
                    );
                    
                    JOptionPane.showMessageDialog(this, message, 
                        "Payment Complete", JOptionPane.INFORMATION_MESSAGE);
                    
                    // 11. Clear cash amount field
                    jTxtCashPayment.setText("");
                    
                    // 12. Ask if user wants to close window
                    int option = JOptionPane.showConfirmDialog(this,
                        "Close payment window?",
                        "Payment Complete", JOptionPane.YES_NO_OPTION);
                    
                    if (option == JOptionPane.YES_OPTION) {
                        this.dispose();
                    }
                    
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to save payment record: " + e.getMessage(), 
                        "Database Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
                
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Payment failed: " + paymentResult, 
                    "Payment Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Please enter valid numbers only", 
                "Invalid Input", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error: " + e.getMessage(), 
                "System Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTxtCashToPay = new javax.swing.JTextField();
        jBtnDone = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jTxtCashPayment = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel2.setText("CARD INFORMATION");

        jLabel3.setText("TOTAL AMOUNT");

        jBtnDone.setText("DONE");

        jLabel4.setText("CASH AMOUNT");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jTxtCashToPay, javax.swing.GroupLayout.DEFAULT_SIZE, 256, Short.MAX_VALUE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(129, 129, 129)
                        .addComponent(jBtnDone, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel4))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jTxtCashPayment)))
                .addContainerGap(122, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTxtCashToPay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTxtCashPayment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50)
                .addComponent(jBtnDone, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        jLabel1.setText("CARD PAYMENT");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 372, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(21, Short.MAX_VALUE))
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
            java.util.logging.Logger.getLogger(CashPayment.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CashPayment.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CashPayment.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CashPayment.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CashPayment().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBtnDone;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField jTxtCashPayment;
    private javax.swing.JTextField jTxtCashToPay;
    // End of variables declaration//GEN-END:variables
}
