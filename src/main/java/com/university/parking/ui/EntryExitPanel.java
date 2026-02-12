package com.university.parking.ui;

import com.university.parking.domain.*;
import com.university.parking.service.FineService;
import com.university.parking.service.ParkingService;
import com.university.parking.service.PaymentService;
import com.university.parking.ui.components.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Panel for vehicle entry and exit operations with modern UI design.
 */
public class EntryExitPanel extends JPanel {
    
    private ParkingService parkingService;
    private PaymentService paymentService;
    private FineService fineService;
    
    // Entry components
    private JPanel vehicleEntryPanel;
    private StyledTextField entryLicensePlateField;
    private StyledComboBox<VehicleType> vehicleTypeComboBox;
    private StyledTable availableSpotsTable;
    private DefaultTableModel spotsTableModel;
    private StyledButton parkVehicleButton;
    private StyledButton refreshSpotsButton;
    
    // Exit components
    private JPanel vehicleExitPanel;
    private StyledTextField exitLicensePlateField;
    private StyledButton findVehicleButton;
    private JLabel hoursLabel;
    private JLabel parkingFeeLabel;
    private JLabel finesLabel;
    private JLabel totalLabel;
    private JCheckBox payFinesCheckBox;
    private StyledComboBox<PaymentMethod> paymentMethodComboBox;
    private StyledButton payExitButton;
    
    // Exit state
    private Vehicle currentExitVehicle;
    private ParkingSpot currentExitSpot;
    private double currentParkingFee;
    private double currentFineAmount;
    
    public EntryExitPanel() {
        initializePanel();
        initializeComponents();
        layoutComponents();
    }
    
    public EntryExitPanel(ParkingService parkingService, PaymentService paymentService, FineService fineService) {
        this.parkingService = parkingService;
        this.paymentService = paymentService;
        this.fineService = fineService;
        initializePanel();
        initializeComponents();
        layoutComponents();
    }
    
    private void initializePanel() {
        setLayout(new GridLayout(1, 2, UIConstants.SPACING_LG, 0));
        setBackground(UIConstants.BG_MAIN);
        setOpaque(false);
    }
    
    private void initializeComponents() {
        vehicleEntryPanel = createVehicleEntryPanel();
        vehicleExitPanel = createVehicleExitPanel();
    }

    private JPanel createVehicleEntryPanel() {
        CardPanel card = new CardPanel("Vehicle Entry");
        card.setContentLayout(new BorderLayout(0, UIConstants.SPACING_MD));
        JPanel content = card.getContentPanel();
        
        // Input section
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, UIConstants.SPACING_MD, UIConstants.SPACING_MD);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // License plate
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        JLabel plateLabel = new JLabel("License Plate");
        plateLabel.setFont(UIConstants.BODY_BOLD);
        plateLabel.setForeground(UIConstants.TEXT_PRIMARY);
        inputPanel.add(plateLabel, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1;
        entryLicensePlateField = new StyledTextField("Enter license plate");
        inputPanel.add(entryLicensePlateField, gbc);
        
        // Vehicle type
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        JLabel typeLabel = new JLabel("Vehicle Type");
        typeLabel.setFont(UIConstants.BODY_BOLD);
        typeLabel.setForeground(UIConstants.TEXT_PRIMARY);
        inputPanel.add(typeLabel, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1;
        vehicleTypeComboBox = new StyledComboBox<>(VehicleType.values());
        vehicleTypeComboBox.setSelectedIndex(-1);
        vehicleTypeComboBox.addActionListener(e -> refreshAvailableSpots());
        inputPanel.add(vehicleTypeComboBox, gbc);
        
        // Refresh button
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.insets = new Insets(UIConstants.SPACING_SM, 0, 0, 0);
        refreshSpotsButton = new StyledButton("Refresh Available Spots", StyledButton.ButtonType.SECONDARY);
        refreshSpotsButton.addActionListener(e -> refreshAvailableSpots());
        inputPanel.add(refreshSpotsButton, gbc);
        
        content.add(inputPanel, BorderLayout.NORTH);
        
        // Table section
        String[] columns = {"Spot ID", "Type", "Floor", "Rate (RM/hr)"};
        spotsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        availableSpotsTable = new StyledTable(spotsTableModel);
        availableSpotsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = availableSpotsTable.createScrollPane();
        scrollPane.setPreferredSize(new Dimension(0, 250));
        
        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setOpaque(false);
        JLabel tableTitle = new JLabel("Available Spots");
        tableTitle.setFont(UIConstants.BODY_BOLD);
        tableTitle.setForeground(UIConstants.TEXT_SECONDARY);
        tableTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, UIConstants.SPACING_SM, 0));
        tableWrapper.add(tableTitle, BorderLayout.NORTH);
        tableWrapper.add(scrollPane, BorderLayout.CENTER);
        
        content.add(tableWrapper, BorderLayout.CENTER);
        
        // Park button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(UIConstants.SPACING_MD, 0, 0, 0));
        parkVehicleButton = new StyledButton("Park Vehicle", StyledButton.ButtonType.SUCCESS);
        parkVehicleButton.setPreferredSize(new Dimension(180, UIConstants.BUTTON_HEIGHT));
        parkVehicleButton.addActionListener(e -> handleParkVehicle());
        buttonPanel.add(parkVehicleButton);
        
        content.add(buttonPanel, BorderLayout.SOUTH);
        
        return card;
    }
    
    private JPanel createVehicleExitPanel() {
        CardPanel card = new CardPanel("Vehicle Exit");
        card.setContentLayout(new BorderLayout(0, UIConstants.SPACING_MD));
        JPanel content = card.getContentPanel();
        
        // Search section
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, UIConstants.SPACING_MD, UIConstants.SPACING_SM);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        JLabel plateLabel = new JLabel("License Plate");
        plateLabel.setFont(UIConstants.BODY_BOLD);
        plateLabel.setForeground(UIConstants.TEXT_PRIMARY);
        searchPanel.add(plateLabel, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1;
        exitLicensePlateField = new StyledTextField("Enter license plate");
        searchPanel.add(exitLicensePlateField, gbc);
        
        gbc.gridx = 2; gbc.weightx = 0;
        gbc.insets = new Insets(0, 0, UIConstants.SPACING_MD, 0);
        findVehicleButton = new StyledButton("Find", StyledButton.ButtonType.PRIMARY);
        findVehicleButton.addActionListener(e -> handleFindVehicle());
        searchPanel.add(findVehicleButton, gbc);
        
        content.add(searchPanel, BorderLayout.NORTH);
        
        // Summary section
        RoundedPanel summaryPanel = new RoundedPanel(UIConstants.RADIUS_MD, false);
        summaryPanel.setBackground(UIConstants.BG_MAIN);
        summaryPanel.setLayout(new GridBagLayout());
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(
            UIConstants.SPACING_LG, UIConstants.SPACING_LG,
            UIConstants.SPACING_LG, UIConstants.SPACING_LG
        ));
        
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(UIConstants.SPACING_SM, 0, UIConstants.SPACING_SM, UIConstants.SPACING_LG);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Hours
        gbc.gridx = 0; gbc.gridy = 0;
        summaryPanel.add(createSummaryLabel("Parking Hours"), gbc);
        gbc.gridx = 1;
        hoursLabel = createValueLabel("-");
        summaryPanel.add(hoursLabel, gbc);
        
        // Parking fee
        gbc.gridx = 0; gbc.gridy = 1;
        summaryPanel.add(createSummaryLabel("Parking Fee"), gbc);
        gbc.gridx = 1;
        parkingFeeLabel = createValueLabel("RM 0.00");
        summaryPanel.add(parkingFeeLabel, gbc);
        
        // Fines
        gbc.gridx = 0; gbc.gridy = 2;
        summaryPanel.add(createSummaryLabel("Unpaid Fines"), gbc);
        gbc.gridx = 1;
        finesLabel = createValueLabel("RM 0.00");
        finesLabel.setForeground(UIConstants.DANGER);
        summaryPanel.add(finesLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(UIConstants.SPACING_SM, 0, UIConstants.SPACING_SM, 0);
        payFinesCheckBox = createStyledCheckBox("Pay fines now (Required)");
        payFinesCheckBox.setSelected(true);
        payFinesCheckBox.setEnabled(false); // Always disabled - fines are mandatory
        summaryPanel.add(payFinesCheckBox, gbc);
        
        // Separator
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(UIConstants.SPACING_MD, 0, UIConstants.SPACING_MD, 0);
        JSeparator sep = new JSeparator();
        sep.setForeground(UIConstants.BORDER);
        summaryPanel.add(sep, gbc);
        
        // Total
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 1;
        gbc.insets = new Insets(UIConstants.SPACING_SM, 0, UIConstants.SPACING_SM, UIConstants.SPACING_LG);
        JLabel totalText = createSummaryLabel("Total Amount");
        totalText.setFont(UIConstants.TITLE_SMALL);
        summaryPanel.add(totalText, gbc);
        gbc.gridx = 1;
        totalLabel = createValueLabel("RM 0.00");
        totalLabel.setFont(UIConstants.TITLE_MEDIUM);
        totalLabel.setForeground(UIConstants.PRIMARY);
        summaryPanel.add(totalLabel, gbc);
        
        // Payment method
        gbc.gridx = 0; gbc.gridy = 6;
        summaryPanel.add(createSummaryLabel("Payment Method"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        paymentMethodComboBox = new StyledComboBox<>(PaymentMethod.values());
        summaryPanel.add(paymentMethodComboBox, gbc);
        
        content.add(summaryPanel, BorderLayout.CENTER);
        
        // Pay button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(UIConstants.SPACING_MD, 0, 0, 0));
        payExitButton = new StyledButton("Pay & Exit", StyledButton.ButtonType.PRIMARY);
        payExitButton.setPreferredSize(new Dimension(180, UIConstants.BUTTON_HEIGHT));
        payExitButton.setEnabled(false);
        payExitButton.addActionListener(e -> handlePayAndExit());
        buttonPanel.add(payExitButton);
        
        content.add(buttonPanel, BorderLayout.SOUTH);
        
        return card;
    }
    
    private JLabel createSummaryLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIConstants.BODY);
        label.setForeground(UIConstants.TEXT_SECONDARY);
        return label;
    }
    
    private JLabel createValueLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIConstants.BODY_BOLD);
        label.setForeground(UIConstants.TEXT_PRIMARY);
        return label;
    }
    
    private JCheckBox createStyledCheckBox(String text) {
        JCheckBox checkBox = new JCheckBox(text);
        checkBox.setFont(UIConstants.BODY);
        checkBox.setForeground(UIConstants.TEXT_PRIMARY);
        checkBox.setBackground(UIConstants.BG_MAIN);
        checkBox.setOpaque(false);
        checkBox.setFocusPainted(false);
        checkBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return checkBox;
    }
    
    private void updateExitTotals() {
        // Fines are always included - no choice
        totalLabel.setText(String.format("RM %.2f", currentParkingFee + currentFineAmount));
    }

    private void refreshAvailableSpots() {
        spotsTableModel.setRowCount(0);
        if (parkingService == null) {
            showError("Parking service is not available.");
            return;
        }
        
        VehicleType selectedType = (VehicleType) vehicleTypeComboBox.getSelectedItem();
        if (selectedType == null) {
            // No vehicle type selected, just clear the table without error
            return;
        }
        
        try {
            List<ParkingSpot> availableSpots = parkingService.getAvailableSpots(selectedType);
            if (availableSpots.isEmpty()) {
                showWarning("No available spots for " + selectedType + " vehicles.");
            }
            for (ParkingSpot spot : availableSpots) {
                Object[] row = {
                    spot.getSpotId(),
                    spot.getType().toString(),
                    extractFloorFromSpotId(spot.getSpotId()),
                    String.format("%.2f", spot.getHourlyRate())
                };
                spotsTableModel.addRow(row);
            }
        } catch (Exception e) {
            showError("Error loading available spots: " + e.getMessage());
        }
    }
    
    private String extractFloorFromSpotId(String spotId) {
        if (spotId != null && spotId.contains("-")) {
            return spotId.substring(0, spotId.indexOf("-"));
        }
        return "F1";
    }
    
    private void handleParkVehicle() {
        String licensePlate = entryLicensePlateField.getText().trim().toUpperCase();
        
        // Validate license plate
        InputValidator.ValidationResult plateValidation = InputValidator.validateLicensePlate(licensePlate);
        if (!plateValidation.isValid()) {
            showError(plateValidation.getErrorMessage());
            entryLicensePlateField.requestFocus();
            return;
        }
        
        VehicleType vehicleType = (VehicleType) vehicleTypeComboBox.getSelectedItem();
        if (vehicleType == null) {
            showError("Please select a vehicle type.");
            return;
        }
        
        int selectedRow = availableSpotsTable.getSelectedRow();
        if (selectedRow < 0) {
            showError("Please select a parking spot.");
            return;
        }
        
        String spotId = (String) spotsTableModel.getValueAt(selectedRow, 0);
        
        if (parkingService == null) {
            showError("Parking service is not available.");
            return;
        }
        
        try {
            Ticket ticket = parkingService.processEntry(licensePlate, vehicleType, spotId);
            
            String message = String.format(
                "Vehicle parked successfully!\n\n" +
                "Ticket ID: %s\n" +
                "License Plate: %s\n" +
                "Spot: %s\n" +
                "Entry Time: %s",
                ticket.getTicketId(),
                ticket.getLicensePlate(),
                ticket.getSpotId(),
                ticket.getEntryTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            );
            
            showSuccess("Entry Successful", message);
            clearEntryInputs();
            refreshAvailableSpots();
            
        } catch (IllegalArgumentException | IllegalStateException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("An unexpected error occurred: " + e.getMessage());
        }
    }
    
    private void handleFindVehicle() {
        String licensePlate = exitLicensePlateField.getText().trim().toUpperCase();
        
        // Validate license plate
        InputValidator.ValidationResult plateValidation = InputValidator.validateLicensePlate(licensePlate);
        if (!plateValidation.isValid()) {
            showError(plateValidation.getErrorMessage());
            exitLicensePlateField.requestFocus();
            return;
        }
        
        if (parkingService == null) {
            showError("Parking service is not available.");
            return;
        }
        
        try {
            currentExitVehicle = parkingService.findVehicleByPlate(licensePlate);
            if (currentExitVehicle == null || currentExitVehicle.getEntryTime() == null) {
                showWarning("Vehicle not found in parked vehicles: " + licensePlate);
                clearExitSummary();
                return;
            }
            
            if (currentExitVehicle.getExitTime() != null) {
                showWarning("Vehicle has already exited: " + licensePlate);
                clearExitSummary();
                return;
            }
            
            currentExitSpot = parkingService.findSpotByVehiclePlate(licensePlate);
            if (currentExitSpot == null) {
                showError("Parking spot not found for vehicle: " + licensePlate);
                clearExitSummary();
                return;
            }
            
            LocalDateTime entryTime = currentExitVehicle.getEntryTime();
            LocalDateTime now = LocalDateTime.now();
            long minutes = ChronoUnit.MINUTES.between(entryTime, now);
            int hours = (int) Math.ceil(minutes / 60.0);
            if (hours < 1) hours = 1;
            
            currentExitVehicle.setExitTime(now);
            currentParkingFee = parkingService.calculateParkingFee(currentExitVehicle, currentExitSpot);
            currentExitVehicle.setExitTime(null);
            
            currentFineAmount = 0.0;
            if (fineService != null) {
                currentFineAmount = fineService.getTotalUnpaidAmount(licensePlate);
            }
            
            // Fines are always required to be paid - checkbox is always checked and disabled
            payFinesCheckBox.setSelected(true);
            payFinesCheckBox.setEnabled(false);
            
            hoursLabel.setText(hours + " hour(s)");
            parkingFeeLabel.setText(String.format("RM %.2f", currentParkingFee));
            finesLabel.setText(String.format("RM %.2f", currentFineAmount));
            updateExitTotals();
            
            payExitButton.setEnabled(true);
            
        } catch (Exception e) {
            showError("Error finding vehicle: " + e.getMessage());
            clearExitSummary();
        }
    }
    
    private void handlePayAndExit() {
        if (currentExitVehicle == null || currentExitSpot == null) {
            showError("Please find a vehicle first.");
            return;
        }
        
        PaymentMethod paymentMethod = (PaymentMethod) paymentMethodComboBox.getSelectedItem();
        if (paymentMethod == null) {
            showError("Please select a payment method.");
            return;
        }
        
        if (parkingService == null) {
            showError("Parking service is not available.");
            return;
        }
        
        boolean payFines = true; // Always pay fines - no choice
        
        // Show card payment dialog if CARD is selected
        if (paymentMethod == PaymentMethod.CARD) {
            double totalAmount = currentParkingFee + currentFineAmount; // Always include fines
            CardPaymentDialog cardDialog = new CardPaymentDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), 
                totalAmount
            );
            
            if (!cardDialog.showDialog()) {
                // User cancelled payment
                return;
            }
        }
        
        try {
            String licensePlate = currentExitVehicle.getLicensePlate();
            Receipt receipt = parkingService.processExit(licensePlate, paymentMethod, payFines);
            
            // Build detailed receipt message
            StringBuilder message = new StringBuilder();
            message.append("Payment Successful!\n\n");
            message.append("═══════════════════════════════\n");
            message.append("         PARKING RECEIPT\n");
            message.append("═══════════════════════════════\n\n");
            message.append(String.format("Receipt ID: %s\n", receipt.getReceiptId()));
            message.append(String.format("License Plate: %s\n\n", receipt.getLicensePlate()));
            
            // Time details
            if (receipt.getEntryTime() != null) {
                message.append(String.format("Entry Time: %s\n", 
                    receipt.getEntryTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
            }
            message.append(String.format("Exit Time: %s\n", 
                receipt.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
            message.append(String.format("Duration: %d hour(s)\n\n", receipt.getDurationHours()));
            
            // Fee breakdown
            message.append("───────────────────────────────\n");
            message.append("Fee Breakdown:\n");
            message.append(String.format("  %s\n", receipt.getFeeBreakdown()));
            message.append(String.format("  Parking Fee: RM %.2f\n", receipt.getParkingFee()));
            if (receipt.getFineAmount() > 0) {
                message.append(String.format("  Fines: RM %.2f\n", receipt.getFineAmount()));
            }
            message.append("───────────────────────────────\n");
            message.append(String.format("Total Paid: RM %.2f\n", receipt.getTotalAmount()));
            message.append(String.format("Payment Method: %s\n", receipt.getPaymentMethod()));
            message.append("\n═══════════════════════════════\n");
            message.append("      Thank you for parking!\n");
            
            showSuccess("Exit Successful", message.toString());
            clearExitInputs();
            refreshAvailableSpots();
            
        } catch (IllegalArgumentException | IllegalStateException e) {
            showError("Payment failed: " + e.getMessage());
        } catch (Exception e) {
            showError("An unexpected error occurred: " + e.getMessage());
        }
    }
    
    private void clearEntryInputs() {
        entryLicensePlateField.setText("");
        vehicleTypeComboBox.setSelectedIndex(-1);
        spotsTableModel.setRowCount(0);
    }
    
    private void clearExitInputs() {
        exitLicensePlateField.setText("");
        clearExitSummary();
    }
    
    private void clearExitSummary() {
        currentExitVehicle = null;
        currentExitSpot = null;
        currentParkingFee = 0.0;
        currentFineAmount = 0.0;

        if (payFinesCheckBox != null) {
            payFinesCheckBox.setSelected(true);
            payFinesCheckBox.setEnabled(false);
        }

        hoursLabel.setText("-");
        parkingFeeLabel.setText("RM 0.00");
        finesLabel.setText("RM 0.00");
        totalLabel.setText("RM 0.00");
        payExitButton.setEnabled(false);
    }
    
    private void layoutComponents() {
        add(vehicleEntryPanel);
        add(vehicleExitPanel);
    }
    
    // Dialog helpers
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }
    
    private void showSuccess(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    // Getters and setters
    
    public JPanel getVehicleEntryPanel() {
        return vehicleEntryPanel;
    }
    
    public JPanel getVehicleExitPanel() {
        return vehicleExitPanel;
    }
    
    public void setParkingService(ParkingService parkingService) {
        this.parkingService = parkingService;
    }
    
    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
    
    public void setFineService(FineService fineService) {
        this.fineService = fineService;
    }
    
    public ParkingService getParkingService() {
        return parkingService;
    }
    
    public PaymentService getPaymentService() {
        return paymentService;
    }
    
    public FineService getFineService() {
        return fineService;
    }
    
    public JTextField getEntryLicensePlateField() {
        return entryLicensePlateField;
    }
    
    public JComboBox<VehicleType> getVehicleTypeComboBox() {
        return vehicleTypeComboBox;
    }
    
    public JTable getAvailableSpotsTable() {
        return availableSpotsTable;
    }
    
    public JButton getParkVehicleButton() {
        return parkVehicleButton;
    }
    
    public JTextField getExitLicensePlateField() {
        return exitLicensePlateField;
    }
    
    public JButton getFindVehicleButton() {
        return findVehicleButton;
    }
    
    public JLabel getHoursLabel() {
        return hoursLabel;
    }
    
    public JLabel getParkingFeeLabel() {
        return parkingFeeLabel;
    }
    
    public JLabel getFinesLabel() {
        return finesLabel;
    }
    
    public JLabel getTotalLabel() {
        return totalLabel;
    }
    
    public JComboBox<PaymentMethod> getPaymentMethodComboBox() {
        return paymentMethodComboBox;
    }
    
    public JButton getPayExitButton() {
        return payExitButton;
    }
}
