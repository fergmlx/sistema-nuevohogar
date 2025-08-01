package sistema.dao;

import conexiondb.core.ConexionBD;
import sistema.modelos.Evento;
import sistema.modelos.Evento.TipoEvento;
import sistema.modelos.Usuario;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventoDAO {
    
    /**
     * Guarda un nuevo evento
     */
    public boolean guardar(Evento evento) {
        try {
            ConexionBD.manage(Evento.class).save(evento);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Actualiza un evento
     */
    public boolean actualizar(Evento evento) {
        try {
            ConexionBD.manage(Evento.class).update(evento);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Elimina un evento
     */
    public boolean eliminar(Integer idEvento) {
        try {
            Evento evento = obtenerPorId(idEvento);
            if (evento != null) {
                ConexionBD.manage(Evento.class).delete(evento);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Obtiene un evento por ID
     */
    public Evento obtenerPorId(Integer idEvento) {
        try {
            return ConexionBD.manage(Evento.class).findById(idEvento).orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Obtiene todos los eventos
     */
    public List<Evento> obtenerTodos() {
        try {
            return ConexionBD.query(Evento.class)
                    .orderBy("fechaInicio", true)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtiene eventos por tipo
     */
    public List<Evento> obtenerPorTipo(TipoEvento tipoEvento) {
        try {
            return ConexionBD.query(Evento.class)
                    .where("tipoEvento", "=", tipoEvento)
                    .orderBy("fechaInicio", true)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtiene eventos por organizador
     */
    public List<Evento> obtenerPorOrganizador(Usuario organizador) {
        try {
            return ConexionBD.query(Evento.class)
                    .where("organizador", "=", organizador)
                    .orderBy("fechaInicio", true)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtiene eventos en un rango de fechas
     */
    public static List<Evento> obtenerPorRangoFechas(Date fechaInicio, Date fechaFin) {
        try {
            return ConexionBD.query(Evento.class)
                    .where("fechaInicio", ">=", fechaInicio)
                    .where("fechaInicio", "<=", fechaFin)
                    .orderBy("fechaInicio", true)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtiene eventos futuros
     */
    public static List<Evento> obtenerEventosFuturos() {
        try {
            return ConexionBD.query(Evento.class)
                    .where("fechaInicio", ">=", new Date())
                    .orderBy("fechaInicio", true)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Inscribe un usuario a un evento
     */
    public boolean inscribirUsuario(Integer idEvento, Usuario usuario) {
        try {
            Evento evento = obtenerPorId(idEvento);
            if (evento != null) {
                // Verificar si ya está inscrito
                if (evento.getParticipantes() != null && evento.getParticipantes().contains(usuario)) {
                    return true; // Ya está inscrito
                }
                
                // Añadir participante
                if (evento.getParticipantes() == null) {
                    evento.setParticipantes(new ArrayList<>());
                }
                evento.getParticipantes().add(usuario);
                return actualizar(evento);
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Cancela la inscripción de un usuario a un evento
     */
    public boolean cancelarInscripcion(Integer idEvento, Usuario usuario) {
        try {
            Evento evento = obtenerPorId(idEvento);
            if (evento != null && evento.getParticipantes() != null) {
                evento.getParticipantes().remove(usuario);
                return actualizar(evento);
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}