package sistema.dao;

import conexiondb.core.ConexionBD;
import java.time.LocalDate;
import sistema.modelos.Adopcion;
import sistema.modelos.Adopcion.EstadoAdopcion;
import sistema.modelos.Animal;
import sistema.modelos.Usuario;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdopcionDAO {
    
    /**
     * Guarda una nueva solicitud de adopción
     */
    public static boolean guardar(Adopcion adopcion) {
        try {
            // Si es nueva, establecer fecha de solicitud
            if (adopcion.getFechaSolicitud() == null) {
                adopcion.setFechaSolicitud(LocalDate.now());
            }
            
            // Si no se especifica un estado, establecer como PENDIENTE
            if (adopcion.getEstado() == null) {
                adopcion.setEstado(EstadoAdopcion.PENDIENTE);
            }
            
            ConexionBD.manage(Adopcion.class).save(adopcion);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Actualiza una solicitud de adopción
     */
    public static boolean actualizar(Adopcion adopcion) {
        try {
            ConexionBD.manage(Adopcion.class).update(adopcion);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Elimina una solicitud de adopción
     */
    public static boolean eliminar(Integer idAdopcion) {
        try {
            Adopcion adopcion = obtenerPorId(idAdopcion);
            if (adopcion != null) {
                ConexionBD.manage(Adopcion.class).delete(adopcion);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Obtiene una adopción por ID
     */
    public static Adopcion obtenerPorId(Integer idAdopcion) {
        try {
            return ConexionBD.manage(Adopcion.class).findById(idAdopcion).orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Obtiene todas las adopciones
     */
    public static List<Adopcion> obtenerTodas() {
        try {
            return ConexionBD.query(Adopcion.class)
                    .orderBy("fechaSolicitud", false)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtiene adopciones por estado
     */
    public static List<Adopcion> obtenerPorEstado(EstadoAdopcion estado) {
        try {
            return ConexionBD.query(Adopcion.class)
                    .where("estado", "=", estado)
                    .orderBy("fechaSolicitud", false)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtiene adopciones pendientes
     */
    public static List<Adopcion> obtenerPendientes() {
        return obtenerPorEstado(EstadoAdopcion.PENDIENTE);
    }
    
    /**
     * Obtiene adopciones por animal
     */
    public static List<Adopcion> obtenerPorAnimal(Animal animal) {
        try {
            return ConexionBD.query(Adopcion.class)
                    .where("animal", "=", animal)
                    .orderBy("fechaSolicitud", false)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtiene adopciones por aprobador
     */
    public static List<Adopcion> obtenerPorAprobador(Usuario aprobador) {
        try {
            return ConexionBD.query(Adopcion.class)
                    .where("aprobador", "=", aprobador)
                    .orderBy("fechaAprobacion", false)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Busca adopciones por nombre o email del adoptante
     */
    public static List<Adopcion> buscarPorAdoptante(String busqueda) {
        try {
            List<Adopcion> porNombre = ConexionBD.query(Adopcion.class)
                    .where("nombreAdoptante", "LIKE", "%" + busqueda + "%")
                    .getResultList();
            
            List<Adopcion> porEmail = ConexionBD.query(Adopcion.class)
                    .where("emailAdoptante", "LIKE", "%" + busqueda + "%")
                    .getResultList();
            
            // Combinar resultados sin duplicados
            for (Adopcion adopcion : porEmail) {
                if (!porNombre.contains(adopcion)) {
                    porNombre.add(adopcion);
                }
            }
            
            return porNombre;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtiene adopciones con seguimiento programado
     */
    public static List<Adopcion> obtenerConSeguimiento(Date fechaLimite) {
        try {
            return ConexionBD.query(Adopcion.class)
                    .where("seguimientoProgramado", "<=", fechaLimite)
                    .where("seguimientoProgramado", ">=", new Date())
                    .where("estado", "=", EstadoAdopcion.FINALIZADA)
                    .orderBy("seguimientoProgramado", true)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Aprueba una solicitud de adopción
     */
    public static boolean aprobarSolicitud(Integer idAdopcion, Usuario aprobador) {
        try {
            Adopcion adopcion = obtenerPorId(idAdopcion);
            if (adopcion != null && adopcion.getEstado() == EstadoAdopcion.PENDIENTE) {
                adopcion.setEstado(EstadoAdopcion.APROBADA);
                adopcion.setFechaResolucion(LocalDate.now());
                adopcion.setAprobador(aprobador);
                
                // También actualizar el estado del animal a ADOPTADO
                Animal animal = adopcion.getAnimal();
                if (animal != null) {
                    animal.setEstado(Animal.EstadoAnimal.ADOPTADO);
                    AnimalDAO animalDAO = new AnimalDAO();
                    animalDAO.actualizar(animal);
                }
                
                return actualizar(adopcion);
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Rechaza una solicitud de adopción
     */
    public static boolean rechazarSolicitud(Integer idAdopcion, String motivo, Usuario aprobador) {
        try {
            Adopcion adopcion = obtenerPorId(idAdopcion);
            if (adopcion != null && adopcion.getEstado() == EstadoAdopcion.PENDIENTE) {
                adopcion.setEstado(EstadoAdopcion.RECHAZADA);
                adopcion.setFechaResolucion(LocalDate.now());
                adopcion.setAprobador(aprobador);
                adopcion.setMotivoRechazo(motivo);
                return actualizar(adopcion);
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Finaliza una adopción (entrega del animal)
     */
    public static boolean finalizarAdopcion(Integer idAdopcion, LocalDate fechaEntrega) {
        try {
            Adopcion adopcion = obtenerPorId(idAdopcion);
            if (adopcion != null && adopcion.getEstado() == EstadoAdopcion.APROBADA) {
                adopcion.setEstado(EstadoAdopcion.FINALIZADA);
                adopcion.setFechaEntrega(fechaEntrega != null ? fechaEntrega : LocalDate.now());
                return actualizar(adopcion);
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Actualiza la fecha de último acceso del usuario
     * 
     * @param id ID del usuario
     * @return true si la operación fue exitosa
     */
    public static boolean actualizarEstado(Integer id, EstadoAdopcion estado) {
        try {
            ConexionBD.transaction(tx -> {
                Adopcion adopcion = tx.get(Adopcion.class, id);
                if (adopcion != null) {
                    adopcion.setEstado(estado);
                    tx.update(adopcion);
                }
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
    * Obtiene adopciones con paginación
    */
    public static List<Adopcion> obtenerConPaginacion(int page, int limit) {
        try {
            int offset = (page - 1) * limit;
            return ConexionBD.query(Adopcion.class)
                    .orderBy("idAdopcion", true)
                    .limit(limit)
                    .offset(offset)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

   /**
    * Obtiene el total de registros de adopciones
    */
    public static int contarTodos() {
        try {
            return (int) ConexionBD.query(Adopcion.class)
                    .count();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

   /**
    * Busca adopciones por nombre de adoptante con paginación
    */
    public static List<Adopcion> buscarPorAdoptanteConPaginacion(String nombre, int page, int limit) {
        try {
            int offset = (page - 1) * limit;
            return ConexionBD.query(Adopcion.class)
                    .where("nombreAdoptante", "LIKE", "%" + nombre + "%")
                    .orderBy("idAdopcion", true)
                    .limit(limit)
                    .offset(offset)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

   /**
    * Cuenta registros de adopciones que coinciden con búsqueda por nombre de adoptante
    */
    public static int contarPorAdoptante(String nombre) {
        try {
            return (int) ConexionBD.query(Adopcion.class)
                    .where("nombreAdoptante", "LIKE", "%" + nombre + "%")
                    .count();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}