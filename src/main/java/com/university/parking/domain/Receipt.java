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
    private final double parkingFee;
    private final double fineAmount;
    private final double totalAmount;
    private final PaymentMethod paymentMethod;
    private final LocalDateTime timestamp;

    public Receipt(String receiptId, String licensePlate, double parkingFee, 
                   double fineAmount, PaymentMethod paymentMethod, LocalDateTime timestamp) {
        this.receiptId = receiptId;
        this.licensePlate = licensePlate;
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
