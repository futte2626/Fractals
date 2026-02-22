import java.awt.*;
import java.awt.image.BufferedImage;

public class Mandlebrot {
    public static int width;
    public static int height;
    public static int maxIterations;

    private double centerX = -0.5, centerY = 0, scale = 3;
    private Point lastMouse;
    public static BufferedImage image;
}
