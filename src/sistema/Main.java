package sistema;

import com.formdev.flatlaf.themes.FlatMacLightLaf;
import java.awt.Font;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Insets;
import sistema.config.DatabaseConfig;
import sistema.login.Login;

public class Main {
    public static void main(String[] args) {
        try {
            // Configurar Look and Feel
            FlatMacLightLaf.setup();
            UIManager.put("Button.arc", 999);
            UIManager.put("TextComponent.arc", 55);
            UIManager.put("Component.arc", 55);
            UIManager.put("TextField.margin", new Insets(5, 10, 5, 5));
            UIManager.put("PasswordField.margin", new Insets(5, 5, 5, 5));
            UIManager.put("defaultFont", new Font("Poppins", Font.PLAIN, 13));
            
            // Inicializar base de datos
            Thread dbThread = new Thread(DatabaseConfig::initialize);
            dbThread.setDaemon(true);
            dbThread.start();
            
            // Iniciar interfaz de usuario
            SwingUtilities.invokeLater(() -> {
                Login login = new Login();
                login.setVisible(true);
            });
            
            // Registrar shutdown hook para cerrar recursos
            Runtime.getRuntime().addShutdownHook(new Thread(DatabaseConfig::shutdown));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}