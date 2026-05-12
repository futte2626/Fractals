package coloringmethods;

import java.awt.*;

public class CustomGradiantScheme implements ColorScheme{
    public static Color inside = Color.black;
    public static Color color1 = Color.red;
    public static Color color2 = Color.green;

    @Override
    public int getColor(int iterations, int maxIterations) {
        if(iterations == maxIterations) return inside.getRGB();

        float t = (float)iterations / maxIterations;

        int newRed = (int) (color1.getRed() * t + color2.getRed()*(1-t));
        int newGreen = (int) (color1.getGreen() * t + color2.getGreen()*(1-t));
        int newBlue = (int) (color1.getBlue() * t + color2.getBlue()*(1-t));
        Color newColor = new Color(newRed, newGreen, newBlue);

        return newColor.getRGB();
    }

}
