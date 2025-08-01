package sistema.panels;

import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import sistema.dao.DonacionDAO;
import sistema.modelos.Donacion;
import sistema.modelos.Donacion.TipoDonacion;
import sistema.modelos.Usuario;

/**
 * Panel para la gestión de donaciones
 * Para: Rol Coordinador
 */
public class DonacionesPanel extends JPanel {
    
    private JTable tablaDonaciones;
    private DefaultTableModel modeloTabla;
    private JComboBox<String> cbxFiltroTipo;
    private JTextField txtBuscar;
    private JButton btnBuscar, btnNueva, btnVer, btnGenerarRecibo, btnExportar;
    
    private DonacionDAO donacionDAO;
    private Usuario usuarioActual;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    
    public DonacionesPanel(Usuario usuario) {
        this.usuarioActual = usuario;
        this.donacionDAO = new DonacionDAO();
        
        initComponents();
        cargarDonaciones();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Panel superior con título y filtros
        JPanel panelSuperior = new JPanel(new BorderLayout(10, 10));
        
        JLabel lblTitulo = new JLabel("Donaciones");
        lblTitulo.setFont(new Font("Dialog", Font.BOLD, 18));
        panelSuperior.add(lblTitulo, BorderLayout.NORTH);
        
        // Panel de filtros
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        
        JLabel lblFiltroTipo = new JLabel("Tipo:");
        cbxFiltroTipo = new JComboBox<>();
        cbxFiltroTipo.addItem("Todos");
        for (TipoDonacion tipo : TipoDonacion.values()) {
            cbxFiltroTipo.addItem(formatearTipoDonacion(tipo));
        }
        
        txtBuscar = new JTextField(15);
        
        btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscarDonaciones());
        
        panelFiltros.add(lblFiltroTipo);
        panelFiltros.add(cbxFiltroTipo);
        panelFiltros.add(new JLabel("Buscar:"));
        panelFiltros.add(txtBuscar);
        panelFiltros.add(btnBuscar);
        
        panelSuperior.add(panelFiltros, BorderLayout.CENTER);
        
        add(panelSuperior, BorderLayout.NORTH);
        
        // Tabla de donaciones
        String[] columnas = {"ID", "Donante", "Tipo", "Fecha", "Monto/Descripción", "Recibo"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 5) return Boolean.class;
                return String.class;
            }
        };
        
        tablaDonaciones = new JTable(modeloTabla);
        tablaDonaciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaDonaciones.setRowHeight(25);
        tablaDonaciones.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scrollPane = new JScrollPane(tablaDonaciones);
        add(scrollPane, BorderLayout.CENTER);
        
        // Panel inferior con botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        btnNueva = new JButton("Nueva Donación");
        btnNueva.addActionListener(e -> nuevaDonacion());
        
        btnVer = new JButton("Ver Detalle");
        btnVer.setEnabled(false);
        btnVer.addActionListener(e -> verDetalleDonacion());
        
        btnGenerarRecibo = new JButton("Generar Recibo");
        btnGenerarRecibo.setEnabled(false);
        btnGenerarRecibo.addActionListener(e -> generarRecibo());
        
        btnExportar = new JButton("Exportar a Excel");
        btnExportar.addActionListener(e -> exportarExcel());
        
        panelBotones.add(btnNueva);
        panelBotones.add(btnVer);
        panelBotones.add(btnGenerarRecibo);
        panelBotones.add(btnExportar);
        
        add(panelBotones, BorderLayout.SOUTH);
        
        // Listener para habilitar/deshabilitar botones según selección
        tablaDonaciones.getSelectionModel().addListSelectionListener(e -> {
            boolean filaSeleccionada = tablaDonaciones.getSelectedRow() != -1;
            btnVer.setEnabled(filaSeleccionada);
            
            if (filaSeleccionada) {
                Boolean tieneRecibo = (Boolean) tablaDonaciones.getValueAt(tablaDonaciones.getSelectedRow(), 5);
                btnGenerarRecibo.setEnabled(!tieneRecibo);
            } else {
                btnGenerarRecibo.setEnabled(false);
            }
        });
    }
    
    private void cargarDonaciones() {
        // Limpiar tabla
        modeloTabla.setRowCount(0);
        
        try {
            List<Donacion> donaciones = donacionDAO.obtenerTodas();
            
            for (Donacion donacion : donaciones) {
                String descripcionOmonto = donacion.getTipoDonacion() == TipoDonacion.MONETARIA ? 
                    String.format("$%.2f", donacion.getMonto()) : 
                    (donacion.getDescripcionItems() != null ? donacion.getDescripcionItems() : "");
                
                modeloTabla.addRow(new Object[]{
                    donacion.getIdDonacion(),
                    donacion.getNombreDonante(),
                    formatearTipoDonacion(donacion.getTipoDonacion()),
                    donacion.getFechaDonacion() != null ? sdf.format(donacion.getFechaDonacion()) : "",
                    descripcionOmonto,
                    donacion.getReciboEmitido()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error al cargar las donaciones: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void buscarDonaciones() {
        // Limpiar tabla
        modeloTabla.setRowCount(0);
        
        // Obtener criterios de filtrado
        String filtroTipo = cbxFiltroTipo.getSelectedIndex() > 0 ? 
                           (String) cbxFiltroTipo.getSelectedItem() : null;
                                 
        String textoBusqueda = txtBuscar.getText().trim();
        
        try {
            // Obtener todas las donaciones
            List<Donacion> donaciones = donacionDAO.obtenerTodas();
            
            // Filtrar resultados según criterios
            List<Donacion> resultadosFiltrados = donaciones.stream()
                .filter(d -> {
                    // Filtrar por tipo
                    if (filtroTipo != null) {
                        String tipoDonacion = formatearTipoDonacion(d.getTipoDonacion());
                        if (!tipoDonacion.equals(filtroTipo)) {
                            return false;
                        }
                    }
                    
                    // Filtrar por texto de búsqueda
                    if (!textoBusqueda.isEmpty()) {
                        return d.getNombreDonante().toLowerCase().contains(textoBusqueda.toLowerCase()) ||
                              (d.getEmailDonante() != null && 
                               d.getEmailDonante().toLowerCase().contains(textoBusqueda.toLowerCase())) ||
                              (d.getDescripcionItems() != null &&
                               d.getDescripcionItems().toLowerCase().contains(textoBusqueda.toLowerCase()));
                    }
                    
                    return true;
                })
                .collect(java.util.stream.Collectors.toList());
            
            // Mostrar resultados
            for (Donacion donacion : resultadosFiltrados) {
                String descripcionOmonto = donacion.getTipoDonacion() == TipoDonacion.MONETARIA ? 
                    String.format("$%.2f", donacion.getMonto()) : 
                    (donacion.getDescripcionItems() != null ? donacion.getDescripcionItems() : "");
                
                modeloTabla.addRow(new Object[]{
                    donacion.getIdDonacion(),
                    donacion.getNombreDonante(),
                    formatearTipoDonacion(donacion.getTipoDonacion()),
                    donacion.getFechaDonacion() != null ? sdf.format(donacion.getFechaDonacion()) : "",
                    descripcionOmonto,
                    donacion.getReciboEmitido()
                });
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error al buscar donaciones: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String formatearTipoDonacion(TipoDonacion tipo) {
        if (tipo == null) return "N/D";
        switch (tipo) {
            case MONETARIA: return "Monetaria";
            case ALIMENTOS: return "Alimentos";
            case MEDICAMENTOS: return "Medicamentos";
            case INSUMOS: return "Insumos";
            case SERVICIOS: return "Servicios";
            case OTRO: return "Otro";
            default: return tipo.toString();
        }
    }
    
    private void nuevaDonacion() {
        // Formulario para nueva donación
        JTextField txtNombre = new JTextField();
        JTextField txtApellido = new JTextField();
        JTextField txtEmail = new JTextField();
        JTextField txtTelefono = new JTextField();
        
        JComboBox<String> cbxTipo = new JComboBox<>();
        for (TipoDonacion tipo : TipoDonacion.values()) {
            cbxTipo.addItem(formatearTipoDonacion(tipo));
        }
        
        JTextField txtMonto = new JTextField("0.00");
        JTextArea txtDescripcion = new JTextArea(3, 20);
        txtDescripcion.setLineWrap(true);
        JScrollPane scrollDescripcion = new JScrollPane(txtDescripcion);
        
        // Habilitar/deshabilitar campos según tipo de donación
        cbxTipo.addActionListener(e -> {
            boolean esMonetaria = cbxTipo.getSelectedIndex() == 0; // Monetaria
            txtMonto.setEnabled(esMonetaria);
            txtDescripcion.setEnabled(!esMonetaria);
        });
        
        Object[] formulario = {
            "Nombre del Donante:", txtNombre,
            "Email:", txtEmail,
            "Teléfono:", txtTelefono,
            "Tipo de Donación:", cbxTipo,
            "Monto ($):", txtMonto,
            "Descripción:", scrollDescripcion
        };
        
        int option = JOptionPane.showConfirmDialog(this, formulario, "Nueva Donación", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (option != JOptionPane.OK_OPTION) {
            return;
        }
        
        // Validar campos obligatorios
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "El nombre del donante es obligatorio", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            // Crear objeto donación
            Donacion donacion = new Donacion();
            donacion.setNombreDonante(txtNombre.getText().trim());
            donacion.setEmailDonante(txtEmail.getText().trim());
            donacion.setTelefonoDonante(txtTelefono.getText().trim());
            
            // Tipo de donación
            for (TipoDonacion tipo : TipoDonacion.values()) {
                if (formatearTipoDonacion(tipo).equals(cbxTipo.getSelectedItem())) {
                    donacion.setTipoDonacion(tipo);
                    break;
                }
            }
            
            // Monto o descripción según tipo
            if (donacion.getTipoDonacion() == TipoDonacion.MONETARIA) {
                try {
                    donacion.setMonto(Double.parseDouble(txtMonto.getText().trim()));
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, 
                        "El monto debe ser un valor numérico", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                donacion.setDescripcionItems(txtDescripcion.getText().trim());
            }
            
            donacion.setFechaDonacion(new Date());
            donacion.setReciboEmitido(false);
            donacion.setReceptor(usuarioActual);
            
            // Guardar donación
            boolean resultado = donacionDAO.guardar(donacion);
            
            if (resultado) {
                JOptionPane.showMessageDialog(this, 
                    "Donación registrada correctamente", 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarDonaciones();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Error al registrar la donación", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void verDetalleDonacion() {
        int filaSeleccionada = tablaDonaciones.getSelectedRow();
        if (filaSeleccionada == -1) {
            return;
        }
        
        Integer idDonacion = (Integer) tablaDonaciones.getValueAt(filaSeleccionada, 0);
        
        try {
            Donacion donacion = donacionDAO.obtenerPorId(idDonacion);
            
            if (donacion == null) {
                JOptionPane.showMessageDialog(this, 
                    "No se pudo obtener la información de la donación", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Crear panel con información detallada
            JPanel panel = new JPanel(new BorderLayout(10, 10));
            panel.setBorder(new EmptyBorder(10, 10, 10, 10));
            
            // Panel de información básica
            JPanel panelInfo = new JPanel(new GridLayout(0, 2, 10, 5));
            
            panelInfo.add(new JLabel("ID:"));
            panelInfo.add(new JLabel(String.valueOf(donacion.getIdDonacion())));
            
            panelInfo.add(new JLabel("Nombre del Donante:"));
            panelInfo.add(new JLabel(donacion.getNombreDonante() + " " + 
                (donacion.getNombreDonante() != null ? donacion.getNombreDonante() : "")));
            
            panelInfo.add(new JLabel("Email:"));
            panelInfo.add(new JLabel(donacion.getEmailDonante() != null ? 
                donacion.getEmailDonante() : "N/D"));
            
            panelInfo.add(new JLabel("Teléfono:"));
            panelInfo.add(new JLabel(donacion.getTelefonoDonante() != null ? 
                donacion.getTelefonoDonante() : "N/D"));
            
            panelInfo.add(new JLabel("Tipo de Donación:"));
            panelInfo.add(new JLabel(formatearTipoDonacion(donacion.getTipoDonacion())));
            
            panelInfo.add(new JLabel("Fecha:"));
            panelInfo.add(new JLabel(donacion.getFechaDonacion() != null ? 
                sdf.format(donacion.getFechaDonacion()) : "N/D"));
            
            if (donacion.getTipoDonacion() == TipoDonacion.MONETARIA) {
                panelInfo.add(new JLabel("Monto:"));
                panelInfo.add(new JLabel(donacion.getMonto() != null ? 
                    String.format("$%.2f", donacion.getMonto()) : "N/D"));
            }
            
            panelInfo.add(new JLabel("Recibo Emitido:"));
            panelInfo.add(new JLabel(donacion.getReciboEmitido() ? "Sí" : "No"));
            
            if (donacion.getReciboEmitido() && donacion.getNumeroRecibo() != null) {
                panelInfo.add(new JLabel("Número de Recibo:"));
                panelInfo.add(new JLabel(donacion.getNumeroRecibo()));
            }
            
            panelInfo.add(new JLabel("Receptor:"));
            panelInfo.add(new JLabel(donacion.getReceptor() != null ? 
                donacion.getReceptor().getNombre() : "N/D"));
            
            // Panel para la descripción (si no es monetaria)
            if (donacion.getTipoDonacion() != TipoDonacion.MONETARIA && 
                donacion.getDescripcionItems() != null && 
                !donacion.getDescripcionItems().isEmpty()) {
                
                JPanel panelDesc = new JPanel(new BorderLayout());
                panelDesc.setBorder(BorderFactory.createTitledBorder("Descripción"));
                
                JTextArea txtDesc = new JTextArea(donacion.getDescripcionItems());
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
            JOptionPane.showMessageDialog(this, panel, "Detalle de Donación", JOptionPane.PLAIN_MESSAGE);
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error al obtener los detalles de la donación: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void generarRecibo() {
        int filaSeleccionada = tablaDonaciones.getSelectedRow();
        if (filaSeleccionada == -1) {
            return;
        }
        
        Integer idDonacion = (Integer) tablaDonaciones.getValueAt(filaSeleccionada, 0);
        String nombreDonante = (String) tablaDonaciones.getValueAt(filaSeleccionada, 1);
        
        // Generar número de recibo
        String numeroRecibo = "REC-" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + 
                             "-" + String.format("%04d", idDonacion);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Desea generar un recibo para la donación de " + nombreDonante + "?\n" +
            "Número de recibo: " + numeroRecibo,
            "Generar Recibo", JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean resultado = donacionDAO.generarRecibo(idDonacion, numeroRecibo);
                
                if (resultado) {
                    JOptionPane.showMessageDialog(this, 
                        "Recibo generado correctamente\n" +
                        "Número de recibo: " + numeroRecibo,
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarDonaciones();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Error al generar el recibo", 
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
    
    private void exportarExcel() {
        JOptionPane.showMessageDialog(this, 
            "Funcionalidad de exportación a Excel pendiente de implementar", 
            "Información", JOptionPane.INFORMATION_MESSAGE);
    }
}