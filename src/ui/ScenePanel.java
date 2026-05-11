package ui;

import model.FractalModel;
import model.SceneSettings;
import utilities.FractalUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

public class ScenePanel extends JPanel {

    FractalModel model;
    private boolean showOverlay = true;
    private boolean showReferenceOrbit = false;

    private int mouseScreenX = -1;
    private int mouseScreenY = -1;

    public ScenePanel(FractalModel model) {
        this.model = model;
        setPreferredSize(new Dimension(model.settings.width, model.settings.height));

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseScreenX = e.getX();
                mouseScreenY = e.getY();
                if (showReferenceOrbit) repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                mouseScreenX = e.getX();
                mouseScreenY = e.getY();
            }
        });
    }

    /** Called by the View > Show Info Overlay menu item. */
    public void setShowOverlay(boolean show) {
        this.showOverlay = show;
        repaint();
    }

    public void setShowReferenceOrbit(boolean show) {
        this.showReferenceOrbit = show;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Update settings to match actual panel size
        model.settings.width  = getWidth();
        model.settings.height = getHeight();

        // Draw fractal image
        BufferedImage image = model.getImage();
        g2.drawImage(image, 0, 0, null);

        // Crosshair at centre
        g2.setColor(Color.RED);
        g2.setStroke(new BasicStroke(1));
        int cx = getWidth()  / 2;
        int cy = getHeight() / 2;
        g2.drawLine(cx - 6, cy, cx + 6, cy);
        g2.drawLine(cx, cy - 6, cx, cy + 6);

        // Info overlay (bottom-left, red text)
        if (showOverlay) {
            SceneSettings s = model.settings;

            int x = 10;
            int y = getHeight() - 90;
            int lineHeight = 15;

            g2.drawString("Center Re: " + s.centerX, x, y);
            g2.drawString("Center Im: " + s.centerY, x, y += lineHeight);
            g2.drawString("Scale: " + s.scale, x, y += lineHeight);
            g2.drawString("Size: " + s.width + "x" + s.height, x, y += lineHeight);
            g2.drawString("RenderTime: " + model.getFrameTime() / 1000000f, x, y += lineHeight);
            g2.drawString("maxIter: " + model.maxIterations, x, y += lineHeight);
        }

        if (showReferenceOrbit && mouseScreenX >= 0) drawReferenceOrbit(g2);
    }

    private void drawReferenceOrbit(Graphics2D g2) {
        Point2D.Double start = FractalUtil.ScreenToWorld(mouseScreenX, mouseScreenY, model.settings);
        Point2D.Double[] orbit = FractalUtil.ComputeOrbit(start.x, start.y, model.maxIterations);

        if (orbit.length == 0) return;

        int[] ScreenXs = new int[orbit.length];
        int[] ScreenYs = new int[orbit.length];
        for (int i = 0; i < orbit.length; i++) {
            ScreenXs[i] = FractalUtil.WorldToScreenX(orbit[i].x, model.settings);
            ScreenYs[i] = FractalUtil.WorldToScreenY(orbit[i].y, model.settings);
        }

        g2.setColor(Color.orange);
        for (int i = 0; i + 1 < orbit.length; i++) {
            g2.drawLine(ScreenXs[i], ScreenYs[i], ScreenXs[i + 1], ScreenYs[i + 1]);
        }

        for (int i = 0; i < orbit.length; i++) {
            int r = 2;
            if(i == 0) r = 4;
            g2.fillOval(ScreenXs[i] - r, ScreenYs[i] - r, r * 2, r * 2);
        }

        g2.setColor(Color.red);
        String label = String.format("(%.6f, %.6fi)  %d iters", start.x, start.y, orbit.length);
        g2.drawString(label, mouseScreenX + 10, mouseScreenY - 6);
    }
}