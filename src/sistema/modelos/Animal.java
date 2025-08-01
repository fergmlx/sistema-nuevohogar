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
import java.util.List;
import javax.swing.Icon;
import sistema.custom.TableBadgeCellRenderer;
import sistema.animales.TableSimpleCellRenderer;

@Entity
@Table(name = "animales")
public class Animal {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_animal")
    private Integer idAnimal;
    
    @Column(name = "nombre", nullable = false)
    private String nombre;
    
    @Column(name = "especie", nullable = false)
    private String especie;
    
    @Column(name = "raza")
    private String raza;
    
    @Column(name = "edad_aproximada")
    private Integer edadAproximada;
    
    @Column(name = "unidad_edad")
    private String unidadEdad;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "sexo")
    private Sexo sexo;
    
    @Column(name = "tamano")
    private String tamano;
    
    @Column(name = "color")
    private String color;
    
    @Column(name = "fecha_ingreso")
    private LocalDate fechaIngreso;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoAnimal estado;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "condicion_salud")
    private CondicionSalud condicionSalud;
    
    @Column(name = "descripcion", length = 1000)
    private String descripcion;
    
    @Column(name = "foto_url")
    private String fotoUrl;
    
    @Column(name = "esterilizado")
    private Boolean esterilizado;
    
    @ManyToOne
    @JoinColumn(name = "id_responsable")
    private Usuario responsable;
    
    @OneToMany(mappedBy = "animal", cascade = CascadeType.ALL)
    private List<HistorialMedico> historialesMedicos;
    
    @OneToMany(mappedBy = "animal", cascade = CascadeType.ALL)
    private List<Actividad> actividades;
    
    // Enumeraciones
    public enum Sexo {
        MACHO, HEMBRA, DESCONOCIDO
    }
    
    public enum EstadoAnimal implements TableBadgeCellRenderer.Info {
        DISPONIBLE("Disponible", new Color(59, 155, 60), "icons8-check-mark.svg"),
        ADOPTADO("Adoptado", new Color(75, 137, 255), "icons8-heart.svg"),
        EN_PROCESO("En Proceso", new Color(255, 164, 75), "icons8-sand-watch.svg"),
        FALLECIDO("Fallecido", new Color(255, 75, 101), "icons8-broken-heart.svg");
        
        EstadoAnimal(String text, Color color, String icon) {
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
    
    public enum CondicionSalud implements TableSimpleCellRenderer.Info {
        SALUDABLE("Saludable", new Color(59, 155, 60), "icons8-heart-health.svg"),
        EN_TRATAMIENTO("En tratamiento", new Color(255, 164, 75), "icons8-treatment.svg"),
        CRITICO("Crítico", new Color(255, 75, 101), "icons8-warning.svg"),
        DESCONOCIDA("Desconocida", new Color(150, 150, 150), "icons8-help.svg");
        
        CondicionSalud (String text, Color color, String icon) {
            this.text = text;
            this.color = color;
            this.icon = new FlatSVGIcon("icons/" + icon, 0.40f);//.setColorFilter(new FlatSVGIcon.ColorFilter((component, color1) -> color));
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
    public Integer getIdAnimal() {
        return idAnimal;
    }

    public void setIdAnimal(Integer idAnimal) {
        this.idAnimal = idAnimal;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public Integer getEdadAproximada() {
        return edadAproximada;
    }

    public void setEdadAproximada(Integer edadAproximada) {
        this.edadAproximada = edadAproximada;
    }

    public String getUnidadEdad() {
        return unidadEdad;
    }

    public void setUnidadEdad(String unidadEdad) {
        this.unidadEdad = unidadEdad;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public String getTamano() {
        return tamano;
    }

    public void setTamano(String tamano) {
        this.tamano = tamano;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDate fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public EstadoAnimal getEstado() {
        return estado;
    }

    public void setEstado(EstadoAnimal estado) {
        this.estado = estado;
    }
    
    public CondicionSalud getCondicionSalud() {
        return condicionSalud;
    }

    public void setCondicionSalud(CondicionSalud condicionSalud) {
        this.condicionSalud = condicionSalud;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public Boolean getEsterilizado() {
        return esterilizado;
    }

    public void setEsterilizado(Boolean esterilizado) {
        this.esterilizado = esterilizado;
    }

    public Usuario getResponsable() {
        return responsable;
    }

    public void setResponsable(Usuario responsable) {
        this.responsable = responsable;
    }
    
    public Object[] toTableRow() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MMMM-yyyy");
        NumberFormat nf = new DecimalFormat("$ #,##0.##");
        return new Object[]{
            false, 
            idAnimal, 
            nombre, 
            especie, 
            raza, 
            edadAproximada, 
            estado,
            condicionSalud, 
            fechaIngreso != null ? fechaIngreso.format(dtf) : "",
            responsable != null ? responsable.getNombre() : ""
        };
    }
}