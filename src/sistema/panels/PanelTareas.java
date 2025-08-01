package sistema.panels;

import sistema.dao.TareaDAO;
import sistema.dao.AnimalDAO;
import sistema.modelos.Tarea;
import sistema.modelos.Animal;
import sistema.modelos.Usuario;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class PanelTareas extends JPanel {
    private Usuario usuarioActual;
    private JPanel contenedorTareas;
    private JPanel kanbanPanel;
    private JPanel detallePanel;
    private Tarea tareaSeleccionada;
    private Map<Tarea.EstadoTarea, JPanel> columnasTareas;
    private JScrollPane scrollKanban;
    private CardLayout detalleCardLayout;
    private JPanel cardPanel;
    private boolean modoDetalle = false;
    private JButton btnCompletarTarea;
    
    // Constantes para el diseño
    private final Color COLOR_PRIMARIO = new Color(79, 70, 229);
    private final Color COLOR_SECUNDARIO = new Color(99, 102, 241);
    private final Color COLOR_TERCIARIO = new Color(129, 140, 248);
    private final Color COLOR_FONDO = new Color(249, 250, 251);
    private final Color COLOR_CARD = Color.WHITE;
    private final Color COLOR_TEXTO = new Color(55, 65, 81);
    private final Color COLOR_TEXTO_SECUNDARIO = new Color(107, 114, 128);
    private final int PADDING_PANEL = 30;
    private final int ESPACIO_COMPONENTES = 20;
    private final int RADIO_ESQUINAS = 12;
    private final int ALTURA_TARJETA = 160;
    private final int ANCHO_COLUMNA = 280;

    public PanelTareas(Usuario usuario) {
        this.usuarioActual = usuario;
        initComponents();
        cargarDatos();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, ESPACIO_COMPONENTES));
        setBorder(new EmptyBorder(PADDING_PANEL, PADDING_PANEL, PADDING_PANEL, PADDING_PANEL));
        setBackground(COLOR_FONDO);

        add(createHeader(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(ESPACIO_COMPONENTES, 0));
        header.setBackground(COLOR_FONDO);
        header.setPreferredSize(new Dimension(0, 120));

        // Panel de título con diseño neomórfico
        JPanel tituloContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fondo principal con gradiente suave
                GradientPaint gradient = new GradientPaint(
                    0, 0, COLOR_PRIMARIO,
                    getWidth(), getHeight(), COLOR_SECUNDARIO
                );
                g2d.setPaint(gradient);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), RADIO_ESQUINAS*2, RADIO_ESQUINAS*2));
                
                // Efecto neomórfico - sombra interior clara en la parte superior izquierda
                g2d.setColor(new Color(255, 255, 255, 60));
                g2d.setStroke(new BasicStroke(2));
                g2d.draw(new RoundRectangle2D.Float(4, 4, getWidth() - 8, getHeight() - 8, RADIO_ESQUINAS*2 - 4, RADIO_ESQUINAS*2 - 4));
                
                // Patrón sutil
                g2d.setColor(new Color(255, 255, 255, 15));
                for (int i = 0; i < getWidth(); i += 20) {
                    g2d.drawLine(i, 0, i + 10, getHeight());
                }
            }
        };
        tituloContainer.setLayout(new BorderLayout());
        tituloContainer.setBorder(new EmptyBorder(25, 35, 25, 35));
        tituloContainer.setPreferredSize(new Dimension(500, 120));
        
        JPanel textoPanel = new JPanel();
        textoPanel.setLayout(new BoxLayout(textoPanel, BoxLayout.Y_AXIS));
        textoPanel.setOpaque(false);
        
        JLabel iconoLabel = new JLabel("📋");
        iconoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        iconoLabel.setForeground(Color.WHITE);
        iconoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel tituloLabel = new JLabel("Mis Tareas");
        tituloLabel.setFont(new Font("Montserrat", Font.BOLD, 28));
        tituloLabel.setForeground(Color.WHITE);
        tituloLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel subtituloLabel = new JLabel("Organiza y gestiona tus actividades");
        subtituloLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtituloLabel.setForeground(new Color(226, 232, 240));
        subtituloLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        textoPanel.add(iconoLabel);
        textoPanel.add(Box.createVerticalStrut(4));
        textoPanel.add(tituloLabel);
        textoPanel.add(Box.createVerticalStrut(5));
        textoPanel.add(subtituloLabel);
        
        tituloContainer.add(textoPanel, BorderLayout.CENTER);

        // Panel de estadísticas
        JPanel statsPanel = createStatsPanel();

        header.add(tituloContainer, BorderLayout.WEST);
        header.add(statsPanel, BorderLayout.CENTER);

        return header;
    }

    private JPanel createStatsPanel() {
        JPanel statsContainer = new JPanel(new GridLayout(1, 3, 15, 0));
        statsContainer.setBackground(COLOR_FONDO);
        
        // Obtener datos para estadísticas
        TareaDAO tareaDAO = new TareaDAO();
        List<Tarea> misTareas = tareaDAO.obtenerPorVoluntario(usuarioActual);
        
        long pendientes = misTareas.stream()
            .filter(t -> t.getEstado() == Tarea.EstadoTarea.PENDIENTE || t.getEstado() == Tarea.EstadoTarea.EN_PROGRESO)
            .count();
            
        long completadas = misTareas.stream()
            .filter(t -> t.getEstado() == Tarea.EstadoTarea.COMPLETADA)
            .count();
            
        long retrasadas = misTareas.stream()
            .filter(t -> t.getEstado() == Tarea.EstadoTarea.RETRASADA || 
                  (t.getEstado() == Tarea.EstadoTarea.PENDIENTE && 
                   t.getFechaLimite() != null && 
                   t.getFechaLimite().before(new Date())))
            .count();
        
        // Crear tarjetas de estadísticas con animación
        statsContainer.add(createAnimatedStatsCard("Pendientes", pendientes + "", 
                new Color(59, 130, 246), "⏳", Tarea.EstadoTarea.PENDIENTE));
        
        statsContainer.add(createAnimatedStatsCard("Completadas", completadas + "", 
                new Color(16, 185, 129), "✅", Tarea.EstadoTarea.COMPLETADA));
                
        statsContainer.add(createAnimatedStatsCard("Retrasadas", retrasadas + "", 
                new Color(244, 63, 94), "⚠️", Tarea.EstadoTarea.RETRASADA));
        
        return statsContainer;
    }

    private JPanel createAnimatedStatsCard(String titulo, String valor, Color color, String icono, Tarea.EstadoTarea estado) {
        // Panel con efecto de elevación 3D
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Sombra exterior
                for (int i = 5; i > 0; i--) {
                    g2d.setColor(new Color(0, 0, 0, 5 * i));
                    g2d.fillRoundRect(i, i, getWidth() - i*2, getHeight() - i*2, RADIO_ESQUINAS, RADIO_ESQUINAS);
                }
                
                // Fondo principal
                g2d.setColor(COLOR_CARD);
                g2d.fillRoundRect(0, 0, getWidth() - 5, getHeight() - 5, RADIO_ESQUINAS, RADIO_ESQUINAS);
                
                // Barra de color en la parte superior
                g2d.setColor(color);
                g2d.fillRoundRect(0, 0, getWidth() - 5, 8, 4, 4);
                
                // Resplandor sutil
                g2d.setPaint(new GradientPaint(
                    0, 15, new Color(color.getRed(), color.getGreen(), color.getBlue(), 30),
                    0, 60, new Color(color.getRed(), color.getGreen(), color.getBlue(), 0)
                ));
                g2d.fillRoundRect(10, 15, getWidth() - 25, 50, RADIO_ESQUINAS, RADIO_ESQUINAS);
            }
        };
        
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 25, 20, 25));
        card.setOpaque(false);
        
        // Contenido
        JPanel contenido = new JPanel(new BorderLayout(15, 0));
        contenido.setOpaque(false);
        
        JLabel iconoLabel = new JLabel(icono);
        iconoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 38));
        iconoLabel.setBorder(new EmptyBorder(0, 0, 0, 10));
        
        JPanel textoPanel = new JPanel();
        textoPanel.setLayout(new BoxLayout(textoPanel, BoxLayout.Y_AXIS));
        textoPanel.setOpaque(false);
        
        JLabel valorLabel = new JLabel(valor);
        valorLabel.setFont(new Font("Montserrat", Font.BOLD, 26));
        valorLabel.setForeground(color);
        
        JLabel tituloLabel = new JLabel(titulo);
        tituloLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tituloLabel.setForeground(COLOR_TEXTO);
        
        textoPanel.add(valorLabel);
        textoPanel.add(Box.createVerticalStrut(5));
        textoPanel.add(tituloLabel);
        
        contenido.add(iconoLabel, BorderLayout.WEST);
        contenido.add(textoPanel, BorderLayout.CENTER);
        
        card.add(contenido, BorderLayout.CENTER);
        
        // Añadir animación de hover
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorder(new CompoundBorder(
                    new LineBorder(color, 1, true),
                    new EmptyBorder(19, 24, 19, 24)
                ));
                card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorder(new EmptyBorder(20, 25, 20, 25));
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                mostrarTareasPorEstado(estado);
            }
        });
        
        return card;
    }

    private JPanel createMainContent() {
        cardPanel = new JPanel();
        detalleCardLayout = new CardLayout();
        cardPanel.setLayout(detalleCardLayout);
        cardPanel.setOpaque(false);
        
        // Panel de vista Kanban
        JPanel kanbanView = createKanbanView();
        
        // Panel de detalle de tarea
        JPanel detalleView = createDetalleView();
        
        cardPanel.add(kanbanView, "kanban");
        cardPanel.add(detalleView, "detalle");
        detalleCardLayout.show(cardPanel, "kanban");
        
        return cardPanel;
    }

    private JPanel createKanbanView() {
        JPanel kanbanView = new JPanel(new BorderLayout(0, ESPACIO_COMPONENTES));
        kanbanView.setBackground(COLOR_FONDO);
        
        // Controles superiores
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.setOpaque(false);
        controlPanel.setPreferredSize(new Dimension(0, 50));
        
        JLabel tituloKanban = new JLabel("Vista Kanban");
        tituloKanban.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tituloKanban.setForeground(COLOR_TEXTO);
        
        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botonesPanel.setOpaque(false);
        
        JButton btnMisAnimales = createActionButton("🐾 Mis Animales", COLOR_SECUNDARIO);
        JButton btnCalendario = createActionButton("📅 Ver Calendario", COLOR_TERCIARIO);
        
        btnMisAnimales.addActionListener(e -> mostrarMisAnimales());
        btnCalendario.addActionListener(e -> mostrarCalendario());
        
        botonesPanel.add(btnMisAnimales);
        botonesPanel.add(btnCalendario);
        
        controlPanel.add(tituloKanban, BorderLayout.WEST);
        controlPanel.add(botonesPanel, BorderLayout.EAST);
        
        // Tablero Kanban
        kanbanPanel = new JPanel();
        kanbanPanel.setLayout(new BoxLayout(kanbanPanel, BoxLayout.X_AXIS));
        kanbanPanel.setOpaque(false);
        
        scrollKanban = new JScrollPane(kanbanPanel);
        scrollKanban.setBorder(null);
        scrollKanban.setOpaque(false);
        scrollKanban.getViewport().setOpaque(false);
        scrollKanban.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollKanban.getHorizontalScrollBar().setUnitIncrement(16);
        
        // Personalizar 
        
        kanbanView.add(controlPanel, BorderLayout.NORTH);
        kanbanView.add(scrollKanban, BorderLayout.CENTER);
        
        return kanbanView;
    }

    private JPanel createDetalleView() {
        detallePanel = new JPanel(new BorderLayout(0, ESPACIO_COMPONENTES));
        detallePanel.setBackground(COLOR_FONDO);
        
        // Panel de cabecera con botón de regreso
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setPreferredSize(new Dimension(0, 50));
        
        JButton btnVolver = new JButton("← Volver");
        btnVolver.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnVolver.setBorderPainted(false);
        btnVolver.setContentAreaFilled(false);
        btnVolver.setForeground(COLOR_SECUNDARIO);
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVolver.addActionListener(e -> mostrarVistaKanban());
        
        JPanel accionesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        accionesPanel.setOpaque(false);
        
        btnCompletarTarea = createActionButton("✅ Completar Tarea", new Color(16, 185, 129));
        btnCompletarTarea.addActionListener(e -> completarTareaSeleccionada());
        
        accionesPanel.add(btnCompletarTarea);
        
        headerPanel.add(btnVolver, BorderLayout.WEST);
        headerPanel.add(accionesPanel, BorderLayout.EAST);
        
        // Panel de contenido de detalle (se rellenará dinámicamente)
        JPanel contenidoDetalle = new JPanel();
        contenidoDetalle.setLayout(new BoxLayout(contenidoDetalle, BoxLayout.Y_AXIS));
        contenidoDetalle.setBackground(COLOR_CARD);
        contenidoDetalle.setBorder(new EmptyBorder(30, 35, 30, 35));
        
        contenedorTareas = contenidoDetalle;
        
        JScrollPane scrollDetalle = new JScrollPane(contenidoDetalle);
        scrollDetalle.setBorder(new RoundedBorder(COLOR_CARD, 1, RADIO_ESQUINAS));
        scrollDetalle.setOpaque(false);
        scrollDetalle.getViewport().setOpaque(false);
        
        detallePanel.add(headerPanel, BorderLayout.NORTH);
        detallePanel.add(scrollDetalle, BorderLayout.CENTER);
        
        return detallePanel;
    }
    
    private JButton createActionButton(String texto, Color color) {
        JButton button = new JButton(texto);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(10, 16, 10, 16));
        
        // Efecto hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
        
        return button;
    }

    private void cargarDatos() {
        // Cargar tareas del usuario actual
        TareaDAO dao = new TareaDAO();
        List<Tarea> misTareas = dao.obtenerPorVoluntario(usuarioActual);
        
        // Inicializar columnas
        columnasTareas = new HashMap<>();
        
        // Crear y añadir columnas para cada estado
        kanbanPanel.removeAll();
        kanbanPanel.add(Box.createHorizontalStrut(10)); // Margen inicial
        
        Tarea.EstadoTarea[] estados = {
            Tarea.EstadoTarea.PENDIENTE, 
            Tarea.EstadoTarea.EN_PROGRESO,
            Tarea.EstadoTarea.COMPLETADA, 
            Tarea.EstadoTarea.RETRASADA
        };
        
        for (Tarea.EstadoTarea estado : estados) {
            JPanel columna = createColumna(estado, misTareas.stream()
                .filter(t -> t.getEstado() == estado)
                .collect(Collectors.toList()));
                
            columnasTareas.put(estado, columna);
            kanbanPanel.add(columna);
            kanbanPanel.add(Box.createHorizontalStrut(15));
        }
        
        kanbanPanel.revalidate();
        kanbanPanel.repaint();
    }
    
    private JPanel createColumna(Tarea.EstadoTarea estado, List<Tarea> tareas) {
        JPanel columna = new JPanel();
        columna.setLayout(new BoxLayout(columna, BoxLayout.Y_AXIS));
        columna.setBackground(new Color(243, 244, 246));
        columna.setBorder(new RoundedBorder(new Color(243, 244, 246), 1, RADIO_ESQUINAS));
        columna.setMaximumSize(new Dimension(ANCHO_COLUMNA, Integer.MAX_VALUE));
        columna.setPreferredSize(new Dimension(ANCHO_COLUMNA, 0));
        
        // Cabecera de la columna
        JPanel headerColumna = new JPanel(new BorderLayout());
        headerColumna.setOpaque(false);
        headerColumna.setBorder(new EmptyBorder(15, 20, 15, 20));
        headerColumna.setMaximumSize(new Dimension(ANCHO_COLUMNA, 60));
        
        JLabel iconoEstado = new JLabel(getEstadoIcono(estado));
        iconoEstado.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        
        JLabel tituloEstado = new JLabel(formatearEstado(estado));
        tituloEstado.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tituloEstado.setForeground(getEstadoColor(estado));
        
        JLabel contador = new JLabel(tareas.size() + "");
        contador.setFont(new Font("Segoe UI", Font.BOLD, 14));
        contador.setForeground(COLOR_TEXTO_SECUNDARIO);
        contador.setOpaque(true);
        contador.setBackground(new Color(229, 231, 235));
        contador.setHorizontalAlignment(SwingConstants.CENTER);
        contador.setBorder(new EmptyBorder(3, 10, 3, 10));
        
        JPanel tituloPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        tituloPanel.setOpaque(false);
        tituloPanel.add(iconoEstado);
        tituloPanel.add(tituloEstado);
        
        headerColumna.add(tituloPanel, BorderLayout.WEST);
        headerColumna.add(contador, BorderLayout.EAST);
        
        columna.add(headerColumna);
        columna.add(Box.createVerticalStrut(10));
        
        // Contenedor para tarjetas
        JPanel contenedorTarjetas = new JPanel();
        contenedorTarjetas.setLayout(new BoxLayout(contenedorTarjetas, BoxLayout.Y_AXIS));
        contenedorTarjetas.setOpaque(false);
        contenedorTarjetas.setBorder(new EmptyBorder(0, 15, 15, 15));
        
        // Añadir tarjetas de tareas
        for (Tarea tarea : tareas) {
            JPanel tarjeta = createTarjetaTarea(tarea);
            contenedorTarjetas.add(tarjeta);
            contenedorTarjetas.add(Box.createVerticalStrut(15));
        }
        
        // Si no hay tareas, mostrar mensaje
        if (tareas.isEmpty()) {
            JPanel emptyState = createEmptyState(estado);
            contenedorTarjetas.add(emptyState);
        }
        
        // Hacer que el contenedor sea scrollable
        JScrollPane scrollTarjetas = new JScrollPane(contenedorTarjetas);
        scrollTarjetas.setBorder(null);
        scrollTarjetas.setOpaque(false);
        scrollTarjetas.getViewport().setOpaque(false);
        scrollTarjetas.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollTarjetas.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        // Personalizar scrollbar vertical
        
        columna.add(scrollTarjetas);
        
        return columna;
    }
    
    private JPanel createTarjetaTarea(Tarea tarea) {
        JPanel tarjeta = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Sombra sutil
                for (int i = 3; i > 0; i--) {
                    g2d.setColor(new Color(0, 0, 0, 5 * i));
                    g2d.fillRoundRect(i, i, getWidth() - i*2, getHeight() - i*2, RADIO_ESQUINAS, RADIO_ESQUINAS);
                }
                
                // Fondo blanco
                g2d.setColor(COLOR_CARD);
                g2d.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, RADIO_ESQUINAS, RADIO_ESQUINAS);
                
                // Indicador de prioridad (línea de color en el lado izquierdo)
                g2d.setColor(getPrioridadColor(tarea.getPrioridad()));
                g2d.fillRect(0, 0, 4, getHeight() - 3);
            }
        };
        
        tarjeta.setLayout(new BorderLayout());
        tarjeta.setBorder(new EmptyBorder(15, 20, 15, 20));
        tarjeta.setMaximumSize(new Dimension(ANCHO_COLUMNA - 40, ALTURA_TARJETA));
        tarjeta.setMinimumSize(new Dimension(ANCHO_COLUMNA - 40, ALTURA_TARJETA));
        tarjeta.setPreferredSize(new Dimension(ANCHO_COLUMNA - 40, ALTURA_TARJETA));
        tarjeta.setOpaque(false);
        
        // Panel superior (título y prioridad)
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        
        JLabel tituloLabel = new JLabel(tarea.getTitulo());
        tituloLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tituloLabel.setForeground(COLOR_TEXTO);
        
        JLabel prioridadLabel = new JLabel(getPrioridadIcono(tarea.getPrioridad()));
        prioridadLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        prioridadLabel.setForeground(getPrioridadColor(tarea.getPrioridad()));
        prioridadLabel.setBorder(new EmptyBorder(2, 8, 2, 8));
        prioridadLabel.setOpaque(true);
        prioridadLabel.setBackground(new Color(getPrioridadColor(tarea.getPrioridad()).getRed(),
                                              getPrioridadColor(tarea.getPrioridad()).getGreen(),
                                              getPrioridadColor(tarea.getPrioridad()).getBlue(), 30));
        
        topPanel.add(tituloLabel, BorderLayout.CENTER);
        topPanel.add(prioridadLabel, BorderLayout.EAST);
        
        // Panel central (descripción)
        JTextArea descripcionArea = new JTextArea();
        descripcionArea.setText(tarea.getDescripcion() != null ? 
            (tarea.getDescripcion().length() > 80 ? 
              tarea.getDescripcion().substring(0, 80) + "..." : 
              tarea.getDescripcion()) : 
            "Sin descripción");
        descripcionArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descripcionArea.setForeground(COLOR_TEXTO_SECUNDARIO);
        descripcionArea.setLineWrap(true);
        descripcionArea.setWrapStyleWord(true);
        descripcionArea.setEditable(false);
        descripcionArea.setOpaque(false);
        descripcionArea.setBorder(null);
        descripcionArea.setRows(3);
        
        // Panel inferior (fecha y animal)
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        JLabel fechaLabel = new JLabel("📅 " + (tarea.getFechaLimite() != null ? 
                                              sdf.format(tarea.getFechaLimite()) : "Sin fecha"));
        fechaLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        fechaLabel.setForeground(COLOR_TEXTO_SECUNDARIO);
        
        String nombreAnimal = tarea.getAnimal() != null ? tarea.getAnimal().getNombre() : "N/A";
        JLabel animalLabel = new JLabel("🐾 " + nombreAnimal);
        animalLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        animalLabel.setForeground(COLOR_TEXTO_SECUNDARIO);
        
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        infoPanel.setOpaque(false);
        infoPanel.add(fechaLabel);
        infoPanel.add(animalLabel);
        
        bottomPanel.add(infoPanel, BorderLayout.WEST);
        
        tarjeta.add(topPanel, BorderLayout.NORTH);
        tarjeta.add(descripcionArea, BorderLayout.CENTER);
        tarjeta.add(bottomPanel, BorderLayout.SOUTH);
        
        // Añadir evento para mostrar detalles
        tarjeta.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                tarjeta.setCursor(new Cursor(Cursor.HAND_CURSOR));
                tarjeta.setBorder(new CompoundBorder(
                    new LineBorder(COLOR_SECUNDARIO, 1, true),
                    new EmptyBorder(14, 19, 14, 19)
                ));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                tarjeta.setBorder(new EmptyBorder(15, 20, 15, 20));
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                mostrarDetalleTarea(tarea);
            }
        });
        
        return tarjeta;
    }
    
    private JPanel createEmptyState(Tarea.EstadoTarea estado) {
        JPanel emptyPanel = new JPanel();
        emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));
        emptyPanel.setOpaque(false);
        emptyPanel.setBorder(new EmptyBorder(30, 0, 30, 0));
        emptyPanel.setMaximumSize(new Dimension(ANCHO_COLUMNA - 40, 120));
        emptyPanel.setPreferredSize(new Dimension(ANCHO_COLUMNA - 40, 120));
        
        JLabel iconoLabel = new JLabel(getEmptyStateIcono(estado));
        iconoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        iconoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel mensajeLabel = new JLabel(getEmptyStateMsg(estado));
        mensajeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        mensajeLabel.setForeground(COLOR_TEXTO_SECUNDARIO);
        mensajeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        emptyPanel.add(Box.createVerticalGlue());
        emptyPanel.add(iconoLabel);
        emptyPanel.add(Box.createVerticalStrut(10));
        emptyPanel.add(mensajeLabel);
        emptyPanel.add(Box.createVerticalGlue());
        
        return emptyPanel;
    }
    
    private String getEmptyStateIcono(Tarea.EstadoTarea estado) {
        switch (estado) {
            case PENDIENTE: return "📋";
            case EN_PROGRESO: return "🔄";
            case COMPLETADA: return "🎉";
            case RETRASADA: return "⏰";
            default: return "📝";
        }
    }
    
    private String getEmptyStateMsg(Tarea.EstadoTarea estado) {
        switch (estado) {
            case PENDIENTE: return "No hay tareas pendientes";
            case EN_PROGRESO: return "No hay tareas en progreso";
            case COMPLETADA: return "No hay tareas completadas";
            case RETRASADA: return "No hay tareas retrasadas";
            default: return "No hay tareas";
        }
    }
    
    private void mostrarDetalleTarea(Tarea tarea) {
        tareaSeleccionada = tarea;
        modoDetalle = true;
        
        // Actualizar estado del botón completar
        btnCompletarTarea.setEnabled(tarea.getEstado() != Tarea.EstadoTarea.COMPLETADA);
        
        // Construir panel de detalles
        contenedorTareas.removeAll();
        
        // Cabecera con título
        JPanel cabeceraPanel = new JPanel(new BorderLayout());
        cabeceraPanel.setOpaque(false);
        cabeceraPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JLabel tituloLabel = new JLabel(tarea.getTitulo());
        tituloLabel.setFont(new Font("Montserrat", Font.BOLD, 24));
        tituloLabel.setForeground(COLOR_TEXTO);
        
        JPanel etiquetasPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        etiquetasPanel.setOpaque(false);
        
        // Etiqueta de estado
        JLabel estadoLabel = new JLabel(getEstadoIcono(tarea.getEstado()) + " " + 
                                      formatearEstado(tarea.getEstado()));
        estadoLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        estadoLabel.setForeground(getEstadoColor(tarea.getEstado()));
        estadoLabel.setBorder(new EmptyBorder(5, 10, 5, 10));
        estadoLabel.setOpaque(true);
        estadoLabel.setBackground(new Color(getEstadoColor(tarea.getEstado()).getRed(),
                                          getEstadoColor(tarea.getEstado()).getGreen(),
                                          getEstadoColor(tarea.getEstado()).getBlue(), 40));
        
        // Etiqueta de prioridad
        JLabel prioridadLabel = new JLabel(getPrioridadIcono(tarea.getPrioridad()) + " " + 
                                        formatearPrioridad(tarea.getPrioridad()));
        prioridadLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        prioridadLabel.setForeground(getPrioridadColor(tarea.getPrioridad()));
        prioridadLabel.setBorder(new EmptyBorder(5, 10, 5, 10));
        prioridadLabel.setOpaque(true);
        prioridadLabel.setBackground(new Color(getPrioridadColor(tarea.getPrioridad()).getRed(),
                                             getPrioridadColor(tarea.getPrioridad()).getGreen(),
                                             getPrioridadColor(tarea.getPrioridad()).getBlue(), 40));
        
        etiquetasPanel.add(prioridadLabel);
        etiquetasPanel.add(estadoLabel);
        
        cabeceraPanel.add(tituloLabel, BorderLayout.WEST);
        cabeceraPanel.add(etiquetasPanel, BorderLayout.EAST);
        
        // Separador estilizado
        JSeparator separador = new JSeparator();
        separador.setForeground(new Color(229, 231, 235));
        separador.setBackground(COLOR_CARD);
        separador.setPreferredSize(new Dimension(0, 1));
        
        // Panel de información
        JPanel infoPanel = new JPanel(new BorderLayout(20, 0));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(new EmptyBorder(20, 0, 20, 0));
        
        // Columna izquierda
        JPanel colIzquierda = new JPanel();
        colIzquierda.setLayout(new BoxLayout(colIzquierda, BoxLayout.Y_AXIS));
        colIzquierda.setOpaque(false);
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        
        addInfoRow(colIzquierda, "Fecha Asignación", 
                 tarea.getFechaAsignacion() != null ? sdf.format(tarea.getFechaAsignacion()) : "No especificada",
                 "📅");
        addInfoRow(colIzquierda, "Fecha Límite", 
                 tarea.getFechaLimite() != null ? sdf.format(tarea.getFechaLimite()) : "No especificada",
                 "⏰");
        addInfoRow(colIzquierda, "Asignada por", 
                 tarea.getAsignador() != null ? tarea.getAsignador().getNombre() : "No especificado",
                 "👤");
        
        // Columna derecha
        JPanel colDerecha = new JPanel();
        colDerecha.setLayout(new BoxLayout(colDerecha, BoxLayout.Y_AXIS));
        colDerecha.setOpaque(false);
        
        addInfoRow(colDerecha, "Animal", 
                 tarea.getAnimal() != null ? tarea.getAnimal().getNombre() : "No asignado",
                 "🐾");
        addInfoRow(colDerecha, "Fecha Completada", 
                 tarea.getFechaCompletada() != null ? sdf.format(tarea.getFechaCompletada()) : "No completada",
                 "✓");
        
        infoPanel.add(colIzquierda, BorderLayout.WEST);
        infoPanel.add(colDerecha, BorderLayout.CENTER);
        
        // Descripción completa
        JPanel descPanel = new JPanel(new BorderLayout());
        descPanel.setOpaque(false);
        descPanel.setBorder(new EmptyBorder(10, 0, 20, 0));
        
        JLabel descTitulo = new JLabel("Descripción");
        descTitulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        descTitulo.setForeground(COLOR_TEXTO);
        
        JTextArea descArea = new JTextArea();
        descArea.setText(tarea.getDescripcion() != null ? tarea.getDescripcion() : "Sin descripción");
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descArea.setForeground(COLOR_TEXTO);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setOpaque(false);
        descArea.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        descPanel.add(descTitulo, BorderLayout.NORTH);
        descPanel.add(descArea, BorderLayout.CENTER);
        
        // Comentarios de completado (si existe)
        JPanel comentariosPanel = new JPanel(new BorderLayout());
        comentariosPanel.setOpaque(false);
        comentariosPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        if (tarea.getComentariosCompletado() != null && !tarea.getComentariosCompletado().isEmpty()) {
            JLabel comentariosTitulo = new JLabel("Comentarios de Finalización");
            comentariosTitulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
            comentariosTitulo.setForeground(COLOR_TEXTO);
            
            JPanel comentarioContent = new JPanel(new BorderLayout());
            comentarioContent.setOpaque(true);
            comentarioContent.setBackground(new Color(243, 244, 246));
            comentarioContent.setBorder(new EmptyBorder(15, 20, 15, 20));
            
            JLabel iconoComentario = new JLabel("💬");
            iconoComentario.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            
            JTextArea comentariosArea = new JTextArea();
            comentariosArea.setText(tarea.getComentariosCompletado());
            comentariosArea.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            comentariosArea.setForeground(COLOR_TEXTO_SECUNDARIO);
            comentariosArea.setLineWrap(true);
            comentariosArea.setWrapStyleWord(true);
            comentariosArea.setEditable(false);
            comentariosArea.setOpaque(false);
            comentariosArea.setBorder(new EmptyBorder(0, 10, 0, 0));
            
            comentarioContent.add(iconoComentario, BorderLayout.WEST);
            comentarioContent.add(comentariosArea, BorderLayout.CENTER);
            
            comentariosPanel.add(comentariosTitulo, BorderLayout.NORTH);
            comentariosPanel.add(Box.createVerticalStrut(10), BorderLayout.CENTER);
            comentariosPanel.add(comentarioContent, BorderLayout.SOUTH);
        }
        
        // Añadir componentes al panel principal
        contenedorTareas.add(cabeceraPanel);
        contenedorTareas.add(separador);
        contenedorTareas.add(infoPanel);
        contenedorTareas.add(descPanel);
        
        if (tarea.getComentariosCompletado() != null && !tarea.getComentariosCompletado().isEmpty()) {
            contenedorTareas.add(comentariosPanel);
        }
        
        contenedorTareas.revalidate();
        contenedorTareas.repaint();
        
        // Cambiar a vista de detalle
        detalleCardLayout.show(cardPanel, "detalle");
    }
    
    private void addInfoRow(JPanel parent, String titulo, String valor, String icono) {
        JPanel rowPanel = new JPanel(new BorderLayout(10, 0));
        rowPanel.setOpaque(false);
        rowPanel.setBorder(new EmptyBorder(5, 0, 5, 0));
        rowPanel.setMaximumSize(new Dimension(350, 30));
        
        JLabel iconoLabel = new JLabel(icono);
        iconoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        iconoLabel.setPreferredSize(new Dimension(20, 20));
        
        JLabel tituloLabel = new JLabel(titulo + ":");
        tituloLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tituloLabel.setForeground(COLOR_TEXTO);
        tituloLabel.setPreferredSize(new Dimension(120, 20));
        
        JLabel valorLabel = new JLabel(valor);
        valorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        valorLabel.setForeground(COLOR_TEXTO_SECUNDARIO);
        
        rowPanel.add(iconoLabel, BorderLayout.WEST);
        rowPanel.add(tituloLabel, BorderLayout.CENTER);
        rowPanel.add(valorLabel, BorderLayout.EAST);
        
        parent.add(rowPanel);
        parent.add(Box.createVerticalStrut(8));
    }
    
    private String getEstadoIcono(Tarea.EstadoTarea estado) {
        if (estado == null) return "❓";
        switch (estado) {
            case PENDIENTE: return "⏳";
            case EN_PROGRESO: return "🔄";
            case COMPLETADA: return "✅";
            case RETRASADA: return "⚠️";
            case CANCELADA: return "❌";
            default: return "📝";
        }
    }
    
    private Color getEstadoColor(Tarea.EstadoTarea estado) {
        if (estado == null) return COLOR_TEXTO_SECUNDARIO;
        switch (estado) {
            case PENDIENTE: return new Color(59, 130, 246);
            case EN_PROGRESO: return new Color(139, 92, 246);
            case COMPLETADA: return new Color(16, 185, 129);
            case RETRASADA: return new Color(244, 63, 94);
            case CANCELADA: return new Color(107, 114, 128);
            default: return COLOR_TEXTO_SECUNDARIO;
        }
    }
    
    private String formatearEstado(Tarea.EstadoTarea estado) {
        if (estado == null) return "No definido";
        switch (estado) {
            case PENDIENTE: return "Pendiente";
            case EN_PROGRESO: return "En Progreso";
            case COMPLETADA: return "Completada";
            case RETRASADA: return "Retrasada";
            case CANCELADA: return "Cancelada";
            default: return estado.toString();
        }
    }
    
    private String getPrioridadIcono(Tarea.PrioridadTarea prioridad) {
        if (prioridad == null) return "⚪";
        switch (prioridad) {
            case BAJA: return "🟢 Baja";
            case MEDIA: return "🟡 Media";
            case ALTA: return "🟠 Alta";
            case URGENTE: return "🔴 Urgente";
            default: return "⚪ N/A";
        }
    }
    
    private Color getPrioridadColor(Tarea.PrioridadTarea prioridad) {
        if (prioridad == null) return COLOR_TEXTO_SECUNDARIO;
        switch (prioridad) {
            case BAJA: return new Color(16, 185, 129);
            case MEDIA: return new Color(245, 158, 11);
            case ALTA: return new Color(249, 115, 22);
            case URGENTE: return new Color(244, 63, 94);
            default: return COLOR_TEXTO_SECUNDARIO;
        }
    }
    
    private String formatearPrioridad(Tarea.PrioridadTarea prioridad) {
        if (prioridad == null) return "No definida";
        switch (prioridad) {
            case BAJA: return "Baja";
            case MEDIA: return "Media";
            case ALTA: return "Alta";
            case URGENTE: return "Urgente";
            default: return prioridad.toString();
        }
    }
    
    private void mostrarVistaKanban() {
        modoDetalle = false;
        detalleCardLayout.show(cardPanel, "kanban");
    }
    
    private void completarTareaSeleccionada() {
        if (tareaSeleccionada != null && tareaSeleccionada.getEstado() != Tarea.EstadoTarea.COMPLETADA) {
            String comentarios = JOptionPane.showInputDialog(this, 
                                                         "Comentarios sobre la tarea completada:",
                                                         "Completar Tarea",
                                                         JOptionPane.INFORMATION_MESSAGE);
            
            if (comentarios != null) { // No canceló el diálogo
                TareaDAO dao = new TareaDAO();
                if (dao.completarTarea(tareaSeleccionada.getIdTarea(), comentarios)) {
                    JOptionPane.showMessageDialog(this, 
                                               "¡Tarea completada con éxito!",
                                               "Tarea Completada",
                                               JOptionPane.INFORMATION_MESSAGE);
                    
                    cargarDatos();
                    mostrarVistaKanban();
                } else {
                    JOptionPane.showMessageDialog(this,
                                               "Error al completar la tarea. Inténtelo de nuevo.",
                                               "Error",
                                               JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void mostrarTareasPorEstado(Tarea.EstadoTarea estado) {
        // Mostrar solo las tareas de un estado específico
        TareaDAO dao = new TareaDAO();
        List<Tarea> tareas;
        
        if (estado == null) {
            tareas = dao.obtenerPorVoluntario(usuarioActual);
        } else {
            tareas = dao.obtenerPorVoluntario(usuarioActual).stream()
                    .filter(t -> t.getEstado() == estado)
                    .collect(Collectors.toList());
        }
        
        // Crear columnas kanban solo con ese estado
        kanbanPanel.removeAll();
        kanbanPanel.add(Box.createHorizontalStrut(10));
        
        JPanel columna = createColumna(estado, tareas);
        columnasTareas = new HashMap<>();
        columnasTareas.put(estado, columna);
        kanbanPanel.add(columna);
        
        // Centrar la columna
        kanbanPanel.add(Box.createHorizontalGlue());
        
        kanbanPanel.revalidate();
        kanbanPanel.repaint();
    }
    
    private void mostrarMisAnimales() {
        // Implementar en futura versión
        JOptionPane.showMessageDialog(this, 
                                   "Visualización de animales asignados en desarrollo",
                                   "Próximamente",
                                   JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void mostrarCalendario() {
        // Implementar en futura versión
        JOptionPane.showMessageDialog(this, 
                                   "Vista de calendario en desarrollo",
                                   "Próximamente",
                                   JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void actualizarDatos() {
        cargarDatos();
        if (modoDetalle && tareaSeleccionada != null) {
            TareaDAO dao = new TareaDAO();
            Tarea tarea = dao.obtenerPorId(tareaSeleccionada.getIdTarea());
            if (tarea != null) {
                mostrarDetalleTarea(tarea);
            } else {
                mostrarVistaKanban();
            }
        }
    }
    
    // Clases auxiliares para personalización visual
    
    // Borde redondeado personalizado
    private class RoundedBorder extends AbstractBorder {
        private Color color;
        private int thickness;
        private int radius;
        
        public RoundedBorder(Color color, int thickness, int radius) {
            this.color = color;
            this.thickness = thickness;
            this.radius = radius;
        }
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(color);
            g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2d.dispose();
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(thickness, thickness, thickness, thickness);
        }
        
        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
            UIManager.put("Button.arc", 999);
            UIManager.put("TextComponent.arc", 55);
            UIManager.put("Component.arc", 55);
            UIManager.put("TextField.margin", new Insets(20, 20, 20, 20));
            UIManager.put("PasswordField.margin", new Insets(20, 20, 20, 20));
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Test Dashboard");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 800);
            frame.setLocationRelativeTo(null);
            
            // Usuario de ejemplo
            Usuario usuario = new Usuario("María González", "maria@nuevohogar.com", 
                                        "password", Usuario.RolUsuario.Coordinador);
            usuario.setIdUsuario(1);
            
            PanelTareas dashboard = new PanelTareas(usuario);
            frame.add(dashboard);
            frame.setVisible(true);
        });
    }
}