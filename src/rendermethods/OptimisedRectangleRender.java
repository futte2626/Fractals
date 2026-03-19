package rendermethods;

import coloringmethods.ColorScheme;
import model.RenderResult;
import model.SceneSettings;
import utilities.FractalUtil;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class OptimisedRectangleRender implements FractalRenderer {
    private BufferedImage image;
    private int[][] escapeCache;

    @Override
    public RenderResult render(SceneSettings options, int maxIterations, ColorScheme colorScheme) {
        int width = options.width;
        int height = options.height;

        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        escapeCache = new int[width][height];
        for (int[] row : escapeCache) Arrays.fill(row, -1);

        long startTime = System.nanoTime();
        checkRectangle(new IntPoint(0,0), new IntPoint(width-1, height-1), options, maxIterations, colorScheme);
        long endTime = System.nanoTime();

        return new RenderResult(image, endTime - startTime, 0);
    }

    // ----------- Recursive rectangle check -----------
    private void checkRectangle(IntPoint topLeft, IntPoint bottomRight,
                                SceneSettings options, int maxIterations, ColorScheme colorScheme) {
        int tlx = topLeft.x, tly = topLeft.y, brx = bottomRight.x, bry = bottomRight.y;

        // single pixel
        if (tlx == brx && tly == bry) {
            int escape = computeEscape(tlx, tly, options, maxIterations);
            int color = colorScheme.getColor(escape, maxIterations);
            image.setRGB(tlx, tly, color);
            return;
        }

        // vertical line
        if (tlx == brx) {
            for (int y = tly; y <= bry; y++) {
                int escape = computeEscape(tlx, y, options, maxIterations);
                image.setRGB(tlx, y, colorScheme.getColor(escape, maxIterations));
            }
            return;
        }

        // horizontal line
        if (tly == bry) {
            for (int x = tlx; x <= brx; x++) {
                int escape = computeEscape(x, tly, options, maxIterations);
                image.setRGB(x, tly, colorScheme.getColor(escape, maxIterations));
            }
            return;
        }

        // --- check edges ---
        boolean sameIterationCount = true;
        int firstIteration = computeEscape(tlx, tly, options, maxIterations);

        // top and bottom
        for (int x = tlx; x <= brx; x++) {
            int top = computeEscape(x, tly, options, maxIterations);
            int bottom = computeEscape(x, bry, options, maxIterations);
            if (top != firstIteration || bottom != firstIteration) {
                sameIterationCount = false;
                break;
            }
        }

        // left and right
        if (sameIterationCount) {
            for (int y = tly + 1; y <= bry - 1; y++) {
                int left = computeEscape(tlx, y, options, maxIterations);
                int right = computeEscape(brx, y, options, maxIterations);
                if (left != firstIteration || right != firstIteration) {
                    sameIterationCount = false;
                    break;
                }
            }
        }

        // fill interior if all the same
        if (sameIterationCount) {
            int color = colorScheme.getColor(firstIteration, maxIterations);
            for (int x = tlx; x <= brx; x++) {
                for (int y = tly; y <= bry; y++) {
                    image.setRGB(x, y, color);
                    escapeCache[x][y] = firstIteration;
                }
            }
        } else {
            // subdivide
            int midX = (tlx + brx) / 2;
            int midY = (tly + bry) / 2;

            checkRectangle(topLeft, new IntPoint(midX, midY), options, maxIterations, colorScheme);
            checkRectangle(new IntPoint(midX + 1, tly), new IntPoint(brx, midY), options, maxIterations, colorScheme);
            checkRectangle(new IntPoint(tlx, midY + 1), new IntPoint(midX, bry), options, maxIterations, colorScheme);
            checkRectangle(new IntPoint(midX + 1, midY + 1), bottomRight, options, maxIterations, colorScheme);
        }
    }

    // ----------- escape-time with caching -----------
    private int computeEscape(int x, int y, SceneSettings options, int maxIterations) {
        if (escapeCache[x][y] != -1) return escapeCache[x][y];

        Point2D.Double pos = FractalUtil.ScreenToWorld(x, y, options);
        int escape = FractalUtil.EscapeTime(pos.x, pos.y, maxIterations);
        escapeCache[x][y] = escape;
        return escape;
    }

    static class IntPoint {
        int x, y;
        IntPoint(int x, int y) { this.x = x; this.y = y; }
    }
}