package org.whitneyrobotics.ftc.teamcode.subsys;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.whitneyrobotics.ftc.teamcode.lib.util.Toggler;

import java.lang.Math;

//To do: Find level positions, find lower and upper bounds
//Make motor PID controller and

/**
 * Create by Gavin-kai Vida on 10/5/2022
 */
public class LinearSlides {
    public final DcMotorEx LSleft;
    public final DcMotorEx LSright;

    private double slidesPosition = 0.0;
    public double getSlidesPosition(){return slidesPosition;}
    private double slidesPositionTarget = 0.0;
    private double slidesVelocity = 0.0;
    public double getSlidesVelocity(){return slidesVelocity;}

    public boolean slidingInProgress = false;
    private final double GEAR_RATIO = 2;

    private enum LinearSlidesSTATE{
        LEVELED, //default, linear slides leveled control
        DRIVER_CONTROLLED, //driver custom position control
        HOLD
    }
    private enum LinearSlidesLEVELS {
        LEVEL0(0.0), //default
        LEVEL1(325.33),
        LEVEL2(650.67),
        LEVEL3(976);
        public final static double interval = 325.33; //how far apart each level is
        private final double position;
        LinearSlidesLEVELS(double position){this.position = position;}
        public double getPosition(){return this.position;}
    }
    private LinearSlidesSTATE linearSlidesSTATE;
    private LinearSlidesLEVELS linearSlidesLEVELS;

    //Emergency Stops
    private static final double SLIDES_UPPER_BOUND = 976.0;
    private static final double SLIDES_LOWER_BOUND = 0.0;

    public LinearSlides(HardwareMap hardwareMap) {
        LSleft = hardwareMap.get(DcMotorEx.class, "LinearSlidesLeft");
        LSright = hardwareMap.get(DcMotorEx.class, "LinearSlidesRight");
        LSleft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        LSright.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        linearSlidesSTATE = linearSlidesSTATE.HOLD;
        linearSlidesLEVELS = linearSlidesLEVELS.LEVEL0;
        resetEncoder();
    }
    public void resetEncoder() {
        LSleft.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        LSleft.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        LSright.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        LSright.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void changeState(LinearSlidesSTATE state) {
        linearSlidesSTATE = state;
    }

    private int lastDirection = 0;
    public void operate(double direction) {
        switch (linearSlidesSTATE) {
            case LEVELED:
                direction = Math.signum(direction);
                if (direction == lastDirection) {
                    direction = 0;
                }
                else if (direction == 1){
                    slidesPositionTarget = slidesPosition + linearSlidesLEVELS.interval;
                }
                else {
                    slidesPositionTarget = slidesPosition - linearSlidesLEVELS.interval;

                }
                if ((direction != 0) || slidesPositionTarget != slidesPosition) {

                }
                lastDirection = (int)direction;
                break;
            case DRIVER_CONTROLLED:
                System.out.println("we driver controlled boi");
                break;

            case HOLD:
                LSleft.setPower(0.0);
                LSright.setPower(0.0);
                break;
        }
    }

}
