package com.university.parking;

import com.university.parking.dao.*;
import com.university.parking.db.DatabaseManager;
import com.university.parking.domain.*;
import com.university.parking.service.*;
import com.university.parking.ui.*;

import javax.swing.*;
import java.sql.SQLException;

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
     */
    private void initializeSampleData() {
        System.out.println("Checking for existing data...");
        
        // Check if data already exists
        if (!parkingSpotDAO.findAll().isEmpty()) {
            System.out.println("Sample data already exists, skipping initialization.");
            return;
        }
        
        System.out.println("Creating sample parking lot data...");
        
        // Create Floor 1 spots (Ground Floor)
        // Compact: RM2/hour, Regular: RM5/hour, Handicapped: RM2/hour (FREE for handicapped card holder)
        createSpot("F1-C01", SpotType.COMPACT, 2.0);
        createSpot("F1-C02", SpotType.COMPACT, 2.0);
        createSpot("F1-C03", SpotType.COMPACT, 2.0);
        createSpot("F1-C04", SpotType.COMPACT, 2.0);
        createSpot("F1-C05", SpotType.COMPACT, 2.0);
        
        createSpot("F1-R01", SpotType.REGULAR, 5.0);
        createSpot("F1-R02", SpotType.REGULAR, 5.0);
        createSpot("F1-R03", SpotType.REGULAR, 5.0);
        createSpot("F1-R04", SpotType.REGULAR, 5.0);
        createSpot("F1-R05", SpotType.REGULAR, 5.0);
        
        // Handicapped spots: RM2/hour (FREE if handicapped card holder parks here)
        createSpot("F1-H01", SpotType.HANDICAPPED, 2.0);
        createSpot("F1-H02", SpotType.HANDICAPPED, 2.0);
        
        // Create Floor 2 spots
        // Reserved: RM10/hour
        createSpot("F2-C01", SpotType.COMPACT, 2.0);
        createSpot("F2-C02", SpotType.COMPACT, 2.0);
        createSpot("F2-C03", SpotType.COMPACT, 2.0);
        createSpot("F2-C04", SpotType.COMPACT, 2.0);
        
        createSpot("F2-R01", SpotType.REGULAR, 5.0);
        createSpot("F2-R02", SpotType.REGULAR, 5.0);
        createSpot("F2-R03", SpotType.REGULAR, 5.0);
        createSpot("F2-R04", SpotType.REGULAR, 5.0);
        createSpot("F2-R05", SpotType.REGULAR, 5.0);
        createSpot("F2-R06", SpotType.REGULAR, 5.0);
        
        createSpot("F2-V01", SpotType.RESERVED, 10.0);
        createSpot("F2-V02", SpotType.RESERVED, 10.0);
        
        // Create Floor 3 spots (Top Floor)
        createSpot("F3-C01", SpotType.COMPACT, 2.0);
        createSpot("F3-C02", SpotType.COMPACT, 2.0);
        createSpot("F3-C03", SpotType.COMPACT, 2.0);
        
        createSpot("F3-R01", SpotType.REGULAR, 5.0);
        createSpot("F3-R02", SpotType.REGULAR, 5.0);
        createSpot("F3-R03", SpotType.REGULAR, 5.0);
        createSpot("F3-R04", SpotType.REGULAR, 5.0);
        
        createSpot("F3-H01", SpotType.HANDICAPPED, 2.0);
        
        createSpot("F3-V01", SpotType.RESERVED, 10.0);
        createSpot("F3-V02", SpotType.RESERVED, 10.0);
        
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
