package com.university.parking.ui.components;

import java.util.regex.Pattern;

/**
 * Utility class for validating user input fields.
 */
public class InputValidator {
    
    // License plate pattern: 1-3 letters followed by 1-4 digits, optionally followed by 1-2 letters
    // Examples: ABC1234, WA1234, B12AB
    private static final Pattern LICENSE_PLATE_PATTERN = 
        Pattern.compile("^[A-Z]{1,3}\\d{1,4}[A-Z]{0,2}$");
    
    // Card number: 13-16 digits
    private static final Pattern CARD_NUMBER_PATTERN = 
        Pattern.compile("^\\d{13,16}$");
    
    // CVV: 3-4 digits
    private static final Pattern CVV_PATTERN = 
        Pattern.compile("^\\d{3,4}$");
    
    // Name: letters, spaces, hyphens, apostrophes (2-50 chars)
    private static final Pattern NAME_PATTERN = 
        Pattern.compile("^[A-Za-z][A-Za-z\\s\\-']{1,49}$");
    
    /**
     * Validation result containing status and error message.
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String errorMessage;
        
        private ValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
        }
        
        public static ValidationResult success() {
            return new ValidationResult(true, null);
        }
        
        public static ValidationResult error(String message) {
            return new ValidationResult(false, message);
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
    }
    
    /**
     * Validates a license plate number.
     */
    public static ValidationResult validateLicensePlate(String plate) {
        if (plate == null || plate.trim().isEmpty()) {
            return ValidationResult.error("License plate is required");
        }
        
        String normalized = plate.trim().toUpperCase().replaceAll("\\s", "");
        
        if (normalized.length() < 2) {
            return ValidationResult.error("License plate is too short");
        }
        
        if (normalized.length() > 10) {
            return ValidationResult.error("License plate is too long");
        }
        
        if (!LICENSE_PLATE_PATTERN.matcher(normalized).matches()) {
            return ValidationResult.error("Invalid license plate format");
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Validates a credit/debit card number using Luhn algorithm.
     */
    public static ValidationResult validateCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.trim().isEmpty()) {
            return ValidationResult.error("Card number is required");
        }
        
        String digitsOnly = cardNumber.replaceAll("\\s", "");
        
        if (!CARD_NUMBER_PATTERN.matcher(digitsOnly).matches()) {
            return ValidationResult.error("Card number must be 13-16 digits");
        }
        
        if (!isValidLuhn(digitsOnly)) {
            return ValidationResult.error("Invalid card number");
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Luhn algorithm (mod 10) for card number validation.
     */
    private static boolean isValidLuhn(String number) {
        int sum = 0;
        boolean alternate = false;
        
        for (int i = number.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(number.charAt(i));
            
            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }
            
            sum += digit;
            alternate = !alternate;
        }
        
        return sum % 10 == 0;
    }
    
    /**
     * Validates CVV code.
     */
    public static ValidationResult validateCvv(String cvv) {
        if (cvv == null || cvv.trim().isEmpty()) {
            return ValidationResult.error("CVV is required");
        }
        
        if (!CVV_PATTERN.matcher(cvv).matches()) {
            return ValidationResult.error("CVV must be 3 or 4 digits");
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Validates card holder name.
     */
    public static ValidationResult validateCardHolderName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return ValidationResult.error("Card holder name is required");
        }
        
        String trimmed = name.trim();
        
        if (trimmed.length() < 2) {
            return ValidationResult.error("Name is too short");
        }
        
        if (!NAME_PATTERN.matcher(trimmed).matches()) {
            return ValidationResult.error("Name contains invalid characters");
        }
        
        if (!trimmed.contains(" ")) {
            return ValidationResult.error("Please enter full name");
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Validates expiry month (1-12).
     */
    public static ValidationResult validateExpiryMonth(int month) {
        if (month < 1 || month > 12) {
            return ValidationResult.error("Invalid expiry month");
        }
        return ValidationResult.success();
    }
    
    /**
     * Validates expiry year (current year or future).
     */
    public static ValidationResult validateExpiryYear(int year) {
        int currentYear = java.time.Year.now().getValue();
        
        if (year < currentYear) {
            return ValidationResult.error("Card has expired");
        }
        
        if (year > currentYear + 20) {
            return ValidationResult.error("Invalid expiry year");
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Validates that expiry date is not in the past.
     */
    public static ValidationResult validateExpiryDate(int month, int year) {
        java.time.YearMonth expiry = java.time.YearMonth.of(year, month);
        java.time.YearMonth now = java.time.YearMonth.now();
        
        if (expiry.isBefore(now)) {
            return ValidationResult.error("Card has expired");
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Validates a required text field is not empty.
     */
    public static ValidationResult validateRequired(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            return ValidationResult.error(fieldName + " is required");
        }
        return ValidationResult.success();
    }
    
    /**
     * Validates a numeric amount is positive.
     */
    public static ValidationResult validatePositiveAmount(double amount) {
        if (amount <= 0) {
            return ValidationResult.error("Amount must be greater than zero");
        }
        return ValidationResult.success();
    }
}
