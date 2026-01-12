# Seminar Management System - UI 组件实现指南

## UIConstants.java 完整定义
```java
public class UIConstants {
    // ===== 颜色定义 =====
    // 主色调
    public static final Color PRIMARY = new Color(25, 55, 109);
    public static final Color PRIMARY_LIGHT = new Color(87, 108, 188);
    public static final Color PRIMARY_DARK = new Color(15, 35, 75);
    public static final Color ACCENT = new Color(10, 194, 239);
    
    // 背景色
    public static final Color BG_MAIN = new Color(248, 249, 252);
    public static final Color BG_CARD = Color.WHITE;
    public static final Color BG_SIDEBAR = new Color(25, 55, 109);
    public static final Color BG_HEADER = Color.WHITE;
    
    // 文字色
    public static final Color TEXT_PRIMARY = new Color(33, 37, 41);
    public static final Color TEXT_SECONDARY = new Color(108, 117, 125);
    public static final Color TEXT_LIGHT = Color.WHITE;
    public static final Color TEXT_MUTED = new Color(173, 181, 189);
    
    // 状态色
    public static final Color SUCCESS = new Color(40, 167, 69);
    public static final Color WARNING = new Color(255, 193, 7);
    public static final Color DANGER = new Color(220, 53, 69);
    public static final Color INFO = new Color(23, 162, 184);
    
    // 边框色
    public static final Color BORDER = new Color(222, 226, 230);
    public static final Color BORDER_LIGHT = new Color(233, 236, 239);
    
    // 表格色
    public static final Color TABLE_HEADER = new Color(241, 243, 245);
    public static final Color TABLE_ROW_ALT = new Color(248, 249, 250);
    public static final Color TABLE_HOVER = new Color(233, 236, 239);
    
    // ===== 字体定义 =====
    public static final String FONT_FAMILY = "Segoe UI";
    public static final Font TITLE_LARGE = new Font(FONT_FAMILY, Font.BOLD, 24);
    public static final Font TITLE_MEDIUM = new Font(FONT_FAMILY, Font.BOLD, 18);
    public static final Font TITLE_SMALL = new Font(FONT_FAMILY, Font.BOLD, 14);
    public static final Font BODY = new Font(FONT_FAMILY, Font.PLAIN, 13);
    public static final Font BODY_BOLD = new Font(FONT_FAMILY, Font.BOLD, 13);
    public static final Font SMALL = new Font(FONT_FAMILY, Font.PLAIN, 11);
    public static final Font MENU = new Font(FONT_FAMILY, Font.PLAIN, 14);
    
    // ===== 尺寸定义 =====
    public static final int SIDEBAR_WIDTH = 220;
    public static final int HEADER_HEIGHT = 60;
    public static final int STATUSBAR_HEIGHT = 30;
    public static final int BUTTON_HEIGHT = 38;
    public static final int INPUT_HEIGHT = 38;
    public static final int TABLE_ROW_HEIGHT = 44;
    
    // ===== 间距定义 =====
    public static final int SPACING_XS = 4;
    public static final int SPACING_SM = 8;
    public static final int SPACING_MD = 16;
    public static final int SPACING_LG = 24;
    public static final int SPACING_XL = 32;
    
    // ===== 圆角定义 =====
    public static final int RADIUS_SM = 4;
    public static final int RADIUS_MD = 8;
    public static final int RADIUS_LG = 12;
}
```

## 自定义按钮组件

### StyledButton.java
```java
public class StyledButton extends JButton {
    public enum ButtonType { PRIMARY, SECONDARY, DANGER, SUCCESS }
    
    private ButtonType type;
    private Color bgColor, hoverColor, pressColor;
    private int radius = UIConstants.RADIUS_MD;
    
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
            public void mouseEntered(MouseEvent e) { repaint(); }
            @Override
            public void mouseExited(MouseEvent e) { repaint(); }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        Color bg = bgColor;
        if (getModel().isPressed()) bg = pressColor;
        else if (getModel().isRollover()) bg = hoverColor;
        
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        
        // 边框 (仅 SECONDARY)
        if (type == ButtonType.SECONDARY) {
            g2.setColor(UIConstants.PRIMARY);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, radius, radius);
        }
        
        g2.dispose();
        super.paintComponent(g);
    }
}
```

## 自定义输入框

### StyledTextField.java
```java
public class StyledTextField extends JTextField {
    private String placeholder;
    private boolean showingPlaceholder = true;
    
    public StyledTextField(String placeholder) {
        this.placeholder = placeholder;
        initStyle();
    }
    
    private void initStyle() {
        setFont(UIConstants.BODY);
        setForeground(UIConstants.TEXT_PRIMARY);
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(UIConstants.BORDER, UIConstants.RADIUS_SM),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        setPreferredSize(new Dimension(getPreferredSize().width, UIConstants.INPUT_HEIGHT));
        
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (showingPlaceholder) {
                    setText("");
                    setForeground(UIConstants.TEXT_PRIMARY);
                    showingPlaceholder = false;
                }
                setBorder(BorderFactory.createCompoundBorder(
                    new RoundedBorder(UIConstants.PRIMARY_LIGHT, UIConstants.RADIUS_SM),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (getText().isEmpty()) {
                    setText(placeholder);
                    setForeground(UIConstants.TEXT_MUTED);
                    showingPlaceholder = true;
                }
                setBorder(BorderFactory.createCompoundBorder(
                    new RoundedBorder(UIConstants.BORDER, UIConstants.RADIUS_SM),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
        });
        
        setText(placeholder);
        setForeground(UIConstants.TEXT_MUTED);
    }
    
    @Override
    public String getText() {
        return showingPlaceholder ? "" : super.getText();
    }
}
```

## 圆角面板

### RoundedPanel.java
```java
public class RoundedPanel extends JPanel {
    private int radius;
    private Color shadowColor = new Color(0, 0, 0, 20);
    private boolean hasShadow;
    
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
        
        // 阴影
        if (hasShadow) {
            g2.setColor(shadowColor);
            g2.fillRoundRect(offset, offset, getWidth() - offset, getHeight() - offset, radius, radius);
        }
        
        // 背景
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth() - offset, getHeight() - offset, radius, radius);
        
        g2.dispose();
        super.paintComponent(g);
    }
}
```

## 卡片面板

### CardPanel.java
```java
public class CardPanel extends RoundedPanel {
    private String title;
    private JPanel contentPanel;
    
    public CardPanel() {
        this(null);
    }
    
    public CardPanel(String title) {
        super(UIConstants.RADIUS_LG, true);
        this.title = title;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(
            UIConstants.SPACING_LG, UIConstants.SPACING_LG,
            UIConstants.SPACING_LG, UIConstants.SPACING_LG
        ));
        
        if (title != null) {
            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(UIConstants.TITLE_SMALL);
            titleLabel.setForeground(UIConstants.TEXT_PRIMARY);
            titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, UIConstants.SPACING_MD, 0));
            add(titleLabel, BorderLayout.NORTH);
        }
        
        contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        add(contentPanel, BorderLayout.CENTER);
    }
    
    public JPanel getContentPanel() {
        return contentPanel;
    }
}
```

## 状态标签

### StatusLabel.java
```java
public class StatusLabel extends JLabel {
    public StatusLabel(String text, Color bgColor) {
        super(text);
        setOpaque(false);
        setFont(UIConstants.SMALL);
        setForeground(Color.WHITE);
        setHorizontalAlignment(CENTER);
        setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        putClientProperty("bgColor", bgColor);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor((Color) getClientProperty("bgColor"));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
        g2.dispose();
        super.paintComponent(g);
    }
    
    // 工厂方法
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
}
```

## 自定义表格

### StyledTable.java
```java
public class StyledTable extends JTable {
    public StyledTable(TableModel model) {
        super(model);
        initStyle();
    }
    
    private void initStyle() {
        setFont(UIConstants.BODY);
        setRowHeight(UIConstants.TABLE_ROW_HEIGHT);
        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));
        setSelectionBackground(UIConstants.TABLE_HOVER);
        setSelectionForeground(UIConstants.TEXT_PRIMARY);
        
        // 表头样式
        JTableHeader header = getTableHeader();
        header.setFont(UIConstants.BODY_BOLD);
        header.setBackground(UIConstants.TABLE_HEADER);
        header.setForeground(UIConstants.TEXT_PRIMARY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.BORDER));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 48));
        
        // 斑马纹
        setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : UIConstants.TABLE_ROW_ALT);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                return c;
            }
        });
    }
}
```
