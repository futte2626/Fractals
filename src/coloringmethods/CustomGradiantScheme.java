package coloringmethods;

import java.awt.*;

public class CustomGradiantScheme implements ColorScheme{
    public static Color inside = Color.black;
    public static Color color1 = Color.red;
    public static Color color2 = Color.green;
    public static double weight = 1;

    @Override
    public int getColor(int iterations, int maxIterations) {
        if(iterations == maxIterations) return inside.getRGB();

        float t = (float)(iterations-1)/ maxIterations;

        int newRed = (int) ((1-Math.pow(t, weight))*color1.getRed() + Math.pow(t, weight)*color2.getRed());
        int newGreen = (int) ((1-Math.pow(t, weight))*color1.getGreen() + Math.pow(t, weight)*color2.getGreen());
        int newBlue = (int) ((1-Math.pow(t, weight))*color1.getBlue() + Math.pow(t, weight)*color2.getBlue());
        Color newColor = new Color(newRed, newGreen, newBlue);

        return newColor.getRGB();
    }

}
