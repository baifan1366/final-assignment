package com.university.parking.ui.components;

import com.university.parking.ui.UIConstants;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.time.YearMonth;
import java.util.Calendar;

/**
 * Dialog for collecting and validating credit/debit card payment information.
 */
public class CardPaymentDialog extends JDialog {
    
    private JTextField cardNumberField;
    private JTextField cardHolderField;
    private JComboBox<String> expiryMonthCombo;
    private JComboBox<String> expiryYearCombo;
    private JPasswordField cvvField;
    
    private JLabel cardNumberError;
    private JLabel cardHolderError;
    private JLabel expiryError;
    private JLabel cvvError;
    
    private boolean confirmed = false;
    private double amount;
    
    public CardPaymentDialog(Frame parent, double amount) {
        super(parent, "Card Payment", true);
        this.amount = amount;
        initializeDialog();
        initializeComponents();
        layoutComponents();
        pack();
        setLocationRelativeTo(parent);
    }
    
    private void initializeDialog() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
    }
    
    private void initializeComponents() {
        // Card number field with formatting (16 digits, spaces every 4)
        cardNumberField = new JTextField(19);
        cardNumberField.setFont(UIConstants.BODY);
        setupCardNumberField();
        
        // Card holder name
        cardHolderField = new JTextField(20);
        cardHolderField.setFont(UIConstants.BODY);
        setupCardHolderField();
        
        // Expiry month combo
        String[] months = {"01", "02", "03", "04", "05", "06", 
                          "07", "08", "09", "10", "11", "12"};
        expiryMonthCombo = new JComboBox<>(months);
        expiryMonthCombo.setFont(UIConstants.BODY);
        
        // Expiry year combo (current year + 10 years)
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        String[] years = new String[11];
        for (int i = 0; i < 11; i++) {
            years[i] = String.valueOf(currentYear + i);
        }
        expiryYearCombo = new JComboBox<>(years);
        expiryYearCombo.setFont(UIConstants.BODY);

        // CVV field (3-4 digits)
        cvvField = new JPasswordField(4);
        cvvField.setFont(UIConstants.BODY);
        setupCvvField();
        
        // Error labels
        cardNumberError = createErrorLabel();
        cardHolderError = createErrorLabel();
        expiryError = createErrorLabel();
        cvvError = createErrorLabel();
    }
    
    private void setupCardNumberField() {
        ((AbstractDocument) cardNumberField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) 
                    throws BadLocationException {
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                String newText = currentText.substring(0, offset) + text + currentText.substring(offset + length);
                
                // Remove spaces for validation
                String digitsOnly = newText.replaceAll("\\s", "");
                
                // Only allow digits and max 16 digits
                if (digitsOnly.matches("\\d*") && digitsOnly.length() <= 16) {
                    // Format with spaces every 4 digits
                    String formatted = formatCardNumber(digitsOnly);
                    fb.getDocument().remove(0, fb.getDocument().getLength());
                    super.insertString(fb, 0, formatted, attrs);
                }
            }
            
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) 
                    throws BadLocationException {
                replace(fb, offset, 0, string, attr);
            }
        });
        
        cardNumberField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                validateCardNumber();
            }
        });
    }
    
    private String formatCardNumber(String digits) {
        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < digits.length(); i++) {
            if (i > 0 && i % 4 == 0) {
                formatted.append(" ");
            }
            formatted.append(digits.charAt(i));
        }
        return formatted.toString();
    }
    
    private void setupCardHolderField() {
        ((AbstractDocument) cardHolderField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) 
                    throws BadLocationException {
                String newText = text.toUpperCase();
                // Only allow letters, spaces, hyphens, and apostrophes
                if (newText.matches("[A-Z\\s\\-']*") && 
                    (fb.getDocument().getLength() - length + newText.length()) <= 50) {
                    super.replace(fb, offset, length, newText, attrs);
                }
            }
        });
        
        cardHolderField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                validateCardHolder();
            }
        });
    }
    
    private void setupCvvField() {
        ((AbstractDocument) cvvField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) 
                    throws BadLocationException {
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                String newText = currentText.substring(0, offset) + text + currentText.substring(offset + length);
                
                // Only allow 3-4 digits
                if (newText.matches("\\d*") && newText.length() <= 4) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
        
        cvvField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                validateCvv();
            }
        });
    }
    
    private JLabel createErrorLabel() {
        JLabel label = new JLabel(" ");
        label.setFont(UIConstants.SMALL);
        label.setForeground(UIConstants.DANGER);
        return label;
    }

    private void layoutComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, UIConstants.SPACING_MD));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(
            UIConstants.SPACING_LG, UIConstants.SPACING_LG,
            UIConstants.SPACING_LG, UIConstants.SPACING_LG
        ));
        mainPanel.setBackground(Color.WHITE);
        
        // Header with amount
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Enter Card Details");
        titleLabel.setFont(UIConstants.TITLE_MEDIUM);
        titleLabel.setForeground(UIConstants.TEXT_PRIMARY);
        
        JLabel amountLabel = new JLabel(String.format("Amount: RM %.2f", amount));
        amountLabel.setFont(UIConstants.TITLE_SMALL);
        amountLabel.setForeground(UIConstants.PRIMARY);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(amountLabel, BorderLayout.EAST);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(UIConstants.SPACING_XS, 0, UIConstants.SPACING_XS, UIConstants.SPACING_MD);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Card number
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        formPanel.add(createFieldLabel("Card Number *"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 1;
        formPanel.add(cardNumberField, gbc);
        
        gbc.gridy = 1;
        formPanel.add(cardNumberError, gbc);
        
        // Card holder
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        formPanel.add(createFieldLabel("Card Holder Name *"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 1;
        formPanel.add(cardHolderField, gbc);
        
        gbc.gridy = 3;
        formPanel.add(cardHolderError, gbc);
        
        // Expiry date
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        formPanel.add(createFieldLabel("Expiry Date *"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 1;
        JPanel expiryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, UIConstants.SPACING_SM, 0));
        expiryPanel.setOpaque(false);
        expiryPanel.add(expiryMonthCombo);
        expiryPanel.add(new JLabel("/"));
        expiryPanel.add(expiryYearCombo);
        formPanel.add(expiryPanel, gbc);
        
        gbc.gridy = 5;
        formPanel.add(expiryError, gbc);
        
        // CVV
        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0;
        formPanel.add(createFieldLabel("CVV *"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 1;
        JPanel cvvPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        cvvPanel.setOpaque(false);
        cvvField.setPreferredSize(new Dimension(60, UIConstants.INPUT_HEIGHT));
        cvvPanel.add(cvvField);
        cvvPanel.add(Box.createHorizontalStrut(UIConstants.SPACING_SM));
        JLabel cvvHint = new JLabel("(3 or 4 digits on back of card)");
        cvvHint.setFont(UIConstants.SMALL);
        cvvHint.setForeground(UIConstants.TEXT_MUTED);
        cvvPanel.add(cvvHint);
        formPanel.add(cvvPanel, gbc);
        
        gbc.gridy = 7;
        formPanel.add(cvvError, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIConstants.SPACING_SM, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(UIConstants.SPACING_MD, 0, 0, 0));
        
        StyledButton cancelButton = new StyledButton("Cancel", StyledButton.ButtonType.SECONDARY);
        cancelButton.addActionListener(e -> {
            confirmed = false;
            dispose();
        });
        
        StyledButton payButton = new StyledButton("Pay Now", StyledButton.ButtonType.PRIMARY);
        payButton.addActionListener(e -> handlePayment());
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(payButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIConstants.BODY_BOLD);
        label.setForeground(UIConstants.TEXT_PRIMARY);
        return label;
    }

    private void handlePayment() {
        // Validate all fields
        boolean valid = true;
        
        if (!validateCardNumber()) valid = false;
        if (!validateCardHolder()) valid = false;
        if (!validateExpiry()) valid = false;
        if (!validateCvv()) valid = false;
        
        if (valid) {
            confirmed = true;
            dispose();
        } else {
            // Show summary error dialog
            JOptionPane.showMessageDialog(
                this,
                "Please correct the errors in the form before proceeding.",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    private boolean validateCardNumber() {
        String cardNumber = cardNumberField.getText().replaceAll("\\s", "");
        
        if (cardNumber.isEmpty()) {
            cardNumberError.setText("Card number is required");
            return false;
        }
        
        if (cardNumber.length() < 13 || cardNumber.length() > 16) {
            cardNumberError.setText("Card number must be 13-16 digits");
            return false;
        }
        
        // Luhn algorithm validation
        if (!isValidLuhn(cardNumber)) {
            cardNumberError.setText("Invalid card number");
            return false;
        }
        
        cardNumberError.setText(" ");
        return true;
    }
    
    /**
     * Validates card number using Luhn algorithm (mod 10 check).
     */
    private boolean isValidLuhn(String cardNumber) {
        int sum = 0;
        boolean alternate = false;
        
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(cardNumber.charAt(i));
            
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
    
    private boolean validateCardHolder() {
        String name = cardHolderField.getText().trim();
        
        if (name.isEmpty()) {
            cardHolderError.setText("Card holder name is required");
            return false;
        }
        
        if (name.length() < 2) {
            cardHolderError.setText("Name is too short");
            return false;
        }
        
        if (!name.contains(" ")) {
            cardHolderError.setText("Please enter full name (first and last)");
            return false;
        }
        
        cardHolderError.setText(" ");
        return true;
    }
    
    private boolean validateExpiry() {
        int month = Integer.parseInt((String) expiryMonthCombo.getSelectedItem());
        int year = Integer.parseInt((String) expiryYearCombo.getSelectedItem());
        
        YearMonth expiry = YearMonth.of(year, month);
        YearMonth now = YearMonth.now();
        
        if (expiry.isBefore(now)) {
            expiryError.setText("Card has expired");
            return false;
        }
        
        expiryError.setText(" ");
        return true;
    }
    
    private boolean validateCvv() {
        String cvv = new String(cvvField.getPassword());
        
        if (cvv.isEmpty()) {
            cvvError.setText("CVV is required");
            return false;
        }
        
        if (cvv.length() < 3) {
            cvvError.setText("CVV must be 3 or 4 digits");
            return false;
        }
        
        cvvError.setText(" ");
        return true;
    }
    
    /**
     * Shows the dialog and returns true if payment was confirmed.
     */
    public boolean showDialog() {
        setVisible(true);
        return confirmed;
    }
    
    /**
     * Returns the masked card number (last 4 digits visible).
     */
    public String getMaskedCardNumber() {
        String cardNumber = cardNumberField.getText().replaceAll("\\s", "");
        if (cardNumber.length() >= 4) {
            return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
        }
        return "****";
    }
    
    /**
     * Returns the card holder name.
     */
    public String getCardHolderName() {
        return cardHolderField.getText().trim();
    }
}
