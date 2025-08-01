package sistema.panels;

import com.formdev.flatlaf.FlatClientProperties;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import elemento.Correo;
import io.github.cdimascio.dotenv.Dotenv;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import sistema.dao.UsuarioDAO;
import sistema.modelos.Usuario;
import sistema.modelos.Usuario.RolUsuario;

/**
 *
 * @author Fer
 */
public class RegistroUsuario extends javax.swing.JPanel {
    private static final Dotenv dotenv = Dotenv.configure()
            .directory(".")
            .filename(".env")
            .ignoreIfMissing()
            .load();

    /**
     * Creates new form RegistroUsuario
     */
    public RegistroUsuario() {
        initComponents();
        
        lblNombre.setVisible(false);
        lblCorreo.setVisible(false);
        lblContra.setVisible(false);
        lblConfContra.setVisible(false);
        lblRol.setVisible(false);
        
        btnRegistrar.setCornerRadius(50);
        btnRegistrar.setBackgroundColor(Color.decode("#0575E6"));
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setFont(new Font("Poppins", Font.PLAIN, 14));
        
        txtNombre.putClientProperty(FlatClientProperties.STYLE, "" + "iconTextGap:10;");
        txtNombre.putClientProperty("JTextField.placeholderText", "Nombre");
        txtNombre.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, new ImageIcon(getClass().getResource("/icons/user.png")));
        txtNombre.setFont(new Font("Poppins", Font.PLAIN, 14));
        
        txtNombre.putClientProperty(FlatClientProperties.STYLE, ""
                + "margin:20,20,20,20;");
        
        txtCorreo.putClientProperty(FlatClientProperties.STYLE, "" + "iconTextGap:10;");
        txtCorreo.putClientProperty("JTextField.placeholderText", "Correo electrónico");
        txtCorreo.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, new ImageIcon(getClass().getResource("/icons/mail2.png")));
        txtCorreo.setFont(new Font("Poppins", Font.PLAIN, 14));
        
        txtCorreo.putClientProperty(FlatClientProperties.STYLE, ""
                + "margin:20,20,20,20;");
        
        txtContrasena.putClientProperty(FlatClientProperties.STYLE, "" + "iconTextGap:10;");
        installRevealButton(txtContrasena);
        txtContrasena.putClientProperty("JTextField.placeholderText", "Contraseña");
        txtContrasena.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, new ImageIcon(getClass().getResource("/icons/lock.png")));
        txtContrasena.setFont(new Font("JetBrains Mono", Font.PLAIN, 14));
        
        txtContrasena.putClientProperty(FlatClientProperties.STYLE, ""
                + "margin:20,20,20,20;");
        
        txtConfirmarContrasena.putClientProperty(FlatClientProperties.STYLE, "" + "iconTextGap:10;");
        installRevealButton(txtConfirmarContrasena);
        txtConfirmarContrasena.putClientProperty("JTextField.placeholderText", "Confirmar contraseña");
        txtConfirmarContrasena.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, new ImageIcon(getClass().getResource("/icons/lock.png")));
        txtConfirmarContrasena.setFont(new Font("JetBrains Mono", Font.PLAIN, 14));
        
        txtConfirmarContrasena.putClientProperty(FlatClientProperties.STYLE, ""
                + "margin:20,20,20,20;");
        
        cbxRol.setFont(new Font("Poppins", Font.PLAIN, 14));
        cbxRol.putClientProperty(FlatClientProperties.STYLE, "" + "padding:20,20,20,20;");
        cbxRol.putClientProperty("JTextField.placeholderText", "Seleccione el rol del usuario");
    }
    
    private void installRevealButton(JPasswordField txt) {

        JToolBar toolBar = new JToolBar();
        toolBar.putClientProperty(FlatClientProperties.STYLE, "" +
                "margin:0,0,0,20;");
        JButton button = new JButton(new ImageIcon(getClass().getResource("/icons/eye.png")));

        button.addActionListener(new ActionListener() {

            private char defaultEchoChart = txt.getEchoChar();
            private boolean show;

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                show = !show;
                if (show) {
                    button.setIcon(new ImageIcon(getClass().getResource("/icons/hide.png")));
                    txt.setEchoChar((char) 0);
                } else {
                    button.setIcon(new ImageIcon(getClass().getResource("/icons/eye.png")));
                    txt.setEchoChar(defaultEchoChart);
                }
            }
        });
        toolBar.add(button);
        txt.putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_COMPONENT, toolBar);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        btnRegistrar = new sistema.custom.BotonModificado();
        txtNombre = new javax.swing.JTextField();
        lblNombre = new javax.swing.JLabel();
        lblCorreo = new javax.swing.JLabel();
        txtCorreo = new javax.swing.JTextField();
        lblContra = new javax.swing.JLabel();
        lblConfContra = new javax.swing.JLabel();
        cbxRol = new javax.swing.JComboBox<>();
        txtContrasena = new javax.swing.JPasswordField();
        txtConfirmarContrasena = new javax.swing.JPasswordField();
        lblRol = new javax.swing.JLabel();
        lblError = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Poppins SemiBold", 0, 40)); // NOI18N
        jLabel1.setText("Registrar");

        jLabel2.setFont(new java.awt.Font("Poppins", 0, 16)); // NOI18N
        jLabel2.setText("Llena los campos para el nuevo usuario");

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/personal-data-protection-badges-with-lock.png"))); // NOI18N

        btnRegistrar.setText("Registrar");
        btnRegistrar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnRegistrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistrarActionPerformed(evt);
            }
        });

        lblNombre.setForeground(new java.awt.Color(255, 102, 102));
        lblNombre.setIcon(new javax.swing.ImageIcon("C:\\Users\\Fer\\Downloads\\icons8-warning-16.png")); // NOI18N

        lblCorreo.setForeground(new java.awt.Color(255, 102, 102));
        lblCorreo.setIcon(new javax.swing.ImageIcon("C:\\Users\\Fer\\Downloads\\icons8-warning-16.png")); // NOI18N
        lblCorreo.setText("Ingresa un correo");

        lblContra.setForeground(new java.awt.Color(255, 102, 102));
        lblContra.setIcon(new javax.swing.ImageIcon("C:\\Users\\Fer\\Downloads\\icons8-warning-16.png")); // NOI18N
        lblContra.setText("Ingresa una contraseña");

        lblConfContra.setForeground(new java.awt.Color(255, 102, 102));
        lblConfContra.setIcon(new javax.swing.ImageIcon("C:\\Users\\Fer\\Downloads\\icons8-warning-16.png")); // NOI18N
        lblConfContra.setText("Confirme la contraseña");

        cbxRol.setEditable(true);
        cbxRol.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Administrador", "Coordinador", "Voluntario", "Veterinario" }));
        cbxRol.setSelectedItem(null);

        lblRol.setForeground(new java.awt.Color(255, 102, 102));
        lblRol.setIcon(new javax.swing.ImageIcon("C:\\Users\\Fer\\Downloads\\icons8-warning-16.png")); // NOI18N
        lblRol.setText("Ingresa una contraseña");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(491, 491, 491)
                .addComponent(txtNombre)
                .addGap(542, 542, 542))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(78, 78, 78)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(60, 60, 60)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(lblRol, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblContra, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 71, Short.MAX_VALUE)
                                .addComponent(lblConfContra, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(219, 219, 219))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCorreo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtConfirmarContrasena, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(202, 202, 202))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(lblCorreo, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(225, 225, 225))))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(491, 491, 491)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE)
                            .addComponent(jLabel1)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(506, 506, 506)
                        .addComponent(lblNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(491, 491, 491)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnRegistrar, javax.swing.GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(cbxRol, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtContrasena, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lblError, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(432, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(202, 202, 202)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2)
                        .addGap(30, 30, 30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblCorreo)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtCorreo, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblNombre)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblContra)
                            .addComponent(lblConfContra))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtConfirmarContrasena, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
                            .addComponent(txtContrasena))
                        .addGap(18, 18, 18)
                        .addComponent(lblRol)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbxRol, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblError, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(41, 41, 41)
                .addComponent(btnRegistrar, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(164, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnRegistrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrarActionPerformed
        String nombre = txtNombre.getText().trim();
        String email = txtCorreo.getText().trim();
        String password = new String(txtContrasena.getPassword());
        String confirmarPassword = new String(txtConfirmarContrasena.getPassword());
        String rolString = (String)cbxRol.getSelectedItem( );
        RolUsuario rolSeleccionado = null;
        
        
        // Variable para controlar si hay errores
        boolean hayErrores = false;
        limpiarErrores();
        
        // ===== VALIDACIÓN DEL ROL =====
        if (rolString == null || rolString.isEmpty()) {
            lblRol.setVisible(true);
            lblRol.setText("Debes seleccionar un rol para el usuario.\n");
            hayErrores = true;
        } else {
            switch (rolString) {
                case "Administrador":
                    rolSeleccionado = RolUsuario.Administrador;
                    break;
                case "Coordinador":
                    rolSeleccionado = RolUsuario.Coordinador;
                    break;
                case "Voluntario":
                    rolSeleccionado = RolUsuario.Voluntario;
                    break;
                case "Veterinario":
                    rolSeleccionado = RolUsuario.Veterinario;
                    break;
                default:
                    lblRol.setVisible(true);
                    lblRol.setText("Rol no válido.\n");
                    hayErrores = true;
            }
        }
        
        
        // ===== VALIDACIÓN DEL NOMBRE =====
        if (nombre.isEmpty()) {
            lblNombre.setVisible(true);
            lblNombre.setText("El campo 'Nombre' no puede estar vacío.\n");
            hayErrores = true;
        }
        
        // ===== VALIDACIÓN DEL EMAIL =====
        if (email.isEmpty()) {
            lblCorreo.setVisible(true);
            lblCorreo.setText("El campo 'Correo electrónico' no puede estar vacío.\n");
            hayErrores = true;
        } else if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            lblCorreo.setVisible(true);
            lblCorreo.setText("Debe tener el formato: usuario@dominio.com\n");
            hayErrores = true;
        }
        
        // ===== VALIDACIÓN DE LA CONTRASEÑA =====
        if (password.isEmpty()) {
            lblContra.setVisible(true);
            lblContra.setText("El campo 'Contraseña' no puede estar vacío.\n");
            hayErrores = true;
        } else if (password.length() < 8) {
            lblContra.setVisible(true);
            lblContra.setText("La contraseña debe tener al menos 8 caracteres.\n");
            hayErrores = true;
        } else {
            // Validar que tenga al menos una mayúscula
            if (!password.matches(".*[A-Z].*")) {
                lblContra.setVisible(true);
                lblContra.setText("La contraseña debe contener al menos una letra mayúscula.\n");
                hayErrores = true;
            }
            
            // Validar que tenga al menos una minúscula
            if (!password.matches(".*[a-z].*")) {
                lblContra.setVisible(true);
                lblContra.setText("La contraseña debe contener al menos una letra minúscula.\n");
                hayErrores = true;
            }
            
            // Validar que tenga al menos un número
            if (!password.matches(".*[0-9].*")) {
                lblContra.setVisible(true);
                lblContra.setText("La contraseña debe contener al menos un número.\n");
                hayErrores = true;
            }
            
            // Validar que tenga al menos un carácter especial
            if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
                lblContra.setVisible(true);
                lblContra.setText("La contraseña debe contener al menos un carácter especial (!@#$%^&*()_+-=[]{}';':\"\\|,.<>/?).\n");
                hayErrores = true;
            }
            
            // Validar que no tenga espacios
            if (password.contains(" ")) {
                lblContra.setVisible(true);
                lblContra.setText("La contraseña no puede contener espacios.\n");
                hayErrores = true;
            }
        }
        
        // ===== VALIDACIÓN DE CONFIRMAR CONTRASEÑA =====
        if (confirmarPassword.isEmpty()) {
            lblConfContra.setVisible(true);
            lblConfContra.setText("El campo 'Confirmar contraseña' no puede estar vacío.\n");
            hayErrores = true;
        } else if (!password.equals(confirmarPassword)) {
            lblConfContra.setVisible(true);
            lblConfContra.setText("Las contraseñas no coinciden. Verifica que hayas escrito la misma contraseña en ambos campos.\n");
            hayErrores = true;
        }
        
        // ===== VALIDACIONES ADICIONALES DE SEGURIDAD =====
        
        // Verificar si el email ya existe (simulación - aquí irías a tu base de datos)
        if (!hayErrores && emailYaExiste(email)) {
            lblCorreo.setVisible(true);
            lblCorreo.setText("Ya existe un usuario registrado con este correo electrónico.\n");
            hayErrores = true;
        }
        
        // ===== MOSTRAR ERRORES O PROCEDER CON EL REGISTRO =====
        if (!hayErrores) {
            // Si no hay errores, proceder con el registro
            try {
                // Aquí iría tu lógica para guardar en la base de datos
                Usuario usuario = new Usuario();
                usuario.setNombre(nombre);
                usuario.setEmail(email);
                usuario.setPassword(password);
                
                usuario.setRol(rolSeleccionado);
                System.out.println("Cerca");
                boolean registroExitoso = UsuarioDAO.guardar(usuario);
                
                if (registroExitoso) {
                    
                    // Limpiar los campos después del registro exitoso
                    limpiarErrores();
                    limpiarCampos();
                    
                    Correo correo = new Correo();
                    String smtpUser = dotenv.get("SMTP_USER");
                    String smtpAppPassword = dotenv.get("SMTP_APP_PASSWORD");
                    correo.setRemitenteNombre(smtpUser, "NuevoHogar", smtpAppPassword);
                    correo.setDestinatario(usuario.getEmail());

                    Map<String, String> variables = new HashMap<>();
                    variables.put("nombre", usuario.getNombre());
                    variables.put("rol", usuario.getRol().name());
                    
                    String html = null;
                    switch (rolSeleccionado) {
                        case Administrador:
                            html = Files.readString(Paths.get("html/administrador_pdf.html"));
                            break;
                        case Coordinador:
                            html = Files.readString(Paths.get("html/coordinador_pdf.html"));
                            break;
                        case Veterinario:
                            html = Files.readString(Paths.get("html/veterinario_pdf.html"));
                            break;
                        case Voluntario:
                            html = Files.readString(Paths.get("html/voluntario_pdf.html"));
                            break;
                    }
                    
                    html = html.replace("${nombre}", usuario.getNombre())
                               .replace("${correo}", usuario.getEmail())
                               .replace("${rol}", usuario.getRol().name());

                    generarPDF(html, "cuenta.pdf");

                    correo.setContenidoDesdeHTMLConVariables("html/bienvenida_plantilla.html", "¡Bienvenido a NuevoHogar!", variables);
                    
                    correo.agregarArchivo("cuenta.pdf");

                    correo.enviarCorreoZoho();
                    
                    
                    RegistroExitoso splash = new RegistroExitoso((Frame) SwingUtilities.getWindowAncestor(this), false);
                    splash.setLocationRelativeTo((Frame) SwingUtilities.getWindowAncestor(this));
                    splash.mostrarPor(2000);
                    
                } else {
                    lblError.setText("Error al registrar usuario");
                }
                
            } catch (Exception ex) {
                
            }
        }
    }//GEN-LAST:event_btnRegistrarActionPerformed

    public static void generarPDF(String htmlContent, String outputPath) throws IOException {
        try (OutputStream os = new FileOutputStream(outputPath)) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFont(new File("fonts/Poppins-Regular.ttf"), "Poppins");
            builder.useFastMode();
            builder.withHtmlContent(htmlContent, null);
            builder.toStream(os);
            builder.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Método auxiliar para verificar si el email ya existe
    private boolean emailYaExiste(String email) {
        Usuario usuario = UsuarioDAO.obtenerPorEmail(email);
        if (usuario != null) {
            return true;
        }
        return false;
    }
    
    // Método auxiliar para limpiar los campos
    private void limpiarCampos() {
        txtNombre.setText("");
        txtCorreo.setText("");
        txtContrasena.setText("");
        txtConfirmarContrasena.setText("");
        cbxRol.setSelectedIndex(0);
    }
    
    private void limpiarErrores() {
        lblNombre.setVisible(false);
        lblCorreo.setVisible(false);
        lblContra.setVisible(false);
        lblConfContra.setVisible(false);
        lblRol.setVisible(false);
        lblError.setVisible(false);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private sistema.custom.BotonModificado btnRegistrar;
    private javax.swing.JComboBox<String> cbxRol;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel lblConfContra;
    private javax.swing.JLabel lblContra;
    private javax.swing.JLabel lblCorreo;
    private javax.swing.JLabel lblError;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblRol;
    private javax.swing.JPasswordField txtConfirmarContrasena;
    private javax.swing.JPasswordField txtContrasena;
    private javax.swing.JTextField txtCorreo;
    private javax.swing.JTextField txtNombre;
    // End of variables declaration//GEN-END:variables
}
