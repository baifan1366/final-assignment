package com.university.parking.ui.components;

import com.university.parking.ui.UIConstants;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.function.Function;

/**
 * Text field with built-in validation support and error display.
 */
public class ValidatedTextField extends JPanel {
    
    private StyledTextField textField;
    private JLabel errorLabel;
    private Function<String, InputValidator.ValidationResult> validator;
    private boolean showErrorOnFocusLost = true;
    
    public ValidatedTextField(String placeholder) {
        this(placeholder, null);
    }
    
    public ValidatedTextField(String placeholder, Function<String, InputValidator.ValidationResult> validator) {
        this.validator = validator;
        initComponents(placeholder);
        layoutComponents();
    }
    
    private void initComponents(String placeholder) {
        textField = new StyledTextField(placeholder);
        
        errorLabel = new JLabel(" ");
        errorLabel.setFont(UIConstants.SMALL);
        errorLabel.setForeground(UIConstants.DANGER);
        
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (showErrorOnFocusLost && validator != null) {
                    validateInput();
                }
            }
        });
    }
    
    private void layoutComponents() {
        setLayout(new BorderLayout(0, 2));
        setOpaque(false);
        add(textField, BorderLayout.CENTER);
        add(errorLabel, BorderLayout.SOUTH);
    }
    
    /**
     * Sets the validator function for this field.
     */
    public void setValidator(Function<String, InputValidator.ValidationResult> validator) {
        this.validator = validator;
    }
    
    /**
     * Validates the current input and shows error if invalid.
     * @return true if valid, false otherwise
     */
    public boolean validateInput() {
        if (validator == null) {
            clearError();
            return true;
        }
        
        InputValidator.ValidationResult result = validator.apply(getText());
        
        if (result.isValid()) {
            clearError();
            return true;
        } else {
            showError(result.getErrorMessage());
            return false;
        }
    }
    
    /**
     * Shows an error message below the field.
     */
    public void showError(String message) {
        errorLabel.setText(message);
        textField.setBorder(createErrorBorder());
    }
    
    /**
     * Clears any error message.
     */
    public void clearError() {
        errorLabel.setText(" ");
        textField.setBorder(createNormalBorder());
    }
    
    private Border createErrorBorder() {
        return BorderFactory.createCompoundBorder(
            new RoundedBorder(UIConstants.DANGER, UIConstants.RADIUS_SM, 2),
            BorderFactory.createEmptyBorder(7, 11, 7, 11)
        );
    }
    
    private Border createNormalBorder() {
        return BorderFactory.createCompoundBorder(
            new RoundedBorder(UIConstants.BORDER, UIConstants.RADIUS_SM, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        );
    }
    
    // Delegate methods to text field
    
    public String getText() {
        return textField.getText();
    }
    
    public void setText(String text) {
        textField.setText(text);
        clearError();
    }
    
    public void setPlaceholder(String placeholder) {
        textField.setPlaceholder(placeholder);
    }
    
    public StyledTextField getTextField() {
        return textField;
    }
    
    @Override
    public void requestFocus() {
        textField.requestFocus();
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        textField.setEnabled(enabled);
    }
    
    public void setShowErrorOnFocusLost(boolean show) {
        this.showErrorOnFocusLost = show;
    }
    
    /**
     * Custom rounded border for validation states.
     */
    private static class RoundedBorder implements Border {
        private Color color;
        private int radius;
        private int thickness;
        
        public RoundedBorder(Color color, int radius, int thickness) {
            this.color = color;
            this.radius = radius;
            this.thickness = thickness;
        }
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(thickness, thickness, thickness, thickness);
        }
        
        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }
}
