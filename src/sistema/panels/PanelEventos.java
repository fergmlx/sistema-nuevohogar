package sistema.panels;

import sistema.dao.EventoDAO;
import sistema.dao.UsuarioDAO;
import sistema.modelos.Evento;
import sistema.modelos.Usuario;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import static java.awt.SystemColor.scrollbar;
import java.awt.event.*;
import java.awt.geom.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class PanelEventos extends JPanel {
    private Usuario usuarioActual;
    private JPanel eventosPanel;
    private JPanel calendarioPanel;
    private JPanel detallePanel;
    private JScrollPane scrollEventos;
    private CardLayout cardLayout;
    private JPanel cardContainer;
    private String vistaActual = "lista";
    private Evento eventoSeleccionado;
    private int mesActual;
    private int añoActual;
    private JLabel mesLabel;
    private Map<Integer, List<Evento>> eventosDelMes;
    private JButton btnInscribirse;
    
    // Constantes de diseño
    private final Color COLOR_PRIMARIO = new Color(220, 38, 38);
    private final Color COLOR_SECUNDARIO = new Color(239, 68, 68);
    private final Color COLOR_ACENTO = new Color(254, 202, 202);
    private final Color COLOR_FONDO = new Color(250, 250, 250);
    private final Color COLOR_TARJETA = Color.WHITE;
    private final Color COLOR_TEXTO = new Color(17, 24, 39);
    private final Color COLOR_TEXTO_SECUNDARIO = new Color(107, 114, 128);
    private final int PADDING_PANEL = 30;
    private final int ESPACIO_COMPONENTES = 20;
    private final int RADIO_BORDES = 12;

    public PanelEventos(Usuario usuario) {
        this.usuarioActual = usuario;
        Calendar cal = Calendar.getInstance();
        this.mesActual = cal.get(Calendar.MONTH);
        this.añoActual = cal.get(Calendar.YEAR);
        
        initComponents();
        cargarDatos();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, ESPACIO_COMPONENTES));
        setBorder(new EmptyBorder(PADDING_PANEL, PADDING_PANEL, PADDING_PANEL, PADDING_PANEL));
        setBackground(COLOR_FONDO);

        add(createHeader(), BorderLayout.NORTH);
        
        // Panel principal con CardLayout
        cardContainer = new JPanel();
        cardLayout = new CardLayout();
        cardContainer.setLayout(cardLayout);
        cardContainer.setOpaque(false);
        
        // Agregar las diferentes vistas
        cardContainer.add(createListaView(), "lista");
        cardContainer.add(createCalendarioView(), "calendario");
        cardContainer.add(createDetalleView(), "detalle");
        
        add(cardContainer, BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(ESPACIO_COMPONENTES, 0));
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 80));

        // Título con estilo moderno y degradado
        JPanel tituloPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Crear degradado para el fondo del título
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(185, 28, 28), 
                    getWidth(), getHeight(), new Color(220, 38, 38)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), RADIO_BORDES, RADIO_BORDES);
                
                // Patrón decorativo
                g2d.setColor(new Color(255, 255, 255, 20));
                for (int i = -50; i < getWidth(); i += 40) {
                    g2d.fillOval(i, -10, 20, 20);
                    g2d.fillOval(i + 20, getHeight() - 10, 20, 20);
                }
                
                g2d.dispose();
            }
        };
        tituloPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 0));
        tituloPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        tituloPanel.setPreferredSize(new Dimension(400, 80));
        
        JLabel iconoLabel = new JLabel("🎪");
        iconoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        iconoLabel.setForeground(Color.WHITE);
        
        JPanel textoPanel = new JPanel();
        textoPanel.setLayout(new BoxLayout(textoPanel, BoxLayout.Y_AXIS));
        textoPanel.setOpaque(false);
        
        JLabel tituloLabel = new JLabel("Eventos y Actividades");
        tituloLabel.setFont(new Font("Montserrat", Font.BOLD, 24));
        tituloLabel.setForeground(Color.WHITE);
        tituloLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel subtituloLabel = new JLabel("Participa en nuestros próximos eventos");
        subtituloLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtituloLabel.setForeground(new Color(255, 255, 255, 200));
        subtituloLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        textoPanel.add(tituloLabel);
        textoPanel.add(Box.createVerticalStrut(5));
        textoPanel.add(subtituloLabel);
        
        tituloPanel.add(iconoLabel);
        tituloPanel.add(textoPanel);
        
        // Panel de cambio de vistas
        JPanel vistasPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        vistasPanel.setOpaque(false);
        vistasPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        
        JPanel btnGroup = createToggleViewPanel();
        
        vistasPanel.add(btnGroup);
        
        header.add(tituloPanel, BorderLayout.WEST);
        header.add(vistasPanel, BorderLayout.EAST);
        
        return header;
    }
    
    private JPanel createToggleViewPanel() {
        // Panel contenedor con efecto flotante
        JPanel toggleContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Sombra
                for (int i = 0; i < 4; i++) {
                    g2.setColor(new Color(0, 0, 0, 10 - i * 2));
                    g2.fillRoundRect(i, i, getWidth() - i * 2, getHeight() - i * 2, RADIO_BORDES, RADIO_BORDES);
                }
                
                // Fondo del contenedor
                g2.setColor(new Color(241, 245, 249));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), RADIO_BORDES, RADIO_BORDES);
                
                // Línea divisoria sutil
                g2.setColor(new Color(226, 232, 240));
                g2.drawLine(getWidth()/2, 5, getWidth()/2, getHeight()-5);
            }
        };
        toggleContainer.setLayout(new GridLayout(1, 2, 0, 0));
        toggleContainer.setBorder(new EmptyBorder(5, 5, 5, 5));
        toggleContainer.setPreferredSize(new Dimension(160, 45));
        
        // Botón de lista
        JButton btnLista = new JButton("Lista") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (vistaActual.equals("lista")) {
                    // Seleccionado con efecto de resplandor
                    g2.setColor(COLOR_TARJETA);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), RADIO_BORDES, RADIO_BORDES);
                    
                    // Borde con degradado
                    GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(COLOR_PRIMARIO.getRed(), COLOR_PRIMARIO.getGreen(), COLOR_PRIMARIO.getBlue(), 100), 
                        getWidth(), getHeight(), new Color(COLOR_SECUNDARIO.getRed(), COLOR_SECUNDARIO.getGreen(), COLOR_SECUNDARIO.getBlue(), 100)
                    );
                    g2.setPaint(gradient);
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, RADIO_BORDES-2, RADIO_BORDES-2);
                    
                    g2.setColor(COLOR_TEXTO);
                } else {
                    // No seleccionado
                    g2.setColor(new Color(241, 245, 249));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), RADIO_BORDES, RADIO_BORDES);
                    g2.setColor(COLOR_TEXTO_SECUNDARIO);
                }
                
                // Dibujar el icono
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
                g2.drawString("📋", 10, getHeight()/2 + 5);
                
                // Dibujar el texto
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                g2.drawString("Lista", getWidth()/2 - 5, getHeight()/2 + 5);
                
                g2.dispose();
            }
        };
        btnLista.setBorderPainted(false);
        btnLista.setContentAreaFilled(false);
        btnLista.setFocusPainted(false);
        btnLista.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLista.addActionListener(e -> cambiarVista("lista"));
        
        // Botón de calendario
        JButton btnCalendario = new JButton("Calendario") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (vistaActual.equals("calendario")) {
                    // Seleccionado con efecto de resplandor
                    g2.setColor(COLOR_TARJETA);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), RADIO_BORDES, RADIO_BORDES);
                    
                    // Borde con degradado
                    GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(COLOR_PRIMARIO.getRed(), COLOR_PRIMARIO.getGreen(), COLOR_PRIMARIO.getBlue(), 100), 
                        getWidth(), getHeight(), new Color(COLOR_SECUNDARIO.getRed(), COLOR_SECUNDARIO.getGreen(), COLOR_SECUNDARIO.getBlue(), 100)
                    );
                    g2.setPaint(gradient);
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, RADIO_BORDES-2, RADIO_BORDES-2);
                    
                    g2.setColor(COLOR_TEXTO);
                } else {
                    // No seleccionado
                    g2.setColor(new Color(241, 245, 249));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), RADIO_BORDES, RADIO_BORDES);
                    g2.setColor(COLOR_TEXTO_SECUNDARIO);
                }
                
                // Dibujar el icono
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
                g2.drawString("📅", 10, getHeight()/2 + 5);
                
                // Dibujar el texto
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                g2.drawString("Calendario", getWidth()/2 - 10, getHeight()/2 + 5);
                
                g2.dispose();
            }
        };
        btnCalendario.setBorderPainted(false);
        btnCalendario.setContentAreaFilled(false);
        btnCalendario.setFocusPainted(false);
        btnCalendario.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCalendario.addActionListener(e -> cambiarVista("calendario"));
        
        toggleContainer.add(btnLista);
        toggleContainer.add(btnCalendario);
        
        return toggleContainer;
    }

    private JPanel createListaView() {
        JPanel listaView = new JPanel(new BorderLayout(0, ESPACIO_COMPONENTES));
        listaView.setOpaque(false);
        
        // Encabezado con filtros
        JPanel filtrosPanel = new JPanel(new BorderLayout());
        filtrosPanel.setOpaque(false);
        filtrosPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        // Título de sección
        JLabel tituloSeccion = new JLabel("Próximos Eventos");
        tituloSeccion.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tituloSeccion.setForeground(COLOR_TEXTO);
        
        // Selector de tipo de eventos
        JPanel tiposPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT,10, 0));
        tiposPanel.setOpaque(false);
        
        tiposPanel.add(createFilterButton("Todos", true));
        
        for (Evento.TipoEvento tipo : Evento.TipoEvento.values()) {
            tiposPanel.add(createFilterButton(formatearTipoEvento(tipo), false));
        }
        
        filtrosPanel.add(tituloSeccion, BorderLayout.WEST);
        filtrosPanel.add(tiposPanel, BorderLayout.EAST);
        
        // Panel de eventos
        eventosPanel = new JPanel();
        eventosPanel.setLayout(new BoxLayout(eventosPanel, BoxLayout.Y_AXIS));
        eventosPanel.setOpaque(false);
        
        scrollEventos = new JScrollPane(eventosPanel);
        scrollEventos.setBorder(null);
        scrollEventos.setOpaque(false);
        scrollEventos.getViewport().setOpaque(false);
        scrollEventos.getVerticalScrollBar().setUnitIncrement(16);
        
        // Personalizar scrollbar
        
        listaView.add(filtrosPanel, BorderLayout.NORTH);
        listaView.add(scrollEventos, BorderLayout.CENTER);
        
        return listaView;
    }
    
    private JButton createFilterButton(String texto, boolean activo) {
        JButton button = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (activo) {
                    // Fondo y borde con degradado
                    GradientPaint gradient = new GradientPaint(
                        0, 0, COLOR_PRIMARIO, 
                        getWidth(), getHeight(), COLOR_SECUNDARIO
                    );
                    g2d.setPaint(gradient);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), RADIO_BORDES, RADIO_BORDES);
                    
                    g2d.setColor(Color.WHITE);
                } else {
                    // Fondo transparente con borde
                    g2d.setColor(COLOR_FONDO);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), RADIO_BORDES, RADIO_BORDES);
                    g2d.setColor(COLOR_PRIMARIO);
                    g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, RADIO_BORDES, RADIO_BORDES);
                    
                    g2d.setColor(COLOR_TEXTO);
                }
                
                // Texto centrado
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(texto);
                int textHeight = fm.getHeight();
                g2d.drawString(texto, (getWidth() - textWidth) / 2, 
                             (getHeight() - textHeight) / 2 + fm.getAscent());
                
                g2d.dispose();
            }
        };
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(100, 35));
        
        // Acción del filtro
        button.addActionListener(e -> filtrarEventosPorTipo(texto));
        
        return button;
    }

    private JPanel createCalendarioView() {
        JPanel calendarioView = new JPanel(new BorderLayout(0, ESPACIO_COMPONENTES));
        calendarioView.setOpaque(false);
        
        // Panel de navegación del mes
        JPanel navegacionPanel = new JPanel(new BorderLayout());
        navegacionPanel.setOpaque(false);
        navegacionPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JPanel controlMesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        controlMesPanel.setOpaque(false);
        
        JButton btnAnterior = new JButton("◀") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Círculo con sombra
                g2d.setColor(new Color(0, 0, 0, 20));
                g2d.fillOval(1, 1, getWidth()-2, getHeight()-2);
                
                g2d.setColor(COLOR_FONDO);
                g2d.fillOval(0, 0, getWidth()-2, getHeight()-2);
                
                g2d.setColor(COLOR_TEXTO);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString("◀", (getWidth() - fm.stringWidth("◀"))/2, 
                             getHeight()/2 + fm.getAscent()/2);
                
                g2d.dispose();
            }
        };
        btnAnterior.setBorderPainted(false);
        btnAnterior.setContentAreaFilled(false);
        btnAnterior.setFocusPainted(false);
        btnAnterior.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAnterior.setPreferredSize(new Dimension(40, 40));
        btnAnterior.addActionListener(e -> cambiarMes(-1));
        
        mesLabel = new JLabel(getNombreMes(mesActual) + " " + añoActual);
        mesLabel.setFont(new Font("Montserrat", Font.BOLD, 20));
        mesLabel.setForeground(COLOR_TEXTO);
        mesLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mesLabel.setPreferredSize(new Dimension(200, 40));
        
        JButton btnSiguiente = new JButton("▶") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Círculo con sombra
                g2d.setColor(new Color(0, 0, 0, 20));
                g2d.fillOval(1, 1, getWidth()-2, getHeight()-2);
                
                g2d.setColor(COLOR_FONDO);
                g2d.fillOval(0, 0, getWidth()-2, getHeight()-2);
                
                g2d.setColor(COLOR_TEXTO);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString("▶", (getWidth() - fm.stringWidth("▶"))/2, 
                             getHeight()/2 + fm.getAscent()/2);
                
                g2d.dispose();
            }
        };
        btnSiguiente.setBorderPainted(false);
        btnSiguiente.setContentAreaFilled(false);
        btnSiguiente.setFocusPainted(false);
        btnSiguiente.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSiguiente.setPreferredSize(new Dimension(40, 40));
        btnSiguiente.addActionListener(e -> cambiarMes(1));
        
        controlMesPanel.add(btnAnterior);
        controlMesPanel.add(mesLabel);
        controlMesPanel.add(btnSiguiente);
        
        navegacionPanel.add(controlMesPanel, BorderLayout.CENTER);
        
        // Calendario
        calendarioPanel = new JPanel(new BorderLayout(0, 15));
        calendarioPanel.setOpaque(false);
        
        JPanel diasSemanaPanel = new JPanel(new GridLayout(1, 7));
        diasSemanaPanel.setOpaque(false);
        
        String[] diasSemana = {"Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb"};
        for (String dia : diasSemana) {
            JLabel diaLabel = new JLabel(dia, SwingConstants.CENTER);
            diaLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            diaLabel.setForeground(COLOR_TEXTO);
            diaLabel.setOpaque(true);
            diaLabel.setBackground(new Color(249, 250, 251));
            diaLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, COLOR_PRIMARIO));
            diasSemanaPanel.add(diaLabel);
        }
        
        JPanel diasPanel = new JPanel(new GridLayout(6, 7, 8, 8));
        diasPanel.setOpaque(false);
        
        // Se rellenará dinámicamente con los días del mes
        for (int i = 0; i < 42; i++) {
            diasPanel.add(createDiaPanel(i));
        }
        
        calendarioPanel.add(diasSemanaPanel, BorderLayout.NORTH);
        calendarioPanel.add(diasPanel, BorderLayout.CENTER);
        
        calendarioView.add(navegacionPanel, BorderLayout.NORTH);
        calendarioView.add(calendarioPanel, BorderLayout.CENTER);
        
        return calendarioView;
    }
    
    private JPanel createDiaPanel(int index) {
        JPanel diaPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fondo con sombra sutil
                g2d.setColor(new Color(0, 0, 0, 10));
                g2d.fillRoundRect(2, 2, getWidth()-4, getHeight()-4, RADIO_BORDES, RADIO_BORDES);
                
                g2d.setColor(COLOR_TARJETA);
                g2d.fillRoundRect(0, 0, getWidth()-2, getHeight()-2, RADIO_BORDES, RADIO_BORDES);
                
                g2d.dispose();
            }
        };
        diaPanel.setLayout(new BorderLayout());
        diaPanel.setOpaque(false);
        diaPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        // El contenido se rellenará dinámicamente
        
        return diaPanel;
    }

    private JPanel createDetalleView() {
        detallePanel = new JPanel(new BorderLayout(0, ESPACIO_COMPONENTES));
        detallePanel.setOpaque(false);
        
        // Encabezado con botón de regreso
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JButton btnVolver = new JButton("← Volver") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
                g2d.setColor(COLOR_SECUNDARIO);
                
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString("← Volver", 0, fm.getAscent());
                
                g2d.dispose();
            }
        };
        btnVolver.setBorderPainted(false);
        btnVolver.setContentAreaFilled(false);
        btnVolver.setFocusPainted(false);
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVolver.setPreferredSize(new Dimension(100, 30));
        btnVolver.addActionListener(e -> volverAVistaAnterior());
        
        headerPanel.add(btnVolver, BorderLayout.WEST);
        
        // El contenido de detalle se llenará dinámicamente
        JPanel contenidoDetalle = new JPanel();
        contenidoDetalle.setLayout(new BoxLayout(contenidoDetalle, BoxLayout.Y_AXIS));
        contenidoDetalle.setOpaque(false);
        
        JScrollPane scrollDetalle = new JScrollPane(contenidoDetalle);
        scrollDetalle.setBorder(null);
        scrollDetalle.setOpaque(false);
        scrollDetalle.getViewport().setOpaque(false);
        scrollDetalle.getVerticalScrollBar().setUnitIncrement(16);
        
        // Personalizar scrollbar
        
        detallePanel.add(headerPanel, BorderLayout.NORTH);
        detallePanel.add(scrollDetalle, BorderLayout.CENTER);
        
        return detallePanel;
    }

    private void cargarDatos() {
        EventoDAO dao = new EventoDAO();
        List<Evento> eventos = EventoDAO.obtenerEventosFuturos();
        
        // Cargar eventos en lista
        eventosPanel.removeAll();
        
        if (eventos.isEmpty()) {
            eventosPanel.add(createEmptyState());
        } else {
            // Agrupar eventos por fecha (para la vista de lista)
            Map<String, List<Evento>> eventosPorFecha = new TreeMap<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            
            for (Evento evento : eventos) {
                String fechaKey = sdf.format(evento.getFechaInicio());
                if (!eventosPorFecha.containsKey(fechaKey)) {
                    eventosPorFecha.put(fechaKey, new ArrayList<>());
                }
                eventosPorFecha.get(fechaKey).add(evento);
            }
            
            // Crear secciones por fecha
            for (Map.Entry<String, List<Evento>> entry : eventosPorFecha.entrySet()) {
                eventosPanel.add(createFechaHeader(entry.getValue().get(0).getFechaInicio()));
                eventosPanel.add(Box.createVerticalStrut(10));
                
                for (Evento evento : entry.getValue()) {
                    eventosPanel.add(createEventoCard(evento));
                    eventosPanel.add(Box.createVerticalStrut(15));
                }
                
                eventosPanel.add(Box.createVerticalStrut(15));
            }
        }
        
        // Cargar eventos en calendario
        cargarCalendario();
        
        eventosPanel.revalidate();
        eventosPanel.repaint();
    }
    
    private JPanel createEmptyState() {
        JPanel emptyPanel = new JPanel();
        emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));
        emptyPanel.setOpaque(false);
        emptyPanel.setBorder(new EmptyBorder(100, 0, 100, 0));
        
        JLabel iconoLabel = new JLabel("📅");
        iconoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        iconoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel mensajeLabel = new JLabel("No hay eventos programados");
        mensajeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        mensajeLabel.setForeground(COLOR_TEXTO);
        mensajeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel submensajeLabel = new JLabel("¡Vuelve pronto para ver nuevas actividades!");
        submensajeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        submensajeLabel.setForeground(COLOR_TEXTO_SECUNDARIO);
        submensajeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        emptyPanel.add(iconoLabel);
        emptyPanel.add(Box.createVerticalStrut(20));
        emptyPanel.add(mensajeLabel);
        emptyPanel.add(Box.createVerticalStrut(10));
        emptyPanel.add(submensajeLabel);
        
        return emptyPanel;
    }
    
    private JPanel createFechaHeader(Date fecha) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        headerPanel.setPreferredSize(new Dimension(0, 40));
        
        // Formato fecha: "Martes, 28 de julio"
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d 'de' MMMM", new Locale("es", "ES"));
        String fechaFormateada = sdf.format(fecha);
        fechaFormateada = Character.toUpperCase(fechaFormateada.charAt(0)) + fechaFormateada.substring(1);
        
        JLabel fechaLabel = new JLabel(fechaFormateada);
        fechaLabel.setFont(new Font("Montserrat", Font.BOLD, 16));
        fechaLabel.setForeground(COLOR_TEXTO);
        
        // Línea decorativa
        JPanel lineaPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(new Color(229, 231, 235));
                g2d.drawLine(0, getHeight()/2, getWidth(), getHeight()/2);
                
                g2d.dispose();
            }
        };
        lineaPanel.setOpaque(false);
        lineaPanel.setPreferredSize(new Dimension(0, 2));
        
        headerPanel.add(fechaLabel, BorderLayout.NORTH);
        headerPanel.add(lineaPanel, BorderLayout.SOUTH);
        
        return headerPanel;
    }
    
    private JPanel createEventoCard(Evento evento) {
        JPanel cardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Sombra
                for (int i = 0; i < 4; i++) {
                    g2d.setColor(new Color(0, 0, 0, 5));
                    g2d.fillRoundRect(i, i, getWidth() - i*2, getHeight() - i*2, RADIO_BORDES, RADIO_BORDES);
                }
                
                // Fondo principal
                g2d.setColor(COLOR_TARJETA);
                g2d.fillRoundRect(0, 0, getWidth()-2, getHeight()-2, RADIO_BORDES, RADIO_BORDES);
                
                // Borde izquierdo con el color del tipo de evento
                Color colorTipo = getColorTipoEvento(evento.getTipoEvento());
                g2d.setColor(colorTipo);
                g2d.fillRect(0, 0, 5, getHeight()-2);
                
                g2d.dispose();
            }
        };
        cardPanel.setLayout(new BorderLayout(15, 0));
        cardPanel.setOpaque(false);
        cardPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        cardPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        cardPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mostrarDetalleEvento(evento);
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                cardPanel.setBorder(new CompoundBorder(
                    new LineBorder(getColorTipoEvento(evento.getTipoEvento()), 1),
                    new EmptyBorder(14, 19, 14, 19)
                ));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                cardPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
            }
        });
        
        // Formato de tiempo: 10:00 - 13:00
        SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm");
        String horaInicio = sdfHora.format(evento.getFechaInicio());
        String horaFin = evento.getFechaFin() != null ? sdfHora.format(evento.getFechaFin()) : "?";
        
        // Icono del tipo
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension(60, 0));
        
        JPanel iconoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Círculo de fondo
                Color colorTipo = getColorTipoEvento(evento.getTipoEvento());
                g2d.setColor(new Color(colorTipo.getRed(), colorTipo.getGreen(), colorTipo.getBlue(), 30));
                g2d.fillOval(0, 0, getWidth(), getHeight());
                
                g2d.dispose();
            }
        };
        iconoPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        iconoPanel.setOpaque(false);
        iconoPanel.setPreferredSize(new Dimension(50, 50));
        
        JLabel iconoTipo = new JLabel(getIconoTipoEvento(evento.getTipoEvento()));
        iconoTipo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        
        iconoPanel.add(iconoTipo);
        
        JLabel horaLabel = new JLabel(horaInicio + " - " + horaFin, SwingConstants.CENTER);
        horaLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        horaLabel.setForeground(COLOR_TEXTO_SECUNDARIO);
        
        leftPanel.add(iconoPanel, BorderLayout.CENTER);
        leftPanel.add(horaLabel, BorderLayout.SOUTH);
        
        // Información del evento
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        
        JLabel tituloLabel = new JLabel(evento.getTitulo());
        tituloLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tituloLabel.setForeground(COLOR_TEXTO);
        tituloLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel ubicacionLabel = new JLabel("📍 " + (evento.getUbicacion() != null ? evento.getUbicacion() : "No especificada"));
        ubicacionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        ubicacionLabel.setForeground(COLOR_TEXTO_SECUNDARIO);
        ubicacionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel tipoLabel = new JLabel(getIconoTipoEvento(evento.getTipoEvento()) + " " + formatearTipoEvento(evento.getTipoEvento()));
        tipoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tipoLabel.setForeground(getColorTipoEvento(evento.getTipoEvento()));
        tipoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        infoPanel.add(tituloLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(ubicacionLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(tipoLabel);
        
        // Botón de inscripción
        JButton btnInscripcion = createActionButton("Inscribirse", COLOR_PRIMARIO, COLOR_SECUNDARIO);
        btnInscripcion.addActionListener(e -> inscribirseEvento(evento));
        btnInscripcion.setPreferredSize(new Dimension(120, 35));
        
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);
        rightPanel.setPreferredSize(new Dimension(120, 0));
        rightPanel.add(btnInscripcion, BorderLayout.CENTER);
        
        cardPanel.add(leftPanel, BorderLayout.WEST);
        cardPanel.add(infoPanel, BorderLayout.CENTER);
        cardPanel.add(rightPanel, BorderLayout.EAST);
        
        return cardPanel;
    }
    
    private JButton createActionButton(String texto, Color colorInicio, Color colorFin) {
        JButton button = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Degradado de fondo
                GradientPaint gradient = new GradientPaint(
                    0, 0, colorInicio, 
                    getWidth(), getHeight(), colorFin
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), RADIO_BORDES, RADIO_BORDES);
                
                // Texto con sombra sutil
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(texto, (getWidth() - fm.stringWidth(texto)) / 2 + 1, 
                             (getHeight() - fm.getHeight()) / 2 + fm.getAscent() + 1);
                
                g2d.setColor(Color.WHITE);
                g2d.drawString(texto, (getWidth() - fm.stringWidth(texto)) / 2, 
                             (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
                
                g2d.dispose();
            }
            
            @Override
            protected void paintBorder(Graphics g) {
                // Sin borde
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Efecto hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.repaint();
            }
        });
        
        return button;
    }
    
    private void cargarCalendario() {
        // Configurar el calendario para el mes actual
        Calendar cal = Calendar.getInstance();
        cal.set(añoActual, mesActual, 1);
        
        int primerDiaSemana = cal.get(Calendar.DAY_OF_WEEK) - 1;
        int ultimoDiaMes = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        
        // Obtener los eventos del mes
        EventoDAO dao = new EventoDAO();
        
        // Inicio del mes
        Calendar inicioMes = Calendar.getInstance();
        inicioMes.set(añoActual, mesActual, 1, 0, 0, 0);
        inicioMes.set(Calendar.MILLISECOND, 0);
        
        // Fin del mes
        Calendar finMes = Calendar.getInstance();
        finMes.set(añoActual, mesActual, ultimoDiaMes, 23, 59, 59);
        finMes.set(Calendar.MILLISECOND, 999);
        
        List<Evento> eventosDelMes = EventoDAO.obtenerPorRangoFechas(inicioMes.getTime(), finMes.getTime());
        
        // Organizar eventos por día
        this.eventosDelMes = new HashMap<>();
        for (Evento evento : eventosDelMes) {
            Calendar eventoCal = Calendar.getInstance();
            eventoCal.setTime(evento.getFechaInicio());
            int diaEvento = eventoCal.get(Calendar.DAY_OF_MONTH);
            
            if (!this.eventosDelMes.containsKey(diaEvento)) {
                this.eventosDelMes.put(diaEvento, new ArrayList<>());
            }
            this.eventosDelMes.get(diaEvento).add(evento);
        }
        
        // Actualizar el calendario visual
        Container panelDias = (Container)calendarioPanel.getComponent(1);
        Component[] dias = panelDias.getComponents();
        
        for (int i = 0; i < dias.length; i++) {
            JPanel diaPanel = (JPanel) dias[i];
            diaPanel.removeAll();
            
            if (i >= primerDiaSemana && i < primerDiaSemana + ultimoDiaMes) {
                int dia = i - primerDiaSemana + 1;
                
                // Configurar el panel de día
                diaPanel.setLayout(new BorderLayout());
                
                // Número del día
                JPanel headerDia = new JPanel(new BorderLayout());
                headerDia.setOpaque(false);
                
                JLabel numeroLabel = new JLabel(Integer.toString(dia));
                numeroLabel.setFont(new Font("Montserrat", Font.BOLD, 14));
                
                // Verificar si es hoy
                boolean esHoy = esFechaHoy(añoActual, mesActual, dia);
                if (esHoy) {
                    numeroLabel.setForeground(Color.WHITE);
                    numeroLabel.setOpaque(true);
                    numeroLabel.setBackground(COLOR_PRIMARIO);
                    numeroLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    numeroLabel.setBorder(new EmptyBorder(2, 8, 2, 8));
                } else {
                    numeroLabel.setForeground(COLOR_TEXTO);
                }
                
                headerDia.add(numeroLabel, BorderLayout.WEST);
                
                // Eventos del día
                JPanel eventosContainer = new JPanel();
                eventosContainer.setLayout(new BoxLayout(eventosContainer, BoxLayout.Y_AXIS));
                eventosContainer.setOpaque(false);
                
                // Agregar eventos
                if (this.eventosDelMes.containsKey(dia)) {
                    List<Evento> eventosDelDia = this.eventosDelMes.get(dia);
                    for (int j = 0; j < Math.min(eventosDelDia.size(), 3); j++) {
                        eventosContainer.add(createEventoDot(eventosDelDia.get(j)));
                        if (j < Math.min(eventosDelDia.size(), 3) - 1) {
                            eventosContainer.add(Box.createVerticalStrut(2));
                        }
                    }
                    
                    if (eventosDelDia.size() > 3) {
                        JLabel masLabel = new JLabel("+" + (eventosDelDia.size() - 3) + " más");
                        masLabel.setFont(new Font("Segoe UI", Font.ITALIC, 10));
                        masLabel.setForeground(COLOR_TEXTO_SECUNDARIO);
                        eventosContainer.add(masLabel);
                    }
                    
                    // Hacer que el día sea clickeable para ver los eventos
                    diaPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    diaPanel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            mostrarEventosDia(dia);
                        }
                    });
                }
                
                diaPanel.add(headerDia, BorderLayout.NORTH);
                diaPanel.add(eventosContainer, BorderLayout.CENTER);
                
            } else {
                // Día fuera del mes
                diaPanel.setBackground(new Color(248, 250, 252));
            }
        }
        
        mesLabel.setText(getNombreMes(mesActual) + " " + añoActual);
        calendarioPanel.revalidate();
        calendarioPanel.repaint();
    }
    
    private boolean esFechaHoy(int año, int mes, int dia) {
        Calendar hoy = Calendar.getInstance();
        return hoy.get(Calendar.YEAR) == año && 
               hoy.get(Calendar.MONTH) == mes && 
               hoy.get(Calendar.DAY_OF_MONTH) == dia;
    }
    
    private JPanel createEventoDot(Evento evento) {
        JPanel dotPanel = new JPanel(new BorderLayout(5, 0));
        dotPanel.setOpaque(false);
        dotPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 15));
        
        // Punto de color según el tipo de evento
        JPanel dot = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(getColorTipoEvento(evento.getTipoEvento()));
                g2d.fillOval(0, 0, getWidth(), getHeight());
                
                g2d.dispose();
            }
        };
        dot.setPreferredSize(new Dimension(8, 8));
        dot.setOpaque(false);
        
        // Título recortado
        String tituloCorto = evento.getTitulo().length() > 15 ? 
            evento.getTitulo().substring(0, 15) + "..." : evento.getTitulo();
            
        JLabel tituloLabel = new JLabel(tituloCorto);
        tituloLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        tituloLabel.setForeground(COLOR_TEXTO_SECUNDARIO);
        
        dotPanel.add(dot, BorderLayout.WEST);
        dotPanel.add(tituloLabel, BorderLayout.CENTER);
        
        return dotPanel;
    }
    
    private void mostrarDetalleEvento(Evento evento) {
        eventoSeleccionado = evento;
        
        // Limpiar panel de detalle
        Component[] components = detallePanel.getComponents();
        JPanel contenidoPanel = (JPanel) ((JScrollPane)components[1]).getViewport().getView();
        contenidoPanel.removeAll();
        
        // Crear detalle del evento con diseño moderno y atractivo
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fondo con degradado
                GradientPaint gradient = new GradientPaint(
                    0, 0, getColorTipoEvento(evento.getTipoEvento()).brighter(), 
                    getWidth(), getHeight(), getColorTipoEvento(evento.getTipoEvento())
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), RADIO_BORDES, RADIO_BORDES);
                
                // Patrón decorativo
                g2d.setColor(new Color(255, 255, 255, 20));
                int size = 150;
                g2d.fillOval(-size/2, -size/2, size, size);
                g2d.fillOval(getWidth() - size/2, getHeight() - size/2, size, size);
                
                g2d.dispose();
            }
        };
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(new EmptyBorder(30, 35, 30, 35));
        headerPanel.setOpaque(false);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        
        JLabel tipoLabel = new JLabel(getIconoTipoEvento(evento.getTipoEvento()) + " " + 
                                   formatearTipoEvento(evento.getTipoEvento()));
        tipoLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tipoLabel.setForeground(Color.WHITE);
        tipoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel tituloLabel = new JLabel(evento.getTitulo());
        tituloLabel.setFont(new Font("Montserrat", Font.BOLD, 26));
        tituloLabel.setForeground(Color.WHITE);
        tituloLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        headerPanel.add(tipoLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(tituloLabel);
        
        // Panel de información del evento
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(new EmptyBorder(30, 35, 30, 35));
        infoPanel.setBackground(COLOR_TARJETA);
        
        // Tarjeta de información con efecto de elevación
        JPanel tarjetaInfo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Sombra
                for (int i = 0; i < 5; i++) {
                    g2d.setColor(new Color(0, 0, 0, 5 - i));
                    g2d.fillRoundRect(i, i, getWidth() - i*2, getHeight() - i*2, RADIO_BORDES, RADIO_BORDES);
                }
                
                g2d.setColor(COLOR_TARJETA);
                g2d.fillRoundRect(0, 0, getWidth()-2, getHeight()-2, RADIO_BORDES, RADIO_BORDES);
                
                g2d.dispose();
            }
        };
        tarjetaInfo.setLayout(new GridLayout(0, 2, 20, 15));
        tarjetaInfo.setOpaque(false);
        tarjetaInfo.setBorder(new EmptyBorder(25, 30, 25, 30));
        
        // Añadir detalles del evento
        SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm");
        
        addInfoItem(tarjetaInfo, "Fecha:", sdfFecha.format(evento.getFechaInicio()), "📅");
        
        String horaInicio = sdfHora.format(evento.getFechaInicio());
        String horaFin = evento.getFechaFin() != null ? sdfHora.format(evento.getFechaFin()) : "No especificada";
        addInfoItem(tarjetaInfo, "Hora:", horaInicio + " - " + horaFin, "🕒");
        
        addInfoItem(tarjetaInfo, "Ubicación:", evento.getUbicacion() != null ? evento.getUbicacion() : "No especificada", "📍");
        
        addInfoItem(tarjetaInfo, "Organizador:", evento.getOrganizador() != null ? 
                              evento.getOrganizador().getNombre() : "No especificado", "👤");
        
        // Descripción del evento
        JPanel descPanel = new JPanel();
        descPanel.setLayout(new BoxLayout(descPanel, BoxLayout.Y_AXIS));
        descPanel.setOpaque(false);
        descPanel.setBorder(new EmptyBorder(20, 0, 20, 0));
        
        JLabel descTitulo = new JLabel("Descripción");
        descTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        descTitulo.setForeground(COLOR_TEXTO);
        descTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JSeparator separador = new JSeparator();
        separador.setForeground(new Color(229, 231, 235));
        separador.setBackground(COLOR_FONDO);
        separador.setMaximumSize(new Dimension(100, 2));
        separador.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextArea descArea = new JTextArea();
        descArea.setText(evento.getDescripcion() != null ? evento.getDescripcion() : "No hay descripción disponible para este evento.");
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descArea.setForeground(COLOR_TEXTO);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setOpaque(false);
        descArea.setBorder(new EmptyBorder(15, 0, 15, 0));
        descArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        descPanel.add(descTitulo);
        descPanel.add(Box.createVerticalStrut(5));
        descPanel.add(separador);
        descPanel.add(Box.createVerticalStrut(10));
        descPanel.add(descArea);
        
        // Botón de inscripción
        JPanel botonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        botonPanel.setOpaque(false);
        botonPanel.setBorder(new EmptyBorder(10, 0, 30, 0));
        
        btnInscribirse = createActionButton("Inscribirse al Evento", COLOR_PRIMARIO, COLOR_SECUNDARIO);
        btnInscribirse.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnInscribirse.setPreferredSize(new Dimension(250, 45));
        btnInscribirse.addActionListener(e -> inscribirseEvento(evento));
        
        // Verificar si el usuario ya está inscrito
        if (evento.getParticipantes() != null && evento.getParticipantes().contains(usuarioActual)) {
            btnInscribirse.setText("Cancelar Inscripción");
        } else {
            btnInscribirse.setText("Inscribirse al Evento");
        }
        
        botonPanel.add(btnInscribirse);
        
        // Añadir todo al contenido
        infoPanel.add(tarjetaInfo);
        infoPanel.add(Box.createVerticalStrut(20));
        infoPanel.add(descPanel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(botonPanel);
        
        // Panel principal con efecto de tarjeta
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(COLOR_TARJETA);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), RADIO_BORDES, RADIO_BORDES);
                
                g2d.dispose();
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setOpaque(false);
        mainPanel.add(infoPanel, BorderLayout.CENTER);
        
        // Añadir componentes al panel de detalle
        contenidoPanel.add(headerPanel);
        contenidoPanel.add(Box.createVerticalStrut(20));
        contenidoPanel.add(mainPanel);
        
        contenidoPanel.revalidate();
        contenidoPanel.repaint();
        
        // Cambiar a vista de detalle
        cardLayout.show(cardContainer, "detalle");
    }
    
        private void addInfoItem(JPanel panel, String titulo, String valor, String icono) {
        JPanel itemPanel = new JPanel(new BorderLayout(10, 0));
        itemPanel.setOpaque(false);
        
        JLabel iconoLabel = new JLabel(icono);
        iconoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        iconoLabel.setPreferredSize(new Dimension(25, 25));
        
        JLabel tituloLabel = new JLabel(titulo);
        tituloLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tituloLabel.setForeground(COLOR_TEXTO);
        
        JPanel labelPanel = new JPanel(new BorderLayout());
        labelPanel.setOpaque(false);
        labelPanel.add(iconoLabel, BorderLayout.WEST);
        labelPanel.add(tituloLabel, BorderLayout.CENTER);
        
        JLabel valorLabel = new JLabel(valor);
        valorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        valorLabel.setForeground(COLOR_TEXTO_SECUNDARIO);
        
        itemPanel.add(labelPanel, BorderLayout.WEST);
        itemPanel.add(valorLabel, BorderLayout.CENTER);
        
        panel.add(itemPanel);
    }
    
    private void mostrarEventosDia(int dia) {
        // Mostrar los eventos de un día específico
        if (eventosDelMes.containsKey(dia)) {
            List<Evento> eventos = eventosDelMes.get(dia);
            
            // Limpiar panel de eventos
            eventosPanel.removeAll();
            
            // Agregar cabecera
            Calendar cal = Calendar.getInstance();
            cal.set(añoActual, mesActual, dia);
            
            eventosPanel.add(createFechaHeader(cal.getTime()));
            eventosPanel.add(Box.createVerticalStrut(10));
            
            // Agregar eventos
            for (Evento evento : eventos) {
                eventosPanel.add(createEventoCard(evento));
                eventosPanel.add(Box.createVerticalStrut(15));
            }
            
            eventosPanel.revalidate();
            eventosPanel.repaint();
            
            // Volver a vista de lista
            cardLayout.show(cardContainer, "lista");
            vistaActual = "lista";
        }
    }
    
    private String getNombreMes(int mes) {
        String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", 
                         "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        return meses[mes];
    }
    
    private String getIconoTipoEvento(Evento.TipoEvento tipo) {
        if (tipo == null) return "📋";
        switch (tipo) {
            case JORNADA_ADOPCION: return "🏠";
            case CAMPAÑA_VACUNACION: return "💉";
            case CAPACITACION: return "🎓";
            case RECAUDACION: return "💰";
            case VOLUNTARIADO: return "🤝";
            default: return "📅";
        }
    }
    
    private String formatearTipoEvento(Evento.TipoEvento tipo) {
        if (tipo == null) return "Otro";
        switch (tipo) {
            case JORNADA_ADOPCION: return "Jornada de Adopción";
            case CAMPAÑA_VACUNACION: return "Campaña de Vacunación";
            case CAPACITACION: return "Capacitación";
            case RECAUDACION: return "Recaudación de Fondos";
            case VOLUNTARIADO: return "Voluntariado";
            default: return "Otro";
        }
    }
    
    private Color getColorTipoEvento(Evento.TipoEvento tipo) {
        if (tipo == null) return COLOR_TEXTO_SECUNDARIO;
        switch (tipo) {
            case JORNADA_ADOPCION: return new Color(16, 185, 129);
            case CAMPAÑA_VACUNACION: return new Color(59, 130, 246);
            case CAPACITACION: return new Color(139, 92, 246);
            case RECAUDACION: return new Color(245, 158, 11);
            case VOLUNTARIADO: return new Color(239, 68, 68);
            default: return COLOR_TEXTO_SECUNDARIO;
        }
    }
    
    private void cambiarVista(String vista) {
        if (!vistaActual.equals(vista)) {
            vistaActual = vista;
            cardLayout.show(cardContainer, vista);
            
            // Actualizar el contenido según la vista
            if (vista.equals("calendario")) {
                cargarCalendario();
            }
        }
    }
    
    private void cambiarMes(int incremento) {
        mesActual += incremento;
        
        if (mesActual < 0) {
            mesActual = 11;
            añoActual--;
        } else if (mesActual > 11) {
            mesActual = 0;
            añoActual++;
        }
        
        cargarCalendario();
    }
    
    private void volverAVistaAnterior() {
        cardLayout.show(cardContainer, vistaActual);
    }
    
    private void inscribirseEvento(Evento evento) {
        EventoDAO dao = new EventoDAO();
        boolean yaInscrito = evento.getParticipantes() != null && evento.getParticipantes().contains(usuarioActual);
        
        if (yaInscrito) {
            // Cancelar inscripción
            if (dao.cancelarInscripcion(evento.getIdEvento(), usuarioActual)) {
                JOptionPane.showMessageDialog(this, 
                                           "Has cancelado tu inscripción al evento.",
                                           "Inscripción Cancelada", 
                                           JOptionPane.INFORMATION_MESSAGE);
                // Actualizar botón
                btnInscribirse.setText("Inscribirse al Evento");
            } else {
                JOptionPane.showMessageDialog(this, 
                                           "Error al cancelar la inscripción.",
                                           "Error", 
                                           JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Inscribirse
            if (dao.inscribirUsuario(evento.getIdEvento(), usuarioActual)) {
                JOptionPane.showMessageDialog(this, 
                                           "¡Te has inscrito exitosamente al evento!",
                                           "Inscripción Exitosa", 
                                           JOptionPane.INFORMATION_MESSAGE);
                // Actualizar botón
                btnInscribirse.setText("Cancelar Inscripción");
            } else {
                JOptionPane.showMessageDialog(this, 
                                           "Error al inscribirse al evento.",
                                           "Error", 
                                           JOptionPane.ERROR_MESSAGE);
            }
        }
        
        // Actualizar datos
        cargarDatos();
    }
    
    private void filtrarEventosPorTipo(String tipo) {
        EventoDAO dao = new EventoDAO();
        List<Evento> eventos;
        
        if (tipo.equals("Todos")) {
            eventos = EventoDAO.obtenerEventosFuturos();
        } else {
            Evento.TipoEvento tipoEvento = null;
            for (Evento.TipoEvento t : Evento.TipoEvento.values()) {
                if (formatearTipoEvento(t).equals(tipo)) {
                    tipoEvento = t;
                    break;
                }
            }
            
            if (tipoEvento != null) {
                eventos = dao.obtenerPorTipo(tipoEvento);
            } else {
                eventos = EventoDAO.obtenerEventosFuturos();
            }
        }
        
        // Actualizar vista
        eventosPanel.removeAll();
        
        if (eventos.isEmpty()) {
            eventosPanel.add(createEmptyState());
        } else {
            // Agrupar eventos por fecha
            Map<String, List<Evento>> eventosPorFecha = new TreeMap<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            
            for (Evento evento : eventos) {
                String fechaKey = sdf.format(evento.getFechaInicio());
                if (!eventosPorFecha.containsKey(fechaKey)) {
                    eventosPorFecha.put(fechaKey, new ArrayList<>());
                }
                eventosPorFecha.get(fechaKey).add(evento);
            }
            
            // Crear secciones por fecha
            for (Map.Entry<String, List<Evento>> entry : eventosPorFecha.entrySet()) {
                eventosPanel.add(createFechaHeader(entry.getValue().get(0).getFechaInicio()));
                eventosPanel.add(Box.createVerticalStrut(10));
                
                for (Evento evento : entry.getValue()) {
                    eventosPanel.add(createEventoCard(evento));
                    eventosPanel.add(Box.createVerticalStrut(15));
                }
                
                eventosPanel.add(Box.createVerticalStrut(15));
            }
        }
        
        eventosPanel.revalidate();
        eventosPanel.repaint();
    }
    
    public void actualizarDatos() {
        cargarDatos();
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
            
            PanelEventos dashboard = new PanelEventos(usuario);
            frame.add(dashboard);
            frame.setVisible(true);
        });
    }
}