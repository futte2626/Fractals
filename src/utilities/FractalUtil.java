package utilities;

import model.SceneSettings;

import java.awt.geom.Point2D;

public class FractalUtil {
    public static int EscapeTime(double re, double im, int maxIterations) {
        ComplexNumber c = new ComplexNumber(re, im);
        ComplexNumber z = new ComplexNumber(0, 0);

        int iterations = 0;
        while (iterations < maxIterations) {
            if(z.getMagnitude() > 2) break;
            z=z.multiply(z).add(c);
            iterations++;
        }
        return iterations;
    }

    public static boolean PointConverges(double re, double im, int maxIterations) {
        return EscapeTime(re, im, maxIterations) == maxIterations;
    }

    public static Point2D.Double ScreenToWorld(int x, int y, SceneSettings options) {
        double re = options.centerX + (x - options.width / 2.0) * options.scale / options.width;
        double im = options.centerY + (y - options.height / 2.0) * options.scale / options.height;
        return new Point2D.Double(re, im);
    }
}
