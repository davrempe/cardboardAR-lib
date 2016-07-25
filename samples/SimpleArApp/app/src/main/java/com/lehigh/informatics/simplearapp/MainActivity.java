package com.lehigh.informatics.simplearapp;

import android.os.Bundle;
import android.view.Surface;

import com.informatics.lehigh.cardboardarlibrary.GarActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends GarActivity {

    @Override
    protected List<Surface> setupCaptureSurfaces() {
        return new ArrayList<>();
    }


    /**
     * Draws the 3D scene to be laid over the current back-facing camera view.
     * These are the augmented portions of the application. This will be called
     * twice per frame, one for the left eye, and again for the right. Anything
     * drawn with an alpha value of 0.0 will be treated as background and filtered
     * from the final render to show the camera view in the background.
     *
     * @param view        The view matrix to use for eye being drawn.
     * @param perspective The perspective matrix to use for the eye being drawn.
     */
    @Override
    protected void drawObjects(float[] view, float[] perspective) {
        return;
    }
}
