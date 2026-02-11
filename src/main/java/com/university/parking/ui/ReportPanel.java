package com.university.parking.ui;

import com.university.parking.domain.Fine;
import com.university.parking.domain.ParkingSpot;
import com.university.parking.domain.Vehicle;
import com.university.parking.service.ReportService;
import com.university.parking.ui.components.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Report panel for viewing parking lot reports with modern UI design.
 */
public class ReportPanel extends JPanel {
    
    private ReportService reportService;
    
    private JTabbedPane reportTabbedPane;
    
    // Currently Parked tab
    private JPanel currentlyParkedPanel;
    private StyledTable parkedVehiclesTable;
    private DefaultTableModel parkedVehiclesTableModel;
    private StyledButton refreshParkedButton;
    private JLabel parkedCountLabel;
    
    // Revenue Report tab
    private JPanel revenueReportPanel;
    private StyledTable revenueTable;
    private DefaultTableModel revenueTableModel;
    private StyledButton refreshRevenueButton;
    private JLabel totalRevenueLabel;
    
    // Occupancy Report tab
    private JPanel occupancyReportPanel;
    private StyledTable occupancyTable;
    private DefaultTableModel occupancyTableModel;
    private StyledButton refreshOccupancyButton;
    private JLabel occupancyRateLabel;
    
    // Outstanding Fines tab
    private JPanel outstandingFinesPanel;
    private StyledTable finesTable;
    private DefaultTableModel finesTableModel;
    private StyledButton refreshFinesButton;
    private JLabel totalFinesLabel;
    
    private static final DateTimeFormatter DATE_TIME_FORMATTER = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public ReportPanel() {
        initializePanel();
        initializeComponents();
        layoutComponents();
    }
    
    public ReportPanel(ReportService reportService) {
        this.reportService = reportService;
        initializePanel();
        initializeComponents();
        layoutComponents();
        refreshAllData();
    }
    
    private void initializePanel() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BG_MAIN);
        setOpaque(false);
    }
    
    private void initializeComponents() {
        reportTabbedPane = createStyledTabbedPane();
        
        currentlyParkedPanel = createCurrentlyParkedPanel();
        revenueReportPanel = createRevenueReportPanel();
        occupancyReportPanel = createOccupancyReportPanel();
        outstandingFinesPanel = createOutstandingFinesPanel();
    }
    
    private JTabbedPane createStyledTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIConstants.BODY_BOLD);
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setForeground(UIConstants.TEXT_PRIMARY);
        tabbedPane.setBorder(BorderFactory.createEmptyBorder());
        return tabbedPane;
    }

    private JPanel createCurrentlyParkedPanel() {
        CardPanel card = new CardPanel();
        card.setContentLayout(new BorderLayout(0, UIConstants.SPACING_MD));
        JPanel content = card.getContentPanel();
        
        // Header
        JPanel headerPanel = createReportHeader("Currently Parked Vehicles");
        refreshParkedButton = new StyledButton("Refresh", StyledButton.ButtonType.SECONDARY);
        refreshParkedButton.addActionListener(e -> refreshCurrentlyParkedVehicles());
        headerPanel.add(refreshParkedButton, BorderLayout.EAST);
        content.add(headerPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"License Plate", "Vehicle Type", "Entry Time", "Spot ID"};
        parkedVehiclesTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        parkedVehiclesTable = new StyledTable(parkedVehiclesTableModel);
        parkedVehiclesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        content.add(parkedVehiclesTable.createScrollPane(), BorderLayout.CENTER);
        
        // Footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footerPanel.setOpaque(false);
        parkedCountLabel = new JLabel("Total parked: 0");
        parkedCountLabel.setFont(UIConstants.BODY_BOLD);
        parkedCountLabel.setForeground(UIConstants.TEXT_SECONDARY);
        footerPanel.add(parkedCountLabel);
        content.add(footerPanel, BorderLayout.SOUTH);
        
        return card;
    }
    
    private JPanel createRevenueReportPanel() {
        CardPanel card = new CardPanel();
        card.setContentLayout(new BorderLayout(0, UIConstants.SPACING_MD));
        JPanel content = card.getContentPanel();
        
        // Header
        JPanel headerPanel = createReportHeader("Revenue Report");
        refreshRevenueButton = new StyledButton("Refresh", StyledButton.ButtonType.SECONDARY);
        refreshRevenueButton.addActionListener(e -> refreshRevenueReport());
        headerPanel.add(refreshRevenueButton, BorderLayout.EAST);
        content.add(headerPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"Report Type", "Amount (RM)"};
        revenueTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        revenueTable = new StyledTable(revenueTableModel);
        revenueTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        content.add(revenueTable.createScrollPane(), BorderLayout.CENTER);
        
        // Footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footerPanel.setOpaque(false);
        totalRevenueLabel = new JLabel("Total Revenue (Today): RM 0.00");
        totalRevenueLabel.setFont(UIConstants.BODY_BOLD);
        totalRevenueLabel.setForeground(UIConstants.SUCCESS);
        footerPanel.add(totalRevenueLabel);
        content.add(footerPanel, BorderLayout.SOUTH);
        
        return card;
    }
    
    private JPanel createOccupancyReportPanel() {
        CardPanel card = new CardPanel();
        card.setContentLayout(new BorderLayout(0, UIConstants.SPACING_MD));
        JPanel content = card.getContentPanel();
        
        // Header
        JPanel headerPanel = createReportHeader("Occupancy Report");
        refreshOccupancyButton = new StyledButton("Refresh", StyledButton.ButtonType.SECONDARY);
        refreshOccupancyButton.addActionListener(e -> refreshOccupancyReport());
        headerPanel.add(refreshOccupancyButton, BorderLayout.EAST);
        content.add(headerPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"Spot Type", "Total", "Available", "Occupied", "Occupancy %"};
        occupancyTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        occupancyTable = new StyledTable(occupancyTableModel);
        occupancyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        content.add(occupancyTable.createScrollPane(), BorderLayout.CENTER);
        
        // Footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footerPanel.setOpaque(false);
        occupancyRateLabel = new JLabel("Overall Occupancy Rate: 0.0%");
        occupancyRateLabel.setFont(UIConstants.BODY_BOLD);
        occupancyRateLabel.setForeground(UIConstants.INFO);
        footerPanel.add(occupancyRateLabel);
        content.add(footerPanel, BorderLayout.SOUTH);
        
        return card;
    }
    
    private JPanel createOutstandingFinesPanel() {
        CardPanel card = new CardPanel();
        card.setContentLayout(new BorderLayout(0, UIConstants.SPACING_MD));
        JPanel content = card.getContentPanel();
        
        // Header
        JPanel headerPanel = createReportHeader("Outstanding Fines");
        refreshFinesButton = new StyledButton("Refresh", StyledButton.ButtonType.SECONDARY);
        refreshFinesButton.addActionListener(e -> refreshOutstandingFines());
        headerPanel.add(refreshFinesButton, BorderLayout.EAST);
        content.add(headerPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"Fine ID", "License Plate", "Amount (RM)", "Reason", "Issued Time"};
        finesTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        finesTable = new StyledTable(finesTableModel);
        finesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        content.add(finesTable.createScrollPane(), BorderLayout.CENTER);
        
        // Footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footerPanel.setOpaque(false);
        totalFinesLabel = new JLabel("Total Outstanding: RM 0.00");
        totalFinesLabel.setFont(UIConstants.BODY_BOLD);
        totalFinesLabel.setForeground(UIConstants.DANGER);
        footerPanel.add(totalFinesLabel);
        content.add(footerPanel, BorderLayout.SOUTH);
        
        return card;
    }
    
    private JPanel createReportHeader(String title) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIConstants.TITLE_SMALL);
        titleLabel.setForeground(UIConstants.TEXT_PRIMARY);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        return headerPanel;
    }
    
    private void layoutComponents() {
        reportTabbedPane.addTab("Currently Parked", currentlyParkedPanel);
        reportTabbedPane.addTab("Revenue Report", revenueReportPanel);
        reportTabbedPane.addTab("Occupancy Report", occupancyReportPanel);
        reportTabbedPane.addTab("Outstanding Fines", outstandingFinesPanel);
        
        add(reportTabbedPane, BorderLayout.CENTER);
    }

    public void refreshAllData() {
        refreshCurrentlyParkedVehicles();
        refreshRevenueReport();
        refreshOccupancyReport();
        refreshOutstandingFines();
    }
    
    private void refreshCurrentlyParkedVehicles() {
        parkedVehiclesTableModel.setRowCount(0);
        if (reportService == null) return;
        
        try {
            List<Vehicle> parkedVehicles = reportService.getCurrentlyParkedVehicles();
            List<ParkingSpot> allSpots = reportService.getAllSpots();
            
            for (Vehicle vehicle : parkedVehicles) {
                String spotId = findSpotIdForVehicle(vehicle.getLicensePlate(), allSpots);
                String entryTimeStr = vehicle.getEntryTime() != null 
                        ? vehicle.getEntryTime().format(DATE_TIME_FORMATTER) 
                        : "N/A";
                
                Object[] row = {
                    vehicle.getLicensePlate(),
                    vehicle.getVehicleType().toString(),
                    entryTimeStr,
                    spotId != null ? spotId : "N/A"
                };
                parkedVehiclesTableModel.addRow(row);
            }
            
            parkedCountLabel.setText("Total parked: " + parkedVehicles.size());
            
        } catch (Exception e) {
            showError("Error loading parked vehicles: " + e.getMessage());
        }
    }
    
    private String findSpotIdForVehicle(String licensePlate, List<ParkingSpot> spots) {
        for (ParkingSpot spot : spots) {
            if (licensePlate.equals(spot.getCurrentVehiclePlate())) {
                return spot.getSpotId();
            }
        }
        return null;
    }
    
    private void refreshRevenueReport() {
        revenueTableModel.setRowCount(0);
        if (reportService == null) return;
        
        try {
            LocalDate today = LocalDate.now();
            double todayRevenue = reportService.getTotalRevenue(today, today);
            
            LocalDate weekStart = today.minusDays(7);
            double weekRevenue = reportService.getTotalRevenue(weekStart, today);
            
            LocalDate monthStart = today.withDayOfMonth(1);
            double monthRevenue = reportService.getTotalRevenue(monthStart, today);
            
            revenueTableModel.addRow(new Object[]{"Today's Revenue", String.format("%.2f", todayRevenue)});
            revenueTableModel.addRow(new Object[]{"Last 7 Days Revenue", String.format("%.2f", weekRevenue)});
            revenueTableModel.addRow(new Object[]{"This Month Revenue", String.format("%.2f", monthRevenue)});
            
            totalRevenueLabel.setText(String.format("Total Revenue (Today): RM %.2f", todayRevenue));
            
        } catch (Exception e) {
            showError("Error loading revenue report: " + e.getMessage());
        }
    }
    
    private void refreshOccupancyReport() {
        occupancyTableModel.setRowCount(0);
        if (reportService == null) return;
        
        try {
            List<ParkingSpot> allSpots = reportService.getAllSpots();
            
            int compactTotal = 0, compactOccupied = 0;
            int regularTotal = 0, regularOccupied = 0;
            int handicappedTotal = 0, handicappedOccupied = 0;
            int reservedTotal = 0, reservedOccupied = 0;
            
            for (ParkingSpot spot : allSpots) {
                switch (spot.getType()) {
                    case COMPACT:
                        compactTotal++;
                        if (!spot.isAvailable()) compactOccupied++;
                        break;
                    case REGULAR:
                        regularTotal++;
                        if (!spot.isAvailable()) regularOccupied++;
                        break;
                    case HANDICAPPED:
                        handicappedTotal++;
                        if (!spot.isAvailable()) handicappedOccupied++;
                        break;
                    case RESERVED:
                        reservedTotal++;
                        if (!spot.isAvailable()) reservedOccupied++;
                        break;
                    case ELECTRIC:
                        // Future-proof: Electric spots counted as regular for now
                        regularTotal++;
                        if (!spot.isAvailable()) regularOccupied++;
                        break;
                }
            }
            
            addOccupancyRow("COMPACT", compactTotal, compactOccupied);
            addOccupancyRow("REGULAR", regularTotal, regularOccupied);
            addOccupancyRow("HANDICAPPED", handicappedTotal, handicappedOccupied);
            addOccupancyRow("RESERVED", reservedTotal, reservedOccupied);
            
            int totalSpots = compactTotal + regularTotal + handicappedTotal + reservedTotal;
            int totalOccupied = compactOccupied + regularOccupied + handicappedOccupied + reservedOccupied;
            addOccupancyRow("TOTAL", totalSpots, totalOccupied);
            
            double overallRate = reportService.getOccupancyRate() * 100;
            occupancyRateLabel.setText(String.format("Overall Occupancy Rate: %.1f%%", overallRate));
            
        } catch (Exception e) {
            showError("Error loading occupancy report: " + e.getMessage());
        }
    }
    
    private void addOccupancyRow(String spotType, int total, int occupied) {
        int available = total - occupied;
        double occupancyPercent = total > 0 ? (double) occupied / total * 100 : 0;
        
        Object[] row = {
            spotType,
            total,
            available,
            occupied,
            String.format("%.1f%%", occupancyPercent)
        };
        occupancyTableModel.addRow(row);
    }
    
    private void refreshOutstandingFines() {
        finesTableModel.setRowCount(0);
        if (reportService == null) return;
        
        try {
            List<Fine> outstandingFines = reportService.getOutstandingFines();
            double totalAmount = 0;
            
            for (Fine fine : outstandingFines) {
                String issuedTimeStr = fine.getIssuedTime() != null 
                        ? fine.getIssuedTime().format(DATE_TIME_FORMATTER) 
                        : "N/A";
                
                Object[] row = {
                    fine.getFineId(),
                    fine.getLicensePlate(),
                    String.format("%.2f", fine.getAmount()),
                    fine.getReason() != null ? fine.getReason() : "N/A",
                    issuedTimeStr
                };
                finesTableModel.addRow(row);
                totalAmount += fine.getAmount();
            }
            
            totalFinesLabel.setText(String.format("Total Outstanding: RM %.2f", totalAmount));
            
        } catch (Exception e) {
            showError("Error loading outstanding fines: " + e.getMessage());
        }
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Getters
    
    public JTabbedPane getReportTabbedPane() {
        return reportTabbedPane;
    }
    
    public JPanel getCurrentlyParkedPanel() {
        return currentlyParkedPanel;
    }
    
    public JPanel getRevenueReportPanel() {
        return revenueReportPanel;
    }
    
    public JPanel getOccupancyReportPanel() {
        return occupancyReportPanel;
    }
    
    public JPanel getOutstandingFinesPanel() {
        return outstandingFinesPanel;
    }
    
    public JTable getParkedVehiclesTable() {
        return parkedVehiclesTable;
    }
    
    public JTable getRevenueTable() {
        return revenueTable;
    }
    
    public JTable getOccupancyTable() {
        return occupancyTable;
    }
    
    public JTable getFinesTable() {
        return finesTable;
    }
    
    public JButton getRefreshParkedButton() {
        return refreshParkedButton;
    }
    
    public JButton getRefreshRevenueButton() {
        return refreshRevenueButton;
    }
    
    public JButton getRefreshOccupancyButton() {
        return refreshOccupancyButton;
    }
    
    public JButton getRefreshFinesButton() {
        return refreshFinesButton;
    }
    
    public JLabel getTotalRevenueLabel() {
        return totalRevenueLabel;
    }
    
    public JLabel getOccupancyRateLabel() {
        return occupancyRateLabel;
    }
    
    public JLabel getTotalFinesLabel() {
        return totalFinesLabel;
    }
    
    public void setReportService(ReportService reportService) {
        this.reportService = reportService;
        refreshAllData();
    }
    
    public ReportService getReportService() {
        return reportService;
    }
}
