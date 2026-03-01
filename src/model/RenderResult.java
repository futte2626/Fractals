package model;

import java.awt.image.BufferedImage;

public class RenderResult {
    public BufferedImage image;
    public long frameTime;

    public RenderResult(BufferedImage image, long frameTime) {
        this.image = image;
        this.frameTime = frameTime;
    }
}
