package com.informatics.lehigh.cardboardarlibrary;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import es.ava.aruco.CameraParameters;
import es.ava.aruco.Marker;
import es.ava.aruco.Utils;
import es.ava.aruco.exceptions.ExtParamException;
import min3d.core.Object3d;
import min3d.core.Object3dContainer;

import com.informatics.lehigh.cardboardarlibrary.CubeDetector;
import com.informatics.lehigh.cardboardarlibrary.CubeConfiguration;

/**
 * Created by Josiah Smith on 6/30/2016.
 * Class representing a cube consisting of 6 different markers
 * Cubes have some benefits over a single marker:
 * -Increased Rotation and Translation Accuracy
 * -More Consistent detection
 * -360 degrees of detection
 */
public class Cube extends Vector<Marker> {
    //fields
    protected CubeConfiguration conf;
    protected Mat Rvec, Tvec;
    protected float markerSizeMeters;
    protected float paddingSizeMeters;
    private Object3dContainer object;

    // constructor

    /**
     * Cube Constructor
     */
    public Cube() {
        Rvec = new Mat(3,1, CvType.CV_64FC1);
        Tvec = new Mat(3,1,CvType.CV_64FC1);
        markerSizeMeters = -1;
        paddingSizeMeters = -1;
    }

    // other methods (UNTESTED)

    /**
     * Create a Mat of the cube image
     * @param markerSizeMeters size of each marker
     * @param paddingSizeMeters the size of the padding of each marker
     * @param conf  The configuration of the cube
     * @return Mat of the cube image
     */
    public Mat createCubeImage(double markerSizeMeters, double paddingSizeMeters, CubeConfiguration conf) {

        //Add Padding Size to Markers
        markerSizeMeters += paddingSizeMeters;

        //Truncate double to 3 decimal places and convert to mm
        int markerSize = (int) Math.floor(markerSizeMeters * 1000);

        //Create cubeImage Mat
        int sizeY = markerSize * 3;
        int sizeX = markerSize * 4;
        Mat cubeImage = new Mat(sizeY,sizeX,CvType.CV_8UC1);
        cubeImage.setTo(new Scalar(255));

        //Put each marker in the correct place in the cubeImage
        HashMap<Integer,Integer> cubeLayout = conf.getCubeLayout();
        for (Map.Entry<Integer, Integer> entry : cubeLayout.entrySet()) {
            if (entry.getValue() == 0) {
                Mat subRect = cubeImage.submat(2 * markerSize, 3 * markerSize, 0, markerSize);
                Mat marker = Marker.createMarkerImage(entry.getKey(), markerSize);
                marker.copyTo(subRect);
            } else if (entry.getValue() == 1) {
                Mat subRect = cubeImage.submat(0, markerSize, markerSize, 2 * markerSize);
                Mat marker = Marker.createMarkerImage(entry.getKey(), markerSize);
                marker.copyTo(subRect);
            } else if (entry.getValue() == 2) {
                Mat subRect = cubeImage.submat(markerSize, 2 * markerSize, markerSize, 2 * markerSize);
                Mat marker = Marker.createMarkerImage(entry.getKey(), markerSize);
                marker.copyTo(subRect);
            } else if (entry.getValue() == 3) {
                Mat subRect = cubeImage.submat(2 * markerSize, 3 * markerSize, markerSize, 2 * markerSize);
                Mat marker = Marker.createMarkerImage(entry.getKey(), markerSize);
                marker.copyTo(subRect);
            } else if (entry.getValue() == 4) {
                Mat subRect = cubeImage.submat(3 * markerSize, 4 * markerSize, markerSize, 2 * markerSize);
                Mat marker = Marker.createMarkerImage(entry.getKey(), markerSize);
                marker.copyTo(subRect);
            } else {
                Mat subRect = cubeImage.submat(2 * markerSize, 3 * markerSize, 2 * markerSize, 3 * markerSize );
                Mat marker = Marker.createMarkerImage(entry.getKey(), markerSize);
                marker.copyTo(subRect);
            }

        }
        return cubeImage;
    }

    /**
     * Overloaded method replacing CubeConfiguration param with int[] param
     * @param markerSize size of each marker
     * @param paddingSizeMeters the size of the padding of each marker
     * @param conf The configuration of the cube
     * @return
     */
    public Mat createCubeImage(double markerSize, double paddingSizeMeters, int[] conf) {
        CubeConfiguration cubeConf = new CubeConfiguration(conf);
        return createCubeImage(markerSize, paddingSizeMeters, cubeConf);
    }

    public Mat getRvec() {
        return Rvec;
    }

    public Mat getTvec() {
        return Tvec;
    }

    public void set3dObject(Object3dContainer object) throws ExtParamException {
        this.object = object;
        double[] matrix = new double[16];
        Utils.glGetModelViewMatrix(matrix,Rvec,Tvec);
        this.object.setModelViewMatrix(matrix);
    }

    public void draw3dAxis(Mat frame, CameraParameters cp, Scalar color) {
        Utils.draw3dAxis(frame, cp, color, 2*this.markerSizeMeters, Rvec, Tvec);
    }
}
