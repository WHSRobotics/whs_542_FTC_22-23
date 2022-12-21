package org.whitneyrobotics.ftc.teamcode.autoop;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.whitneyrobotics.ftc.teamcode.lib.util.Vector3;


//To do: Conversions of ticks to inches
/**
 * Created by Gavin-kai Vida on 12/9/2022
 */
public class WHSOdometry {
    private DcMotorEx leftWheel, rightWheel, centerWheel;
    private Vector3 lastMeasurements = new Vector3(0,0,0);//encoders reset on init
    private Vector3 currentPos;
    public Vector3 getCurrentPos() {
        return currentPos;
    }


    private double TRACK_WIDTH; //inches
    private double TRACK_LENGTH; //inches

    public WHSOdometry (HardwareMap hardwareMap, double initX, double initY, double initTheta, int width, int length) {
        leftWheel = hardwareMap.get(DcMotorEx.class, "leftOdometry");
        rightWheel = hardwareMap.get(DcMotorEx.class, "rightOdometry");
        centerWheel = hardwareMap.get(DcMotorEx.class, "centerOdometry");
        leftWheel.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        rightWheel.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        centerWheel.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        currentPos = new Vector3(initX,initY,initTheta);
        // ^^^ ASSUME BOTTOM RIGHT CORNER FROM AUDIENCE POV IS (0,0), ANGLES MEASURED IN RADIANS, COORDS MEASURED IN INCHES ^^^
        TRACK_WIDTH = width;
        TRACK_LENGTH = length;
    }

    public void updatePos() {
        //wheel changes since last measurement
        Vector3 wheelDeltas = new Vector3( leftWheel.getCurrentPosition()-lastMeasurements.x, // left delta
                rightWheel.getCurrentPosition()-lastMeasurements.y, // right delta
                centerWheel.getCurrentPosition()-lastMeasurements.z); // center delta
        lastMeasurements = new Vector3(leftWheel.getCurrentPosition(),rightWheel.getCurrentPosition(),centerWheel.getCurrentPosition());

        //local coordinate changes
        double localDeltaY = (wheelDeltas.y+wheelDeltas.x)/2; // local change in y (robot coordinate plane)
        double localDeltaTheta = (wheelDeltas.y-wheelDeltas.x)/TRACK_LENGTH; // local change in angle
        double localDeltaX = wheelDeltas.z-(TRACK_WIDTH*(localDeltaTheta)); // local change in x

        //conversion to global coordinate plane
        currentPos.x += (localDeltaX*Math.cos(currentPos.z))-(localDeltaY*Math.sin(currentPos.z));
        currentPos.y += (localDeltaY*Math.cos(currentPos.z))+(localDeltaY*Math.sin(currentPos.z));
        currentPos.z += localDeltaTheta;
    }


}
