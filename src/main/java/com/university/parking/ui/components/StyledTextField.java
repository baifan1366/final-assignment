package com.university.parking.ui.components;

import com.university.parking.ui.UIConstants;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * Custom styled text field with placeholder support and focus effects.
 */
public class StyledTextField extends JTextField {
    
    private String placeholder;
    private boolean showingPlaceholder = false;
    private Border normalBorder;
    private Border focusBorder;
    
    public StyledTextField() {
        this("");
    }
    
    public StyledTextField(String placeholder) {
        this.placeholder = placeholder;
        initStyle();
    }
    
    public StyledTextField(int columns) {
        super(columns);
        this.placeholder = "";
        initStyle();
    }
    
    private void initStyle() {
        setFont(UIConstants.BODY);
        setForeground(UIConstants.TEXT_PRIMARY);
        setBackground(Color.WHITE);
        setCaretColor(UIConstants.PRIMARY);
        
        normalBorder = BorderFactory.createCompoundBorder(
            new RoundedBorder(UIConstants.BORDER, UIConstants.RADIUS_SM, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        );
        
        focusBorder = BorderFactory.createCompoundBorder(
            new RoundedBorder(UIConstants.PRIMARY_LIGHT, UIConstants.RADIUS_SM, 2),
            BorderFactory.createEmptyBorder(7, 11, 7, 11)
        );
        
        setBorder(normalBorder);
        setPreferredSize(new Dimension(getPreferredSize().width, UIConstants.INPUT_HEIGHT));
        
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                setBorder(focusBorder);
                if (showingPlaceholder) {
                    setText("");
                    setForeground(UIConstants.TEXT_PRIMARY);
                    showingPlaceholder = false;
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                setBorder(normalBorder);
                if (getText().isEmpty() && placeholder != null && !placeholder.isEmpty()) {
                    showPlaceholder();
                }
            }
        });
        
        if (placeholder != null && !placeholder.isEmpty()) {
            showPlaceholder();
        }
    }
    
    private void showPlaceholder() {
        super.setText(placeholder);
        setForeground(UIConstants.TEXT_MUTED);
        showingPlaceholder = true;
    }
    
    @Override
    public String getText() {
        return showingPlaceholder ? "" : super.getText();
    }
    
    @Override
    public void setText(String text) {
        if (text == null || text.isEmpty()) {
            if (placeholder != null && !placeholder.isEmpty() && !hasFocus()) {
                showPlaceholder();
            } else {
                super.setText("");
                showingPlaceholder = false;
            }
        } else {
            super.setText(text);
            setForeground(UIConstants.TEXT_PRIMARY);
            showingPlaceholder = false;
        }
    }
    
    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        if (getText().isEmpty() && !hasFocus()) {
            showPlaceholder();
        }
    }
    
    public String getPlaceholder() {
        return placeholder;
    }
    
    /**
     * Custom rounded border for text fields.
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
