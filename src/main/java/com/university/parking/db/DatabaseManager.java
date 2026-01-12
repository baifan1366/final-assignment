package com.university.parking.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Singleton class for managing SQLite database connections.
 * Handles connection creation, table initialization, and connection cleanup.
 */
public class DatabaseManager {
    
    private static DatabaseManager instance;
    private Connection connection;
    private String databaseUrl;
    
    private static final String DEFAULT_DB_PATH = "parking_lot.db";
    
    /**
     * Private constructor for singleton pattern.
     */
    private DatabaseManager() {
        this.databaseUrl = "jdbc:sqlite:" + DEFAULT_DB_PATH;
    }
    
    /**
     * Private constructor for testing with custom database path.
     * @param dbPath the path to the database file (use ":memory:" for in-memory)
     */
    private DatabaseManager(String dbPath) {
        this.databaseUrl = "jdbc:sqlite:" + dbPath;
    }
    
    /**
     * Gets the singleton instance of DatabaseManager.
     * @return the DatabaseManager instance
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    /**
     * Creates a new DatabaseManager instance for testing purposes.
     * This allows tests to use in-memory databases.
     * @param dbPath the path to the database file (use ":memory:" for in-memory)
     * @return a new DatabaseManager instance
     */
    public static DatabaseManager createForTesting(String dbPath) {
        return new DatabaseManager(dbPath);
    }
    
    /**
     * Resets the singleton instance. Used for testing purposes.
     */
    public static synchronized void resetInstance() {
        if (instance != null) {
            instance.closeConnection();
            instance = null;
        }
    }
    
    /**
     * Gets a connection to the database.
     * Creates a new connection if one doesn't exist or is closed.
     * @return the database connection
     * @throws SQLException if connection cannot be established
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(databaseUrl);
        }
        return connection;
    }
    
    /**
     * Initializes the database by creating all required tables if they don't exist.
     * Tables created: parking_spot, vehicle, ticket, fine, payment
     * @throws SQLException if table creation fails
     */
    public void initializeDatabase() throws SQLException {
        Connection conn = getConnection();
        
        try (Statement stmt = conn.createStatement()) {
            // Create parking_spot table
            stmt.execute(CREATE_PARKING_SPOT_TABLE);
            
            // Create vehicle table
            stmt.execute(CREATE_VEHICLE_TABLE);
            
            // Create ticket table
            stmt.execute(CREATE_TICKET_TABLE);
            
            // Create fine table
            stmt.execute(CREATE_FINE_TABLE);
            
            // Create payment table
            stmt.execute(CREATE_PAYMENT_TABLE);
        }
    }
    
    /**
     * Closes the database connection.
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                // Log error but don't throw - we're cleaning up
                System.err.println("Error closing database connection: " + e.getMessage());
            }
            connection = null;
        }
    }
    
    /**
     * Checks if the database connection is active.
     * @return true if connected, false otherwise
     */
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
    
    // SQL statements for table creation
    
    private static final String CREATE_PARKING_SPOT_TABLE = 
        "CREATE TABLE IF NOT EXISTS parking_spot (" +
        "spot_id TEXT PRIMARY KEY, " +
        "floor_id TEXT NOT NULL, " +
        "type TEXT NOT NULL, " +
        "status TEXT NOT NULL DEFAULT 'AVAILABLE', " +
        "hourly_rate REAL NOT NULL, " +
        "current_vehicle_plate TEXT)";
    
    private static final String CREATE_VEHICLE_TABLE = 
        "CREATE TABLE IF NOT EXISTS vehicle (" +
        "license_plate TEXT PRIMARY KEY, " +
        "vehicle_type TEXT NOT NULL, " +
        "entry_time TEXT, " +
        "exit_time TEXT, " +
        "spot_id TEXT, " +
        "FOREIGN KEY (spot_id) REFERENCES parking_spot(spot_id))";
    
    private static final String CREATE_TICKET_TABLE = 
        "CREATE TABLE IF NOT EXISTS ticket (" +
        "ticket_id TEXT PRIMARY KEY, " +
        "license_plate TEXT NOT NULL, " +
        "spot_id TEXT NOT NULL, " +
        "entry_time TEXT NOT NULL, " +
        "FOREIGN KEY (license_plate) REFERENCES vehicle(license_plate), " +
        "FOREIGN KEY (spot_id) REFERENCES parking_spot(spot_id))";
    
    private static final String CREATE_FINE_TABLE = 
        "CREATE TABLE IF NOT EXISTS fine (" +
        "fine_id TEXT PRIMARY KEY, " +
        "license_plate TEXT NOT NULL, " +
        "amount REAL NOT NULL, " +
        "reason TEXT, " +
        "issued_time TEXT NOT NULL, " +
        "paid INTEGER NOT NULL DEFAULT 0)";
    
    private static final String CREATE_PAYMENT_TABLE = 
        "CREATE TABLE IF NOT EXISTS payment (" +
        "payment_id TEXT PRIMARY KEY, " +
        "amount REAL NOT NULL, " +
        "method TEXT NOT NULL, " +
        "payment_time TEXT NOT NULL, " +
        "license_plate TEXT, " +
        "ticket_id TEXT, " +
        "FOREIGN KEY (license_plate) REFERENCES vehicle(license_plate), " +
        "FOREIGN KEY (ticket_id) REFERENCES ticket(ticket_id))";
}
