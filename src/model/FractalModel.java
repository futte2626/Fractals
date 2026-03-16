package model;

import coloringmethods.ColorScheme;
import rendermethods.FractalRenderer;
import ui.ScenePanel;
import ui.SettingsPanel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class FractalModel {

    public SceneSettings settings;
    public FractalRenderer renderer;
    public ColorScheme colorScheme;
    public int maxIterations;
    private RenderResult latestRender;

    public ScenePanel scenePanel;
    public SettingsPanel settingsPanel; // link to panel

    public FractalModel(SceneSettings settings, FractalRenderer renderer, ColorScheme colorScheme, int maxIterations) {
        this.settings = settings;
        this.renderer = renderer;
        this.colorScheme = colorScheme;
        this.maxIterations = maxIterations;
    }

    public void render() {
        latestRender = renderer.render(settings, maxIterations, colorScheme);
        if (scenePanel != null) scenePanel.repaint();
        if (settingsPanel != null) settingsPanel.updateInfoLabel();
    }

    public RenderResult getRender() {
        if (latestRender == null) render();
        return latestRender;
    }

    public BufferedImage getImage() {
        if (latestRender == null) render();
        return latestRender.image;
    }

    public void SaveImage(String filename) throws IOException {
        File file = new File(filename + ".jpg");
        ImageIO.write(latestRender.image, "jpg", file);
    }

    public void SaveIterationsTest(int maxSample) {
        try {
            System.out.println("Iteration test started");
            FileWriter writer = new FileWriter("iterationtest.csv");

            writer.append("MaxIterations,iterations\n");

            for (int maxIter = 1; maxIter <= maxSample; maxIter++) {

                RenderResult result = renderer.render(settings, maxIter, colorScheme);
                long totalIterations = result.totalIterationCount;

                writer.append(maxIter + "," + totalIterations + "\n");

                System.out.println("maxIterations: " + maxIter + " actualIterations: " + totalIterations);
            }

            writer.flush();
            writer.close();

            System.out.println("Test finished. File saved as escape_time_test.csv");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void SaveRunTimeTest(int maxSample, int samples) {
        try {
            System.out.println("Runtime test started");
            FileWriter writer = new FileWriter("runtimetest.csv");

            writer.append("MaxIterations,Runtime(ns)\n");

            for (int maxIter = 1; maxIter <= maxSample; maxIter++) {

                long[] runTimes = new long[samples];

                for (int sample = 0; sample < samples; sample++) {
                    RenderResult result = renderer.render(settings, maxIter, colorScheme);
                    runTimes[sample] = result.frameTime;
                }

                Arrays.sort(runTimes);

                long medianRunTime;
                if (samples % 2 == 1) {
                    medianRunTime = runTimes[samples / 2];
                } else {
                    medianRunTime = (runTimes[samples / 2 - 1] + runTimes[samples / 2]) / 2;
                }

                writer.append(maxIter + "," + medianRunTime + "\n");

                System.out.println("maxIterations: " + maxIter + " Runtime(ns): " + medianRunTime);
            }

            writer.flush();
            writer.close();

            System.out.println("Test finished");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public long getFrameTime() {
        if (latestRender == null) render();
        return latestRender.frameTime;
    }

    public void zoom(double factor) {
        settings.scale *= factor;
        if (settingsPanel != null) settingsPanel.updateInfoLabel();
    }

    public void move(double dx, double dy) {
        settings.centerX += dx;
        settings.centerY += dy;
        if (settingsPanel != null) settingsPanel.updateInfoLabel();
    }
}