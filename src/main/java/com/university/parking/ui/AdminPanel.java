package com.university.parking.ui;

import com.university.parking.domain.*;
import com.university.parking.service.FineService;
import com.university.parking.service.ReportService;
import com.university.parking.ui.components.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Admin panel for parking lot management with modern UI design.
 */
public class AdminPanel extends JPanel {
    
    private FineService fineService;
    private ReportService reportService;
    
    // Overview components
    private JPanel overviewPanel;
    private StyledTable spotsTable;
    private DefaultTableModel spotsTableModel;
    private JLabel occupancyLabel;
    
    // Fine scheme components
    private JPanel fineSchemePanel;
    private JRadioButton fixedRadio;
    private JRadioButton progressiveRadio;
    private JRadioButton hourlyRadio;
    private ButtonGroup fineSchemeGroup;
    private StyledButton applyButton;
    private JLabel currentStrategyLabel;
    
    // Statistics components
    private JPanel statisticsPanel;
    private StatCard totalSpotsCard;
    private StatCard availableSpotsCard;
    private StatCard occupiedSpotsCard;
    private StatCard occupancyRateCard;
    
    public AdminPanel() {
        initializePanel();
        initializeComponents();
        layoutComponents();
    }
    
    public AdminPanel(FineService fineService, ReportService reportService) {
        this.fineService = fineService;
        this.reportService = reportService;
        initializePanel();
        initializeComponents();
        layoutComponents();
        refreshData();
    }
    
    private void initializePanel() {
        setLayout(new BorderLayout(UIConstants.SPACING_LG, UIConstants.SPACING_LG));
        setBackground(UIConstants.BG_MAIN);
        setOpaque(false);
    }
    
    private void initializeComponents() {
        statisticsPanel = createStatisticsPanel();
        overviewPanel = createOverviewPanel();
        fineSchemePanel = createFineSchemePanel();
    }
    
    private JPanel createStatisticsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, UIConstants.SPACING_MD, 0));
        panel.setOpaque(false);
        
        totalSpotsCard = new StatCard("Total Spots", "0", UIConstants.PRIMARY);
        availableSpotsCard = new StatCard("Available", "0", UIConstants.SUCCESS);
        occupiedSpotsCard = new StatCard("Occupied", "0", UIConstants.WARNING);
        occupancyRateCard = new StatCard("Occupancy Rate", "0%", UIConstants.INFO);
        
        panel.add(totalSpotsCard);
        panel.add(availableSpotsCard);
        panel.add(occupiedSpotsCard);
        panel.add(occupancyRateCard);
        
        return panel;
    }
    
    private JPanel createOverviewPanel() {
        CardPanel card = new CardPanel("Parking Lot Overview");
        card.setContentLayout(new BorderLayout(0, UIConstants.SPACING_MD));
        JPanel content = card.getContentPanel();
        
        // Table
        String[] columns = {"Floor", "Spot ID", "Type", "Status", "Vehicle"};
        spotsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        spotsTable = new StyledTable(spotsTableModel);
        spotsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = spotsTable.createScrollPane();
        content.add(scrollPane, BorderLayout.CENTER);
        
        // Footer with occupancy and refresh
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(UIConstants.SPACING_MD, 0, 0, 0));
        
        occupancyLabel = new JLabel("Occupancy: 0%");
        occupancyLabel.setFont(UIConstants.BODY_BOLD);
        occupancyLabel.setForeground(UIConstants.TEXT_SECONDARY);
        
        StyledButton refreshButton = new StyledButton("Refresh", StyledButton.ButtonType.SECONDARY);
        refreshButton.addActionListener(e -> refreshData());
        
        footerPanel.add(occupancyLabel, BorderLayout.WEST);
        footerPanel.add(refreshButton, BorderLayout.EAST);
        
        content.add(footerPanel, BorderLayout.SOUTH);
        
        return card;
    }

    private JPanel createFineSchemePanel() {
        CardPanel card = new CardPanel("Fine Scheme Selection");
        card.setContentLayout(new BoxLayout(card.getContentPanel(), BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(280, 0));
        JPanel content = card.getContentPanel();
        
        // Current strategy
        currentStrategyLabel = new JLabel("Current: None");
        currentStrategyLabel.setFont(UIConstants.BODY);
        currentStrategyLabel.setForeground(UIConstants.TEXT_SECONDARY);
        currentStrategyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        currentStrategyLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, UIConstants.SPACING_MD, 0));
        
        // Radio buttons
        fixedRadio = createStyledRadio("Fixed Fine", "Flat RM50 fine for overstaying");
        progressiveRadio = createStyledRadio("Progressive Fine", "RM50 + RM100 + RM150 + RM200 by tier");
        hourlyRadio = createStyledRadio("Hourly Fine", "RM20 per hour of overstay, max RM500");
        
        fineSchemeGroup = new ButtonGroup();
        fineSchemeGroup.add(fixedRadio);
        fineSchemeGroup.add(progressiveRadio);
        fineSchemeGroup.add(hourlyRadio);
        
        // Apply button
        applyButton = new StyledButton("Apply Scheme", StyledButton.ButtonType.PRIMARY);
        applyButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        applyButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, UIConstants.BUTTON_HEIGHT));
        applyButton.addActionListener(e -> applyFineScheme());
        
        content.add(currentStrategyLabel);
        content.add(Box.createVerticalStrut(UIConstants.SPACING_SM));
        
        JLabel selectLabel = new JLabel("Select Fine Scheme:");
        selectLabel.setFont(UIConstants.BODY_BOLD);
        selectLabel.setForeground(UIConstants.TEXT_PRIMARY);
        selectLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(selectLabel);
        
        content.add(Box.createVerticalStrut(UIConstants.SPACING_MD));
        content.add(fixedRadio);
        content.add(Box.createVerticalStrut(UIConstants.SPACING_SM));
        content.add(progressiveRadio);
        content.add(Box.createVerticalStrut(UIConstants.SPACING_SM));
        content.add(hourlyRadio);
        content.add(Box.createVerticalStrut(UIConstants.SPACING_LG));
        content.add(applyButton);
        content.add(Box.createVerticalGlue());
        
        return card;
    }
    
    private JRadioButton createStyledRadio(String text, String tooltip) {
        JRadioButton radio = new JRadioButton(text);
        radio.setFont(UIConstants.BODY);
        radio.setForeground(UIConstants.TEXT_PRIMARY);
        radio.setBackground(Color.WHITE);
        radio.setOpaque(false);
        radio.setFocusPainted(false);
        radio.setToolTipText(tooltip);
        radio.setAlignmentX(Component.LEFT_ALIGNMENT);
        radio.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return radio;
    }
    
    private void layoutComponents() {
        // Top: Statistics cards
        add(statisticsPanel, BorderLayout.NORTH);
        
        // Center: Overview table
        add(overviewPanel, BorderLayout.CENTER);
        
        // Right: Fine scheme panel
        add(fineSchemePanel, BorderLayout.EAST);
    }
    
    public void refreshData() {
        refreshOverviewTable();
        refreshStatistics();
        updateCurrentStrategyLabel();
    }
    
    private void refreshOverviewTable() {
        spotsTableModel.setRowCount(0);
        if (reportService == null) return;
        
        List<ParkingSpot> spots = reportService.getAllSpots();
        for (ParkingSpot spot : spots) {
            String floorId = extractFloorId(spot.getSpotId());
            Object[] row = {
                floorId,
                spot.getSpotId(),
                spot.getType().toString(),
                spot.getStatus().toString(),
                spot.getCurrentVehiclePlate() != null ? spot.getCurrentVehiclePlate() : "-"
            };
            spotsTableModel.addRow(row);
        }
        
        double occupancyRate = reportService.getOccupancyRate();
        occupancyLabel.setText(String.format("Occupancy: %.1f%%", occupancyRate * 100));
    }
    
    private String extractFloorId(String spotId) {
        if (spotId != null && spotId.contains("-")) {
            return spotId.substring(0, spotId.indexOf("-"));
        }
        return "N/A";
    }
    
    private void refreshStatistics() {
        if (reportService == null) return;
        
        int totalSpots = reportService.getTotalSpots();
        int availableSpots = reportService.getAvailableSpots();
        int occupiedSpots = totalSpots - availableSpots;
        double occupancyRate = reportService.getOccupancyRate();
        
        totalSpotsCard.setValue(String.valueOf(totalSpots));
        availableSpotsCard.setValue(String.valueOf(availableSpots));
        occupiedSpotsCard.setValue(String.valueOf(occupiedSpots));
        occupancyRateCard.setValue(String.format("%.1f%%", occupancyRate * 100));
    }
    
    private void updateCurrentStrategyLabel() {
        if (fineService == null) {
            currentStrategyLabel.setText("Current: None");
            return;
        }
        
        FineStrategy strategy = fineService.getCurrentStrategy();
        if (strategy == null) {
            currentStrategyLabel.setText("Current: None");
        } else if (strategy instanceof FixedFineStrategy) {
            currentStrategyLabel.setText("Current: Fixed");
            fixedRadio.setSelected(true);
        } else if (strategy instanceof ProgressiveFineStrategy) {
            currentStrategyLabel.setText("Current: Progressive");
            progressiveRadio.setSelected(true);
        } else if (strategy instanceof HourlyFineStrategy) {
            currentStrategyLabel.setText("Current: Hourly");
            hourlyRadio.setSelected(true);
        } else {
            currentStrategyLabel.setText("Current: Custom");
        }
    }
    
    private void applyFineScheme() {
        if (fineService == null) {
            showError("Fine service is not available.");
            return;
        }
        
        FineStrategy newStrategy = null;
        String strategyName = "";
        
        if (fixedRadio.isSelected()) {
            // Fixed: Flat RM50 fine
            newStrategy = new FixedFineStrategy();
            strategyName = "Fixed";
        } else if (progressiveRadio.isSelected()) {
            // Progressive: Tiered calculation (RM50/100/150/200)
            newStrategy = new ProgressiveFineStrategy();
            strategyName = "Progressive";
        } else if (hourlyRadio.isSelected()) {
            // Hourly: RM20/hour, max RM500
            newStrategy = new HourlyFineStrategy(20.0, 500.0);
            strategyName = "Hourly";
        } else {
            showWarning("Please select a fine scheme.");
            return;
        }
        
        try {
            fineService.setFineStrategy(newStrategy);
            currentStrategyLabel.setText("Current: " + strategyName);
            showSuccess("Fine scheme changed to: " + strategyName);
        } catch (Exception e) {
            showError("Failed to apply fine scheme: " + e.getMessage());
        }
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }
    
    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    // Getters for panels
    
    public JPanel getOverviewPanel() {
        return overviewPanel;
    }
    
    public JPanel getFineSchemePanel() {
        return fineSchemePanel;
    }
    
    public JPanel getStatisticsPanel() {
        return statisticsPanel;
    }
    
    public JTable getSpotsTable() {
        return spotsTable;
    }
    
    public JLabel getOccupancyLabel() {
        return occupancyLabel;
    }
    
    public JRadioButton getFixedRadio() {
        return fixedRadio;
    }
    
    public JRadioButton getProgressiveRadio() {
        return progressiveRadio;
    }
    
    public JRadioButton getHourlyRadio() {
        return hourlyRadio;
    }
    
    public JButton getApplyButton() {
        return applyButton;
    }
    
    public JLabel getCurrentStrategyLabel() {
        return currentStrategyLabel;
    }
    
    public void setFineService(FineService fineService) {
        this.fineService = fineService;
        updateCurrentStrategyLabel();
    }
    
    public void setReportService(ReportService reportService) {
        this.reportService = reportService;
        refreshData();
    }
}
