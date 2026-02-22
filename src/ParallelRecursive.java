import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class ParallelRecursive extends JPanel {
    private double centerX = -0.5, centerY = 0, scale = 3;
    private int width, height;
    private final int maxIterations = 1000;
    private Point lastMouse;
    private BufferedImage image;

    private final ForkJoinPool pool = new ForkJoinPool(); // parallel thread pool

    public ParallelRecursive() {
        setPreferredSize(new Dimension(1080, 720));

        addMouseWheelListener(e -> {
            if (e.getWheelRotation() < 0) scale /= 1.2;
            else scale *= 1.2;
            repaint();
        });

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { lastMouse = e.getPoint(); }
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
        width = getWidth();
        height = getHeight();

        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        long start = System.currentTimeMillis();
        pool.invoke(new TileTask(0, 0, width, height));
        long end = System.currentTimeMillis();
        lastFrameTimeMs = end - start;

        g.drawImage(image, 0, 0, null);

        // Draw time per frame
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Frame time: " + lastFrameTimeMs + " ms", 10, 20);
    }

    private boolean PointConverges(int x, int y) {
        double reVal = centerX + (x - width / 2.0) * scale / width;
        double imVal = centerY - (y - height / 2.0) * scale / width;
        ComplexNumber z = new ComplexNumber(0, 0);
        ComplexNumber c = new ComplexNumber(reVal, imVal);

        for (int i = 0; i < maxIterations; i++) {
            if (z.getMagnitude() > 2) return false;
            z = z.multiply(z).add(c);
        }
        return true;
    }

    // ForkJoin RecursiveAction
    private class TileTask extends RecursiveAction {
        private final int x0, y0, x1, y1;

        TileTask(int x0, int y0, int x1, int y1) {
            this.x0 = x0; this.y0 = y0; this.x1 = x1; this.y1 = y1;
        }

        @Override
        protected void compute() {
            int widthTile = x1 - x0;
            int heightTile = y1 - y0;

            // Base case: single pixel
            if (widthTile <= 1 && heightTile <= 1) {
                if (PointConverges(x0, y0)) image.setRGB(x0, y0, 0);
                else image.setRGB(x0, y0, 0xFFFFFF);
                return;
            }

            boolean allConverge = true;
            boolean allDiverge = true;

            // Check perimeter
            for (int x = x0; x < x1; x++) {
                if (!PointConverges(x, y0)) allConverge = false; else allDiverge = false;
                if (!PointConverges(x, y1 - 1)) allConverge = false; else allDiverge = false;
            }
            for (int y = y0 + 1; y < y1 - 1; y++) {
                if (!PointConverges(x0, y)) allConverge = false; else allDiverge = false;
                if (!PointConverges(x1 - 1, y)) allConverge = false; else allDiverge = false;
            }

            // Fill if uniform
            if (allConverge) {
                for (int x = x0; x < x1; x++)
                    for (int y = y0; y < y1; y++)
                        image.setRGB(x, y, 0);
                return;
            }

            // Otherwise, recurse in parallel
            int mx = x0 + widthTile / 2;
            int my = y0 + heightTile / 2;

            invokeAll(
                    new TileTask(x0, y0, mx, my),     // upper-left
                    new TileTask(mx, y0, x1, my),     // upper-right
                    new TileTask(x0, my, mx, y1),     // lower-left
                    new TileTask(mx, my, x1, y1)      // lower-right
            );
        }
    }
}