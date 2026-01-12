package com.university.parking.ui.components;

import com.university.parking.ui.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Custom styled button with modern appearance and hover effects.
 */
public class StyledButton extends JButton {
    
    public enum ButtonType {
        PRIMARY, SECONDARY, DANGER, SUCCESS
    }
    
    private ButtonType type;
    private Color bgColor;
    private Color hoverColor;
    private Color pressColor;
    private int radius = UIConstants.RADIUS_MD;
    
    public StyledButton(String text) {
        this(text, ButtonType.PRIMARY);
    }
    
    public StyledButton(String text, ButtonType type) {
        super(text);
        this.type = type;
        initStyle();
    }
    
    private void initStyle() {
        setFont(UIConstants.BODY_BOLD);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(getPreferredSize().width + 32, UIConstants.BUTTON_HEIGHT));
        
        switch (type) {
            case PRIMARY:
                bgColor = UIConstants.PRIMARY;
                hoverColor = UIConstants.PRIMARY_LIGHT;
                pressColor = UIConstants.PRIMARY_DARK;
                setForeground(Color.WHITE);
                break;
            case SECONDARY:
                bgColor = Color.WHITE;
                hoverColor = new Color(248, 249, 252);
                pressColor = new Color(233, 236, 239);
                setForeground(UIConstants.PRIMARY);
                break;
            case DANGER:
                bgColor = UIConstants.DANGER;
                hoverColor = new Color(200, 35, 51);
                pressColor = new Color(180, 25, 41);
                setForeground(Color.WHITE);
                break;
            case SUCCESS:
                bgColor = UIConstants.SUCCESS;
                hoverColor = new Color(33, 136, 56);
                pressColor = new Color(25, 105, 43);
                setForeground(Color.WHITE);
                break;
        }
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                repaint();
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        Color bg = bgColor;
        if (!isEnabled()) {
            bg = UIConstants.TEXT_MUTED;
        } else if (getModel().isPressed()) {
            bg = pressColor;
        } else if (getModel().isRollover()) {
            bg = hoverColor;
        }
        
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        
        // Border for SECONDARY type
        if (type == ButtonType.SECONDARY && isEnabled()) {
            g2.setColor(UIConstants.PRIMARY);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, radius, radius);
        }
        
        g2.dispose();
        super.paintComponent(g);
    }
    
    public void setButtonType(ButtonType type) {
        this.type = type;
        initStyle();
        repaint();
    }
}
