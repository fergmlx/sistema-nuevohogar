package sistema.dao;

import java.util.List;
import java.util.Optional;

import conexiondb.core.ConexionBD;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import sistema.modelos.Usuario;

public class UsuarioDAO {
    
    public static boolean guardar(Usuario usuario) {
        try {
            ConexionBD.manage(Usuario.class).save(usuario);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static Usuario obtenerPorId(Integer id) {
        try {
            return ConexionBD.manage(Usuario.class).findById(id).orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static Usuario obtenerPorEmail(String email) {
        try {
            return ConexionBD.query(Usuario.class)
                    .where("email", email)
                    .getSingleResult().orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static List<Usuario> obtenerTodos() {
        try {
            return ConexionBD.manage(Usuario.class).findAll();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static boolean actualizar(Usuario usuario) {
        try {
            ConexionBD.manage(Usuario.class).update(usuario);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean eliminar(Integer id) {
        try {
            Optional<Usuario> usuario = ConexionBD.manage(Usuario.class).findById(id);
            if (usuario.isPresent()) {
                ConexionBD.manage(Usuario.class).delete(usuario.get());
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Verifica si existe un usuario con el email dado
     * 
     * @param email Email a verificar
     * @return true si existe un usuario con ese email
     */
    public boolean existeEmail(String email) {
        try {
            long count = ConexionBD.query(Usuario.class)
                    .where("email", email)
                    .count();
            return count > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Obtiene usuarios por rol
     * 
     * @param rol Rol de usuario
     * @return Lista de usuarios con el rol especificado
     */
    public List<Usuario> obtenerPorRol(Usuario.RolUsuario rol) {
        try {
            return ConexionBD.query(Usuario.class)
                    .where("rol", rol)
                    .orderBy("nombre", true)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }
    
    /**
    * Busca usuarios por nombre o email
    * 
    * @param termino Término de búsqueda
    * @return Lista de usuarios que coinciden con el término
    */
    public List<Usuario> buscarPorNombreOEmail(String termino) {
        try {
            // Usar Set para evitar duplicados automáticamente
            Set<Usuario> resultados = new LinkedHashSet<>();

            // Buscar por nombre usando ConexionBD
            List<Usuario> resultadosPorNombre = ConexionBD.query(Usuario.class)
                    .where("nombre", "LIKE", "%" + termino + "%")
                    .orderBy("nombre", true)
                    .getResultList();
            resultados.addAll(resultadosPorNombre);

            // También buscar por email
            List<Usuario> resultadosPorEmail = ConexionBD.query(Usuario.class)
                    .where("email", "LIKE", "%" + termino + "%")
                    .orderBy("nombre", true)
                    .getResultList();
            resultados.addAll(resultadosPorEmail);

            // Convertir a lista para retornar
            return new ArrayList<>(resultados);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Actualiza la fecha de último acceso del usuario
     * 
     * @param id ID del usuario
     * @return true si la operación fue exitosa
     */
    public static boolean actualizarUltimoAcceso(Integer id) {
        try {
            ConexionBD.transaction(tx -> {
                Usuario usuario = tx.get(Usuario.class, id);
                if (usuario != null) {
                    usuario.setUltimoAcceso(new Date());
                    tx.update(usuario);
                }
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}