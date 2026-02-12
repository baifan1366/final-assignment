package com.university.parking.ui;

import com.university.parking.ui.components.PasswordDialog;
import com.university.parking.ui.components.RoundedPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Main application frame for the University Parking Lot Management System.
 * Features modern sidebar navigation with card-based content area.
 */
public class MainFrame extends JFrame {
    
    private JPanel sidebarPanel;
    private JPanel contentPanel;
    private JPanel headerPanel;
    private CardLayout cardLayout;
    
    private JPanel entryExitPanel;
    private JPanel adminPanel;
    private JPanel reportPanel;
    private JPanel reservationPanel;
    
    private NavButton entryExitNav;
    private NavButton adminNav;
    private NavButton reportNav;
    private NavButton reservationNav;
    private NavButton activeNav;
    
    private JLabel timeLabel;
    private Timer clockTimer;
    
    public MainFrame() {
        initializeFrame();
        initializeComponents();
        layoutComponents();
        startClock();
    }
    
    private void initializeFrame() {
        setTitle("University Parking Lot Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setMinimumSize(new Dimension(1024, 700));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIConstants.BG_MAIN);
    }
    
    private void initializeComponents() {
        sidebarPanel = createSidebar();
        headerPanel = createHeader();
        
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(UIConstants.BG_MAIN);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(
            UIConstants.SPACING_LG, UIConstants.SPACING_LG,
            UIConstants.SPACING_LG, UIConstants.SPACING_LG
        ));
        
        entryExitPanel = createPlaceholderPanel("Entry/Exit Panel", "Manage vehicle entry and exit");
        adminPanel = createPlaceholderPanel("Admin Panel", "System administration");
        reportPanel = createPlaceholderPanel("Reports Panel", "View reports and statistics");
        reservationPanel = createPlaceholderPanel("Reservation Panel", "Manage parking reservations");
        
        contentPanel.add(entryExitPanel, "entry");
        contentPanel.add(adminPanel, "admin");
        contentPanel.add(reportPanel, "reports");
        contentPanel.add(reservationPanel, "reservations");
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(UIConstants.BG_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(UIConstants.SIDEBAR_WIDTH, 0));
        sidebar.setLayout(new BorderLayout());
        
        // Logo section
        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setBorder(BorderFactory.createEmptyBorder(
            UIConstants.SPACING_XL, UIConstants.SPACING_MD,
            UIConstants.SPACING_XL, UIConstants.SPACING_MD
        ));
        
        JLabel logoIcon = new JLabel("P");
        logoIcon.setFont(new Font(UIConstants.FONT_FAMILY, Font.BOLD, 36));
        logoIcon.setForeground(UIConstants.ACCENT);
        logoIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel titleLabel = new JLabel("Parking Lot");
        titleLabel.setFont(UIConstants.TITLE_MEDIUM);
        titleLabel.setForeground(UIConstants.TEXT_LIGHT);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Management System");
        subtitleLabel.setFont(UIConstants.SMALL);
        subtitleLabel.setForeground(UIConstants.TEXT_MUTED);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        logoPanel.add(logoIcon);
        logoPanel.add(Box.createVerticalStrut(UIConstants.SPACING_SM));
        logoPanel.add(titleLabel);
        logoPanel.add(subtitleLabel);
        
        // Navigation section
        JPanel navPanel = new JPanel();
        navPanel.setOpaque(false);
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBorder(BorderFactory.createEmptyBorder(
            UIConstants.SPACING_MD, UIConstants.SPACING_SM,
            UIConstants.SPACING_MD, UIConstants.SPACING_SM
        ));
        
        entryExitNav = new NavButton("Entry / Exit");
        adminNav = new NavButton("Administration");
        reportNav = new NavButton("Reports");
        reservationNav = new NavButton("Reservations");
        
        entryExitNav.addActionListener(e -> switchPanel("entry", entryExitNav));
        adminNav.addActionListener(e -> switchPanel("admin", adminNav));
        reportNav.addActionListener(e -> switchPanel("reports", reportNav));
        reservationNav.addActionListener(e -> switchPanel("reservations", reservationNav));
        
        navPanel.add(entryExitNav);
        navPanel.add(Box.createVerticalStrut(UIConstants.SPACING_XS));
        navPanel.add(adminNav);
        navPanel.add(Box.createVerticalStrut(UIConstants.SPACING_XS));
        navPanel.add(reportNav);
        navPanel.add(Box.createVerticalStrut(UIConstants.SPACING_XS));
        navPanel.add(reservationNav);
        
        setActiveNav(entryExitNav);
        
        // Footer section
        JPanel footerPanel = new JPanel();
        footerPanel.setOpaque(false);
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(
            UIConstants.SPACING_MD, UIConstants.SPACING_MD,
            UIConstants.SPACING_LG, UIConstants.SPACING_MD
        ));
        
        JLabel versionLabel = new JLabel("v1.0.0");
        versionLabel.setFont(UIConstants.SMALL);
        versionLabel.setForeground(UIConstants.TEXT_MUTED);
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        footerPanel.add(versionLabel);
        
        sidebar.add(logoPanel, BorderLayout.NORTH);
        sidebar.add(navPanel, BorderLayout.CENTER);
        sidebar.add(footerPanel, BorderLayout.SOUTH);
        
        return sidebar;
    }
    
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIConstants.BG_HEADER);
        header.setPreferredSize(new Dimension(0, UIConstants.HEADER_HEIGHT));
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.BORDER_LIGHT),
            BorderFactory.createEmptyBorder(0, UIConstants.SPACING_LG, 0, UIConstants.SPACING_LG)
        ));
        
        JLabel pageTitle = new JLabel("Entry / Exit");
        pageTitle.setFont(UIConstants.TITLE_MEDIUM);
        pageTitle.setForeground(UIConstants.TEXT_PRIMARY);
        pageTitle.setName("pageTitle");
        
        timeLabel = new JLabel();
        timeLabel.setFont(UIConstants.BODY);
        timeLabel.setForeground(UIConstants.TEXT_SECONDARY);
        updateTime();
        
        header.add(pageTitle, BorderLayout.WEST);
        header.add(timeLabel, BorderLayout.EAST);
        
        return header;
    }
    
    private JPanel createPlaceholderPanel(String title, String subtitle) {
        RoundedPanel panel = new RoundedPanel(UIConstants.RADIUS_LG, true);
        panel.setLayout(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        
        JPanel centerContent = new JPanel();
        centerContent.setOpaque(false);
        centerContent.setLayout(new BoxLayout(centerContent, BoxLayout.Y_AXIS));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIConstants.TITLE_LARGE);
        titleLabel.setForeground(UIConstants.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(UIConstants.BODY);
        subtitleLabel.setForeground(UIConstants.TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        centerContent.add(titleLabel);
        centerContent.add(Box.createVerticalStrut(UIConstants.SPACING_SM));
        centerContent.add(subtitleLabel);
        
        panel.add(centerContent);
        return panel;
    }

    private void layoutComponents() {
        JPanel mainWrapper = new JPanel(new BorderLayout());
        mainWrapper.setBackground(UIConstants.BG_MAIN);
        mainWrapper.add(headerPanel, BorderLayout.NORTH);
        mainWrapper.add(contentPanel, BorderLayout.CENTER);
        
        add(sidebarPanel, BorderLayout.WEST);
        add(mainWrapper, BorderLayout.CENTER);
    }
    
    private void switchPanel(String panelName, NavButton navButton) {
        // Check if admin authentication is required
        if ("admin".equals(panelName) || "reports".equals(panelName) || "reservations".equals(panelName)) {
            if (!authenticateAdmin()) {
                // Authentication failed - stay on current panel
                return;
            }
        }
        
        cardLayout.show(contentPanel, panelName);
        setActiveNav(navButton);
        
        String title = "Dashboard";
        switch (panelName) {
            case "entry":
                title = "Entry / Exit";
                break;
            case "admin":
                title = "Administration";
                break;
            case "reports":
                title = "Reports";
                break;
            case "reservations":
                title = "Reservations";
                break;
        }
        
        for (Component comp : headerPanel.getComponents()) {
            if (comp instanceof JLabel && "pageTitle".equals(comp.getName())) {
                ((JLabel) comp).setText(title);
                break;
            }
        }
    }
    
    /**
     * Shows password dialog for admin authentication.
     * 
     * @return true if authentication successful, false otherwise
     */
    private boolean authenticateAdmin() {
        PasswordDialog dialog = new PasswordDialog(this);
        return dialog.showDialog();
    }
    
    private void setActiveNav(NavButton navButton) {
        if (activeNav != null) {
            activeNav.setActive(false);
        }
        activeNav = navButton;
        activeNav.setActive(true);
    }
    
    private void startClock() {
        clockTimer = new Timer(1000, e -> updateTime());
        clockTimer.start();
    }
    
    private void updateTime() {
        LocalDateTime now = LocalDateTime.now();
        String timeStr = now.format(DateTimeFormatter.ofPattern("EEEE, MMM d, yyyy  HH:mm:ss"));
        timeLabel.setText(timeStr);
    }
    
    public void setEntryExitPanel(JPanel panel) {
        contentPanel.remove(entryExitPanel);
        this.entryExitPanel = panel;
        contentPanel.add(panel, "entry");
        cardLayout.show(contentPanel, "entry");
    }
    
    public void setAdminPanel(JPanel panel) {
        contentPanel.remove(adminPanel);
        this.adminPanel = panel;
        contentPanel.add(panel, "admin");
    }
    
    public void setReportPanel(JPanel panel) {
        contentPanel.remove(reportPanel);
        this.reportPanel = panel;
        contentPanel.add(panel, "reports");
    }
    
    public void setReservationPanel(JPanel panel) {
        contentPanel.remove(reservationPanel);
        this.reservationPanel = panel;
        contentPanel.add(panel, "reservations");
    }
    
    public JPanel getEntryExitPanel() {
        return entryExitPanel;
    }
    
    public JPanel getAdminPanel() {
        return adminPanel;
    }
    
    public JPanel getReportPanel() {
        return reportPanel;
    }
    
    public JPanel getReservationPanel() {
        return reservationPanel;
    }
    
    public JTabbedPane getTabbedPane() {
        return null;
    }
    
    @Override
    public void dispose() {
        if (clockTimer != null) {
            clockTimer.stop();
        }
        super.dispose();
    }
    
    /**
     * Custom navigation button for sidebar.
     */
    private static class NavButton extends JButton {
        private boolean active = false;
        
        public NavButton(String text) {
            super(text);
            initStyle();
        }
        
        private void initStyle() {
            setFont(UIConstants.MENU);
            setForeground(UIConstants.TEXT_LIGHT);
            setBackground(UIConstants.BG_SIDEBAR);
            setBorderPainted(false);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setHorizontalAlignment(LEFT);
            setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
            setPreferredSize(new Dimension(UIConstants.SIDEBAR_WIDTH - 16, 44));
            setMaximumSize(new Dimension(UIConstants.SIDEBAR_WIDTH - 16, 44));
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!active) repaint();
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    if (!active) repaint();
                }
            });
        }
        
        public void setActive(boolean active) {
            this.active = active;
            repaint();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (active) {
                g2.setColor(UIConstants.PRIMARY_LIGHT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            } else if (getModel().isRollover()) {
                g2.setColor(new Color(255, 255, 255, 20));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            }
            
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
