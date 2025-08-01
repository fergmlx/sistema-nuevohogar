package sistema.animales;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Shape;
import javaswingdev.picturebox.DefaultPictureBoxRender;
import sistema.modelos.Animal;
import sistema.modelos.Animal.Sexo;

public class TableCellDetalles extends javax.swing.JPanel {

    public TableCellDetalles(Animal data, Font font) {
        initComponents();
        lblSexo.setFont(font);
        lblEdad.setFont(font);
        lblSexo.setText(data.getSexo().name());
        if (data.getSexo() == Sexo.MACHO) {
            lblSexo.setIcon(new FlatSVGIcon("icons/icons8-male.svg", 0.25f));
        } else  if (data.getSexo() == Sexo.HEMBRA) {
            lblSexo.setIcon(new FlatSVGIcon("icons/icons8-female.svg", 0.25f));
        }
        lblEdad.setText(data.getEdadAproximada().toString() + " " + data.getUnidadEdad());
        lblEdad.putClientProperty(FlatClientProperties.STYLE, ""
                + "foreground:$Label.disabledForeground");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblSexo = new javax.swing.JLabel();
        lblEdad = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        lblSexo.setText("Sexo");

        lblEdad.setText("Edad");

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/dot.png"))); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblSexo, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addGap(14, 14, 14)
                .addComponent(lblEdad, javax.swing.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(28, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblSexo)
                    .addComponent(lblEdad)
                    .addComponent(jLabel1))
                .addGap(26, 26, 26))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lblEdad;
    private javax.swing.JLabel lblSexo;
    // End of variables declaration//GEN-END:variables
}
