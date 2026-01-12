package com.university.parking.ui.components;

import com.university.parking.ui.UIConstants;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;

/**
 * Custom styled combo box with modern appearance.
 */
public class StyledComboBox<E> extends JComboBox<E> {
    
    public StyledComboBox() {
        super();
        initStyle();
    }
    
    public StyledComboBox(E[] items) {
        super(items);
        initStyle();
    }
    
    public StyledComboBox(ComboBoxModel<E> model) {
        super(model);
        initStyle();
    }
    
    private void initStyle() {
        setFont(UIConstants.BODY);
        setForeground(UIConstants.TEXT_PRIMARY);
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(getPreferredSize().width, UIConstants.INPUT_HEIGHT));
        
        setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(UIConstants.BORDER, UIConstants.RADIUS_SM, 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        
        setUI(new StyledComboBoxUI());
        
        setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setFont(UIConstants.BODY);
                setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
                
                if (isSelected) {
                    setBackground(UIConstants.PRIMARY_LIGHT);
                    setForeground(Color.WHITE);
                } else {
                    setBackground(Color.WHITE);
                    setForeground(UIConstants.TEXT_PRIMARY);
                }
                
                return this;
            }
        });
    }
    
    /**
     * Custom UI for combo box with styled arrow button.
     */
    private static class StyledComboBoxUI extends BasicComboBoxUI {
        @Override
        protected JButton createArrowButton() {
            JButton button = new JButton() {
                @Override
                public void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    g2.setColor(Color.WHITE);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    
                    // Draw arrow
                    int centerX = getWidth() / 2;
                    int centerY = getHeight() / 2;
                    int[] xPoints = {centerX - 5, centerX + 5, centerX};
                    int[] yPoints = {centerY - 2, centerY - 2, centerY + 4};
                    
                    g2.setColor(UIConstants.TEXT_SECONDARY);
                    g2.fillPolygon(xPoints, yPoints, 3);
                    
                    g2.dispose();
                }
            };
            button.setBorder(BorderFactory.createEmptyBorder());
            button.setFocusPainted(false);
            return button;
        }
    }
    
    /**
     * Custom rounded border.
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
