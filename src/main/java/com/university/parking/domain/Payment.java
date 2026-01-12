package com.university.parking.domain;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing a payment transaction in the parking system.
 * Requirements: 6.2
 */
public class Payment {
    
    private final String paymentId;
    private final double amount;
    private final PaymentMethod method;
    private final LocalDateTime paymentTime;
    private final String licensePlate;
    private final String ticketId;
    
    /**
     * Creates a new Payment with all fields specified.
     * 
     * @param paymentId unique identifier for the payment
     * @param amount the payment amount
     * @param method the payment method (CASH or CARD)
     * @param paymentTime the time of payment
     * @param licensePlate the license plate associated with this payment
     * @param ticketId the ticket ID associated with this payment
     */
    public Payment(String paymentId, double amount, PaymentMethod method, 
                   LocalDateTime paymentTime, String licensePlate, String ticketId) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.method = method;
        this.paymentTime = paymentTime;
        this.licensePlate = licensePlate;
        this.ticketId = ticketId;
    }
    
    /**
     * Creates a new Payment with auto-generated ID and current timestamp.
     * 
     * @param amount the payment amount
     * @param method the payment method
     * @param licensePlate the license plate associated with this payment
     * @param ticketId the ticket ID associated with this payment
     */
    public Payment(double amount, PaymentMethod method, String licensePlate, String ticketId) {
        this(generatePaymentId(), amount, method, LocalDateTime.now(), licensePlate, ticketId);
    }
    
    /**
     * Generates a unique payment ID.
     * 
     * @return a unique payment ID with format "P-{UUID}"
     */
    public static String generatePaymentId() {
        return "P-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    public String getPaymentId() {
        return paymentId;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public PaymentMethod getMethod() {
        return method;
    }
    
    public LocalDateTime getPaymentTime() {
        return paymentTime;
    }
    
    public String getLicensePlate() {
        return licensePlate;
    }
    
    public String getTicketId() {
        return ticketId;
    }
    
    @Override
    public String toString() {
        return "Payment{" +
                "paymentId='" + paymentId + '\'' +
                ", amount=" + amount +
                ", method=" + method +
                ", paymentTime=" + paymentTime +
                ", licensePlate='" + licensePlate + '\'' +
                ", ticketId='" + ticketId + '\'' +
                '}';
    }
}
