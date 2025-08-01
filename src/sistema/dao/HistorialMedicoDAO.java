package sistema.dao;

import conexiondb.core.ConexionBD;
import sistema.modelos.Animal;
import sistema.modelos.HistorialMedico;
import sistema.modelos.HistorialMedico.TipoConsulta;
import sistema.modelos.Usuario;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HistorialMedicoDAO {
    
    /**
     * Guarda un nuevo registro médico
     */
    public boolean guardar(HistorialMedico historial) {
        try {
            // Si es nuevo, establecer fecha
            if (historial.getFecha() == null) {
                historial.setFecha(new Date());
            }
            
            ConexionBD.manage(HistorialMedico.class).save(historial);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Actualiza un registro médico
     */
    public boolean actualizar(HistorialMedico historial) {
        try {
            ConexionBD.manage(HistorialMedico.class).update(historial);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Elimina un registro médico
     */
    public boolean eliminar(Integer idHistorial) {
        try {
            HistorialMedico historial = obtenerPorId(idHistorial);
            if (historial != null) {
                ConexionBD.manage(HistorialMedico.class).delete(historial);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Obtiene un registro médico por ID
     */
    public HistorialMedico obtenerPorId(Integer idHistorial) {
        try {
            return ConexionBD.manage(HistorialMedico.class).findById(idHistorial).orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Obtiene el historial médico de un animal
     */
    public List<HistorialMedico> obtenerHistorialPorAnimal(Animal animal) {
        try {
            return ConexionBD.query(HistorialMedico.class)
                    .where("animal", "=", animal)
                    .orderBy("fecha", false) // Del más reciente al más antiguo
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtiene historiales médicos por tipo de consulta
     */
    public List<HistorialMedico> obtenerPorTipoConsulta(TipoConsulta tipoConsulta) {
        try {
            return ConexionBD.query(HistorialMedico.class)
                    .where("tipoConsulta", "=", tipoConsulta)
                    .orderBy("fecha", false)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtiene registros médicos por veterinario
     */
    public List<HistorialMedico> obtenerPorVeterinario(Usuario veterinario) {
        try {
            return ConexionBD.query(HistorialMedico.class)
                    .where("veterinario", "=", veterinario)
                    .orderBy("fecha", false)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtiene registros médicos para próximas revisiones
     */
    public static List<HistorialMedico> obtenerProximasRevisiones(Date fechaLimite) {
        try {
            return ConexionBD.query(HistorialMedico.class)
                    .where("proximaRevision", "<=", fechaLimite)
                    .where("proximaRevision", ">=", new Date()) // Solo fechas futuras
                    .orderBy("proximaRevision", true) // Las más próximas primero
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtiene registros médicos por rango de fechas
     */
    public List<HistorialMedico> obtenerPorRangoFechas(Date fechaInicio, Date fechaFin) {
        try {
            return ConexionBD.query(HistorialMedico.class)
                    .where("fecha", ">=", fechaInicio)
                    .where("fecha", "<=", fechaFin)
                    .orderBy("fecha", false)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public static List<HistorialMedico> obtenerTodos() {
        try {
            return ConexionBD.manage(HistorialMedico.class).findAll();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}