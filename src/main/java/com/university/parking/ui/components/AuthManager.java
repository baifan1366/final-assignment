package com.university.parking.ui.components;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages authentication attempts and lockout for admin panels.
 * Tracks failed login attempts per day and enforces 3-attempt limit.
 */
public class AuthManager {
    
    private static final String ADMIN_PASSWORD = "admin1!";
    private static final int MAX_ATTEMPTS = 3;
    
    private static AuthManager instance;
    
    private Map<LocalDate, Integer> attemptsByDate;
    private LocalDate lastAttemptDate;
    private int todayAttempts;
    
    private AuthManager() {
        attemptsByDate = new HashMap<>();
        lastAttemptDate = LocalDate.now();
        todayAttempts = 0;
    }
    
    /**
     * Gets the singleton instance of AuthManager.
     */
    public static synchronized AuthManager getInstance() {
        if (instance == null) {
            instance = new AuthManager();
        }
        return instance;
    }
    
    /**
     * Checks if the user is currently locked out.
     * Resets attempts if it's a new day.
     */
    public boolean isLockedOut() {
        LocalDate today = LocalDate.now();
        
        // Reset attempts if it's a new day
        if (!today.equals(lastAttemptDate)) {
            todayAttempts = 0;
            lastAttemptDate = today;
        }
        
        return todayAttempts >= MAX_ATTEMPTS;
    }
    
    /**
     * Attempts to authenticate with the provided password.
     * 
     * @param password The password to verify
     * @return true if authentication successful, false otherwise
     */
    public boolean authenticate(String password) {
        LocalDate today = LocalDate.now();
        
        // Reset attempts if it's a new day
        if (!today.equals(lastAttemptDate)) {
            todayAttempts = 0;
            lastAttemptDate = today;
        }
        
        // Check if already locked out
        if (isLockedOut()) {
            return false;
        }
        
        // Verify password
        if (ADMIN_PASSWORD.equals(password)) {
            // Successful login - reset attempts
            todayAttempts = 0;
            return true;
        } else {
            // Failed login - increment attempts
            todayAttempts++;
            return false;
        }
    }
    
    /**
     * Gets the number of remaining attempts before lockout.
     */
    public int getRemainingAttempts() {
        LocalDate today = LocalDate.now();
        
        // Reset if new day
        if (!today.equals(lastAttemptDate)) {
            todayAttempts = 0;
            lastAttemptDate = today;
        }
        
        return Math.max(0, MAX_ATTEMPTS - todayAttempts);
    }
    
    /**
     * Gets the current number of failed attempts today.
     */
    public int getTodayAttempts() {
        LocalDate today = LocalDate.now();
        
        // Reset if new day
        if (!today.equals(lastAttemptDate)) {
            todayAttempts = 0;
            lastAttemptDate = today;
        }
        
        return todayAttempts;
    }
}
