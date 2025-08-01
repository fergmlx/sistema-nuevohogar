package sistema.panels;

import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import sistema.dao.EventoDAO;
import sistema.modelos.Evento;
import sistema.modelos.Usuario;

/**
 * Panel para la gestión de eventos
 * Para: Rol Voluntario
 */
public class EventosPanel extends JPanel {
    
    private JTable tablaEventos;
    private DefaultTableModel modeloTabla;
    private JComboBox<String> cbxFiltroTipo;
    private JTextField txtBuscar;
    private JButton btnBuscar, btnVer, btnInscribirse, btnCancelar;
    
    private EventoDAO eventoDAO;
    private Usuario usuarioActual;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    
    public EventosPanel(Usuario usuario) {
        this.usuarioActual = usuario;
        this.eventoDAO = new EventoDAO();
        
        initComponents();
        cargarEventos();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Panel superior con título y filtros
        JPanel panelSuperior = new JPanel(new BorderLayout(10, 10));
        
        JLabel lblTitulo = new JLabel("Eventos");
        lblTitulo.setFont(new Font("Dialog", Font.BOLD, 18));
        panelSuperior.add(lblTitulo, BorderLayout.NORTH);
        
        // Panel de filtros
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        
        JLabel lblFiltroTipo = new JLabel("Tipo:");
        cbxFiltroTipo = new JComboBox<>();
        cbxFiltroTipo.addItem("Todos");
        for (Evento.TipoEvento tipo : Evento.TipoEvento.values()) {
            cbxFiltroTipo.addItem(formatearTipoEvento(tipo));
        }
        
        txtBuscar = new JTextField(15);
        
        btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscarEventos());
        
        panelFiltros.add(lblFiltroTipo);
        panelFiltros.add(cbxFiltroTipo);
        panelFiltros.add(new JLabel("Buscar:"));
        panelFiltros.add(txtBuscar);
        panelFiltros.add(btnBuscar);
        
        panelSuperior.add(panelFiltros, BorderLayout.CENTER);
        
        add(panelSuperior, BorderLayout.NORTH);
        
        // Tabla de eventos
        String[] columnas = {"ID", "Título", "Tipo", "Fecha Inicio", "Ubicación", "Inscrito"};
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
        
        tablaEventos = new JTable(modeloTabla);
        tablaEventos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaEventos.setRowHeight(25);
        tablaEventos.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scrollPane = new JScrollPane(tablaEventos);
        add(scrollPane, BorderLayout.CENTER);
        
        // Panel inferior con botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        btnVer = new JButton("Ver Detalle");
        btnVer.setEnabled(false);
        btnVer.addActionListener(e -> verDetalleEvento());
        
        btnInscribirse = new JButton("Inscribirse");
        btnInscribirse.setEnabled(false);
        btnInscribirse.addActionListener(e -> inscribirseEvento());
        
        btnCancelar = new JButton("Cancelar Inscripción");
        btnCancelar.setEnabled(false);
        btnCancelar.addActionListener(e -> cancelarInscripcion());
        
        panelBotones.add(btnVer);
        panelBotones.add(btnInscribirse);
        panelBotones.add(btnCancelar);
        
        add(panelBotones, BorderLayout.SOUTH);
        
        // Listener para habilitar/deshabilitar botones según selección
        tablaEventos.getSelectionModel().addListSelectionListener(e -> {
            boolean filaSeleccionada = tablaEventos.getSelectedRow() != -1;
            btnVer.setEnabled(filaSeleccionada);
            
            if (filaSeleccionada) {
                Boolean inscrito = (Boolean) tablaEventos.getValueAt(tablaEventos.getSelectedRow(), 5);
                
                btnInscribirse.setEnabled(!inscrito);
                btnCancelar.setEnabled(inscrito);
            } else {
                btnInscribirse.setEnabled(false);
                btnCancelar.setEnabled(false);
            }
        });
    }
    
    private void cargarEventos() {
        // Limpiar tabla
        modeloTabla.setRowCount(0);
        
        try {
            List<Evento> eventos = eventoDAO.obtenerEventosFuturos();
            
            for (Evento evento : eventos) {
                // Verificar si el usuario está inscrito
                boolean inscrito = evento.getParticipantes() != null && 
                                  evento.getParticipantes().contains(usuarioActual);
                
                modeloTabla.addRow(new Object[]{
                    evento.getIdEvento(),
                    evento.getTitulo(),
                    formatearTipoEvento(evento.getTipoEvento()),
                    evento.getFechaInicio() != null ? sdf.format(evento.getFechaInicio()) : "N/D",
                    evento.getUbicacion() != null ? evento.getUbicacion() : "N/D",
                    inscrito
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error al cargar los eventos: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void buscarEventos() {
        // Limpiar tabla
        modeloTabla.setRowCount(0);
        
        // Obtener criterios de filtrado
        String filtroTipo = cbxFiltroTipo.getSelectedIndex() > 0 ? 
                           (String) cbxFiltroTipo.getSelectedItem() : null;
                                 
        String textoBusqueda = txtBuscar.getText().trim();
        
        try {
            // Obtener eventos futuros
            List<Evento> eventos = eventoDAO.obtenerEventosFuturos();
            
            // Filtrar resultados según criterios
            List<Evento> resultadosFiltrados = eventos.stream()
                .filter(e -> {
                    // Filtrar por tipo
                    if (filtroTipo != null) {
                        String tipoEvento = formatearTipoEvento(e.getTipoEvento());
                        if (!tipoEvento.equals(filtroTipo)) {
                            return false;
                        }
                    }
                    
                    // Filtrar por texto de búsqueda
                    if (!textoBusqueda.isEmpty()) {
                        return e.getTitulo().toLowerCase().contains(textoBusqueda.toLowerCase()) ||
                              (e.getDescripcion() != null && 
                               e.getDescripcion().toLowerCase().contains(textoBusqueda.toLowerCase())) ||
                              (e.getUbicacion() != null &&
                               e.getUbicacion().toLowerCase().contains(textoBusqueda.toLowerCase()));
                    }
                    
                    return true;
                })
                .collect(java.util.stream.Collectors.toList());
            
            // Mostrar resultados
            for (Evento evento : resultadosFiltrados) {
                // Verificar si el usuario está inscrito
                boolean inscrito = evento.getParticipantes() != null && 
                                  evento.getParticipantes().contains(usuarioActual);
                
                modeloTabla.addRow(new Object[]{
                    evento.getIdEvento(),
                    evento.getTitulo(),
                    formatearTipoEvento(evento.getTipoEvento()),
                    evento.getFechaInicio() != null ? sdf.format(evento.getFechaInicio()) : "N/D",
                    evento.getUbicacion() != null ? evento.getUbicacion() : "N/D",
                    inscrito
                });
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error al buscar eventos: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String formatearTipoEvento(Evento.TipoEvento tipo) {
        if (tipo == null) return "N/D";
        switch (tipo) {
            case JORNADA_ADOPCION: return "Jornada de Adopción";
            case CAMPAÑA_VACUNACION: return "Campaña de Vacunación";
            case CAPACITACION: return "Capacitación";
            case RECAUDACION: return "Evento de Recaudación";
            case VOLUNTARIADO: return "Actividad de Voluntariado";
            case OTRO: return "Otro";
            default: return tipo.toString();
        }
    }
    
    private void verDetalleEvento() {
        int filaSeleccionada = tablaEventos.getSelectedRow();
        if (filaSeleccionada == -1) {
            return;
        }
        
        Integer idEvento = (Integer) tablaEventos.getValueAt(filaSeleccionada, 0);
        
        try {
            Evento evento = eventoDAO.obtenerPorId(idEvento);
            
            if (evento == null) {
                JOptionPane.showMessageDialog(this, 
                    "No se pudo obtener la información del evento", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Crear panel con información detallada
            JPanel panel = new JPanel(new BorderLayout(10, 10));
            panel.setBorder(new EmptyBorder(10, 10, 10, 10));
            
            // Panel de información básica
            JPanel panelInfo = new JPanel(new GridLayout(0, 2, 10, 5));
            
            panelInfo.add(new JLabel("Título:"));
            panelInfo.add(new JLabel(evento.getTitulo()));
            
            panelInfo.add(new JLabel("Tipo:"));
            panelInfo.add(new JLabel(formatearTipoEvento(evento.getTipoEvento())));
            
            panelInfo.add(new JLabel("Fecha inicio:"));
            panelInfo.add(new JLabel(evento.getFechaInicio() != null ? 
                sdf.format(evento.getFechaInicio()) : "N/D"));
            
            panelInfo.add(new JLabel("Fecha fin:"));
            panelInfo.add(new JLabel(evento.getFechaFin() != null ? 
                sdf.format(evento.getFechaFin()) : "N/D"));
            
            panelInfo.add(new JLabel("Ubicación:"));
            panelInfo.add(new JLabel(evento.getUbicacion() != null ? 
                evento.getUbicacion() : "N/D"));
            
            int inscritos = evento.getParticipantes() != null ? evento.getParticipantes().size() : 0;
            panelInfo.add(new JLabel("Participantes inscritos:"));
            panelInfo.add(new JLabel(String.valueOf(inscritos)));
            
            boolean inscrito = evento.getParticipantes() != null && 
                              evento.getParticipantes().contains(usuarioActual);
            panelInfo.add(new JLabel("Estado:"));
            panelInfo.add(new JLabel(inscrito ? "Inscrito" : "No inscrito"));
            
            // Panel para la descripción
            JPanel panelDesc = new JPanel(new BorderLayout());
            panelDesc.setBorder(BorderFactory.createTitledBorder("Descripción"));
            
            JTextArea txtDesc = new JTextArea(evento.getDescripcion() != null ? evento.getDescripcion() : "");
            txtDesc.setEditable(false);
            txtDesc.setLineWrap(true);
            txtDesc.setWrapStyleWord(true);
            txtDesc.setRows(5);
            
            panelDesc.add(new JScrollPane(txtDesc), BorderLayout.CENTER);
            
            panel.add(panelInfo, BorderLayout.NORTH);
            panel.add(panelDesc, BorderLayout.CENTER);
            
            // Botones específicos según si está inscrito o no
            JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            
            if (inscrito) {
                JButton btnCancelar = new JButton("Cancelar Inscripción");
                btnCancelar.addActionListener(e -> {
                    int confirm = JOptionPane.showConfirmDialog(this, 
                        "¿Está seguro que desea cancelar su inscripción a este evento?", 
                        "Confirmar cancelación", JOptionPane.YES_NO_OPTION);
                        
                    if (confirm == JOptionPane.YES_OPTION) {
                        try {
                            boolean resultado = eventoDAO.cancelarInscripcion(evento.getIdEvento(), usuarioActual);
                            
                            if (resultado) {
                                JOptionPane.showMessageDialog(this, 
                                    "Inscripción cancelada correctamente", 
                                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                                cargarEventos();
                                JOptionPane.getRootFrame().dispose(); // Cerrar diálogo
                            } else {
                                JOptionPane.showMessageDialog(this, 
                                    "Error al cancelar la inscripción", 
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            }
                            
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(this, 
                                "Error: " + ex.getMessage(), 
                                "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
                
                panelBotones.add(btnCancelar);
                
            } else {
                
                JButton btnInscribirse = new JButton("Inscribirse");
                
                btnInscribirse.addActionListener(e -> {
                    try {
                        boolean resultado = eventoDAO.inscribirUsuario(evento.getIdEvento(), usuarioActual);
                        
                        if (resultado) {
                            JOptionPane.showMessageDialog(this, 
                                "Inscripción realizada correctamente", 
                                "Éxito", JOptionPane.INFORMATION_MESSAGE);
                            cargarEventos();
                            JOptionPane.getRootFrame().dispose(); // Cerrar diálogo
                        } else {
                            JOptionPane.showMessageDialog(this, 
                                "Error al realizar la inscripción", 
                                "Error", JOptionPane.ERROR_MESSAGE);
                        }
                        
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(this, 
                            "Error: " + ex.getMessage(), 
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });
                
                panelBotones.add(btnInscribirse);
            }
            
            panel.add(panelBotones, BorderLayout.SOUTH);
            
            // Mostrar panel en un diálogo
            JOptionPane.showMessageDialog(this, panel, "Detalle de Evento", JOptionPane.PLAIN_MESSAGE);
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error al obtener los detalles del evento: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void inscribirseEvento() {
        int filaSeleccionada = tablaEventos.getSelectedRow();
        if (filaSeleccionada == -1) {
            return;
        }
        
        Integer idEvento = (Integer) tablaEventos.getValueAt(filaSeleccionada, 0);
        String tituloEvento = (String) tablaEventos.getValueAt(filaSeleccionada, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Desea inscribirse al evento '" + tituloEvento + "'?",
            "Confirmar inscripción", JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean resultado = eventoDAO.inscribirUsuario(idEvento, usuarioActual);
                
                if (resultado) {
                    JOptionPane.showMessageDialog(this, 
                        "Inscripción realizada correctamente", 
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarEventos();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Error al realizar la inscripción", 
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
    
    private void cancelarInscripcion() {
        int filaSeleccionada = tablaEventos.getSelectedRow();
        if (filaSeleccionada == -1) {
            return;
        }
        
        Integer idEvento = (Integer) tablaEventos.getValueAt(filaSeleccionada, 0);
        String tituloEvento = (String) tablaEventos.getValueAt(filaSeleccionada, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Está seguro que desea cancelar su inscripción al evento '" + tituloEvento + "'?",
            "Confirmar cancelación", JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean resultado = eventoDAO.cancelarInscripcion(idEvento, usuarioActual);
                
                if (resultado) {
                    JOptionPane.showMessageDialog(this, 
                        "Inscripción cancelada correctamente", 
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarEventos();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Error al cancelar la inscripción", 
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
}