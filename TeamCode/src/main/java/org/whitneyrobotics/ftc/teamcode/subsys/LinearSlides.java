package org.whitneyrobotics.ftc.teamcode.subsys;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

//To do: Find level positions, find lower and upper bounds

/**
 * Create by Gavin-kai Vida on 10/5/2022
 */
public class LinearSlides {
    public final DcMotorEx LSleft;
    public final DcMotorEx LSright;
    private enum LinearSlidesSTATE{
        LEVELED, //default, linear slides leveled control
        DRIVER_CONTROLLED, //driver custom position control
    }
    private enum LinearSlidesLEVELS {
        LEVEL0(0.0), //default
        LEVEL1(1.0),
        LEVEL2(2.0),
        LEVEL3(3.0);
        private double position;
        LinearSlidesLEVELS(double position){this.position = position;}
        public double getPosition(){return this.position;}
    }
    private LinearSlidesSTATE linearSlidesSTATE;
    private LinearSlidesLEVELS linearSlidesLEVELS;
    private double slidesPosition;
    public double getSlidesPosition(){return slidesPosition;}
    private double slidesVelocity;
    public boolean slidingInProgress = false;

    //Emergency Stops
    private static final double SLIDES_UPPER_BOUND = 10.0;
    private static final double SLIDES_LOWER_BOUND = 0.0;

    public LinearSlides(HardwareMap hardwareMap) {
        LSleft = hardwareMap.get(DcMotorEx.class, "LinearSlidesLeft");
        LSright = hardwareMap.get(DcMotorEx.class, "LinearSlidesRight");
        LSleft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        LSright.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        linearSlidesSTATE = linearSlidesSTATE.LEVELED;
        linearSlidesLEVELS = linearSlidesLEVELS.LEVEL0;
        resetEncoder();
    }
    public void resetEncoder() {
        LSleft.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        LSleft.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        LSright.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        LSright.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void updateState(LinearSlidesSTATE state) {
        linearSlidesSTATE = state;
    }
    public void updateLEVEL(LinearSlidesLEVELS level) {
        linearSlidesLEVELS = level;
    }

    void operate() {
        switch (linearSlidesSTATE) {
            case LEVELED:
                System.out.println("we leveled boi");
                break;
            case DRIVER_CONTROLLED:
                System.out.println("we driver controlled boi");
                break;
        }
    }

}
