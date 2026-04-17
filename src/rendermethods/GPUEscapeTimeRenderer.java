package rendermethods;

import coloringmethods.ColorScheme;
import gpu.OpenCLContext;
import model.RenderResult;
import model.SceneSettings;

import java.awt.image.BufferedImage;
import java.util.Arrays;

public class GPUEscapeTimeRenderer implements FractalRenderer {

    private final OpenCLContext cl = new OpenCLContext();

    @Override
    public RenderResult render(SceneSettings s, int maxIter, ColorScheme scheme) {

        int total = s.width * s.height;
        int[] pixels = new int[total];

        long start = System.nanoTime();
        cl.runMandelbrot(pixels, s, maxIter);
        long gpuTime = System.nanoTime() - start;

        // Colourise in parallel — trivially data-parallel, no contention
        Arrays.parallelSetAll(pixels, i -> scheme.getColor(pixels[i], maxIter));

        // Write directly into the DataBuffer — avoids a full array copy
        BufferedImage img = new BufferedImage(s.width, s.height,
                BufferedImage.TYPE_INT_RGB);
        img.setRGB(0, 0, s.width, s.height, pixels, 0, s.width);

        return new RenderResult(img, gpuTime, 0);
    }
}