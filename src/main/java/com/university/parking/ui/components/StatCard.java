package com.university.parking.ui.components;

import com.university.parking.ui.UIConstants;

import javax.swing.*;
import java.awt.*;

/**
 * Statistics card component for displaying metrics.
 */
public class StatCard extends RoundedPanel {
    
    private JLabel valueLabel;
    private JLabel titleLabel;
    private Color accentColor;
    
    public StatCard(String title, String value, Color accentColor) {
        super(UIConstants.RADIUS_LG, true);
        this.accentColor = accentColor;
        initComponents(title, value);
    }
    
    public StatCard(String title, String value) {
        this(title, value, UIConstants.PRIMARY);
    }
    
    private void initComponents(String title, String value) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(
            UIConstants.SPACING_LG, UIConstants.SPACING_LG,
            UIConstants.SPACING_LG, UIConstants.SPACING_LG
        ));
        
        // Left accent bar
        JPanel accentBar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(accentColor);
                g2.fillRoundRect(0, 0, 4, getHeight(), 2, 2);
                g2.dispose();
            }
        };
        accentBar.setOpaque(false);
        accentBar.setPreferredSize(new Dimension(4, 0));
        
        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, UIConstants.SPACING_MD, 0, 0));
        
        // Title
        titleLabel = new JLabel(title);
        titleLabel.setFont(UIConstants.SMALL);
        titleLabel.setForeground(UIConstants.TEXT_SECONDARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Value
        valueLabel = new JLabel(value);
        valueLabel.setFont(UIConstants.TITLE_LARGE);
        valueLabel.setForeground(UIConstants.TEXT_PRIMARY);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(UIConstants.SPACING_XS));
        contentPanel.add(valueLabel);
        
        add(accentBar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }
    
    public void setValue(String value) {
        valueLabel.setText(value);
    }
    
    public void setTitle(String title) {
        titleLabel.setText(title);
    }
    
    public void setAccentColor(Color color) {
        this.accentColor = color;
        repaint();
    }
    
    public String getValue() {
        return valueLabel.getText();
    }
}
