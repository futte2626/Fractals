package utilities;

import coloringmethods.ColorScheme;
import model.RenderResult;
import model.SceneSettings;
import rendermethods.FractalRenderer;

public class RenderList {
    public RenderResult currentRender;
    public int maxSize;


    public RenderList(int maxSize) {
        currentRender = null;
        this.maxSize = maxSize;
    }

    public void AddToStart(RenderResult result) {
        if (currentRender == null) {
            currentRender = result;
            return;
        }

        result.next = currentRender;
        result.prev = null;

        currentRender.prev = result;
    }

    public void AddToEnd(RenderResult result) {
        if (currentRender == null) {
            currentRender = result;
            return;
        }

        currentRender.next = result;
        result.prev = currentRender;
        result.next = null;
    }

    public boolean traverseForward() {
        if (currentRender == null || currentRender.next == null) return false;
        currentRender = currentRender.next;
        return true;
    }

    public boolean traverseBackward() {
        if (currentRender == null || currentRender.prev == null) return false;
        currentRender = currentRender.prev;
        return true;
    }

}