package com.university.parking.service;

import com.university.parking.domain.Payment;
import com.university.parking.domain.PaymentMethod;
import com.university.parking.domain.Receipt;

import java.util.List;

/**
 * Service interface for payment processing operations.
 * Handles payment recording and receipt generation.
 * Requirements: 6.1-6.5
 */
public interface PaymentService {
    
    /**
     * Processes a payment transaction.
     * 
     * @param amount the payment amount
     * @param method the payment method (CASH or CARD)
     * @param licensePlate the vehicle's license plate
     * @param ticketId the associated ticket ID (optional)
     * @return the processed payment
     * @throws IllegalArgumentException if inputs are invalid
     */
    Payment processPayment(double amount, PaymentMethod method, String licensePlate, String ticketId);
    
    /**
     * Generates a receipt for a completed payment.
     * 
     * @param payment the payment record
     * @param parkingFee the parking fee component
     * @param fineAmount the fine amount component
     * @return the generated receipt
     */
    Receipt generateReceipt(Payment payment, double parkingFee, double fineAmount);
    
    /**
     * Gets all payments for a specific license plate.
     * 
     * @param licensePlate the vehicle's license plate
     * @return list of payments
     */
    List<Payment> getPaymentsByLicensePlate(String licensePlate);
    
    /**
     * Gets a payment by its ID.
     * 
     * @param paymentId the payment's unique identifier
     * @return the payment if found, null otherwise
     */
    Payment getPaymentById(String paymentId);
}
