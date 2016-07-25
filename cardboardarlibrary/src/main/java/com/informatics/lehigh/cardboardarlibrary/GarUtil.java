package com.informatics.lehigh.cardboardarlibrary;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;
import android.content.res.Resources;

import com.google.vr.sdk.base.HeadTransform;

import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class GarUtil {

    private final String TAG = "GarUtil";
    private Resources mRes;

    /**
     * Utility object for various OpenGL ES common tasks.
     * @param res The Resources object for app package - use android.content.Context.getResources()
     */
    public GarUtil(Resources res) {
        mRes = res;
    }

    /**
     * Converts a raw text file, saved as a resource, into an OpenGL ES shader.
     *
     * @param type The type of shader we will be creating.
     * @param resId The resource ID of the raw text file about to be turned into a shader.
     * @return The shader object handler.
     */
    public int loadGLShader(int type, int resId) {
        String code = readRawTextFile(resId);
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, code);
        GLES20.glCompileShader(shader);

        // Get the compilation status.
        final int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

        // If the compilation failed, delete the shader.
        if (compileStatus[0] == 0) {
            Log.e(TAG, "Error compiling shader: " + GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            shader = 0;
        }

        if (shader == 0) {
            throw new RuntimeException("Error creating shader.");
        }

        return shader;
    }

    /**
     * Checks if we've had an error inside of OpenGL ES, and if so what that error is.
     *
     * @param label Label to report in case of error.
     */
    public void checkGLError(String label) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, label + ": glError " + error);
            throw new RuntimeException(label + ": glError " + error);
        }
    }

    /**
     * Converts a raw text file into a string.
     *
     * @param resId The resource ID of the raw text file about to be turned into a shader.
     * @return The context of the text file, or null in case of error.
     */
    private String readRawTextFile(int resId) {
        InputStream inputStream = mRes.openRawResource(resId);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Calculates a column-major transformation matrix that will transform a vector in the basis
     * formed by the Google cardboard (up, right, and forward vector from the HeadTransform)
     * to the world system (right-handed for OpenGL). I.e., given the returned matrix M, and a vector b in cardboard coordinates,
     * then a = M * b where a is in world coordinates.
     * @param M The 4x4 matrix to place the transformation in.
     * @param headTransform The head transform to form the transformation from
     */
    public static void getCardboardToWorldTransform(float[] M, HeadTransform headTransform) {
        if (M.length != 16) {
            throw new IllegalArgumentException("Destination matrix array must be 4x4 (length 16)!");
        }

        float[] forwardVec = new float[3];
        float[] upVec = new float[3];
        float[] rightVec = new float[3];
        headTransform.getForwardVector(forwardVec, 0);
        headTransform.getUpVector(upVec, 0);
        headTransform.getRightVector(rightVec, 0);

        // transform to world coordinates from camera
        // create m in column major order
        // invert forward vec because in opengl -z is forward
        float[] m = new float[] {
                rightVec[0], upVec[0], -forwardVec[0], 0.0f,
                rightVec[1], upVec[1], -forwardVec[1], 0.0f,
                rightVec[2], upVec[2], -forwardVec[2], 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f
        };
        // a = m^T * b where b is vector representation in camera (cardboard) basis
        Matrix.transposeM(M, 0, m, 0);
    }

    /**
     * Calculates a column-major translation matrix from the given
     * OpenCV tvec. This method transforms the translation into the
     * world coordinate system to valid OpenGL coordinates using the given HeadTransform, which could
     * be passed directly into a shader to translate an object to the
     * marker that produced the tvec.
     * @param transMat The 4x4 matrix to place the translation in.
     * @param tvec OpenCV tvec.
     */
    public static void getTranslationMatFromTvec(float[] transMat, Mat tvec, HeadTransform headTransform) {
        if (transMat.length != 16) {
            throw new IllegalArgumentException("Destination matrix array must be 4x4 (length 16)!");
        }

        float[] tvecCam = new float[4];
        tvecToCardboardCoords(tvecCam, tvec);

        // transform to world coords
        float[] mt = new float[16];
        getCardboardToWorldTransform(mt, headTransform);
        float tvecWorld[] = new float[4];
        Matrix.multiplyMV(tvecWorld, 0, mt, 0, tvecCam, 0);

        // Build translation matrix
        Matrix.setIdentityM(transMat, 0);
        Matrix.translateM(transMat, 0, tvecWorld[0], tvecWorld[1], tvecWorld[2]);
    }

    /**
     * Changes a tvec produced from OpenCV to be in a right handed OpenGL-compatible
     * coordinate system with the same origin as OpenCV (so the cardboard coordinate system). This simply negates
     * the y and z of the OpenCV tvec.
     * @param cardTvec The 4 element array to place the new translation vector in. glTvec[3] is just 1.0
     * @param tvec The OpenCV tvec to use.
     */
    public static void tvecToCardboardCoords(float[] cardTvec, Mat tvec) {
        if (cardTvec.length != 4) {
            throw new IllegalArgumentException("Destination array must be a 4-vector!");
        }

        // negate y and z for opengl coordinates
        cardTvec[0] = (float) tvec.get(0, 0)[0];
        cardTvec[1] = -(float) tvec.get(1, 0)[0];
        cardTvec[2] = -(float) tvec.get(2, 0)[0];
        cardTvec[3] = 1.0f;
    }

    /**
     * Calculates a column-major rotation matrix from the given
     * OpenCV rvec. This method transforms the rotation into the
     * world coordinate system using the given HeadTransform to valid OpenGL coordinates, which could
     * be passed directly into a shader to rotate an object to the
     * the pose estimated by the marker that produced the rvec.
     * @param rotMat The 4x4 matrix to place the rotation in.
     * @param rvec OpenCV rvec.
     * @param headTransform The HeadTransform for the current frame.
     */
    public static void getRotationMatFromRvec(float[] rotMat, Mat rvec, HeadTransform headTransform) {
        if (rotMat.length != 16) {
            throw new IllegalArgumentException("Destination matrix array must be 4x4 (length 16)!");
        }

        // must use rvec to find rotation matrix
        // transform rotation axis to world space
        float angleRad = (float) Core.norm(rvec, Core.NORM_L2);
        float angle = (float)Math.toDegrees(angleRad);
        float rvecCam[] = {(float)rvec.get(0, 0)[0] / angleRad, -1.0f * (float)rvec.get(1, 0)[0] / angleRad, -1.0f * (float)rvec.get(2, 0)[0] / angleRad, 0.0f};
        float rvecArr[] = new float[4];
        float[] mt = new float[16];
        getCardboardToWorldTransform(mt, headTransform);
        Matrix.multiplyMV(rvecArr, 0, mt, 0, rvecCam, 0);

        // Build rotation matrix
        Matrix.setIdentityM(rotMat, 0);
        Matrix.rotateM(rotMat, 0, angle, rvecArr[0], rvecArr[1], rvecArr[2]);
    }

    /**
     * Takes in an OpenCV tvec and rvec, and returns a transformation matrix to place
     * an object defined in the Cardboard coordinate system (defined by up, right, and forward vectors from the HeadTransform)
     * in the pose and location defined by the tvec and rvec. <br></br><br></br>
     * <b>Note: this method is recommended for drawing augmented objects in the scene.</b>
     * @param transMat The 4x4 matrix to place the transformation in.
     * @param tvec The OpenCV tvec to use.
     * @param rvec The OpenCV rvec to use.
     * @param headTransform The head transformation from the same frame as tvec and rvec.
     */
    public static void getTransformationFromTrackingParams(float[] transMat, Mat tvec, Mat rvec, HeadTransform headTransform) {
        if (transMat.length != 16) {
            throw new IllegalArgumentException("Destination matrix array must be 4x4 (length 16)!");
        }

        // Get the rotation matrix
        float[] rot = new float[16];
        getRotationMatFromRvec(rot, rvec, headTransform);

        // Turn tvec into a cardboard basis coordinate system
        float[] tvecCam = new float[4];
        tvecToCardboardCoords(tvecCam, tvec);

        // adjust z so not too close to camera
        // update x and y accordingly
        // must move away in cardboard -z as well as properly scale so this translation isn't noticed
        float xRatio = tvecCam[0] / Math.abs(tvecCam[2]);
        float yRatio = tvecCam[1] / Math.abs(tvecCam[2]);
        float scaleRatio = 1.0f / Math.abs(tvecCam[2]);
        float distToMoveZ = Math.abs(Math.abs(tvecCam[2]) - Math.abs(StereoScreenRenderer.SCREEN_DEPTH));
        float adjustZ = (StereoScreenRenderer.SCREEN_DEPTH <= tvecCam[2] ? -distToMoveZ : distToMoveZ); // actually moves to the initial Z position
        float scaleFact = 1.0f - adjustZ * scaleRatio;
        float adjustX = -adjustZ * xRatio;
        float adjustY = -adjustZ * yRatio;
        tvecCam[0] += adjustX;
        tvecCam[1] += adjustY;
        tvecCam[2] += adjustZ;
        // build the scale matrix
        float[] scale = new float[16];
        Matrix.setIdentityM(scale, 0);
        Matrix.scaleM(scale, 0, scaleFact, scaleFact, scaleFact);

        // transform to world coords
        float[] mt = new float[16];
        getCardboardToWorldTransform(mt, headTransform);
        float tvecWorld[] = new float[4];
        Matrix.multiplyMV(tvecWorld, 0, mt, 0, tvecCam, 0);
        // Build translation matrix
        float trans[] = new float[16];
        Matrix.setIdentityM(trans, 0);
        Matrix.translateM(trans, 0, tvecWorld[0], tvecWorld[1], tvecWorld[2]);

        // multiply together, we want v' = TSRM^T v
        float[] basisChangeRot = new float[16];
        float[] addScale = new float[16];
        Matrix.multiplyMM(basisChangeRot, 0, rot, 0, mt, 0);
        Matrix.multiplyMM(addScale, 0, scale, 0, basisChangeRot, 0);

        Matrix.setIdentityM(transMat, 0);
        Matrix.multiplyMM(transMat, 0, trans, 0, addScale, 0);
    }


}
