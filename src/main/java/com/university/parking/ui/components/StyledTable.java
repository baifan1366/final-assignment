package com.university.parking.ui.components;

import com.university.parking.ui.UIConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import java.awt.*;

/**
 * Custom styled table with modern appearance.
 */
public class StyledTable extends JTable {
    
    public StyledTable() {
        super();
        initStyle();
    }
    
    public StyledTable(TableModel model) {
        super(model);
        initStyle();
    }
    
    private void initStyle() {
        setFont(UIConstants.BODY);
        setRowHeight(UIConstants.TABLE_ROW_HEIGHT);
        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));
        setSelectionBackground(UIConstants.TABLE_SELECTED);
        setSelectionForeground(UIConstants.TABLE_SELECTED_TEXT);
        setBackground(Color.WHITE);
        setFillsViewportHeight(true);
        
        // Header style
        JTableHeader header = getTableHeader();
        header.setFont(UIConstants.BODY_BOLD);
        header.setBackground(UIConstants.TABLE_HEADER);
        header.setForeground(UIConstants.TEXT_PRIMARY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.BORDER));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 48));
        header.setReorderingAllowed(false);
        
        // Custom header renderer
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setFont(UIConstants.BODY_BOLD);
                setBackground(UIConstants.TABLE_HEADER);
                setForeground(UIConstants.TEXT_PRIMARY);
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.BORDER),
                    BorderFactory.createEmptyBorder(0, 12, 0, 12)
                ));
                setHorizontalAlignment(LEFT);
                return this;
            }
        });
        
        // Zebra striping cell renderer
        setDefaultRenderer(Object.class, new StyledCellRenderer());
    }
    
    /**
     * Custom cell renderer with zebra striping and enhanced selection effect.
     */
    private static class StyledCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            setFont(UIConstants.BODY);
            
            if (isSelected) {
                // Enhanced selection style with left border accent
                setBackground(UIConstants.TABLE_SELECTED);
                setForeground(UIConstants.TABLE_SELECTED_TEXT);
                
                // Add left border accent for first column
                if (column == 0) {
                    setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 3, 0, 0, UIConstants.PRIMARY),
                        BorderFactory.createEmptyBorder(0, 9, 0, 12)
                    ));
                } else {
                    setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                }
            } else {
                // Zebra striping for non-selected rows
                setBackground(row % 2 == 0 ? Color.WHITE : UIConstants.TABLE_ROW_ALT);
                setForeground(UIConstants.TEXT_PRIMARY);
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
            }
            
            return this;
        }
    }
    
    /**
     * Creates a styled scroll pane for this table.
     */
    public JScrollPane createScrollPane() {
        JScrollPane scrollPane = new JScrollPane(this);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_LIGHT));
        scrollPane.getViewport().setBackground(Color.WHITE);
        return scrollPane;
    }
}
