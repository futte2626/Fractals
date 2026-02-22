import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.stream.IntStream;

public class ParallelPanel extends JPanel {
    private double centerX = -0.5, centerY = 0, scale = 3;
    private int maxIterations = 5000;
    private Point lastMouse;

    private BufferedImage image;

    public ParallelPanel() {
        setPreferredSize(new Dimension(1080, 720));

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
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int w = getWidth();
        int h = getHeight();

        image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        // Parallel over rows (each thread handles one y)
        long start = System.currentTimeMillis();
        IntStream.range(0, h).parallel().forEach(y -> {
            for (int x = 0; x < w; x++) {

                double reVal = centerX + (x - w / 2.0) * scale / w;
                double imVal = centerY - (y - h / 2.0) * scale / w;

                double zx = 0;
                double zy = 0;

                int i;
                for (i = 0; i < maxIterations; i++) {
                    double zx2 = zx * zx - zy * zy + reVal;
                    double zy2 = 2 * zx * zy + imVal;

                    zx = zx2;
                    zy = zy2;

                    if (zx * zx + zy * zy > 4)
                        break;
                }
                if (i == maxIterations) {
                    image.setRGB(x, y, 0x000000);
                } else {

                    double modSquared = zx*zx + zy*zy;

                    double smooth = i + 1 -
                            Math.log(Math.log(modSquared) / 2.0) / Math.log(2.0);

                    double t = smooth / maxIterations;
                    t = Math.max(0.0, Math.min(1.0, t));

                    t = Math.pow(t, 0.3);  // glow curve

                    int blue  = (int)(255 * t);
                    int green = (int)(80 * t);

                    int rgb = (green << 8) | blue;

                    image.setRGB(x, y, rgb);
                }
            }
        });
        long end = System.currentTimeMillis();
        lastFrameTimeMs = end - start;

        g.drawImage(image, 0, 0, null);
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Frame time: " + lastFrameTimeMs + " ms", 10, 20);
    }

    static double log(double x, double base) {
        return Math.log(x) / Math.log(base);
    }
}