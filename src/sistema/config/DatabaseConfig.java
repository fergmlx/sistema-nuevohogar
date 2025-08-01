package sistema.config;

import conexiondb.config.DBConfig;
import conexiondb.core.ConexionBD;
import io.github.cdimascio.dotenv.Dotenv;
import sistema.modelos.*;

/**
 * Clase para inicializar la conexión a la base de datos usando ConexionBD
 */
public class DatabaseConfig {
    private static final Dotenv dotenv = Dotenv.configure()
            .directory(".")
            .filename(".env")
            .ignoreIfMissing()
            .load();
    
    /**
     * Inicializa la conexión a la base de datos
     */
    public static void initialize() {
        try {
            System.out.println("Inicializando ConexionBD...");
            
            String dbUrl = dotenv.get("DB_URL");
            String dbUser = dotenv.get("DB_USER");
            String dbPassword = dotenv.get("DB_PASSWORD");
            
            // Configuración para MySQL
            DBConfig config = DBConfig.newBuilder()
                    .withMySql(
                        dbUrl,
                        dbUser,
                        dbPassword)
                    .withSchemaUpdate("update")
                    .withDebugSql(false, false)
                    .withConnectionPool(5, 10, 30000)
                    .withEntities(
                        Usuario.class,
                        Actividad.class,
                        Adopcion.class,
                        Animal.class,
                        Donacion.class,
                        Evento.class,
                        HistorialMedico.class,
                        Inventario.class,
                        Tarea.class
                    )
                    .build();
            
            // Inicializar ConexionBD con la configuración
            ConexionBD.init(config);
            
            // Verificar conexión
            if (ConexionBD.testConnection()) {
                System.out.println("Conexión a la base de datos establecida correctamente");
            } else {
                System.err.println("No se pudo establecer conexión a la base de datos");
            }
            
        } catch (Exception e) {
            System.err.println("Error al inicializar SimplDB: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Cierra la conexión a la base de datos
     */
    public static void shutdown() {
        try {
            ConexionBD.shutdown();
            System.out.println("Conexión a la base de datos cerrada correctamente");
        } catch (Exception e) {
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }
}