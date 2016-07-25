package com.informatics.lehigh.cardboardarlibrary;

import com.google.vr.sdk.base.HeadTransform;

/**
 * Basic interface describing a class that sets up, updates,
 * and executes rendering of an object for the Google Cardboard using
 * OpenGL. Classes that implement this interface are usually called on
 * by activities that implement
 * {@link com.google.vr.sdk.base.GvrView.StereoRenderer GvrView.StereoRenderer}.
 *
 */
public interface GLRenderer {

    /**
     * Initialize all GL elements of the renderer like buffers, textures,
     * and shaders. Should be called from
     * {@link com.google.vr.sdk.base.GvrView.StereoRenderer#onSurfaceCreated onSurfaceCreated()}.
     */
    public void init();

    /**
     * Updates the GL elements of the renderer like vertex buffers
     * and model matrices. These things are independent of any single view
     * and is meant to be called once per frame. Should be called from
     * {@link com.google.vr.sdk.base.GvrView.StereoRenderer#onNewFrame onNewFrame()}.
     * @param headTransform Contains the up, right, and forward vectors of the
     *                      cardboard viewer needed for calculations.
     */
    public void update(HeadTransform headTransform);

    /**
     * Draws the GL elements defined by this renderer. This should be called from
     * {@link com.google.vr.sdk.base.GvrView.StereoRenderer#onDrawEye onDrawEye()}.
     * @param view The 4x4 view matrix to use for rendering.
     * @param perspective The 4x4 projection matrix to user for rendering.
     */
    public void draw(float[] view, float[] perspective);
}
