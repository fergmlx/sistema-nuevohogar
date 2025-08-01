package sistema.animales;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class TableSimpleCellRenderer extends JLabel implements TableCellRenderer {

    private final TableCellRenderer oldCellRenderer;
    private JComponent component;
    private final int fixedWidth;

    private TableSimpleCellRenderer(JTable table) {
        this(table, 0);
    }

    private TableSimpleCellRenderer(JTable table, int fixedWidth) {
        oldCellRenderer = table.getDefaultRenderer(Object.class);
        putClientProperty(FlatClientProperties.STYLE, "" +
                "iconTextGap:5");
        this.fixedWidth = fixedWidth;

        if (fixedWidth > 0) {
            // Configure component for better text positioning
            setHorizontalAlignment(CENTER);
            setVerticalAlignment(CENTER);
            setHorizontalTextPosition(RIGHT);
            setVerticalTextPosition(CENTER);
        }
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JComponent com = (JComponent) oldCellRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (value instanceof Info) {
            Info info = (Info) value;
            // Set text, color and icon without background
            setText(info.getText());
            setForeground(info.getColor());
            setIcon(info.getIcon());
            
            // Make this label completely transparent
            setOpaque(false);
            setBackground(com.getBackground());
            
            JComponent simple = getComponent();
            // Use the original cell background and make it match exactly
            simple.setBackground(com.getBackground());
            simple.setBorder(com.getBorder());
            simple.setOpaque(com.isOpaque()); // Match the opacity of original cell
            
            return simple;
        }
        return com;
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        if (fixedWidth > 0) {
            size.width = fixedWidth;
        }
        return size;
    }

    // Remove the custom paintComponent method to eliminate the background painting

    protected LayoutManager getCelLayout() {
        return new MigLayout("insets 0,al center center");
    }

    protected JComponent getComponent() {
        if (component == null) {
            component = new JPanel(getCelLayout());
            component.add(this, "align center");
        }
        return component;
    }

    public static void apply(JTable table, Class<?> clazz) {
        table.setDefaultRenderer(clazz, new TableSimpleCellRenderer(table));
    }

    public static void apply(JTable table, Class<?> clazz, int fixedWidth) {
        table.setDefaultRenderer(clazz, new TableSimpleCellRenderer(table, fixedWidth));
    }

    public interface Info {
        String getText();
        Color getColor();
        Icon getIcon();
    }
}