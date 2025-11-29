/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.lanyastarhotelreservation.view;
import com.mycompany.lanyastarhotelreservation.model.Addon;
import com.mycompany.lanyastarhotelreservation.model.Services;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
/**
 *
 * @author johnm
 */
public class RoomSelectionForm extends javax.swing.JFrame {
    private List<Addon> addons;
    private List<Services> services;
    private int totalGuests;
    private int nightsStay;
    /**
     * Creates new form RoomSelectionForm
     */
    public RoomSelectionForm() {
        initComponents();
        initializeModels();
        initializeForm();
    }
    private void initializeModels() {
        // Initialize addons
        addons = new ArrayList<>();
        addons.add(new Addon("Bed", 650.00, "Extra bed per night"));
        addons.add(new Addon("Blanket", 250.00, "Additional blanket"));
        addons.add(new Addon("Pillows", 100.00, "Extra pillows"));
        addons.add(new Addon("Toiletries", 200.00, "Toiletries set"));

        // Initialize services
        services = new ArrayList<>();
        services.add(new Services("Swimming Pool", 300.00, "Daily access", "per day per person"));
        services.add(new Services("Gym", 500.00, "Fitness center access", "per day per person"));
        services.add(new Services("Foot Spa", 825.00, "Relaxing foot treatment", "45 minutes"));
        services.add(new Services("Aroma Facial Massage", 1045.00, "Aromatherapy facial massage", "45 minutes"));
        services.add(new Services("Thai Massage", 1540.00, "Traditional Thai massage", "75 minutes"));
    }

     private void initializeForm() {
        cmbAvailAddons.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--Select--", "Yes", "No" }));
        cmbAvailServices.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--Select--", "Yes", "No" }));

        AddonPanel.setVisible(false);
        ServicesPanel.setVisible(false);
        jLabel8.setText("Php 100.00");

        cmbRoomType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { 
            "--Select Room Type--", "Standard", "Deluxe", "Quadruple", "Family", "Suite" 
        }));
    }
     
    public void setBookingDetails(int totalGuests, int nightsStay) {
        this.totalGuests = totalGuests;
        this.nightsStay = nightsStay;
        updateValidationLimits();
    }
    
    private void updateValidationLimits() {
        // Services already have their max quantity calculation
        for (Services service : services) {
            service.calculateMaxQuantity(totalGuests, nightsStay);
        }
        // Addons will be validated in validateAddonQuantity method
    }
    // Validation methods that delegate to model
    public boolean validateForm() {
        List<String> errors = new ArrayList<>();
        
        // Validate room selection
        if (cmbRoomType.getSelectedIndex() == 0) {
            errors.add("Please select a room type");
        }
        
        // Validate addons if panel is visible
        if (AddonPanel.isVisible()) {
            String addonValidation = validateAddons();
            if (!"VALID".equals(addonValidation)) {
                errors.add(addonValidation);
            }
        }
        
        // Validate services if panel is visible
        if (ServicesPanel.isVisible()) {
            String serviceValidation = validateServices();
            if (!"VALID".equals(serviceValidation)) {
                errors.add(serviceValidation);
            }
        }
        
        if (!errors.isEmpty()) {
            showValidationErrors(errors);
            return false;
        }
        
        return true;
    }
    
    private String validateAddons() {
        StringBuilder errors = new StringBuilder();
        List<Addon> selectedAddons = getSelectedAddons();

        for (Addon addon : selectedAddons) {
            String validation = validateAddonQuantity(addon);
            if (!"VALID".equals(validation)) {
                if (errors.length() > 0) errors.append("\n");
                errors.append(validation);
            }
        }

        return errors.length() == 0 ? "VALID" : errors.toString();
    }
    
    private String validateAddonQuantity(Addon addon) {
       int quantity = addon.getQuantity();
       String name = addon.getName();

       if (quantity < 0) {
           return name + " quantity cannot be negative";
       }

       if (quantity == 0) {
           return "VALID";
       }

       // Custom validation based on addon type
       switch (name) {
           case "Bed":
               if (quantity > totalGuests) {
                   return "Number of beds cannot exceed number of guests (" + totalGuests + ")";
               }
               break;
           case "Blanket":
               if (quantity > totalGuests * 2) {
                   return "Maximum 2 blankets per guest allowed (" + totalGuests + " guests)";
               }
               break;
           case "Pillows":
               if (quantity > totalGuests * 4) {
                   return "Maximum 4 pillows per guest allowed (" + totalGuests + " guests)";
               }
               break;
           case "Toiletries":
               if (quantity > totalGuests * 2) {
                   return "Maximum 2 toiletries sets per guest allowed (" + totalGuests + " guests)";
               }
               break;
       }

       return "VALID";
   }

    private String validateServices() {
        StringBuilder errors = new StringBuilder();
        List<Services> selectedServices = getSelectedServices();

        for (Services service : selectedServices) {
            int daysAvailed = getServiceDays(service);
            String validation = service.validateQuantity(totalGuests, daysAvailed, nightsStay);
            if (!"VALID".equals(validation)) {
                if (errors.length() > 0) errors.append("\n");
                errors.append(validation);
            }
        }

        return errors.length() == 0 ? "VALID" : errors.toString();
    }
    
    private int getServiceDays(Services service) {
        // For daily services, you might want to add day input fields
        // For now, assuming they want the service for all days
        if (service.requiresDaysInput()) {
            return nightsStay; // Default to all days
        }
        return 0;
    }
    
    private void showValidationErrors(List<String> errors) {
        String errorMessage = String.join("\n", errors);
        JOptionPane.showMessageDialog(this, errorMessage, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }
    
    // Existing methods remain the same...


    
    private void clearAddonSelections() {
        cbBed.setSelected(false);
        cbBlanket.setSelected(false);
        cbPillows.setSelected(false);
        cbToiletries.setSelected(false);
        txtBedQuantity.setText("");
        txtBlanketQuantity.setText("");
        txtPillowsQuantity.setText("");
        txtToiletriesQuantity.setText("");
    }
    
    private void clearServiceSelections() {
        cbSwimmingPool.setSelected(false);
        cbGym.setSelected(false);
        cbFootSpa.setSelected(false);
        cbAFMassage.setSelected(false);
        cbThaiMassage.setSelected(false);
        txtSPQuantity.setText("");
        txtGymQuantity.setText("");
        txtFSQuantity.setText("");
        txtAFMQuantity.setText("");
        txtTMQuantity.setText("");
    }
    
    // Add a Next button handler (you'll need to add this button to your form)
    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {
        if (validateForm()) {
            // Proceed to next form (e.g., SummaryForm)
            openSummaryForm();
        }
    }
    
    private void openSummaryForm() {
        // Implementation for opening summary form
        JOptionPane.showMessageDialog(this, "Proceeding to summary...");
    }
    
    // Rest of your existing methods (getSelectedAddons, getSelectedServices, etc.)
    public List<Addon> getSelectedAddons() {
        List<Addon> selectedAddons = new ArrayList<>();
        
        if (cbBed.isSelected() && !txtBedQuantity.getText().isEmpty()) {
            Addon bed = addons.get(0);
            bed.setSelected(true);
            bed.setQuantity(Integer.parseInt(txtBedQuantity.getText()));
            selectedAddons.add(bed);
        }
        
        if (cbBlanket.isSelected() && !txtBlanketQuantity.getText().isEmpty()) {
            Addon blanket = addons.get(1);
            blanket.setSelected(true);
            blanket.setQuantity(Integer.parseInt(txtBlanketQuantity.getText()));
            selectedAddons.add(blanket);
        }
        
        if (cbPillows.isSelected() && !txtPillowsQuantity.getText().isEmpty()) {
            Addon pillows = addons.get(2);
            pillows.setSelected(true);
            pillows.setQuantity(Integer.parseInt(txtPillowsQuantity.getText()));
            selectedAddons.add(pillows);
        }
        
        if (cbToiletries.isSelected() && !txtToiletriesQuantity.getText().isEmpty()) {
            Addon toiletries = addons.get(3);
            toiletries.setSelected(true);
            toiletries.setQuantity(Integer.parseInt(txtToiletriesQuantity.getText()));
            selectedAddons.add(toiletries);
        }
        
        return selectedAddons;
    }

    public List<Services> getSelectedServices() {
        List<Services> selectedServices = new ArrayList<>();

        if (cbSwimmingPool.isSelected() && !txtSPQuantity.getText().isEmpty()) {
            Services pool = services.get(0);
            pool.setSelected(true);
            pool.setQuantity(Integer.parseInt(txtSPQuantity.getText()));
            selectedServices.add(pool);
        }

        if (cbGym.isSelected() && !txtGymQuantity.getText().isEmpty()) {
            Services gym = services.get(1);
            gym.setSelected(true);
            gym.setQuantity(Integer.parseInt(txtGymQuantity.getText()));
            selectedServices.add(gym);
        }

        if (cbFootSpa.isSelected() && !txtFSQuantity.getText().isEmpty()) {
            Services footSpa = services.get(2);
            footSpa.setSelected(true);
            footSpa.setQuantity(Integer.parseInt(txtFSQuantity.getText()));
            selectedServices.add(footSpa);
        }

        if (cbAFMassage.isSelected() && !txtAFMQuantity.getText().isEmpty()) {
            Services aromaMassage = services.get(3);
            aromaMassage.setSelected(true);
            aromaMassage.setQuantity(Integer.parseInt(txtAFMQuantity.getText()));
            selectedServices.add(aromaMassage);
        }

        if (cbThaiMassage.isSelected() && !txtTMQuantity.getText().isEmpty()) {
            Services thaiMassage = services.get(4);
            thaiMassage.setSelected(true);
            thaiMassage.setQuantity(Integer.parseInt(txtTMQuantity.getText()));
            selectedServices.add(thaiMassage);
        }

        return selectedServices;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        cmbRoomType = new javax.swing.JComboBox<>();
        cmbAvailAddons = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        cmbAvailServices = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        AddonPanel = new javax.swing.JPanel();
        cbBed = new javax.swing.JCheckBox();
        jLabel4 = new javax.swing.JLabel();
        cbBlanket = new javax.swing.JCheckBox();
        cbPillows = new javax.swing.JCheckBox();
        cbToiletries = new javax.swing.JCheckBox();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtBedQuantity = new javax.swing.JTextField();
        txtBlanketQuantity = new javax.swing.JTextField();
        txtPillowsQuantity = new javax.swing.JTextField();
        txtToiletriesQuantity = new javax.swing.JTextField();
        ServicesPanel = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        cbSwimmingPool = new javax.swing.JCheckBox();
        cbGym = new javax.swing.JCheckBox();
        jLabel12 = new javax.swing.JLabel();
        cbFootSpa = new javax.swing.JCheckBox();
        cbAFMassage = new javax.swing.JCheckBox();
        cbThaiMassage = new javax.swing.JCheckBox();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        txtSPQuantity = new javax.swing.JTextField();
        txtGymQuantity = new javax.swing.JTextField();
        txtFSQuantity = new javax.swing.JTextField();
        txtAFMQuantity = new javax.swing.JTextField();
        txtTMQuantity = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTable1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Standard",  new Integer(1),  new Integer(1),  new Integer(5),  new Integer(2000),  new Integer(4000),  new Integer(6000),  new Integer(9000)},
                {"Deluxe",  new Integer(2),  new Integer(1),  new Integer(4),  new Integer(3000),  new Integer(5000),  new Integer(8000),  new Integer(12000)},
                {"Qaudruple",  new Integer(4),  new Integer(1),  new Integer(5),  new Integer(4000),  new Integer(7000),  new Integer(10000),  new Integer(15000)},
                {"Family",  new Integer(6),  new Integer(1),  new Integer(3),  new Integer(5000),  new Integer(9000),  new Integer(12000),  new Integer(18000)},
                {"Suite",  new Integer(4),  new Integer(0),  new Integer(2),  new Integer(6000),  new Integer(11000),  new Integer(14000),  new Integer(21000)}
            },
            new String [] {
                "Room Type", "Capacity", "Extra Bed", "Available Rooms", "Lean", "High", "Peak", "Super Peak"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Room Details"));

        cmbRoomType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--ROOM TYPE--", "Standard", "Quadruple", "Family", "Suite" }));
        cmbRoomType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbRoomTypeActionPerformed(evt);
            }
        });

        cmbAvailAddons.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--Yes Or No--", "Yes", "No" }));
        cmbAvailAddons.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbAvailAddonsActionPerformed(evt);
            }
        });

        jLabel1.setText("Select Room Type");

        jLabel2.setText("Avail Addons?");

        cmbAvailServices.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--Yes Or No--", "Yes", "No" }));
        cmbAvailServices.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbAvailServicesActionPerformed(evt);
            }
        });

        jLabel3.setText("Avail Amenities/Services?");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cmbRoomType, 0, 189, Short.MAX_VALUE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(cmbAvailAddons, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbAvailServices, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(12, 12, 12)
                .addComponent(cmbRoomType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbAvailAddons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbAvailServices, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        AddonPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        cbBed.setText("Bed");

        jLabel4.setText("Add-ons");

        cbBlanket.setText("Blanket");

        cbPillows.setText("Pillows");

        cbToiletries.setText("Toiletries");

        jLabel5.setText("Rate");

        jLabel6.setText("Php 650.00/night ");

        jLabel7.setText("Php 250.00");

        jLabel8.setText("jLabel8");

        jLabel9.setText("Php 200.00/set");

        jLabel10.setText("Quantity");

        javax.swing.GroupLayout AddonPanelLayout = new javax.swing.GroupLayout(AddonPanel);
        AddonPanel.setLayout(AddonPanelLayout);
        AddonPanelLayout.setHorizontalGroup(
            AddonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AddonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(AddonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(cbBed)
                    .addComponent(cbBlanket)
                    .addComponent(cbPillows)
                    .addComponent(cbToiletries))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 71, Short.MAX_VALUE)
                .addGroup(AddonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(jLabel9)
                    .addComponent(jLabel8)
                    .addComponent(jLabel7)
                    .addGroup(AddonPanelLayout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(70, 70, 70)))
                .addGroup(AddonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(AddonPanelLayout.createSequentialGroup()
                        .addGap(61, 61, 61)
                        .addComponent(jLabel10))
                    .addGroup(AddonPanelLayout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addComponent(txtToiletriesQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AddonPanelLayout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addGroup(AddonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtBlanketQuantity, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPillowsQuantity, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtBedQuantity, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(45, 45, 45))
        );
        AddonPanelLayout.setVerticalGroup(
            AddonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AddonPanelLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(AddonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(AddonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbBed)
                    .addComponent(jLabel6)
                    .addComponent(txtBedQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(AddonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbBlanket)
                    .addComponent(jLabel7)
                    .addComponent(txtBlanketQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(AddonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbPillows)
                    .addComponent(jLabel8)
                    .addComponent(txtPillowsQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(AddonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbToiletries)
                    .addComponent(jLabel9)
                    .addComponent(txtToiletriesQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        ServicesPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel11.setText("Amenities/Services");

        cbSwimmingPool.setText("Swimming Pool ");

        cbGym.setText("Gym");

        jLabel12.setText("SPA");

        cbFootSpa.setText("Foot Spa");

        cbAFMassage.setText("Aroma Facial Massage");

        cbThaiMassage.setText(" Thai Massage ");

        jLabel13.setText("Fees");

        jLabel14.setText("Php 300.00/day per person");

        jLabel15.setText("Php 500.00/day per person");

        jLabel16.setText("Rates per person");

        jLabel17.setText("Php 825.00 for 45 minutes");

        jLabel18.setText("Php 1,045.00 for 45 minutes");

        jLabel19.setText("Php 1,540.00 for 75 minutes ");

        jLabel20.setText("Quantity");

        txtTMQuantity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTMQuantityActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ServicesPanelLayout = new javax.swing.GroupLayout(ServicesPanel);
        ServicesPanel.setLayout(ServicesPanelLayout);
        ServicesPanelLayout.setHorizontalGroup(
            ServicesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ServicesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ServicesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11)
                    .addComponent(cbSwimmingPool)
                    .addComponent(cbGym)
                    .addComponent(jLabel12)
                    .addGroup(ServicesPanelLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(ServicesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbAFMassage)
                            .addComponent(cbFootSpa)
                            .addComponent(cbThaiMassage))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(ServicesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel17)
                    .addComponent(jLabel16)
                    .addComponent(jLabel15)
                    .addComponent(jLabel14)
                    .addGroup(ServicesPanelLayout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addGap(125, 125, 125))
                    .addComponent(jLabel18)
                    .addComponent(jLabel19))
                .addGap(10, 10, 10)
                .addGroup(ServicesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtSPQuantity, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 74, Short.MAX_VALUE)
                    .addComponent(txtGymQuantity, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ServicesPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel20))
                    .addComponent(txtFSQuantity, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtAFMQuantity, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtTMQuantity))
                .addContainerGap())
        );
        ServicesPanelLayout.setVerticalGroup(
            ServicesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ServicesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ServicesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jLabel13)
                    .addComponent(jLabel20))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ServicesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbSwimmingPool)
                    .addComponent(jLabel14)
                    .addComponent(txtSPQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ServicesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbGym)
                    .addComponent(jLabel15)
                    .addComponent(txtGymQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ServicesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ServicesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbFootSpa)
                    .addComponent(jLabel17)
                    .addComponent(txtFSQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ServicesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbAFMassage)
                    .addComponent(jLabel18)
                    .addComponent(txtAFMQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ServicesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbThaiMassage)
                    .addComponent(jLabel19)
                    .addComponent(txtTMQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(AddonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ServicesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(AddonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(ServicesPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(119, 119, 119)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cmbAvailAddonsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbAvailAddonsActionPerformed
     String selection = (String) cmbAvailAddons.getSelectedItem();
        AddonPanel.setVisible("Yes".equals(selection));
        if (!"Yes".equals(selection)) {
            clearAddonSelections();
        }
    }//GEN-LAST:event_cmbAvailAddonsActionPerformed

    private void cmbRoomTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbRoomTypeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbRoomTypeActionPerformed
    // Validation method for addons

    private void txtTMQuantityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTMQuantityActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTMQuantityActionPerformed

    private void cmbAvailServicesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbAvailServicesActionPerformed
        String selection = (String) cmbAvailServices.getSelectedItem();
        ServicesPanel.setVisible("Yes".equals(selection));
        if (!"Yes".equals(selection)) {
            clearServiceSelections();
        }
    }//GEN-LAST:event_cmbAvailServicesActionPerformed

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
            java.util.logging.Logger.getLogger(RoomSelectionForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RoomSelectionForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RoomSelectionForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RoomSelectionForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new RoomSelectionForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel AddonPanel;
    private javax.swing.JPanel ServicesPanel;
    private javax.swing.JCheckBox cbAFMassage;
    private javax.swing.JCheckBox cbBed;
    private javax.swing.JCheckBox cbBlanket;
    private javax.swing.JCheckBox cbFootSpa;
    private javax.swing.JCheckBox cbGym;
    private javax.swing.JCheckBox cbPillows;
    private javax.swing.JCheckBox cbSwimmingPool;
    private javax.swing.JCheckBox cbThaiMassage;
    private javax.swing.JCheckBox cbToiletries;
    private javax.swing.JComboBox<String> cmbAvailAddons;
    private javax.swing.JComboBox<String> cmbAvailServices;
    private javax.swing.JComboBox<String> cmbRoomType;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField txtAFMQuantity;
    private javax.swing.JTextField txtBedQuantity;
    private javax.swing.JTextField txtBlanketQuantity;
    private javax.swing.JTextField txtFSQuantity;
    private javax.swing.JTextField txtGymQuantity;
    private javax.swing.JTextField txtPillowsQuantity;
    private javax.swing.JTextField txtSPQuantity;
    private javax.swing.JTextField txtTMQuantity;
    private javax.swing.JTextField txtToiletriesQuantity;
    // End of variables declaration//GEN-END:variables
}
