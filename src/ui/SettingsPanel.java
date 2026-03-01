package ui;

import model.FractalModel;

import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends JPanel {
    FractalModel model;

    JLabel informationLabel;
    JButton increaseIterations;
    JButton decreaseIterations;

    SettingsPanel(FractalModel model) {
        this.model = model;

        setPreferredSize(new Dimension(300, model.options.height));
        setBackground(Color.lightGray);

        informationLabel = new JLabel("Frame time: " + model.getFrameTime() + "\n" + "maxIterations: " + model.maxIterations);
        increaseIterations = new JButton("Increase Iterations");
        decreaseIterations = new JButton("Decrease Iterations");
        add(informationLabel);
        add(increaseIterations);
        add(decreaseIterations);

        increaseIterations.addActionListener(e -> {
           model.maxIterations += 50;
        });
        decreaseIterations.addActionListener(e -> {
            model.maxIterations -= 50;
        });
    }
}
