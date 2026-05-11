package controllers;

import model.FractalModel;
import ui.ScenePanel;

import java.awt.*;
import java.awt.event.*;

public class InputController {

    private Point lastMouse;
    private double factor = 1.2;

    public InputController(ScenePanel view, FractalModel model) {

        view.addMouseWheelListener(e -> {
            System.out.println(e.getWheelRotation());
            if (e.getWheelRotation() < 0) model.zoomIn(factor);
            else model.zoomOut(factor);
        });

        view.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { lastMouse = e.getPoint(); }
        });

        view.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                double dx = (e.getX() - lastMouse.x) * model.settings.scale / view.getWidth();
                double dy = (e.getY() - lastMouse.y) * model.settings.scale / view.getHeight();
                model.move(-dx, -dy);
                lastMouse = e.getPoint();
                model.render();
                view.repaint();
            }
        });


    }
}