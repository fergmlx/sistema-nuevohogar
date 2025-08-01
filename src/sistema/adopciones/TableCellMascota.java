package sistema.adopciones;

import sistema.animales.*;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Shape;
import javaswingdev.picturebox.DefaultPictureBoxRender;
import sistema.modelos.Animal;
import sistema.modelos.Animal.Sexo;

public class TableCellMascota extends javax.swing.JPanel {

    public TableCellMascota(Animal data, Font font) {
        initComponents();
        lblNombre.setFont(font);
        lblRaza.setFont(font);
        lblNombre.setText(data.getNombre());
        lblRaza.setText(data.getRaza());
        lblRaza.putClientProperty(FlatClientProperties.STYLE, ""
                + "foreground:$Label.disabledForeground");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblNombre = new javax.swing.JLabel();
        lblRaza = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        lblNombre.setText("Sexo");

        lblRaza.setText("Edad");

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/dot.png"))); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblRaza, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(23, 23, 23))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(lblNombre)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblRaza)
                    .addComponent(jLabel1))
                .addContainerGap(18, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblRaza;
    // End of variables declaration//GEN-END:variables
}
