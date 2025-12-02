/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.lanyastarhotelreservation.view;
import com.mycompany.lanyastarhotelreservation.model.Addon;
import com.mycompany.lanyastarhotelreservation.model.Services;
import com.mycompany.lanyastarhotelreservation.model.Booking;
import com.mycompany.lanyastarhotelreservation.model.Room;
import com.DAO.BookingDAO;
import com.DAO.RoomDAO;
import com.DAO.AddonDAO;
import com.DAO.ServicesDAO;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import java.sql.*;
import java.time.LocalDate;

/**
 *
 * @author johnm
 */
public class RoomSelectionForm extends javax.swing.JFrame {
    private List<Addon> addons;
    private List<Services> services;
    private AddonDAO addonDAO;
    private ServicesDAO servicesDAO;
    private int totalGuests;
    private int nightsStay;
    private int numAdults; 
    private Booking booking;
    private String destinationType;
    private String season;
    private RoomDAO roomDAO;
    private Room selectedRoom; // ADD THIS LINE
    /**
     * Creates new form RoomSelectionForm
     */
    public RoomSelectionForm() {
        initComponents();
        this.roomDAO = new RoomDAO();
        this.addonDAO = new AddonDAO();
        this.servicesDAO = new ServicesDAO();
        initializeModels();
        initializeForm();
    }
    private void initializeModels() {
        // Load addons from database
        addons = addonDAO.getAllAddons();
        
        // Load services from database
        services = servicesDAO.getAllServices();
        
        // Debug to see what's loaded
        System.out.println("Loaded " + addons.size() + " addons from database");
        System.out.println("Loaded " + services.size() + " services from database");
    }

    private void initializeForm() {
        cmbAvailAddons.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--Select--", "Yes", "No" }));
        cmbAvailServices.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--Select--", "Yes", "No" }));

        AddonPanel.setVisible(false);
        ServicesPanel.setVisible(false);
        
        // Update labels based on loaded data
        updateAddonLabels();
        updateServiceLabels();
        
        cmbRoomType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { 
            "--Select Room Type--", "Standard", "Deluxe", "Quadruple", "Family", "Suite" 
        }));
    }
    
        private void updateAddonLabels() {
        if (!addons.isEmpty()) {
            // Find each addon and update its label
            for (Addon addon : addons) {
                switch (addon.getName()) {
                    case "Bed":
                        jLabel6.setText(String.format("Php %.2f/%s", addon.getRate(), addon.getUnit()));
                        break;
                    case "Blanket":
                        jLabel7.setText(String.format("Php %.2f", addon.getRate()));
                        break;
                    case "Pillows":
                        jLabel8.setText(String.format("Php %.2f", addon.getRate()));
                        break;
                    case "Toiletries":
                        jLabel9.setText(String.format("Php %.2f/%s", addon.getRate(), addon.getUnit()));
                        break;
                }
            }
        }
    }

    private void updateServiceLabels() {
        if (!services.isEmpty()) {
            for (Services service : services) {
                switch (service.getName()) {
                    case "Swimming Pool":
                        jLabel14.setText(String.format("Php %.2f %s", service.getRate(), service.getPricingUnit()));
                        break;
                    case "Gym":
                        jLabel15.setText(String.format("Php %.2f %s", service.getRate(), service.getPricingUnit()));
                        break;
                    case "Foot Spa":
                        jLabel17.setText(String.format("Php %.2f for %s", service.getRate(), service.getDuration()));
                        break;
                    case "Aroma Facial Massage":
                        jLabel18.setText(String.format("Php %.2f for %s", service.getRate(), service.getDuration()));
                        break;
                    case "Thai Massage":
                        jLabel19.setText(String.format("Php %.2f for %s", service.getRate(), service.getDuration()));
                        break;
                }
            }
        }
    }
     
    public void setBooking(Booking booking) {
        this.booking = booking;
        this.totalGuests = booking.calculatePayingGuests();
        this.nightsStay = booking.calculateNights();
        this.numAdults = booking.getNumberOfAdults();

        // Add null check for destinationType
        this.destinationType = booking.getDestinationType();
        if (this.destinationType == null) {
            System.err.println("ERROR: destinationType is null from booking object");
            this.destinationType = "Local"; // Default to Local
        }

        // Use booking's season instead of calculating separately
        this.season = booking.getSeason();
        if (this.season == null) {
            this.season = "Lean"; // Default
        }

        updateValidationLimits();
        loadRoomsFromDatabase();
    }
    
    private int calculateRoomsNeeded(Room room, int totalGuests) {
        if (room.getCapacity() >= totalGuests) {
            return 1;
        }
        return (int) Math.ceil((double) totalGuests / room.getCapacity());
    }
    private void loadRoomsFromDatabase() {
        List<Room> availableRooms = roomDAO.getAvailableRooms(destinationType, season, totalGuests);

        // DEBUG: See what rooms are being returned
        System.out.println("=== ROOM FILTERING DEBUG ===");
        System.out.println("Total Guests: " + totalGuests);
        System.out.println("Available Rooms Found: " + availableRooms.size());
        for (Room room : availableRooms) {
            System.out.println("Room: " + room.getRoomType() + 
                              " | Capacity: " + room.getCapacity() + 
                              " + " + room.getExtraBedCount() + " extra beds = " + 
                              (room.getCapacity() + room.getExtraBedCount()));
        }

        updateRoomTable(availableRooms);
        updateRoomTypeComboBox(availableRooms);
        updateSeasonInfo();
    }

    private void updateRoomTable(List<Room> availableRooms) {
        Object[][] tableData = new Object[availableRooms.size()][6];

        for (int i = 0; i < availableRooms.size(); i++) {
            Room room = availableRooms.get(i);
            double currentPrice = room.getPrice(destinationType, season);

            int roomsNeededForAllGuests = calculateRoomsNeeded(room, totalGuests);
            boolean hasEnoughRooms = roomsNeededForAllGuests <= room.getAvailableRooms();

            String capacityDesc = room.getCapacity() + " guest" + (room.getCapacity() > 1 ? "s" : "");
            if (room.getExtraBedCount() > 0) {
                capacityDesc += " + " + room.getExtraBedCount() + " extra bed" + (room.getExtraBedCount() > 1 ? "s" : "");
            }

            String status;
            String recommendation;

            if (room.getCapacity() >= totalGuests) {
                if (room.getAvailableRooms() > 0) {
                    status = "Perfect Fit";
                    recommendation = "1 room needed";
                } else {
                    status = "No Rooms Available";
                    recommendation = "Sold out";
                }
            } else {
                if (hasEnoughRooms) {
                    status = "Perfect Fit (" + roomsNeededForAllGuests + " rooms)";
                    recommendation = roomsNeededForAllGuests + " rooms needed";
                } else {
                    status = "Insufficient Rooms";
                    recommendation = "Need " + roomsNeededForAllGuests + " rooms, only " + room.getAvailableRooms() + " available";
                }
            }

            tableData[i][0] = room.getRoomType();
            tableData[i][1] = capacityDesc;
            tableData[i][2] = room.getAvailableRooms();
            tableData[i][3] = String.format("₱ %,.2f", currentPrice);
            tableData[i][4] = status;
            tableData[i][5] = recommendation;
        }

        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(
            tableData,
            new String[] {"Room Type", "Capacity", "Available", "Price (" + season + ")", "Status", "Recommendation"}
        ) {
            Class[] types = new Class[] {
                java.lang.String.class, java.lang.String.class, 
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        jTable1.setModel(model);

        // Auto-select the first perfect fit room if available
        if (!availableRooms.isEmpty() && jTable1.getRowCount() > 0) {
            // Find first perfect fit room
            int perfectFitRow = -1;
            for (int i = 0; i < availableRooms.size(); i++) {
                Room room = availableRooms.get(i);
                int roomsNeeded = calculateRoomsNeeded(room, totalGuests);
                if (roomsNeeded <= room.getAvailableRooms()) {
                    perfectFitRow = i;
                    break;
                }
            }

            // Select the perfect fit room, or first available room
            int rowToSelect = (perfectFitRow != -1) ? perfectFitRow : 0;
            jTable1.setRowSelectionInterval(rowToSelect, rowToSelect);
            String selectedRoomType = (String) jTable1.getValueAt(rowToSelect, 0);
            cmbRoomType.setSelectedItem(selectedRoomType);

            // Store the selected room
            selectedRoom = availableRooms.get(rowToSelect);

            // Update room quantity combo box
            updateRoomQuantityComboBox(selectedRoom);

            // Show recommendation message
            if (perfectFitRow != -1) {
                showRecommendedRoom(availableRooms.get(perfectFitRow));
            }
        }
    }
    private void updateRoomQuantityComboBox(Room selectedRoom) {
        if (selectedRoom == null) {
            // Try to get the room from the selected room type
            String selectedRoomType = (String) cmbRoomType.getSelectedItem();
            if (selectedRoomType == null || selectedRoomType.equals("--Select Room Type--")) {
                return;
            }
            selectedRoom = roomDAO.getRoomByType(selectedRoomType, destinationType);
            if (selectedRoom == null) return;
        }

        int roomsNeeded = calculateRoomsNeeded(selectedRoom, totalGuests);
        int maxRooms = Math.min(selectedRoom.getAvailableRooms(), roomsNeeded + 5);

        List<String> quantities = new ArrayList<>();
        quantities.add("--Select Quantity--");

        for (int i = roomsNeeded; i <= maxRooms; i++) {
            quantities.add(String.valueOf(i));
        }

        cmbSelectRoomQuantity.setModel(new javax.swing.DefaultComboBoxModel<>(
            quantities.toArray(new String[0])
        ));

        if (roomsNeeded <= maxRooms) {
            cmbSelectRoomQuantity.setSelectedItem(String.valueOf(roomsNeeded));
        }
    }
    
    private void showRecommendedRoom(Room recommendedRoom) {
        int roomsNeeded = calculateRoomsNeeded(recommendedRoom, totalGuests);
        double pricePerRoom = recommendedRoom.getPrice(destinationType, season);
        double totalPricePerNight = pricePerRoom * roomsNeeded;

        String message;
        if (roomsNeeded == 1) {
            message = String.format(
                "RECOMMENDED: %s Room\n\n" +
                "• Perfect for %d guests (1 room)\n" +
                "• Price: ₱ %,.2f per night\n" +
                "• Season: %s\n" +
                "• Available rooms: %d",
                recommendedRoom.getRoomType(),
                totalGuests,
                pricePerRoom,
                season,
                recommendedRoom.getAvailableRooms()
            );
        } else {
            message = String.format(
                "RECOMMENDED: %d × %s Rooms\n\n" +
                "• %d guests = %d rooms × %d guests each\n" +
                "• Price: ₱ %,.2f per room\n" +
                "• Total: ₱ %,.2f per night\n" +
                "• Season: %s\n" +
                "• Available rooms: %d",
                roomsNeeded,
                recommendedRoom.getRoomType(),
                totalGuests,
                roomsNeeded,
                recommendedRoom.getCapacity(),
                pricePerRoom,
                totalPricePerNight,
                season,
                recommendedRoom.getAvailableRooms()
            );
        }

        System.out.println("RECOMMENDATION: " + message);
    }
    
    private String determineSeason(LocalDate checkInDate) {
        if (checkInDate == null) return "Lean";
        int month = checkInDate.getMonthValue();
        if (month >= 3 && month <= 5) return "Lean";
        else if (month >= 6 && month <= 8) return "High";
        else if (month >= 9 && month <= 11) return "Peak";
        else return "Super Peak";
    }

    private void updateRoomTypeComboBox(List<Room> availableRooms) {
        List<String> roomTypes = new ArrayList<>();
        roomTypes.add("--Select Room Type--");
        for (Room room : availableRooms) {
            roomTypes.add(room.getRoomType());
        }
        cmbRoomType.setModel(new javax.swing.DefaultComboBoxModel<>(roomTypes.toArray(new String[0])));
    }

    private void updateSeasonInfo() {
        System.out.println("Destination: " + destinationType + " | Season: " + season + 
                          " | Guests: " + totalGuests + " | Nights: " + nightsStay);
    }
    
    
    private void updateValidationLimits() {
        // For services that need max quantity validation
        for (Services service : services) {
            if (service.requiresDaysInput()) {
                // For daily services, max quantity is guests × nights
                service.setMaxQuantity(totalGuests * nightsStay);
            } else {
                // For spa services, max quantity is total guests
                service.setMaxQuantity(totalGuests);
            }
        }
    }
    public boolean validateForm() {
        List<String> errors = new ArrayList<>();

        // Validate room selection
        if (cmbRoomType.getSelectedIndex() == 0) {
            errors.add("Please select a room type");
        }

        // Validate room quantity selection
        if (cmbSelectRoomQuantity.getSelectedIndex() == 0) {
            errors.add("Please select number of rooms");
        } else {
            String selectedQuantity = (String) cmbSelectRoomQuantity.getSelectedItem();
            int roomQuantity = Integer.parseInt(selectedQuantity);

            // Get selected room
            String selectedRoomType = (String) cmbRoomType.getSelectedItem();
            Room selectedRoom = roomDAO.getRoomByType(selectedRoomType, destinationType);

            if (selectedRoom != null) {
                int roomsNeeded = calculateRoomsNeeded(selectedRoom, totalGuests);
                if (roomQuantity < roomsNeeded) {
                    errors.add("Need at least " + roomsNeeded + " rooms for " + totalGuests + " guests");
                }
                if (roomQuantity > selectedRoom.getAvailableRooms()) {
                    errors.add("Only " + selectedRoom.getAvailableRooms() + " " + 
                              selectedRoom.getRoomType() + " rooms available for " + destinationType);
                }
            }
        }

        // Validate quantities are entered for selected items
        String quantityValidation = validateQuantitiesNotEmpty();
        if (!"VALID".equals(quantityValidation)) {
            errors.add(quantityValidation);
        }

        // Validate addons if panel is visible
        if (AddonPanel.isVisible()) {
            String addonValidation = validateAddons();
            if (!"VALID".equals(addonValidation)) {
                errors.add(addonValidation);
            }

            String addonDiscountValidation = validateAddonDiscounts();
            if (!"VALID".equals(addonDiscountValidation)) {
                errors.add(addonDiscountValidation);
            }
        }

        // Validate services if panel is visible
        if (ServicesPanel.isVisible()) {
            String serviceValidation = validateServices();
            if (!"VALID".equals(serviceValidation)) {
                errors.add(serviceValidation);
            }

            String serviceDiscountValidation = validateServiceDiscounts();
            if (!"VALID".equals(serviceDiscountValidation)) {
                errors.add(serviceDiscountValidation);
            }
        }

        if (!errors.isEmpty()) {
            showValidationErrors(errors);
            return false;
        }

        return true;
    }
    //New Validation for discounts
    private String validateAddonDiscounts() {
        StringBuilder errors = new StringBuilder();
        List<Addon> selectedAddons = getSelectedAddons();

        for (Addon addon : selectedAddons) {
            String validation = addon.validateDiscount(numAdults);
            if (!"VALID".equals(validation)) {
                if (errors.length() > 0) errors.append("\n");
                errors.append(validation);
            }
        }

        return errors.length() == 0 ? "VALID" : errors.toString();
    }

    // NEW: Validate service discounts
    private String validateServiceDiscounts() {
        StringBuilder errors = new StringBuilder();
        List<Services> selectedServices = getSelectedServices();

        for (Services service : selectedServices) {
            String validation = service.validateDiscount(numAdults);
            if (!"VALID".equals(validation)) {
                if (errors.length() > 0) errors.append("\n");
                errors.append(validation);
            }
        }

        return errors.length() == 0 ? "VALID" : errors.toString();
    }
    
    private String validateAddons() {
        StringBuilder errors = new StringBuilder();
        List<Addon> selectedAddons = getSelectedAddons();

        for (Addon addon : selectedAddons) {
            String validation = addon.validateQuantity(totalGuests, nightsStay);
            if (!"VALID".equals(validation)) {
                if (errors.length() > 0) errors.append("\n");
                errors.append(validation);
            }

            // Additional validation for bed limits
            if ("Bed".equals(addon.getName())) {
                if (addon.getQuantity() > totalGuests) {
                    if (errors.length() > 0) errors.append("\n");
                    errors.append("Number of beds cannot exceed number of paying guests (" + totalGuests + ")");
                }
            }
        }
        return errors.length() == 0 ? "VALID" : errors.toString();
    }
    
    private String validateQuantitiesNotEmpty() {
        StringBuilder errors = new StringBuilder();

        // Check addons
        if (AddonPanel.isVisible()) {
            if (cbBed.isSelected() && txtBedQuantity.getText().trim().isEmpty()) {
                errors.append("Please enter quantity for Bed\n");
            }
            if (cbBlanket.isSelected() && txtBlanketQuantity.getText().trim().isEmpty()) {
                errors.append("Please enter quantity for Blanket\n");
            }
            if (cbPillows.isSelected() && txtPillowsQuantity.getText().trim().isEmpty()) {
                errors.append("Please enter quantity for Pillows\n");
            }
            if (cbToiletries.isSelected() && txtToiletriesQuantity.getText().trim().isEmpty()) {
                errors.append("Please enter quantity for Toiletries\n");
            }
        }

        // Check services
        if (ServicesPanel.isVisible()) {
            if (cbSwimmingPool.isSelected() && txtSPQuantity.getText().trim().isEmpty()) {
                errors.append("Please enter quantity for Swimming Pool\n");
            }
            if (cbGym.isSelected() && txtGymQuantity.getText().trim().isEmpty()) {
                errors.append("Please enter quantity for Gym\n");
            }
            if (cbFootSpa.isSelected() && txtFSQuantity.getText().trim().isEmpty()) {
                errors.append("Please enter quantity for Foot Spa\n");
            }
            if (cbAFMassage.isSelected() && txtAFMQuantity.getText().trim().isEmpty()) {
                errors.append("Please enter quantity for Aroma Facial Massage\n");
            }
            if (cbThaiMassage.isSelected() && txtTMQuantity.getText().trim().isEmpty()) {
                errors.append("Please enter quantity for Thai Massage\n");
            }
        }

        return errors.length() == 0 ? "VALID" : errors.toString();
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

            // Additional validation for daily services - days shouldn't exceed nights stay
            if (service.requiresDaysInput()) {
                // For daily services, quantity represents guests, not days
                int serviceDays = nightsStay; // Assuming they want it for all days
                if (serviceDays > nightsStay) {
                    if (errors.length() > 0) errors.append("\n");
                    errors.append(service.getName() + " cannot be availed for more days than your stay (" + nightsStay + " nights)");
                }
            }
        }
        return errors.length() == 0 ? "VALID" : errors.toString();
    }
    
    private int getServiceDays(Services service) {
        if (service.requiresDaysInput()) {
        }
        return 1; // For spa services, it's per session (not daily)
    }
    
    private void showValidationErrors(List<String> errors) {
        String errorMessage = String.join("\n", errors);
        JOptionPane.showMessageDialog(this, errorMessage, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }
    


    
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
    
    
    private void openSummaryForm() {
        // Implementation for opening summary form
        JOptionPane.showMessageDialog(this, "Proceeding to summary...");
    }
    
 public List<Addon> getSelectedAddons() {
    List<Addon> selectedAddons = new ArrayList<>();

    if (cbBed.isSelected() && !txtBedQuantity.getText().isEmpty()) {
        Addon bed = findAddonByName("Bed");
        if (bed != null) {
            bed.setSelected(true);
            bed.setQuantity(Integer.parseInt(txtBedQuantity.getText()));
            bed.setDiscountCountFromTextField(txtDiscountBed.getText());
            selectedAddons.add(bed);
        }
    }

    if (cbBlanket.isSelected() && !txtBlanketQuantity.getText().isEmpty()) {
        Addon blanket = findAddonByName("Blanket");
        if (blanket != null) {
            blanket.setSelected(true);
            blanket.setQuantity(Integer.parseInt(txtBlanketQuantity.getText()));
            blanket.setDiscountCountFromTextField(txtDiscountBlanket.getText());
            selectedAddons.add(blanket);
        }
    }

    if (cbPillows.isSelected() && !txtPillowsQuantity.getText().isEmpty()) {
        Addon pillows = findAddonByName("Pillows");
        if (pillows != null) {
            pillows.setSelected(true);
            pillows.setQuantity(Integer.parseInt(txtPillowsQuantity.getText()));
            pillows.setDiscountCountFromTextField(txtDiscountPillow.getText());
            selectedAddons.add(pillows);
        }
    }

    if (cbToiletries.isSelected() && !txtToiletriesQuantity.getText().isEmpty()) {
        Addon toiletries = findAddonByName("Toiletries");
        if (toiletries != null) {
            toiletries.setSelected(true);
            toiletries.setQuantity(Integer.parseInt(txtToiletriesQuantity.getText()));
            toiletries.setDiscountCountFromTextField(txtDiscountToiletry.getText());
            selectedAddons.add(toiletries);
        }
    }

    return selectedAddons;
}

    public List<Services> getSelectedServices() {
        List<Services> selectedServices = new ArrayList<>();

        if (cbSwimmingPool.isSelected() && !txtSPQuantity.getText().isEmpty()) {
            Services pool = findServiceByName("Swimming Pool");
            if (pool != null) {
                pool.setSelected(true);
                pool.setQuantity(Integer.parseInt(txtSPQuantity.getText()));
                pool.setDiscountCountFromTextField(txtDiscountPool.getText());
                selectedServices.add(pool);
            }
        }

        if (cbGym.isSelected() && !txtGymQuantity.getText().isEmpty()) {
            Services gym = findServiceByName("Gym");
            if (gym != null) {
                gym.setSelected(true);
                gym.setQuantity(Integer.parseInt(txtGymQuantity.getText()));
                gym.setDiscountCountFromTextField(txtDiscountGym.getText());
                selectedServices.add(gym);
            }
        }

        if (cbFootSpa.isSelected() && !txtFSQuantity.getText().isEmpty()) {
            Services footSpa = findServiceByName("Foot Spa");
            if (footSpa != null) {
                footSpa.setSelected(true);
                footSpa.setQuantity(Integer.parseInt(txtFSQuantity.getText()));
                footSpa.setDiscountCountFromTextField(txtDiscountFSpa.getText());
                selectedServices.add(footSpa);
            }
        }

        if (cbAFMassage.isSelected() && !txtAFMQuantity.getText().isEmpty()) {
            Services aromaMassage = findServiceByName("Aroma Facial Massage");
            if (aromaMassage != null) {
                aromaMassage.setSelected(true);
                aromaMassage.setQuantity(Integer.parseInt(txtAFMQuantity.getText()));
                aromaMassage.setDiscountCountFromTextField(txtFMassage.getText());
                selectedServices.add(aromaMassage);
            }
        }

        if (cbThaiMassage.isSelected() && !txtTMQuantity.getText().isEmpty()) {
            Services thaiMassage = findServiceByName("Thai Massage");
            if (thaiMassage != null) {
                thaiMassage.setSelected(true);
                thaiMassage.setQuantity(Integer.parseInt(txtTMQuantity.getText()));
                thaiMassage.setDiscountCountFromTextField(txtTMassage.getText());
                selectedServices.add(thaiMassage);
            }
        }

        return selectedServices;
    }

    // Helper method to find addon by name
    private Addon findAddonByName(String name) {
        for (Addon addon : addons) {
            if (addon.getName().equals(name)) {
                return addon;
            }
        }
        return null;
    }

    // Helper method to find service by name
    private Services findServiceByName(String name) {
        for (Services service : services) {
            if (service.getName().equals(name)) {
                return service;
            }
        }
        return null;
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
        jLabel23 = new javax.swing.JLabel();
        cmbSelectRoomQuantity = new javax.swing.JComboBox<>();
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
        jLabel21 = new javax.swing.JLabel();
        txtDiscountBed = new javax.swing.JTextField();
        txtDiscountBlanket = new javax.swing.JTextField();
        txtDiscountPillow = new javax.swing.JTextField();
        txtDiscountToiletry = new javax.swing.JTextField();
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
        jLabel22 = new javax.swing.JLabel();
        txtDiscountPool = new javax.swing.JTextField();
        txtDiscountGym = new javax.swing.JTextField();
        txtDiscountFSpa = new javax.swing.JTextField();
        txtFMassage = new javax.swing.JTextField();
        txtTMassage = new javax.swing.JTextField();
        btnNext = new javax.swing.JButton();

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

        jLabel23.setText("Select Room Quantity");

        cmbSelectRoomQuantity.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbSelectRoomQuantity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbSelectRoomQuantityActionPerformed(evt);
            }
        });

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 68, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cmbAvailServices, 0, 189, Short.MAX_VALUE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel23)
                    .addComponent(cmbSelectRoomQuantity, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel23))
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbRoomType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbSelectRoomQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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

        jLabel21.setText("# of PWD/Senior Citizen");

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
                .addGap(42, 42, 42)
                .addGroup(AddonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(jLabel9)
                    .addComponent(jLabel8)
                    .addComponent(jLabel7)
                    .addComponent(jLabel5))
                .addGap(18, 18, 18)
                .addGroup(AddonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(AddonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(txtToiletriesQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtBlanketQuantity, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtPillowsQuantity, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtBedQuantity, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel10))
                .addGap(18, 18, 18)
                .addGroup(AddonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel21)
                    .addGroup(AddonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(txtDiscountToiletry, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
                        .addComponent(txtDiscountPillow, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(txtDiscountBlanket, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(txtDiscountBed, javax.swing.GroupLayout.Alignment.LEADING)))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        AddonPanelLayout.setVerticalGroup(
            AddonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AddonPanelLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(AddonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(AddonPanelLayout.createSequentialGroup()
                        .addGroup(AddonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(jLabel21))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(AddonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtBedQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDiscountBed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(AddonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtBlanketQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDiscountBlanket, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(AddonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtPillowsQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDiscountPillow, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(AddonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtToiletriesQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDiscountToiletry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(AddonPanelLayout.createSequentialGroup()
                        .addGroup(AddonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(AddonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cbBed)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(AddonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cbBlanket)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(AddonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cbPillows)
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(AddonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cbToiletries)
                            .addComponent(jLabel9))))
                .addContainerGap(43, Short.MAX_VALUE))
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

        jLabel22.setText("# of PWD/Senior Citizen");

        txtDiscountGym.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDiscountGymActionPerformed(evt);
            }
        });

        txtTMassage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTMassageActionPerformed(evt);
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
                    .addComponent(jLabel13)
                    .addComponent(jLabel18)
                    .addComponent(jLabel19))
                .addGap(10, 10, 10)
                .addGroup(ServicesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ServicesPanelLayout.createSequentialGroup()
                        .addGroup(ServicesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtAFMQuantity)
                            .addComponent(txtTMQuantity, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtSPQuantity, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                            .addComponent(txtGymQuantity)
                            .addComponent(txtFSQuantity))
                        .addGap(18, 18, 18)
                        .addGroup(ServicesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(ServicesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(txtDiscountPool)
                                .addComponent(txtDiscountGym)
                                .addComponent(txtFMassage)
                                .addComponent(txtTMassage, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtDiscountFSpa, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())
                    .addGroup(ServicesPanelLayout.createSequentialGroup()
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel22)
                        .addGap(18, 18, 18))))
        );
        ServicesPanelLayout.setVerticalGroup(
            ServicesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ServicesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ServicesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel22, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(ServicesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel11)
                        .addComponent(jLabel13)
                        .addComponent(jLabel20)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ServicesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(ServicesPanelLayout.createSequentialGroup()
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
                            .addComponent(txtTMQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(ServicesPanelLayout.createSequentialGroup()
                        .addComponent(txtDiscountPool, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDiscountGym, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addComponent(txtDiscountFSpa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFMassage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTMassage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(15, 15, 15))
        );

        btnNext.setText("NEXT");
        btnNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextActionPerformed(evt);
            }
        });

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
                            .addComponent(AddonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ServicesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnNext, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(192, 192, 192)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(AddonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(ServicesPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(36, 36, 36)
                        .addComponent(btnNext, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
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
    String selectedRoomType = (String) cmbRoomType.getSelectedItem();
    if (selectedRoomType != null && !selectedRoomType.equals("--Select Room Type--")) {
        selectedRoom = roomDAO.getRoomByType(selectedRoomType, destinationType);
        if (selectedRoom != null) {
            updateRoomQuantityComboBox(selectedRoom);
        }
    }
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

    private void txtDiscountGymActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDiscountGymActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDiscountGymActionPerformed

    private void txtTMassageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTMassageActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTMassageActionPerformed

    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
        // TODO add your handling code here:
    if (validateForm()) {
        try {
            String selectedRoomType = (String) cmbRoomType.getSelectedItem();
            String quantityStr = (String) cmbSelectRoomQuantity.getSelectedItem();
            int roomQuantity = Integer.parseInt(quantityStr);
            
            if (selectedRoomType == null || selectedRoomType.equals("--Select Room Type--")) {
                JOptionPane.showMessageDialog(this, "Please select a room type", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Make sure booking has season calculated before proceeding
            if (booking.getSeason() == null) {
                booking.setSeason(booking.calculateSeason());
            }
            
            // Update room availability with quantity
            boolean roomUpdated = roomDAO.updateRoomAvailability(selectedRoomType, destinationType, roomQuantity);
            if (!roomUpdated) {
                JOptionPane.showMessageDialog(this, 
                    "Not enough rooms available. Please choose another option.",
                    "Rooms Not Available", JOptionPane.WARNING_MESSAGE);
                loadRoomsFromDatabase();
                return;
            }
            
            // Get room details for confirmation
            Room selectedRoom = roomDAO.getRoomByType(selectedRoomType, destinationType);
            
            if (selectedRoom == null) {
                JOptionPane.showMessageDialog(this, 
                    "Room not found. Please select another room.",
                    "Room Not Found", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Save booking (season will be calculated and saved in saveToDatabase method)
            BookingDAO bookingDAO = new BookingDAO();
            int bookingId = bookingDAO.saveBooking(booking);
            
            // Save addons and services
            List<Addon> selectedAddons = getSelectedAddons();
            List<Services> selectedServices = getSelectedServices();
            
            if (!selectedAddons.isEmpty()) {
                bookingDAO.saveBookingAddons(bookingId, selectedAddons);
            }
            if (!selectedServices.isEmpty()) {
                bookingDAO.saveBookingServices(bookingId, selectedServices);
            }
            
            // Open the reservation summary form with room quantity
            openReservationSummary(booking, selectedRoom, selectedAddons, 
                      selectedServices, destinationType, bookingId, roomQuantity);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Database Error: " + e.getMessage(), 
                "Save Failed", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Invalid room quantity selected", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    }//GEN-LAST:event_btnNextActionPerformed
    private void openReservationSummary(Booking booking, Room room, List<Addon> addons, 
                                       List<Services> services, String destType, 
                                       int bookingId, int roomQuantity) {
        try {
            ReservationSummary summaryForm = new ReservationSummary();

            String season = booking.getSeason();

            // Calculate final amount with room quantity
            double finalAmount = calculateFinalAmount(room, addons, services, destType, season, booking, roomQuantity);

            // Pass all data including the calculated amount and room quantity
            summaryForm.setBookingData(booking, room, addons, services, 
                                      destType, season, bookingId, finalAmount, roomQuantity);

            summaryForm.setLocationRelativeTo(null);
            summaryForm.setVisible(true);

            this.dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error opening reservation summary: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }


    // Helper method to calculate final amount (NO VAT)
    private double calculateFinalAmount(Room room, List<Addon> addons, 
                                      List<Services> services, String destType, 
                                      String season, Booking booking, int roomQuantity) {

       // Calculate room total with quantity
       double roomTotal = room.getPrice(destType, season) * booking.calculateNights() * roomQuantity;

       double addonsTotal = 0;
       if (addons != null) {
           for (Addon addon : addons) {
               if (addon.isSelected() && addon.getQuantity() > 0) {
                   addonsTotal += addon.calculateTotalWithDiscount();
               }
           }
       }

       double servicesTotal = 0;
       if (services != null) {
           for (Services service : services) {
               if (service.isSelected() && service.getQuantity() > 0) {
                   servicesTotal += service.calculateTotalWithDiscount(booking.calculateNights());
               }
           }
       }

       // Calculate final amount (NO VAT)
       double finalAmount = roomTotal + addonsTotal + servicesTotal;

       return finalAmount;
   }
    private void cmbSelectRoomQuantityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbSelectRoomQuantityActionPerformed
        // TODO add your handling code here:
    String selectedRoomType = (String) cmbRoomType.getSelectedItem();
    if (selectedRoomType != null && !selectedRoomType.equals("--Select Room Type--")) {
        selectedRoom = roomDAO.getRoomByType(selectedRoomType, destinationType);
    }
    }//GEN-LAST:event_cmbSelectRoomQuantityActionPerformed

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
    private javax.swing.JButton btnNext;
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
    private javax.swing.JComboBox<String> cmbSelectRoomQuantity;
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
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
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
    private javax.swing.JTextField txtDiscountBed;
    private javax.swing.JTextField txtDiscountBlanket;
    private javax.swing.JTextField txtDiscountFSpa;
    private javax.swing.JTextField txtDiscountGym;
    private javax.swing.JTextField txtDiscountPillow;
    private javax.swing.JTextField txtDiscountPool;
    private javax.swing.JTextField txtDiscountToiletry;
    private javax.swing.JTextField txtFMassage;
    private javax.swing.JTextField txtFSQuantity;
    private javax.swing.JTextField txtGymQuantity;
    private javax.swing.JTextField txtPillowsQuantity;
    private javax.swing.JTextField txtSPQuantity;
    private javax.swing.JTextField txtTMQuantity;
    private javax.swing.JTextField txtTMassage;
    private javax.swing.JTextField txtToiletriesQuantity;
    // End of variables declaration//GEN-END:variables
}
