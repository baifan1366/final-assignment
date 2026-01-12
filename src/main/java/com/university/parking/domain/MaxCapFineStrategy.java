package com.university.parking.domain;

/**
 * Decorator strategy that wraps another fine strategy with a maximum cap.
 * Demonstrates extensibility - fine cannot exceed the specified maximum.
 * Example: MaxCapFineStrategy(new HourlyFineStrategy(), 500.0) caps fines at RM500.
 */
public class MaxCapFineStrategy implements FineStrategy {
    
    private final FineStrategy baseStrategy;
    private final double maxCap;
    
    /**
     * Creates a MaxCapFineStrategy that wraps another strategy.
     * 
     * @param baseStrategy the underlying strategy to use for calculation
     * @param maxCap the maximum fine amount (e.g., 500.0 for RM500 cap)
     */
    public MaxCapFineStrategy(FineStrategy baseStrategy, double maxCap) {
        if (baseStrategy == null) {
            throw new IllegalArgumentException("Base strategy cannot be null");
        }
        if (maxCap < 0) {
            throw new IllegalArgumentException("Max cap cannot be negative");
        }
        this.baseStrategy = baseStrategy;
        this.maxCap = maxCap;
    }
    
    @Override
    public double calculateFine(int overstayHours) {
        if (overstayHours <= 0) {
            return 0.0;
        }
        double calculatedFine = baseStrategy.calculateFine(overstayHours);
        return Math.min(calculatedFine, maxCap);
    }
    
    @Override
    public double getMaxCap() {
        return maxCap;
    }
    
    /**
     * Gets the underlying base strategy.
     * 
     * @return the base strategy
     */
    public FineStrategy getBaseStrategy() {
        return baseStrategy;
    }
}
