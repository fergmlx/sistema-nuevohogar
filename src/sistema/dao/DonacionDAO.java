package sistema.dao;

import conexiondb.core.ConexionBD;
import sistema.modelos.Donacion;
import sistema.modelos.Donacion.TipoDonacion;
import sistema.modelos.Usuario;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DonacionDAO {
    
    /**
     * Guarda una nueva donación
     */
    public boolean guardar(Donacion donacion) {
        try {
            // Si es nueva, establecer fecha
            if (donacion.getFechaDonacion() == null) {
                donacion.setFechaDonacion(new Date());
            }
            
            ConexionBD.manage(Donacion.class).save(donacion);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Actualiza una donación
     */
    public boolean actualizar(Donacion donacion) {
        try {
            ConexionBD.manage(Donacion.class).update(donacion);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Elimina una donación
     */
    public boolean eliminar(Integer idDonacion) {
        try {
            Donacion donacion = obtenerPorId(idDonacion);
            if (donacion != null) {
                ConexionBD.manage(Donacion.class).delete(donacion);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Obtiene una donación por ID
     */
    public Donacion obtenerPorId(Integer idDonacion) {
        try {
            return ConexionBD.manage(Donacion.class).findById(idDonacion).orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Obtiene todas las donaciones
     */
    public List<Donacion> obtenerTodas() {
        try {
            return ConexionBD.query(Donacion.class)
                    .orderBy("fechaDonacion", false)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtiene donaciones por tipo
     */
    public List<Donacion> obtenerPorTipo(TipoDonacion tipoDonacion) {
        try {
            return ConexionBD.query(Donacion.class)
                    .where("tipoDonacion", "=", tipoDonacion)
                    .orderBy("fechaDonacion", false)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtiene donaciones por receptor
     */
    public List<Donacion> obtenerPorReceptor(Usuario receptor) {
        try {
            return ConexionBD.query(Donacion.class)
                    .where("receptor", "=", receptor)
                    .orderBy("fechaDonacion", false)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Busca donaciones por nombre de donante
     */
    public List<Donacion> buscarPorDonante(String nombreDonante) {
        try {
            return ConexionBD.query(Donacion.class)
                    .where("nombreDonante", "LIKE", "%" + nombreDonante + "%")
                    .orderBy("fechaDonacion", false)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtiene donaciones por rango de fechas
     */
    public static List<Donacion> obtenerPorRangoFechas(Date fechaInicio, Date fechaFin) {
        try {
            return ConexionBD.query(Donacion.class)
                    .where("fechaDonacion", ">=", fechaInicio)
                    .where("fechaDonacion", "<=", fechaFin)
                    .orderBy("fechaDonacion", false)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtiene donaciones monetarias y calcula el total
     */
    public static double calcularTotalDonacionesMonetarias(Date fechaInicio, Date fechaFin) {
        try {
            List<Donacion> donaciones = ConexionBD.query(Donacion.class)
                    .where("tipoDonacion", "=", TipoDonacion.MONETARIA)
                    .where("fechaDonacion", ">=", fechaInicio)
                    .where("fechaDonacion", "<=", fechaFin)
                    .getResultList();
            
            double total = 0;
            for (Donacion d : donaciones) {
                if (d.getMonto() != null) {
                    total += d.getMonto();
                }
            }
            
            return total;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    /**
     * Genera recibo para una donación
     */
    public boolean generarRecibo(Integer idDonacion, String numeroRecibo) {
        try {
            Donacion donacion = obtenerPorId(idDonacion);
            if (donacion != null && !donacion.getReciboEmitido()) {
                donacion.setReciboEmitido(true);
                donacion.setNumeroRecibo(numeroRecibo);
                return actualizar(donacion);
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}