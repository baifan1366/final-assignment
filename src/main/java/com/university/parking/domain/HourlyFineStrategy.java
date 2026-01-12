package com.university.parking.domain;

/**
 * Hourly fine strategy that charges per hour of overstay.
 * PDF requirement: RM20 per hour for overstaying (beyond 24-hour limit).
 */
public class HourlyFineStrategy implements FineStrategy {
    
    private static final double DEFAULT_HOURLY_RATE = 20.0;
    
    private final double hourlyRate;
    private final double maxCap;
    
    /**
     * Creates an HourlyFineStrategy with default rate (RM20/hour) and no cap.
     */
    public HourlyFineStrategy() {
        this.hourlyRate = DEFAULT_HOURLY_RATE;
        this.maxCap = Double.MAX_VALUE;
    }
    
    /**
     * Creates an HourlyFineStrategy with specified rate and max cap.
     * 
     * @param hourlyRate the fine amount per hour of overstay
     * @param maxCap the maximum fine amount
     */
    public HourlyFineStrategy(double hourlyRate, double maxCap) {
        this.hourlyRate = hourlyRate;
        this.maxCap = maxCap;
    }
    
    @Override
    public double calculateFine(int overstayHours) {
        if (overstayHours <= 0) {
            return 0.0;
        }
        double calculatedFine = hourlyRate * overstayHours;
        return Math.min(calculatedFine, maxCap);
    }
    
    @Override
    public double getMaxCap() {
        return maxCap;
    }
    
    /**
     * Gets the hourly rate for this strategy.
     * 
     * @return the hourly fine rate
     */
    public double getHourlyRate() {
        return hourlyRate;
    }
}
