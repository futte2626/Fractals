package model;

import coloringmethods.ColorScheme;
import rendermethods.FractalRenderer;
import ui.ScenePanel;
import ui.SettingsPanel;
import utilities.RenderList;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Stack;

public class FractalModel {

    public SceneSettings settings;
    public FractalRenderer renderer;
    public ColorScheme colorScheme;
    public int maxIterations;
    private RenderResult latestRender;
    public RenderList renderList;

    public ScenePanel scenePanel;
    public SettingsPanel settingsPanel; // link to panel

    public FractalModel(SceneSettings settings, FractalRenderer renderer, ColorScheme colorScheme, int maxIterations) {
        this.settings = settings;
        this.renderer = renderer;
        this.colorScheme = colorScheme;
        this.maxIterations = maxIterations;
        renderList = new RenderList(10);
    }

    public void render() {
        latestRender = renderer.render(settings, maxIterations, colorScheme);

        if (renderList.currentRender == null) {
            renderList.currentRender = latestRender;
        }

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


    public void PrintSampleTest(int samples) {
        long[] runTimes = new long[samples];

        for (int sample = 0; sample < samples; sample++) {
            RenderResult result = renderer.render(settings, maxIterations, colorScheme);
            runTimes[sample] = result.frameTime;
        }

        Arrays.sort(runTimes);

        long medianRunTime;
        if (samples % 2 == 1) {
            medianRunTime = runTimes[samples / 2];
        } else {
            medianRunTime = (runTimes[samples / 2 - 1] + runTimes[samples / 2]) / 2;
        }

        System.out.println("Median runtime (ns): " + medianRunTime+ " (ms) " + medianRunTime/1000000f );
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

    public void ClearList() {
        renderList.currentRender = null;
        renderList.currentSize = 0;
    }

    public void zoomIn(double factor) {
        settings.scale /= factor;

        if (renderList.traverseForward()) {
            System.out.println("Used precomputed render");
            latestRender = renderList.currentRender;
        } else {
            System.out.println("Computing new render");
            latestRender = renderer.render(settings, maxIterations, colorScheme);
            renderList.AddToEnd(latestRender);
            renderList.currentRender = latestRender;
        }

        scenePanel.repaint();
        if (settingsPanel != null) settingsPanel.updateInfoLabel();
    }

    public void zoomOut(double factor) {
        settings.scale *= factor;

        if(renderList.traverseBackward()) {
            System.out.println("Used precomputed render");
            long startTime = System.nanoTime();
            latestRender = renderList.currentRender;
            long endTime = System.nanoTime();
            latestRender.frameTime = endTime - startTime;
        }
        else {
            System.out.println("Computing new render");
            latestRender = renderer.render(settings, maxIterations, colorScheme);
            renderList.AddToStart(latestRender);
            renderList.currentRender = latestRender;
        }

        scenePanel.repaint();
        if (settingsPanel != null) settingsPanel.updateInfoLabel();
    }

    public void move(double dx, double dy) {
        settings.centerX += dx;
        settings.centerY += dy;
        ClearList();
        if (settingsPanel != null) settingsPanel.updateInfoLabel();
    }

    public void renderZoomAnimation(double seconds, String outputName) {
        try {
            System.out.println("Zoom animation started");

            final int fps = 30;
            int frames = (int) Math.round(seconds * fps);

            double targetScale = settings.scale;
            double startScale = 2.0;

            File folder = new File("animation_frames");
            if (!folder.exists()) folder.mkdirs();

            for (int i = 0; i < frames; i++) {

                double t = (double) i / (frames - 1);

                // smoothstep interpolation (nice zoom curve)
                double smoothT = t * t * (3 - 2 * t);

                settings.scale = startScale * Math.pow(targetScale / startScale, smoothT);

                RenderResult result = renderer.render(settings, maxIterations, colorScheme);
                latestRender = result;

                File frameFile = new File(folder, String.format("frame_%05d.png", i));
                ImageIO.write(result.image, "png", frameFile);

                System.out.println("Frame " + i + "/" + frames);
            }

            settings.scale = targetScale;

            createVideoWithFFmpeg(folder.getPath(), outputName, fps, frames);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void createVideoWithFFmpeg(String folder, String outputName, int fps, int frames) {
        try {
            System.out.println("Creating video with ffmpeg...");

            String cmd = String.format(
                    "ffmpeg -y -framerate %d -i %s/frame_%%05d.png -c:v libx264 -pix_fmt yuv420p %s.mp4",
                    fps,
                    folder,
                    outputName
            );

            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", cmd);
            pb.redirectErrorStream(true);

            Process process = pb.start();

            new Thread(() -> {
                try (var reader = new java.io.BufferedReader(
                        new java.io.InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println("[ffmpeg] " + line);
                    }
                } catch (Exception ignored) {}
            }).start();

            int exitCode = process.waitFor();

            System.out.println("FFmpeg finished with code: " + exitCode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}