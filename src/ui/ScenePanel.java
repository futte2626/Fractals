package ui;

import model.FractalModel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ScenePanel extends JPanel {
    FractalModel model;

    public ScenePanel(FractalModel model) {
        this.model = model;
        setPreferredSize(new Dimension(model.settings.width, model.settings.height));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        BufferedImage image = model.getImage();

        //Opdatere FractalModel settings hvis vindue størrelse ændres
        model.settings.width = getWidth();
        model.settings.height = getHeight();

        g2d.drawImage(image, 0, 0, null);
        g2d.setColor(Color.red);
        g2d.setStroke(new BasicStroke(1));
        int cx = getWidth() / 2;
        int cy = getHeight() / 2;

        g2d.drawLine(cx - 5, cy, cx + 5, cy); // horizontal
        g2d.drawLine(cx, cy - 5, cx, cy + 5); // vertical
    }
}