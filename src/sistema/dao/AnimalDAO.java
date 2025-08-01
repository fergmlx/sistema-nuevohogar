package sistema.dao;

import conexiondb.core.ConexionBD;
import sistema.modelos.Animal;
import sistema.modelos.Animal.EstadoAnimal;

import java.util.ArrayList;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import sistema.modelos.Animal.CondicionSalud;

public class AnimalDAO {
    
    /**
     * Guarda un nuevo animal en la base de datos
     */
    public static boolean guardar(Animal animal) {
        try {
            // Si es nuevo, establecer fecha de ingreso
            if (animal.getFechaIngreso() == null) {
                java.util.Date utilDate = new java.util.Date();
                animal.setFechaIngreso(LocalDate.now());
            }
            
            ConexionBD.manage(Animal.class).save(animal);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Actualiza los datos de un animal existente
     */
    public static boolean actualizar(Animal animal) {
        try {
            ConexionBD.manage(Animal.class).update(animal);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Elimina un animal de la base de datos
     */
    public static boolean eliminar(Integer idAnimal) {
        try {
            Animal animal = obtenerPorId(idAnimal);
            if (animal != null) {
                ConexionBD.manage(Animal.class).delete(animal);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Obtiene un animal por su ID
     */
    public static Animal obtenerPorId(Integer idAnimal) {
        try {
            return ConexionBD.manage(Animal.class).findById(idAnimal).orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Obtiene todos los animales
     */
    public static List<Animal> obtenerTodos() {
        try {
            return ConexionBD.query(Animal.class)
                    .orderBy("idAnimal", true)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtiene animales por estado
     */
    public List<Animal> obtenerPorEstado(EstadoAnimal estado) {
        try {
            return ConexionBD.query(Animal.class)
                    .where("estado", "=", estado)
                    .orderBy("fechaIngreso", false)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtiene animales por condicion
     */
    public static List<Animal> obtenerPorCondicion(CondicionSalud condicion) {
        try {
            return ConexionBD.query(Animal.class)
                    .where("condicionSalud", "=", condicion)
                    .orderBy("fechaIngreso", false)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    
    /**
     * Busca animales por nombre
     */
    public static List<Animal> buscarPorNombre(String nombre) {
        try {
            return ConexionBD.query(Animal.class)
                    .where("nombre", "LIKE", "%" + nombre + "%")
                    .orderBy("idAnimal", true)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Busca animales por especie
     */
    public List<Animal> buscarPorEspecie(String especie) {
        try {
            return ConexionBD.query(Animal.class)
                    .where("especie", "LIKE", "%" + especie + "%")
                    .orderBy("nombre", true)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtiene animales en tratamiento médico
     */
    public List<Animal> obtenerAnimalesEnTratamiento() {
        try {
            return ConexionBD.query(Animal.class)
                    .where("condicion_salud", "=", CondicionSalud.EN_TRATAMIENTO)
                    .orderBy("fechaIngreso", false)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtiene animales disponibles para adopción
     */
    public static List<Animal> obtenerAnimalesDisponibles() {
        try {
            return ConexionBD.query(Animal.class)
                    .where("estado", "=", EstadoAnimal.DISPONIBLE)
                    .orderBy("fechaIngreso", true) // Los más antiguos primero
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Búsqueda avanzada con múltiples criterios
     */
    public List<Animal> busquedaAvanzada(String nombre, String especie, String raza, 
                                          Animal.Sexo sexo, EstadoAnimal estado, 
                                          Boolean esterilizado) {
        try {
            // Crear un query builder
            var query = ConexionBD.query(Animal.class);
            
            // Añadir criterios si no son nulos
            if (nombre != null && !nombre.isEmpty()) {
                query.where("nombre", "LIKE", "%" + nombre + "%");
            }
            
            if (especie != null && !especie.isEmpty()) {
                query.where("especie", "LIKE", "%" + especie + "%");
            }
            
            if (raza != null && !raza.isEmpty()) {
                query.where("raza", "LIKE", "%" + raza + "%");
            }
            
            if (sexo != null) {
                query.where("sexo", "=", sexo);
            }
            
            if (estado != null) {
                query.where("estado", "=", estado);
            }
            
            if (esterilizado != null) {
                query.where("esterilizado", "=", esterilizado);
            }
            
            return query.orderBy("fechaIngreso", false).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
    * Obtiene animales con paginación
    */
    public static List<Animal> obtenerConPaginacion(int page, int limit) {
        try {
            int offset = (page - 1) * limit;
            return ConexionBD.query(Animal.class)
                    .orderBy("idAnimal", true)
                    .limit(limit)
                    .offset(offset)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }    

   /**
    * Obtiene el total de registros para calcular páginas
    */
    public static int contarTodos() {
        try {
            return (int) ConexionBD.query(Animal.class)
                    .count();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

   /**
    * Busca animales por nombre con paginación
    */
    public static List<Animal> buscarPorNombreConPaginacion(String nombre, int page, int limit) {
        try {
            int offset = (page - 1) * limit;
            return ConexionBD.query(Animal.class)
                    .where("nombre", "LIKE", "%" + nombre + "%")
                    .orderBy("idAnimal", true)
                    .limit(limit)
                    .offset(offset)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

   /**
    * Cuenta registros que coinciden con búsqueda por nombre
    */
    public static int contarPorNombre(String nombre) {
        try {
            return (int) ConexionBD.query(Animal.class)
                    .where("nombre", "LIKE", "%" + nombre + "%")
                    .count();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}