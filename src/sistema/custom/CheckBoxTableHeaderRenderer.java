package sistema.custom;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.util.UIScale;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableCellRenderer;

public class CheckBoxTableHeaderRenderer extends JCheckBox implements TableCellRenderer {

    private final JTable table;
    private final int column;

    public CheckBoxTableHeaderRenderer(JTable table, int column) {
        this.table = table;
        this.column = column;
        init();
    }

    private void init() {
        putClientProperty(FlatClientProperties.STYLE, ""
                + "background:$Table.background");
        setHorizontalAlignment(SwingConstants.CENTER);

        table.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                if (SwingUtilities.isLeftMouseButton(me)) {
                    int col = table.columnAtPoint(me.getPoint());
                    if (col == column) {
                        putClientProperty(FlatClientProperties.SELECTED_STATE, null);
                        
                        // Lógica mejorada: determinar qué acción tomar basado en el estado actual
                        boolean shouldSelectAll = shouldSelectAllRows();
                        setSelected(shouldSelectAll);
                        selectedTableRow(shouldSelectAll);
                    }
                }
            }
        });

        table.getModel().addTableModelListener((tme) -> {
            if (tme.getColumn() == column || tme.getType() == TableModelEvent.DELETE) {
                checkRow();
            }
        });
    }

    /**
     * Determina si se deben seleccionar todas las filas o deseleccionar todas.
     * Si todas están seleccionadas, deselecciona.
     * Si algunas están seleccionadas (estado indeterminado), deselecciona todas.
     * Si ninguna está seleccionada, selecciona todas.
     */
    private boolean shouldSelectAllRows() {
        if (table.getRowCount() == 0) {
            return false;
        }
        
        int selectedCount = 0;
        for (int i = 0; i < table.getRowCount(); i++) {
            if ((boolean) table.getValueAt(i, column)) {
                selectedCount++;
            }
        }
        
        // Si hay alguna seleccionada (parcial o todas), deseleccionar todas
        // Solo seleccionar todas si ninguna está seleccionada
        return selectedCount == 0;
    }

    private void checkRow() {
        if (table.getRowCount() == 0) {
            putClientProperty(FlatClientProperties.SELECTED_STATE, null);
            setSelected(false);
            table.getTableHeader().repaint();
            return;
        }
        
        int selectedCount = 0;
        for (int i = 0; i < table.getRowCount(); i++) {
            if ((boolean) table.getValueAt(i, column)) {
                selectedCount++;
            }
        }
        
        if (selectedCount == 0) {
            // Ninguna seleccionada
            putClientProperty(FlatClientProperties.SELECTED_STATE, null);
            setSelected(false);
        } else if (selectedCount == table.getRowCount()) {
            // Todas seleccionadas
            putClientProperty(FlatClientProperties.SELECTED_STATE, null);
            setSelected(true);
        } else {
            // Algunas seleccionadas (estado intermedio)
            putClientProperty(FlatClientProperties.SELECTED_STATE, FlatClientProperties.SELECTED_STATE_INDETERMINATE);
            setSelected(false); // El estado visual se maneja por SELECTED_STATE_INDETERMINATE
        }
        
        table.getTableHeader().repaint();
    }

    private void selectedTableRow(boolean selected) {
        for (int i = 0; i < table.getRowCount(); i++) {
            table.setValueAt(selected, i, column);
        }
    }

    @Override
    public Component getTableCellRendererComponent(JTable jtable, Object o, boolean bln, boolean bln1, int i, int i1) {
        return this;
    }

    @Override
    protected void paintComponent(Graphics grphcs) {
        Graphics2D g2 = (Graphics2D) grphcs.create();
        g2.setColor(UIManager.getColor("TableHeader.bottomSeparatorColor"));
        float size = UIScale.scale(1f);
        g2.fill(new Rectangle2D.Float(0, getHeight() - size, getWidth(), size));
        g2.dispose();
        super.paintComponent(grphcs);
    }
}