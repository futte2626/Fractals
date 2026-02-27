package utilities;

public class ScreenOptions {
    public double centerX, centerY, scale;
    public int width, height;

    public ScreenOptions(double centerX, double centerY, int width, int height, double scale) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.width = width;
        this.height = height;
        this.scale = scale;
    }
}
