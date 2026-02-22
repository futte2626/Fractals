import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class DrawPanel extends JPanel {
    private double centerX = -0.5, centerY = 0, scale = 3;
    private int maxIterations = 50;
    private Point lastMouse;

    public DrawPanel() {
        setPreferredSize(new Dimension(1280, 720));

        addMouseWheelListener(e -> {
            if (e.getWheelRotation() < 0) {
                scale = scale / 1.2;
            } else {
                scale = scale * 1.2;
            }
            repaint();
        });

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                lastMouse = e.getPoint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                centerX -= (e.getX() - lastMouse.x) * scale / getWidth();
                centerY += (e.getY() - lastMouse.y) * scale / getWidth();
                lastMouse = e.getPoint();
                repaint();
            }
        });
    }

    private long lastFrameTimeMs = 0;
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = getWidth();
        int h = getHeight();
        ComplexNumber z;
        ComplexNumber c;

        long start = System.currentTimeMillis();
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                g.setColor(Color.black);
                double reVal = centerX + (x - w / 2.0) * scale / w;
                double imVal = centerY - (y - h / 2.0) * scale / w;
                z = new ComplexNumber(0, 0);
                c = new ComplexNumber(reVal, imVal);

                for(int i = 0; i < maxIterations; i++) {
                    if(z.getMagnitude() > 2) {
                        g.setColor(Color.white);
                        break;
                    }
                    z = z.multiply(z).add(c);
                }
                g.fillRect(x, y, 1, 1);
            }
        }

        long end = System.currentTimeMillis();
        lastFrameTimeMs = end - start;

        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Frame time: " + lastFrameTimeMs + " ms", 10, 20);
    }
}