package model;

import coloringmethods.ColorScheme;
import rendermethods.FractalRenderer;
import rendermethods.MinSizeRectangleRender;
import rendermethods.PaperSplitRectangleRender;
import ui.ScenePanel;
import ui.SettingsPanel;

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
    private Stack<RenderResult> renderStack;

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

    public void SaveMinSizeTest() {
        try {
            int samples = 60;
            renderer = new MinSizeRectangleRender();
            System.out.println("Runtime test started");
            FileWriter writer = new FileWriter("minsizetest.csv");

            writer.append("MinSize,Runtime(ns)\n");

            for (int i = 1; i <= 16; i++) {
                MinSizeRectangleRender.minSize= i;
                renderer.render(settings, 200, colorScheme);

                System.gc();
                Thread.sleep(50);

                long[] runTimes = new long[samples];

                int batchSize = 5;

                for (int sample = 0; sample < samples; sample++) {
                    long start = System.nanoTime();

                    for (int j = 0; j < batchSize; j++) {
                        renderer.render(settings, 200, colorScheme);
                    }

                    long end = System.nanoTime();
                    runTimes[sample] = (end - start) / batchSize;
                }

                Arrays.sort(runTimes);
                int trim = samples / 10;
                long[] trimmed = Arrays.copyOfRange(runTimes, trim, samples - trim);

// then take median of trimmed[]

                long medianRunTime;
                if (samples % 2 == 1) {
                    medianRunTime = trimmed[samples / 2];
                } else {
                    medianRunTime = (trimmed[samples / 2 - 1] + trimmed[samples / 2]) / 2;
                }


                writer.append(i + "," + medianRunTime + "\n");

                System.out.println("minSize: " + i + " Runtime(ns): " + medianRunTime);
            }

            writer.flush();
            writer.close();

            System.out.println("Test finished");
        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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

    public void zoom(double factor) {
        settings.scale *= factor;
        if (settingsPanel != null) settingsPanel.updateInfoLabel();
    }

    public void move(double dx, double dy) {
        settings.centerX += dx;
        settings.centerY += dy;
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