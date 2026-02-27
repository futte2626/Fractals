import rendermethods.*;
import coloringmethods.*;
import utilities.ScreenOptions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

public class Mandelbrot extends JPanel {
    private int maxIterations = 100;
    private Point lastMouse;
    private BufferedImage image;
    private ScreenOptions options;
    private FractalRenderer renderer;
    private ColorScheme colorScheme;

    // Constructor takes the renderer and color scheme
    public Mandelbrot(FractalRenderer renderer, ColorScheme colorScheme) {
        this.renderer = renderer;
        this.colorScheme = colorScheme;
        options = new ScreenOptions(-0.5, 0, 1000, 1000, 3);

        setPreferredSize(new Dimension(options.width, options.height));

        // Zoom with mouse wheel
        addMouseWheelListener(e -> {
            if (e.getWheelRotation() < 0) options.scale /= 1.2;
            else options.scale *= 1.2;
            repaint();
        });

        // Start dragging
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastMouse = e.getPoint();
            }
        });

        // Drag to pan
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                options.centerX -= (e.getX() - lastMouse.x) * options.scale / getWidth();
                options.centerY -= (e.getY() - lastMouse.y) * options.scale / getHeight();
                lastMouse = e.getPoint();
                repaint();
            }
        });
    }

    private long frameRenderTimeMs = 0;
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Render fractal
        long start = System.nanoTime();
        image = renderer.render(options, maxIterations, colorScheme);
        long end = System.nanoTime();

        frameRenderTimeMs = (end - start) / 1000000;
        // Draw the image
        g.drawImage(image, 0, 0, null);

        // Draw information string
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Frame time: " + frameRenderTimeMs + " ms    Scale: " + options.scale, 10, 20);
    }

    public void ChangeRenderer(FractalRenderer renderer) {
        this.renderer = renderer;
        repaint();
    }
    public void ChangeColorScheme(ColorScheme colorScheme) {
        this.colorScheme = colorScheme;
        repaint();
    }
}