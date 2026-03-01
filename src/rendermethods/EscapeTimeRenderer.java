package rendermethods;

import coloringmethods.ColorScheme;
import java.awt.image.BufferedImage;
import java.awt.geom.Point2D;

import model.RenderResult;
import utilities.ComplexNumber; // Make sure your utilities.ComplexNumber class is in util or correct package
import utilities.FractalUtil;
import model.SceneSettings;


public class EscapeTimeRenderer implements FractalRenderer {

    @Override
    public RenderResult render(SceneSettings options, int maxIterations,
                               ColorScheme colorScheme) {
        int width = options.width;
        int height = options.height;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        long startTime = System.nanoTime();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {

                // Convert pixel to complex plane
                Point2D.Double pos = FractalUtil.ScreenToWorld(x, y, options);
                utilities.ComplexNumber c = new ComplexNumber(pos.x, pos.y);
                utilities.ComplexNumber z = new ComplexNumber(0, 0);

                int iterations = 0;
                while (iterations < maxIterations) {
                    if (z.getMagnitude() > 2) break;
                    z = z.multiply(z).add(c);
                    iterations++;
                }

                // Map iterations to color using the color scheme
                int rgb = colorScheme.getColor(iterations, maxIterations);
                image.setRGB(x, y, rgb);
            }
        }
        long endTime = System.nanoTime();

        return new RenderResult(image, endTime - startTime);
    }
}