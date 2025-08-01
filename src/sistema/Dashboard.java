/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package sistema;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JPanel;
import sistema.email.EnviarCorreo;
import sistema.adopciones.PrincipalAdopciones;
import sistema.login.Login;
import sistema.modelos.Usuario;
import sistema.panels.DashboardPrincipal;
import sistema.panels.DonacionesPanel;
import sistema.animales.PrincipalAnimales;
import sistema.panels.RegistroUsuario;
import sistema.panels.GestionUsuarios;
import sistema.panels.HistorialMedicoPanel;
import sistema.panels.InventarioPanel;
import sistema.panels.PanelEventos;
import sistema.panels.PanelTareas;
import sistema.panels.VoluntariadoPanel;

/**
 *
 * @author Fer
 */
public class Dashboard extends javax.swing.JFrame {
    private EnviarCorreo ventanaMensaje;
    private Perfil dialogoPerfil;
    private Usuario usuarioActual;
    private DashboardPrincipal dashboardPrincipal;

    /**
     * Creates new form Bienvenida
     */
    public Dashboard(Usuario usuarioActual) {
        initComponents();
        this.usuarioActual = usuarioActual;
        InitContent();
        configureMenuIcons();
        configureMenuVisibility();
        setupMenuActions();
        configureMenuByUserRole(usuarioActual);
        setupUserSpecificMenuActions();
        menuLateral.deselectAllMenuItems();
    }
    
    private void InitContent() {
        dashboardPrincipal = new DashboardPrincipal(usuarioActual);
        ShowJPanel(dashboardPrincipal);
        dialogoPerfil = new Perfil(null, true);
        ventanaMensaje = new EnviarCorreo();
        menuLateral.setBackgroundColor(new Color(0, 0, 0, 0));
    }
    
    private void configureMenuIcons() {
        menuLateral.setMenuItemIcons("Inicio", "/icons/icons8-home-32.png", "/icons/home2.png");
        menuLateral.setMenuItemIcons("Servicio correo", "/icons/icons8-mail-account-32.png", "/icons/mail-account-filled.png");
        menuLateral.setMenuItemIcons("Enviar", "/icons/icons8-send-32.png", "/icons/send-filled.png");
        menuLateral.setMenuItemIcons("Administración", "/icons/icons8-administrative-tools-32.png", "/icons/administrative-tools-filled.png");
        menuLateral.setMenuItemIcons("Registrar usuario", "/icons/icons8-add-user-male-32.png", "/icons/add-user-filled.png");
        menuLateral.setMenuItemIcons("Usuarios", "/icons/icons8-users-settings-32.png", "/icons/users-settings-filled.png");
        menuLateral.setMenuItemIcons("Animales", "/icons/icons8-pets-32.png", "/icons/pets-filled.png");
        menuLateral.setMenuItemIcons("Adopciones", "/icons/icons8-form-32.png", "/icons/form-filled.png");
        menuLateral.setMenuItemIcons("Historial médico", "/icons/icons8-health-32.png", "/icons/health-filled.png");
        menuLateral.setMenuItemIcons("Voluntariado", "/icons/icons8-volunteer-32.png", "/icons/volunteer-filled.png");
        menuLateral.setMenuItemIcons("Donaciones", "/icons/icons8-charity-32.png", "/icons/charity-filled.png");
        menuLateral.setMenuItemIcons("Inventario", "/icons/icons8-medicine-32.png", "/icons/medicine-filled.png");
        menuLateral.setMenuItemIcons("Tareas", "/icons/icons8-tasks-32.png", "/icons/tasks-filled.png");
        menuLateral.setMenuItemIcons("Eventos", "/icons/icons8-event-32.png", "/icons/event-filled.png");
    }
    
    private void configureMenuVisibility() {
        String[] hiddenMenuItems = {
            "Administración", "Animales", "Adopciones", "Historial médico",
            "Voluntariado", "Donaciones", "Inventario", "Tareas", "Eventos"
        };
        
        for (String item : hiddenMenuItems) {
            menuLateral.getMenuItem(item).setShown(false);
        }
    }
    
    /**
     * Configura las acciones básicas del menú (las comunes a todos los usuarios)
     */
    private void setupMenuActions() {
        // Acción para ir al inicio
        menuLateral.setMenuItemActionWithSelection("Inicio", e -> {
            ShowJPanel(dashboardPrincipal);
        });
        
        // Acción para configurar servicio de correo
        menuLateral.setMenuItemActionWithSelection("Servicio correo", e -> {
            handleEmailServiceConfiguration();
        });
        
        // Acción para enviar correo
        menuLateral.setMenuItemActionWithSelection("Enviar", e -> {
            ventanaMensaje.setVisible(true);
        });
        
        // Acción para ir al login
        menuLateral.setMenuItemAction("Login", e -> {
            navigateToLogin();
        });
    }
    
     /**
     * Configura la visibilidad del menú según el rol del usuario
     * 
     * @param usuario Usuario actual
     */
    private void configureMenuByUserRole(Usuario usuario) {
        Usuario.RolUsuario rol = usuario.getRol();
        
        switch (rol) {
            case Administrador:
                showAdministratorMenuItems();
                break;
            case Coordinador:
                showCoordinatorMenuItems();
                break;
            case Veterinario:
                showVeterinarianMenuItems();
                break;
            case Voluntario:
                showVolunteerMenuItems();
                break;
        }
    }
    
    /**
     * Muestra elementos del menú para administradores
     */
    private void showAdministratorMenuItems() {
        String[] adminItems = {
            "Administración", "Animales", "Adopciones", 
            "Historial médico", "Voluntariado"
        };
        
        for (String item : adminItems) {
            menuLateral.getMenuItem(item).setShown(true);
        }
    }
    
    /**
     * Muestra elementos del menú para coordinadores
     */
    private void showCoordinatorMenuItems() {
        String[] coordinatorItems = {
            "Animales", "Adopciones", "Voluntariado", "Donaciones"
        };
        
        for (String item : coordinatorItems) {
            menuLateral.getMenuItem(item).setShown(true);
        }
    }
    
    /**
     * Muestra elementos del menú para veterinarios
     */
    private void showVeterinarianMenuItems() {
        String[] veterinarianItems = {
            "Animales", "Historial médico", "Inventario"
        };
        
        for (String item : veterinarianItems) {
            menuLateral.getMenuItem(item).setShown(true);
        }
    }
    
    /**
     * Muestra elementos del menú para voluntarios
     */
    private void showVolunteerMenuItems() {
        String[] volunteerItems = {"Animales", "Tareas"};
        
        for (String item : volunteerItems) {
            menuLateral.getMenuItem(item).setShown(true);
        }
        // Eventos comentado por el momento
        // menuLateral.getMenuItem("Eventos").setShown(true);
    }
    
    /**
     * Configura las acciones del menú específicas para usuarios autenticados
     */
    private void setupUserSpecificMenuActions() {
        menuLateral.setMenuItemActionWithSelection("Registrar usuario", e -> {
            ShowJPanel(new RegistroUsuario());
        });
        
        menuLateral.setMenuItemActionWithSelection("Usuarios", e -> {
            ShowJPanel(new GestionUsuarios());
        });
        
        menuLateral.setMenuItemActionWithSelection("Animales", e -> {
            ShowJPanel(new PrincipalAnimales(usuarioActual));
        });
        
        menuLateral.setMenuItemActionWithSelection("Adopciones", e -> {
            ShowJPanel(new PrincipalAdopciones(usuarioActual));
        });
        
        menuLateral.setMenuItemActionWithSelection("Historial médico", e -> {
            ShowJPanel(new HistorialMedicoPanel(usuarioActual));
        });
        
        menuLateral.setMenuItemActionWithSelection("Voluntariado", e -> {
            ShowJPanel(new VoluntariadoPanel(usuarioActual));
        });
        
        menuLateral.setMenuItemActionWithSelection("Donaciones", e -> {
            ShowJPanel(new DonacionesPanel(usuarioActual));
        });
        
        menuLateral.setMenuItemActionWithSelection("Inventario", e -> {
            ShowJPanel(new InventarioPanel(usuarioActual));
        });
        
        menuLateral.setMenuItemActionWithSelection("Tareas", e -> {
            ShowJPanel(new PanelTareas(usuarioActual));
        });
        
        menuLateral.setMenuItemActionWithSelection("Eventos", e -> {
            ShowJPanel(new PanelEventos(usuarioActual));
        });
    }
    
    /**
     * Maneja la configuración del servicio de correo
     */
    private void handleEmailServiceConfiguration() {
        dialogoPerfil.setVisible(true);
        if (dialogoPerfil.getBotonPulsado() == 0) {
            ventanaMensaje.setEmail(dialogoPerfil.getEmail());
            ventanaMensaje.setPassword(dialogoPerfil.getPassword());
        }
    }
    
    /**
     * Navega a la pantalla de login
     */
    private void navigateToLogin() {
        (new Login()).setVisible(true);
        this.dispose();
    }
    
    public void ShowJPanel(JPanel p) {
        p.setSize(1340, 900);
        p.setLocation(0,0);
        
        contentPanel.removeAll();
        contentPanel.add(p, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    public void setUsuarioActual(Usuario usuario) {
        this.usuarioActual = usuario;
        ShowJPanel(new DashboardPrincipal(usuarioActual));
        if (usuarioActual.getRol() == Usuario.RolUsuario.Administrador) {
            menuLateral.getMenuItem("Administración").setShown(true);
            menuLateral.getMenuItem("Animales").setShown(true);
            menuLateral.getMenuItem("Adopciones").setShown(true);
            menuLateral.getMenuItem("Historial médico").setShown(true);
            menuLateral.getMenuItem("Voluntariado").setShown(true);
        }
        
        if (usuarioActual.getRol() == Usuario.RolUsuario.Coordinador) {
            menuLateral.getMenuItem("Animales").setShown(true);
            menuLateral.getMenuItem("Adopciones").setShown(true);
            menuLateral.getMenuItem("Voluntariado").setShown(true);
            menuLateral.getMenuItem("Donaciones").setShown(true);
        }
        
        if (usuarioActual.getRol() == Usuario.RolUsuario.Veterinario) {
            menuLateral.getMenuItem("Animales").setShown(true);
            menuLateral.getMenuItem("Historial médico").setShown(true);
            menuLateral.getMenuItem("Inventario").setShown(true);
        }
        
        if (usuarioActual.getRol() == Usuario.RolUsuario.Voluntario) {
            menuLateral.getMenuItem("Animales").setShown(true);
            menuLateral.getMenuItem("Tareas").setShown(true);
            //menuLateral.getMenuItem("Eventos").setShown(true);
        }
        menuLateral.setMenuItemActionWithSelection("Registrar usuario", e -> {
            ShowJPanel(new RegistroUsuario());
        });
        menuLateral.setMenuItemActionWithSelection("Usuarios", e -> {
            ShowJPanel(new GestionUsuarios());
        });
        menuLateral.setMenuItemActionWithSelection("Animales", e -> {
            ShowJPanel(new PrincipalAnimales(usuarioActual));
        });
        menuLateral.setMenuItemActionWithSelection("Adopciones", e -> {
            ShowJPanel(new PrincipalAdopciones(usuarioActual));
        });
        menuLateral.setMenuItemActionWithSelection("Historial médico", e -> {
            ShowJPanel(new HistorialMedicoPanel(usuarioActual));
        });
        menuLateral.setMenuItemActionWithSelection("Voluntariado", e -> {
            ShowJPanel(new VoluntariadoPanel(usuarioActual));
        });
        menuLateral.setMenuItemActionWithSelection("Donaciones", e -> {
            ShowJPanel(new DonacionesPanel(usuarioActual));
        });
        menuLateral.setMenuItemActionWithSelection("Inventario", e -> {
            ShowJPanel(new InventarioPanel(usuarioActual));
        });
        menuLateral.setMenuItemActionWithSelection("Tareas", e -> {
            ShowJPanel(new PanelTareas(usuarioActual));
        });
        menuLateral.setMenuItemActionWithSelection("Eventos", e -> {
            ShowJPanel(new PanelEventos(usuarioActual));
        });
        revalidate();
        repaint();
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        gradientePanel1 = new prueba2.GradientePanel();
        menuLateral = new menulateral.SideMenuComponent();
        contentPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        gradientePanel1.setEndColor(new java.awt.Color(2, 27, 121));
        gradientePanel1.setStartColor(new java.awt.Color(5, 117, 230));

        menuLateral.setBackgroundColor(new java.awt.Color(5, 117, 230));
        menuLateral.setCollapsedWidth(100);
        menuLateral.setExpandedWidth(280);
        menuLateral.setDefaultHamburgerIconColor(new java.awt.Color(255, 255, 255));
        menuLateral.setHamburgerIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8-fries-menu-32.png"))); // NOI18N
        menuLateral.setHoverColor(new java.awt.Color(5, 117, 230));
        menuLateral.setModel(new menulateral.SideMenuModel() {{ addItem(new menulateral.SideMenuItem("Inicio", "C:\\Users\\Fer\\Documents\\NetBeansProjects\\Sistema\\src\\icons\\icons8-home-32.png", "Ir a Inicio")); addItem(new menulateral.SideMenuItem("Perfil", "C:\\Users\\Fer\\Documents\\NetBeansProjects\\Sistema\\src\\icons\\icons8-user-32.png")); menulateral.SideMenuItem item7140 = getItem(1); menulateral.SideMenuItem item580 = new menulateral.SideMenuItem("Servicio correo", "C:\\Users\\Fer\\Documents\\NetBeansProjects\\Sistema\\src\\icons\\icons8-mail-account-32.png", "Configurar el correo y contraseña de aplicación para envío de correos"); item7140.addChild(item580); addItem(new menulateral.SideMenuItem("Mensajes", "C:\\Users\\Fer\\Documents\\NetBeansProjects\\Sistema\\src\\icons\\icons8-chat-message-32.png")); menulateral.SideMenuItem item1744 = getItem(2); menulateral.SideMenuItem item9752 = new menulateral.SideMenuItem("Enviar", "C:\\Users\\Fer\\Documents\\NetBeansProjects\\Sistema\\src\\icons\\icons8-send-32.png", "Enviar nuevo correo"); item1744.addChild(item9752); addItem(new menulateral.SideMenuItem("Administración", "C:\\Users\\Fer\\Documents\\NetBeansProjects\\Sistema\\src\\icons\\icons8-administrative-tools-32.png")); menulateral.SideMenuItem item3568 = getItem(3); menulateral.SideMenuItem item6073 = new menulateral.SideMenuItem("Registrar usuario", "C:\\Users\\Fer\\Documents\\NetBeansProjects\\Sistema\\src\\icons\\icons8-add-user-male-32.png", "Registrar nuevo usuario"); item3568.addChild(item6073); menulateral.SideMenuItem item7031 = new menulateral.SideMenuItem("Usuarios", "C:\\Users\\Fer\\Documents\\NetBeansProjects\\Sistema\\src\\icons\\icons8-users-settings-32.png", "Administrar usuarios"); item3568.addChild(item7031); addItem(new menulateral.SideMenuItem("Animales", "C:\\Users\\Fer\\Documents\\NetBeansProjects\\Sistema\\src\\icons\\icons8-pets-32.png", "Gestionar animales")); addItem(new menulateral.SideMenuItem("Adopciones", "C:\\Users\\Fer\\Documents\\NetBeansProjects\\Sistema\\src\\icons\\icons8-form-32.png", "Gestionar adopciones")); addItem(new menulateral.SideMenuItem("Historial médico", "C:\\Users\\Fer\\Documents\\NetBeansProjects\\Sistema\\src\\icons\\icons8-health-32.png", "Ver historial médico")); addItem(new menulateral.SideMenuItem("Voluntariado", "C:\\Users\\Fer\\Documents\\NetBeansProjects\\Sistema\\src\\icons\\icons8-volunteer-32.png", "Gestionar usuarios voluntarios")); addItem(new menulateral.SideMenuItem("Donaciones", "C:\\Users\\Fer\\Documents\\NetBeansProjects\\Sistema\\src\\icons\\icons8-charity-32.png", "Gestionar donaciones")); addItem(new menulateral.SideMenuItem("Inventario", "C:\\Users\\Fer\\Documents\\NetBeansProjects\\Sistema\\src\\icons\\icons8-medicine-32.png", "Gestionar inventario")); addItem(new menulateral.SideMenuItem("Tareas", "C:\\Users\\Fer\\Documents\\NetBeansProjects\\Sistema\\src\\icons\\icons8-tasks-32.png", "Gestionar tareas de voluntarios")); addItem(new menulateral.SideMenuItem("Eventos", "C:\\Users\\Fer\\Documents\\NetBeansProjects\\Sistema\\src\\icons\\icons8-event-32.png", "Gestionar eventos")); addItem(new menulateral.SideMenuItem("Login", "C:\\Users\\Fer\\Documents\\NetBeansProjects\\Sistema\\src\\icons\\icons8-login-rounded-32.png", "Ir a login")); }});
        menuLateral.setPreferredHeight(80);
        menuLateral.setTextColor(new java.awt.Color(255, 255, 255));
        menuLateral.setTextHoverColor(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout gradientePanel1Layout = new javax.swing.GroupLayout(gradientePanel1);
        gradientePanel1.setLayout(gradientePanel1Layout);
        gradientePanel1Layout.setHorizontalGroup(
            gradientePanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(gradientePanel1Layout.createSequentialGroup()
                .addComponent(menuLateral, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        gradientePanel1Layout.setVerticalGroup(
            gradientePanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(gradientePanel1Layout.createSequentialGroup()
                .addComponent(menuLateral, javax.swing.GroupLayout.PREFERRED_SIZE, 900, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        getContentPane().add(gradientePanel1, java.awt.BorderLayout.LINE_START);

        contentPanel.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout contentPanelLayout = new javax.swing.GroupLayout(contentPanel);
        contentPanel.setLayout(contentPanelLayout);
        contentPanelLayout.setHorizontalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1340, Short.MAX_VALUE)
        );
        contentPanelLayout.setVerticalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 900, Short.MAX_VALUE)
        );

        getContentPane().add(contentPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel contentPanel;
    private prueba2.GradientePanel gradientePanel1;
    private menulateral.SideMenuComponent menuLateral;
    // End of variables declaration//GEN-END:variables
}
