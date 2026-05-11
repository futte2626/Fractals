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

    public static double ScreenToWorldRe(int x, SceneSettings options) {
        return options.centerX + (x - options.width * 0.5) * options.scale / options.width;
    }

    public static double ScreenToWorldIm(int y, SceneSettings options) {
        return options.centerY + (y - options.height * 0.5) * options.scale / options.height;
    }

    public static int WorldToScreenX(double re, SceneSettings options) {
        return (int) Math.round((re - options.centerX) * options.width / options.scale + options.width  / 2.0);
    }

    public static int WorldToScreenY(double im, SceneSettings options) {
        return (int) Math.round((im - options.centerY) * options.height / options.scale + options.height / 2.0);
    }


    public static Point2D.Double[] ComputeOrbit(double re, double im, int maxIterations) {
        Point2D.Double[] orbit = new Point2D.Double[maxIterations + 1];
        int count = 0;

        double real = 0, imag = 0;

        // z_0 = 0 (the starting point is always the origin)
        orbit[count++] = new Point2D.Double(real, imag);

        for (int i = 0; i < maxIterations; i++) {
            double real2 = real * real;
            double imag2 = imag * imag;
            if (real2 + imag2 > 4) break;

            imag = 2 * real * imag + im;
            real = real2 - imag2 + re;
            orbit[count++] = new Point2D.Double(real, imag);
        }

        // Trim to actual length
        Point2D.Double[] result = new Point2D.Double[count];
        System.arraycopy(orbit, 0, result, 0, count);
        return result;
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