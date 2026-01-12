package com.university.parking.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manager class for handling fine calculations and tracking.
 * Uses the Strategy pattern to support different fine calculation schemes.
 */
public class FineManager {
    
    private FineStrategy currentStrategy;
    private final List<Fine> fines;
    
    /**
     * Creates a FineManager with a default FixedFineStrategy.
     */
    public FineManager() {
        this.currentStrategy = new FixedFineStrategy(10.0, 100.0);
        this.fines = new ArrayList<>();
    }
    
    /**
     * Creates a FineManager with the specified strategy.
     * 
     * @param strategy the fine calculation strategy to use
     */
    public FineManager(FineStrategy strategy) {
        this.currentStrategy = strategy;
        this.fines = new ArrayList<>();
    }
    
    /**
     * Sets the fine calculation strategy.
     * 
     * @param strategy the new strategy to use for fine calculations
     */
    public void setStrategy(FineStrategy strategy) {
        this.currentStrategy = strategy;
    }
    
    /**
     * Gets the current fine calculation strategy.
     * 
     * @return the current strategy
     */
    public FineStrategy getCurrentStrategy() {
        return currentStrategy;
    }
    
    /**
     * Calculates a fine based on the current strategy.
     * 
     * @param duration the duration in hours for which the fine is calculated
     * @return the calculated fine amount
     */
    public double calculateFine(int duration) {
        return currentStrategy.calculateFine(duration);
    }
    
    /**
     * Adds a fine to the manager.
     * 
     * @param fine the fine to add
     */
    public void addFine(Fine fine) {
        fines.add(fine);
    }
    
    /**
     * Gets all unpaid fines for a given license plate.
     * 
     * @param licensePlate the license plate to query
     * @return list of unpaid fines for the license plate
     */
    public List<Fine> getUnpaidFines(String licensePlate) {
        return fines.stream()
                .filter(fine -> fine.getLicensePlate().equals(licensePlate))
                .filter(fine -> !fine.isPaid())
                .collect(Collectors.toList());
    }
    
    /**
     * Gets the total amount of unpaid fines for a given license plate.
     * 
     * @param licensePlate the license plate to query
     * @return the sum of all unpaid fine amounts
     */
    public double getTotalUnpaidAmount(String licensePlate) {
        return getUnpaidFines(licensePlate).stream()
                .mapToDouble(Fine::getAmount)
                .sum();
    }
    
    /**
     * Gets all fines managed by this manager.
     * 
     * @return list of all fines
     */
    public List<Fine> getAllFines() {
        return new ArrayList<>(fines);
    }
    
    /**
     * Marks a fine as paid by its ID.
     * 
     * @param fineId the ID of the fine to mark as paid
     * @return true if the fine was found and marked as paid, false otherwise
     */
    public boolean markFineAsPaid(String fineId) {
        for (Fine fine : fines) {
            if (fine.getFineId().equals(fineId)) {
                fine.markAsPaid();
                return true;
            }
        }
        return false;
    }
}
