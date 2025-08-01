package sistema.modelos;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "donaciones")
public class Donacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_donacion")
    private Integer idDonacion;
    
    @Column(name = "tipo_donacion")
    @Enumerated(EnumType.STRING)
    private TipoDonacion tipoDonacion;
    
    @Column(name = "fecha_donacion")
    @Temporal(TemporalType.DATE)
    private Date fechaDonacion;
    
    @Column(name = "nombre_donante")
    private String nombreDonante;
    
    @Column(name = "email_donante")
    private String emailDonante;
    
    @Column(name = "telefono_donante")
    private String telefonoDonante;
    
    @Column(name = "monto")
    private Double monto;
    
    @Column(name = "descripcion_items", length = 1000)
    private String descripcionItems;
    
    @Column(name = "valor_estimado")
    private Double valorEstimado;
    
    @Column(name = "destino_especifico", length = 500)
    private String destinoEspecifico;
    
    @Column(name = "recibo_emitido")
    private Boolean reciboEmitido;
    
    @Column(name = "numero_recibo")
    private String numeroRecibo;
    
    @ManyToOne
    @JoinColumn(name = "id_receptor")
    private Usuario receptor;
    
    // Enumeraciones
    public enum TipoDonacion {
        MONETARIA, ALIMENTOS, MEDICAMENTOS, INSUMOS, SERVICIOS, OTRO
    }
    
    // Getters y setters
    public Integer getIdDonacion() {
        return idDonacion;
    }

    public void setIdDonacion(Integer idDonacion) {
        this.idDonacion = idDonacion;
    }

    public TipoDonacion getTipoDonacion() {
        return tipoDonacion;
    }

    public void setTipoDonacion(TipoDonacion tipoDonacion) {
        this.tipoDonacion = tipoDonacion;
    }

    public Date getFechaDonacion() {
        return fechaDonacion;
    }

    public void setFechaDonacion(Date fechaDonacion) {
        this.fechaDonacion = fechaDonacion;
    }

    public String getNombreDonante() {
        return nombreDonante;
    }

    public void setNombreDonante(String nombreDonante) {
        this.nombreDonante = nombreDonante;
    }

    public String getEmailDonante() {
        return emailDonante;
    }

    public void setEmailDonante(String emailDonante) {
        this.emailDonante = emailDonante;
    }

    public String getTelefonoDonante() {
        return telefonoDonante;
    }

    public void setTelefonoDonante(String telefonoDonante) {
        this.telefonoDonante = telefonoDonante;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public String getDescripcionItems() {
        return descripcionItems;
    }

    public void setDescripcionItems(String descripcionItems) {
        this.descripcionItems = descripcionItems;
    }

    public Double getValorEstimado() {
        return valorEstimado;
    }

    public void setValorEstimado(Double valorEstimado) {
        this.valorEstimado = valorEstimado;
    }

    public String getDestinoEspecifico() {
        return destinoEspecifico;
    }

    public void setDestinoEspecifico(String destinoEspecifico) {
        this.destinoEspecifico = destinoEspecifico;
    }

    public Boolean getReciboEmitido() {
        return reciboEmitido;
    }

    public void setReciboEmitido(Boolean reciboEmitido) {
        this.reciboEmitido = reciboEmitido;
    }

    public String getNumeroRecibo() {
        return numeroRecibo;
    }

    public void setNumeroRecibo(String numeroRecibo) {
        this.numeroRecibo = numeroRecibo;
    }

    public Usuario getReceptor() {
        return receptor;
    }

    public void setReceptor(Usuario receptor) {
        this.receptor = receptor;
    }
}