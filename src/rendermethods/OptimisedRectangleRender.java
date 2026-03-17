package rendermethods;

import coloringmethods.ColorScheme;
import model.RenderResult;
import model.SceneSettings;
import utilities.FractalUtil;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

public class OptimisedRectangleRender implements FractalRenderer {
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
        boolean allConverges = true;
        int width = brx-tlx, height = bry-tly;

        // --- Check top and bottom line ---
        for(int i = tlx; i <= brx; i++) {
            Point2D.Double posTop = FractalUtil.ScreenToWorld(i, tly, options);
            Point2D.Double posBottom = FractalUtil.ScreenToWorld(i, bry, options);

            if(FractalUtil.PointDiverges(posTop.x, posTop.y, maxIterations) || FractalUtil.PointDiverges(posBottom.x, posBottom.y, maxIterations)) {
                allConverges = false;
                break;
            }
        }

        // --- Check left and right line ---
        if(allConverges) {
            for(int i = tly+1; i <= bry-1; i++) {
                Point2D.Double posLeft = FractalUtil.ScreenToWorld(tlx, i, options);
                Point2D.Double posRight = FractalUtil.ScreenToWorld(brx, i, options);

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

/* int width = tlx-brx;
        boolean[] topLine = new boolean[width];
        boolean[] bottomLine = new boolean[width];
        for(int i = 0; i < width; i++) {
            int temp = tlx+i;
            Point2D.Double posTop = FractalUtil.ScreenToWorld(temp, tly, options);
            Point2D.Double posBottom = FractalUtil.ScreenToWorld(temp, bry, options);

            topLine[temp] = FractalUtil.PointConverges(posTop.x, posTop.y, maxIterations);
            bottomLine[temp] = FractalUtil.PointConverges(posBottom.x, posBottom.y, maxIterations);
        }

        // --- Makes left and right array
        int height = bry-tly;
        boolean[] leftLine = new boolean[height];
        boolean[] rightLine = new boolean[height];
        for(int i = 0; i < height; i++) {
            int temp = tly+i;
            Point2D.Double posLeft = FractalUtil.ScreenToWorld(tlx, temp, options);
            Point2D.Double posRight = FractalUtil.ScreenToWorld(brx, temp, options);

            leftLine[temp] = FractalUtil.PointConverges(posLeft.x, posLeft.y, maxIterations);
            rightLine[temp] = FractalUtil.PointConverges(posRight.x, posRight.y, maxIterations);
        } */

/*
private void CheckRectangle(IntPoint topLeft, IntPoint bottomRight, SceneSettings options, int maxIterations, ColorScheme colorScheme) {
        int tlx = topLeft.x,  tly = topLeft.y, brx = bottomRight.x, bry = bottomRight.y;

        if (brx - tlx <= 1 && bry - tly <= 1) {
            Point2D.Double pos = FractalUtil.ScreenToWorld(tlx, tly, options);
            image.setRGB(tlx, tly, colorScheme.getColor(FractalUtil.EscapeTime(pos.x, pos.y, maxIterations), maxIterations));
            return;
        }

        // ----- Stops code when straight line is reached. And colors them appropriately -----
        if (tlx == brx) {
            for(int i = tly; i <= bry; i++) {
                Point2D.Double pos = FractalUtil.ScreenToWorld(tlx, i, options);
                image.setRGB(tlx, i, colorScheme.getColor(FractalUtil.EscapeTime(pos.x, pos.y, maxIterations), maxIterations));
            }
            return;
        } else if (tly == bry) {
            for(int i = tlx; i <= brx; i++) {
                Point2D.Double pos = FractalUtil.ScreenToWorld(i, tly, options);
                image.setRGB(i, tly, colorScheme.getColor(FractalUtil.EscapeTime(pos.x, pos.y, maxIterations), maxIterations));
            }
            return;
        }

        // ----- Check if all points converges -----
        boolean allConverges = true;
        int width = brx-tlx, height = bry-tly;

        // --- Check top and bottem line ---
        for(int i = 0; i < width; i++) {
            int temp = tlx+i;
            Point2D.Double posTop = FractalUtil.ScreenToWorld(temp, tly, options);
            Point2D.Double posBottom = FractalUtil.ScreenToWorld(temp, bry, options);

            if(!FractalUtil.PointConverges(posTop.x, posTop.y, maxIterations)) allConverges = false;
            else if(!FractalUtil.PointConverges(posBottom.x, posBottom.y, maxIterations))  allConverges = false;
        }

        // --- Check Left and Right line ---
        for(int i = 0; i < height; i++) {
            int temp = tly+i;
            Point2D.Double posLeft = FractalUtil.ScreenToWorld(tlx, temp, options);
            Point2D.Double posRight = FractalUtil.ScreenToWorld(brx, temp, options);

            if(!FractalUtil.PointConverges(posLeft.x, posLeft.y, maxIterations)) allConverges = false;
            else if(!FractalUtil.PointConverges(posRight.x, posRight.y, maxIterations)) allConverges = false;
        }

        if(allConverges){
            for(int i = 0; i < width; i++) {
                for(int j = 0; j < height; j++) {
                    image.setRGB(tlx+i, tly+j, 255);
                }
            }
        } else {
            int newX = (brx+tlx)/2;
            int newY = (bry+tly)/2;
            IntPoint middle = new IntPoint(newX, newY);

            CheckRectangle(topLeft, middle, options, maxIterations, colorScheme);
            CheckRectangle(new IntPoint(newX+1, tly), new IntPoint(brx, newY), options, maxIterations, colorScheme);
            CheckRectangle(new IntPoint(tlx, newY+1), new IntPoint(newX, bry), options, maxIterations, colorScheme);
            CheckRectangle(new IntPoint(newX+1, newY+1), bottomRight, options, maxIterations, colorScheme);
        }

    }
 */

