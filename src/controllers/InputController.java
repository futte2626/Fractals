package controllers;

import model.FractalModel;
import ui.ScenePanel;

import java.awt.*;
import java.awt.event.*;

public class InputController {

    private Point lastMouse;

    public InputController(ScenePanel view, FractalModel model) {

        view.addMouseWheelListener(e -> {
            if (e.getWheelRotation() < 0) model.zoom(1/1.2);
            else model.zoom(1.2);
            model.render();
            view.repaint();
        });

        view.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { lastMouse = e.getPoint(); }
        });

        view.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                double dx = (e.getX() - lastMouse.x) * model.options.scale / view.getWidth();
                double dy = (e.getY() - lastMouse.y) * model.options.scale / view.getHeight();
                model.move(-dx, -dy);
                lastMouse = e.getPoint();
                model.render();
                view.repaint();
            }
        });
    }
}