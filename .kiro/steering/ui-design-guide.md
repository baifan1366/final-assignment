# Seminar Management System - UI 设计规范

## 设计理念
学术风格、专业简洁、层次分明的现代化界面设计。

## 配色方案
```java
// 主色调 - 学术蓝
public static final Color PRIMARY = new Color(25, 55, 109);      // #19376D 深蓝
public static final Color PRIMARY_LIGHT = new Color(87, 108, 188); // #576CBC 浅蓝
public static final Color ACCENT = new Color(10, 194, 239);       // #0AC2EF 强调色

// 背景色
public static final Color BG_MAIN = new Color(248, 249, 252);     // #F8F9FC 主背景
public static final Color BG_CARD = Color.WHITE;                   // 卡片背景
public static final Color BG_SIDEBAR = new Color(25, 55, 109);    // 侧边栏

// 文字色
public static final Color TEXT_PRIMARY = new Color(33, 37, 41);   // #212529 主文字
public static final Color TEXT_SECONDARY = new Color(108, 117, 125); // #6C757D 次要文字
public static final Color TEXT_LIGHT = Color.WHITE;                // 浅色文字

// 状态色
public static final Color SUCCESS = new Color(40, 167, 69);       // #28A745 成功
public static final Color WARNING = new Color(255, 193, 7);       // #FFC107 警告
public static final Color DANGER = new Color(220, 53, 69);        // #DC3545 错误
public static final Color INFO = new Color(23, 162, 184);         // #17A2B8 信息
```

## 字体规范
```java
// 字体家族
public static final String FONT_FAMILY = "Segoe UI";
public static final String FONT_FAMILY_CN = "Microsoft YaHei";

// 字体大小
public static final Font TITLE_LARGE = new Font(FONT_FAMILY, Font.BOLD, 24);
public static final Font TITLE_MEDIUM = new Font(FONT_FAMILY, Font.BOLD, 18);
public static final Font TITLE_SMALL = new Font(FONT_FAMILY, Font.BOLD, 14);
public static final Font BODY = new Font(FONT_FAMILY, Font.PLAIN, 13);
public static final Font BODY_SMALL = new Font(FONT_FAMILY, Font.PLAIN, 11);
```

## 间距规范
```java
// 间距单位 (px)
public static final int SPACING_XS = 4;
public static final int SPACING_SM = 8;
public static final int SPACING_MD = 16;
public static final int SPACING_LG = 24;
public static final int SPACING_XL = 32;

// 圆角
public static final int BORDER_RADIUS_SM = 4;
public static final int BORDER_RADIUS_MD = 8;
public static final int BORDER_RADIUS_LG = 12;
```

## 组件样式

### 按钮样式
```java
// 主要按钮 - 蓝色填充
class PrimaryButton extends JButton {
    // 背景: PRIMARY, 文字: WHITE, 圆角: 8px
    // Hover: PRIMARY_LIGHT, 阴影效果
}

// 次要按钮 - 边框样式
class SecondaryButton extends JButton {
    // 背景: 透明, 边框: PRIMARY, 文字: PRIMARY
    // Hover: 浅蓝背景
}

// 危险按钮 - 红色
class DangerButton extends JButton {
    // 背景: DANGER, 文字: WHITE
}
```

### 输入框样式
```java
// 统一输入框样式
// 边框: 1px #DEE2E6, 圆角: 6px
// Focus: 边框变为 PRIMARY_LIGHT, 添加浅蓝阴影
// 内边距: 10px 12px
// 高度: 38px
```

### 卡片样式
```java
// 内容卡片
// 背景: WHITE, 圆角: 12px
// 阴影: 0 2px 8px rgba(0,0,0,0.08)
// 内边距: 20px
```

### 表格样式
```java
// 表头: 背景 #F1F3F5, 文字加粗
// 行高: 44px
// 斑马纹: 奇数行 WHITE, 偶数行 #F8F9FA
// Hover: #E9ECEF
// 边框: 无边框，使用分隔线
```
