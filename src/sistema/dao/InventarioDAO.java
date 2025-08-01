package sistema.dao;

import conexiondb.core.ConexionBD;
import sistema.modelos.Inventario;
import sistema.modelos.Inventario.CategoriaItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InventarioDAO {
    
    /**
     * Guarda un nuevo item de inventario
     */
    public boolean guardar(Inventario item) {
        try {
            // Si es nuevo, establecer fecha de adquisición
            if (item.getFechaAdquisicion() == null) {
                item.setFechaAdquisicion(new Date());
            }
            
            ConexionBD.manage(Inventario.class).save(item);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Actualiza un item de inventario
     */
    public boolean actualizar(Inventario item) {
        try {
            ConexionBD.manage(Inventario.class).update(item);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Elimina un item de inventario
     */
    public boolean eliminar(Integer idItem) {
        try {
            Inventario item = obtenerPorId(idItem);
            if (item != null) {
                ConexionBD.manage(Inventario.class).delete(item);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Obtiene un item por ID
     */
    public Inventario obtenerPorId(Integer idItem) {
        try {
            return ConexionBD.manage(Inventario.class).findById(idItem).orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Obtiene todos los items de inventario
     */
    public List<Inventario> obtenerTodos() {
        try {
            return ConexionBD.query(Inventario.class)
                    .orderBy("nombre", true)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtiene items por categoría
     */
    public List<Inventario> obtenerPorCategoria(CategoriaItem categoria) {
        try {
            return ConexionBD.query(Inventario.class)
                    .where("categoria", "=", categoria)
                    .orderBy("nombre", true)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Busca items por nombre
     */
    public List<Inventario> buscarPorNombre(String nombre) {
        try {
            return ConexionBD.query(Inventario.class)
                    .where("nombre", "LIKE", "%" + nombre + "%")
                    .orderBy("nombre", true)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtiene items con stock bajo
     */
    public static List<Inventario> obtenerItemsStockBajo() {
        try {
            // Obtener items donde la cantidad es menor o igual al stock mínimo
            return ConexionBD.query(Inventario.class)
                    .where("cantidad", "<=", "stockMinimo")
                    .where("stockMinimo", ">", 0) // Solo items con stock mínimo definido
                    .orderBy("cantidad", true) // Los de menor stock primero
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtiene items próximos a caducar
     */
    public List<Inventario> obtenerItemsProximosACaducar(Date fechaLimite) {
        try {
            return ConexionBD.query(Inventario.class)
                    .where("fechaCaducidad", "<=", fechaLimite)
                    .where("fechaCaducidad", ">=", new Date()) // Solo items que aún no han caducado
                    .orderBy("fechaCaducidad", true) // Los más próximos primero
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Actualiza la cantidad de un item
     */
    public boolean actualizarCantidad(Integer idItem, Integer nuevaCantidad) {
        try {
            Inventario item = obtenerPorId(idItem);
            if (item != null) {
                item.setCantidad(nuevaCantidad);
                
                // Verificar si se debe generar alerta por stock bajo
                if (item.getStockMinimo() != null && nuevaCantidad <= item.getStockMinimo()) {
                    item.setAlertaGenerada(true);
                } else {
                    item.setAlertaGenerada(false);
                }
                
                return actualizar(item);
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Registra salida de stock
     */
    public boolean registrarSalida(Integer idItem, Integer cantidad) {
        try {
            Inventario item = obtenerPorId(idItem);
            if (item != null && item.getCantidad() >= cantidad) {
                int nuevaCantidad = item.getCantidad() - cantidad;
                return actualizarCantidad(idItem, nuevaCantidad);
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Registra entrada de stock
     */
    public boolean registrarEntrada(Integer idItem, Integer cantidad) {
        try {
            Inventario item = obtenerPorId(idItem);
            if (item != null) {
                int nuevaCantidad = item.getCantidad() + cantidad;
                return actualizarCantidad(idItem, nuevaCantidad);
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}