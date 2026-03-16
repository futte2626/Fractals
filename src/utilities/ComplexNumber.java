package utilities;

public class ComplexNumber {
    public double real;
    public double imag;

    public ComplexNumber(double real, double imag) {
        this.real = real;
        this.imag = imag;
    }

    public ComplexNumber add(ComplexNumber other) {
        return new ComplexNumber(real + other.real, imag+ other.imag);
    }

    public ComplexNumber subtract(ComplexNumber other) {
        return new ComplexNumber(real- other.real, imag - other.imag);
    }

    public ComplexNumber multiply(ComplexNumber other) {
        return new ComplexNumber(real*other.real-imag*other.imag, imag*other.real+real*other.imag);
    }

    public double getMagnitudeSquared() {
        return real*real + imag*imag;
    }

    public double getMagnitude() {
        return Math.sqrt(real*real + imag*imag);
    }
}

/*
private void CheckRectangle(IntPoint topLeft, IntPoint bottomRight, SceneSettings options, int maxIterations) {
    int tlx = topLeft.x, tly = topLeft.y;
    int brx = bottomRight.x, bry = bottomRight.y;

    // Base case: single pixel
    if (tlx == brx && tly == bry) {
        Point2D.Double pos = FractalUtil.ScreenToWorld(tlx, tly, options);
        boolean converges = FractalUtil.PointConverges(pos.x, pos.y, maxIterations);
        int color = converges ? 0xFF000000 : 0xFFFFFFFF; // black if inside, white if outside
        image.setRGB(tlx, tly, color);
        return;
    }

    // Check if all edges converge
    boolean allConverges = true;

    // Top & bottom edges
    for (int x = tlx; x <= brx; x++) {
        Point2D.Double top = FractalUtil.ScreenToWorld(x, tly, options);
        Point2D.Double bottom = FractalUtil.ScreenToWorld(x, bry, options);
        if (!FractalUtil.PointConverges(top.x, top.y, maxIterations) ||
            !FractalUtil.PointConverges(bottom.x, bottom.y, maxIterations)) {
            allConverges = false;
            break;
        }
    }

    // Left & right edges (skip corners to avoid double-checking)
    if (allConverges) {
        for (int y = tly + 1; y <= bry - 1; y++) {
            Point2D.Double left = FractalUtil.ScreenToWorld(tlx, y, options);
            Point2D.Double right = FractalUtil.ScreenToWorld(brx, y, options);
            if (!FractalUtil.PointConverges(left.x, left.y, maxIterations) ||
                !FractalUtil.PointConverges(right.x, right.y, maxIterations)) {
                allConverges = false;
                break;
            }
        }
    }

    // If all edges converge, fill the rectangle with black
    if (allConverges) {
        for (int x = tlx; x <= brx; x++) {
            for (int y = tly; y <= bry; y++) {
                image.setRGB(x, y, 0xFF000000);
            }
        }
        return;
    }

    // Subdivide rectangle without overlapping pixels
    int midX = (tlx + brx) / 2;
    int midY = (tly + bry) / 2;

    // Top-left
    CheckRectangle(new IntPoint(tlx, tly), new IntPoint(midX, midY), options, maxIterations);
    // Top-right
    if (midX < brx) CheckRectangle(new IntPoint(midX + 1, tly), new IntPoint(brx, midY), options, maxIterations);
    // Bottom-left
    if (midY < bry) CheckRectangle(new IntPoint(tlx, midY + 1), new IntPoint(midX, bry), options, maxIterations);
    // Bottom-right
    if (midX < brx && midY < bry) CheckRectangle(new IntPoint(midX + 1, midY + 1), new IntPoint(brx, bry), options, maxIterations);
}
 */