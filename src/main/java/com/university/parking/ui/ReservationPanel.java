package com.university.parking.ui;

import com.university.parking.domain.Reservation;
import com.university.parking.domain.ReservationStatus;
import com.university.parking.service.ParkingService;
import com.university.parking.service.ReservationService;
import com.university.parking.ui.components.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Panel for managing parking spot reservations.
 * Allows users to create, view, confirm, and cancel reservations.
 */
public class ReservationPanel extends JPanel {
    
    private final ReservationService reservationService;
    private final ParkingService parkingService;
    
    // UI Components
    private StyledTextField licensePlateField;
    private StyledTextField spotIdField;
    private JSpinner startDateSpinner;
    private JSpinner startTimeSpinner;
    private JSpinner endDateSpinner;
    private JSpinner endTimeSpinner;
    private StyledTable reservationTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    public ReservationPanel(ReservationService reservationService, ParkingService parkingService) {
        this.reservationService = reservationService;
        this.parkingService = parkingService;
        
        setLayout(new BorderLayout(UIConstants.SPACING_LG, UIConstants.SPACING_LG));
        setBackground(UIConstants.BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(
            UIConstants.SPACING_LG, UIConstants.SPACING_LG,
            UIConstants.SPACING_LG, UIConstants.SPACING_LG));
        
        initializeComponents();
        layoutComponents();
        refreshReservationTable();
    }
    
    private void initializeComponents() {
        // Input fields
        licensePlateField = new StyledTextField("License Plate (e.g., ABC1234)");
        spotIdField = new StyledTextField("Spot ID (e.g., F1-R1-S1)");
        
        // Date and time spinners
        startDateSpinner = createDateSpinner();
        startTimeSpinner = createTimeSpinner();
        endDateSpinner = createDateSpinner();
        endTimeSpinner = createTimeSpinner();
        
        // Status label
        statusLabel = new JLabel(" ");
        statusLabel.setFont(UIConstants.BODY);
        statusLabel.setForeground(UIConstants.TEXT_SECONDARY);
        
        // Table
        String[] columns = {"Reservation ID", "License Plate", "Spot ID", "Start Time", "End Time", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        reservationTable = new StyledTable(tableModel);
        reservationTable.getColumnModel().getColumn(0).setPreferredWidth(120);
        reservationTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        reservationTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        reservationTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        reservationTable.getColumnModel().getColumn(4).setPreferredWidth(150);
        reservationTable.getColumnModel().getColumn(5).setPreferredWidth(100);
    }
    
    private JSpinner createDateSpinner() {
        SpinnerDateModel model = new SpinnerDateModel();
        JSpinner spinner = new JSpinner(model);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "yyyy-MM-dd");
        spinner.setEditor(editor);
        spinner.setFont(UIConstants.BODY);
        return spinner;
    }
    
    private JSpinner createTimeSpinner() {
        SpinnerDateModel model = new SpinnerDateModel();
        JSpinner spinner = new JSpinner(model);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "HH:mm");
        spinner.setEditor(editor);
        spinner.setFont(UIConstants.BODY);
        return spinner;
    }
    
    private void layoutComponents() {
        // Top section - Create Reservation
        JPanel createPanel = createReservationFormPanel();
        
        // Middle section - Reservation List
        JPanel listPanel = createReservationListPanel();
        
        // Bottom section - Actions
        JPanel actionPanel = createActionPanel();
        
        // Layout
        JPanel topContainer = new JPanel(new BorderLayout(0, UIConstants.SPACING_MD));
        topContainer.setOpaque(false);
        topContainer.add(createPanel, BorderLayout.NORTH);
        topContainer.add(listPanel, BorderLayout.CENTER);
        
        add(topContainer, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createReservationFormPanel() {
        CardPanel card = new CardPanel("Create New Reservation");
        JPanel content = card.getContentPanel();
        content.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(UIConstants.SPACING_SM, UIConstants.SPACING_SM, 
                                UIConstants.SPACING_SM, UIConstants.SPACING_SM);
        
        // Row 1: License Plate and Spot ID
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.0;
        content.add(createLabel("License Plate:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        content.add(licensePlateField, gbc);
        
        gbc.gridx = 2; gbc.weightx = 0.0;
        content.add(createLabel("Spot ID:"), gbc);
        
        gbc.gridx = 3; gbc.weightx = 1.0;
        content.add(spotIdField, gbc);
        
        // Row 2: Start Date and Time
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0;
        content.add(createLabel("Start Date:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        content.add(startDateSpinner, gbc);
        
        gbc.gridx = 2; gbc.weightx = 0.0;
        content.add(createLabel("Start Time:"), gbc);
        
        gbc.gridx = 3; gbc.weightx = 1.0;
        content.add(startTimeSpinner, gbc);
        
        // Row 3: End Date and Time
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.0;
        content.add(createLabel("End Date:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        content.add(endDateSpinner, gbc);
        
        gbc.gridx = 2; gbc.weightx = 0.0;
        content.add(createLabel("End Time:"), gbc);
        
        gbc.gridx = 3; gbc.weightx = 1.0;
        content.add(endTimeSpinner, gbc);
        
        // Row 4: Buttons
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        StyledButton checkAvailabilityBtn = new StyledButton("Check Availability", StyledButton.ButtonType.SECONDARY);
        checkAvailabilityBtn.addActionListener(e -> handleCheckAvailability());
        content.add(checkAvailabilityBtn, gbc);
        
        gbc.gridx = 2; gbc.gridwidth = 2;
        StyledButton createBtn = new StyledButton("Create Reservation", StyledButton.ButtonType.PRIMARY);
        createBtn.addActionListener(e -> handleCreateReservation());
        content.add(createBtn, gbc);
        
        // Row 5: Status
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 4;
        content.add(statusLabel, gbc);
        
        return card;
    }
    
    private JPanel createReservationListPanel() {
        CardPanel card = new CardPanel("Reservation List");
        JPanel content = card.getContentPanel();
        content.setLayout(new BorderLayout());
        
        JScrollPane scrollPane = new JScrollPane(reservationTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER, 1));
        content.add(scrollPane, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIConstants.SPACING_MD, 0));
        panel.setOpaque(false);
        
        StyledButton viewSpotsBtn = new StyledButton("View Available Spots", StyledButton.ButtonType.SECONDARY);
        viewSpotsBtn.addActionListener(e -> handleViewAvailableSpots());
        
        StyledButton refreshBtn = new StyledButton("Refresh", StyledButton.ButtonType.SECONDARY);
        refreshBtn.addActionListener(e -> refreshReservationTable());
        
        StyledButton cancelBtn = new StyledButton("Cancel Selected", StyledButton.ButtonType.DANGER);
        cancelBtn.addActionListener(e -> handleCancelReservation());
        
        StyledButton viewByPlateBtn = new StyledButton("View by License Plate", StyledButton.ButtonType.SECONDARY);
        viewByPlateBtn.addActionListener(e -> handleViewByLicensePlate());
        
        panel.add(viewSpotsBtn);
        panel.add(viewByPlateBtn);
        panel.add(refreshBtn);
        panel.add(cancelBtn);
        
        // Note: Confirm button removed since reservations are auto-confirmed upon creation
        
        return panel;
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIConstants.BODY_BOLD);
        label.setForeground(UIConstants.TEXT_PRIMARY);
        return label;
    }
    
    // Event Handlers
    
    private void handleCheckAvailability() {
        try {
            String spotId = spotIdField.getText().trim();
            if (spotId.isEmpty()) {
                showError("Please enter a spot ID");
                return;
            }
            
            LocalDateTime startTime = getStartDateTime();
            LocalDateTime endTime = getEndDateTime();
            
            if (endTime.isBefore(startTime)) {
                showError("End time must be after start time");
                return;
            }
            
            boolean available = reservationService.isSpotAvailableForReservation(spotId, startTime, endTime);
            
            if (available) {
                statusLabel.setText("✓ Spot is available for the selected time range");
                statusLabel.setForeground(UIConstants.SUCCESS);
            } else {
                statusLabel.setText("✗ Spot is not available for the selected time range");
                statusLabel.setForeground(UIConstants.DANGER);
            }
            
        } catch (Exception ex) {
            showError("Error checking availability: " + ex.getMessage());
        }
    }
    
    private void handleCreateReservation() {
        try {
            // Validate inputs
            String licensePlate = licensePlateField.getText().trim().toUpperCase();
            String spotId = spotIdField.getText().trim().toUpperCase();
            
            if (licensePlate.isEmpty() || spotId.isEmpty()) {
                showError("Please enter license plate and spot ID");
                return;
            }
            
            InputValidator.ValidationResult validation = InputValidator.validateLicensePlate(licensePlate);
            if (!validation.isValid()) {
                showError(validation.getErrorMessage());
                return;
            }
            
            // Check if spot exists and is RESERVED type
            com.university.parking.domain.ParkingSpot spot = parkingService.getAllSpots().stream()
                .filter(s -> s.getSpotId().equals(spotId))
                .findFirst()
                .orElse(null);
            
            if (spot == null) {
                showError("Spot not found: " + spotId);
                return;
            }
            
            if (spot.getType() != com.university.parking.domain.SpotType.RESERVED) {
                showError("Only RESERVED type spots can be reserved.\nSpot " + spotId + 
                         " is type: " + spot.getType() + "\nPlease select a RESERVED spot.");
                return;
            }
            
            LocalDateTime startTime = getStartDateTime();
            LocalDateTime endTime = getEndDateTime();
            
            if (endTime.isBefore(startTime)) {
                showError("End time must be after start time");
                return;
            }
            
            // Create reservation with CONFIRMED status directly (admin creates active reservations)
            Reservation reservation = reservationService.createReservation(
                licensePlate, spotId, startTime, endTime);
            
            // Manually confirm the reservation immediately
            reservation.confirm();
            
            // Save the confirmed reservation to database
            try {
                // Use the DAO directly to update the status
                reservationService.confirmReservation(reservation.getReservationId());
                System.out.println("DEBUG: Reservation confirmed successfully: " + reservation.getReservationId());
            } catch (Exception confirmEx) {
                System.err.println("DEBUG: Failed to confirm reservation: " + confirmEx.getMessage());
                confirmEx.printStackTrace();
                // Show warning but continue
                statusLabel.setText("⚠ Reservation created but not confirmed. Status: PENDING");
                statusLabel.setForeground(UIConstants.WARNING);
            }
            
            showSuccess("Reservation created successfully!\n" +
                       "Reservation ID: " + reservation.getReservationId() + "\n" +
                       "License Plate: " + licensePlate + "\n" +
                       "Spot: " + spotId + "\n" +
                       "Start: " + startTime.format(DISPLAY_FORMATTER) + "\n" +
                       "End: " + endTime.format(DISPLAY_FORMATTER) + "\n" +
                       "Status: " + reservation.getStatus());
            
            // Clear form and refresh table
            clearForm();
            refreshReservationTable();
            
        } catch (IllegalArgumentException ex) {
            showError("Invalid input: " + ex.getMessage());
        } catch (IllegalStateException ex) {
            showError("Cannot create reservation: " + ex.getMessage());
        } catch (Exception ex) {
            showError("Error creating reservation: " + ex.getMessage());
        }
    }
    
    private void handleConfirmReservation() {
        int selectedRow = reservationTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a reservation to confirm");
            return;
        }
        
        try {
            String reservationId = (String) tableModel.getValueAt(selectedRow, 0);
            String status = (String) tableModel.getValueAt(selectedRow, 5);
            
            if ("CONFIRMED".equals(status)) {
                showInfo("This reservation is already confirmed");
                return;
            }
            
            if (!"PENDING".equals(status)) {
                showError("Only pending reservations can be confirmed");
                return;
            }
            
            reservationService.confirmReservation(reservationId);
            showSuccess("Reservation confirmed successfully");
            refreshReservationTable();
            
        } catch (IllegalArgumentException ex) {
            showError("Reservation not found: " + ex.getMessage());
        } catch (Exception ex) {
            showError("Error confirming reservation: " + ex.getMessage());
        }
    }
    
    private void handleCancelReservation() {
        int selectedRow = reservationTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a reservation to cancel");
            return;
        }
        
        try {
            String reservationId = (String) tableModel.getValueAt(selectedRow, 0);
            String status = (String) tableModel.getValueAt(selectedRow, 5);
            String startTimeStr = (String) tableModel.getValueAt(selectedRow, 3);
            
            System.out.println("DEBUG: Attempting to cancel reservation: " + reservationId + " with status: " + status);
            
            if ("COMPLETED".equals(status)) {
                showError("Cannot cancel completed reservations");
                return;
            }
            
            if ("CANCELLED".equals(status)) {
                showError("Reservation is already cancelled");
                return;
            }
            
            // Check if within 10 minutes of start time
            LocalDateTime startTime = LocalDateTime.parse(startTimeStr, DISPLAY_FORMATTER);
            LocalDateTime now = LocalDateTime.now();
            long minutesUntilStart = java.time.temporal.ChronoUnit.MINUTES.between(now, startTime);
            
            System.out.println("DEBUG: Minutes until start: " + minutesUntilStart);
            
            if (minutesUntilStart < 10 && minutesUntilStart >= 0) {
                showError("Cannot cancel reservation within 10 minutes of start time.\n" +
                         "Start time: " + startTimeStr + "\n" +
                         "Current time: " + now.format(DISPLAY_FORMATTER) + "\n" +
                         "Minutes until start: " + minutesUntilStart);
                return;
            }
            
            if (minutesUntilStart < 0) {
                // Reservation has already started
                long minutesSinceStart = Math.abs(minutesUntilStart);
                if (minutesSinceStart < 10) {
                    showError("Cannot cancel reservation within 10 minutes of start time.\n" +
                             "Reservation started " + minutesSinceStart + " minute(s) ago.");
                    return;
                }
            }
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel this reservation?\n" +
                "Reservation ID: " + reservationId + "\n" +
                "Status: " + status + "\n" +
                "Start Time: " + startTimeStr,
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    System.out.println("DEBUG: Calling cancelReservation for: " + reservationId);
                    reservationService.cancelReservation(reservationId);
                    System.out.println("DEBUG: Reservation cancelled successfully");
                    showSuccess("Reservation cancelled successfully");
                    refreshReservationTable();
                } catch (IllegalArgumentException cancelEx) {
                    System.err.println("DEBUG: Cancellation failed - reservation not found");
                    cancelEx.printStackTrace();
                    showError("Reservation not found in database: " + reservationId + "\n" +
                             "Error: " + cancelEx.getMessage() + "\n\n" +
                             "The reservation may have been deleted or the database may be out of sync.\n" +
                             "Try clicking 'Refresh' to reload the table.");
                } catch (Exception cancelEx) {
                    System.err.println("DEBUG: Cancellation failed with exception");
                    cancelEx.printStackTrace();
                    showError("Error cancelling reservation: " + cancelEx.getMessage() + "\n\n" +
                             "Please check the console for details and try refreshing the table.");
                }
            }
            
        } catch (Exception ex) {
            System.err.println("DEBUG: Outer exception in handleCancelReservation");
            ex.printStackTrace();
            showError("Error cancelling reservation: " + ex.getMessage());
        }
    }
    
    private void handleViewByLicensePlate() {
        String licensePlate = JOptionPane.showInputDialog(this,
            "Enter license plate:",
            "View Reservations",
            JOptionPane.QUESTION_MESSAGE);
        
        if (licensePlate != null && !licensePlate.trim().isEmpty()) {
            try {
                List<Reservation> reservations = reservationService.findByLicensePlate(
                    licensePlate.trim().toUpperCase());
                
                if (reservations.isEmpty()) {
                    showInfo("No reservations found for license plate: " + licensePlate);
                } else {
                    updateTableWithReservations(reservations);
                }
                
            } catch (Exception ex) {
                showError("Error retrieving reservations: " + ex.getMessage());
            }
        }
    }
    
    private void handleViewAvailableSpots() {
        try {
            // Get all spots
            List<com.university.parking.domain.ParkingSpot> allSpots = 
                parkingService.getAllSpots();
            
            // Filter available RESERVED spots only
            List<com.university.parking.domain.ParkingSpot> availableReservedSpots = allSpots.stream()
                .filter(spot -> spot.getStatus() == com.university.parking.domain.SpotStatus.AVAILABLE)
                .filter(spot -> spot.getType() == com.university.parking.domain.SpotType.RESERVED)
                .collect(java.util.stream.Collectors.toList());
            
            if (availableReservedSpots.isEmpty()) {
                showInfo("No available RESERVED spots at the moment.\n" +
                        "Note: Only RESERVED type spots can be reserved.");
                return;
            }
            
            // Create dialog to show available spots
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                        "Available RESERVED Spots", true);
            dialog.setLayout(new BorderLayout(UIConstants.SPACING_MD, UIConstants.SPACING_MD));
            dialog.setSize(600, 400);
            dialog.setLocationRelativeTo(this);
            
            // Create table
            String[] columns = {"Spot ID", "Type", "Hourly Rate (RM)"};
            DefaultTableModel model = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            for (com.university.parking.domain.ParkingSpot spot : availableReservedSpots) {
                model.addRow(new Object[]{
                    spot.getSpotId(),
                    spot.getType().name(),
                    String.format("%.2f", spot.getHourlyRate())
                });
            }
            
            StyledTable table = new StyledTable(model);
            JScrollPane scrollPane = new JScrollPane(table);
            
            // Info label
            JLabel infoLabel = new JLabel("Only RESERVED type spots can be reserved");
            infoLabel.setFont(UIConstants.BODY);
            infoLabel.setForeground(UIConstants.INFO);
            infoLabel.setBorder(BorderFactory.createEmptyBorder(
                UIConstants.SPACING_SM, UIConstants.SPACING_MD, 
                UIConstants.SPACING_SM, UIConstants.SPACING_MD));
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            StyledButton closeBtn = new StyledButton("Close", StyledButton.ButtonType.SECONDARY);
            closeBtn.addActionListener(e -> dialog.dispose());
            
            StyledButton selectBtn = new StyledButton("Use Selected Spot", StyledButton.ButtonType.PRIMARY);
            selectBtn.addActionListener(e -> {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    String spotId = (String) model.getValueAt(selectedRow, 0);
                    spotIdField.setText(spotId);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, 
                        "Please select a spot", "No Selection", JOptionPane.WARNING_MESSAGE);
                }
            });
            
            buttonPanel.add(selectBtn);
            buttonPanel.add(closeBtn);
            
            dialog.add(infoLabel, BorderLayout.NORTH);
            dialog.add(scrollPane, BorderLayout.CENTER);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            
            dialog.setVisible(true);
            
        } catch (Exception ex) {
            showError("Error retrieving available spots: " + ex.getMessage());
        }
    }
    
    private void refreshReservationTable() {
        try {
            List<Reservation> reservations = reservationService.getAllReservations();
            
            if (reservations.isEmpty()) {
                tableModel.setRowCount(0);
                statusLabel.setText("No reservations found");
                statusLabel.setForeground(UIConstants.TEXT_SECONDARY);
            } else {
                updateTableWithReservations(reservations);
                
                // Count active reservations
                long activeCount = reservations.stream()
                    .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED 
                              && r.getStartTime().isBefore(LocalDateTime.now())
                              && r.getEndTime().isAfter(LocalDateTime.now()))
                    .count();
                
                statusLabel.setText("Showing " + reservations.size() + " reservation(s) (" + 
                                  activeCount + " active)");
                statusLabel.setForeground(UIConstants.TEXT_SECONDARY);
            }
            
        } catch (Exception ex) {
            showError("Error refreshing table: " + ex.getMessage());
        }
    }
    
    private void updateTableWithReservations(List<Reservation> reservations) {
        tableModel.setRowCount(0);
        
        for (Reservation reservation : reservations) {
            Object[] row = {
                reservation.getReservationId(),
                reservation.getLicensePlate(),
                reservation.getSpotId(),
                reservation.getStartTime().format(DISPLAY_FORMATTER),
                reservation.getEndTime().format(DISPLAY_FORMATTER),
                reservation.getStatus().name()
            };
            tableModel.addRow(row);
        }
    }
    
    // Helper Methods
    
    private LocalDateTime getStartDateTime() {
        java.util.Date startDate = (java.util.Date) startDateSpinner.getValue();
        java.util.Date startTime = (java.util.Date) startTimeSpinner.getValue();
        
        java.util.Calendar dateCal = java.util.Calendar.getInstance();
        dateCal.setTime(startDate);
        
        java.util.Calendar timeCal = java.util.Calendar.getInstance();
        timeCal.setTime(startTime);
        
        return LocalDateTime.of(
            dateCal.get(java.util.Calendar.YEAR),
            dateCal.get(java.util.Calendar.MONTH) + 1,
            dateCal.get(java.util.Calendar.DAY_OF_MONTH),
            timeCal.get(java.util.Calendar.HOUR_OF_DAY),
            timeCal.get(java.util.Calendar.MINUTE)
        );
    }
    
    private LocalDateTime getEndDateTime() {
        java.util.Date endDate = (java.util.Date) endDateSpinner.getValue();
        java.util.Date endTime = (java.util.Date) endTimeSpinner.getValue();
        
        java.util.Calendar dateCal = java.util.Calendar.getInstance();
        dateCal.setTime(endDate);
        
        java.util.Calendar timeCal = java.util.Calendar.getInstance();
        timeCal.setTime(endTime);
        
        return LocalDateTime.of(
            dateCal.get(java.util.Calendar.YEAR),
            dateCal.get(java.util.Calendar.MONTH) + 1,
            dateCal.get(java.util.Calendar.DAY_OF_MONTH),
            timeCal.get(java.util.Calendar.HOUR_OF_DAY),
            timeCal.get(java.util.Calendar.MINUTE)
        );
    }
    
    private void clearForm() {
        licensePlateField.setText("");
        spotIdField.setText("");
        startDateSpinner.setValue(new java.util.Date());
        startTimeSpinner.setValue(new java.util.Date());
        endDateSpinner.setValue(new java.util.Date());
        endTimeSpinner.setValue(new java.util.Date());
        statusLabel.setText(" ");
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }
}
