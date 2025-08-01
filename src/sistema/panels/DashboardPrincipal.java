package sistema.panels;

import conexiondb.core.ConexionBD;
import sistema.modelos.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import sistema.dao.ActividadDAO;
import sistema.dao.AdopcionDAO;
import sistema.dao.AnimalDAO;
import sistema.dao.DonacionDAO;
import sistema.dao.EventoDAO;
import sistema.modelos.Actividad;
import sistema.modelos.Adopcion;
import static sistema.modelos.Adopcion.EstadoAdopcion.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import sistema.dao.HistorialMedicoDAO;
import sistema.dao.InventarioDAO;
import sistema.modelos.Animal;
import sistema.modelos.Evento;
import sistema.modelos.HistorialMedico;
import sistema.modelos.Inventario;

public class DashboardPrincipal extends JPanel {
    private Usuario usuarioActual;
    private JLabel lblFechaHora;
    private Timer timerFecha;
    
    // Colores personalizados
    private final Color COLOR_PRIMARIO = new Color(46, 125, 50);
    private final Color COLOR_SECUNDARIO = new Color(76, 175, 80);
    private final Color COLOR_ACENTO = new Color(255, 193, 7);
    private final Color COLOR_EXITO = new Color(76, 175, 80);
    private final Color COLOR_ADVERTENCIA = new Color(255, 152, 0);
    private final Color COLOR_PELIGRO = new Color(244, 67, 54);
    private final Color COLOR_INFO = new Color(33, 150, 243);
    private final Color COLOR_FONDO = new Color(249, 249, 249);

    public DashboardPrincipal(Usuario usuario) {
        this.usuarioActual = usuario;
        initializePanel();
        setupUI();
        startTimeUpdater();
    }

    private void initializePanel() {
        setLayout(new BorderLayout());
        setBackground(COLOR_FONDO);
        setBorder(new EmptyBorder(20, 20, 20, 20));
    }

    private void setupUI() {
        // Header del dashboard
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Contenido principal
        JScrollPane scrollPane = new JScrollPane(createMainContent());
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 25, 0));

        // Panel izquierdo con saludo
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setOpaque(false);
        
        JLabel welcomeLabel = new JLabel("¡Bienvenid@ de vuelta, " + usuarioActual.getNombre() + "!");
        welcomeLabel.setFont(new Font("Poppins", Font.BOLD, 24));
        welcomeLabel.setForeground(COLOR_PRIMARIO);
        
        JLabel subtitleLabel = new JLabel("Aquí tienes un resumen de las actividades del refugio NuevoHogar");
        subtitleLabel.setFont(new Font("Poppins", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(102, 102, 102));
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(welcomeLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(subtitleLabel);
        
        leftPanel.add(textPanel);

        // Panel derecho con fecha y hora
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        
        lblFechaHora = new JLabel();
        lblFechaHora.setFont(new Font("Poppins", Font.BOLD, 16));
        lblFechaHora.setForeground(COLOR_PRIMARIO);
        
        rightPanel.add(lblFechaHora);

        header.add(leftPanel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }

    private JPanel createMainContent() {
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setOpaque(false);

        // Panel de estadísticas principales
        JPanel statsPanel = createStatsPanel();
        mainContent.add(statsPanel, BorderLayout.NORTH);

        // Panel de widgets
        JPanel widgetsPanel = createWidgetsPanel();
        mainContent.add(widgetsPanel, BorderLayout.CENTER);

        return mainContent;
    }

    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.setPreferredSize(new Dimension(0, 150));
        statsPanel.setBorder(new EmptyBorder(0, 0, 30, 0));
        
        int animalesSinDueno = AnimalDAO.obtenerAnimalesDisponibles().size();
        int adopcionesAprobadas = AdopcionDAO.obtenerPorEstado(Adopcion.EstadoAdopcion.APROBADA).size();
        LocalDate first = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        LocalDate last = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        double totalDonacionesMesActual = DonacionDAO.calcularTotalDonacionesMonetarias(Date.from(first.atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(last.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        int eventosProximos = EventoDAO.obtenerEventosFuturos().size();

        // Estadísticas principales (aquí conectarías con tu base de datos)
        statsPanel.add(createStatCard("Mascotas sin dueño :(", String.valueOf(animalesSinDueno), "🐕", COLOR_INFO, "Listos para adopción"));
        statsPanel.add(createStatCard("Adopciones Este Mes", String.valueOf(adopcionesAprobadas), "❤️", COLOR_EXITO, "Nuevos hogares felices"));
        statsPanel.add(createStatCard("Donaciones Recibidas", String.valueOf(totalDonacionesMesActual), "💰", COLOR_ACENTO, "Este mes"));
        statsPanel.add(createStatCard("Eventos Programados", String.valueOf(eventosProximos), "📅", COLOR_ADVERTENCIA, "Próximos eventos"));

        return statsPanel;
    }

    private JPanel createStatCard(String titulo, String valor, String icono, Color color, String descripcion) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Sombra suave
                g2.setColor(new Color(0, 0, 0, 15));
                g2.fillRoundRect(3, 3, getWidth()-3, getHeight()-3, 20, 20);
                
                // Fondo del card
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth()-3, getHeight()-3, 20, 20);
                
                // Acento de color en la parte superior
                g2.setColor(color);
                g2.fillRoundRect(0, 0, getWidth()-3, 8, 20, 20);
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(25, 20, 20, 20));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Panel superior con icono y valor
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        
        JLabel iconLabel = new JLabel(icono);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 32));
        iconLabel.setHorizontalAlignment(SwingConstants.LEFT);

        JLabel valueLabel = new JLabel(valor);
        valueLabel.setFont(new Font("Poppins", Font.BOLD, 28));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        topPanel.add(iconLabel, BorderLayout.WEST);
        topPanel.add(valueLabel, BorderLayout.EAST);

        // Panel inferior con título y descripción
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        
        JLabel titleLabel = new JLabel(titulo);
        titleLabel.setFont(new Font("Poppins", Font.BOLD, 14));
        titleLabel.setForeground(new Color(66, 66, 66));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel descLabel = new JLabel(descripcion);
        descLabel.setFont(new Font("Poppins", Font.PLAIN, 11));
        descLabel.setForeground(new Color(130, 130, 130));
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        bottomPanel.add(titleLabel);
        bottomPanel.add(Box.createVerticalStrut(1));
        bottomPanel.add(descLabel);

        card.add(topPanel, BorderLayout.NORTH);
        card.add(bottomPanel, BorderLayout.SOUTH);

        // Efecto hover
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.repaint();
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.repaint();
            }
        });

        return card;
    }

    private JPanel createWidgetsPanel() {
        JPanel widgetsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        widgetsPanel.setOpaque(false);

        // Crear los 4 widgets principales
        widgetsPanel.add(createActivityWidget());
        widgetsPanel.add(createQuickActionsWidget());
        widgetsPanel.add(createRecentAnimalsWidget());
        widgetsPanel.add(createAlertsWidget());

        return widgetsPanel;
    }

    private JPanel createActivityWidget() {
        JPanel widget = createWidgetPanel("📊 Actividad Reciente", COLOR_INFO);
        
        List<Actividad> actividades = obtenerActividadesRecientes();
        
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);
        listPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        if (actividades.isEmpty()) {
            JLabel noActivityLabel = new JLabel("No hay actividades recientes");
            noActivityLabel.setFont(new Font("Poppins", Font.ITALIC, 14));
            noActivityLabel.setForeground(new Color(150, 150, 150));
            listPanel.add(noActivityLabel);
        } else {
            for (int i = 0; i < Math.min(actividades.size(), 6); i++) {
                Actividad actividad = actividades.get(i);
                JPanel itemPanel = new JPanel(new BorderLayout());
                itemPanel.setOpaque(false);
                itemPanel.setBorder(new EmptyBorder(8, 0, 8, 0));
                
                String iconoActividad = obtenerIconoActividad(actividad.getTipoActividad());
                String textoActividad = String.format("%s %s - %s", 
                    iconoActividad, 
                    actividad.getAnimal().getNombre(),
                    actividad.getTipoActividad().name());
                
                JLabel actLabel = new JLabel(textoActividad);
                actLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
                actLabel.setForeground(new Color(66, 66, 66));
                
                // Calcular tiempo transcurrido
                String tiempoTranscurrido = calcularTiempoTranscurrido(actividad.getFecha());
                JLabel timeLabel = new JLabel(tiempoTranscurrido);
                timeLabel.setFont(new Font("Poppins", Font.PLAIN, 12));
                timeLabel.setForeground(new Color(150, 150, 150));
                
                itemPanel.add(actLabel, BorderLayout.CENTER);
                itemPanel.add(timeLabel, BorderLayout.EAST);
                
                listPanel.add(itemPanel);
                
                if (i < Math.min(actividades.size(), 6) - 1) {
                    JSeparator separator = new JSeparator();
                    separator.setForeground(new Color(240, 240, 240));
                    listPanel.add(separator);
                }
            }
        }
        
        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        
        widget.add(scrollPane, BorderLayout.CENTER);
        return widget;
    }
    
    private List<Actividad> obtenerActividadesRecientes() {
        try {
            return ConexionBD.transactionWithResult(tx -> {
                String hql = "SELECT a FROM Actividad a " +
                            "JOIN FETCH a.animal animal " +
                            "LEFT JOIN FETCH a.responsable " +
                            "ORDER BY a.fecha DESC";

                return tx.getSession()
                    .createQuery(hql, Actividad.class)
                    .setMaxResults(10)
                    .getResultList();
            });
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    private String obtenerIconoActividad(Actividad.TipoActividad tipo) {
        switch (tipo) {
            case PASEO: return "🚶";
            case JUEGO: return "🎾";
            case ENTRENAMIENTO: return "🎯";
            case SOCIALIZACIÓN: return "👥";
            case BAÑO: return "🛁";
            case CEPILLADO: return "🪮";
            default: return "📝";
        }
    }

    private String calcularTiempoTranscurrido(LocalDateTime fecha) {
        if (fecha == null) return "Fecha desconocida";

        LocalDateTime ahora = LocalDateTime.now();
        Duration duracion = Duration.between(fecha, ahora);

        long minutos = duracion.toMinutes();
        long horas = duracion.toHours();
        long dias = duracion.toDays();

        if (dias > 0) {
            return "Hace " + dias + " día" + (dias > 1 ? "s" : "");
        } else if (horas > 0) {
            return "Hace " + horas + " hora" + (horas > 1 ? "s" : "");
        } else {
            return "Hace " + Math.max(1, minutos) + " min";
        }
    }

    private JPanel createQuickActionsWidget() {
        JPanel widget = createWidgetPanel("⚡ Acciones Rápidas", COLOR_EXITO);
        
        JPanel actionsPanel = new JPanel(new GridLayout(4, 1, 0, 12));
        actionsPanel.setOpaque(false);
        actionsPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JButton btnNewAnimal = createActionButton("Registrar Nuevo Animal", COLOR_SECUNDARIO);
        JButton btnNewAdoption = createActionButton("Procesar Adopción", COLOR_EXITO);
        JButton btnNewDonation = createActionButton("Registrar Donación", COLOR_ACENTO);
        JButton btnScheduleEvent = createActionButton("Programar Evento", COLOR_ADVERTENCIA);
        
        actionsPanel.add(btnNewAnimal);
        actionsPanel.add(btnNewAdoption);
        actionsPanel.add(btnNewDonation);
        actionsPanel.add(btnScheduleEvent);
        
        widget.add(actionsPanel, BorderLayout.CENTER);
        return widget;
    }

    private JPanel createRecentAnimalsWidget() {
        JPanel widget = createWidgetPanel("🐾 Últimos Animales Registrados", COLOR_ADVERTENCIA);
        
        // Obtener animales reales de la base de datos
        List<Animal> animalesRecientes = obtenerAnimalesRecientes();
        
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);
        listPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        if (animalesRecientes.isEmpty()) {
            JLabel noAnimalsLabel = new JLabel("No hay animales registrados recientemente");
            noAnimalsLabel.setFont(new Font("Poppins", Font.ITALIC, 14));
            noAnimalsLabel.setForeground(new Color(150, 150, 150));
            listPanel.add(noAnimalsLabel);
        } else {
            for (int i = 0; i < Math.min(animalesRecientes.size(), 5); i++) {
                Animal animal = animalesRecientes.get(i);
                JPanel animalPanel = new JPanel(new BorderLayout());
                animalPanel.setOpaque(false);
                animalPanel.setBorder(new EmptyBorder(8, 0, 8, 0));
                
                JPanel infoPanel = new JPanel();
                infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
                infoPanel.setOpaque(false);
                
                JLabel nameLabel = new JLabel(animal.getNombre());
                nameLabel.setFont(new Font("Poppins", Font.BOLD, 14));
                nameLabel.setForeground(new Color(66, 66, 66));
                
                String detalles = String.format("%s • %s meses • %s", 
                    animal.getRaza() != null ? animal.getRaza() : animal.getEspecie(),
                    animal.getEdadAproximada() != null ? animal.getEdadAproximada().toString() : "?",
                    animal.getSexo() != null ? animal.getSexo().name() : "desconocido"
                );
                
                JLabel detailsLabel = new JLabel(detalles);
                detailsLabel.setFont(new Font("Poppins", Font.PLAIN, 13));
                detailsLabel.setForeground(new Color(120, 120, 120));
                
                infoPanel.add(nameLabel);
                infoPanel.add(detailsLabel);
                
                // Estado del animal
                String estadoTexto = obtenerEstadoTexto(animal.getEstado());
                JLabel statusLabel = new JLabel(estadoTexto);
                statusLabel.setFont(new Font("Poppins", Font.BOLD, 13));
                Color statusColor = obtenerColorEstado(animal.getEstado());
                statusLabel.setForeground(statusColor);
                
                animalPanel.add(infoPanel, BorderLayout.WEST);
                animalPanel.add(statusLabel, BorderLayout.EAST);
                
                listPanel.add(animalPanel);
                
                if (i < Math.min(animalesRecientes.size(), 5) - 1) {
                    JSeparator separator = new JSeparator();
                    separator.setForeground(new Color(240, 240, 240));
                    listPanel.add(separator);
                }
            }
        }
        
        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        
        widget.add(scrollPane, BorderLayout.CENTER);
        return widget;
    }
    
    private List<Animal> obtenerAnimalesRecientes() {
        try {
            return ConexionBD.transactionWithResult(tx -> {
                String hql = "SELECT a FROM Animal a " +
                            "LEFT JOIN FETCH a.responsable " +
                            "ORDER BY a.fechaIngreso DESC";

                return tx.getSession()
                    .createQuery(hql, Animal.class)
                    .setMaxResults(5)
                    .getResultList();
            });
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private String obtenerEstadoTexto(Animal.EstadoAnimal estado) {
        if (estado == null) return "Desconocido";
        
        switch (estado) {
            case DISPONIBLE: return "Disponible";
            case ADOPTADO: return "Adoptado";
            case EN_PROCESO: return "En proceso";
            case FALLECIDO: return "Fallecido";
            default: return estado.toString();
        }
    }

    private Color obtenerColorEstado(Animal.EstadoAnimal estado) {
        if (estado == null) return new Color(150, 150, 150);
        
        switch (estado) {
            case DISPONIBLE: return COLOR_EXITO;
            case ADOPTADO: return COLOR_INFO;
            case EN_PROCESO: return COLOR_ADVERTENCIA;
            case FALLECIDO: return COLOR_PELIGRO;
            default: return new Color(150, 150, 150);
        }
    }

    private JPanel createAlertsWidget() {
        JPanel widget = createWidgetPanel("⚠️ Alertas y Recordatorios", COLOR_PELIGRO);
        
        // Obtener alertas reales del sistema
        List<String[]> alertas = obtenerAlertas();
        
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);
        listPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        if (alertas.isEmpty()) {
            JLabel noAlertsLabel = new JLabel("No hay alertas pendientes");
            noAlertsLabel.setFont(new Font("Poppins", Font.ITALIC, 14));
            noAlertsLabel.setForeground(new Color(150, 150, 150));
            listPanel.add(noAlertsLabel);
        } else {
            for (int i = 0; i < Math.min(alertas.size(), 5); i++) {
                String[] alerta = alertas.get(i);
                JPanel alertPanel = new JPanel(new BorderLayout());
                alertPanel.setOpaque(false);
                alertPanel.setBorder(new EmptyBorder(8, 0, 8, 0));
                
                JLabel iconLabel = new JLabel(alerta[0]);
                iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
                iconLabel.setBorder(new EmptyBorder(0, 0, 0, 10));
                
                JLabel textLabel = new JLabel("<html>" + alerta[1] + "</html>");
                textLabel.setFont(new Font("Poppins", Font.PLAIN, 12));
                textLabel.setForeground(new Color(66, 66, 66));
                
                JLabel priorityLabel = new JLabel(alerta[2]);
                priorityLabel.setFont(new Font("Poppins", Font.BOLD, 12));
                Color priorityColor = alerta[2].equals("ALTA") ? COLOR_PELIGRO : 
                                    alerta[2].equals("MEDIA") ? COLOR_ADVERTENCIA : COLOR_INFO;
                priorityLabel.setForeground(priorityColor);
                
                JPanel leftPanel = new JPanel(new BorderLayout());
                leftPanel.setOpaque(false);
                leftPanel.add(iconLabel, BorderLayout.WEST);
                leftPanel.add(textLabel, BorderLayout.CENTER);
                
                alertPanel.add(leftPanel, BorderLayout.CENTER);
                alertPanel.add(priorityLabel, BorderLayout.EAST);
                
                listPanel.add(alertPanel);
                
                if (i < Math.min(alertas.size(), 5) - 1) {
                    JSeparator separator = new JSeparator();
                    separator.setForeground(new Color(240, 240, 240));
                    listPanel.add(separator);
                }
            }
        }
        
        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        
        widget.add(scrollPane, BorderLayout.CENTER);
        return widget;
    }

    private List<String[]> obtenerAlertas() {
        List<String[]> alertas = new ArrayList<>();

        try {
            // Usar una sola transacción para todas las consultas
            ConexionBD.transaction(tx -> {
                // 1. Próximas revisiones médicas con JOIN FETCH para evitar N+1
                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.add(java.util.Calendar.DAY_OF_MONTH, 7);
                Date fechaLimite = cal.getTime();

                String hqlRevisiones = "SELECT hm FROM HistorialMedico hm " +
                                     "JOIN FETCH hm.animal a " +
                                     "WHERE hm.proximaRevision <= :fecha " +
                                     "AND hm.proximaRevision >= CURRENT_DATE " +
                                     "ORDER BY hm.proximaRevision";

                List<HistorialMedico> proximasRevisiones = tx.getSession()
                    .createQuery(hqlRevisiones, HistorialMedico.class)
                    .setParameter("fecha", fechaLimite)
                    .setMaxResults(5)
                    .getResultList();

                for (HistorialMedico historial : proximasRevisiones) {
                    String mensaje = String.format("Revisión médica para %s programada", 
                        historial.getAnimal().getNombre());
                    alertas.add(new String[]{"🩺", mensaje, "ALTA"});
                }

                // 2. Stock bajo - CORREGIR la consulta HQL
                String hqlStock = "SELECT i FROM Inventario i WHERE i.cantidad <= i.stockMinimo";
                List<Inventario> stockBajo = tx.getSession()
                    .createQuery(hqlStock, Inventario.class)
                    .setMaxResults(5)
                    .getResultList();

                for (Inventario item : stockBajo) {
                    String mensaje = String.format("Stock bajo: %s (Quedan %d)", 
                        item.getNombre(), item.getCantidad());
                    alertas.add(new String[]{"📦", mensaje, "MEDIA"});
                }

                // 3. Animales críticos - CORREGIR el nombre del atributo
                String hqlCriticos = "SELECT a FROM Animal a WHERE a.condicionSalud = :condicion";
                List<Animal> animalesCriticos = tx.getSession()
                    .createQuery(hqlCriticos, Animal.class)
                    .setParameter("condicion", Animal.CondicionSalud.CRITICO)
                    .setMaxResults(5)
                    .getResultList();

                for (Animal animal : animalesCriticos) {
                    String mensaje = String.format("%s requiere atención médica urgente", 
                        animal.getNombre());
                    alertas.add(new String[]{"🚨", mensaje, "ALTA"});
                }

                // 4. Adopciones con seguimiento pendiente
                LocalDate fechaSeguimiento = LocalDate.now().plusDays(3);

                String hqlSeguimientos = "SELECT a FROM Adopcion a " +
                                       "JOIN FETCH a.animal animal " +
                                       "WHERE a.seguimientoProgramado <= :fecha " +
                                       "AND a.seguimientoProgramado >= CURRENT_DATE " +
                                       "AND a.estado = :estado";

                List<Adopcion> seguimientos = tx.getSession()
                    .createQuery(hqlSeguimientos, Adopcion.class)
                    .setParameter("fecha", fechaSeguimiento)
                    .setParameter("estado", Adopcion.EstadoAdopcion.APROBADA)
                    .setMaxResults(5)
                    .getResultList();

                for (Adopcion adopcion : seguimientos) {
                    String mensaje = String.format("Seguimiento de adopción: %s", 
                        adopcion.getAnimal().getNombre());
                    alertas.add(new String[]{"❤️", mensaje, "MEDIA"});
                }

                // 5. Eventos próximos
                cal = java.util.Calendar.getInstance();
                cal.add(java.util.Calendar.DAY_OF_MONTH, 3);
                Date fechaEventos = cal.getTime();

                String hqlEventos = "SELECT e FROM Evento e " +
                                  "WHERE e.fechaInicio BETWEEN CURRENT_DATE AND :fecha " +
                                  "ORDER BY e.fechaInicio";

                List<Evento> eventosProximos = tx.getSession()
                    .createQuery(hqlEventos, Evento.class)
                    .setParameter("fecha", fechaEventos)
                    .setMaxResults(5)
                    .getResultList();

                for (Evento evento : eventosProximos) {
                    String mensaje = String.format("Evento próximo: %s", evento.getTitulo());
                    alertas.add(new String[]{"📅", mensaje, "BAJA"});
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            alertas.add(new String[]{"⚠️", "Error al cargar alertas del sistema", "MEDIA"});
        }

        return alertas;
    }

    private JPanel createWidgetPanel(String title, Color color) {
        JPanel widget = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Sombra suave
                g2.setColor(new Color(0, 0, 0, 10));
                g2.fillRoundRect(3, 3, getWidth()-3, getHeight()-3, 20, 20);
                
                // Fondo del widget
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth()-3, getHeight()-3, 20, 20);
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        widget.setOpaque(false);
        widget.setBorder(new EmptyBorder(20, 20, 20, 20));
        widget.setMinimumSize(new Dimension(300, 250));
        
        // Header del widget
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 15));
        titleLabel.setForeground(color);
        
        // Línea decorativa
        JPanel linePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(color);
                g2.fillRoundRect(0, getHeight()/2 - 1, getWidth(), 2, 2, 2);
                g2.dispose();
            }
        };
        linePanel.setOpaque(false);
        linePanel.setPreferredSize(new Dimension(0, 4));
        
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(linePanel, BorderLayout.SOUTH);
        
        widget.add(headerPanel, BorderLayout.NORTH);
        
        return widget;
    }

    private JButton createActionButton(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color bgColor = getModel().isPressed() ? color.darker().darker() :
                               getModel().isRollover() ? color.darker() : color;
                
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        btn.setFont(new Font("Poppins", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 35));
        
        return btn;
    }

    private void startTimeUpdater() {
        timerFecha = new Timer(1000, e -> {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd 'de' MMMM 'de' yyyy");
            lblFechaHora.setText(sdf.format(new Date()));
        });
        timerFecha.start();
    }

    public void stopTimer() {
        if (timerFecha != null) {
            timerFecha.stop();
        }
    }

    // Método para actualizar estadísticas desde la base de datos
    public void actualizarEstadisticas() {
        // Aquí puedes agregar la lógica para actualizar las estadísticas
        // desde tu base de datos usando Hibernate
    }

    // Método de ejemplo para mostrar cómo usar este panel
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
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
            
            DashboardPrincipal dashboard = new DashboardPrincipal(usuario);
            frame.add(dashboard);
            frame.setVisible(true);
        });
    }
}