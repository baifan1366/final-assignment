package com.university.parking.ui.components;

import com.university.parking.ui.UIConstants;

import javax.swing.*;
import java.awt.*;

/**
 * Status label with colored background pill style.
 */
public class StatusLabel extends JLabel {
    
    private Color bgColor;
    
    public StatusLabel(String text, Color bgColor) {
        super(text);
        this.bgColor = bgColor;
        initStyle();
    }
    
    private void initStyle() {
        setOpaque(false);
        setFont(UIConstants.SMALL);
        setForeground(Color.WHITE);
        setHorizontalAlignment(CENTER);
        setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(bgColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
        g2.dispose();
        super.paintComponent(g);
    }
    
    public void setStatusColor(Color color) {
        this.bgColor = color;
        repaint();
    }
    
    // Factory methods for common status types
    
    public static StatusLabel success(String text) {
        return new StatusLabel(text, UIConstants.SUCCESS);
    }
    
    public static StatusLabel warning(String text) {
        return new StatusLabel(text, UIConstants.WARNING);
    }
    
    public static StatusLabel danger(String text) {
        return new StatusLabel(text, UIConstants.DANGER);
    }
    
    public static StatusLabel info(String text) {
        return new StatusLabel(text, UIConstants.INFO);
    }
    
    public static StatusLabel primary(String text) {
        return new StatusLabel(text, UIConstants.PRIMARY);
    }
}
