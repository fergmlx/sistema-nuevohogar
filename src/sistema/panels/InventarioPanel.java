package sistema.panels;

import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import sistema.dao.InventarioDAO;
import sistema.modelos.Inventario;
import sistema.modelos.Inventario.CategoriaItem;
import sistema.modelos.Inventario.EstadoItem;
import sistema.modelos.Usuario;

/**
 * Panel para la gestión del inventario
 * Para: Rol Veterinario
 */
public class InventarioPanel extends JPanel {
    
    private JTable tablaInventario;
    private DefaultTableModel modeloTabla;
    private JComboBox<String> cbxFiltroCategoria;
    private JTextField txtBuscar;
    private JButton btnBuscar, btnNuevo, btnVer, btnEditar, btnEliminar;
    private JButton btnEntrada, btnSalida, btnLimpiarFiltros;
    
    private InventarioDAO inventarioDAO;
    private Usuario usuarioActual;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    
    public InventarioPanel(Usuario usuario) {
        this.usuarioActual = usuario;
        this.inventarioDAO = new InventarioDAO();
        
        initComponents();
        cargarInventario();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Panel superior con título y filtros
        JPanel panelSuperior = new JPanel(new BorderLayout(10, 10));
        
        JLabel lblTitulo = new JLabel("Inventario");
        lblTitulo.setFont(new Font("Dialog", Font.BOLD, 18));
        panelSuperior.add(lblTitulo, BorderLayout.NORTH);
        
        // Panel de filtros
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        
        JLabel lblFiltroCategoria = new JLabel("Categoría:");
        cbxFiltroCategoria = new JComboBox<>();
        cbxFiltroCategoria.addItem("Todas");
        for (CategoriaItem categoria : CategoriaItem.values()) {
            cbxFiltroCategoria.addItem(formatearCategoria(categoria));
        }
        
        txtBuscar = new JTextField(15);
        txtBuscar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    buscarItems();
                }
            }
        });
        
        btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscarItems());
        
        btnLimpiarFiltros = new JButton("Limpiar");
        btnLimpiarFiltros.addActionListener(e -> limpiarFiltros());
        
        // Listener para filtrar automáticamente por categoría
        cbxFiltroCategoria.addActionListener(e -> buscarItems());
        
        panelFiltros.add(lblFiltroCategoria);
        panelFiltros.add(cbxFiltroCategoria);
        panelFiltros.add(new JLabel("Buscar:"));
        panelFiltros.add(txtBuscar);
        panelFiltros.add(btnBuscar);
        panelFiltros.add(btnLimpiarFiltros);
        
        panelSuperior.add(panelFiltros, BorderLayout.CENTER);
        
        add(panelSuperior, BorderLayout.NORTH);
        
        // Tabla de inventario
        String[] columnas = {"ID", "Nombre", "Categoría", "Estado", "Cantidad", "Stock Mín", "Fecha Caducidad", "Ubicación"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 4 || columnIndex == 5) return Integer.class;
                return String.class;
            }
        };
        
        tablaInventario = new JTable(modeloTabla);
        tablaInventario.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaInventario.setRowHeight(25);
        tablaInventario.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scrollPane = new JScrollPane(tablaInventario);
        add(scrollPane, BorderLayout.CENTER);
        
        // Panel inferior con botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        btnNuevo = new JButton("Nuevo Item");
        btnNuevo.addActionListener(e -> nuevoItem());
        
        btnVer = new JButton("Ver Detalle");
        btnVer.setEnabled(false);
        btnVer.addActionListener(e -> verDetalleItem());
        
        btnEditar = new JButton("Editar");
        btnEditar.setEnabled(false);
        btnEditar.addActionListener(e -> editarItem());
        
        btnEliminar = new JButton("Eliminar");
        btnEliminar.setEnabled(false);
        btnEliminar.addActionListener(e -> eliminarItem());
        
        btnEntrada = new JButton("Registrar Entrada");
        btnEntrada.setEnabled(false);
        btnEntrada.addActionListener(e -> registrarEntrada());
        
        btnSalida = new JButton("Registrar Salida");
        btnSalida.setEnabled(false);
        btnSalida.addActionListener(e -> registrarSalida());
        
        panelBotones.add(btnNuevo);
        panelBotones.add(btnVer);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(new JSeparator(SwingConstants.VERTICAL));
        panelBotones.add(btnEntrada);
        panelBotones.add(btnSalida);
        
        add(panelBotones, BorderLayout.SOUTH);
        
        // Listener para habilitar/deshabilitar botones según selección
        tablaInventario.getSelectionModel().addListSelectionListener(e -> {
            boolean filaSeleccionada = tablaInventario.getSelectedRow() != -1;
            btnVer.setEnabled(filaSeleccionada);
            btnEditar.setEnabled(filaSeleccionada);
            btnEliminar.setEnabled(filaSeleccionada);
            btnEntrada.setEnabled(filaSeleccionada);
            btnSalida.setEnabled(filaSeleccionada);
        });
    }
    
    private void cargarInventario() {
        // Limpiar tabla
        modeloTabla.setRowCount(0);
        
        try {
            List<Inventario> items = inventarioDAO.obtenerTodos();
            
            for (Inventario item : items) {
                modeloTabla.addRow(new Object[]{
                    item.getIdItem(),
                    item.getNombre(),
                    formatearCategoria(item.getCategoria()),
                    formatearEstado(item.getEstadoItem()),
                    item.getCantidad(),
                    item.getStockMinimo(),
                    item.getFechaCaducidad() != null ? sdf.format(item.getFechaCaducidad()) : "",
                    item.getUbicacion() != null ? item.getUbicacion() : ""
                });
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error al cargar el inventario: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void buscarItems() {
        // Limpiar tabla
        modeloTabla.setRowCount(0);
        
        // Obtener criterios de filtrado
        String filtroCategoria = cbxFiltroCategoria.getSelectedIndex() > 0 ? 
                                (String) cbxFiltroCategoria.getSelectedItem() : null;
                                 
        String textoBusqueda = txtBuscar.getText().trim();
        
        try {
            // Obtener todos los items
            List<Inventario> items = inventarioDAO.obtenerTodos();
            
            // Filtrar resultados según criterios
            for (Inventario item : items) {
                boolean incluirItem = true;
                
                // Filtrar por categoría
                if (filtroCategoria != null && !filtroCategoria.equals("Todas")) {
                    String categoriaItem = formatearCategoria(item.getCategoria());
                    if (!categoriaItem.equals(filtroCategoria)) {
                        incluirItem = false;
                    }
                }
                
                // Filtrar por texto de búsqueda
                if (incluirItem && !textoBusqueda.isEmpty()) {
                    String nombreItem = item.getNombre() != null ? item.getNombre().toLowerCase() : "";
                    String descripcionItem = item.getDescripcion() != null ? item.getDescripcion().toLowerCase() : "";
                    String ubicacionItem = item.getUbicacion() != null ? item.getUbicacion().toLowerCase() : "";
                    
                    String busqueda = textoBusqueda.toLowerCase();
                    
                    if (!nombreItem.contains(busqueda) && 
                        !descripcionItem.contains(busqueda) && 
                        !ubicacionItem.contains(busqueda)) {
                        incluirItem = false;
                    }
                }
                
                // Añadir item si pasa los filtros
                if (incluirItem) {
                    modeloTabla.addRow(new Object[]{
                        item.getIdItem(),
                        item.getNombre(),
                        formatearCategoria(item.getCategoria()),
                        formatearEstado(item.getEstadoItem()),
                        item.getCantidad(),
                        item.getStockMinimo(),
                        item.getFechaCaducidad() != null ? sdf.format(item.getFechaCaducidad()) : "",
                        item.getUbicacion() != null ? item.getUbicacion() : ""
                    });
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error al buscar items: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void limpiarFiltros() {
        cbxFiltroCategoria.setSelectedIndex(0);
        txtBuscar.setText("");
        cargarInventario();
    }
    
    private String formatearCategoria(CategoriaItem categoria) {
        if (categoria == null) return "N/D";
        switch (categoria) {
            case MEDICAMENTO: return "Medicamento";
            case ALIMENTO: return "Alimento";
            case EQUIPAMIENTO: return "Equipamiento";
            case LIMPIEZA: return "Limpieza";
            case ACCESORIOS: return "Accesorios";
            case MATERIAL_MEDICO: return "Material médico";
            case OTRO: return "Otro";
            default: return categoria.toString();
        }
    }
    
    private String formatearEstado(EstadoItem estado) {
        if (estado == null) return "N/D";
        switch (estado) {
            case DISPONIBLE: return "Disponible";
            case AGOTADO: return "Agotado";
            case CADUCADO: return "Caducado";
            default: return estado.toString();
        }
    }
    
    private void nuevoItem() {
        // Formulario para nuevo item
        JTextField txtNombre = new JTextField();
        
        JComboBox<String> cbxCategoria = new JComboBox<>();
        for (CategoriaItem categoria : CategoriaItem.values()) {
            cbxCategoria.addItem(formatearCategoria(categoria));
        }
        
        JComboBox<String> cbxEstado = new JComboBox<>();
        for (EstadoItem estado : EstadoItem.values()) {
            cbxEstado.addItem(formatearEstado(estado));
        }
        cbxEstado.setSelectedItem("Disponible");
        
        JTextField txtCantidad = new JTextField("0");
        JTextField txtStockMinimo = new JTextField("0");
        JTextField txtUnidadMedida = new JTextField();
        JTextField txtUbicacion = new JTextField();
        JTextField txtPrecioUnitario = new JTextField("0.00");
        JTextField txtProveedor = new JTextField();
        JTextField txtFechaCaducidad = new JTextField();
        
        JTextArea txtDescripcion = new JTextArea(3, 20);
        txtDescripcion.setLineWrap(true);
        JScrollPane scrollDescripcion = new JScrollPane(txtDescripcion);
        
        Object[] formulario = {
            "Nombre *:", txtNombre,
            "Categoría *:", cbxCategoria,
            "Estado *:", cbxEstado,
            "Cantidad *:", txtCantidad,
            "Unidad de Medida:", txtUnidadMedida,
            "Stock Mínimo:", txtStockMinimo,
            "Ubicación:", txtUbicacion,
            "Precio Unitario:", txtPrecioUnitario,
            "Proveedor:", txtProveedor,
            "Fecha Caducidad (dd/mm/yyyy):", txtFechaCaducidad,
            "Descripción:", scrollDescripcion
        };
        
        int option = JOptionPane.showConfirmDialog(this, formulario, "Nuevo Item", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (option != JOptionPane.OK_OPTION) {
            return;
        }
        
        // Validar campos obligatorios
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "El nombre es obligatorio", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            // Crear objeto inventario
            Inventario item = new Inventario();
            item.setNombre(txtNombre.getText().trim());
            
            // Categoría
            for (CategoriaItem categoria : CategoriaItem.values()) {
                if (formatearCategoria(categoria).equals(cbxCategoria.getSelectedItem())) {
                    item.setCategoria(categoria);
                    break;
                }
            }
            
            // Estado
            for (EstadoItem estado : EstadoItem.values()) {
                if (formatearEstado(estado).equals(cbxEstado.getSelectedItem())) {
                    item.setEstadoItem(estado);
                    break;
                }
            }
            
            // Datos numéricos
            try {
                item.setCantidad(Integer.parseInt(txtCantidad.getText().trim()));
                item.setStockMinimo(Integer.parseInt(txtStockMinimo.getText().trim()));
                item.setPrecioUnitario(Double.parseDouble(txtPrecioUnitario.getText().trim()));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, 
                    "Los valores numéricos no son válidos", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            item.setUnidadMedida(txtUnidadMedida.getText().trim().isEmpty() ? null : txtUnidadMedida.getText().trim());
            item.setUbicacion(txtUbicacion.getText().trim().isEmpty() ? null : txtUbicacion.getText().trim());
            item.setProveedor(txtProveedor.getText().trim().isEmpty() ? null : txtProveedor.getText().trim());
            item.setDescripcion(txtDescripcion.getText().trim().isEmpty() ? null : txtDescripcion.getText().trim());
            
            // Fecha de caducidad
            if (!txtFechaCaducidad.getText().trim().isEmpty()) {
                try {
                    item.setFechaCaducidad(sdf.parse(txtFechaCaducidad.getText().trim()));
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, 
                        "Formato de fecha incorrecto. Use dd/mm/yyyy", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            item.setFechaAdquisicion(new Date());
            item.setAlertaGenerada(false);
            
            // Guardar item
            boolean resultado = inventarioDAO.guardar(item);
            
            if (resultado) {
                JOptionPane.showMessageDialog(this, 
                    "Item guardado correctamente", 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarInventario();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Error al guardar el item", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void verDetalleItem() {
        int filaSeleccionada = tablaInventario.getSelectedRow();
        if (filaSeleccionada == -1) {
            return;
        }
        
        Integer idItem = (Integer) tablaInventario.getValueAt(filaSeleccionada, 0);
        
        try {
            Inventario item = inventarioDAO.obtenerPorId(idItem);
            
            if (item == null) {
                JOptionPane.showMessageDialog(this, 
                    "No se pudo obtener la información del item", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Crear panel con información detallada
            JPanel panel = new JPanel(new BorderLayout(10, 10));
            panel.setBorder(new EmptyBorder(10, 10, 10, 10));
            
            // Panel de información básica
            JPanel panelInfo = new JPanel(new GridLayout(0, 2, 10, 5));
            
            panelInfo.add(new JLabel("ID:"));
            panelInfo.add(new JLabel(String.valueOf(item.getIdItem())));
            
            panelInfo.add(new JLabel("Nombre:"));
            panelInfo.add(new JLabel(item.getNombre()));
            
            panelInfo.add(new JLabel("Categoría:"));
            panelInfo.add(new JLabel(formatearCategoria(item.getCategoria())));
            
            panelInfo.add(new JLabel("Estado:"));
            panelInfo.add(new JLabel(formatearEstado(item.getEstadoItem())));
            
            panelInfo.add(new JLabel("Cantidad:"));
            panelInfo.add(new JLabel(String.valueOf(item.getCantidad())));
            
            panelInfo.add(new JLabel("Unidad de Medida:"));
            panelInfo.add(new JLabel(item.getUnidadMedida() != null ? item.getUnidadMedida() : "N/D"));
            
            panelInfo.add(new JLabel("Stock Mínimo:"));
            panelInfo.add(new JLabel(item.getStockMinimo() != null ? 
                String.valueOf(item.getStockMinimo()) : "N/D"));
            
            panelInfo.add(new JLabel("Ubicación:"));
            panelInfo.add(new JLabel(item.getUbicacion() != null ? 
                item.getUbicacion() : "N/D"));
            
            panelInfo.add(new JLabel("Precio Unitario:"));
            panelInfo.add(new JLabel(item.getPrecioUnitario() != null ? 
                String.format("$%.2f", item.getPrecioUnitario()) : "N/D"));
            
            panelInfo.add(new JLabel("Proveedor:"));
            panelInfo.add(new JLabel(item.getProveedor() != null ? item.getProveedor() : "N/D"));
            
            panelInfo.add(new JLabel("Fecha de Adquisición:"));
            panelInfo.add(new JLabel(item.getFechaAdquisicion() != null ? 
                sdf.format(item.getFechaAdquisicion()) : "N/D"));
            
            panelInfo.add(new JLabel("Fecha de Caducidad:"));
            panelInfo.add(new JLabel(item.getFechaCaducidad() != null ? 
                sdf.format(item.getFechaCaducidad()) : "N/D"));
            
            // Panel para la descripción
            if (item.getDescripcion() != null && !item.getDescripcion().isEmpty()) {
                JPanel panelDesc = new JPanel(new BorderLayout());
                panelDesc.setBorder(BorderFactory.createTitledBorder("Descripción"));
                
                JTextArea txtDesc = new JTextArea(item.getDescripcion());
                txtDesc.setEditable(false);
                txtDesc.setLineWrap(true);
                txtDesc.setWrapStyleWord(true);
                txtDesc.setRows(3);
                
                panelDesc.add(new JScrollPane(txtDesc), BorderLayout.CENTER);
                
                panel.add(panelInfo, BorderLayout.NORTH);
                panel.add(panelDesc, BorderLayout.CENTER);
            } else {
                panel.add(panelInfo, BorderLayout.CENTER);
            }
            
            // Mostrar panel en un diálogo
            JOptionPane.showMessageDialog(this, panel, "Detalle de Item", JOptionPane.PLAIN_MESSAGE);
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error al obtener los detalles del item: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void editarItem() {
        int filaSeleccionada = tablaInventario.getSelectedRow();
        if (filaSeleccionada == -1) {
            return;
        }
        
        Integer idItem = (Integer) tablaInventario.getValueAt(filaSeleccionada, 0);
        
        try {
            Inventario item = inventarioDAO.obtenerPorId(idItem);
            
            if (item == null) {
                JOptionPane.showMessageDialog(this, 
                    "No se pudo obtener la información del item", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Formulario para editar item
            JTextField txtNombre = new JTextField(item.getNombre());
            
            JComboBox<String> cbxCategoria = new JComboBox<>();
            for (CategoriaItem categoria : CategoriaItem.values()) {
                cbxCategoria.addItem(formatearCategoria(categoria));
                if (item.getCategoria() == categoria) {
                    cbxCategoria.setSelectedIndex(cbxCategoria.getItemCount() - 1);
                }
            }
            
            JComboBox<String> cbxEstado = new JComboBox<>();
            for (EstadoItem estado : EstadoItem.values()) {
                cbxEstado.addItem(formatearEstado(estado));
                if (item.getEstadoItem() == estado) {
                    cbxEstado.setSelectedIndex(cbxEstado.getItemCount() - 1);
                }
            }
            
            JTextField txtCantidad = new JTextField(String.valueOf(item.getCantidad()));
            
            JTextField txtUnidadMedida = new JTextField(item.getUnidadMedida() != null ? 
                item.getUnidadMedida() : "");
            
            JTextField txtStockMinimo = new JTextField(item.getStockMinimo() != null ? 
                String.valueOf(item.getStockMinimo()) : "0");
                
            JTextField txtUbicacion = new JTextField(item.getUbicacion() != null ? 
                item.getUbicacion() : "");
                
            JTextField txtPrecioUnitario = new JTextField(item.getPrecioUnitario() != null ? 
                String.format("%.2f", item.getPrecioUnitario()) : "0.00");
                
            JTextField txtProveedor = new JTextField(item.getProveedor() != null ? 
                item.getProveedor() : "");
                
            JTextField txtFechaCaducidad = new JTextField(item.getFechaCaducidad() != null ? 
                sdf.format(item.getFechaCaducidad()) : "");
            
            JTextArea txtDescripcion = new JTextArea(3, 20);
            txtDescripcion.setText(item.getDescripcion() != null ? item.getDescripcion() : "");
            txtDescripcion.setLineWrap(true);
            JScrollPane scrollDescripcion = new JScrollPane(txtDescripcion);
            
            Object[] formulario = {
                "Nombre *:", txtNombre,
                "Categoría *:", cbxCategoria,
                "Estado *:", cbxEstado,
                "Cantidad *:", txtCantidad,
                "Unidad de Medida:", txtUnidadMedida,
                "Stock Mínimo:", txtStockMinimo,
                "Ubicación:", txtUbicacion,
                "Precio Unitario:", txtPrecioUnitario,
                "Proveedor:", txtProveedor,
                "Fecha Caducidad (dd/mm/yyyy):", txtFechaCaducidad,
                "Descripción:", scrollDescripcion
            };
            
            int option = JOptionPane.showConfirmDialog(this, formulario, "Editar Item", 
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
            if (option != JOptionPane.OK_OPTION) {
                return;
            }
            
            // Validar campos obligatorios
            if (txtNombre.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "El nombre es obligatorio", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Actualizar objeto
            item.setNombre(txtNombre.getText().trim());
            
            // Categoría
            for (CategoriaItem categoria : CategoriaItem.values()) {
                if (formatearCategoria(categoria).equals(cbxCategoria.getSelectedItem())) {
                    item.setCategoria(categoria);
                    break;
                }
            }
            
            // Estado
            for (EstadoItem estado : EstadoItem.values()) {
                if (formatearEstado(estado).equals(cbxEstado.getSelectedItem())) {
                    item.setEstadoItem(estado);
                    break;
                }
            }
            
            // Datos numéricos
            try {
                item.setCantidad(Integer.parseInt(txtCantidad.getText().trim()));
                item.setStockMinimo(Integer.parseInt(txtStockMinimo.getText().trim()));
                item.setPrecioUnitario(Double.parseDouble(txtPrecioUnitario.getText().trim()));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, 
                    "Los valores numéricos no son válidos", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            item.setUnidadMedida(txtUnidadMedida.getText().trim().isEmpty() ? null : txtUnidadMedida.getText().trim());
            item.setUbicacion(txtUbicacion.getText().trim().isEmpty() ? null : txtUbicacion.getText().trim());
            item.setProveedor(txtProveedor.getText().trim().isEmpty() ? null : txtProveedor.getText().trim());
            item.setDescripcion(txtDescripcion.getText().trim().isEmpty() ? null : txtDescripcion.getText().trim());
            
            // Fecha de caducidad
            if (!txtFechaCaducidad.getText().trim().isEmpty()) {
                try {
                    item.setFechaCaducidad(sdf.parse(txtFechaCaducidad.getText().trim()));
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, 
                        "Formato de fecha incorrecto. Use dd/mm/yyyy", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                item.setFechaCaducidad(null);
            }
            
            // Guardar cambios
            boolean resultado = inventarioDAO.actualizar(item);
            
            if (resultado) {
                JOptionPane.showMessageDialog(this, 
                    "Item actualizado correctamente", 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarInventario();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Error al actualizar el item", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void eliminarItem() {
        int filaSeleccionada = tablaInventario.getSelectedRow();
        if (filaSeleccionada == -1) {
            return;
        }
        
        Integer idItem = (Integer) tablaInventario.getValueAt(filaSeleccionada, 0);
        String nombreItem = (String) tablaInventario.getValueAt(filaSeleccionada, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar el item '" + nombreItem + "'?",
            "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean resultado = inventarioDAO.eliminar(idItem);
                
                if (resultado) {
                    JOptionPane.showMessageDialog(this, 
                        "Item eliminado correctamente", 
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarInventario();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Error al eliminar el item", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, 
                    "Error: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void registrarEntrada() {
        int filaSeleccionada = tablaInventario.getSelectedRow();
        if (filaSeleccionada == -1) {
            return;
        }
        
        Integer idItem = (Integer) tablaInventario.getValueAt(filaSeleccionada, 0);
        String nombreItem = (String) tablaInventario.getValueAt(filaSeleccionada, 1);
        Integer cantidadActual = (Integer) tablaInventario.getValueAt(filaSeleccionada, 4);
        
        // Formulario para registrar entrada
        JTextField txtCantidad = new JTextField("1");
        JTextField txtObservaciones = new JTextField();
        
        Object[] formulario = {
            "Item:", nombreItem,
            "Cantidad actual:", cantidadActual,
            "Cantidad a agregar *:", txtCantidad,
            "Observaciones:", txtObservaciones
        };
        
        int option = JOptionPane.showConfirmDialog(this, formulario, "Registrar Entrada", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (option != JOptionPane.OK_OPTION) {
            return;
        }
        
        try {
            int cantidadEntrada = Integer.parseInt(txtCantidad.getText().trim());
            
            if (cantidadEntrada <= 0) {
                JOptionPane.showMessageDialog(this, 
                    "La cantidad debe ser mayor a 0", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Obtener el item actual
            Inventario item = inventarioDAO.obtenerPorId(idItem);
            if (item == null) {
                JOptionPane.showMessageDialog(this, 
                    "No se pudo obtener la información del item", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Actualizar cantidad
            int nuevaCantidad = item.getCantidad() + cantidadEntrada;
            item.setCantidad(nuevaCantidad);
            
            // Si estaba agotado y ahora tiene stock, cambiar estado
            if (item.getEstadoItem() == EstadoItem.AGOTADO && nuevaCantidad > 0) {
                item.setEstadoItem(EstadoItem.DISPONIBLE);
            }
            
            // Actualizar en base de datos
            boolean resultado = inventarioDAO.actualizar(item);
            
            if (resultado) {
                // Aquí podrías registrar el movimiento en una tabla de historial
                // Por ahora solo mostramos confirmación
                JOptionPane.showMessageDialog(this, 
                    "Entrada registrada correctamente\n" +
                    "Cantidad anterior: " + cantidadActual + "\n" +
                    "Cantidad agregada: " + cantidadEntrada + "\n" +
                    "Nueva cantidad: " + nuevaCantidad, 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarInventario();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Error al registrar la entrada", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "La cantidad debe ser un número válido", 
                "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void registrarSalida() {
        int filaSeleccionada = tablaInventario.getSelectedRow();
        if (filaSeleccionada == -1) {
            return;
        }
        
        Integer idItem = (Integer) tablaInventario.getValueAt(filaSeleccionada, 0);
        String nombreItem = (String) tablaInventario.getValueAt(filaSeleccionada, 1);
        Integer cantidadActual = (Integer) tablaInventario.getValueAt(filaSeleccionada, 4);
        
        if (cantidadActual <= 0) {
            JOptionPane.showMessageDialog(this, 
                "No hay stock disponible para este item", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Formulario para registrar salida
        JTextField txtCantidad = new JTextField("1");
        JTextField txtObservaciones = new JTextField();
        
        Object[] formulario = {
            "Item:", nombreItem,
            "Cantidad actual:", cantidadActual,
            "Cantidad a retirar *:", txtCantidad,
            "Observaciones:", txtObservaciones
        };
        
        int option = JOptionPane.showConfirmDialog(this, formulario, "Registrar Salida", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (option != JOptionPane.OK_OPTION) {
            return;
        }
        
        try {
            int cantidadSalida = Integer.parseInt(txtCantidad.getText().trim());
            
            if (cantidadSalida <= 0) {
                JOptionPane.showMessageDialog(this, 
                    "La cantidad debe ser mayor a 0", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (cantidadSalida > cantidadActual) {
                JOptionPane.showMessageDialog(this, 
                    "No hay suficiente stock disponible\n" +
                    "Stock actual: " + cantidadActual + "\n" +
                    "Cantidad solicitada: " + cantidadSalida, 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Obtener el item actual
            Inventario item = inventarioDAO.obtenerPorId(idItem);
            if (item == null) {
                JOptionPane.showMessageDialog(this, 
                    "No se pudo obtener la información del item", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Actualizar cantidad
            int nuevaCantidad = item.getCantidad() - cantidadSalida;
            item.setCantidad(nuevaCantidad);
            
            // Verificar estado según la nueva cantidad
            if (nuevaCantidad == 0) {
                item.setEstadoItem(EstadoItem.AGOTADO);
            } else if (item.getStockMinimo() != null && 
                       nuevaCantidad <= item.getStockMinimo()) {
                // Generar alerta de stock bajo
                item.setAlertaGenerada(true);
                // Aquí podrías implementar lógica para notificaciones
            }
            
            // Actualizar en base de datos
            boolean resultado = inventarioDAO.actualizar(item);
            
            if (resultado) {
                // Mensaje de confirmación
                String mensaje = "Salida registrada correctamente\n" +
                    "Cantidad anterior: " + cantidadActual + "\n" +
                    "Cantidad retirada: " + cantidadSalida + "\n" +
                    "Nueva cantidad: " + nuevaCantidad;
                
                // Agregar advertencia si queda poco stock
                if (item.getStockMinimo() != null && nuevaCantidad <= item.getStockMinimo()) {
                    mensaje += "\n\n⚠️ ADVERTENCIA: Stock por debajo del mínimo (" + 
                              item.getStockMinimo() + ")";
                }
                
                JOptionPane.showMessageDialog(this, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarInventario();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Error al registrar la salida", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "La cantidad debe ser un número válido", 
                "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Método para obtener items con stock bajo (opcional, para uso externo)
     */
    public List<Inventario> obtenerItemsStockBajo() {
        try {
            return inventarioDAO.obtenerItemsStockBajo();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Método para refrescar la tabla desde el exterior
     */
    public void refrescarTabla() {
        cargarInventario();
    }
}