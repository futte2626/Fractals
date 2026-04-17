package rendermethods;

import coloringmethods.ColorScheme;
import model.RenderResult;
import model.SceneSettings;
import utilities.FractalUtil;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class MultiThreadingRectangleRender implements FractalRenderer {

    private static final ForkJoinPool pool = ForkJoinPool.commonPool();

    private BufferedImage image;
    private int[] pixels;
    private int[] escapeCache;
    private int imageWidth;

    @Override
    public RenderResult render(SceneSettings options, int maxIterations, ColorScheme colorScheme) {

        int width = options.width;
        int height = options.height;
        imageWidth = width;

        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        pixels = new int[width * height];

        escapeCache = new int[width * height];
        java.util.Arrays.fill(escapeCache, -1);

        long startTime = System.nanoTime();

        pool.invoke(new RectangleTask(
                new IntPoint(0, 0),
                new IntPoint(width - 1, height - 1),
                options,
                maxIterations,
                colorScheme
        ));

        image.setRGB(0, 0, width, height, pixels, 0, width);

        long endTime = System.nanoTime();

        return new RenderResult(image, endTime - startTime, 0);
    }

    // ---------------- ForkJoin task ----------------

    private class RectangleTask extends RecursiveAction {

        IntPoint topLeft;
        IntPoint bottomRight;
        SceneSettings options;
        int maxIterations;
        ColorScheme colorScheme;

        RectangleTask(IntPoint tl, IntPoint br, SceneSettings o, int m, ColorScheme c) {
            this.topLeft = tl;
            this.bottomRight = br;
            this.options = o;
            this.maxIterations = m;
            this.colorScheme = c;
        }

        @Override
        protected void compute() {
            int tlx = topLeft.x, tly = topLeft.y, brx = bottomRight.x, bry = bottomRight.y;

            // single pixel
            if (tlx == brx && tly == bry) {
                int escape = computeEscape(tlx, tly, options, maxIterations);
                pixels[tly * imageWidth + tlx] = colorScheme.getColor(escape, maxIterations);
                return;
            }

            // vertical line
            if (tlx == brx) {
                for (int y = tly; y <= bry; y++) {
                    int escape = computeEscape(tlx, y, options, maxIterations);
                    pixels[y * imageWidth + tlx] = colorScheme.getColor(escape, maxIterations);
                }
                return;
            }

            // horizontal line
            if (tly == bry) {
                for (int x = tlx; x <= brx; x++) {
                    int escape = computeEscape(x, tly, options, maxIterations);
                    pixels[tly * imageWidth + x] = colorScheme.getColor(escape, maxIterations);
                }
                return;
            }

            boolean sameIterationCount = true;
            int firstIteration = computeEscape(tlx, tly, options, maxIterations);

            for (int x = tlx; x <= brx; x++) {
                int top = computeEscape(x, tly, options, maxIterations);
                int bottom = computeEscape(x, bry, options, maxIterations);
                if (top != firstIteration || bottom != firstIteration) {
                    sameIterationCount = false;
                    break;
                }
            }

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

            if (sameIterationCount) {

                int color = colorScheme.getColor(firstIteration, maxIterations);

                for (int x = tlx; x <= brx; x++) {
                    for (int y = tly; y <= bry; y++) {
                        pixels[y * imageWidth + x] = color;
                        escapeCache[y * imageWidth + x] = firstIteration;
                    }
                }

            } else {

                int midX = (tlx + brx) / 2;
                int midY = (tly + bry) / 2;

                RectangleTask q1 = new RectangleTask(topLeft, new IntPoint(midX, midY), options, maxIterations, colorScheme);
                RectangleTask q2 = new RectangleTask(new IntPoint(midX + 1, tly), new IntPoint(brx, midY), options, maxIterations, colorScheme);
                RectangleTask q3 = new RectangleTask(new IntPoint(tlx, midY + 1), new IntPoint(midX, bry), options, maxIterations, colorScheme);
                RectangleTask q4 = new RectangleTask(new IntPoint(midX + 1, midY + 1), bottomRight, options, maxIterations, colorScheme);

                invokeAll(q1, q2, q3, q4);
            }
        }
    }

    private int computeEscape(int x, int y, SceneSettings options, int maxIterations) {
        int idx = y * imageWidth + x;
        int cached = escapeCache[idx];
        if (cached != -1) return cached;

        Point2D.Double pos = FractalUtil.ScreenToWorld(x, y, options);
        int escape = FractalUtil.EscapeTime(pos.x, pos.y, maxIterations);
        escapeCache[idx] = escape;
        return escape;
    }

    static class IntPoint {
        int x, y;
        IntPoint(int x, int y) { this.x = x; this.y = y; }
    }
}