package org.whitneyrobotics.ftc.teamcode.autoop;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
;import org.whitneyrobotics.ftc.teamcode.lib.util.Vector3;


//To do: Conversions of ticks to inches
/**
 * Created by Gavin-kai Vida on 12/9/2022
 */
public class WHSOdometry {
    private DcMotorEx leftWheel, rightWheel, centerWheel;
    private Vector3 currentPos;

    private static final double TRACK_WIDTH = 10; //inches
    private static final double TRACK_LENGTH = (10)/2; //inches

    public WHSOdometry (HardwareMap hardwareMap, double initX, double initY) {
        leftWheel = hardwareMap.get(DcMotorEx.class, "leftOdometry");
        rightWheel = hardwareMap.get(DcMotorEx.class, "rightOdometry");
        centerWheel = hardwareMap.get(DcMotorEx.class, "centerOdometry");
        currentPos = new Vector3(initX,initY,0);
    }

    public Vector3 getCurrentPos() {
        currentPos.y = getDeltaY();
        currentPos.x = getDeltaX();
        return currentPos;

    }
    private double getDeltaY () {
        return (leftWheel.getCurrentPosition()+rightWheel.getCurrentPosition())/2;
    }
    private double getDeltaX () {
        return centerWheel.getCurrentPosition()-TRACK_LENGTH*((leftWheel.getCurrentPosition()-rightWheel.getCurrentPosition())/2)
    }
    public double getCurrentRotation() {
        return (rightWheel.getCurrentPosition()-leftWheel.getCurrentPosition())/2;
    }





}
