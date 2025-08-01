package sistema.panels;

import sistema.modelos.Usuario;
import sistema.modelos.Usuario.RolUsuario;
import sistema.dao.UsuarioDAO;
import sistema.modelos.RolComboBoxModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class DialogoUsuario extends JDialog {
    
    private JTextField txtNombre, txtEmail;
    private JPasswordField txtPassword, txtConfirmPassword;
    private JComboBox<String> cbxRol;
    private JButton btnGuardar, btnCancelar;
    
    private Usuario usuario;
    private boolean esNuevo;
    private boolean aceptado = false;
    
    /**
     * Constructor para nuevo usuario
     */
    public DialogoUsuario(Window owner) {
        super(owner, "Nuevo Usuario", ModalityType.APPLICATION_MODAL);
        this.usuario = new Usuario();
        this.esNuevo = true;
        initComponents();
    }
    
    /**
     * Constructor para editar usuario
     */
    public DialogoUsuario(Window owner, Usuario usuario) {
        super(owner, "Editar Usuario", ModalityType.APPLICATION_MODAL);
        this.usuario = usuario;
        this.esNuevo = false;
        initComponents();
        cargarDatosUsuario();
    }
    
    private void initComponents() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Panel de formulario con GridBagLayout para alineación
        JPanel panelForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Nombre
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        panelForm.add(new JLabel("Nombre:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        txtNombre = new JTextField(20);
        panelForm.add(txtNombre, gbc);
        
        // Email
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        panelForm.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        txtEmail = new JTextField(20);
        panelForm.add(txtEmail, gbc);
        
        // Rol
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        panelForm.add(new JLabel("Rol:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        cbxRol = new JComboBox<>();
        cbxRol.setModel(new RolComboBoxModel());
        panelForm.add(cbxRol, gbc);
        
        // Contraseña
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        JLabel lblPassword = new JLabel("Contraseña:");
        panelForm.add(lblPassword, gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        txtPassword = new JPasswordField(20);
        panelForm.add(txtPassword, gbc);
        
        // Confirmar Contraseña
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        JLabel lblConfirmPassword = new JLabel("Confirmar Contraseña:");
        panelForm.add(lblConfirmPassword, gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        txtConfirmPassword = new JPasswordField(20);
        panelForm.add(txtConfirmPassword, gbc);
        
        // Nota para contraseña en edición
        if (!esNuevo) {
            gbc.gridx = 0;
            gbc.gridy = 5;
            gbc.gridwidth = 3;
            gbc.weightx = 1.0;
            JLabel lblNota = new JLabel("Nota: Dejar en blanco para mantener la contraseña actual");
            lblNota.setFont(lblNota.getFont().deriveFont(Font.ITALIC));
            lblNota.setForeground(Color.GRAY);
            panelForm.add(lblNota, gbc);
        }
        
        panel.add(panelForm, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        btnGuardar = new JButton("Guardar");
        btnGuardar.putClientProperty("JButton.buttonType", "roundRect");
        btnGuardar.addActionListener(this::guardarUsuario);
        
        btnCancelar = new JButton("Cancelar");
        btnCancelar.putClientProperty("JButton.buttonType", "roundRect");
        btnCancelar.addActionListener(e -> dispose());
        
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        
        panel.add(panelBotones, BorderLayout.SOUTH);
        
        setContentPane(panel);
        pack();
        setLocationRelativeTo(getOwner());
        setResizable(false);
    }
    
    private void cargarDatosUsuario() {
        if (usuario != null) {
            txtNombre.setText(usuario.getNombre());
            txtEmail.setText(usuario.getEmail());
            
            // Seleccionar rol
            RolComboBoxModel model = (RolComboBoxModel) cbxRol.getModel();
            model.setSelectedRol(usuario.getRol());
            
            // No mostrar contraseña existente por seguridad
            txtPassword.setText("");
            txtConfirmPassword.setText("");
        }
    }
    
    private void guardarUsuario(ActionEvent evt) {
        // Validar campos
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "El nombre es obligatorio", 
                "Error de validación", JOptionPane.ERROR_MESSAGE);
            txtNombre.requestFocus();
            return;
        }
        
        if (txtEmail.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "El email es obligatorio", 
                "Error de validación", JOptionPane.ERROR_MESSAGE);
            txtEmail.requestFocus();
            return;
        }
        
        // Validar formato de email
        if (!validarEmail(txtEmail.getText().trim())) {
            JOptionPane.showMessageDialog(this, 
                "El formato del email no es válido", 
                "Error de validación", JOptionPane.ERROR_MESSAGE);
            txtEmail.requestFocus();
            return;
        }
        
        // Validar contraseña para usuarios nuevos
        String password = new String(txtPassword.getPassword()).trim();
        String confirmPassword = new String(txtConfirmPassword.getPassword()).trim();
        
        if (esNuevo && password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "La contraseña es obligatoria para nuevos usuarios", 
                "Error de validación", JOptionPane.ERROR_MESSAGE);
            txtPassword.requestFocus();
            return;
        }
        
        if (!password.isEmpty() && !password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, 
                "Las contraseñas no coinciden", 
                "Error de validación", JOptionPane.ERROR_MESSAGE);
            txtConfirmPassword.requestFocus();
            return;
        }
        
        // Verificar si el email ya existe (solo para nuevos usuarios o si cambió el email)
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        if (esNuevo || !txtEmail.getText().trim().equals(usuario.getEmail())) {
            if (usuarioDAO.existeEmail(txtEmail.getText().trim())) {
                JOptionPane.showMessageDialog(this, 
                    "Ya existe un usuario con este email", 
                    "Error de validación", JOptionPane.ERROR_MESSAGE);
                txtEmail.requestFocus();
                return;
            }
        }
        
        try {
            // Actualizar objeto usuario
            usuario.setNombre(txtNombre.getText().trim());
            usuario.setEmail(txtEmail.getText().trim());
            
            // Obtener rol seleccionado
            RolComboBoxModel model = (RolComboBoxModel) cbxRol.getModel();
            RolUsuario rolSeleccionado = model.getRolAtIndex(cbxRol.getSelectedIndex());
            usuario.setRol(rolSeleccionado);
            
            // Actualizar contraseña solo si se proporciona una nueva
            if (!password.isEmpty()) {
                usuario.setPassword(password);
            }
            
            // Guardar en la base de datos
            boolean resultado;
            if (esNuevo) {
                resultado = usuarioDAO.guardar(usuario);
            } else {
                resultado = usuarioDAO.actualizar(usuario);
            }
            
            if (resultado) {
                aceptado = true;
                JOptionPane.showMessageDialog(this, 
                    "Usuario guardado correctamente", 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Error al guardar el usuario", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error al guardar el usuario: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean validarEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(regex);
    }
    
    public boolean isAceptado() {
        return aceptado;
    }
}