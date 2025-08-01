package sistema.panels;

import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import sistema.dao.AnimalDAO;
import sistema.dao.HistorialMedicoDAO;
import sistema.modelos.Animal;
import sistema.modelos.HistorialMedico;
import static sistema.modelos.HistorialMedico.EstadoTratamiento.COMPLETADO;
import static sistema.modelos.HistorialMedico.EstadoTratamiento.EN_CURSO;
import static sistema.modelos.HistorialMedico.EstadoTratamiento.PENDIENTE;
import sistema.modelos.HistorialMedico.TipoConsulta;
import sistema.modelos.Usuario;

/**
 * Panel para gestión de historial médico de los animales
 * Para: Administrador, Veterinario
 */
public class HistorialMedicoPanel extends JPanel {
    
    private JTable tablaHistorial;
    private DefaultTableModel modeloTabla;
    private JComboBox<String> cbxFiltroAnimal;
    private JComboBox<String> cbxFiltroTipo;
    private JTextField txtBuscar;
    private JButton btnBuscar, btnNuevo, btnVer, btnEditar;
    
    private HistorialMedicoDAO historialDAO;
    private AnimalDAO animalDAO;
    private Usuario usuarioActual;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    
    public HistorialMedicoPanel(Usuario usuario) {
        this.usuarioActual = usuario;
        this.historialDAO = new HistorialMedicoDAO();
        this.animalDAO = new AnimalDAO();
        
        initComponents();
        cargarHistorial();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Panel superior con título y filtros
        // Panel superior con título y filtros
        JPanel panelSuperior = new JPanel(new BorderLayout(10, 10));

        JLabel lblTitulo = new JLabel("Historial Médico");
        lblTitulo.setFont(new Font("Dialog", Font.BOLD, 18));
        panelSuperior.add(lblTitulo, BorderLayout.NORTH);

        // Panel de filtros mejorado
        JPanel panelFiltros = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Primera fila de filtros
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        panelFiltros.add(new JLabel("Animal:"), gbc);

        gbc.gridx = 1;
        cbxFiltroAnimal = new JComboBox<>();
        cbxFiltroAnimal.setPreferredSize(new Dimension(150, 25));
        cbxFiltroAnimal.addItem("Todos los animales");
        cargarAnimales();
        // Agregar listener para filtrado automático
        cbxFiltroAnimal.addActionListener(e -> aplicarFiltros());
        panelFiltros.add(cbxFiltroAnimal, gbc);

        gbc.gridx = 2;
        panelFiltros.add(new JLabel("Tipo:"), gbc);

        gbc.gridx = 3;
        cbxFiltroTipo = new JComboBox<>();
        cbxFiltroTipo.setPreferredSize(new Dimension(130, 25));
        cbxFiltroTipo.addItem("Todos los tipos");
        for (TipoConsulta tipo : TipoConsulta.values()) {
            cbxFiltroTipo.addItem(formatearTipoConsulta(tipo));
        }
        // Agregar listener para filtrado automático
        cbxFiltroTipo.addActionListener(e -> aplicarFiltros());
        panelFiltros.add(cbxFiltroTipo, gbc);

        // Segunda fila - búsqueda por texto
        gbc.gridx = 0; gbc.gridy = 1;
        panelFiltros.add(new JLabel("Buscar:"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtBuscar = new JTextField(20);
        txtBuscar.setToolTipText("Buscar en diagnóstico, tratamiento, síntomas o veterinario");
        // Agregar listener para búsqueda en tiempo real
        txtBuscar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { aplicarFiltros(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { aplicarFiltros(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { aplicarFiltros(); }
        });
        panelFiltros.add(txtBuscar, gbc);

        gbc.gridx = 3; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        btnBuscar = new JButton("Limpiar");
        btnBuscar.addActionListener(e -> limpiarFiltros());
        panelFiltros.add(btnBuscar, gbc);

        panelSuperior.add(panelFiltros, BorderLayout.CENTER);
        add(panelSuperior, BorderLayout.NORTH);
        
        // Tabla de historiales médicos
        String[] columnas = {"ID", "Animal", "Tipo", "Fecha", "Diagnóstico", "Próxima Revisión", "Veterinario"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaHistorial = new JTable(modeloTabla);
        tablaHistorial.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaHistorial.setRowHeight(25);
        tablaHistorial.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scrollPane = new JScrollPane(tablaHistorial);
        add(scrollPane, BorderLayout.CENTER);
        
        // Panel inferior con botones de acción
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        btnNuevo = new JButton("Nuevo Registro");
        btnNuevo.addActionListener(e -> nuevoHistorial());
        
        btnVer = new JButton("Ver Detalle");
        btnVer.setEnabled(false);
        btnVer.addActionListener(e -> verDetalleHistorial());
        
        btnEditar = new JButton("Editar");
        btnEditar.setEnabled(false);
        btnEditar.addActionListener(e -> editarHistorial());
        
        panelBotones.add(btnNuevo);
        panelBotones.add(btnVer);
        
        // Solo veterinarios pueden editar historiales médicos
        if (usuarioActual.getRol() == Usuario.RolUsuario.Veterinario || 
            usuarioActual.getRol() == Usuario.RolUsuario.Administrador) {
            panelBotones.add(btnEditar);
        }
        
        add(panelBotones, BorderLayout.SOUTH);
        
        // Listener para habilitar/deshabilitar botones según selección
        tablaHistorial.getSelectionModel().addListSelectionListener(e -> {
            boolean filaSeleccionada = tablaHistorial.getSelectedRow() != -1;
            btnVer.setEnabled(filaSeleccionada);
            
            if (usuarioActual.getRol() == Usuario.RolUsuario.Veterinario || 
                usuarioActual.getRol() == Usuario.RolUsuario.Administrador) {
                btnEditar.setEnabled(filaSeleccionada);
            }
        });
    }
    
    private void cargarAnimales() {
        try {
            List<Animal> animales = animalDAO.obtenerTodos();

            for (Animal animal : animales) {
                String item = animal.getIdAnimal() + ": " + animal.getNombre() + 
                             " (" + animal.getEspecie() + ")";
                cbxFiltroAnimal.addItem(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error al cargar la lista de animales: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void aplicarFiltros() {
        // Limpiar tabla
        modeloTabla.setRowCount(0);

        // Obtener criterios de filtrado
        String filtroAnimal = cbxFiltroAnimal.getSelectedIndex() > 0 ? 
                              (String) cbxFiltroAnimal.getSelectedItem() : null;

        String filtroTipo = cbxFiltroTipo.getSelectedIndex() > 0 ? 
                           (String) cbxFiltroTipo.getSelectedItem() : null;

        String textoBusqueda = txtBuscar.getText().trim().toLowerCase();

        try {
            List<HistorialMedico> historiales = historialDAO.obtenerTodos();

            // Filtrar resultados según criterios
            List<HistorialMedico> resultadosFiltrados = historiales.stream()
                .filter(h -> {
                    // Filtrar por animal
                    if (filtroAnimal != null && !filtroAnimal.equals("Todos los animales")) {
                        try {
                            int idAnimal = Integer.parseInt(filtroAnimal.substring(0, filtroAnimal.indexOf(':')));
                            if (h.getAnimal() == null || h.getAnimal().getIdAnimal() != idAnimal) {
                                return false;
                            }
                        } catch (Exception e) {
                            return false; // Si hay error parseando el ID, no incluir
                        }
                    }

                    // Filtrar por tipo de consulta
                    if (filtroTipo != null && !filtroTipo.equals("Todos los tipos")) {
                        boolean tipoCoincide = false;
                        for (TipoConsulta tipo : TipoConsulta.values()) {
                            if (formatearTipoConsulta(tipo).equals(filtroTipo)) {
                                if (h.getTipoConsulta() == tipo) {
                                    tipoCoincide = true;
                                }
                                break;
                            }
                        }
                        if (!tipoCoincide) {
                            return false;
                        }
                    }

                    // Filtrar por texto de búsqueda (más completo)
                    if (!textoBusqueda.isEmpty()) {
                        boolean contieneBusqueda = false;

                        // Buscar en diagnóstico
                        if (h.getDiagnostico() != null && 
                            h.getDiagnostico().toLowerCase().contains(textoBusqueda)) {
                            contieneBusqueda = true;
                        }

                        // Buscar en tratamiento
                        if (!contieneBusqueda && h.getTratamiento() != null && 
                            h.getTratamiento().toLowerCase().contains(textoBusqueda)) {
                            contieneBusqueda = true;
                        }

                        // Buscar en síntomas
                        if (!contieneBusqueda && h.getSintomas() != null && 
                            h.getSintomas().toLowerCase().contains(textoBusqueda)) {
                            contieneBusqueda = true;
                        }

                        // Buscar en nombre del veterinario
                        if (!contieneBusqueda && h.getVeterinario() != null && 
                            h.getVeterinario().getNombre() != null &&
                            h.getVeterinario().getNombre().toLowerCase().contains(textoBusqueda)) {
                            contieneBusqueda = true;
                        }

                        // Buscar en nombre del animal
                        if (!contieneBusqueda && h.getAnimal() != null && 
                            h.getAnimal().getNombre() != null &&
                            h.getAnimal().getNombre().toLowerCase().contains(textoBusqueda)) {
                            contieneBusqueda = true;
                        }

                        if (!contieneBusqueda) {
                            return false;
                        }
                    }

                    return true;
                })
                // Ordenar por fecha (más recientes primero)
                .sorted((h1, h2) -> {
                    if (h1.getFecha() == null && h2.getFecha() == null) return 0;
                    if (h1.getFecha() == null) return 1;
                    if (h2.getFecha() == null) return -1;
                    return h2.getFecha().compareTo(h1.getFecha());
                })
                .collect(java.util.stream.Collectors.toList());

            // Mostrar resultados
            for (HistorialMedico historial : resultadosFiltrados) {
                modeloTabla.addRow(new Object[]{
                    historial.getIdHistorial(),
                    historial.getAnimal() != null ? historial.getAnimal().getNombre() : "N/D",
                    formatearTipoConsulta(historial.getTipoConsulta()),
                    historial.getFecha() != null ? sdf.format(historial.getFecha()) : "N/D",
                    historial.getDiagnostico() != null ? 
                        (historial.getDiagnostico().length() > 30 ? 
                            historial.getDiagnostico().substring(0, 30) + "..." : 
                            historial.getDiagnostico()) : 
                        "N/D",
                    historial.getProximaRevision() != null ? sdf.format(historial.getProximaRevision()) : "N/D",
                    historial.getVeterinario() != null ? historial.getVeterinario().getNombre() : "N/D"
                });
            }

            // Actualizar título con número de resultados
            actualizarTituloConResultados(resultadosFiltrados.size(), historiales.size());

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error al aplicar filtros: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método para limpiar todos los filtros
    private void limpiarFiltros() {
        cbxFiltroAnimal.setSelectedIndex(0);
        cbxFiltroTipo.setSelectedIndex(0);
        txtBuscar.setText("");
        // Los listeners automáticos se encargarán de actualizar la tabla
    }

    // Método para actualizar el título con información de resultados
    private void actualizarTituloConResultados(int mostrados, int total) {
        JLabel lblTitulo = null;

        // Buscar el label del título en el panel superior
        Component[] components = ((JPanel) getComponent(0)).getComponents();
        for (Component comp : components) {
            if (comp instanceof JLabel && ((JLabel) comp).getFont().getSize() == 18) {
                lblTitulo = (JLabel) comp;
                break;
            }
        }

        if (lblTitulo != null) {
            if (mostrados == total) {
                lblTitulo.setText("Historial Médico (" + total + " registros)");
            } else {
                lblTitulo.setText("Historial Médico (" + mostrados + " de " + total + " registros)");
            }
        }
    }

    // Simplificar el método buscarHistorial (ya no es necesario, pero lo mantenemos por compatibilidad)
    private void buscarHistorial() {
        aplicarFiltros();
    }

    // Actualizar el método cargarHistorial para usar el nuevo sistema
    private void cargarHistorial() {
        // Limpiar filtros primero
        if (cbxFiltroAnimal != null) cbxFiltroAnimal.setSelectedIndex(0);
        if (cbxFiltroTipo != null) cbxFiltroTipo.setSelectedIndex(0);
        if (txtBuscar != null) txtBuscar.setText("");

        // Aplicar filtros (que ahora cargarán todos los datos)
        if (modeloTabla != null) {
            aplicarFiltros();
        }
    }
    
    private String formatearTipoConsulta(TipoConsulta tipo) {
        if (tipo == null) return "N/D";
        switch (tipo) {
            case REVISION_GENERAL: return "Revisión General";
            case VACUNACION: return "Vacunación";
            case DESPARASITACION: return "Desparasitación";
            case TRATAMIENTO: return "Tratamiento";
            case CIRUGIA: return "Cirugía";
            case EMERGENCIA: return "Emergencia";
            default: return "Otro";
        }
    }
    
    private void nuevoHistorial() {
        if (usuarioActual.getRol() != Usuario.RolUsuario.Veterinario && 
            usuarioActual.getRol() != Usuario.RolUsuario.Administrador) {
            JOptionPane.showMessageDialog(this, 
                "Solo los veterinarios pueden crear registros médicos", 
                "Acceso denegado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Seleccionar animal primero
        try {
            List<Animal> animales = animalDAO.obtenerTodos();
            
            if (animales.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No hay animales registrados en el sistema", 
                    "Información", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            JComboBox<String> cbxAnimales = new JComboBox<>();
            for (Animal animal : animales) {
                cbxAnimales.addItem(animal.getIdAnimal() + ": " + animal.getNombre() + 
                    " (" + animal.getEspecie() + ")");
            }
            
            Object[] mensaje = {
                "Seleccione un animal para el registro médico:",
                cbxAnimales
            };
            
            int option = JOptionPane.showConfirmDialog(this, mensaje, "Seleccionar Animal", 
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
            if (option != JOptionPane.OK_OPTION) {
                return;
            }
            
            // Obtener animal seleccionado
            String seleccion = (String) cbxAnimales.getSelectedItem();
            int idAnimal = Integer.parseInt(seleccion.substring(0, seleccion.indexOf(':')));
            Animal animalSeleccionado = animalDAO.obtenerPorId(idAnimal);
            
            if (animalSeleccionado == null) {
                JOptionPane.showMessageDialog(this, 
                    "Error al obtener el animal seleccionado", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Formulario para nuevo registro médico
            JComboBox<String> cbxTipo = new JComboBox<>();
            for (TipoConsulta tipo : TipoConsulta.values()) {
                cbxTipo.addItem(formatearTipoConsulta(tipo));
            }
            
            // Agregar ComboBox para estado de tratamiento
            JComboBox<Object> cbxEstadoTratamiento = new JComboBox<>();
            cbxEstadoTratamiento.addItem(EN_CURSO);
            cbxEstadoTratamiento.addItem(COMPLETADO);
            cbxEstadoTratamiento.addItem(PENDIENTE);
            cbxEstadoTratamiento.setSelectedIndex(0); // Por defecto "EN_CURSO"

            
            JTextField txtPeso = new JTextField();
            JTextField txtTemperatura = new JTextField();
            JTextArea txtSintomas = new JTextArea(3, 20);
            JTextArea txtDiagnostico = new JTextArea(3, 20);
            JTextArea txtTratamiento = new JTextArea(3, 20);
            JTextField txtProximaRevision = new JTextField(new SimpleDateFormat("dd/MM/yyyy").format(
                new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000))); // Una semana después
            
            // Configurar áreas de texto
            txtSintomas.setLineWrap(true);
            txtDiagnostico.setLineWrap(true);
            txtTratamiento.setLineWrap(true);
            
            JScrollPane scrollSintomas = new JScrollPane(txtSintomas);
            JScrollPane scrollDiagnostico = new JScrollPane(txtDiagnostico);
            JScrollPane scrollTratamiento = new JScrollPane(txtTratamiento);
            
            Object[] formulario = {
                "Animal: " + animalSeleccionado.getNombre(),
                "",
                "Tipo de consulta:", cbxTipo,
                "Estado de tratamiento:", cbxEstadoTratamiento,
                "Peso (kg):", txtPeso,
                "Temperatura (°C):", txtTemperatura,
                "Síntomas:", scrollSintomas,
                "Diagnóstico:", scrollDiagnostico,
                "Tratamiento:", scrollTratamiento,
                "Próxima revisión (dd/mm/yyyy):", txtProximaRevision
            };
            
            option = JOptionPane.showConfirmDialog(this, formulario, "Nuevo Registro Médico", 
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
            if (option != JOptionPane.OK_OPTION) {
                return;
            }
            
            // Validar campos obligatorios
            if (txtDiagnostico.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "El diagnóstico es obligatorio", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Crear objeto de historial médico
            HistorialMedico historial = new HistorialMedico();
            historial.setAnimal(animalSeleccionado);
            historial.setFecha(new Date());
            
            // Tipo de consulta
            String tipoTexto = (String) cbxTipo.getSelectedItem();
            for (TipoConsulta tipo : TipoConsulta.values()) {
                if (formatearTipoConsulta(tipo).equals(tipoTexto)) {
                    historial.setTipoConsulta(tipo);
                    break;
                }
            }
            
            // Datos numéricos
            try {
                if (!txtPeso.getText().trim().isEmpty()) {
                    historial.setPeso(Double.parseDouble(txtPeso.getText().trim()));
                }
                
                if (!txtTemperatura.getText().trim().isEmpty()) {
                    historial.setTemperatura(Double.parseDouble(txtTemperatura.getText().trim()));
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, 
                    "Los valores de peso y temperatura deben ser numéricos", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            historial.setSintomas(txtSintomas.getText().trim());
            historial.setDiagnostico(txtDiagnostico.getText().trim());
            historial.setTratamiento(txtTratamiento.getText().trim());
            historial.setEstadoTratamiento((HistorialMedico.EstadoTratamiento) cbxEstadoTratamiento.getSelectedItem());
            
            // Próxima revisión
            try {
                if (!txtProximaRevision.getText().trim().isEmpty()) {
                    historial.setProximaRevision(new SimpleDateFormat("dd/MM/yyyy").parse(
                        txtProximaRevision.getText().trim()));
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Formato de fecha incorrecto. Use dd/mm/yyyy", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            historial.setVeterinario(usuarioActual);
            
            // Si el animal estaba disponible, cambiarlo a "En Tratamiento"
            if (animalSeleccionado.getCondicionSalud() == Animal.CondicionSalud.SALUDABLE) {
                animalSeleccionado.setCondicionSalud(Animal.CondicionSalud.EN_TRATAMIENTO);
                animalDAO.actualizar(animalSeleccionado);
            }
            
            // Guardar historial
            boolean resultado = historialDAO.guardar(historial);
            
            if (resultado) {
                JOptionPane.showMessageDialog(this, 
                    "Registro médico creado correctamente", 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarHistorial();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Error al crear el registro médico", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void verDetalleHistorial() {
        int filaSeleccionada = tablaHistorial.getSelectedRow();
        if (filaSeleccionada == -1) {
            return;
        }
        
        Integer idHistorial = (Integer) tablaHistorial.getValueAt(filaSeleccionada, 0);
        
        try {
            HistorialMedico historial = historialDAO.obtenerPorId(idHistorial);
            
            if (historial == null) {
                JOptionPane.showMessageDialog(this, 
                    "No se pudo obtener la información del registro médico", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Crear panel con información detallada
            JPanel panel = new JPanel(new BorderLayout(10, 10));
            panel.setBorder(new EmptyBorder(10, 10, 10, 10));
            
            // Panel de información básica
            JPanel panelInfo = new JPanel(new GridLayout(0, 2, 10, 5));
            
            panelInfo.add(new JLabel("ID:"));
            panelInfo.add(new JLabel(String.valueOf(historial.getIdHistorial())));
            
            panelInfo.add(new JLabel("Animal:"));
            panelInfo.add(new JLabel(historial.getAnimal() != null ? 
                historial.getAnimal().getNombre() + " (" + historial.getAnimal().getEspecie() + ")" : "N/D"));
            
            panelInfo.add(new JLabel("Tipo de consulta:"));
            panelInfo.add(new JLabel(formatearTipoConsulta(historial.getTipoConsulta())));
            
            panelInfo.add(new JLabel("Fecha:"));
            panelInfo.add(new JLabel(historial.getFecha() != null ? 
                new SimpleDateFormat("dd/MM/yyyy HH:mm").format(historial.getFecha()) : "N/D"));
            
            panelInfo.add(new JLabel("Veterinario:"));
            panelInfo.add(new JLabel(historial.getVeterinario() != null ? 
                historial.getVeterinario().getNombre() : "N/D"));
            
            panelInfo.add(new JLabel("Peso:"));
            panelInfo.add(new JLabel(historial.getPeso() != null ? 
                historial.getPeso() + " kg" : "N/D"));
            
            panelInfo.add(new JLabel("Temperatura:"));
            panelInfo.add(new JLabel(historial.getTemperatura() != null ? 
                historial.getTemperatura() + " °C" : "N/D"));
            
            panelInfo.add(new JLabel("Próxima revisión:"));
            panelInfo.add(new JLabel(historial.getProximaRevision() != null ? 
                sdf.format(historial.getProximaRevision()) : "N/D"));
            
            // Panel para las descripciones
            JPanel panelDesc = new JPanel(new GridLayout(3, 1, 0, 10));
            panelDesc.setBorder(new EmptyBorder(10, 0, 0, 0));
            
            if (historial.getSintomas() != null && !historial.getSintomas().isEmpty()) {
                JPanel panelSintomas = new JPanel(new BorderLayout());
                panelSintomas.setBorder(BorderFactory.createTitledBorder("Síntomas"));
                
                JTextArea txtSintomas = new JTextArea(historial.getSintomas());
                txtSintomas.setEditable(false);
                txtSintomas.setLineWrap(true);
                txtSintomas.setWrapStyleWord(true);
                txtSintomas.setRows(3);
                
                panelSintomas.add(new JScrollPane(txtSintomas), BorderLayout.CENTER);
                panelDesc.add(panelSintomas);
            }
            
            if (historial.getDiagnostico() != null && !historial.getDiagnostico().isEmpty()) {
                JPanel panelDiagnostico = new JPanel(new BorderLayout());
                panelDiagnostico.setBorder(BorderFactory.createTitledBorder("Diagnóstico"));
                
                JTextArea txtDiagnostico = new JTextArea(historial.getDiagnostico());
                txtDiagnostico.setEditable(false);
                txtDiagnostico.setLineWrap(true);
                txtDiagnostico.setWrapStyleWord(true);
                txtDiagnostico.setRows(3);
                
                panelDiagnostico.add(new JScrollPane(txtDiagnostico), BorderLayout.CENTER);
                panelDesc.add(panelDiagnostico);
            }
            
            if (historial.getTratamiento() != null && !historial.getTratamiento().isEmpty()) {
                JPanel panelTratamiento = new JPanel(new BorderLayout());
                panelTratamiento.setBorder(BorderFactory.createTitledBorder("Tratamiento"));
                
                JTextArea txtTratamiento = new JTextArea(historial.getTratamiento());
                txtTratamiento.setEditable(false);
                txtTratamiento.setLineWrap(true);
                txtTratamiento.setWrapStyleWord(true);
                txtTratamiento.setRows(3);
                
                panelTratamiento.add(new JScrollPane(txtTratamiento), BorderLayout.CENTER);
                panelDesc.add(panelTratamiento);
            }
            
            // Unir paneles
            panel.add(panelInfo, BorderLayout.NORTH);
            panel.add(panelDesc, BorderLayout.CENTER);
            
            // Mostrar panel en un diálogo
            JOptionPane.showMessageDialog(this, panel, "Detalle de Registro Médico", JOptionPane.PLAIN_MESSAGE);
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error al obtener los detalles del registro médico: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void editarHistorial() {
        if (usuarioActual.getRol() != Usuario.RolUsuario.Veterinario && 
            usuarioActual.getRol() != Usuario.RolUsuario.Administrador) {
            JOptionPane.showMessageDialog(this, 
                "Solo los veterinarios pueden editar registros médicos", 
                "Acceso denegado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int filaSeleccionada = tablaHistorial.getSelectedRow();
        if (filaSeleccionada == -1) {
            return;
        }
        
        Integer idHistorial = (Integer) tablaHistorial.getValueAt(filaSeleccionada, 0);
        
        try {
            HistorialMedico historial = historialDAO.obtenerPorId(idHistorial);
            
            if (historial == null) {
                JOptionPane.showMessageDialog(this, 
                    "No se pudo obtener la información del registro médico", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Formulario para editar registro médico
            JComboBox<String> cbxTipo = new JComboBox<>();
            for (TipoConsulta tipo : TipoConsulta.values()) {
                cbxTipo.addItem(formatearTipoConsulta(tipo));
                if (historial.getTipoConsulta() == tipo) {
                    cbxTipo.setSelectedIndex(cbxTipo.getItemCount() - 1);
                }
            }
            
            // Agregar ComboBox para estado de tratamiento
            JComboBox<Object> cbxEstadoTratamiento = new JComboBox<>();
            cbxEstadoTratamiento.addItem(EN_CURSO);
            cbxEstadoTratamiento.addItem(COMPLETADO);
            cbxEstadoTratamiento.addItem(PENDIENTE);
            if (historial.getEstadoTratamiento() != null) {
                cbxEstadoTratamiento.setSelectedItem(historial.getEstadoTratamiento());
            }
            
            JTextField txtPeso = new JTextField(historial.getPeso() != null ? 
                historial.getPeso().toString() : "");
            
            JTextField txtTemperatura = new JTextField(historial.getTemperatura() != null ? 
                historial.getTemperatura().toString() : "");
            
            JTextArea txtSintomas = new JTextArea(3, 20);
            txtSintomas.setText(historial.getSintomas() != null ? historial.getSintomas() : "");
            
            JTextArea txtDiagnostico = new JTextArea(3, 20);
            txtDiagnostico.setText(historial.getDiagnostico() != null ? historial.getDiagnostico() : "");
            
            JTextArea txtTratamiento = new JTextArea(3, 20);
            txtTratamiento.setText(historial.getTratamiento() != null ? historial.getTratamiento() : "");
            
            JTextField txtProximaRevision = new JTextField(historial.getProximaRevision() != null ? 
                sdf.format(historial.getProximaRevision()) : "");
            
            // Configurar áreas de texto
            txtSintomas.setLineWrap(true);
            txtDiagnostico.setLineWrap(true);
            txtTratamiento.setLineWrap(true);
            
            JScrollPane scrollSintomas = new JScrollPane(txtSintomas);
            JScrollPane scrollDiagnostico = new JScrollPane(txtDiagnostico);
            JScrollPane scrollTratamiento = new JScrollPane(txtTratamiento);
            
            Object[] formulario = {
                "Animal: " + (historial.getAnimal() != null ? historial.getAnimal().getNombre() : "N/D"),
                "",
                "Tipo de consulta:", cbxTipo,
                "Estado de tratamiento:", cbxEstadoTratamiento,
                "Peso (kg):", txtPeso,
                "Temperatura (°C):", txtTemperatura,
                "Síntomas:", scrollSintomas,
                "Diagnóstico:", scrollDiagnostico,
                "Tratamiento:", scrollTratamiento,
                "Próxima revisión (dd/mm/yyyy):", txtProximaRevision
            };
            
            int option = JOptionPane.showConfirmDialog(this, formulario, "Editar Registro Médico", 
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
            if (option != JOptionPane.OK_OPTION) {
                return;
            }
            
            // Validar campos obligatorios
            if (txtDiagnostico.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "El diagnóstico es obligatorio", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Actualizar objeto de historial médico
            
            // Tipo de consulta
            String tipoTexto = (String) cbxTipo.getSelectedItem();
            for (TipoConsulta tipo : TipoConsulta.values()) {
                if (formatearTipoConsulta(tipo).equals(tipoTexto)) {
                    historial.setTipoConsulta(tipo);
                    break;
                }
            }
            
            // Datos numéricos
            try {
                if (!txtPeso.getText().trim().isEmpty()) {
                    historial.setPeso(Double.parseDouble(txtPeso.getText().trim()));
                } else {
                    historial.setPeso(null);
                }
                
                if (!txtTemperatura.getText().trim().isEmpty()) {
                    historial.setTemperatura(Double.parseDouble(txtTemperatura.getText().trim()));
                } else {
                    historial.setTemperatura(null);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, 
                    "Los valores de peso y temperatura deben ser numéricos", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            historial.setSintomas(txtSintomas.getText().trim());
            historial.setDiagnostico(txtDiagnostico.getText().trim());
            historial.setTratamiento(txtTratamiento.getText().trim());
            historial.setEstadoTratamiento((HistorialMedico.EstadoTratamiento) cbxEstadoTratamiento.getSelectedItem());
            
            // Próxima revisión
            try {
                if (!txtProximaRevision.getText().trim().isEmpty()) {
                    historial.setProximaRevision(new SimpleDateFormat("dd/MM/yyyy").parse(
                        txtProximaRevision.getText().trim()));
                } else {
                    historial.setProximaRevision(null);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Formato de fecha incorrecto. Use dd/mm/yyyy", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Guardar cambios
            boolean resultado = historialDAO.actualizar(historial);
            
            if (resultado) {
                JOptionPane.showMessageDialog(this, 
                    "Registro médico actualizado correctamente", 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarHistorial();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Error al actualizar el registro médico", 
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