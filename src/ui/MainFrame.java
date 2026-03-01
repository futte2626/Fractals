package ui;

import coloringmethods.BooleanColorScheme;
import coloringmethods.GreyScaleScheme;
import controllers.InputController;
import model.FractalModel;
import model.SceneSettings;
import rendermethods.EscapeTimeRenderer;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    FractalModel model;
    ScenePanel scene;
    SettingsPanel settings;

    public MainFrame() {
        model = new FractalModel(new SceneSettings(-0.5, 0, 1000, 1000, 3),
                new EscapeTimeRenderer(),
                new GreyScaleScheme(),
                150
        );

        scene = new ScenePanel(model);
        settings = new SettingsPanel(model);

        new InputController(scene, model);

        setLayout(new BorderLayout());
        add(scene, BorderLayout.CENTER);
        add(settings, BorderLayout.EAST);

        setSize(1300, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
}
