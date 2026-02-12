package com.university.parking.ui.components;

import com.university.parking.ui.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Custom password dialog for admin authentication.
 * Styled to match the application's design system.
 */
public class PasswordDialog extends JDialog {
    
    private JPasswordField passwordField;
    private JLabel messageLabel;
    private JLabel attemptsLabel;
    private boolean authenticated = false;
    private AuthManager authManager;
    
    public PasswordDialog(Frame parent) {
        super(parent, "Admin Authentication", true);
        this.authManager = AuthManager.getInstance();
        initComponents();
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setSize(500, 400);
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);
        
        // Main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(
            UIConstants.SPACING_XL, UIConstants.SPACING_XL,
            UIConstants.SPACING_LG, UIConstants.SPACING_XL
        ));
        
        // Icon
        JLabel iconLabel = new JLabel("🔒");
        iconLabel.setFont(new Font(UIConstants.FONT_FAMILY, Font.PLAIN, 48));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Title
        JLabel titleLabel = new JLabel("Admin Access Required");
        titleLabel.setFont(UIConstants.TITLE_MEDIUM);
        titleLabel.setForeground(UIConstants.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Message
        messageLabel = new JLabel("Please enter the admin password");
        messageLabel.setFont(UIConstants.BODY);
        messageLabel.setForeground(UIConstants.TEXT_SECONDARY);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Attempts label
        attemptsLabel = new JLabel("");
        attemptsLabel.setFont(UIConstants.SMALL);
        attemptsLabel.setForeground(UIConstants.TEXT_MUTED);
        attemptsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        updateAttemptsLabel();
        
        // Password field
        passwordField = new JPasswordField(20);
        passwordField.setFont(UIConstants.BODY);
        passwordField.setPreferredSize(new Dimension(350, UIConstants.INPUT_HEIGHT));
        passwordField.setMaximumSize(new Dimension(350, UIConstants.INPUT_HEIGHT));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.BORDER, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        // Enter key listener
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleLogin();
                }
            }
        });
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, UIConstants.SPACING_MD, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        StyledButton loginButton = new StyledButton("Login", StyledButton.ButtonType.PRIMARY);
        loginButton.setPreferredSize(new Dimension(140, UIConstants.BUTTON_HEIGHT));
        loginButton.addActionListener(e -> handleLogin());
        
        StyledButton cancelButton = new StyledButton("Cancel", StyledButton.ButtonType.SECONDARY);
        cancelButton.setPreferredSize(new Dimension(140, UIConstants.BUTTON_HEIGHT));
        cancelButton.addActionListener(e -> {
            authenticated = false;
            dispose();
        });
        
        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);
        
        // Add components with spacing
        contentPanel.add(iconLabel);
        contentPanel.add(Box.createVerticalStrut(UIConstants.SPACING_MD));
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(UIConstants.SPACING_SM));
        contentPanel.add(messageLabel);
        contentPanel.add(Box.createVerticalStrut(UIConstants.SPACING_XS));
        contentPanel.add(attemptsLabel);
        contentPanel.add(Box.createVerticalStrut(UIConstants.SPACING_XL));
        contentPanel.add(passwordField);
        contentPanel.add(Box.createVerticalStrut(UIConstants.SPACING_XL));
        contentPanel.add(buttonPanel);
        contentPanel.add(Box.createVerticalStrut(UIConstants.SPACING_MD));
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Focus password field
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent e) {
                passwordField.requestFocusInWindow();
            }
        });
    }
    
    private void handleLogin() {
        // Check if locked out
        if (authManager.isLockedOut()) {
            showError("Access locked. Please try again tomorrow.");
            return;
        }
        
        String password = new String(passwordField.getPassword());
        
        if (password.isEmpty()) {
            showError("Please enter a password");
            passwordField.requestFocusInWindow();
            return;
        }
        
        if (authManager.authenticate(password)) {
            authenticated = true;
            dispose();
        } else {
            int remaining = authManager.getRemainingAttempts();
            
            if (remaining == 0) {
                showError("Access locked. Please try again tomorrow.");
                passwordField.setEnabled(false);
            } else {
                showError("Incorrect password. " + remaining + " attempt(s) remaining.");
                passwordField.setText("");
                passwordField.requestFocusInWindow();
                updateAttemptsLabel();
            }
        }
    }
    
    private void showError(String message) {
        messageLabel.setText(message);
        messageLabel.setForeground(UIConstants.DANGER);
    }
    
    private void updateAttemptsLabel() {
        int remaining = authManager.getRemainingAttempts();
        if (remaining > 0) {
            attemptsLabel.setText("Attempts remaining: " + remaining);
            attemptsLabel.setForeground(UIConstants.TEXT_MUTED);
        } else {
            attemptsLabel.setText("No attempts remaining");
            attemptsLabel.setForeground(UIConstants.DANGER);
        }
    }
    
    /**
     * Shows the dialog and returns whether authentication was successful.
     */
    public boolean showDialog() {
        // Check if already locked out
        if (authManager.isLockedOut()) {
            JOptionPane.showMessageDialog(
                getParent(),
                "Access is locked due to too many failed attempts.\nPlease try again tomorrow.",
                "Access Locked",
                JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
        
        setVisible(true);
        return authenticated;
    }
}
