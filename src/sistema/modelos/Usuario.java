package sistema.modelos;

import jakarta.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "Usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;
    
    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;
    
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;
    
    @Column(name = "password", nullable = false, length = 255)
    private String password;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false)
    private RolUsuario rol;
    
    @Column(name = "ultimo_acceso")
    @Temporal(TemporalType.TIMESTAMP)
    private Date ultimoAcceso;
    
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaRegistro;
    
    @PrePersist
    protected void onCreate() {
        fechaRegistro = new Date();
    }
    
    // Enum para roles
    public enum RolUsuario {
        Administrador, Coordinador, Voluntario, Veterinario
    }
    
    // Constructores
    public Usuario() {
    }
    
    public Usuario(String nombre, String email, String password, RolUsuario rol) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.rol = rol;
    }
    
    // Getters y Setters
    public Integer getIdUsuario() {
        return idUsuario;
    }
    
    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public RolUsuario getRol() {
        return rol;
    }
    
    public void setRol(RolUsuario rol) {
        this.rol = rol;
    }
    
    public Date getUltimoAcceso() {
        return ultimoAcceso;
    }
    
    public void setUltimoAcceso(Date ultimoAcceso) {
        this.ultimoAcceso = ultimoAcceso;
    }
    
    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", rol=" + rol +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Usuario usuario = (Usuario) o;

        // Dos usuarios son iguales si tienen el mismo ID
        return Objects.equals(idUsuario, usuario.idUsuario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUsuario);
    }
}