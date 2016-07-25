package com.informatics.lehigh.cardboardarlibrary;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

/**
 * Created by Josiah Smith on 7/8/2016.
 * Contains Util methods used by other classes
 */
public class CVUtil {
    /**
     * Rotates a matrix around the X axis by the desired amount
     * @param rotation The matrix to be rotated
     * @param rotateDegrees The amount to rotate in degrees
     */
    public static void rotateXAxis(Mat rotation, double rotateDegrees) {
        // get the matrix corresponding to the rotation vector
        Mat R = new Mat(3, 3, CvType.CV_64FC1);
        Calib3d.Rodrigues(rotation, R);

        // create the matrix to rotate around the X axis
        // 1, 0, 0
        // 0 cos -sin
        // 0 sin cos
        double[] rot = {
                1, 0, 0,
                0, Math.cos(Math.toRadians(rotateDegrees)), -Math.sin(Math.toRadians(rotateDegrees)),
                0, Math.sin(Math.toRadians(rotateDegrees)), Math.cos(Math.toRadians(rotateDegrees))
        };
        // multiply both matrix
        Mat res = new Mat(3, 3, CvType.CV_64FC1);
        double[] prod = new double[9];
        double[] a = new double[9];
        R.get(0, 0, a);
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++) {
                prod[3 * i + j] = 0;
                for (int k = 0; k < 3; k++) {
                    prod[3 * i + j] += a[3 * i + k] * rot[3 * k + j];
                }
            }
        // convert the matrix to a vector with rodrigues back
        res.put(0, 0, prod);
        Calib3d.Rodrigues(res, rotation);
    }

    /**
     * Rotates a matrix around the Y axis by the desired amount
     * @param rotation The matrix to be rotated
     * @param rotateDegrees The amount to rotate in degrees
     */
    public static void rotateYAxis(Mat rotation, double rotateDegrees) {
        // get the matrix corresponding to the rotation vector
        Mat R = new Mat(3,3,CvType.CV_64FC1);
        Calib3d.Rodrigues(rotation, R);

        // create the matrix to rotate around the Y axis
        // cos 0 sin
        // 0   1  0
        //-sin 0 cos
        double[] rot = {
                Math.cos(Math.toRadians(rotateDegrees)), 0, Math.sin(Math.toRadians(rotateDegrees)),
                0,1,0,
                -Math.sin(Math.toRadians(rotateDegrees)), 0, Math.cos(Math.toRadians(rotateDegrees))
        };
        // multiply both matrix
        Mat res = new Mat(3,3, CvType.CV_64FC1);
        double[] prod = new double[9];
        double[] a = new double[9];
        R.get(0, 0, a);
        for(int i=0;i<3;i++)
            for(int j=0;j<3;j++){
                prod[3*i+j] = 0;
                for(int k=0;k<3;k++){
                    prod[3*i+j] += a[3*i+k]*rot[3*k+j];
                }
            }
        // convert the matrix to a vector with rodrigues back
        res.put(0, 0, prod);
        Calib3d.Rodrigues(res, rotation);
    }

    /**
     * Rotates a matrix around the Z axis by the desired amount
     * @param rotation The matrix to be rotated
     * @param rotateDegrees The amount to rotate in degrees
     */
    public static void rotateZAxis(Mat rotation, double rotateDegrees) {
        // get the matrix corresponding to the rotation vector
        Mat R = new Mat(3,3,CvType.CV_64FC1);
        Calib3d.Rodrigues(rotation, R);

        // create the matrix to rotate around the Z axis
        // cos -sin  0
        // sin  cos  0
        // 0    0    1
        double[] rot = {
                Math.cos(Math.toRadians(rotateDegrees)), -Math.sin(Math.toRadians(rotateDegrees)), 0,
                Math.sin(Math.toRadians(rotateDegrees)), Math.cos(Math.toRadians(rotateDegrees)), 0,
                0,0,1
        };
        // multiply both matrix
        Mat res = new Mat(3,3, CvType.CV_64FC1);
        double[] prod = new double[9];
        double[] a = new double[9];
        R.get(0, 0, a);
        for(int i=0;i<3;i++)
            for(int j=0;j<3;j++){
                prod[3*i+j] = 0;
                for(int k=0;k<3;k++){
                    prod[3*i+j] += a[3*i+k]*rot[3*k+j];
                }
            }
        // convert the matrix to a vector with rodrigues back
        res.put(0, 0, prod);
        Calib3d.Rodrigues(res, rotation);
    }
}
