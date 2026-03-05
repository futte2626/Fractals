package rendermethods;

import coloringmethods.ColorScheme;
import java.awt.image.BufferedImage;
import java.awt.geom.Point2D;

import model.RenderResult;
import utilities.FractalUtil;
import model.SceneSettings;


public class EscapeTimeRenderer implements FractalRenderer {
    @Override
    public RenderResult render(SceneSettings settings, int maxIterations, ColorScheme colorScheme) {
        int width = settings.width;
        int height = settings.height;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        long totalIterations = 0;
        long startTime = System.nanoTime();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {

                Point2D.Double p = FractalUtil.ScreenToWorld(x,y, settings);
                int iterations = FractalUtil.EscapeTime(p.x,p.y,maxIterations);
                totalIterations += iterations;

                int rgb = colorScheme.getColor(iterations, maxIterations);
                image.setRGB(x, y, rgb);
            }
        }
        return new RenderResult(image, System.nanoTime() - startTime, totalIterations);
    }
}