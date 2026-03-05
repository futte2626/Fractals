package model;

import java.awt.image.BufferedImage;

public class RenderResult {
    public BufferedImage image;
    public long frameTime;
    public long totalIterationCount;

    public RenderResult(BufferedImage image, long frameTime, long totalIterationCount) {
        this.image = image;
        this.frameTime = frameTime;
        this.totalIterationCount = totalIterationCount;
    }
}
