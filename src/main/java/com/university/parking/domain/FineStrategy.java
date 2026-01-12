package com.university.parking.domain;

/**
 * Strategy interface for calculating fines.
 * Supports multiple fine calculation schemes: Fixed, Progressive, and Hourly.
 */
public interface FineStrategy {
    
    /**
     * Calculates the fine amount based on the duration.
     * 
     * @param duration the duration in hours for which the fine is calculated
     * @return the calculated fine amount, capped at maxCap if configured
     */
    double calculateFine(int duration);
    
    /**
     * Gets the maximum cap for the fine amount.
     * 
     * @return the maximum fine amount that can be charged
     */
    double getMaxCap();
}
