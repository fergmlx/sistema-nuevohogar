package sistema.animales;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.util.UIScale;
import com.raven.datechooser.SelectedDate;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Shape;
import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import javaswingdev.picturebox.DefaultPictureBoxRender;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import jnafilechooser.api.JnaFileChooser;
import sistema.dao.UsuarioDAO;
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
import sistema.modelos.Animal.Sexo;
import sistema.modelos.Usuario;

/**
 *
 * @author RAVEN
 */
public class Create extends javax.swing.JPanel {
    private Animal animalOriginal;
    private String url;

    /**
     * Creates new form Create
     */
    public Create() {
        initComponents();
        datePicker1.setCloseAfterSelected(true);
        datePicker1.setEditor(txtDate);
        pic.setPictureBoxRender(new DefaultPictureBoxRender() {
            @Override
            public Shape render(Rectangle rectangle) {
                return createRound(rectangle, UIScale.scale(10));
            }
        });
        //pic.setImage(new FlatSVGIcon("sample/icon/profile.svg", 5f));
        panelPic.putClientProperty(FlatClientProperties.STYLE, ""
                + "border:0,0,0,0,$Component.borderColor,,10;"
                + "background:$TextArea.background");
        
    }

    public void loadData(Animal data) {
        this.animalOriginal = data;
        
        try {
            for (Usuario usuario : UsuarioDAO.obtenerTodos()) {
                comboUsuarios.addItem(usuario);
                if (data != null && data.getResponsable().getIdUsuario() == usuario.getIdUsuario()) {
                    comboUsuarios.setSelectedItem(usuario);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (data != null) {
            try {
                comboPosition.addItem(Animal.Sexo.MACHO);
                comboPosition.addItem(Animal.Sexo.HEMBRA);
                comboPosition.addItem(Animal.Sexo.DESCONOCIDO);
                if (data.getSexo() != null) {
                    switch (data.getSexo()) {
                        case MACHO:
                            comboPosition.setSelectedIndex(0);
                            break;
                        case HEMBRA:
                            comboPosition.setSelectedIndex(1);
                            break;
                        default:
                            comboPosition.setSelectedIndex(2);
                    }
                }
                comboEstado.addItem(DISPONIBLE);
                comboEstado.addItem(ADOPTADO);
                comboEstado.addItem(EN_PROCESO);
                comboEstado.addItem(FALLECIDO);
                if (data.getEstado()!= null) {
                    switch (data.getEstado()) {
                        case DISPONIBLE:
                            comboEstado.setSelectedIndex(0);
                            break;
                        case ADOPTADO:
                            comboEstado.setSelectedIndex(1);
                            break;
                        case EN_PROCESO:
                            comboEstado.setSelectedIndex(2);
                            break;
                        case FALLECIDO:
                            comboEstado.setSelectedIndex(3);
                    }
                }

                comboSalud.addItem(SALUDABLE);
                comboSalud.addItem(EN_TRATAMIENTO);
                comboSalud.addItem(CRITICO);
                comboSalud.addItem(DESCONOCIDA);
                if (data.getCondicionSalud()!= null) {
                    switch (data.getCondicionSalud()) {
                        case SALUDABLE:
                            comboSalud.setSelectedIndex(0);
                            break;
                        case EN_TRATAMIENTO:
                            comboSalud.setSelectedIndex(1);
                            break;
                        case CRITICO:
                            comboSalud.setSelectedIndex(2);
                            break;
                        case DESCONOCIDA:
                            comboSalud.setSelectedIndex(3);
                    }
                }
                
                txtName.setText(data.getNombre());
                txtLocation.setText(data.getEspecie());
                if (data.getFechaIngreso() != null) {
                    datePicker1.setSelectedDate(data.getFechaIngreso());
                }
                txtSalary.setValue(data.getEdadAproximada());
                txtUnidad.setText(data.getUnidadEdad());
                txtDescription.setText(data.getDescripcion());
                txtRaza.setText(data.getRaza());
                
                url =  data.getFotoUrl();
                if (url != null && !url.isEmpty()) {
                    pic.setImage(new ImageIcon(url));
                }
            } catch (Exception e) {
                e.printStackTrace();
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
        jLabel1 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtLocation = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtDate = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        txtSalary = new javax.swing.JFormattedTextField();
        jLabel5 = new javax.swing.JLabel();
        comboPosition = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtDescription = new javax.swing.JTextArea();
        jLabel7 = new javax.swing.JLabel();
        panelPic = new javax.swing.JPanel();
        pic = new javaswingdev.picturebox.PictureBox();
        jToolBar1 = new javax.swing.JToolBar();
        cmdBrowse = new javax.swing.JButton();
        cmdDelete = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        txtRaza = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        comboEstado = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        comboSalud = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        txtUnidad = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        comboUsuarios = new javax.swing.JComboBox<>();

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel1.setText("Nombre");

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel2.setText("Especie");

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel3.setText("Ingreso");

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel4.setText("Edad");

        txtSalary.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel5.setText("Sexo");

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel6.setText("Descripción");

        txtDescription.setColumns(20);
        txtDescription.setLineWrap(true);
        txtDescription.setRows(5);
        txtDescription.setWrapStyleWord(true);
        jScrollPane1.setViewportView(txtDescription);

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel7.setText("Foto");

        panelPic.setLayout(new java.awt.BorderLayout());

        pic.setPreferredSize(new java.awt.Dimension(344, 120));

        jToolBar1.setRollover(true);
        jToolBar1.setOpaque(false);

        cmdBrowse.setText("Browse");
        cmdBrowse.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdBrowse.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdBrowseActionPerformed(evt);
            }
        });
        jToolBar1.add(cmdBrowse);

        cmdDelete.setForeground(new java.awt.Color(255, 0, 0));
        cmdDelete.setText("Delete");
        cmdDelete.setFocusable(false);
        cmdDelete.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdDelete.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdDeleteActionPerformed(evt);
            }
        });
        jToolBar1.add(cmdDelete);

        javax.swing.GroupLayout picLayout = new javax.swing.GroupLayout(pic);
        pic.setLayout(picLayout);
        picLayout.setHorizontalGroup(
            picLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, picLayout.createSequentialGroup()
                .addContainerGap(243, Short.MAX_VALUE)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        picLayout.setVerticalGroup(
            picLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(picLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(89, Short.MAX_VALUE))
        );

        panelPic.add(pic, java.awt.BorderLayout.CENTER);

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel8.setText("Raza");

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel9.setText("Estado");

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel10.setText("Condicion salud");

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel11.setText("Unidad Edad");

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel12.setText("Responsable");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(16, 16, 16)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(comboEstado, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtRaza, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(txtDate)
                                    .addComponent(txtLocation)
                                    .addComponent(txtName)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(txtSalary, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel11)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtUnidad))
                                    .addComponent(comboSalud, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(comboUsuarios, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(panelPic, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(comboPosition, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1)))
                .addGap(42, 42, 42))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSalary, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel11)
                    .addComponent(txtUnidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtRaza, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(comboEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(comboSalud, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboPosition, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(comboUsuarios, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelPic, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(78, 78, 78))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cmdBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdBrowseActionPerformed
        JnaFileChooser ch = new JnaFileChooser();
        ch.addFilter("Image", "png", "jpg");
        boolean act = ch.showOpenDialog(SwingUtilities.getWindowAncestor(this));
        if (act) {
            File file = ch.getSelectedFile();
            pic.setImage(new ImageIcon(file.getAbsolutePath()));
            url = file.getAbsolutePath();
        }
    }//GEN-LAST:event_cmdBrowseActionPerformed


    private void cmdDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdDeleteActionPerformed
        //pic.setImage(new FlatSVGIcon("sample/icon/profile.svg", 5f));
        pic.setImage(null);
        url = null;
    }//GEN-LAST:event_cmdDeleteActionPerformed

    public Animal getData() {
        Animal animal;
        if (animalOriginal != null) {
            animal = animalOriginal;
        } else {
            // Si estamos creando uno nuevo
            animal = new Animal();
        }
        String nombre = txtName.getText().trim();
        String especie = txtLocation.getText().trim();
        LocalDate ingreso = datePicker1.isDateSelected() ? datePicker1.getSelectedDate() : null;
        Integer edad = Integer.valueOf(txtSalary.getText());
        String unidad = txtUnidad.getText();
        String raza = txtRaza.getText();
        String descripcion = txtDescription.getText().trim();
        animal.setNombre(nombre);
        animal.setEspecie(especie);
        animal.setFechaIngreso(ingreso);
        animal.setEdadAproximada(edad);
        animal.setUnidadEdad(unidad);
        animal.setRaza(raza);
        animal.setDescripcion(descripcion);
        animal.setResponsable((Usuario) comboUsuarios.getSelectedItem());
        animal.setSexo((Sexo) comboPosition.getSelectedItem());
        animal.setEstado((EstadoAnimal) comboEstado.getSelectedItem());
        animal.setCondicionSalud((CondicionSalud) comboSalud.getSelectedItem());
        animal.setFotoUrl(url);
        return animal;
    }
    
    public void init() {
        txtName.grabFocus();
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
            UIManager.put("Button.arc", 999);
            UIManager.put("TextComponent.arc", 55);
            UIManager.put("Component.arc", 55);
            UIManager.put("TextField.margin", new Insets(20, 20, 20, 20));
            UIManager.put("PasswordField.margin", new Insets(20, 20, 20, 20));
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Test Dashboard");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 800);
            frame.setLocationRelativeTo(null);
            
            Create create = new Create();
            int result = JOptionPane.showConfirmDialog(
               frame,
                create,
                "Crear Mascota",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );
            frame.add(create);
            frame.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cmdBrowse;
    private javax.swing.JButton cmdDelete;
    private javax.swing.JComboBox<Object> comboEstado;
    private javax.swing.JComboBox<Object> comboPosition;
    private javax.swing.JComboBox<Object> comboSalud;
    private javax.swing.JComboBox<Object> comboUsuarios;
    private raven.datetime.component.date.DatePicker datePicker1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JPanel panelPic;
    private javaswingdev.picturebox.PictureBox pic;
    private javax.swing.JFormattedTextField txtDate;
    private javax.swing.JTextArea txtDescription;
    private javax.swing.JTextField txtLocation;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtRaza;
    private javax.swing.JFormattedTextField txtSalary;
    private javax.swing.JTextField txtUnidad;
    // End of variables declaration//GEN-END:variables
}
