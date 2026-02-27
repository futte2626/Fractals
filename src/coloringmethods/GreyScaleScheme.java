package coloringmethods;

import java.awt.*;

public class GreyScaleScheme implements ColorScheme{
    @Override
    public int getColor(int iterations, int maxIterations) {
        if(iterations == maxIterations) return 0;
        int cVal = (int)(255*((float)iterations/((float)maxIterations)));
        Color color = new Color(255-cVal, 255-cVal, 255-cVal);
        return color.getRGB();
    }
}
