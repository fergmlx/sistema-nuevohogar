/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistema;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import sistema.config.DatabaseConfig;
import sistema.dao.ActividadDAO;
import sistema.dao.AnimalDAO;
import sistema.dao.UsuarioDAO;
import sistema.modelos.Actividad;

/**
 *
 * @author Fer
 */
public class RegistrosPrueba {
    
    public static void main(String[] args) {
        try {
            DatabaseConfig.initialize();
            ActividadDAO actividadDAO = new ActividadDAO();
            
            Actividad actividad = new Actividad();
            actividad.setAnimal(AnimalDAO.obtenerPorId(1));
            actividad.setTipoActividad(Actividad.TipoActividad.CEPILLADO);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            //actividad.setFecha(sdf.parse("27/04/2025"));
            actividad.setDescripcion("Se realizó el baño y cepillado completo del perro.");
            actividad.setResponsable(UsuarioDAO.obtenerPorId(1));
            
            boolean resultado = actividadDAO.guardar(actividad);
            if (resultado) {
                System.out.println("bien");
            } else {
                System.out.println("mal");
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
}
