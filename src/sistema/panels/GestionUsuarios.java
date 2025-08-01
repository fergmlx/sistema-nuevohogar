package sistema.panels;

import conexiondb.core.ConexionBD;
import sistema.modelos.Usuario;
import sistema.modelos.Usuario.RolUsuario;
import sistema.dao.UsuarioDAO;
import sistema.modelos.RolComboBoxModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ArrayList;
import javax.swing.table.TableCellRenderer;

public class GestionUsuarios extends JPanel {
    
    private JTable tablaUsuarios;
    private DefaultTableModel modeloTabla;
    private JTextField txtBuscar;
    private JButton btnAgregar, btnEditar, btnEliminar;
    private JComboBox<String> cbxFiltroRol;
    private UsuarioDAO usuarioDAO;
    private List<Integer> usuariosSeleccionados;
    
    public GestionUsuarios() {
        usuarioDAO = new UsuarioDAO();
        usuariosSeleccionados = new ArrayList<>();
        initComponents();
        cargarDatos();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);
        
        // Panel superior con título y buscador
        JPanel panelSuperior = new JPanel(new BorderLayout(15, 0));
        panelSuperior.setOpaque(false);
        
        JLabel lblTitulo = new JLabel("Gestión de Usuarios");
        lblTitulo.setFont(new Font("Poppins", Font.BOLD, 24));
        panelSuperior.add(lblTitulo, BorderLayout.WEST);
        
        // Panel de búsqueda
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBusqueda.setOpaque(false);
        
        txtBuscar = new JTextField(15);
        txtBuscar.putClientProperty("JTextField.placeholderText", "Buscar usuario...");
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchKeyReleased(evt);
            }
        });
        
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.putClientProperty("JButton.buttonType", "roundRect");
        btnBuscar.addActionListener(e -> buscarUsuarios());
        
        // ComboBox para filtrar por rol
        JLabel lblFiltro = new JLabel("Filtrar por rol:");
        cbxFiltroRol = new JComboBox<>();
        cbxFiltroRol.setModel(new RolComboBoxModel());
        cbxFiltroRol.insertItemAt("Todos", 0);
        cbxFiltroRol.setSelectedIndex(0);
        cbxFiltroRol.addActionListener(e -> cargarDatos());
        
        panelBusqueda.add(lblFiltro);
        panelBusqueda.add(cbxFiltroRol);
        panelBusqueda.add(txtBuscar);
        panelBusqueda.add(btnBuscar);
        
        panelSuperior.add(panelBusqueda, BorderLayout.EAST);
        add(panelSuperior, BorderLayout.NORTH);
        
        // Tabla de usuarios
        // Nota: Añadimos una columna de selección al inicio
        String[] columnas = {"Seleccionar", "ID", "Nombre", "Email", "Rol", "Acciones"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) { // Primera columna es boolean para checkboxes
                    return Boolean.class;
                }
                return super.getColumnClass(columnIndex);
            }
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0 || column == 5; // Solo la columna de selección y acciones son editables
            }
        };
        
        tablaUsuarios = new JTable(modeloTabla);
        tablaUsuarios.setRowHeight(50);
        tablaUsuarios.setShowVerticalLines(false);
        tablaUsuarios.setSelectionBackground(new Color(237, 242, 255));
        tablaUsuarios.setSelectionForeground(Color.BLACK);
        tablaUsuarios.setIntercellSpacing(new Dimension(0, 8));
        tablaUsuarios.getTableHeader().setBackground(new Color(240, 240, 240));
        tablaUsuarios.getTableHeader().setFont(new Font("Poppins", Font.BOLD, 12));
        
        // Configurar renderers para cada columna
        configurarColumnasTabla();
        
        // Listener para detectar cambios en los checkboxes
        modeloTabla.addTableModelListener(e -> {
            if (e.getColumn() == 0) { // Columna de checkboxes
                int row = e.getFirstRow();
                Boolean isSelected = (Boolean) modeloTabla.getValueAt(row, 0);
                int idUsuario = (int) modeloTabla.getValueAt(row, 1);
                
                if (isSelected) {
                    if (!usuariosSeleccionados.contains(idUsuario)) {
                        usuariosSeleccionados.add(idUsuario);
                    }
                } else {
                    usuariosSeleccionados.remove(Integer.valueOf(idUsuario));
                }
                
                // Habilitar/deshabilitar botones según selección
                actualizarEstadoBotones();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tablaUsuarios);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);
        
        // Panel inferior con botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.setOpaque(false);
        
        btnAgregar = new JButton("Agregar Usuario");
        btnAgregar.putClientProperty("JButton.buttonType", "roundRect");
        btnAgregar.addActionListener(this::agregarUsuario);
        
        btnEditar = new JButton("Editar");
        btnEditar.setEnabled(false);
        btnEditar.putClientProperty("JButton.buttonType", "roundRect");
        btnEditar.addActionListener(this::editarUsuario);
        
        btnEliminar = new JButton("Eliminar");
        btnEliminar.setEnabled(false);
        btnEliminar.putClientProperty("JButton.buttonType", "roundRect");
        btnEliminar.addActionListener(this::eliminarUsuario);
        
        panelBotones.add(btnAgregar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        
        add(panelBotones, BorderLayout.SOUTH);
    }
    
    private void txtSearchKeyReleased(java.awt.event.KeyEvent evt) {                                      
        buscarUsuarios();
    }   
    
    private void actualizarEstadoBotones() {
        boolean haySeleccionados = !usuariosSeleccionados.isEmpty();
        btnEditar.setEnabled(haySeleccionados && usuariosSeleccionados.size() == 1);
        btnEliminar.setEnabled(haySeleccionados);
    }
    
    private void configurarColumnasTabla() {
        // Configurar ancho de columnas
        tablaUsuarios.getColumnModel().getColumn(0).setMaxWidth(80); // Seleccionar
        tablaUsuarios.getColumnModel().getColumn(1).setMaxWidth(50); // ID
        tablaUsuarios.getColumnModel().getColumn(2).setPreferredWidth(200); // Nombre
        tablaUsuarios.getColumnModel().getColumn(3).setPreferredWidth(250); // Email
        tablaUsuarios.getColumnModel().getColumn(4).setPreferredWidth(100); // Rol
        tablaUsuarios.getColumnModel().getColumn(5).setPreferredWidth(100); // Acciones
        
        // Renderer para rol
        DefaultTableCellRenderer rolRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof RolUsuario) {
                    switch ((RolUsuario) value) {
                        case Administrador:
                            value = "Administrador";
                            setForeground(new Color(41, 121, 255));
                            break;
                        case Voluntario:
                            value = "Voluntario";
                            setForeground(new Color(76, 175, 80));
                            break;
                        default:
                            setForeground(table.getForeground());
                    }
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        };
        rolRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        // Renderer para botones de acción
        tablaUsuarios.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        tablaUsuarios.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox()));
        
        // Aplicar renderers
        tablaUsuarios.getColumnModel().getColumn(4).setCellRenderer(rolRenderer);
    }
    
    private void cargarDatos() {
        modeloTabla.setRowCount(0);
        usuariosSeleccionados.clear();
        
        List<Usuario> usuarios;
        
        // Verificar si hay un filtro de rol seleccionado
        if (cbxFiltroRol.getSelectedIndex() > 0) {
            RolComboBoxModel model = (RolComboBoxModel) cbxFiltroRol.getModel();
            RolUsuario rolFiltro = model.getRolAtIndex(cbxFiltroRol.getSelectedIndex() - 1);
            usuarios = usuarioDAO.obtenerPorRol(rolFiltro);
        } else {
            usuarios = usuarioDAO.obtenerTodos();
        }
        
        for (Usuario usuario : usuarios) {
            modeloTabla.addRow(new Object[]{
                false, // Checkbox de selección inicialmente no marcado
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol(),
                "Acciones"
            });
        }
        
        actualizarEstadoBotones();
    }
    
    private void buscarUsuarios() {
        String termino = txtBuscar.getText().trim();
        if (termino.isEmpty()) {
            cargarDatos();
            return;
        }

        modeloTabla.setRowCount(0);
        usuariosSeleccionados.clear();

        // Usar el método DAO para buscar (ahora devuelve resultados sin duplicados)
        List<Usuario> resultados = usuarioDAO.buscarPorNombreOEmail(termino);

        // Aplicar filtro de rol si está seleccionado
        if (cbxFiltroRol.getSelectedIndex() > 0) {
            RolComboBoxModel model = (RolComboBoxModel) cbxFiltroRol.getModel();
            RolUsuario rolFiltro = model.getRolAtIndex(cbxFiltroRol.getSelectedIndex() - 1);

            // Crear una nueva lista filtrada
            List<Usuario> resultadosFiltrados = new ArrayList<>();
            for (Usuario u : resultados) {
                if (u.getRol() == rolFiltro) {
                    resultadosFiltrados.add(u);
                }
            }
            resultados = resultadosFiltrados;
        }

        // Llenar la tabla con los resultados
        for (Usuario usuario : resultados) {
            modeloTabla.addRow(new Object[]{
                false, // Checkbox no marcado
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol(),
                "Acciones"
            });
        }

        actualizarEstadoBotones();
    }
    
    private void agregarUsuario(ActionEvent evt) {
        DialogoUsuario dialogo = new DialogoUsuario(SwingUtilities.getWindowAncestor(this));
        dialogo.setVisible(true);
        
        if (dialogo.isAceptado()) {
            cargarDatos();
        }
    }
    
    private void editarUsuario(ActionEvent evt) {
        if (usuariosSeleccionados.size() != 1) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione exactamente un usuario para editar", 
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idUsuario = usuariosSeleccionados.get(0);
        Usuario usuario = usuarioDAO.obtenerPorId(idUsuario);
        
        if (usuario != null) {
            DialogoUsuario dialogo = new DialogoUsuario(SwingUtilities.getWindowAncestor(this), usuario);
            dialogo.setVisible(true);
            
            if (dialogo.isAceptado()) {
                cargarDatos();
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                "No se pudo encontrar el usuario seleccionado", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void eliminarUsuario(ActionEvent evt) {
        if (usuariosSeleccionados.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione al menos un usuario para eliminar", 
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro que desea eliminar " + 
            (usuariosSeleccionados.size() == 1 ? "este usuario?" : 
            "estos " + usuariosSeleccionados.size() + " usuarios?"),
            "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
            
        if (confirmacion == JOptionPane.YES_OPTION) {
            int eliminados = 0;
            
            for (Integer idUsuario : usuariosSeleccionados) {
                if (usuarioDAO.eliminar(idUsuario)) {
                    eliminados++;
                }
            }
            
            if (eliminados > 0) {
                JOptionPane.showMessageDialog(this, 
                    "Se " + (eliminados == 1 ? "ha" : "han") + " eliminado " + 
                    eliminados + " " + (eliminados == 1 ? "usuario" : "usuarios") + 
                    " correctamente", 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarDatos();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "No se pudo eliminar ningún usuario", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Clases internas para los botones en la tabla
    
    private class ButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton btnEditar, btnEliminar;
        
        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
            setOpaque(true);
            
            btnEditar = new JButton("Editar");
            btnEditar.setToolTipText("Editar");
            btnEditar.setPreferredSize(new Dimension(65, 30));
            
            btnEliminar = new JButton("Eliminar");
            btnEliminar.setToolTipText("Eliminar");
            btnEliminar.setPreferredSize(new Dimension(75, 30));
            
            add(btnEditar);
            add(btnEliminar);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            return this;
        }
    }
    
    private class ButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton btnEditar, btnEliminar;
        private int fila;
        private String accion;
        
        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            
            btnEditar = new JButton("Editar");
            btnEditar.setToolTipText("Editar");
            btnEditar.setPreferredSize(new Dimension(65, 30));
            btnEditar.addActionListener(e -> {
                accion = "editar";
                fireEditingStopped();
            });
            
            btnEliminar = new JButton("Eliminar");
            btnEliminar.setToolTipText("Eliminar");
            btnEliminar.setPreferredSize(new Dimension(75, 30));
            btnEliminar.addActionListener(e -> {
                accion = "eliminar";
                fireEditingStopped();
            });
            
            panel.add(btnEditar);
            panel.add(btnEliminar);
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, 
                boolean isSelected, int row, int column) {
            fila = row;
            accion = null;
            panel.setBackground(table.getSelectionBackground());
            return panel;
        }
        
        @Override
        public Object getCellEditorValue() {
            if (accion != null) {
                int idUsuario = (int) modeloTabla.getValueAt(fila, 1);
                
                if (accion.equals("editar")) {
                    // Seleccionar este usuario
                    usuariosSeleccionados.clear();
                    usuariosSeleccionados.add(idUsuario);
                    actualizarEstadoBotones();
                    
                    // Y editar
                    editarUsuario(new ActionEvent(btnEditar, ActionEvent.ACTION_PERFORMED, "edit"));
                } else if (accion.equals("eliminar")) {
                    // Seleccionar este usuario
                    usuariosSeleccionados.clear();
                    usuariosSeleccionados.add(idUsuario);
                    actualizarEstadoBotones();
                    
                    // Y eliminar
                    eliminarUsuario(new ActionEvent(btnEliminar, ActionEvent.ACTION_PERFORMED, "delete"));
                }
            }
            return "Acciones";
        }
    }
}