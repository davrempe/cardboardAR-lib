package com.informatics.lehigh.cardboardarlibrary;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.util.Log;

import com.google.vr.sdk.base.GvrView;
import com.google.vr.sdk.base.HeadTransform;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Renderer for drawing the camera feed on a GL texture to a normal
 * Texture2D.
 */
public class CameraTextureRenderer implements GLRenderer {

    //
    // CONSTANTS
    //
    /** Number of coordinates per screen vertex */
    private static final int COORDS_PER_VERTEX = 3;
    /** Number of bytes in a float */
    private static final int BYTES_PER_FLOAT = 4;
    /** Vertices making up screen (just a plane of 2 triangles that fills screen) */
    private final float[] SCREEN_COORDS = new float[] {
            -1.0f, 1.0f, 0.0f,
            -1.0f, -1.0f, 0.0f,
            1.0f, 1.0f, 0.0f,
            -1.0f, -1.0f, 0.0f,
            1.0f, -1.0f, 0.0f,
            1.0f, 1.0f, 0.0f
    };
    /** Texture coordinates for the screen plane */
    private static final float[] SCREEN_TEX_COORDS = new float [] {
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
    };

    //
    // OpenGL-related members
    //
    /** Buffer for screen vertices */
    private FloatBuffer mScreenVertBuf;
    /** Buffer for screen texture coordinates */
    private FloatBuffer mScreenTexBuf;
    /** Program using screen shaders */
    private int mCameraTexProgram;
    /** Attribute location for screen position */
    private int mScreenPositionParam;
    /** Attribute location for screen texture */
    private int mScreenTextureParam;
    /** The surface texture used to update the camera feed */
    private SurfaceTexture mSurfaceTexture;
    /** ID of screen texture that holds camera feed */
    private int mScreenTextureID;
    /** GarUtil instance */
    private GarUtil garutil;

    public CameraTextureRenderer(Activity activity) {
        garutil = new GarUtil(activity.getResources());
    }

    /**
     * Initialize all GL elements of the renderer like buffers, textures,
     * and shaders. Should be called from
     * {@link GvrView.StereoRenderer#onSurfaceCreated onSurfaceCreated()}.
     */
    @Override
    public void init() {
        // make buffer for screen vertices
        ByteBuffer bbScreenVertices = ByteBuffer.allocateDirect(SCREEN_COORDS.length * BYTES_PER_FLOAT);
        bbScreenVertices.order(ByteOrder.nativeOrder());
        mScreenVertBuf = bbScreenVertices.asFloatBuffer();
        mScreenVertBuf.put(SCREEN_COORDS);
        mScreenVertBuf.position(0);
        // make buffer for screen texture coordinates
        ByteBuffer bbScreenTex = ByteBuffer.allocateDirect(SCREEN_TEX_COORDS.length * BYTES_PER_FLOAT);
        bbScreenTex.order(ByteOrder.nativeOrder());
        mScreenTexBuf = bbScreenTex.asFloatBuffer();
        mScreenTexBuf.put(SCREEN_TEX_COORDS);
        mScreenTexBuf.position(0);

        // make camera texture
        int[] textures = new int[1];
        // Generate the texture to where android view will be rendered
        GLES20.glGenTextures(1, textures, 0);
        garutil.checkGLError("Texture generate");
        mScreenTextureID = textures[0];

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mScreenTextureID);
        garutil.checkGLError("Texture bind");

        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MIN_FILTER,GL10.GL_NEAREST);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

        int vertexShader = garutil.loadGLShader(GLES20.GL_VERTEX_SHADER, R.raw.cameratex_vert);
        int fragmentShader = garutil.loadGLShader(GLES20.GL_FRAGMENT_SHADER, R.raw.cameratex_frag);

        mCameraTexProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mCameraTexProgram, vertexShader);
        GLES20.glAttachShader(mCameraTexProgram, fragmentShader);

        mScreenPositionParam = 0;
        GLES20.glBindAttribLocation(mCameraTexProgram, mScreenPositionParam, "a_Position");
        Log.d("position", String.valueOf(mScreenPositionParam));
        mScreenTextureParam = 1;
        GLES20.glBindAttribLocation(mCameraTexProgram, mScreenTextureParam, "a_TexCoordinate");
        Log.d("texture", String.valueOf(mScreenTextureParam));

        GLES20.glLinkProgram(mCameraTexProgram);
        GLES20.glUseProgram(mCameraTexProgram);

        garutil.checkGLError("Camera to texture program");

    }

    /**
     * Updates the GL elements of the renderer like vertex buffers
     * and model matrices. These things are independent of any single view
     * and is meant to be called once per frame. Should be called from
     * {@link GvrView.StereoRenderer#onNewFrame onNewFrame()}.
     *
     * @param headTransform Contains the up, right, and forward vectors of the
     *                      cardboard viewer needed for calculations.
     */
    @Override
    public void update(HeadTransform headTransform) {
        // Nothing to update
        return;
    }

    /**
     * Draws the GL elements defined by this renderer. This should be called from
     * {@link GvrView.StereoRenderer#onDrawEye onDrawEye()}.
     *
     * @param view        The 4x4 view matrix to use for rendering.
     * @param perspective The 4x4 projection matrix to user for rendering.
     */
    @Override
    public void draw(float[] view, float[] perspective) {
        GLES20.glUseProgram(mCameraTexProgram);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mScreenTextureID);
        garutil.checkGLError("binding uniform texture");

        // update the camera surface texture with the new image
        mSurfaceTexture.updateTexImage();

        // Set the position of the screen
        GLES20.glVertexAttribPointer(mScreenPositionParam, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, mScreenVertBuf);
        garutil.checkGLError("set screen pos pointer");

        // Set the texture coords for the screen
        GLES20.glVertexAttribPointer(mScreenTextureParam, 2, GLES20.GL_FLOAT, false, 0, mScreenTexBuf);
        garutil.checkGLError("setting texture attribute pointers");

        // Enable vertex arrays
        GLES20.glEnableVertexAttribArray(mScreenPositionParam);
        GLES20.glEnableVertexAttribArray(mScreenTextureParam);

        // actually draw
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
        garutil.checkGLError("Drawing camera texture");

        // free texture
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
    }

    /**
     * @return the ID of the texture that the camera feed should be drawn to using
     * a SurfaceTexture.
     */
    public int getCameraTexture() {
        return mScreenTextureID;
    }

    /**
     * Sets the surface texture to update in order to get the most recent camera fram
     * while rendering.
     * @param surfaceTexture
     */
    public void setCameraSurfaceTexture(SurfaceTexture surfaceTexture) {
        mSurfaceTexture = surfaceTexture;
    }
}
