package sistema.login;

import sistema.modelos.Usuario;
import sistema.dao.UsuarioDAO;
import java.util.Date;
import sistema.config.DatabaseConfig;

public class CrearUsuarioAdmin {
    
    public static void main(String[] args) {
        try {
            DatabaseConfig.initialize();
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            
            // Verificar si ya existe un usuario con este email
            if (usuarioDAO.obtenerPorEmail("luisfgm.2704@gmail.com") != null) {
                System.out.println("El usuario administrador ya existe");
                return;
            }
            
            // Crear usuario administrador
            Usuario admin = new Usuario();
            admin.setNombre("Fernando");
            admin.setEmail("fergm@nuevohogar.me");
            admin.setPassword("admin123");
            admin.setRol(Usuario.RolUsuario.Administrador);
            
            // Guardar en base de datos
            boolean resultado = usuarioDAO.guardar(admin);
            
            if (resultado) {
                new Thread(() -> {
                    try {
                        usuarioDAO.actualizarUltimoAcceso(admin.getIdUsuario());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }).start();
                System.out.println("Usuario administrador creado exitosamente");
                System.out.println("Email: fergm@nuevohogar.me");
                System.out.println("Contraseña: admin123");
            } else {
                System.out.println("Error al crear usuario administrador");
            }
            DatabaseConfig.shutdown();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}