package sistema.modelos;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "actividades")
public class Actividad {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_actividad")
    private Integer idActividad;
    
    @ManyToOne
    @JoinColumn(name = "id_animal", nullable = false)
    private Animal animal;
    
    @Column(name = "tipo_actividad")
    @Enumerated(EnumType.STRING)
    private TipoActividad tipoActividad;
    
    @Column(name = "fecha")
    private LocalDateTime fecha;
    
    @Column(name = "descripcion", length = 1000)
    private String descripcion;
    
    @Column(name = "observaciones", length = 500)
    private String observaciones;
    
    @ManyToOne
    @JoinColumn(name = "id_responsable")
    private Usuario responsable;
    
    // Enumeraciones
    public enum TipoActividad {
        PASEO, JUEGO, ENTRENAMIENTO, SOCIALIZACIÓN, BAÑO, CEPILLADO, OTRO
    }
    
    // Getters y setters
    public Integer getIdActividad() {
        return idActividad;
    }

    public void setIdActividad(Integer idActividad) {
        this.idActividad = idActividad;
    }

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }

    public TipoActividad getTipoActividad() {
        return tipoActividad;
    }

    public void setTipoActividad(TipoActividad tipoActividad) {
        this.tipoActividad = tipoActividad;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Usuario getResponsable() {
        return responsable;
    }

    public void setResponsable(Usuario responsable) {
        this.responsable = responsable;
    }
}