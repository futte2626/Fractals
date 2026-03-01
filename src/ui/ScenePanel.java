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
        BufferedImage image = model.getImage();

        //Opdatere FractalModel settings hvis vindue størrelse ændres
        model.settings.width = getWidth();
        model.settings.height = getHeight();

        g.drawImage(image, 0, 0, null);
    }
}