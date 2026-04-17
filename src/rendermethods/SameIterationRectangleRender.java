package rendermethods;

import coloringmethods.ColorScheme;
import model.RenderResult;
import model.SceneSettings;
import utilities.FractalUtil;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

public class SameIterationRectangleRender implements FractalRenderer {
    private BufferedImage image;
    @Override
    public RenderResult render(SceneSettings options, int maxIterations, ColorScheme colorScheme) {
        int width = options.width;
        int height = options.height;

        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        long startTime = System.nanoTime();
        CheckRectangle(new IntPoint(0,0), new IntPoint(width-1, height-1), options, maxIterations, colorScheme);
        long endTime = System.nanoTime();

        return new RenderResult(image, endTime-startTime, 0);
    }

    private void CheckRectangle(IntPoint topLeft, IntPoint bottomRight, SceneSettings options, int maxIterations, ColorScheme colorScheme) {
        int tlx = topLeft.x,  tly = topLeft.y, brx = bottomRight.x, bry = bottomRight.y;

        if (brx==tlx && bry==tly) {
            Point2D.Double pos = FractalUtil.ScreenToWorld(tlx, tly, options);
            image.setRGB(tlx, tly, colorScheme.getColor(FractalUtil.EscapeTime(pos.x, pos.y, maxIterations), maxIterations));
            return;
        }

        // ----- Stops code when straight line is reached. And colors them appropriately -----
        if (tlx == brx) { // --- Handles vertical lines ---
            for(int i = tly; i <= bry; i++) {
                Point2D.Double pos = FractalUtil.ScreenToWorld(tlx, i, options);
                int escapeTime = FractalUtil.EscapeTime(pos.x, pos.y, maxIterations);
                image.setRGB(tlx, i, colorScheme.getColor(escapeTime, maxIterations));
            }
            return;
        } else if (tly == bry) { // --- Handles horizontal lines ---
            for(int i = tlx; i <= brx; i++) {
                Point2D.Double pos = FractalUtil.ScreenToWorld(i, tly, options);
                int escapeTime = FractalUtil.EscapeTime(pos.x, pos.y, maxIterations);
                image.setRGB(i, tly, colorScheme.getColor(escapeTime, maxIterations));
            }
            return;
        }

        // ----- Check if all points converges -----
        boolean sameIterationCount = true;

        Point2D.Double pos = FractalUtil.ScreenToWorld(tlx, tly, options);
        int firstIterationCount = FractalUtil.EscapeTime(pos.x, pos.y, maxIterations);

        // --- Check top and bottom line ---
        for(int i = tlx; i <= brx; i++) {
            Point2D.Double posTop = FractalUtil.ScreenToWorld(i, tly, options);
            Point2D.Double posBottom = FractalUtil.ScreenToWorld(i, bry, options);
            int topEscapeTime = FractalUtil.EscapeTime(posTop.x, posTop.y, maxIterations);
            int bottomEscapeTime= FractalUtil.EscapeTime(posBottom.x, posBottom.y, maxIterations);
            image.setRGB(i, tly, 255);
            image.setRGB(i, bry, 255);

            if(topEscapeTime!=firstIterationCount || bottomEscapeTime!=firstIterationCount) {
                sameIterationCount = false;
                break;
            }
        }

        // --- Check left and right line ---
        if(sameIterationCount) {
            for(int i = tly+1; i <= bry-1; i++) {
                Point2D.Double posLeft = FractalUtil.ScreenToWorld(tlx, i, options);
                Point2D.Double posRight = FractalUtil.ScreenToWorld(brx, i, options);
                int leftEscapeTime =FractalUtil.EscapeTime(posLeft.x, posLeft.y, maxIterations);
                int rightEscapeTime = FractalUtil.EscapeTime(posRight.x, posRight.y, maxIterations);
                image.setRGB(tlx, i, 255);
                image.setRGB(brx, i, 255);

                if(leftEscapeTime!=firstIterationCount || rightEscapeTime!=firstIterationCount) {
                    sameIterationCount = false;
                    break;
                }
            }
        }

        if(sameIterationCount) {
            for(int x = tlx+1; x < brx; x++){
                for(int y = tly+1; y < bry; y++) {
                    image.setRGB(x, y, colorScheme.getColor(firstIterationCount, maxIterations));
                }
            }
        }
        else {
            int newX = (brx+tlx)/2; int newY = (bry+tly)/2;
            IntPoint middle = new IntPoint(newX, newY);

            CheckRectangle(topLeft, middle, options, maxIterations, colorScheme);
            CheckRectangle(new IntPoint(newX+1, tly), new IntPoint(brx, newY), options, maxIterations, colorScheme);
            CheckRectangle(new IntPoint(tlx, newY+1), new IntPoint(newX, bry), options, maxIterations, colorScheme);
            CheckRectangle(new IntPoint(newX+1, newY+1), bottomRight, options, maxIterations, colorScheme);
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

/*
private void CheckRectangle(IntPoint topLeft, IntPoint bottomRight, SceneSettings options, int maxIterations, ColorScheme colorScheme) {
        int tlx = topLeft.x,  tly = topLeft.y, brx = bottomRight.x, bry = bottomRight.y;

        if (brx==tlx && bry==tly) {
            Point2D.Double pos = FractalUtil.ScreenToWorld(tlx, tly, options);
            image.setRGB(tlx, tly, colorScheme.getColor(FractalUtil.EscapeTime(pos.x, pos.y, maxIterations), maxIterations));
            return;
        }

        // ----- Stops code when straight line is reached. And colors them appropriately -----
        if (tlx == brx) { // --- Handles vertical lines ---
            for(int i = tly; i <= bry; i++) {
                Point2D.Double pos = FractalUtil.ScreenToWorld(tlx, i, options);
                int escapeTime = FractalUtil.EscapeTime(pos.x, pos.y, maxIterations);
                image.setRGB(tlx, i, colorScheme.getColor(escapeTime, maxIterations));
            }
            return;
        } else if (tly == bry) { // --- Handles horizontal lines ---
            for(int i = tlx; i <= brx; i++) {
                Point2D.Double pos = FractalUtil.ScreenToWorld(i, tly, options);
                int escapeTime = FractalUtil.EscapeTime(pos.x, pos.y, maxIterations);
                image.setRGB(i, tly, colorScheme.getColor(escapeTime, maxIterations));
            }
            return;
        }

        // ----- Check if all points converges -----
        boolean sameIterationCount = true;

        Point2D.Double pos = FractalUtil.ScreenToWorld(tlx, tly, options);
        int firstIterationCount = FractalUtil.EscapeTime(pos.x, pos.y, maxIterations);

        // --- Check top and bottom line ---
        for(int i = tlx; i <= brx; i++) {
            Point2D.Double posTop = FractalUtil.ScreenToWorld(i, tly, options);
            Point2D.Double posBottom = FractalUtil.ScreenToWorld(i, bry, options);
            int topEscapeTime = FractalUtil.EscapeTime(posTop.x, posTop.y, maxIterations);
            int bottomEscapeTime= FractalUtil.EscapeTime(posBottom.x, posBottom.y, maxIterations);
            image.setRGB(i, tly, 255);
            image.setRGB(i, bry, 255);

            if(topEscapeTime!=firstIterationCount || bottomEscapeTime!=firstIterationCount) {
                sameIterationCount = false;
                break;
            }
        }

        // --- Check left and right line ---
        if(sameIterationCount) {
            for(int i = tly+1; i <= bry-1; i++) {
                Point2D.Double posLeft = FractalUtil.ScreenToWorld(tlx, i, options);
                Point2D.Double posRight = FractalUtil.ScreenToWorld(brx, i, options);
                int leftEscapeTime =FractalUtil.EscapeTime(posLeft.x, posLeft.y, maxIterations);
                int rightEscapeTime = FractalUtil.EscapeTime(posRight.x, posRight.y, maxIterations);
                image.setRGB(tlx, i, 255);
                image.setRGB(brx, i, 255);

                if(leftEscapeTime!=firstIterationCount || rightEscapeTime!=firstIterationCount) {
                    sameIterationCount = false;
                    break;
                }
            }
        }

        if(sameIterationCount) {
            for(int x = tlx+1; x < brx; x++){
                for(int y = tly+1; y < bry; y++) {
                    image.setRGB(x, y, colorScheme.getColor(firstIterationCount, maxIterations));
                }
            }
        }
        else {
            int newX = (brx+tlx)/2; int newY = (bry+tly)/2;
            IntPoint middle = new IntPoint(newX, newY);

            CheckRectangle(topLeft, middle, options, maxIterations, colorScheme);
            CheckRectangle(new IntPoint(newX+1, tly), new IntPoint(brx, newY), options, maxIterations, colorScheme);
            CheckRectangle(new IntPoint(tlx, newY+1), new IntPoint(newX, bry), options, maxIterations, colorScheme);
            CheckRectangle(new IntPoint(newX+1, newY+1), bottomRight, options, maxIterations, colorScheme);
        }

    }
 */

