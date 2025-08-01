package sistema.dao;

import conexiondb.core.ConexionBD;
import java.time.LocalDateTime;
import sistema.modelos.Actividad;
import sistema.modelos.Actividad.TipoActividad;
import sistema.modelos.Animal;
import sistema.modelos.Usuario;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ActividadDAO {
    
    /**
     * Guarda una nueva actividad
     */
    public static boolean guardar(Actividad actividad) {
        try {
            // Si es nueva, establecer fecha actual
            if (actividad.getFecha() == null) {
                actividad.setFecha(LocalDateTime.now());
            }
            
            ConexionBD.manage(Actividad.class).save(actividad);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Actualiza una actividad
     */
    public boolean actualizar(Actividad actividad) {
        try {
            ConexionBD.manage(Actividad.class).update(actividad);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Elimina una actividad
     */
    public boolean eliminar(Integer idActividad) {
        try {
            Actividad actividad = obtenerPorId(idActividad);
            if (actividad != null) {
                ConexionBD.manage(Actividad.class).delete(actividad);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Obtiene una actividad por ID
     */
    public Actividad obtenerPorId(Integer idActividad) {
        try {
            return ConexionBD.manage(Actividad.class).findById(idActividad).orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Obtiene todas las actividades
     */
    public static List<Actividad> obtenerTodas() {
        try {
            return ConexionBD.query(Actividad.class)
                    .orderBy("fecha", false)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtiene actividades por animal
     */
    public List<Actividad> obtenerPorAnimal(Animal animal) {
        try {
            return ConexionBD.query(Actividad.class)
                    .where("animal", "=", animal)
                    .orderBy("fecha", false)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtiene actividades por tipo
     */
    public List<Actividad> obtenerPorTipo(TipoActividad tipoActividad) {
        try {
            return ConexionBD.query(Actividad.class)
                    .where("tipoActividad", "=", tipoActividad)
                    .orderBy("fecha", false)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtiene actividades por responsable
     */
    public List<Actividad> obtenerPorResponsable(Usuario responsable) {
        try {
            return ConexionBD.query(Actividad.class)
                    .where("responsable", "=", responsable)
                    .orderBy("fecha", false)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtiene actividades por rango de fechas
     */
    public List<Actividad> obtenerPorRangoFechas(Date fechaInicio, Date fechaFin) {
        try {
            return ConexionBD.query(Actividad.class)
                    .where("fecha", ">=", fechaInicio)
                    .where("fecha", "<=", fechaFin)
                    .orderBy("fecha", false)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtiene actividades por animal y tipo
     */
    public List<Actividad> obtenerPorAnimalYTipo(Animal animal, TipoActividad tipoActividad) {
        try {
            return ConexionBD.query(Actividad.class)
                    .where("animal", "=", animal)
                    .where("tipoActividad", "=", tipoActividad)
                    .orderBy("fecha", false)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}