package sistema.login;


import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */

/**
 *
 * @author ayele
 */
public class CaptchaPanel extends javax.swing.JPanel {

    private String codigo;
    private int longitudMin = 4;
    private int longitudMax=8;
    private TipoCaptcha tipo = TipoCaptcha.mixto;
    private BufferedImage imagen;
    private int longitudPrede=6;
    private int anchoCaptcha = 180;
    private int altoCaptcha = 60;
    private int opacidad = 70; // Valor entre 0 (transparente) y 100 (opaco)

    /**
     * Creates new form CaptchaPanel
     */
    public CaptchaPanel() {
        
        generarCaptcha();
        
    }

    public void generarCaptcha() {
        codigo = generarCodigoAleatorio();
        imagen = generarImagenDistorsionada(codigo);
        repaint();
    }

    public String getCodigo() {
        return codigo;
    }
    
    public enum TipoCaptcha {
    letras,
    numeros,
    mixto
}

    public TipoCaptcha getTipo() {
    return tipo;
}

public void setTipo(TipoCaptcha tipo) {
    this.tipo = (tipo != null) ? tipo : TipoCaptcha.mixto;
    generarCaptcha();
}


    public int getOpacidad() {
    return opacidad;
}

public void setOpacidad(int opacidad) {
    this.opacidad = Math.max(0, Math.min(100, opacidad)); // asegura el rango 0–100
    generarCaptcha(); // redibuja con la nueva opacidad
}

    public int getAnchoCaptcha() {
    return anchoCaptcha;
}

public void setAnchoCaptcha(int anchoCaptcha) {
    if (anchoCaptcha >= 100 && anchoCaptcha <= 400) {
        this.anchoCaptcha = anchoCaptcha;
    } else {
        System.out.println("⚠️ Ancho fuera de rango (100 - 400). Usando 180.");
        this.anchoCaptcha = 180;
    }
    generarCaptcha(); // redibuja
}

public int getAltoCaptcha() {
    return altoCaptcha;
}

public void setAltoCaptcha(int altoCaptcha) {
    if (altoCaptcha >= 30 && altoCaptcha <= 150) {
        this.altoCaptcha = altoCaptcha;
    } else {
        System.out.println("⚠️ Alto fuera de rango (30 - 150). Usando 60.");
        this.altoCaptcha = 60;
    }
    generarCaptcha(); // redibuja
}

   public int getLongitud() {
    return longitudPrede;
}

public void setLongitud(int longitud) {
    if (longitud >= longitudMin && longitud <= longitudMax) {
        this.longitudPrede = longitud;
    } else {
        System.out.println("⚠️ Longitud fuera de rango (" + longitudMin + " - " + longitudMax + "). Usando " + longitudMin + " por defecto.");
        this.longitudPrede = longitudMin;
    }
    generarCaptcha(); // Redibujar
}

    public String generarCodigoAleatorio() {
        String letras = "ABCDEFGHJKLMNPQRSTUVWXYZ"; // sin O
        String numeros = "23456789"; // sin 0 y 1
        String caracteres;

        switch (tipo) {
    case letras:
        caracteres = letras;
        break;
    case numeros:
        caracteres = numeros;
        break;
    default:
        caracteres = letras + numeros;
}

        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < longitudPrede; i++) {
            sb.append(caracteres.charAt(r.nextInt(caracteres.length())));
        }
        return sb.toString();
    }

   public BufferedImage generarImagenDistorsionada(String texto) {
    BufferedImage img = new BufferedImage(anchoCaptcha, altoCaptcha, BufferedImage.TYPE_INT_RGB);
    Graphics2D g2d = img.createGraphics();

    Random r = new Random();

    // Fondo con gradiente
    GradientPaint gp = new GradientPaint(0, 0, Color.LIGHT_GRAY, altoCaptcha, anchoCaptcha, Color.WHITE);
    g2d.setPaint(gp);
    g2d.fillRect(0, 0, anchoCaptcha, altoCaptcha);

    // Dibuja líneas de interferencia
    for (int i = 0; i < 20; i++) {
        g2d.setColor(new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255)));
        int x1 = r.nextInt(altoCaptcha);
        int y1 = r.nextInt(anchoCaptcha);
        int x2 = r.nextInt(altoCaptcha);
        int y2 = r.nextInt(anchoCaptcha); 
        g2d.drawLine(x1, y1, x2, y2);
    }

    // Texto distorsionado con opacidad baja
    g2d.setFont(new Font("Arial", Font.BOLD, 32));
    int baseX = 15;

    for (int i = 0; i < texto.length(); i++) {
        char c = texto.charAt(i);

        // Rotación ligera
        double angle = Math.toRadians(r.nextInt(80) - 40);
        g2d.rotate(angle, baseX, 50);

        // Color con baja opacidad
        Color color = new Color(r.nextInt(150), r.nextInt(150), r.nextInt(150));
        g2d.setColor(color);
        float alpha = opacidad / 100f;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));


        int y = 35 + r.nextInt(10);
        g2d.drawString(String.valueOf(c), baseX, y);

        // Deshace rotación y opacidad
        g2d.rotate(-angle, baseX, 50);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f)); // restaura opacidad total

        baseX += 25 + r.nextInt(5);
    }

    for (int i = 0; i < 100; i++) {
    int x = r.nextInt(anchoCaptcha); // ← usa ancho correcto
    int y = r.nextInt(altoCaptcha);  // ← usa alto correcto

    // Asegura que x e y estén dentro de los límites válidos
    if (x >= 0 && x < img.getWidth() && y >= 0 && y < img.getHeight()) {
        img.setRGB(x, y, r.nextInt(0xFFFFFF));
    }
}

    g2d.dispose();
    return img;
}


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (imagen != null) {
            g.drawImage(imagen, 0, 0, this);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(anchoCaptcha, altoCaptcha);
    }



    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 406, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 255, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
