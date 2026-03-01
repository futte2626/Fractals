package model;

import coloringmethods.ColorScheme;
import rendermethods.FractalRenderer;

import java.awt.image.BufferedImage;

public class FractalModel {

    public SceneSettings settings;
    public FractalRenderer renderer;
    public ColorScheme colorScheme;
    public int maxIterations;
    private RenderResult latestRender;

    public FractalModel(SceneSettings settings, FractalRenderer renderer, ColorScheme colorScheme, int maxIterations) {
        this.settings = settings;
        this.renderer = renderer;
        this.colorScheme = colorScheme;
        this.maxIterations = maxIterations;
    }

    public void render() {
        latestRender = renderer.render(settings, maxIterations, colorScheme);
    }
    public RenderResult getRender() {
        if(latestRender == null) render();
        return latestRender;
    }
    public BufferedImage getImage() {
        if(latestRender == null) render();
        return latestRender.image;
    }
    public long getFrameTime() {
        if(latestRender == null) render();
        return latestRender.frameTime;
    }

    public void zoom(double factor) { settings.scale *= factor; }
    public void move(double dx, double dy) {
        settings.centerX += dx;
        settings.centerY += dy;
    }

}