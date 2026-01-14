package com.university.parking.domain;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Value object representing a receipt generated upon vehicle exit and payment.
 * Requirements: 6.4
 */
public class Receipt {
    private final String receiptId;
    private final String licensePlate;
    private final LocalDateTime entryTime;
    private final LocalDateTime exitTime;
    private final int durationHours;
    private final double hourlyRate;
    private final double parkingFee;
    private final double fineAmount;
    private final double totalAmount;
    private final PaymentMethod paymentMethod;
    private final LocalDateTime timestamp;

    /**
     * Creates a receipt with full details.
     */
    public Receipt(String receiptId, String licensePlate, LocalDateTime entryTime,
                   LocalDateTime exitTime, int durationHours, double hourlyRate,
                   double parkingFee, double fineAmount, PaymentMethod paymentMethod) {
        this.receiptId = receiptId;
        this.licensePlate = licensePlate;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.durationHours = durationHours;
        this.hourlyRate = hourlyRate;
        this.parkingFee = parkingFee;
        this.fineAmount = fineAmount;
        this.totalAmount = parkingFee + fineAmount;
        this.paymentMethod = paymentMethod;
        this.timestamp = exitTime;
    }

    /**
     * Legacy constructor for backward compatibility.
     */
    public Receipt(String receiptId, String licensePlate, double parkingFee, 
                   double fineAmount, PaymentMethod paymentMethod, LocalDateTime timestamp) {
        this.receiptId = receiptId;
        this.licensePlate = licensePlate;
        this.entryTime = null;
        this.exitTime = timestamp;
        this.durationHours = 0;
        this.hourlyRate = 0.0;
        this.parkingFee = parkingFee;
        this.fineAmount = fineAmount;
        this.totalAmount = parkingFee + fineAmount;
        this.paymentMethod = paymentMethod;
        this.timestamp = timestamp;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public int getDurationHours() {
        return durationHours;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public double getParkingFee() {
        return parkingFee;
    }

    public double getFineAmount() {
        return fineAmount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Gets the fee breakdown string (hours x rate = fee).
     */
    public String getFeeBreakdown() {
        if (durationHours > 0 && hourlyRate > 0) {
            return String.format("%d hours × RM%.2f = RM%.2f", durationHours, hourlyRate, parkingFee);
        }
        return String.format("RM%.2f", parkingFee);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Receipt receipt = (Receipt) o;
        return Objects.equals(receiptId, receipt.receiptId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(receiptId);
    }

    @Override
    public String toString() {
        return "Receipt{" +
                "receiptId='" + receiptId + '\'' +
                ", licensePlate='" + licensePlate + '\'' +
                ", parkingFee=" + parkingFee +
                ", fineAmount=" + fineAmount +
                ", totalAmount=" + totalAmount +
                ", paymentMethod=" + paymentMethod +
                ", timestamp=" + timestamp +
                '}';
    }
}
