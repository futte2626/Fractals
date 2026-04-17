package utilities;

import model.SceneSettings;

import java.awt.geom.Point2D;

public class FractalUtil {
    public static int EscapeTime(double re, double im, int maxIterations) {
        double real = 0, imag = 0;
        double real2, imag2;

        int iterations = 0;
        while (iterations < maxIterations) {
            real2 = real*real;
            imag2 = imag*imag;
            if(real2+imag2>4) break;

            imag = 2 * real * imag + im;
            real = real2 - imag2 + re;
            iterations++;
        }
        return iterations;
    }

    public static boolean PointConverges(double re, double im, int maxIterations) {
        return EscapeTime(re, im, maxIterations) == maxIterations;
    }

    public static boolean PointDiverges(double re, double im, int maxIterations) {
        return EscapeTime(re, im, maxIterations) != maxIterations;
    }

    public static Point2D.Double ScreenToWorld(int x, int y, SceneSettings options) {
        double re = options.centerX + (x - options.width / 2.0) * options.scale / options.width;
        double im = options.centerY + (y - options.height / 2.0) * options.scale / options.height;
        return new Point2D.Double(re, im);
    }

    public static Point2D.Double WorldToScreen(double re, double im, SceneSettings options) {
        double x = (re - options.centerX) * options.width  / options.scale + options.width  / 2.0;
        double y = (im - options.centerY) * options.height / options.scale + options.height / 2.0;
        return new Point2D.Double(x, y);
    }
}

/*
public static int EscapeTime(double re, double im, int maxIterations) {
    double cReal = re;
    double cImag = im;
    double real = 0, imag = 0;
    double newReal, newImag;

    int iterations = 0;
    while (iterations < maxIterations && Math.sqrt(real*real + imag*imag) < 2) {
        newReal = real*real - imag*imag + cReal;
        newImag = 2 * real * imag + cImag;
        real = newReal;
        imag = newImag;
        iterations++;
    }
    return iterations;
} */
/*
public static int EscapeTime(double re, double im, int maxIterations) {
    ComplexNumber c = new ComplexNumber(re,im);
    ComplexNumber z = new ComplexNumber(0,0);

    int iterations = 0;
    while (iterations < maxIterations && z.getMagnitude() < 2) {
        z=z.multiply(z).add(c);
        iterations++;
    }
    return iterations;
} */