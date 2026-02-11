package com.university.parking;

import com.university.parking.dao.*;
import com.university.parking.db.DatabaseManager;
import com.university.parking.domain.*;
import com.university.parking.service.*;
import com.university.parking.ui.*;

import javax.swing.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Main application entry point for the University Parking Lot Management System.
 * Initializes database, creates sample data, wires up services, and launches the UI.
 * Requirements: 9.4, 1.1, 1.2, 1.3
 */
public class Application {
    
    private DatabaseManager dbManager;
    private ParkingSpotDAO parkingSpotDAO;
    private VehicleDAO vehicleDAO;
    private TicketDAO ticketDAO;
    private FineDAO fineDAO;
    private PaymentDAO paymentDAO;
    
    private ParkingService parkingService;
    private FineService fineService;
    private PaymentService paymentService;
    private ReportService reportService;
    
    /**
     * Main entry point for the application.
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        // Set look and feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Fall back to default look and feel
            System.err.println("Could not set system look and feel: " + e.getMessage());
        }
        
        // Launch application on EDT
        SwingUtilities.invokeLater(() -> {
            Application app = new Application();
            app.start();
        });
    }
    
    /**
     * Starts the application by initializing all components and launching the UI.
     */
    public void start() {
        try {
            // Initialize database (Requirements 9.4)
            initializeDatabase();
            
            // Initialize DAOs
            initializeDAOs();
            
            // Initialize sample data (Requirements 1.1, 1.2, 1.3)
            initializeSampleData();
            
            // Initialize services
            initializeServices();
            
            // Launch UI
            launchUI();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                "Failed to start application: " + e.getMessage(),
                "Startup Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Initializes the database connection and creates tables.
     * Requirements: 9.4
     */
    private void initializeDatabase() throws SQLException {
        System.out.println("Initializing database...");
        dbManager = DatabaseManager.getInstance();
        dbManager.initializeDatabase();
        System.out.println("Database initialized successfully.");
    }
    
    /**
     * Initializes all DAO instances.
     */
    private void initializeDAOs() {
        System.out.println("Initializing DAOs...");
        parkingSpotDAO = new ParkingSpotDAOImpl(dbManager);
        vehicleDAO = new VehicleDAOImpl(dbManager);
        ticketDAO = new TicketDAOImpl(dbManager);
        fineDAO = new FineDAOImpl(dbManager);
        paymentDAO = new PaymentDAOImpl(dbManager);
        System.out.println("DAOs initialized successfully.");
    }
    
    /**
     * Initializes sample parking lot data with floors and spots.
     * Only creates data if the database is empty.
     * Requirements: 1.1, 1.2, 1.3
     * PDF: 5 floors, Spot ID format "F1-R1-S1" (Floor-Row-Spot)
     */
    private void initializeSampleData() {
        System.out.println("Checking for existing data...");
        
        // Check if data already exists
        if (!parkingSpotDAO.findAll().isEmpty()) {
            System.out.println("Sample data already exists, skipping initialization.");
            return;
        }
        
        System.out.println("Clearing existing parking spot data...");
        clearAllSpots();
        
        System.out.println("Creating sample parking lot data (5 floors)...");
        
        // Standard 5-floor parking structure with consistent row and spot numbering
        
        // Floor 1 (Ground Floor) - 3 rows, 8 spots per row = 24 spots
        // Row 1: Compact vehicles
        createSpot("F1-R1-S1", SpotType.COMPACT, 2.0);
        createSpot("F1-R1-S2", SpotType.COMPACT, 2.0);
        createSpot("F1-R1-S3", SpotType.COMPACT, 2.0);
        createSpot("F1-R1-S4", SpotType.COMPACT, 2.0);
        createSpot("F1-R1-S5", SpotType.COMPACT, 2.0);
        createSpot("F1-R1-S6", SpotType.COMPACT, 2.0);
        createSpot("F1-R1-S7", SpotType.COMPACT, 2.0);
        createSpot("F1-R1-S8", SpotType.COMPACT, 2.0);
        
        // Row 2: Regular vehicles
        createSpot("F1-R2-S1", SpotType.REGULAR, 5.0);
        createSpot("F1-R2-S2", SpotType.REGULAR, 5.0);
        createSpot("F1-R2-S3", SpotType.REGULAR, 5.0);
        createSpot("F1-R2-S4", SpotType.REGULAR, 5.0);
        createSpot("F1-R2-S5", SpotType.REGULAR, 5.0);
        createSpot("F1-R2-S6", SpotType.REGULAR, 5.0);
        createSpot("F1-R2-S7", SpotType.REGULAR, 5.0);
        createSpot("F1-R2-S8", SpotType.REGULAR, 5.0);
        
        // Row 3: Handicapped and Regular mix
        createSpot("F1-R3-S1", SpotType.HANDICAPPED, 2.0);
        createSpot("F1-R3-S2", SpotType.HANDICAPPED, 2.0);
        createSpot("F1-R3-S3", SpotType.HANDICAPPED, 2.0);
        createSpot("F1-R3-S4", SpotType.HANDICAPPED, 2.0);
        createSpot("F1-R3-S5", SpotType.REGULAR, 5.0);
        createSpot("F1-R3-S6", SpotType.REGULAR, 5.0);
        createSpot("F1-R3-S7", SpotType.REGULAR, 5.0);
        createSpot("F1-R3-S8", SpotType.REGULAR, 5.0);
        
        // Floor 2 - 3 rows, 10 spots per row = 30 spots
        // Row 1: Compact and Regular mix
        createSpot("F2-R1-S1", SpotType.COMPACT, 2.0);
        createSpot("F2-R1-S2", SpotType.COMPACT, 2.0);
        createSpot("F2-R1-S3", SpotType.COMPACT, 2.0);
        createSpot("F2-R1-S4", SpotType.COMPACT, 2.0);
        createSpot("F2-R1-S5", SpotType.REGULAR, 5.0);
        createSpot("F2-R1-S6", SpotType.REGULAR, 5.0);
        createSpot("F2-R1-S7", SpotType.REGULAR, 5.0);
        createSpot("F2-R1-S8", SpotType.REGULAR, 5.0);
        createSpot("F2-R1-S9", SpotType.REGULAR, 5.0);
        createSpot("F2-R1-S10", SpotType.REGULAR, 5.0);
        
        // Row 2: Regular vehicles
        createSpot("F2-R2-S1", SpotType.REGULAR, 5.0);
        createSpot("F2-R2-S2", SpotType.REGULAR, 5.0);
        createSpot("F2-R2-S3", SpotType.REGULAR, 5.0);
        createSpot("F2-R2-S4", SpotType.REGULAR, 5.0);
        createSpot("F2-R2-S5", SpotType.REGULAR, 5.0);
        createSpot("F2-R2-S6", SpotType.REGULAR, 5.0);
        createSpot("F2-R2-S7", SpotType.REGULAR, 5.0);
        createSpot("F2-R2-S8", SpotType.REGULAR, 5.0);
        createSpot("F2-R2-S9", SpotType.REGULAR, 5.0);
        createSpot("F2-R2-S10", SpotType.REGULAR, 5.0);
        
        // Row 3: Reserved spots
        createSpot("F2-R3-S1", SpotType.RESERVED, 10.0);
        createSpot("F2-R3-S2", SpotType.RESERVED, 10.0);
        createSpot("F2-R3-S3", SpotType.RESERVED, 10.0);
        createSpot("F2-R3-S4", SpotType.RESERVED, 10.0);
        createSpot("F2-R3-S5", SpotType.RESERVED, 10.0);
        createSpot("F2-R3-S6", SpotType.RESERVED, 10.0);
        createSpot("F2-R3-S7", SpotType.REGULAR, 5.0);
        createSpot("F2-R3-S8", SpotType.REGULAR, 5.0);
        createSpot("F2-R3-S9", SpotType.REGULAR, 5.0);
        createSpot("F2-R3-S10", SpotType.REGULAR, 5.0);
        
        // Floor 3 - 4 rows, 8 spots per row = 32 spots
        // Row 1: Compact vehicles
        for (int i = 1; i <= 8; i++) {
            createSpot("F3-R1-S" + i, SpotType.COMPACT, 2.0);
        }
        
        // Row 2: Regular vehicles
        for (int i = 1; i <= 8; i++) {
            createSpot("F3-R2-S" + i, SpotType.REGULAR, 5.0);
        }
        
        // Row 3: Regular vehicles
        for (int i = 1; i <= 8; i++) {
            createSpot("F3-R3-S" + i, SpotType.REGULAR, 5.0);
        }
        
        // Row 4: Handicapped spots
        for (int i = 1; i <= 8; i++) {
            createSpot("F3-R4-S" + i, SpotType.HANDICAPPED, 2.0);
        }
        
        // Floor 4 - 3 rows, 12 spots per row = 36 spots
        // Row 1: Regular vehicles
        for (int i = 1; i <= 12; i++) {
            createSpot("F4-R1-S" + i, SpotType.REGULAR, 5.0);
        }
        
        // Row 2: Regular vehicles
        for (int i = 1; i <= 12; i++) {
            createSpot("F4-R2-S" + i, SpotType.REGULAR, 5.0);
        }
        
        // Row 3: Reserved spots
        for (int i = 1; i <= 12; i++) {
            if (i <= 4) {
                createSpot("F4-R3-S" + i, SpotType.RESERVED, 10.0);
            } else {
                createSpot("F4-R3-S" + i, SpotType.REGULAR, 5.0);
            }
        }
        
        // Floor 5 (Top Floor) - 2 rows, 15 spots per row = 30 spots
        // Row 1: Mixed spots
        for (int i = 1; i <= 15; i++) {
            if (i <= 5) {
                createSpot("F5-R1-S" + i, SpotType.COMPACT, 2.0);
            } else if (i <= 10) {
                createSpot("F5-R1-S" + i, SpotType.REGULAR, 5.0);
            } else {
                createSpot("F5-R1-S" + i, SpotType.HANDICAPPED, 2.0);
            }
        }
        
        // Row 2: Reserved and Regular mix
        for (int i = 1; i <= 15; i++) {
            if (i <= 6) {
                createSpot("F5-R2-S" + i, SpotType.RESERVED, 10.0);
            } else {
                createSpot("F5-R2-S" + i, SpotType.REGULAR, 5.0);
            }
        }
        
        System.out.println("Sample data created successfully.");
        System.out.println("Total spots created: " + parkingSpotDAO.findAll().size());
    }
    
    /**
     * Helper method to create and save a parking spot.
     */
    private void createSpot(String spotId, SpotType type, double hourlyRate) {
        ParkingSpot spot = new ParkingSpot(spotId, type, hourlyRate);
        parkingSpotDAO.save(spot);
    }

    /**
     * Helper method to clear all parking spots from the database.
     */
    private void clearAllSpots() {
        List<ParkingSpot> allSpots = parkingSpotDAO.findAll();
        for (ParkingSpot spot : allSpots) {
            parkingSpotDAO.delete(spot.getSpotId());
        }
    }

    /**
     * Initializes all service instances with their dependencies.
     */
    private void initializeServices() {
        System.out.println("Initializing services...");
        
        // Create services with DAO dependencies
        ParkingServiceImpl parkingServiceImpl = new ParkingServiceImpl(
            parkingSpotDAO, vehicleDAO, ticketDAO, fineDAO, paymentDAO);
        
        fineService = new FineServiceImpl(fineDAO);
        // Set default fine strategy (Fixed: RM50)
        fineService.setFineStrategy(new FixedFineStrategy());
        
        // Wire FineService to ParkingService for fine calculation
        parkingServiceImpl.setFineService(fineService);
        parkingService = parkingServiceImpl;
        
        paymentService = new PaymentServiceImpl(paymentDAO);
        
        reportService = new ReportServiceImpl(
            parkingSpotDAO, vehicleDAO, fineDAO, paymentDAO);
        
        System.out.println("Services initialized successfully.");
    }
    
    /**
     * Launches the main UI frame with all panels wired to services.
     */
    private void launchUI() {
        System.out.println("Launching UI...");
        
        // Create main frame
        MainFrame mainFrame = new MainFrame();
        
        // Create and wire Entry/Exit panel
        EntryExitPanel entryExitPanel = new EntryExitPanel(
            parkingService, paymentService, fineService);
        mainFrame.setEntryExitPanel(entryExitPanel);
        
        // Create and wire Admin panel
        AdminPanel adminPanel = new AdminPanel(fineService, reportService);
        mainFrame.setAdminPanel(adminPanel);
        
        // Create and wire Report panel
        ReportPanel reportPanel = new ReportPanel(reportService);
        mainFrame.setReportPanel(reportPanel);
        
        // Add shutdown hook to close database connection
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down application...");
            if (dbManager != null) {
                dbManager.closeConnection();
            }
            System.out.println("Application shutdown complete.");
        }));
        
        // Show the main frame
        mainFrame.setVisible(true);
        
        System.out.println("Application started successfully.");
        System.out.println("University Parking Lot Management System is ready.");
    }
    
    // Getters for testing purposes
    
    /**
     * Gets the database manager.
     * @return the database manager
     */
    public DatabaseManager getDbManager() {
        return dbManager;
    }
    
    /**
     * Gets the parking service.
     * @return the parking service
     */
    public ParkingService getParkingService() {
        return parkingService;
    }
    
    /**
     * Gets the fine service.
     * @return the fine service
     */
    public FineService getFineService() {
        return fineService;
    }
    
    /**
     * Gets the payment service.
     * @return the payment service
     */
    public PaymentService getPaymentService() {
        return paymentService;
    }
    
    /**
     * Gets the report service.
     * @return the report service
     */
    public ReportService getReportService() {
        return reportService;
    }
}