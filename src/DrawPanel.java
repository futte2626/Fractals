import javax.swing.*;
import java.awt.*;

public class DrawPanel extends JPanel {
    public DrawPanel() {
        this.setPreferredSize(new Dimension(1200, 800));
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.translate(this.getWidth() / 2, this.getHeight() / 2);
        g2d.scale(1,-1);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        int maxIterations = 1000;
        for(int x = -getWidth()/2; x < getWidth()/2; x++) {
            for(int y = -getHeight()/2; y < getHeight()/2; y++) {
                g2d.setColor(Color.BLACK);
                double real = -2.0 + (x + getWidth()/2) * 3.0 / getWidth();
                double imag = -1.0 + (y + getHeight()/2) * 2.0 / getHeight();
                ComplexNumber c = new ComplexNumber(real, imag);
                ComplexNumber z = new ComplexNumber(0, 0);

                for(int i = 0; i < maxIterations; i++) {
                    if(z.getMagnitude()>2) {
                        g2d.setColor(Color.WHITE);
                        break;
                    }
                    z = z.multiply(z).add(c);
                }
                g2d.fillRect(x, y, 1, 1);
            }
        }
    }
}