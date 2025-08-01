package sistema.dao;

import conexiondb.core.ConexionBD;
import sistema.modelos.Animal;
import sistema.modelos.Tarea;
import sistema.modelos.Tarea.EstadoTarea;
import sistema.modelos.Tarea.PrioridadTarea;
import sistema.modelos.Usuario;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TareaDAO {
    
    /**
     * Guarda una nueva tarea
     */
    public boolean guardar(Tarea tarea) {
        try {
            // Si es nueva, establecer fecha de asignación
            if (tarea.getFechaAsignacion() == null) {
                tarea.setFechaAsignacion(new Date());
            }
            
            // Si no se especifica un estado, establecer como PENDIENTE
            if (tarea.getEstado() == null) {
                tarea.setEstado(EstadoTarea.PENDIENTE);
            }
            
            ConexionBD.manage(Tarea.class).save(tarea);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Actualiza una tarea
     */
    public boolean actualizar(Tarea tarea) {
        try {
            ConexionBD.manage(Tarea.class).update(tarea);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Elimina una tarea
     */
    public boolean eliminar(Integer idTarea) {
        try {
            Tarea tarea = obtenerPorId(idTarea);
            if (tarea != null) {
                ConexionBD.manage(Tarea.class).delete(tarea);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Obtiene una tarea por ID
     */
    public Tarea obtenerPorId(Integer idTarea) {
        try {
            return ConexionBD.manage(Tarea.class).findById(idTarea).orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Obtiene todas las tareas
     */
    public List<Tarea> obtenerTodas() {
        try {
            return ConexionBD.query(Tarea.class)
                    .orderBy("fechaLimite", true)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtiene tareas por estado
     */
    public List<Tarea> obtenerPorEstado(EstadoTarea estado) {
        try {
            return ConexionBD.query(Tarea.class)
                    .where("estado", "=", estado)
                    .orderBy("fechaLimite", true)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtiene tareas por voluntario asignado
     */
    public List<Tarea> obtenerPorVoluntario(Usuario voluntario) {
        try {
            return ConexionBD.query(Tarea.class)
                    .where("voluntario", "=", voluntario)
                    .orderBy("fechaLimite", true)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtiene tareas pendientes de un voluntario
     */
    public List<Tarea> obtenerTareasPendientesPorVoluntario(Usuario voluntario) {
        try {
            return ConexionBD.query(Tarea.class)
                    .where("voluntario", "=", voluntario)
                    .where("estado", "=", EstadoTarea.PENDIENTE)
                    .orderBy("fechaLimite", true)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtiene tareas asignadas por un usuario
     */
    public List<Tarea> obtenerPorAsignador(Usuario asignador) {
        try {
            return ConexionBD.query(Tarea.class)
                    .where("asignador", "=", asignador)
                    .orderBy("fechaAsignacion", false)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtiene tareas por animal
     */
    public List<Tarea> obtenerPorAnimal(Animal animal) {
        try {
            return ConexionBD.query(Tarea.class)
                    .where("animal", "=", animal)
                    .orderBy("fechaLimite", true)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtiene tareas por prioridad
     */
    public List<Tarea> obtenerPorPrioridad(PrioridadTarea prioridad) {
        try {
            return ConexionBD.query(Tarea.class)
                    .where("prioridad", "=", prioridad)
                    .orderBy("fechaLimite", true)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtiene tareas con fecha límite vencida
     */
    public List<Tarea> obtenerTareasVencidas() {
        try {
            return ConexionBD.query(Tarea.class)
                    .where("estado", "=", EstadoTarea.PENDIENTE)
                    .where("fechaLimite", "<", new Date())
                    .orderBy("fechaLimite", true)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Marca una tarea como completada
     */
    public boolean completarTarea(Integer idTarea, String comentarios) {
        try {
            Tarea tarea = obtenerPorId(idTarea);
            if (tarea != null && tarea.getEstado() == EstadoTarea.PENDIENTE) {
                tarea.setEstado(EstadoTarea.COMPLETADA);
                tarea.setFechaCompletada(new Date());
                tarea.setComentariosCompletado(comentarios);
                return actualizar(tarea);
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}