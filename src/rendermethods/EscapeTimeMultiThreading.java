package rendermethods;

import coloringmethods.ColorScheme;
import java.awt.image.BufferedImage;
import java.awt.geom.Point2D;
import java.util.concurrent.*;
import java.util.concurrent.atomic.LongAdder;
import java.util.Set;

import model.RenderResult;
import utilities.FractalUtil;
import model.SceneSettings;

public class EscapeTimeMultiThreading implements FractalRenderer {
    private static final ForkJoinPool pool = ForkJoinPool.commonPool();


    @Override
    public RenderResult render(SceneSettings settings, int maxIterations, ColorScheme colorScheme) {

        int width = settings.width;
        int height = settings.height;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);


        long startTime = System.nanoTime();

        for (int y = 0; y < height; y++) {
            final int row = y;

            pool.submit(() -> {
                for (int x = 0; x < width; x++) {
                    Point2D.Double p = FractalUtil.ScreenToWorld(x, row, settings);
                    int iterations = FractalUtil.EscapeTime(p.x, p.y, maxIterations);

                    int rgb = colorScheme.getColor(iterations, maxIterations);
                    image.setRGB(x, row, rgb);
                }
            });
        }

        long renderTime = System.nanoTime() - startTime;

        return new RenderResult(image, renderTime, 0);
    }
}