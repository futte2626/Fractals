import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class RecursivePanel extends JPanel {
    private double centerX = -0.5, centerY = 0, scale = 3;
    private int width, height;
    private int maxIterations = 150;
    private Point lastMouse;
    private BufferedImage image;

    public RecursivePanel() {
        setPreferredSize(new Dimension(1080, 720));

        addMouseWheelListener(e -> {
            if (e.getWheelRotation() < 0) {
                scale = scale / 1.2;
                maxIterations -= (int) (50 * Math.log10(scale));
                System.out.println(maxIterations);
            } else {
                scale = scale * 1.2;
                maxIterations -= (int) (50 * Math.log10(scale));
                System.out.println(maxIterations);
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
        System.out.println(maxIterations);

    }

    private long lastFrameTimeMs = 0;
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        width = getWidth();
        height = getHeight();

        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        long start = System.currentTimeMillis();
        renderTile(0,0,width,height, 0);
        long end = System.currentTimeMillis();
        lastFrameTimeMs = end - start;

        g.drawImage(image, 0, 0, null);
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Frame time: " + lastFrameTimeMs + " ms", 10, 20);
    }

    public boolean PointConverges(int x, int y) {
        boolean converged = true;
        double reVal = centerX + (x - width / 2.0) * scale / width;
        double imVal = centerY - (y - height / 2.0) * scale / width;
        ComplexNumber z = new ComplexNumber(0, 0);
        ComplexNumber c = new ComplexNumber(reVal, imVal);

        for(int i = 0; i < maxIterations; i++) {
            if(z.getMagnitude() > 2) {
                converged = false;
                break;
            }
            z = z.multiply(z).add(c);
        }
        return converged;
    }

    public void renderTile(int x0, int y0, int x1, int y1, int level) {
        int widthTile = x1 - x0;
        int heightTile = y1 - y0;

        // Base case: single pixel
        if (widthTile <= 1 && heightTile <= 1) {
            if (PointConverges(x0, y0)) image.setRGB(x0, y0, level);
            else image.setRGB(x0, y0, 0xFFFFFF);
            return;
        }

        boolean allConverge = true;
        boolean allDiverge = true;

        // Check top and bottom edges
        for (int x = x0; x < x1; x++) {
            if (!PointConverges(x, y0)) allConverge = false;
            else allDiverge = false;

            if (!PointConverges(x, y1 - 1)) allConverge = false;
            else allDiverge = false;
        }

        // Check left and right edges
        for (int y = y0 + 1; y < y1 - 1; y++) { // corners already checked
            if (!PointConverges(x0, y)) allConverge = false;
            else allDiverge = false;

            if (!PointConverges(x1 - 1, y)) allConverge = false;
            else allDiverge = false;
        }

        // If the entire perimeter converges → fill black
        if (allConverge) {
            for (int x = x0; x < x1; x++)
                for (int y = y0; y < y1; y++)
                    image.setRGB(x, y, 0);
            return;
        }

        // Otherwise → recurse into 4 subtiles
        int mx = x0 + widthTile / 2;
        int my = y0 + heightTile / 2;

        renderTile(x0, y0, mx, my, level+1);       // upper-left
        renderTile(mx, y0, x1, my, level+1);       // upper-right
        renderTile(x0, my, mx, y1, level+1);       // lower-left
        renderTile(mx, my, x1, y1, level+1);       // lower-right
    }
}