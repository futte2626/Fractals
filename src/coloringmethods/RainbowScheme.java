package coloringmethods;

import java.awt.*;

public class RainbowScheme implements ColorScheme {

    @Override
    public int getColor(int iter, int maxIter) {
        if (iter == maxIter) return 0x000000;

        float t = (float) iter / maxIter;

        float hue = 0.8f + 5 * t;
        hue = hue - (float)Math.floor(hue);

        return Color.HSBtoRGB(hue, 1f, 1f);
    }
}