package coloringmethods;

import java.awt.*;

public class WaveScheme implements ColorScheme {

    @Override
    public int getColor(int iter, int maxIter) {
        if (iter == maxIter) return 0x000000;

        double t = (double) iter / maxIter;

        int r = (int)(128 + 127 * Math.sin(10 * t));
        int g = (int)(128 + 127 * Math.sin(10 * t + 2));
        int b = (int)(128 + 127 * Math.sin(10 * t + 4));

        return new Color(r, g, b).getRGB();
    }
}