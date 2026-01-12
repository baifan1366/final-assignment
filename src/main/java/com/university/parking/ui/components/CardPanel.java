package com.university.parking.ui.components;

import com.university.parking.ui.UIConstants;

import javax.swing.*;
import java.awt.*;

/**
 * Card panel with title, rounded corners, and shadow.
 */
public class CardPanel extends RoundedPanel {
    
    private String title;
    private JPanel contentPanel;
    private JLabel titleLabel;
    
    public CardPanel() {
        this(null);
    }
    
    public CardPanel(String title) {
        super(UIConstants.RADIUS_LG, true);
        this.title = title;
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(
            UIConstants.SPACING_LG, UIConstants.SPACING_LG,
            UIConstants.SPACING_LG, UIConstants.SPACING_LG
        ));
        
        if (title != null && !title.isEmpty()) {
            titleLabel = new JLabel(title);
            titleLabel.setFont(UIConstants.TITLE_SMALL);
            titleLabel.setForeground(UIConstants.TEXT_PRIMARY);
            titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, UIConstants.SPACING_MD, 0));
            add(titleLabel, BorderLayout.NORTH);
        }
        
        contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BorderLayout());
        add(contentPanel, BorderLayout.CENTER);
    }
    
    public JPanel getContentPanel() {
        return contentPanel;
    }
    
    public void setTitle(String title) {
        this.title = title;
        if (titleLabel != null) {
            titleLabel.setText(title);
        }
    }
    
    public void setContentLayout(LayoutManager layout) {
        contentPanel.setLayout(layout);
    }
}
