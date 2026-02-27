package rendermethods;

import coloringmethods.ColorScheme;
import utilities.ScreenOptions;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;

public interface FractalRenderer {
    BufferedImage render(ScreenOptions options, int maxIterations, ColorScheme colorScheme);
}
