package vista;

import javax.swing.*;
import java.awt.*;

public class AnotadorPuntos extends JPanel {
    private int puntos;

    public AnotadorPuntos(String titulo) {
        setBorder(BorderFactory.createTitledBorder(null, titulo, 0, 0, null, Color.WHITE));
        setPreferredSize(new Dimension(110, 200));
        setOpaque(false);
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(150, 200, 255));
        g2.setStroke(new BasicStroke(3));

        for (int i = 0; i < puntos; i++) {
            int columna = (i / 5) % 2;
            int fila = (i / 10);
            int p = i % 5;

            int x = 20 + (columna * 40);
            int y = 40 + (fila * 50);

            if (p == 0) g2.drawLine(x, y, x + 25, y);
            if (p == 1) g2.drawLine(x, y, x, y + 25);
            if (p == 2) g2.drawLine(x, y + 25, x + 25, y + 25);
            if (p == 3) g2.drawLine(x + 25, y, x + 25, y + 25);
            if (p == 4) g2.drawLine(x, y, x + 25, y + 25);
        }
    }
}