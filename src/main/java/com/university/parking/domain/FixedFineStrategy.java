package com.university.parking.domain;

/**
 * Fixed fine strategy that charges a flat amount for overstaying.
 * PDF requirement: Flat RM50 fine for overstaying.
 */
public class FixedFineStrategy implements FineStrategy {
    
    private static final double DEFAULT_FIXED_AMOUNT = 50.0;
    
    private final double fixedAmount;
    private final double maxCap;
    
    /**
     * Creates a FixedFineStrategy with default amount (RM50).
     */
    public FixedFineStrategy() {
        this.fixedAmount = DEFAULT_FIXED_AMOUNT;
        this.maxCap = DEFAULT_FIXED_AMOUNT;
    }
    
    /**
     * Creates a FixedFineStrategy with specified amount and max cap.
     * 
     * @param fixedAmount the flat fine amount
     * @param maxCap the maximum fine amount (typically same as fixedAmount)
     */
    public FixedFineStrategy(double fixedAmount, double maxCap) {
        this.fixedAmount = fixedAmount;
        this.maxCap = maxCap;
    }
    
    @Override
    public double calculateFine(int overstayHours) {
        if (overstayHours <= 0) {
            return 0.0;
        }
        // Fixed amount regardless of duration
        return Math.min(fixedAmount, maxCap);
    }
    
    @Override
    public double getMaxCap() {
        return maxCap;
    }
    
    /**
     * Gets the fixed amount for this strategy.
     * 
     * @return the fixed fine amount
     */
    public double getFixedAmount() {
        return fixedAmount;
    }
}
