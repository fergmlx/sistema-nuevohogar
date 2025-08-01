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
import sistema.dao.UsuarioDAO;
import sistema.modelos.Tarea;
import sistema.modelos.Tarea.EstadoTarea;
import sistema.modelos.Tarea.PrioridadTarea;
import sistema.modelos.Usuario;

/**
 * Panel para gestión de voluntariado y tareas
 * Para: Administrador, Coordinador
 */
public class VoluntariadoPanel extends JPanel {
    
    private JTabbedPane tabbedPane;
    private JTable tablaTareas;
    private DefaultTableModel modeloTabla;
    private JComboBox<String> cbxFiltroEstado;
    private JComboBox<String> cbxFiltroVoluntario;
    private JTextField txtBuscar;
    private JButton btnBuscar, btnNueva, btnVer, btnEditar, btnEliminar;
    
    private TareaDAO tareaDAO;
    private UsuarioDAO usuarioDAO;
    private Usuario usuarioActual;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    
    public VoluntariadoPanel(Usuario usuario) {
        this.usuarioActual = usuario;
        this.tareaDAO = new TareaDAO();
        this.usuarioDAO = new UsuarioDAO();
        
        initComponents();
        cargarTareas();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Pestañas
        tabbedPane = new JTabbedPane();
        
        // Pestaña de tareas
        JPanel panelTareas = crearPanelTareas();
        tabbedPane.addTab("Tareas", panelTareas);
        
        // Pestaña de horarios
        JPanel panelHorarios = crearPanelHorarios();
        //tabbedPane.addTab("Horarios", panelHorarios);
        
        // Pestaña de estadísticas
        JPanel panelEstadisticas = crearPanelEstadisticas();
        //tabbedPane.addTab("Estadísticas", panelEstadisticas);
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel crearPanelTareas() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Panel superior con filtros
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        
        JLabel lblFiltroEstado = new JLabel("Estado:");
        cbxFiltroEstado = new JComboBox<>();
        cbxFiltroEstado.addItem("Todos");
        for (Tarea.EstadoTarea estado : Tarea.EstadoTarea.values()) {
            cbxFiltroEstado.addItem(formatearEstadoTarea(estado));
        }
        
        JLabel lblFiltroVoluntario = new JLabel("Voluntario:");
        cbxFiltroVoluntario = new JComboBox<>();
        cbxFiltroVoluntario.addItem("Todos");
        cargarVoluntarios();
        
        txtBuscar = new JTextField(15);
        
        btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscarTareas());
        
        panelFiltros.add(lblFiltroEstado);
        panelFiltros.add(cbxFiltroEstado);
        panelFiltros.add(lblFiltroVoluntario);
        panelFiltros.add(cbxFiltroVoluntario);
        panelFiltros.add(new JLabel("Buscar:"));
        panelFiltros.add(txtBuscar);
        panelFiltros.add(btnBuscar);
        
        panel.add(panelFiltros, BorderLayout.NORTH);
        
        // Tabla de tareas
        String[] columnas = {"ID", "Título", "Voluntario", "Fecha Límite", "Estado", "Prioridad"};
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
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        btnNueva = new JButton("Nueva Tarea");
        btnNueva.addActionListener(e -> nuevaTarea());
        
        btnVer = new JButton("Ver Detalle");
        btnVer.setEnabled(false);
        btnVer.addActionListener(e -> verDetalleTarea());
        
        btnEditar = new JButton("Editar");
        btnEditar.setEnabled(false);
        btnEditar.addActionListener(e -> editarTarea());
        
        btnEliminar = new JButton("Eliminar");
        btnEliminar.setEnabled(false);
        btnEliminar.addActionListener(e -> eliminarTarea());
        
        panelBotones.add(btnNueva);
        panelBotones.add(btnVer);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        
        panel.add(panelBotones, BorderLayout.SOUTH);
        
        // Listener para habilitar/deshabilitar botones según selección
        tablaTareas.getSelectionModel().addListSelectionListener(e -> {
            boolean filaSeleccionada = tablaTareas.getSelectedRow() != -1;
            btnVer.setEnabled(filaSeleccionada);
            btnEditar.setEnabled(filaSeleccionada);
            btnEliminar.setEnabled(filaSeleccionada);
        });
        
        return panel;
    }
    
    private JPanel crearPanelHorarios() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // En una implementación real, aquí se mostraría un calendario con los horarios de los voluntarios
        JLabel lblInfo = new JLabel("Calendario de horarios de voluntarios");
        lblInfo.setFont(new Font("Dialog", Font.BOLD, 16));
        lblInfo.setHorizontalAlignment(SwingConstants.CENTER);
        
        panel.add(lblInfo, BorderLayout.NORTH);
        
        // Agregar un panel simulando un calendario
        JPanel calendarPanel = new JPanel(new GridLayout(5, 7, 2, 2));
        String[] diasSemana = {"Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"};
        
        for (String dia : diasSemana) {
            JLabel lblDia = new JLabel(dia);
            lblDia.setHorizontalAlignment(SwingConstants.CENTER);
            lblDia.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            lblDia.setOpaque(true);
            lblDia.setBackground(new Color(230, 230, 230));
            calendarPanel.add(lblDia);
        }
        
        for (int i = 1; i <= 28; i++) {
            JPanel cellPanel = new JPanel(new BorderLayout());
            JLabel lblFecha = new JLabel(String.valueOf(i));
            lblFecha.setHorizontalAlignment(SwingConstants.RIGHT);
            
            cellPanel.add(lblFecha, BorderLayout.NORTH);
            
            // Simular algunos voluntarios asignados
            if (i % 3 == 0) {
                JLabel lblVoluntario = new JLabel("Ana, Carlos");
                lblVoluntario.setFont(new Font("Dialog", Font.PLAIN, 9));
                cellPanel.add(lblVoluntario, BorderLayout.CENTER);
            } else if (i % 5 == 0) {
                JLabel lblVoluntario = new JLabel("Luis");
                lblVoluntario.setFont(new Font("Dialog", Font.PLAIN, 9));
                cellPanel.add(lblVoluntario, BorderLayout.CENTER);
            }
            
            cellPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            calendarPanel.add(cellPanel);
        }
        
        panel.add(calendarPanel, BorderLayout.CENTER);
        
        // Botones de acción
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton btnAsignarHorario = new JButton("Asignar Horario");
        btnAsignarHorario.addActionListener(e -> 
            JOptionPane.showMessageDialog(this, 
                "Funcionalidad de asignación de horarios pendiente de implementar", 
                "Información", JOptionPane.INFORMATION_MESSAGE));
        
        JButton btnVerHorarios = new JButton("Ver Todos los Horarios");
        btnVerHorarios.addActionListener(e -> 
            JOptionPane.showMessageDialog(this, 
                "Funcionalidad de visualización de horarios pendiente de implementar", 
                "Información", JOptionPane.INFORMATION_MESSAGE));
        
        panelBotones.add(btnAsignarHorario);
        panelBotones.add(btnVerHorarios);
        
        panel.add(panelBotones, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel crearPanelEstadisticas() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel lblTitulo = new JLabel("Estadísticas de Voluntariado");
        lblTitulo.setFont(new Font("Dialog", Font.BOLD, 16));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        
        panel.add(lblTitulo, BorderLayout.NORTH);
        
        // Panel con estadísticas
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        statsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Estadística 1: Tareas completadas
        JPanel panel1 = new JPanel(new BorderLayout());
        panel1.setBorder(BorderFactory.createTitledBorder("Tareas Completadas (Mes)"));
        JLabel lblStat1 = new JLabel("85%");
        lblStat1.setFont(new Font("Dialog", Font.BOLD, 24));
        lblStat1.setHorizontalAlignment(SwingConstants.CENTER);
        panel1.add(lblStat1, BorderLayout.CENTER);
        
        // Estadística 2: Voluntarios activos
        JPanel panel2 = new JPanel(new BorderLayout());
        panel2.setBorder(BorderFactory.createTitledBorder("Voluntarios Activos"));
        JLabel lblStat2 = new JLabel("12");
        lblStat2.setFont(new Font("Dialog", Font.BOLD, 24));
        lblStat2.setHorizontalAlignment(SwingConstants.CENTER);
        panel2.add(lblStat2, BorderLayout.CENTER);
        
        // Estadística 3: Horas voluntariado
        JPanel panel3 = new JPanel(new BorderLayout());
        panel3.setBorder(BorderFactory.createTitledBorder("Horas de Voluntariado (Mes)"));
        JLabel lblStat3 = new JLabel("156");
        lblStat3.setFont(new Font("Dialog", Font.BOLD, 24));
        lblStat3.setHorizontalAlignment(SwingConstants.CENTER);
        panel3.add(lblStat3, BorderLayout.CENTER);
        
        // Estadística 4: Tareas pendientes
        JPanel panel4 = new JPanel(new BorderLayout());
        panel4.setBorder(BorderFactory.createTitledBorder("Tareas Pendientes"));
        JLabel lblStat4 = new JLabel("7");
        lblStat4.setFont(new Font("Dialog", Font.BOLD, 24));
        lblStat4.setHorizontalAlignment(SwingConstants.CENTER);
        panel4.add(lblStat4, BorderLayout.CENTER);
        
        statsPanel.add(panel1);
        statsPanel.add(panel2);
        statsPanel.add(panel3);
        statsPanel.add(panel4);
        
        panel.add(statsPanel, BorderLayout.CENTER);
        
        // Panel para botones de informes
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton btnInformeActividad = new JButton("Generar Informe de Actividad");
        btnInformeActividad.addActionListener(e -> 
            JOptionPane.showMessageDialog(this, 
                "Funcionalidad de generación de informes pendiente de implementar", 
                                "Información", JOptionPane.INFORMATION_MESSAGE));
                
        JButton btnExportar = new JButton("Exportar Estadísticas");
        btnExportar.addActionListener(e -> 
            JOptionPane.showMessageDialog(this, 
                "Funcionalidad de exportación pendiente de implementar", 
                "Información", JOptionPane.INFORMATION_MESSAGE));
                
        panelBotones.add(btnInformeActividad);
        panelBotones.add(btnExportar);
        
        panel.add(panelBotones, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void cargarVoluntarios() {
        try {
            // Limpiar combo antes de cargar
            cbxFiltroVoluntario.removeAllItems();
            cbxFiltroVoluntario.addItem("Todos");

            List<Usuario> voluntarios = usuarioDAO.obtenerPorRol(Usuario.RolUsuario.Voluntario);

            for (Usuario voluntario : voluntarios) {
                cbxFiltroVoluntario.addItem(voluntario.getIdUsuario() + ": " + voluntario.getNombre());
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error al cargar la lista de voluntarios: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cargarTareas() {
        // Limpiar tabla
        modeloTabla.setRowCount(0);
        
        try {
            List<Tarea> tareas = tareaDAO.obtenerTodas();
            
            for (Tarea tarea : tareas) {
                modeloTabla.addRow(new Object[]{
                    tarea.getIdTarea(),
                    tarea.getTitulo(),
                    tarea.getVoluntario() != null ? tarea.getVoluntario().getNombre() : "Sin asignar",
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

        String filtroVoluntario = cbxFiltroVoluntario.getSelectedIndex() > 0 ? 
                                 (String) cbxFiltroVoluntario.getSelectedItem() : null;

        String textoBusqueda = txtBuscar.getText().trim().toLowerCase();

        try {
            List<Tarea> tareas = tareaDAO.obtenerTodas();

            // Filtrar resultados según criterios
            for (Tarea tarea : tareas) {
                boolean cumpleFiltros = true;

                // Filtrar por estado
                if (filtroEstado != null && cumpleFiltros) {
                    String estadoActual = formatearEstadoTarea(tarea.getEstado());
                    if (!estadoActual.equals(filtroEstado)) {
                        cumpleFiltros = false;
                    }
                }

                // Filtrar por voluntario
                if (filtroVoluntario != null && cumpleFiltros) {
                    try {
                        String[] partes = filtroVoluntario.split(":");
                        int idVoluntario = Integer.parseInt(partes[0].trim());

                        if (tarea.getVoluntario() == null || 
                            !tarea.getVoluntario().getIdUsuario().equals(idVoluntario)) {
                            cumpleFiltros = false;
                        }
                    } catch (Exception e) {
                        cumpleFiltros = false;
                    }
                }

                // Filtrar por texto de búsqueda
                if (!textoBusqueda.isEmpty() && cumpleFiltros) {
                    boolean coincideTexto = false;

                    if (tarea.getTitulo() != null && 
                        tarea.getTitulo().toLowerCase().contains(textoBusqueda)) {
                        coincideTexto = true;
                    }

                    if (!coincideTexto && tarea.getDescripcion() != null && 
                        tarea.getDescripcion().toLowerCase().contains(textoBusqueda)) {
                        coincideTexto = true;
                    }

                    if (!coincideTexto && tarea.getVoluntario() != null && 
                        tarea.getVoluntario().getNombre().toLowerCase().contains(textoBusqueda)) {
                        coincideTexto = true;
                    }

                    if (!coincideTexto) {
                        cumpleFiltros = false;
                    }
                }

                // Si cumple todos los filtros, agregar a la tabla
                if (cumpleFiltros) {
                    modeloTabla.addRow(new Object[]{
                        tarea.getIdTarea(),
                        tarea.getTitulo(),
                        tarea.getVoluntario() != null ? tarea.getVoluntario().getNombre() : "Sin asignar",
                        tarea.getFechaLimite() != null ? sdf.format(tarea.getFechaLimite()) : "Sin fecha",
                        formatearEstadoTarea(tarea.getEstado()),
                        formatearPrioridadTarea(tarea.getPrioridad())
                    });
                }
            }

            // Mostrar mensaje si no hay resultados
            if (modeloTabla.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, 
                    "No se encontraron tareas que coincidan con los criterios de búsqueda", 
                    "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
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
    
    private void nuevaTarea() {
        try {
            // Obtener voluntarios para asignar la tarea
            List<Usuario> voluntarios = usuarioDAO.obtenerPorRol(Usuario.RolUsuario.Voluntario);

            // Formulario para nueva tarea
            JTextField txtTitulo = new JTextField(20);
            JTextArea txtDescripcion = new JTextArea(4, 25);
            txtDescripcion.setLineWrap(true);
            txtDescripcion.setWrapStyleWord(true);

            JComboBox<String> cbxVoluntario = new JComboBox<>();
            cbxVoluntario.addItem("Sin asignar");

            for (Usuario voluntario : voluntarios) {
                cbxVoluntario.addItem(voluntario.getIdUsuario() + ": " + voluntario.getNombre());
            }

            JComboBox<String> cbxPrioridad = new JComboBox<>(new String[]{
                "Baja", "Media", "Alta", "Urgente"
            });
            cbxPrioridad.setSelectedIndex(1); // Media por defecto

            // Fecha límite por defecto: una semana después
            Date fechaDefecto = new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000L);
            JTextField txtFechaLimite = new JTextField(sdf.format(fechaDefecto));

            // Panel del formulario
            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;

            // Título (obligatorio)
            gbc.gridx = 0; gbc.gridy = 0;
            panel.add(new JLabel("*Título:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
            panel.add(txtTitulo, gbc);

            // Descripción
            gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
            panel.add(new JLabel("Descripción:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH;
            panel.add(new JScrollPane(txtDescripcion), gbc);

            // Voluntario
            gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
            panel.add(new JLabel("Voluntario:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
            panel.add(cbxVoluntario, gbc);

            // Prioridad
            gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
            panel.add(new JLabel("Prioridad:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
            panel.add(cbxPrioridad, gbc);

            // Fecha límite
            gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE;
            panel.add(new JLabel("Fecha límite (dd/MM/yyyy):"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
            panel.add(txtFechaLimite, gbc);

            // Nota sobre campos obligatorios
            gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
            JLabel lblNota = new JLabel("* Campos obligatorios");
            lblNota.setFont(lblNota.getFont().deriveFont(Font.ITALIC));
            lblNota.setForeground(Color.GRAY);
            panel.add(lblNota, gbc);

            int option = JOptionPane.showConfirmDialog(this, panel, "Nueva Tarea", 
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (option != JOptionPane.OK_OPTION) {
                return;
            }

            // Validar campos obligatorios
            if (txtTitulo.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "El título es obligatorio", 
                    "Error de validación", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Crear objeto tarea
            Tarea tarea = new Tarea();
            tarea.setTitulo(txtTitulo.getText().trim());
            tarea.setDescripcion(txtDescripcion.getText().trim().isEmpty() ? null : txtDescripcion.getText().trim());

            // Asignar voluntario
            if (cbxVoluntario.getSelectedIndex() > 0) {
                try {
                    String seleccion = (String) cbxVoluntario.getSelectedItem();
                    String[] partes = seleccion.split(":");
                    int idVoluntario = Integer.parseInt(partes[0].trim());
                    Usuario voluntario = usuarioDAO.obtenerPorId(idVoluntario);
                    tarea.setVoluntario(voluntario);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, 
                        "Error al asignar voluntario", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // Asignar prioridad
            PrioridadTarea[] prioridades = {
                PrioridadTarea.BAJA, PrioridadTarea.MEDIA, 
                PrioridadTarea.ALTA, PrioridadTarea.URGENTE
            };
            tarea.setPrioridad(prioridades[cbxPrioridad.getSelectedIndex()]);

            // Asignar fecha límite
            if (!txtFechaLimite.getText().trim().isEmpty()) {
                try {
                    Date fechaLimite = sdf.parse(txtFechaLimite.getText().trim());
                    // Validar que la fecha no sea anterior a hoy
                    if (fechaLimite.before(new Date())) {
                        int confirm = JOptionPane.showConfirmDialog(this,
                            "La fecha límite es anterior a la fecha actual. ¿Desea continuar?",
                            "Fecha límite", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                        if (confirm != JOptionPane.YES_OPTION) {
                            return;
                        }
                    }
                    tarea.setFechaLimite(fechaLimite);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, 
                        "Formato de fecha incorrecto. Use dd/MM/yyyy", 
                        "Error de formato", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // Establecer valores por defecto
            tarea.setFechaAsignacion(new Date());
            tarea.setEstado(EstadoTarea.PENDIENTE);
            tarea.setAsignador(usuarioActual);

            // Guardar tarea
            boolean resultado = tareaDAO.guardar(tarea);

            if (resultado) {
                JOptionPane.showMessageDialog(this, 
                    "Tarea creada correctamente", 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarTareas();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Error al crear la tarea", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error inesperado: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
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
                new SimpleDateFormat("dd/MM/yyyy HH:mm").format(tarea.getFechaAsignacion()) : "N/D"));
            
            panelInfo.add(new JLabel("Fecha límite:"));
            panelInfo.add(new JLabel(tarea.getFechaLimite() != null ? 
                sdf.format(tarea.getFechaLimite()) : "Sin fecha límite"));
            
            panelInfo.add(new JLabel("Voluntario:"));
            panelInfo.add(new JLabel(tarea.getVoluntario() != null ? 
                tarea.getVoluntario().getNombre() : "Sin asignar"));
            
            panelInfo.add(new JLabel("Asignado por:"));
            panelInfo.add(new JLabel(tarea.getAsignador() != null ? 
                tarea.getAsignador().getNombre() : "N/D"));
            
            if (tarea.getEstado() == Tarea.EstadoTarea.COMPLETADA) {
                panelInfo.add(new JLabel("Fecha de completado:"));
                panelInfo.add(new JLabel(tarea.getFechaCompletada() != null ? 
                    new SimpleDateFormat("dd/MM/yyyy HH:mm").format(tarea.getFechaCompletada()) : "N/D"));
            }
            
            // Panel para la descripción
            JPanel panelDesc = new JPanel(new BorderLayout());
            panelDesc.setBorder(BorderFactory.createTitledBorder("Descripción"));
            
            JTextArea txtDesc = new JTextArea(tarea.getDescripcion() != null ? tarea.getDescripcion() : "");
            txtDesc.setEditable(false);
            txtDesc.setLineWrap(true);
            txtDesc.setWrapStyleWord(true);
            txtDesc.setRows(5);
            
            panelDesc.add(new JScrollPane(txtDesc), BorderLayout.CENTER);
            
            // Panel para comentarios (si está completada)
            if (tarea.getEstado() == Tarea.EstadoTarea.COMPLETADA && 
                tarea.getComentariosCompletado() != null && 
                !tarea.getComentariosCompletado().isEmpty()) {
                
                JPanel panelComentarios = new JPanel(new BorderLayout());
                panelComentarios.setBorder(BorderFactory.createTitledBorder("Comentarios de Completado"));
                
                JTextArea txtComentarios = new JTextArea(tarea.getComentariosCompletado());
                txtComentarios.setEditable(false);
                txtComentarios.setLineWrap(true);
                txtComentarios.setWrapStyleWord(true);
                txtComentarios.setRows(3);
                
                panelComentarios.add(new JScrollPane(txtComentarios), BorderLayout.CENTER);
                
                // Unir paneles
                JPanel panelTextos = new JPanel(new GridLayout(2, 1, 0, 10));
                panelTextos.add(panelDesc);
                panelTextos.add(panelComentarios);
                
                panel.add(panelInfo, BorderLayout.NORTH);
                panel.add(panelTextos, BorderLayout.CENTER);
            } else {
                panel.add(panelInfo, BorderLayout.NORTH);
                panel.add(panelDesc, BorderLayout.CENTER);
            }
            
            // Mostrar panel en un diálogo
            JOptionPane.showMessageDialog(this, panel, "Detalle de Tarea", JOptionPane.PLAIN_MESSAGE);
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error al obtener los detalles de la tarea: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void editarTarea() {
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

            // Obtener voluntarios
            List<Usuario> voluntarios = usuarioDAO.obtenerPorRol(Usuario.RolUsuario.Voluntario);

            // Formulario para editar tarea
            JTextField txtTitulo = new JTextField(tarea.getTitulo(), 20);

            JTextArea txtDescripcion = new JTextArea(4, 25);
            txtDescripcion.setText(tarea.getDescripcion() != null ? tarea.getDescripcion() : "");
            txtDescripcion.setLineWrap(true);
            txtDescripcion.setWrapStyleWord(true);

            JComboBox<String> cbxVoluntario = new JComboBox<>();
            cbxVoluntario.addItem("Sin asignar");
            int voluntarioSeleccionado = 0;

            for (int i = 0; i < voluntarios.size(); i++) {
                Usuario voluntario = voluntarios.get(i);
                cbxVoluntario.addItem(voluntario.getIdUsuario() + ": " + voluntario.getNombre());
                if (tarea.getVoluntario() != null && 
                    tarea.getVoluntario().getIdUsuario().equals(voluntario.getIdUsuario())) {
                    voluntarioSeleccionado = i + 1;
                }
            }
            cbxVoluntario.setSelectedIndex(voluntarioSeleccionado);

            JComboBox<String> cbxEstado = new JComboBox<>(new String[]{
                "Pendiente", "En Progreso", "Completada", "Cancelada", "Retrasada"
            });

            // Establecer estado actual
            if (tarea.getEstado() != null) {
                switch (tarea.getEstado()) {
                    case PENDIENTE: cbxEstado.setSelectedIndex(0); break;
                    case EN_PROGRESO: cbxEstado.setSelectedIndex(1); break;
                    case COMPLETADA: cbxEstado.setSelectedIndex(2); break;
                    case CANCELADA: cbxEstado.setSelectedIndex(3); break;
                    case RETRASADA: cbxEstado.setSelectedIndex(4); break;
                }
            }

            JComboBox<String> cbxPrioridad = new JComboBox<>(new String[]{
                "Baja", "Media", "Alta", "Urgente"
            });

            // Establecer prioridad actual
            if (tarea.getPrioridad() != null) {
                switch (tarea.getPrioridad()) {
                    case BAJA: cbxPrioridad.setSelectedIndex(0); break;
                    case MEDIA: cbxPrioridad.setSelectedIndex(1); break;
                    case ALTA: cbxPrioridad.setSelectedIndex(2); break;
                    case URGENTE: cbxPrioridad.setSelectedIndex(3); break;
                }
            }

            JTextField txtFechaLimite = new JTextField(
                tarea.getFechaLimite() != null ? sdf.format(tarea.getFechaLimite()) : "");

            // Panel del formulario
            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;

            // Título (obligatorio)
            gbc.gridx = 0; gbc.gridy = 0;
            panel.add(new JLabel("*Título:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
            panel.add(txtTitulo, gbc);

            // Descripción
            gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
            panel.add(new JLabel("Descripción:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH;
            panel.add(new JScrollPane(txtDescripcion), gbc);

            // Voluntario
            gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
            panel.add(new JLabel("Voluntario:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
            panel.add(cbxVoluntario, gbc);

            // Estado
            gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
            panel.add(new JLabel("Estado:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
            panel.add(cbxEstado, gbc);

            // Prioridad
            gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE;
            panel.add(new JLabel("Prioridad:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
            panel.add(cbxPrioridad, gbc);

            // Fecha límite
            gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.NONE;
            panel.add(new JLabel("Fecha límite (dd/MM/yyyy):"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
            panel.add(txtFechaLimite, gbc);

            // Nota sobre campos obligatorios
            gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
            JLabel lblNota = new JLabel("* Campos obligatorios");
            lblNota.setFont(lblNota.getFont().deriveFont(Font.ITALIC));
            lblNota.setForeground(Color.GRAY);
            panel.add(lblNota, gbc);

            int option = JOptionPane.showConfirmDialog(this, panel, "Editar Tarea", 
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (option != JOptionPane.OK_OPTION) {
                return;
            }

            // Validar campos obligatorios
            if (txtTitulo.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "El título es obligatorio", 
                    "Error de validación", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Guardar estado anterior para verificar cambios
            EstadoTarea estadoAnterior = tarea.getEstado();

            // Actualizar objeto tarea
            tarea.setTitulo(txtTitulo.getText().trim());
            tarea.setDescripcion(txtDescripcion.getText().trim().isEmpty() ? null : txtDescripcion.getText().trim());

            // Actualizar voluntario
            if (cbxVoluntario.getSelectedIndex() > 0) {
                try {
                    String seleccion = (String) cbxVoluntario.getSelectedItem();
                    String[] partes = seleccion.split(":");
                    int idVoluntario = Integer.parseInt(partes[0].trim());
                    Usuario voluntario = usuarioDAO.obtenerPorId(idVoluntario);
                    tarea.setVoluntario(voluntario);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, 
                        "Error al asignar voluntario", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                tarea.setVoluntario(null);
            }

            // Actualizar estado
            EstadoTarea[] estados = {
                EstadoTarea.PENDIENTE, EstadoTarea.EN_PROGRESO, 
                EstadoTarea.COMPLETADA, EstadoTarea.CANCELADA, EstadoTarea.RETRASADA
            };
            EstadoTarea nuevoEstado = estados[cbxEstado.getSelectedIndex()];

            // Si se marca como completada y no estaba completada antes
            if (nuevoEstado == EstadoTarea.COMPLETADA && estadoAnterior != EstadoTarea.COMPLETADA) {
                String comentarios = JOptionPane.showInputDialog(this, 
                    "Comentarios de completado (opcional):", 
                    "Tarea completada", JOptionPane.PLAIN_MESSAGE);
                tarea.setComentariosCompletado(comentarios);
                tarea.setFechaCompletada(new Date());
            }
            tarea.setEstado(nuevoEstado);

            // Actualizar prioridad
            PrioridadTarea[] prioridades = {
                PrioridadTarea.BAJA, PrioridadTarea.MEDIA, 
                PrioridadTarea.ALTA, PrioridadTarea.URGENTE
            };
            tarea.setPrioridad(prioridades[cbxPrioridad.getSelectedIndex()]);

            // Actualizar fecha límite
            if (!txtFechaLimite.getText().trim().isEmpty()) {
                try {
                    Date fechaLimite = sdf.parse(txtFechaLimite.getText().trim());
                    tarea.setFechaLimite(fechaLimite);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, 
                        "Formato de fecha incorrecto. Use dd/MM/yyyy", 
                        "Error de formato", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                tarea.setFechaLimite(null);
            }

            // Guardar cambios
            boolean resultado = tareaDAO.actualizar(tarea);

            if (resultado) {
                JOptionPane.showMessageDialog(this, 
                    "Tarea actualizada correctamente", 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarTareas();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Error al actualizar la tarea", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error inesperado: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void eliminarTarea() {
        int filaSeleccionada = tablaTareas.getSelectedRow();
        if (filaSeleccionada == -1) {
            return;
        }
        
        Integer idTarea = (Integer) tablaTareas.getValueAt(filaSeleccionada, 0);
        String tituloTarea = (String) tablaTareas.getValueAt(filaSeleccionada, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar la tarea '" + tituloTarea + "'?",
            "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean resultado = tareaDAO.eliminar(idTarea);
                
                if (resultado) {
                    JOptionPane.showMessageDialog(this, 
                        "Tarea eliminada correctamente", 
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarTareas();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Error al eliminar la tarea", 
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
    
    private void configurarEventos() {
        // Agregar listener para búsqueda al cambiar filtros
        cbxFiltroEstado.addActionListener(e -> buscarTareas());
        cbxFiltroVoluntario.addActionListener(e -> buscarTareas());

        // Agregar listener para búsqueda al presionar Enter en el campo de texto
        txtBuscar.addActionListener(e -> buscarTareas());

        // Agregar listener para limpiar búsqueda
        txtBuscar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (txtBuscar.getText().trim().isEmpty() && 
                    cbxFiltroEstado.getSelectedIndex() == 0 && 
                    cbxFiltroVoluntario.getSelectedIndex() == 0) {
                    cargarTareas();
                }
            }
        });
    }

    private JButton crearBotonLimpiarFiltros() {
        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.addActionListener(e -> {
            cbxFiltroEstado.setSelectedIndex(0);
            cbxFiltroVoluntario.setSelectedIndex(0);
            txtBuscar.setText("");
            cargarTareas();
        });
        return btnLimpiar;
    }
}