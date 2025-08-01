package sistema.modelos;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "inventario")
public class Inventario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item")
    private Integer idItem;
    
    @Column(name = "nombre", nullable = false)
    private String nombre;
    
    @Column(name = "categoria")
    @Enumerated(EnumType.STRING)
    private CategoriaItem categoria;
    
    @Column(name = "descripcion", length = 500)
    private String descripcion;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_item", nullable = false)
    private EstadoItem estadoItem;
    
    @Column(name = "cantidad")
    private Integer cantidad;
    
    @Column(name = "unidad_medida")
    private String unidadMedida;
    
    @Column(name = "fecha_adquisicion")
    @Temporal(TemporalType.DATE)
    private Date fechaAdquisicion;
    
    @Column(name = "fecha_caducidad")
    @Temporal(TemporalType.DATE)
    private Date fechaCaducidad;
    
    @Column(name = "ubicacion")
    private String ubicacion;
    
    @Column(name = "precio_unitario")
    private Double precioUnitario;
    
    @Column(name = "proveedor")
    private String proveedor;
    
    @Column(name = "stock_minimo")
    private Integer stockMinimo;
    
    @Column(name = "alerta_generada")
    private Boolean alertaGenerada;
    
    @ManyToOne
    @JoinColumn(name = "id_donacion")
    private Donacion donacion;
    
    // Enumeraciones
    public enum CategoriaItem {
        MEDICAMENTO, ALIMENTO, LIMPIEZA, ACCESORIOS, EQUIPAMIENTO, MATERIAL_MEDICO, OTRO
    }
    
    public enum EstadoItem {
        DISPONIBLE,
        AGOTADO,
        CADUCADO
    }
    
    // Getters y setters
    public Integer getIdItem() {
        return idItem;
    }

    public void setIdItem(Integer idItem) {
        this.idItem = idItem;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public CategoriaItem getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaItem categoria) {
        this.categoria = categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public EstadoItem getEstadoItem() {
        return estadoItem;
    }

    public void setEstadoItem(EstadoItem estadoItem) {
        this.estadoItem = estadoItem;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public Date getFechaAdquisicion() {
        return fechaAdquisicion;
    }

    public void setFechaAdquisicion(Date fechaAdquisicion) {
        this.fechaAdquisicion = fechaAdquisicion;
    }

    public Date getFechaCaducidad() {
        return fechaCaducidad;
    }

    public void setFechaCaducidad(Date fechaCaducidad) {
        this.fechaCaducidad = fechaCaducidad;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public Double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(Double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    public Integer getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(Integer stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public Boolean getAlertaGenerada() {
        return alertaGenerada;
    }

    public void setAlertaGenerada(Boolean alertaGenerada) {
        this.alertaGenerada = alertaGenerada;
    }

    public Donacion getDonacion() {
        return donacion;
    }

    public void setDonacion(Donacion donacion) {
        this.donacion = donacion;
    }
}