package sistema.panels;

import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import sistema.dao.TareaDAO;
import sistema.modelos.Tarea;
import sistema.modelos.Usuario;

/**
 * Panel para que voluntarios vean y gestionen sus tareas asignadas
 * Para: Rol Voluntario
 */
public class TareasPanel extends JPanel {
    
    private JTable tablaTareas;
    private DefaultTableModel modeloTabla;
    private JComboBox<String> cbxFiltroEstado;
    private JTextField txtBuscar;
    private JButton btnBuscar, btnVer, btnCompletar, btnInformar;
    
    private TareaDAO tareaDAO;
    private Usuario voluntario;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    
    public TareasPanel(Usuario voluntario) {
        this.voluntario = voluntario;
        this.tareaDAO = new TareaDAO();
        
        initComponents();
        cargarTareas();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Panel superior con título y filtros
        JPanel panelSuperior = new JPanel(new BorderLayout(10, 10));
        
        JLabel lblTitulo = new JLabel("Mis Tareas");
        lblTitulo.setFont(new Font("Dialog", Font.BOLD, 18));
        panelSuperior.add(lblTitulo, BorderLayout.NORTH);
        
        // Panel de filtros
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        
        JLabel lblFiltroEstado = new JLabel("Estado:");
        cbxFiltroEstado = new JComboBox<>();
        cbxFiltroEstado.addItem("Todos");
        cbxFiltroEstado.addItem("Pendiente");
        cbxFiltroEstado.addItem("En Progreso");
        cbxFiltroEstado.addItem("Completada");
        cbxFiltroEstado.addItem("Retrasada");
        
        txtBuscar = new JTextField(15);
        
        btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscarTareas());
        
        panelFiltros.add(lblFiltroEstado);
        panelFiltros.add(cbxFiltroEstado);
        panelFiltros.add(new JLabel("Buscar:"));
        panelFiltros.add(txtBuscar);
        panelFiltros.add(btnBuscar);
        
        panelSuperior.add(panelFiltros, BorderLayout.CENTER);
        
        add(panelSuperior, BorderLayout.NORTH);
        
        // Tabla de tareas
        String[] columnas = {"ID", "Título", "Fecha Límite", "Estado", "Prioridad"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaTareas = new JTable(modeloTabla);
        tablaTareas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaTareas.setRowHeight(25);
        tablaTareas.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scrollPane = new JScrollPane(tablaTareas);
        add(scrollPane, BorderLayout.CENTER);
        
        // Panel inferior con botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        btnVer = new JButton("Ver Detalle");
        btnVer.setEnabled(false);
        btnVer.addActionListener(e -> verDetalleTarea());
        
        btnCompletar = new JButton("Marcar como Completada");
        btnCompletar.setEnabled(false);
        btnCompletar.addActionListener(e -> completarTarea());
        
        btnInformar = new JButton("Informar Progreso");
        btnInformar.setEnabled(false);
        btnInformar.addActionListener(e -> informarProgreso());
        
        panelBotones.add(btnVer);
        panelBotones.add(btnInformar);
        panelBotones.add(btnCompletar);
        
        add(panelBotones, BorderLayout.SOUTH);
        
        // Listener para habilitar/deshabilitar botones según selección
        tablaTareas.getSelectionModel().addListSelectionListener(e -> {
            boolean filaSeleccionada = tablaTareas.getSelectedRow() != -1;
            btnVer.setEnabled(filaSeleccionada);
            
            if (filaSeleccionada) {
                String estado = (String) tablaTareas.getValueAt(tablaTareas.getSelectedRow(), 3);
                boolean esPendiente = "Pendiente".equals(estado) || "En Progreso".equals(estado) || "Retrasada".equals(estado);
                
                btnCompletar.setEnabled(esPendiente);
                btnInformar.setEnabled(esPendiente);
            } else {
                btnCompletar.setEnabled(false);
                btnInformar.setEnabled(false);
            }
        });
    }
    
    private void cargarTareas() {
        // Limpiar tabla
        modeloTabla.setRowCount(0);
        
        try {
            List<Tarea> tareas = tareaDAO.obtenerPorVoluntario(voluntario);
            
            for (Tarea tarea : tareas) {
                modeloTabla.addRow(new Object[]{
                    tarea.getIdTarea(),
                    tarea.getTitulo(),
                    tarea.getFechaLimite() != null ? sdf.format(tarea.getFechaLimite()) : "Sin fecha",
                    formatearEstadoTarea(tarea.getEstado()),
                    formatearPrioridadTarea(tarea.getPrioridad())
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error al cargar las tareas: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void buscarTareas() {
        // Limpiar tabla
        modeloTabla.setRowCount(0);
        
        // Obtener criterios de filtrado
        String filtroEstado = cbxFiltroEstado.getSelectedIndex() > 0 ? 
                              (String) cbxFiltroEstado.getSelectedItem() : null;
                                 
        String textoBusqueda = txtBuscar.getText().trim();
        
        try {
            // Obtener tareas del voluntario
            List<Tarea> tareas = tareaDAO.obtenerPorVoluntario(voluntario);
            
            // Filtrar resultados según criterios
            List<Tarea> resultadosFiltrados = tareas.stream()
                .filter(t -> {
                    // Filtrar por estado
                    if (filtroEstado != null) {
                        String estadoTarea = formatearEstadoTarea(t.getEstado());
                        if (!estadoTarea.equals(filtroEstado)) {
                            return false;
                        }
                    }
                    
                    // Filtrar por texto de búsqueda
                    if (!textoBusqueda.isEmpty()) {
                        return t.getTitulo().toLowerCase().contains(textoBusqueda.toLowerCase()) ||
                              (t.getDescripcion() != null && 
                               t.getDescripcion().toLowerCase().contains(textoBusqueda.toLowerCase()));
                    }
                    
                    return true;
                })
                .collect(java.util.stream.Collectors.toList());
            
            // Mostrar resultados
            for (Tarea tarea : resultadosFiltrados) {
                modeloTabla.addRow(new Object[]{
                    tarea.getIdTarea(),
                    tarea.getTitulo(),
                    tarea.getFechaLimite() != null ? sdf.format(tarea.getFechaLimite()) : "Sin fecha",
                    formatearEstadoTarea(tarea.getEstado()),
                    formatearPrioridadTarea(tarea.getPrioridad())
                });
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error al buscar tareas: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String formatearEstadoTarea(Tarea.EstadoTarea estado) {
        if (estado == null) return "N/D";
        switch (estado) {
            case PENDIENTE: return "Pendiente";
            case COMPLETADA: return "Completada";
            case CANCELADA: return "Cancelada";
            case EN_PROGRESO: return "En Progreso";
            case RETRASADA: return "Retrasada";
            default: return estado.toString();
        }
    }
    
    private String formatearPrioridadTarea(Tarea.PrioridadTarea prioridad) {
        if (prioridad == null) return "N/D";
        switch (prioridad) {
            case BAJA: return "Baja";
            case MEDIA: return "Media";
            case ALTA: return "Alta";
            case URGENTE: return "Urgente";
            default: return prioridad.toString();
        }
    }
    
    private void verDetalleTarea() {
        int filaSeleccionada = tablaTareas.getSelectedRow();
        if (filaSeleccionada == -1) {
            return;
        }
        
        Integer idTarea = (Integer) tablaTareas.getValueAt(filaSeleccionada, 0);
        
        try {
            Tarea tarea = tareaDAO.obtenerPorId(idTarea);
            
            if (tarea == null) {
                JOptionPane.showMessageDialog(this, 
                    "No se pudo obtener la información de la tarea", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Crear panel con información detallada
            JPanel panel = new JPanel(new BorderLayout(10, 10));
            panel.setBorder(new EmptyBorder(10, 10, 10, 10));
            
            // Panel de información básica
            JPanel panelInfo = new JPanel(new GridLayout(0, 2, 10, 5));
            
            panelInfo.add(new JLabel("ID:"));
            panelInfo.add(new JLabel(String.valueOf(tarea.getIdTarea())));
            
            panelInfo.add(new JLabel("Título:"));
            panelInfo.add(new JLabel(tarea.getTitulo()));
            
            panelInfo.add(new JLabel("Estado:"));
            panelInfo.add(new JLabel(formatearEstadoTarea(tarea.getEstado())));
            
            panelInfo.add(new JLabel("Prioridad:"));
            panelInfo.add(new JLabel(formatearPrioridadTarea(tarea.getPrioridad())));
            
            panelInfo.add(new JLabel("Fecha de asignación:"));
            panelInfo.add(new JLabel(tarea.getFechaAsignacion() != null ? 
                new SimpleDateFormat("dd/MM/yyyy").format(tarea.getFechaAsignacion()) : "N/D"));
            
            panelInfo.add(new JLabel("Fecha límite:"));
            panelInfo.add(new JLabel(tarea.getFechaLimite() != null ? 
                sdf.format(tarea.getFechaLimite()) : "Sin fecha límite"));
            
            panelInfo.add(new JLabel("Asignado por:"));
            panelInfo.add(new JLabel(tarea.getAsignador() != null ? 
                tarea.getAsignador().getNombre() : "N/D"));
            
            // Panel para la descripción
            JPanel panelDesc = new JPanel(new BorderLayout());
            panelDesc.setBorder(BorderFactory.createTitledBorder("Descripción"));
            
            JTextArea txtDesc = new JTextArea(tarea.getDescripcion() != null ? tarea.getDescripcion() : "");
            txtDesc.setEditable(false);
            txtDesc.setLineWrap(true);
            txtDesc.setWrapStyleWord(true);
            txtDesc.setRows(5);
            
            panelDesc.add(new JScrollPane(txtDesc), BorderLayout.CENTER);
            
            panel.add(panelInfo, BorderLayout.NORTH);
            panel.add(panelDesc, BorderLayout.CENTER);
            
            // Mostrar panel en un diálogo
            JOptionPane.showMessageDialog(this, panel, "Detalle de Tarea", JOptionPane.PLAIN_MESSAGE);
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error al obtener los detalles de la tarea: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void completarTarea() {
        int filaSeleccionada = tablaTareas.getSelectedRow();
        if (filaSeleccionada == -1) {
            return;
        }
        
        Integer idTarea = (Integer) tablaTareas.getValueAt(filaSeleccionada, 0);
        String tituloTarea = (String) tablaTareas.getValueAt(filaSeleccionada, 1);
        
        // Solicitar comentarios de completado
        JTextArea txtComentarios = new JTextArea(3, 20);
        txtComentarios.setLineWrap(true);
        JScrollPane scrollComentarios = new JScrollPane(txtComentarios);
        
        Object[] mensaje = {
            "¿Desea marcar como completada la tarea '" + tituloTarea + "'?",
            "Comentarios:",
            scrollComentarios
        };
        
        int option = JOptionPane.showConfirmDialog(this, mensaje, "Completar Tarea", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                
        if (option == JOptionPane.OK_OPTION) {
            try {
                boolean resultado = tareaDAO.completarTarea(idTarea, txtComentarios.getText().trim());
                
                if (resultado) {
                    JOptionPane.showMessageDialog(this, 
                        "Tarea marcada como completada correctamente", 
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarTareas();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Error al completar la tarea", 
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
    
    private void informarProgreso() {
        int filaSeleccionada = tablaTareas.getSelectedRow();
        if (filaSeleccionada == -1) {
            return;
        }
        
        Integer idTarea = (Integer) tablaTareas.getValueAt(filaSeleccionada, 0);
        
        try {
            Tarea tarea = tareaDAO.obtenerPorId(idTarea);
            
            if (tarea == null) {
                JOptionPane.showMessageDialog(this, 
                    "No se pudo obtener la información de la tarea", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Formulario para informar progreso
            JTextArea txtComentarios = new JTextArea(5, 20);
            txtComentarios.setLineWrap(true);
            JScrollPane scrollComentarios = new JScrollPane(txtComentarios);
            
            JComboBox<String> cbxEstado = new JComboBox<>(new String[]{
                "En Progreso", "Retrasada"
            });
            
            Object[] mensaje = {
                "Tarea: " + tarea.getTitulo(),
                "",
                "Estado actual:",
                cbxEstado,
                "Comentarios de progreso:",
                scrollComentarios
            };
            
            int option = JOptionPane.showConfirmDialog(this, mensaje, "Informar Progreso", 
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
            if (option == JOptionPane.OK_OPTION) {
                // Actualizar estado de la tarea
                if (cbxEstado.getSelectedIndex() == 0) {
                    tarea.setEstado(Tarea.EstadoTarea.EN_PROGRESO);
                } else {
                    tarea.setEstado(Tarea.EstadoTarea.RETRASADA);
                }
                
                // Guardar comentarios en la descripción existente
                String descripcionActual = tarea.getDescripcion() != null ? tarea.getDescripcion() : "";
                String nuevaDescripcion = descripcionActual + 
                    "\n\n--- Progreso " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()) + " ---\n" +
                    txtComentarios.getText().trim();
                tarea.setDescripcion(nuevaDescripcion);
                
                boolean resultado = tareaDAO.actualizar(tarea);
                
                if (resultado) {
                    JOptionPane.showMessageDialog(this, 
                        "Progreso informado correctamente", 
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarTareas();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Error al informar el progreso", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}