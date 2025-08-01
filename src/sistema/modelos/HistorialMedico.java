package sistema.modelos;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "historiales_medicos")
public class HistorialMedico {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historial")
    private Integer idHistorial;
    
    @ManyToOne
    @JoinColumn(name = "id_animal", nullable = false)
    private Animal animal;
    
    @Column(name = "fecha", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    
    @Column(name = "tipo_consulta")
    @Enumerated(EnumType.STRING)
    private TipoConsulta tipoConsulta;
    
    @Column(name = "peso")
    private Double peso;
    
    @Column(name = "temperatura")
    private Double temperatura;
    
    @Column(name = "sintomas", length = 1000)
    private String sintomas;
    
    @Column(name = "diagnostico", length = 1000)
    private String diagnostico;
    
    @Column(name = "tratamiento", length = 1000)
    private String tratamiento;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_tratamiento", nullable = false)
    private EstadoTratamiento estadoTratamiento;
    
    @Column(name = "medicamentos", length = 500)
    private String medicamentos;
    
    @Column(name = "observaciones", length = 1000)
    private String observaciones;
    
    @Column(name = "proxima_revision")
    @Temporal(TemporalType.DATE)
    private Date proximaRevision;
    
    @ManyToOne
    @JoinColumn(name = "id_veterinario")
    private Usuario veterinario;
    
    // Enumeraciones
    public enum TipoConsulta {
        REVISION_GENERAL, VACUNACION, DESPARASITACION, TRATAMIENTO, CIRUGIA, EMERGENCIA, OTRO
    }
    
    public enum EstadoTratamiento {
        EN_CURSO,
        COMPLETADO,
        PENDIENTE
    }
    
    // Getters y setters
    public Integer getIdHistorial() {
        return idHistorial;
    }

    public void setIdHistorial(Integer idHistorial) {
        this.idHistorial = idHistorial;
    }

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public TipoConsulta getTipoConsulta() {
        return tipoConsulta;
    }

    public void setTipoConsulta(TipoConsulta tipoConsulta) {
        this.tipoConsulta = tipoConsulta;
    }

    public Double getPeso() {
        return peso;
    }

    public void setPeso(Double peso) {
        this.peso = peso;
    }

    public Double getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(Double temperatura) {
        this.temperatura = temperatura;
    }

    public String getSintomas() {
        return sintomas;
    }

    public void setSintomas(String sintomas) {
        this.sintomas = sintomas;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public String getTratamiento() {
        return tratamiento;
    }

    public void setTratamiento(String tratamiento) {
        this.tratamiento = tratamiento;
    }
    
    public EstadoTratamiento getEstadoTratamiento() {
        return estadoTratamiento;
    }

    public void setEstadoTratamiento(EstadoTratamiento estadoTratamiento) {
        this.estadoTratamiento = estadoTratamiento;
    }

    public String getMedicamentos() {
        return medicamentos;
    }

    public void setMedicamentos(String medicamentos) {
        this.medicamentos = medicamentos;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Date getProximaRevision() {
        return proximaRevision;
    }

    public void setProximaRevision(Date proximaRevision) {
        this.proximaRevision = proximaRevision;
    }

    public Usuario getVeterinario() {
        return veterinario;
    }

    public void setVeterinario(Usuario veterinario) {
        this.veterinario = veterinario;
    }
}