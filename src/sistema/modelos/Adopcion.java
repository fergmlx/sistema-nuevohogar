package sistema.modelos;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import jakarta.persistence.*;
import java.awt.Color;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import javax.swing.Icon;
import sistema.custom.TableBadgeCellRenderer;

@Entity
@Table(name = "adopciones")
public class Adopcion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_adopcion")
    private Integer idAdopcion;
    
    @ManyToOne
    @JoinColumn(name = "id_animal", nullable = false)
    private Animal animal;
    
    @Column(name = "nombre_adoptante", nullable = false)
    private String nombreAdoptante;
    
    @Column(name = "email_adoptante")
    private String emailAdoptante;
    
    @Column(name = "telefono_adoptante")
    private String telefonoAdoptante;
    
    @Column(name = "direccion_adoptante")
    private String direccionAdoptante;
    
    @Column(name = "fecha_solicitud")
    private LocalDate fechaSolicitud;
    
    @Column(name = "fecha_resolucion")
    private LocalDate fechaResolucion;
    
    @Column(name = "fecha_entrega")
    private LocalDate fechaEntrega;
    
    @Column(name = "estado")
    @Enumerated(EnumType.STRING)
    private EstadoAdopcion estado;
    
    @Column(name = "motivo_rechazo", length = 500)
    private String motivoRechazo;
    
    @Column(name = "compromisos", length = 1000)
    private String compromisos;
    
    @Column(name = "seguimiento_programado")
    private LocalDate seguimientoProgramado;
    
    @ManyToOne
    @JoinColumn(name = "id_aprobador")
    private Usuario aprobador;
    
    // Enumeraciones
    public enum EstadoAdopcion implements TableBadgeCellRenderer.Info {
        PENDIENTE("Pendiente", new Color(255, 165, 0), "hourglass.svg"), 
        EN_REVISION("En revisión", new Color(255, 255, 102), "search.svg"), 
        APROBADA("Aprobada", new Color(60, 179, 113), "checked-checkbox.svg"), 
        RECHAZADA("Rechazada", new Color(220, 20, 60), "multiplication.svg"), 
        FINALIZADA("Finalizada", new Color(65, 105, 225), "inspection.svg"), 
        CANCELADA("Cancelada", new Color(169, 169, 169), "sad-cloud.svg");
        
        EstadoAdopcion(String text, Color color, String icon) {
            this.text = text;
            this.color = color;
            this.icon = new FlatSVGIcon("icons/" + icon, 0.55f).setColorFilter(new FlatSVGIcon.ColorFilter((component, color1) -> color));
        }

        private final String text;
        private final Color color;
        private final Icon icon;

        @Override
        public String getText() {
            return text;
        }

        @Override
        public Color getColor() {
            return color;
        }

        @Override
        public Icon getIcon() {
            return icon;
        }
    }
    
    // Getters y setters
    public Integer getIdAdopcion() {
        return idAdopcion;
    }

    public void setIdAdopcion(Integer idAdopcion) {
        this.idAdopcion = idAdopcion;
    }

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }

    public String getNombreAdoptante() {
        return nombreAdoptante;
    }

    public void setNombreAdoptante(String nombreAdoptante) {
        this.nombreAdoptante = nombreAdoptante;
    }

    public String getEmailAdoptante() {
        return emailAdoptante;
    }

    public void setEmailAdoptante(String emailAdoptante) {
        this.emailAdoptante = emailAdoptante;
    }

    public String getTelefonoAdoptante() {
        return telefonoAdoptante;
    }

    public void setTelefonoAdoptante(String telefonoAdoptante) {
        this.telefonoAdoptante = telefonoAdoptante;
    }

    public String getDireccionAdoptante() {
        return direccionAdoptante;
    }

    public void setDireccionAdoptante(String direccionAdoptante) {
        this.direccionAdoptante = direccionAdoptante;
    }

    public LocalDate getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDate fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public LocalDate getFechaResolucion() {
        return fechaResolucion;
    }

    public void setFechaResolucion(LocalDate fechaResolucion) {
        this.fechaResolucion = fechaResolucion;
    }

    public LocalDate getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(LocalDate fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public EstadoAdopcion getEstado() {
        return estado;
    }

    public void setEstado(EstadoAdopcion estado) {
        this.estado = estado;
    }

    public String getMotivoRechazo() {
        return motivoRechazo;
    }

    public void setMotivoRechazo(String motivoRechazo) {
        this.motivoRechazo = motivoRechazo;
    }

    public String getCompromisos() {
        return compromisos;
    }

    public void setCompromisos(String compromisos) {
        this.compromisos = compromisos;
    }

    public LocalDate getSeguimientoProgramado() {
        return seguimientoProgramado;
    }

    public void setSeguimientoProgramado(LocalDate seguimientoProgramado) {
        this.seguimientoProgramado = seguimientoProgramado;
    }

    public Usuario getAprobador() {
        return aprobador;
    }

    public void setAprobador(Usuario aprobador) {
        this.aprobador = aprobador;
    }
    
    public Object[] toTableRow() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MMMM-yyyy");
        return new Object[]{
            false, 
            idAdopcion, 
            nombreAdoptante, 
            animal, 
            this, 
            fechaSolicitud != null ? fechaSolicitud.format(dtf) : "", 
            estado, 
            fechaResolucion != null ? fechaResolucion.format(dtf) : "", 
            motivoRechazo, 
            fechaEntrega != null ? fechaEntrega.format(dtf) : "", 
            seguimientoProgramado != null ? seguimientoProgramado.format(dtf) : ""};
    }
}