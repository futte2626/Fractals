package rendermethods;

import coloringmethods.ColorScheme;
import model.RenderResult;
import model.SceneSettings;

public interface FractalRenderer {
    RenderResult render(SceneSettings options, int maxIterations, ColorScheme colorScheme);
}
