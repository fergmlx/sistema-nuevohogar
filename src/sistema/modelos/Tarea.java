package sistema.modelos;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tareas")
public class Tarea {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tarea")
    private Integer idTarea;
    
    @Column(name = "titulo", nullable = false)
    private String titulo;
    
    @Column(name = "descripcion", length = 1000)
    private String descripcion;
    
    @Column(name = "fecha_asignacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaAsignacion;
    
    @Column(name = "fecha_limite")
    @Temporal(TemporalType.DATE)
    private Date fechaLimite;
    
    @Column(name = "estado")
    @Enumerated(EnumType.STRING)
    private EstadoTarea estado;
    
    @Column(name = "prioridad")
    @Enumerated(EnumType.STRING)
    private PrioridadTarea prioridad;
    
    @ManyToOne
    @JoinColumn(name = "id_voluntario")
    private Usuario voluntario;
    
    @ManyToOne
    @JoinColumn(name = "id_asignador")
    private Usuario asignador;
    
    @ManyToOne
    @JoinColumn(name = "id_animal")
    private Animal animal;
    
    @Column(name = "fecha_completada")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCompletada;
    
    @Column(name = "comentarios_completado", length = 500)
    private String comentariosCompletado;
    
    // Enumeraciones
    public enum EstadoTarea {
        PENDIENTE, COMPLETADA, CANCELADA, EN_PROGRESO, RETRASADA
    }
    
    public enum PrioridadTarea {
        BAJA, MEDIA, ALTA, URGENTE
    }
    
    // Getters y setters
    public Integer getIdTarea() {
        return idTarea;
    }

    public void setIdTarea(Integer idTarea) {
        this.idTarea = idTarea;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(Date fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    public Date getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(Date fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public EstadoTarea getEstado() {
        return estado;
    }

    public void setEstado(EstadoTarea estado) {
        this.estado = estado;
    }

    public PrioridadTarea getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(PrioridadTarea prioridad) {
        this.prioridad = prioridad;
    }

    public Usuario getVoluntario() {
        return voluntario;
    }

    public void setVoluntario(Usuario voluntario) {
        this.voluntario = voluntario;
    }

    public Usuario getAsignador() {
        return asignador;
    }

    public void setAsignador(Usuario asignador) {
        this.asignador = asignador;
    }

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }

    public Date getFechaCompletada() {
        return fechaCompletada;
    }

    public void setFechaCompletada(Date fechaCompletada) {
        this.fechaCompletada = fechaCompletada;
    }

    public String getComentariosCompletado() {
        return comentariosCompletado;
    }

    public void setComentariosCompletado(String comentariosCompletado) {
        this.comentariosCompletado = comentariosCompletado;
    }
}