package sistema.adopciones;

import sistema.animales.*;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.formdev.flatlaf.util.UIScale;
import com.raven.datechooser.SelectedDate;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Shape;
import java.io.File;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import javaswingdev.picturebox.DefaultPictureBoxRender;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import jnafilechooser.api.JnaFileChooser;
import sistema.dao.AnimalDAO;
import sistema.dao.UsuarioDAO;
import sistema.modelos.Adopcion;
import sistema.modelos.Adopcion.EstadoAdopcion;
import sistema.modelos.Animal;
import sistema.modelos.Animal.CondicionSalud;
import static sistema.modelos.Animal.CondicionSalud.CRITICO;
import static sistema.modelos.Animal.CondicionSalud.DESCONOCIDA;
import static sistema.modelos.Animal.CondicionSalud.EN_TRATAMIENTO;
import static sistema.modelos.Animal.CondicionSalud.SALUDABLE;
import sistema.modelos.Animal.EstadoAnimal;
import static sistema.modelos.Animal.EstadoAnimal.ADOPTADO;
import static sistema.modelos.Animal.EstadoAnimal.DISPONIBLE;
import static sistema.modelos.Animal.EstadoAnimal.EN_PROCESO;
import static sistema.modelos.Animal.EstadoAnimal.FALLECIDO;
import sistema.modelos.Usuario;


public class Create extends javax.swing.JPanel {
    private Adopcion adopcionOriginal;

    /**
     * Creates new form Create
     */
    public Create() {
        initComponents();
        datePicker1.setCloseAfterSelected(true);
        datePicker1.setEditor(txtSolicitud);
        datePicker2.setCloseAfterSelected(true);
        datePicker2.setEditor(txtResolucion);
        datePicker3.setCloseAfterSelected(true);
        datePicker3.setEditor(txtEntrega);
        datePicker4.setCloseAfterSelected(true);
        datePicker4.setEditor(txtSeguimiento);
        txtAnimal.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Id del animal");
        txtNombreAdoptante.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nombre del adoptante");
        txtEmailAdoptante.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Email del adoptante");
        txtTelefonoAdoptante.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Telefono del adoptante");
        txtDireccionAdoptante.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Dirección del adoptante");
        txtSolicitud.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Fecha de solicitud");
        txtResolucion.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Fecha de resolución");
        txtEntrega.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Fecha de entrega de la mascota");
        txtSeguimiento.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Fecha para verificar bienestar posterior");
        txtMotivoRechazo.putClientProperty("JTextField.placeholderText", "Razón si la adopción fue rechazada");
        txtCompromisos.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Acuerdos que el adoptante acepta cumplir");
        
        comboEstado.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Estado del proceso");
        
        comboEstado.addItem(EstadoAdopcion.PENDIENTE);
        comboEstado.addItem(EstadoAdopcion.EN_REVISION);
        comboEstado.addItem(EstadoAdopcion.APROBADA);
        comboEstado.addItem(EstadoAdopcion.RECHAZADA);
        comboEstado.addItem(EstadoAdopcion.FINALIZADA);
        comboEstado.addItem(EstadoAdopcion.CANCELADA);
        
        comboEstado.setSelectedIndex(-1);

    }

    public void loadData(Adopcion data) {
        this.adopcionOriginal = data;
        if (data != null) {
            txtAnimal.setText(data.getAnimal().getIdAnimal().toString());
            txtNombreAdoptante.setText(data.getNombreAdoptante());
            txtEmailAdoptante.setText(data.getEmailAdoptante());
            txtTelefonoAdoptante.setText(data.getTelefonoAdoptante());
            txtDireccionAdoptante.setText(data.getDireccionAdoptante());
            LocalDate solicitud = data.getFechaSolicitud();
            LocalDate resolucion = data.getFechaResolucion();
            LocalDate entrega = data.getFechaEntrega();
            if (solicitud != null) {
                datePicker1.setSelectedDate(solicitud);
            }
            if (resolucion != null) {
                datePicker2.setSelectedDate(resolucion);
            }
            if (entrega != null) {
                datePicker3.setSelectedDate(entrega);
            }
            comboEstado.setSelectedItem(data.getEstado());
            txtMotivoRechazo.setText(data.getMotivoRechazo());
            txtCompromisos.setText(data.getCompromisos());
            LocalDate seguimiento = data.getSeguimientoProgramado();
            if (seguimiento != null) {
                datePicker4.setSelectedDate(seguimiento);
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        datePicker1 = new raven.datetime.component.date.DatePicker();
        datePicker2 = new raven.datetime.component.date.DatePicker();
        datePicker3 = new raven.datetime.component.date.DatePicker();
        datePicker4 = new raven.datetime.component.date.DatePicker();
        txtAnimal = new javax.swing.JTextField();
        txtNombreAdoptante = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtMotivoRechazo = new javax.swing.JTextArea();
        txtTelefonoAdoptante = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        comboEstado = new javax.swing.JComboBox<>();
        txtEmailAdoptante = new javax.swing.JTextField();
        txtDireccionAdoptante = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtCompromisos = new javax.swing.JTextArea();
        txtSolicitud = new javax.swing.JFormattedTextField();
        txtResolucion = new javax.swing.JFormattedTextField();
        txtEntrega = new javax.swing.JFormattedTextField();
        txtSeguimiento = new javax.swing.JFormattedTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();

        txtMotivoRechazo.setColumns(20);
        txtMotivoRechazo.setLineWrap(true);
        txtMotivoRechazo.setRows(5);
        txtMotivoRechazo.setWrapStyleWord(true);
        jScrollPane1.setViewportView(txtMotivoRechazo);

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel9.setText("Fecha de Solicitud");

        comboEstado.setEditable(true);

        txtCompromisos.setColumns(20);
        txtCompromisos.setLineWrap(true);
        txtCompromisos.setRows(5);
        txtCompromisos.setWrapStyleWord(true);
        jScrollPane2.setViewportView(txtCompromisos);

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel10.setText("Fecha de Resolución");

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel11.setText("Fecha de Entrega");

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel12.setText("Seguimiento");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(85, 85, 85)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 392, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtSeguimiento))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(comboEstado, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtNombreAdoptante)
                                    .addComponent(txtAnimal)
                                    .addComponent(txtEmailAdoptante)
                                    .addComponent(txtTelefonoAdoptante)
                                    .addComponent(txtDireccionAdoptante)
                                    .addComponent(txtEntrega, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(txtSolicitud, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(txtResolucion))))))
                .addGap(30, 30, 30))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(txtAnimal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNombreAdoptante, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtEmailAdoptante, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTelefonoAdoptante, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDireccionAdoptante, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSolicitud, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(comboEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtResolucion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtEntrega, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(58, 58, 58)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSeguimiento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addGap(39, 39, 39))
        );
    }// </editor-fold>//GEN-END:initComponents


    public Adopcion getData() {
        Adopcion adopcion;
        if (adopcionOriginal != null) {
            adopcion = adopcionOriginal;
        } else {
            adopcion = new Adopcion();
        }
        Integer idAnimal = Integer.valueOf(txtAnimal.getText());
        Animal animal = AnimalDAO.obtenerPorId(idAnimal);
        adopcion.setAnimal(animal);
        adopcion.setNombreAdoptante(txtNombreAdoptante.getText().trim());
        adopcion.setEmailAdoptante(txtEmailAdoptante.getText().trim());
        adopcion.setTelefonoAdoptante(txtTelefonoAdoptante.getText().trim());
        adopcion.setDireccionAdoptante(txtDireccionAdoptante.getText().trim());
        LocalDate solicitud = datePicker1.isDateSelected() ? datePicker1.getSelectedDate() : null;
        adopcion.setFechaSolicitud(solicitud);
        LocalDate resolucion = datePicker2.isDateSelected() ? datePicker2.getSelectedDate() : null;
        adopcion.setFechaResolucion(resolucion);
        LocalDate entrega = datePicker3.isDateSelected() ? datePicker3.getSelectedDate() : null;
        adopcion.setFechaEntrega(entrega);
        adopcion.setEstado((EstadoAdopcion) comboEstado.getSelectedItem());
        adopcion.setMotivoRechazo(txtMotivoRechazo.getText().trim());
        adopcion.setCompromisos(txtCompromisos.getText().trim());
        LocalDate seguimiento = datePicker4.isDateSelected() ? datePicker4.getSelectedDate() : null;
        adopcion.setSeguimientoProgramado(seguimiento);
        return adopcion;
    }
    
    public void init() {
        txtAnimal.grabFocus();
    }
    
    /*public static void main(String[] args) {
        try {
            FlatMacLightLaf.setup();
            UIManager.put("Button.arc", 999);
            UIManager.put("TextComponent.arc", 55);
            UIManager.put("Component.arc", 55);
            UIManager.put("TextField.margin", new Insets(5, 10, 5, 5));
            UIManager.put("PasswordField.margin", new Insets(5, 5, 5, 5));
            UIManager.put("defaultFont", new Font("Poppins", Font.PLAIN, 13));
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Test Dashboard");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(546, 654);
            frame.setLocationRelativeTo(null);
            
            Create create = new Create();
            frame.add(create);
            frame.setVisible(true);
        });
    }*/

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<Object> comboEstado;
    private raven.datetime.component.date.DatePicker datePicker1;
    private raven.datetime.component.date.DatePicker datePicker2;
    private raven.datetime.component.date.DatePicker datePicker3;
    private raven.datetime.component.date.DatePicker datePicker4;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField txtAnimal;
    private javax.swing.JTextArea txtCompromisos;
    private javax.swing.JTextField txtDireccionAdoptante;
    private javax.swing.JTextField txtEmailAdoptante;
    private javax.swing.JFormattedTextField txtEntrega;
    private javax.swing.JTextArea txtMotivoRechazo;
    private javax.swing.JTextField txtNombreAdoptante;
    private javax.swing.JFormattedTextField txtResolucion;
    private javax.swing.JFormattedTextField txtSeguimiento;
    private javax.swing.JFormattedTextField txtSolicitud;
    private javax.swing.JTextField txtTelefonoAdoptante;
    // End of variables declaration//GEN-END:variables
}
