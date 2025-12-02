/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.lanyastarhotelreservation.view;
import com.mycompany.lanyastarhotelreservation.model.Booking;
import com.mycompany.lanyastarhotelreservation.model.Addon;
import com.mycompany.lanyastarhotelreservation.model.Services;
import com.mycompany.lanyastarhotelreservation.model.Room;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author johnm
 */
public class ReservationSummary extends javax.swing.JFrame {
    private Booking booking;
    private Room selectedRoom;
    private List<Addon> selectedAddons;
    private List<Services> selectedServices;
    private String destinationType;
    private String season;
    private int bookingId;
    private double finalAmount;
    private int roomQuantity;
    /**
     * Creates new form ReservationSummary
     */
    public ReservationSummary() {
        initComponents();
    
    jTblAddons.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {},
        new String [] {"NAME", "QUANTITY", "ORIGINAL PRICE", "DISCOUNT", "TOTAL"}
    ));

    jTblServices.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {},
        new String [] {"NAME", "DETAILS", "ORIGINAL PRICE", "DISCOUNT", "TOTAL"}
    ));

    jTblTotalAmount.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {},
        new String [] {"ITEM", "ORIGINAL PRICE", "DISCOUNT", "TOTAL"}
    ));
    
    // Also fix the discount tables
    jTblDiscAddons.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {},
        new String [] {"NAME", "QUANTITY", "DISCOUNT AMOUNT"}
    ));
    
    jTblDiscServices.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {},
        new String [] {"NAME", "QUANTITY", "DISCOUNT AMOUNT"}
    ));

    }
     // Method to set booking data
    public void setBookingData(Booking booking, Room room, List<Addon> addons, 
                              List<Services> services, String destinationType, 
                              String season, int bookingId, double finalAmount, int roomQuantity) {
        
        System.out.println("=== DEBUG: Setting Booking Data ===");
        System.out.println("Booking ID: " + bookingId);
        System.out.println("Final Amount: " + finalAmount);
        System.out.println("Room: " + (room != null ? room.getRoomType() : "null"));
        System.out.println("Room Quantity: " + roomQuantity);

        this.booking = booking;
        this.selectedRoom = room;
        this.selectedAddons = addons;
        this.selectedServices = services;
        this.destinationType = destinationType;
        this.season = season;
        this.bookingId = bookingId;
        this.finalAmount = finalAmount;
        this.roomQuantity = roomQuantity; // Store room quantity

        // Update all tables
        updateBookingInfoTable();
        updateRoomSummaryTable(); // This needs to use roomQuantity
        updateAddonsTable();
        updateServicesTable();
        updateTotalAmountTable();
    }

    
    private void updateBookingInfoTable() {
        DefaultTableModel model = (DefaultTableModel) jTblBookingFormSummary.getModel();
        model.setRowCount(0);
        
        if (booking != null) {
            model.addRow(new Object[]{
                bookingId,
                "Lead Guest", // You might want to add a name field to Booking model
                booking.getLeadGuestAge(),
                booking.getNumberOfAdults(),
                booking.getNumberOfChildren(),
                booking.getCheckInDate(),
                booking.getCheckOutDate()
            });
        }
    }
    
    private void updateRoomSummaryTable() {
        DefaultTableModel model = (DefaultTableModel) jTblRoomSummary.getModel();
        model.setRowCount(0);
        model.setColumnIdentifiers(new String[]{"Room Type", "Quantity", "Duration", "Price per Night", "Total Cost"});

        if (selectedRoom != null && booking != null) {
            double roomPrice = selectedRoom.getPrice(destinationType, season);
            double totalRoomCost = roomPrice * booking.calculateNights() * roomQuantity;

            model.addRow(new Object[]{
                selectedRoom.getRoomType(),
                roomQuantity + " room(s)",
                booking.calculateNights() + " night(s)",
                String.format("₱ %,.2f", roomPrice) + "/night",
                String.format("₱ %,.2f", totalRoomCost)
            });
        }
    }

    private void updateAddonsTable() {
        DefaultTableModel model = (DefaultTableModel) jTblAddons.getModel();
        model.setRowCount(0);

        DefaultTableModel discModel = (DefaultTableModel) jTblDiscAddons.getModel();
        discModel.setRowCount(0);

        if (selectedAddons != null) {
            for (Addon addon : selectedAddons) {
                if (addon.getQuantity() > 0) {
                    double totalPrice = addon.calculateTotalWithDiscount();
                    double originalPrice = addon.getOriginalTotal();
                    double discountAmount = addon.calculateDiscountAmount();

                    if (addon.getDiscountCount() > 0) {
                        // Add to discounted table
                        discModel.addRow(new Object[]{
                            addon.getName(),
                            addon.getDiscountCount() + " item(s)",
                            String.format("P %,.2f (20%% off)", discountAmount)
                        });
                    }

                    // Add to regular addons table
                    model.addRow(new Object[]{
                        addon.getName(),
                        addon.getQuantity() + " " + addon.getUnit(),
                        String.format("P %,.2f", originalPrice),
                        String.format("P %,.2f", discountAmount),
                        String.format("P %,.2f", totalPrice)
                    });
                }
            }
        }
    }

    private void updateServicesTable() {
        DefaultTableModel model = (DefaultTableModel) jTblServices.getModel();
        model.setRowCount(0);

        DefaultTableModel discModel = (DefaultTableModel) jTblDiscServices.getModel();
        discModel.setRowCount(0);

        if (selectedServices != null) {
            for (Services service : selectedServices) {
                if (service.getQuantity() > 0) {
                    double totalPrice = service.calculateTotalWithDiscount(booking.calculateNights());
                    double originalPrice = service.calculateOriginalTotal(booking.calculateNights());
                    double discountAmount = service.calculateDiscountAmount();

                    if (service.getDiscountCount() > 0) {
                        // Adjust discount amount for daily services
                        if (service.requiresDaysInput() && service.isDailyService()) {
                            discountAmount = discountAmount * booking.calculateNights();
                        }

                        // Add to discounted table
                        discModel.addRow(new Object[]{
                            service.getName(),
                            service.getDiscountCount() + " guest(s)",
                            String.format("P %,.2f (20%% off)", discountAmount)
                        });
                    }

                    // Add to regular services table
                    model.addRow(new Object[]{
                        service.getName(),
                        service.getQuantityDescription(),
                        String.format("P %,.2f", originalPrice),
                        String.format("P %,.2f", discountAmount),
                        String.format("P %,.2f", totalPrice)
                    });
                }
            }
        }
    }
    
    private void updateDiscountTables() {
        // Already handled in updateAddonsTable and updateServicesTable
    }
        
    private void updateTotalAmountTable() {
        DefaultTableModel model = (DefaultTableModel) jTblTotalAmount.getModel();
        model.setRowCount(0);

        // Define the columns first
        model.setColumnIdentifiers(new String[]{"Item", "Original Price", "Discount", "Total"});

        double roomTotal = 0;
        double addonsTotal = 0;
        double servicesTotal = 0;
        double discountTotal = 0;

        // Calculate room total with quantity
        if (selectedRoom != null) {
            roomTotal = selectedRoom.getPrice(destinationType, season) * booking.calculateNights() * roomQuantity;
            model.addRow(new Object[]{
                "Room: " + selectedRoom.getRoomType() + " × " + roomQuantity,
                String.format("₱ %,.2f", roomTotal),
                "₱ 0.00",
                String.format("₱ %,.2f", roomTotal)
            });
        }

        // Calculate addons total
        if (selectedAddons != null) {
            for (Addon addon : selectedAddons) {
                if (addon.getQuantity() > 0) {
                    double addonPrice = addon.calculateTotalWithDiscount();
                    double addonOriginal = addon.getOriginalTotal();
                    double addonDiscount = addon.calculateDiscountAmount();

                    addonsTotal += addonPrice;
                    discountTotal += addonDiscount;

                    model.addRow(new Object[]{
                        "Addon: " + addon.getName(),
                        String.format("P %,.2f", addonOriginal),
                        String.format("P %,.2f", addonDiscount),
                        String.format("P %,.2f", addonPrice)
                    });
                }
            }
        }

        // Calculate services total
        if (selectedServices != null) {
            for (Services service : selectedServices) {
                if (service.getQuantity() > 0) {
                    double servicePrice = service.calculateTotalWithDiscount(booking.calculateNights());
                    double serviceOriginal = service.calculateOriginalTotal(booking.calculateNights());
                    double serviceDiscount = service.calculateDiscountAmount();

                    // Adjust discount for daily services
                    if (service.requiresDaysInput() && service.isDailyService()) {
                        serviceDiscount = serviceDiscount * booking.calculateNights();
                    }

                    servicesTotal += servicePrice;
                    discountTotal += serviceDiscount;

                    model.addRow(new Object[]{
                        "Service: " + service.getName(),
                        String.format("P %,.2f", serviceOriginal),
                        String.format("P %,.2f", serviceDiscount),
                        String.format("P %,.2f", servicePrice)
                    });
                }
            }
        }

        // Calculate grand total (without VAT)
        double grandTotal = roomTotal + addonsTotal + servicesTotal;

        // Add grand total
        model.addRow(new Object[]{"", "", "", ""});
        model.addRow(new Object[]{
            "GRAND TOTAL",
            "",
            String.format("P %,.2f", discountTotal),
            String.format("P %,.2f", grandTotal)
        });

        // Store the final amount (without VAT)
        this.finalAmount = grandTotal;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTable5 = new javax.swing.JTable();
        jScrollPane8 = new javax.swing.JScrollPane();
        jTblAddons1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane9 = new javax.swing.JScrollPane();
        jTblBookingFormSummary = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTblAddons = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTblServices = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTblDiscAddons = new javax.swing.JTable();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTblDiscServices = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTblTotalAmount = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane10 = new javax.swing.JScrollPane();
        jTblRoomSummary = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jBtnCard = new javax.swing.JButton();
        jBtnCash = new javax.swing.JButton();

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "NAME", "QUANTITY"
            }
        ));
        jScrollPane2.setViewportView(jTable2);

        jTable5.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "NAME", "QUANTITY"
            }
        ));
        jScrollPane5.setViewportView(jTable5);

        jTblAddons1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "NAME", "QUANTITY", "PRICE"
            }
        ));
        jScrollPane8.setViewportView(jTblAddons1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        jLabel1.setText("LANYA STAR HOTEL");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 782, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
                .addGap(10, 10, 10))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel2.setText("LEAD GUEST INFO");

        jTblBookingFormSummary.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "BOOKING ID", "NAME", "AGE", "NO. ADULTS", "NO. CHILDREN", "CHECK IN DATE", "CHECK OUT DATE"
            }
        ));
        jScrollPane9.setViewportView(jTblBookingFormSummary);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane9))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jTblAddons.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "NAME", "QUANTITY", "PRICE"
            }
        ));
        jScrollPane1.setViewportView(jTblAddons);

        jTblServices.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "NAME", "QUANTITY", "PRICE"
            }
        ));
        jScrollPane3.setViewportView(jTblServices);

        jTblDiscAddons.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "NAME", "QUANTITY", "PRICE"
            }
        ));
        jScrollPane4.setViewportView(jTblDiscAddons);

        jTblDiscServices.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "NAME", "QUANTITY", "PRICE"
            }
        ));
        jScrollPane6.setViewportView(jTblDiscServices);

        jLabel3.setText("ADD-ONS AVAILED ");

        jLabel4.setText("SERVICES AVAILED ");

        jLabel5.setText("DISCOUNTED SERVICES ");

        jLabel6.setText("DISCOUNTED ADD-ONS");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 358, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 377, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addGap(17, 17, 17))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 368, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel5)
                            .addComponent(jScrollPane6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 377, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jTblTotalAmount.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "NAME", "PRICE", "DISC. PRICE", "TOTAL"
            }
        ));
        jScrollPane7.setViewportView(jTblTotalAmount);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 402, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel7.setText("ROOM SUMMARY");

        jTblRoomSummary.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "NAME", "QUANTITY", "PRICE"
            }
        ));
        jScrollPane10.setViewportView(jTblRoomSummary);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 565, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel7)
                .addContainerGap(79, Short.MAX_VALUE))
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addGap(32, 32, 32)
                    .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel8.setText("PAYMENT METHOD");

        jBtnCard.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jBtnCard.setText("CARD");
        jBtnCard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnCardActionPerformed(evt);
            }
        });

        jBtnCash.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jBtnCash.setText("CASH");
        jBtnCash.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnCashActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jBtnCash, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jBtnCard, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(90, 90, 90)
                .addComponent(jLabel8)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jBtnCard, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBtnCash, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jBtnCashActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnCashActionPerformed
        // TODO add your handling code here:
    if (finalAmount <= 0) {
        javax.swing.JOptionPane.showMessageDialog(this, 
            "Cannot process payment - total amount is zero or invalid",
            "Payment Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    try {
        CashPayment cashPayment = new CashPayment(finalAmount, bookingId); // Pass bookingId
        cashPayment.setLocationRelativeTo(this);
        cashPayment.setVisible(true);
    } catch (Exception e) {
        e.printStackTrace();
        javax.swing.JOptionPane.showMessageDialog(this, 
            "Error opening cash payment: " + e.getMessage(),
            "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_jBtnCashActionPerformed

    private void jBtnCardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnCardActionPerformed
        // TODO add your handling code here:
    if (finalAmount <= 0) {
        javax.swing.JOptionPane.showMessageDialog(this, 
            "Cannot process payment - total amount is zero or invalid",
            "Payment Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    try {
        CardPayment cardPayment = new CardPayment(finalAmount, bookingId); // Pass bookingId
        cardPayment.setLocationRelativeTo(this);
        cardPayment.setVisible(true);
    } catch (Exception e) {
        e.printStackTrace();
        javax.swing.JOptionPane.showMessageDialog(this, 
            "Error opening card payment: " + e.getMessage(),
            "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_jBtnCardActionPerformed

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
            java.util.logging.Logger.getLogger(ReservationSummary.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ReservationSummary.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ReservationSummary.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ReservationSummary.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ReservationSummary().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBtnCard;
    private javax.swing.JButton jBtnCash;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable5;
    private javax.swing.JTable jTblAddons;
    private javax.swing.JTable jTblAddons1;
    private javax.swing.JTable jTblBookingFormSummary;
    private javax.swing.JTable jTblDiscAddons;
    private javax.swing.JTable jTblDiscServices;
    private javax.swing.JTable jTblRoomSummary;
    private javax.swing.JTable jTblServices;
    private javax.swing.JTable jTblTotalAmount;
    // End of variables declaration//GEN-END:variables
}
