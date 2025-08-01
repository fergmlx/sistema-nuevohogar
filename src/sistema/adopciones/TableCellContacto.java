package sistema.adopciones;

import sistema.animales.*;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Shape;
import javaswingdev.picturebox.DefaultPictureBoxRender;
import sistema.modelos.Adopcion;
import sistema.modelos.Animal;
import sistema.modelos.Animal.Sexo;

public class TableCellContacto extends javax.swing.JPanel {

    public TableCellContacto(Adopcion data, Font font) {
        initComponents();
        lblDireccion.setFont(font);
        lblTelefono.setFont(font);
        lblDireccion.setText(data.getDireccionAdoptante());
        lblTelefono.setText(data.getTelefonoAdoptante());
        lblEmail.setText(data.getEmailAdoptante());
        lblTelefono.putClientProperty(FlatClientProperties.STYLE, ""
                + "foreground:$Label.disabledForeground");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblDireccion = new javax.swing.JLabel();
        lblTelefono = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblEmail = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        lblDireccion.setText("Direccion");

        lblTelefono.setText("Telefono");

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8-phone-18.png"))); // NOI18N

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8-location-18.png"))); // NOI18N

        lblEmail.setText("Email");

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8-email-18.png"))); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(jLabel2)
                        .addComponent(jLabel1))
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblEmail, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblTelefono, javax.swing.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE)
                    .addComponent(lblDireccion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(23, 23, 23))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel2)
                    .addComponent(lblDireccion))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblTelefono)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblEmail)
                    .addComponent(jLabel3))
                .addContainerGap(16, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel lblDireccion;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblTelefono;
    // End of variables declaration//GEN-END:variables
}
