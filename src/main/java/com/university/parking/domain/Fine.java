package com.university.parking.domain;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Fine entity representing a penalty issued to a vehicle.
 * Fines are associated with license plates (not tickets) and persist across sessions.
 */
public class Fine {
    
    private final String fineId;
    private final String licensePlate;
    private final double amount;
    private final String reason;
    private final LocalDateTime issuedTime;
    private boolean paid;
    
    /**
     * Creates a new Fine with the specified details.
     * 
     * @param fineId unique identifier for the fine
     * @param licensePlate the license plate of the vehicle
     * @param amount the fine amount
     * @param reason the reason for the fine
     * @param issuedTime the time when the fine was issued
     * @param paid whether the fine has been paid
     */
    public Fine(String fineId, String licensePlate, double amount, String reason, 
                LocalDateTime issuedTime, boolean paid) {
        this.fineId = fineId;
        this.licensePlate = licensePlate;
        this.amount = amount;
        this.reason = reason;
        this.issuedTime = issuedTime;
        this.paid = paid;
    }
    
    /**
     * Creates a new unpaid Fine with auto-generated ID and current timestamp.
     * 
     * @param licensePlate the license plate of the vehicle
     * @param amount the fine amount
     * @param reason the reason for the fine
     */
    public Fine(String licensePlate, double amount, String reason) {
        this(generateFineId(), licensePlate, amount, reason, LocalDateTime.now(), false);
    }
    
    /**
     * Generates a unique fine ID.
     * 
     * @return a unique fine ID with format "F-{UUID}"
     */
    public static String generateFineId() {
        return "F-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    public String getFineId() {
        return fineId;
    }
    
    public String getLicensePlate() {
        return licensePlate;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public String getReason() {
        return reason;
    }
    
    public LocalDateTime getIssuedTime() {
        return issuedTime;
    }
    
    public boolean isPaid() {
        return paid;
    }
    
    /**
     * Marks this fine as paid.
     */
    public void markAsPaid() {
        this.paid = true;
    }
    
    @Override
    public String toString() {
        return "Fine{" +
                "fineId='" + fineId + '\'' +
                ", licensePlate='" + licensePlate + '\'' +
                ", amount=" + amount +
                ", reason='" + reason + '\'' +
                ", issuedTime=" + issuedTime +
                ", paid=" + paid +
                '}';
    }
}
