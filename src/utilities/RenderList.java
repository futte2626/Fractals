package utilities;

import coloringmethods.ColorScheme;
import model.RenderResult;
import model.SceneSettings;
import rendermethods.FractalRenderer;

public class RenderList {
    public RenderResult currentRender;
    public int maxSize;
    public int currentSize;


    public RenderList(int maxSize) {
        currentRender = null;
        currentSize = 0;
        this.maxSize = maxSize;
    }

    public void AddToStart(RenderResult result) {
        currentSize++;

        if (currentRender == null) {
            currentRender = result;
            return;
        }

        if(currentSize >= maxSize) RemoveAtEnd();

        result.next = currentRender;
        result.prev = null;

        currentRender.prev = result;
    }

    public void AddToEnd(RenderResult result) {
        currentSize++;

        if (currentRender == null) {
            currentRender = result;
            return;
        }

        if(currentSize >= maxSize) RemoveAtStart();

        currentRender.next = result;
        result.prev = currentRender;
        result.next = null;
    }

    public void RemoveAtEnd() {
        currentSize--;
        RenderResult temp = currentRender;
        if(temp.next == null) {return;}
        while (temp.next.next != null) {
            temp = temp.next;
        }

        temp.next.prev = null;
        temp.next = null;
    }

    public void RemoveAtStart() {
        currentSize--;
        RenderResult temp = currentRender;
        if(temp.prev == null) {return;}
        while (temp.prev.prev != null) {
            temp = temp.prev;
        }

        temp.prev.next = null;
        temp.prev = null;
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