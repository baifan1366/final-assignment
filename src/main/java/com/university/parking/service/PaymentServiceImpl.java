package com.university.parking.service;

import com.university.parking.dao.PaymentDAO;
import com.university.parking.domain.Payment;
import com.university.parking.domain.PaymentMethod;
import com.university.parking.domain.Receipt;

import java.util.List;
import java.util.UUID;

/**
 * Implementation of PaymentService.
 * Handles payment recording and receipt generation.
 * Requirements: 6.1-6.5
 */
public class PaymentServiceImpl implements PaymentService {
    
    private final PaymentDAO paymentDAO;
    
    public PaymentServiceImpl(PaymentDAO paymentDAO) {
        this.paymentDAO = paymentDAO;
    }
    
    @Override
    public Payment processPayment(double amount, PaymentMethod method, String licensePlate, String ticketId) {
        // Validate inputs (Requirements 6.2)
        if (amount < 0) {
            throw new IllegalArgumentException("Payment amount cannot be negative");
        }
        if (method == null) {
            throw new IllegalArgumentException("Payment method cannot be null");
        }
        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            throw new IllegalArgumentException("License plate cannot be empty");
        }
        
        // Create and save payment
        Payment payment = new Payment(amount, method, licensePlate, ticketId);
        paymentDAO.save(payment);
        
        return payment;
    }
    
    @Override
    public Receipt generateReceipt(Payment payment, double parkingFee, double fineAmount) {
        if (payment == null) {
            throw new IllegalArgumentException("Payment cannot be null");
        }
        
        // Generate receipt ID
        String receiptId = "R-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        // Create receipt with all required fields (Requirements 6.4)
        return new Receipt(
                receiptId,
                payment.getLicensePlate(),
                parkingFee,
                fineAmount,
                payment.getMethod(),
                payment.getPaymentTime()
        );
    }
    
    @Override
    public List<Payment> getPaymentsByLicensePlate(String licensePlate) {
        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            throw new IllegalArgumentException("License plate cannot be empty");
        }
        return paymentDAO.findByLicensePlate(licensePlate);
    }
    
    @Override
    public Payment getPaymentById(String paymentId) {
        if (paymentId == null || paymentId.trim().isEmpty()) {
            return null;
        }
        return paymentDAO.findById(paymentId);
    }
}
