package com.informatics.lehigh.cardboardarlibrary;

import org.opencv.core.Mat;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Vector;

import es.ava.aruco.CameraParameters;
import es.ava.aruco.Marker;
import es.ava.aruco.MarkerDetector;
import com.informatics.lehigh.cardboardarlibrary.Cube;
import com.informatics.lehigh.cardboardarlibrary.CubeDetector;

/**
 * Configuration of a Cube with positions of marker IDs
 * Created by Josiah Smith on 6/30/2016.
 * @author Josiah Smith
 */
public class CubeConfiguration {

    protected HashMap<Integer, Integer> cubeLayout;


    //////////////////////////////////////////////////////
    //                                                  //
    //                       ////////////               //
    //                       //        //               //
    //                       //    0   //               //
    //                       //        //               //
    //   //////////////////////////////////////////     //
    //   //        //        //        //        //     //
    //   //    1   //   2    //    3   //    4   //     //
    //   //        //        //        //        //     //
    //   //////////////////////////////////////////     //
    //                       //        //               //
    //                       //    5   //               //
    //                       //        //               //
    //                       ////////////               //
    //                                                  //
    //////////////////////////////////////////////////////
    //                                                  //
    // The ID of the marker in position 0 should be the //
    // first ID in the cubeLayout array                 //
    // The ID of the marker in position 1 should be the //
    // second ID in the cubeLayout array and so forth   //
    //                                                  //
    //////////////////////////////////////////////////////


    //Takes in an array of marker ids and creates a map of the id to the position they reside in
    public CubeConfiguration(int[] cubeLayout) throws InvalidParameterException{
        //TODO check that all ids are distinct
        this.cubeLayout = new HashMap<Integer, Integer>();

        for (int i = 0; i < cubeLayout.length; i++) {
            if (cubeLayout[i] > 1023 || cubeLayout[i] < 0) {
                throw new InvalidParameterException("IDs must range from 0-1023");
            } else {
                this.cubeLayout.put(cubeLayout[i], i);
            }

        }

    }

    public HashMap<Integer, Integer> getCubeLayout() {
        return cubeLayout;
    }
}