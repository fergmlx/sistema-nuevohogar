package sistema.custom;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * A custom password field with rounded corners, an icon, placeholder text,
 * and show/hide password functionality. Can be used in NetBeans GUI Builder.
 */
public class PasswordFieldModificado extends JPasswordField {
    
    private Icon icon;
    private String placeholder;
    private Icon showIcon;
    private Icon hideIcon;
    private boolean passwordVisible = false;
    private Color borderColor = new Color(220, 220, 220);
    private int arcWidth = 40;
    private int arcHeight = 40;
    private int iconTextGap = 10;
    private Insets padding = new Insets(10, 55, 10, 40);
    private Color placeholderColor = new Color(170, 170, 170);
    private Rectangle showHideButtonRect;
    
    /**
     * Default constructor for NetBeans compatibility
     */
    public PasswordFieldModificado() {
        this(null, "", null, null, 0);
    }
    
    /**
     * Constructor with customization options
     */
    public PasswordFieldModificado(Icon icon, String placeholder, Icon showIcon, Icon hideIcon, int columns) {
        super(columns);
        this.icon = icon;
        this.placeholder = placeholder;
        this.showIcon = showIcon;
        this.hideIcon = hideIcon;
        
        // Remove default border
        setBorder(new EmptyBorder(padding));
        setOpaque(false);
        setEchoChar('•');
        
        // Add focus listener to repaint on focus change
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                repaint();
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                repaint();
            }
        });
        
        // Add mouse listener for show/hide password button
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (showHideButtonRect != null && showHideButtonRect.contains(e.getPoint())) {
                    passwordVisible = !passwordVisible;
                    updateEchoChar();
                    repaint();
                }
            }
        });
        
        // Add mouse motion listener for hover effect
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (showHideButtonRect != null && showHideButtonRect.contains(e.getPoint())) {
                    setCursor(new Cursor(Cursor.HAND_CURSOR));
                } else {
                    setCursor(new Cursor(Cursor.TEXT_CURSOR));
                }
            }
        });
    }
    
    private void updateEchoChar() {
        if (passwordVisible) {
            setEchoChar((char) 0); // Show password
        } else {
            setEchoChar('•'); // Hide password
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Paint background
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, arcWidth, arcHeight));
        
        // Paint icon
        if (icon != null) {
            icon.paintIcon(this, g2, padding.left - 35, (getHeight() - icon.getIconHeight()) / 2);
        }
        
        // Paint show/hide button
        int buttonSize = 20;
        int buttonX = getWidth() - buttonSize - 15;
        int buttonY = (getHeight() - buttonSize) / 2;
        showHideButtonRect = new Rectangle(buttonX, buttonY, buttonSize, buttonSize);
        
        Icon currentIcon = passwordVisible ? hideIcon : showIcon;
        if (currentIcon != null) {
            currentIcon.paintIcon(this, g2, buttonX, buttonY);
        } else {
            // Draw a default show/hide icon if no custom icons are provided
            g2.setColor(new Color(170, 170, 170));
            g2.setStroke(new BasicStroke(1.5f));
            
            if (passwordVisible) {
                // Eye with line across (hide)
                g2.drawOval(buttonX + 2, buttonY + 6, 10, 8);
                g2.drawLine(buttonX + 7, buttonY + 10, buttonX + 7, buttonY + 10);
                g2.drawLine(buttonX, buttonY + 18, buttonX + 16, buttonY + 2);
            } else {
                // Simple eye (show)
                g2.drawOval(buttonX + 2, buttonY + 6, 10, 8);
                g2.fillOval(buttonX + 6, buttonY + 10, 2, 2);
            }
        }
        
        // Set clip to respect rounded corners
        g2.setClip(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, arcWidth, arcHeight));
        
        // Paint placeholder if the field is empty
        if (getPassword().length == 0 && placeholder != null && !placeholder.isEmpty() && !isFocusOwner()) {
            g2.setColor(placeholderColor);
            g2.setFont(getFont());
            FontMetrics fontMetrics = g2.getFontMetrics();
            g2.drawString(placeholder, padding.left, 
                    (getHeight() - fontMetrics.getHeight()) / 2 + fontMetrics.getAscent());
        }
        
        // Call the parent's paintComponent
        super.paintComponent(g2);
        
        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Paint rounded border
        g2.setColor(borderColor);
        g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, arcWidth, arcHeight));
        
        g2.dispose();
    }

    @Override
    public Insets getInsets() {
        return padding;
    }

    // Properties getters and setters for NetBeans property editor
    
    public Icon getIcon() {
        return icon;
    }
    
    public void setIcon(Icon icon) {
        this.icon = icon;
        repaint();
    }
    
    public String getPlaceholder() {
        return placeholder;
    }
    
    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }
    
    public Icon getShowIcon() {
        return showIcon;
    }
    
    public void setShowIcon(Icon showIcon) {
        this.showIcon = showIcon;
        repaint();
    }
    
    public Icon getHideIcon() {
        return hideIcon;
    }
    
    public void setHideIcon(Icon hideIcon) {
        this.hideIcon = hideIcon;
        repaint();
    }
    
    public Color getBorderColor() {
        return borderColor;
    }
    
    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
        repaint();
    }
    
    public int getArcWidth() {
        return arcWidth;
    }
    
    public void setArcWidth(int arcWidth) {
        this.arcWidth = arcWidth;
        repaint();
    }
    
    public int getArcHeight() {
        return arcHeight;
    }
    
    public void setArcHeight(int arcHeight) {
        this.arcHeight = arcHeight;
        repaint();
    }
    
    public Color getPlaceholderColor() {
        return placeholderColor;
    }
    
    public void setPlaceholderColor(Color placeholderColor) {
        this.placeholderColor = placeholderColor;
        repaint();
    }
    
    public boolean isPasswordVisible() {
        return passwordVisible;
    }
    
    public void setPasswordVisible(boolean passwordVisible) {
        this.passwordVisible = passwordVisible;
        updateEchoChar();
        repaint();
    }
}