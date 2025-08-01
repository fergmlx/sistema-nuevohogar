package sistema.custom;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * A custom text field with rounded corners, an icon, and placeholder text.
 * Can be used in NetBeans GUI Builder.
 */
public class TextFieldModificado extends JTextField {
    
    private Icon icon;
    private String placeholder;
    private Color borderColor = new Color(220, 220, 220);
    private int arcWidth = 40;
    private int arcHeight = 40;
    private int iconTextGap = 10;
    private Insets padding = new Insets(10, 55, 10, 10);
    private Color placeholderColor = new Color(170, 170, 170);
    
    /**
     * Default constructor for NetBeans compatibility
     */
    public TextFieldModificado() {
        this(null, "", 0);
    }
    
    /**
     * Constructor with customization options
     */
    public TextFieldModificado(Icon icon, String placeholder, int columns) {
        super(columns);
        this.icon = icon;
        this.placeholder = placeholder;
        
        // Remove default border
        setBorder(new EmptyBorder(padding));
        setOpaque(false);
        
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
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Paint background
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, arcWidth, arcHeight));
        
        // Set icon area
        if (icon != null) {
            icon.paintIcon(this, g2, padding.left - 35, (getHeight() - icon.getIconHeight()) / 2);
        }
        
        // Set clip to respect the rounded corners
        g2.setClip(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, arcWidth, arcHeight));
        
        // Call the parent's paintComponent
        super.paintComponent(g2);
        
        // Paint placeholder AFTER calling super to avoid being overwritten
        if (getText().isEmpty() && placeholder != null && !placeholder.isEmpty() && !isFocusOwner()) {
            g2.setColor(placeholderColor);
            g2.setFont(getFont());
            FontMetrics fontMetrics = g2.getFontMetrics();
            int textY = (getHeight() - fontMetrics.getHeight()) / 2 + fontMetrics.getAscent();
            g2.drawString(placeholder, padding.left, textY);
        }
        
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
    
    public int getIconTextGap() {
        return iconTextGap;
    }
    
    public void setIconTextGap(int iconTextGap) {
        this.iconTextGap = iconTextGap;
        repaint();
    }
}