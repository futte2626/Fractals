package rendermethods;

import coloringmethods.ColorScheme;
import model.RenderResult;
import model.SceneSettings;
import utilities.FractalUtil;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

public class UnoptimisedRectangleRenderer implements FractalRenderer {
    private BufferedImage image;
    private SceneSettings settings;
    private int maxIterations;
    private ColorScheme colorScheme;
    @Override
    public RenderResult render(SceneSettings options, int maxIterations, ColorScheme colorScheme) {
        int width = options.width;
        int height = options.height;
        this.settings = options;
        this.maxIterations = maxIterations;
        this.colorScheme = colorScheme;

        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        long startTime = System.nanoTime();
        CheckRectangle(new IntPoint(0,0), new IntPoint(width-1, height-1));
        long endTime = System.nanoTime();

        return new RenderResult(image, endTime-startTime, 0);
    }

    private void CheckRectangle(IntPoint topLeft, IntPoint bottomRight) {
        int tlx = topLeft.x,  tly = topLeft.y, brx = bottomRight.x, bry = bottomRight.y;

        // ----- Stops code when straight line is reached. And colors them appropriately -----
        if (tlx == brx) { // --- Handles vertical lines ---
            for(int i = tly; i <= bry; i++) {
                Point2D.Double pos = FractalUtil.ScreenToWorld(tlx, i, settings);
                int escapeTime = FractalUtil.EscapeTime(pos.x, pos.y, maxIterations);
                image.setRGB(tlx, i, colorScheme.getColor(escapeTime, maxIterations));
            }
            return;
        } else if (tly == bry) { // --- Handles horizontal lines ---
            for(int i = tlx; i <= brx; i++) {
                Point2D.Double pos = FractalUtil.ScreenToWorld(i, tly, settings);
                int escapeTime = FractalUtil.EscapeTime(pos.x, pos.y, maxIterations);
                image.setRGB(i, tly, colorScheme.getColor(escapeTime, maxIterations));
            }
            return;
        }

        // ----- Check if all points converges -----
        boolean allConverges = true;

        // --- Check top and bottom line ---
        for(int i = tlx; i <= brx; i++) {
            Point2D.Double posTop = FractalUtil.ScreenToWorld(i, tly, settings);
            Point2D.Double posBottom = FractalUtil.ScreenToWorld(i, bry, settings);

            if(FractalUtil.PointDiverges(posTop.x, posTop.y, maxIterations) || FractalUtil.PointDiverges(posBottom.x, posBottom.y, maxIterations)) {
                allConverges = false;
                break;
            }
        }

        // --- Check left and right line ---
        if(allConverges) {
            for(int i = tly+1; i <= bry-1; i++) {
                Point2D.Double posLeft = FractalUtil.ScreenToWorld(tlx, i, settings);
                Point2D.Double posRight = FractalUtil.ScreenToWorld(brx, i, settings);

                if(FractalUtil.PointDiverges(posLeft.x, posLeft.y, maxIterations) || FractalUtil.PointDiverges(posRight.x, posRight.y, maxIterations)) {
                    allConverges = false;
                    break;
                }
            }
        }

        if(allConverges){
            for(int x = tlx; x <= brx; x++){
                for(int y = tly; y <= bry; y++) {
                    image.setRGB(x, y, colorScheme.getColor(maxIterations, maxIterations));
                }
            }
        }
        else {
            int newX = (brx+tlx)/2; int newY = (bry+tly)/2;
            IntPoint middle = new IntPoint(newX, newY);

            CheckRectangle(topLeft, middle);
            CheckRectangle(new IntPoint(newX+1, tly), new IntPoint(brx, newY));
            CheckRectangle(new IntPoint(tlx, newY+1), new IntPoint(newX, bry));
            CheckRectangle(new IntPoint(newX+1, newY+1), bottomRight);
        }
    }

    static class IntPoint {
        int x, y;
        IntPoint(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}

