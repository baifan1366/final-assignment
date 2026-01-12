package com.university.parking.domain;

/**
 * Progressive fine strategy with tiered calculation based on overstay duration.
 * PDF requirement:
 * - First 24 hours overstay: RM50
 * - Hours 24-48 overstay: Additional RM100 (total RM150)
 * - Hours 48-72 overstay: Additional RM150 (total RM300)
 * - Above 72 hours overstay: Additional RM200 (total RM500)
 */
public class ProgressiveFineStrategy implements FineStrategy {
    
    // Tier thresholds (hours of overstay beyond 24-hour limit)
    private static final int TIER_1_HOURS = 24;
    private static final int TIER_2_HOURS = 48;
    private static final int TIER_3_HOURS = 72;
    
    // Fine amounts for each tier
    private static final double TIER_1_FINE = 50.0;
    private static final double TIER_2_FINE = 100.0;
    private static final double TIER_3_FINE = 150.0;
    private static final double TIER_4_FINE = 200.0;
    
    private final double maxCap;
    
    /**
     * Creates a ProgressiveFineStrategy with default max cap of RM500.
     */
    public ProgressiveFineStrategy() {
        this.maxCap = 500.0;
    }
    
    /**
     * Creates a ProgressiveFineStrategy with custom max cap.
     * 
     * @param maxCap the maximum fine amount
     */
    public ProgressiveFineStrategy(double maxCap) {
        this.maxCap = maxCap;
    }
    
    /**
     * Legacy constructor for backward compatibility.
     * Parameters are ignored, uses tiered calculation instead.
     */
    public ProgressiveFineStrategy(double baseAmount, double incrementRate, double maxCap) {
        this.maxCap = maxCap;
    }
    
    @Override
    public double calculateFine(int overstayHours) {
        if (overstayHours <= 0) {
            return 0.0;
        }
        
        double totalFine = 0.0;
        
        // Tier 1: First 24 hours of overstay
        if (overstayHours > 0) {
            totalFine += TIER_1_FINE;
        }
        
        // Tier 2: Hours 24-48 of overstay
        if (overstayHours > TIER_1_HOURS) {
            totalFine += TIER_2_FINE;
        }
        
        // Tier 3: Hours 48-72 of overstay
        if (overstayHours > TIER_2_HOURS) {
            totalFine += TIER_3_FINE;
        }
        
        // Tier 4: Above 72 hours of overstay
        if (overstayHours > TIER_3_HOURS) {
            totalFine += TIER_4_FINE;
        }
        
        return Math.min(totalFine, maxCap);
    }
    
    @Override
    public double getMaxCap() {
        return maxCap;
    }
}
