package sistema.adopciones;

import sistema.animales.*;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import sistema.dao.AnimalDAO;
import sistema.modelos.Animal;

public class MascotaTableRenderer implements TableCellRenderer {

    private final TableCellRenderer oldCellRenderer;

    public MascotaTableRenderer(JTable table) {
        oldCellRenderer = table.getDefaultRenderer(Object.class);
    }

    @Override
    public Component getTableCellRendererComponent(JTable jtable, Object o, boolean bln, boolean bln1, int i, int i1) {
        Component com = oldCellRenderer.getTableCellRendererComponent(jtable, o, bln, bln1, i, i1);
        TableCellMascota cell = new TableCellMascota((Animal) o, com.getFont());
        cell.setBackground(com.getBackground());
        return cell;
    }
}
