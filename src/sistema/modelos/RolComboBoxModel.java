package sistema.modelos;

import javax.swing.DefaultComboBoxModel;
import sistema.modelos.Usuario.RolUsuario;

/**
 * Modelo de ComboBox personalizado para manejar roles
 */
public class RolComboBoxModel extends DefaultComboBoxModel<String> {
    
    // Mapa para convertir entre String y RolUsuario
    private final RolUsuario[] roles;
    
    /**
     * Constructor que inicializa el modelo con todos los roles
     */
    public RolComboBoxModel() {
        // Obtener todos los valores del enum
        roles = RolUsuario.values();
        
        // Añadir los nombres legibles al modelo
        for (RolUsuario rol : roles) {
            addElement(getRolDisplayName(rol));
        }
    }
    
    /**
     * Obtiene el rol correspondiente al índice seleccionado
     * 
     * @param selectedIndex Índice seleccionado en el ComboBox
     * @return RolUsuario correspondiente o null si el índice es inválido
     */
    public RolUsuario getRolAtIndex(int selectedIndex) {
        if (selectedIndex >= 0 && selectedIndex < roles.length) {
            return roles[selectedIndex];
        }
        return null;
    }
    
    /**
     * Obtiene el rol correspondiente al texto seleccionado
     * 
     * @param selectedItem Texto seleccionado en el ComboBox
     * @return RolUsuario correspondiente o null si no hay coincidencia
     */
    public RolUsuario getRolFromDisplayName(String selectedItem) {
        for (RolUsuario rol : roles) {
            if (getRolDisplayName(rol).equals(selectedItem)) {
                return rol;
            }
        }
        return null;
    }
    
    /**
     * Establece el elemento seleccionado basado en un RolUsuario
     * 
     * @param rol RolUsuario a seleccionar
     */
    public void setSelectedRol(RolUsuario rol) {
        setSelectedItem(getRolDisplayName(rol));
    }
    
    /**
     * Convierte un RolUsuario a su nombre legible
     * 
     * @param rol RolUsuario a convertir
     * @return Nombre legible del rol
     */
    private String getRolDisplayName(RolUsuario rol) {
        switch (rol) {
            case Administrador:
                return "Administrador";
            case Voluntario:
                return "Voluntario";
            // Añadir más casos según sea necesario
            default:
                return rol.toString();
        }
    }
}