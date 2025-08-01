package sistema.modelos;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "eventos")
public class Evento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evento")
    private Integer idEvento;
    
    @Column(name = "titulo", nullable = false)
    private String titulo;
    
    @Column(name = "descripcion", length = 1000)
    private String descripcion;
    
    @Column(name = "fecha_inicio")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaInicio;
    
    @Column(name = "fecha_fin")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaFin;
    
    @Column(name = "ubicacion")
    private String ubicacion;
    
    @Column(name = "tipo_evento")
    @Enumerated(EnumType.STRING)
    private TipoEvento tipoEvento;
    
    @ManyToOne
    @JoinColumn(name = "id_organizador")
    private Usuario organizador;
    
    @ManyToMany
    @JoinTable(
        name = "participantes_eventos",
        joinColumns = @JoinColumn(name = "id_evento"),
        inverseJoinColumns = @JoinColumn(name = "id_usuario")
    )
    private List<Usuario> participantes;
    
    // Enumeraciones
    public enum TipoEvento {
        JORNADA_ADOPCION, CAMPAÑA_VACUNACION, CAPACITACION, RECAUDACION, VOLUNTARIADO, OTRO
    }
    
    // Getters y setters
    public Integer getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(Integer idEvento) {
        this.idEvento = idEvento;
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

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public TipoEvento getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(TipoEvento tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public Usuario getOrganizador() {
        return organizador;
    }

    public void setOrganizador(Usuario organizador) {
        this.organizador = organizador;
    }

    public List<Usuario> getParticipantes() {
        return participantes;
    }

    public void setParticipantes(List<Usuario> participantes) {
        this.participantes = participantes;
    }
}