package com.university.parking.ui.components;

import com.university.parking.ui.UIConstants;

import javax.swing.*;
import java.awt.*;

/**
 * Panel with rounded corners and optional shadow effect.
 */
public class RoundedPanel extends JPanel {
    
    private int radius;
    private boolean hasShadow;
    private Color shadowColor = new Color(0, 0, 0, 20);
    
    public RoundedPanel() {
        this(UIConstants.RADIUS_LG, true);
    }
    
    public RoundedPanel(int radius) {
        this(radius, false);
    }
    
    public RoundedPanel(int radius, boolean hasShadow) {
        this.radius = radius;
        this.hasShadow = hasShadow;
        setOpaque(false);
        setBackground(Color.WHITE);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int offset = hasShadow ? 3 : 0;
        
        // Draw shadow
        if (hasShadow) {
            g2.setColor(shadowColor);
            g2.fillRoundRect(offset, offset, getWidth() - offset, getHeight() - offset, radius, radius);
        }
        
        // Draw background
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth() - offset, getHeight() - offset, radius, radius);
        
        g2.dispose();
        super.paintComponent(g);
    }
    
    public void setRadius(int radius) {
        this.radius = radius;
        repaint();
    }
    
    public void setHasShadow(boolean hasShadow) {
        this.hasShadow = hasShadow;
        repaint();
    }
}
