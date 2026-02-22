import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class BoundryPanel extends JPanel {
    private double centerX = -0.5, centerY = 0, scale = 3;
    private int width, height;
    private int maxIterations = 150;
    private Point lastMouse;
    private BufferedImage image;

    // Grid sampling step – lower values give more accurate boundaries but more work
    private static final int GRID_STEP = 8;

    public BoundryPanel() {
        setPreferredSize(new Dimension(1080, 720));

        addMouseWheelListener(e -> {
            if (e.getWheelRotation() < 0) {
                scale = scale / 1.2;
                maxIterations -= (int) (50 * Math.log10(scale));
            } else {
                scale = scale * 1.2;
                maxIterations -= (int) (50 * Math.log10(scale));
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
        width = getWidth();
        height = getHeight();

        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        long start = System.currentTimeMillis();
        renderBoundaryTracing();
        long end = System.currentTimeMillis();
        lastFrameTimeMs = end - start;

        g.drawImage(image, 0, 0, null);
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Frame time: " + lastFrameTimeMs + " ms", 10, 20);
    }

    /** Returns true if the point (x,y) is inside the Mandelbrot set. */
    private boolean pointConverges(int x, int y) {
        double re = centerX + (x - width / 2.0) * scale / width;
        double im = centerY - (y - height / 2.0) * scale / width;
        double zr = 0, zi = 0;
        for (int i = 0; i < maxIterations; i++) {
            if (zr * zr + zi * zi > 4) return false;
            double newZr = zr * zr - zi * zi + re;
            double newZi = 2 * zr * zi + im;
            zr = newZr;
            zi = newZi;
        }
        return true;
    }

    /** Renders the image using grid‑based boundary tracing. */
    private void renderBoundaryTracing() {
        // 1. Create a low‑resolution grid
        int gridW = (width + GRID_STEP - 1) / GRID_STEP;
        int gridH = (height + GRID_STEP - 1) / GRID_STEP;
        boolean[][] gridInside = new boolean[gridW][gridH];

        for (int gx = 0; gx < gridW; gx++) {
            for (int gy = 0; gy < gridH; gy++) {
                int px = gx * GRID_STEP;
                int py = gy * GRID_STEP;
                gridInside[gx][gy] = pointConverges(px, py);
            }
        }

        // 2. Render each pixel, using the grid to decide whether to compute
        for (int y = 0; y < height; y++) {
            int gy = y / GRID_STEP;
            // Neighboring grid cells in y direction
            int gy0 = Math.max(0, gy - 1);
            int gy1 = Math.min(gridH - 1, gy + 1);

            for (int x = 0; x < width; x++) {
                int gx = x / GRID_STEP;
                // Neighboring grid cells in x direction
                int gx0 = Math.max(0, gx - 1);
                int gx1 = Math.min(gridW - 1, gx + 1);

                // Check if all neighboring grid cells agree
                boolean allSame = true;
                boolean firstVal = gridInside[gx][gy];
                outer:
                for (int nx = gx0; nx <= gx1; nx++) {
                    for (int ny = gy0; ny <= gy1; ny++) {
                        if (gridInside[nx][ny] != firstVal) {
                            allSame = false;
                            break outer;
                        }
                    }
                }

                int color;
                if (allSame) {
                    // Entire neighborhood uniform → fill with that color
                    color = firstVal ? 0 : 0xFFFFFF;
                } else {
                    // On or near boundary → compute precisely
                    color = pointConverges(x, y) ? 0 : 0xFFFFFF;
                }
                image.setRGB(x, y, color);
            }
        }
    }
}