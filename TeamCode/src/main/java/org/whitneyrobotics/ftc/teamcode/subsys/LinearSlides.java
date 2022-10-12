package org.whitneyrobotics.ftc.teamcode.subsys;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.function.Consumer;
import org.whitneyrobotics.ftc.teamcode.GamepadEx.Button;
import org.whitneyrobotics.ftc.teamcode.GamepadEx.GamepadInteractionEvent;
import org.whitneyrobotics.ftc.teamcode.lib.util.Toggler;

import java.lang.Math;

//To do: Find level positions, find lower and upper bounds
//Make motor PID controller and

/**
 * Create by Gavin-kai Vida on 10/5/2022
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class LinearSlides {
    public final DcMotorEx LSleft;
    public final DcMotorEx LSright;

    private double slidesPosition = 0.0;
    public double getSlidesPosition(){return slidesPosition;}
    private double slidesPositionTarget = 0.0;
    public boolean isOnTarget(){return slidesPositionTarget == slidesPosition;}
    private double slidesVelocity = 0.0;
    public double getSlidesVelocity(){return slidesVelocity;}

    public boolean slidingInProgress = false;
    private final double GEAR_RATIO = 2;

    private enum LinearSlidesSTATE{
        LEVELED, //default, linear slides leveled control
        DRIVER_CONTROLLED; //driver custom position control
        public final static double leveled_interval = 325.33;
        public final static double driver_interval = 1.0;
    }
    private enum LinearSlidesLEVELS {
        LEVEL0(0.0),//default
        LEVEL1(325.33),
        LEVEL2(650.67),
        LEVEL3(976);
        private final double position;
        LinearSlidesLEVELS(double position){this.position = position;}
        public double getPosition(){return this.position;}
    }
    private int currentLevel;
    private LinearSlidesSTATE linearSlidesSTATE;
    private LinearSlidesLEVELS linearSlidesLEVELS;

    //Emergency Stops
    private static final double SLIDES_UPPER_BOUND = 976.0;
    private static final double SLIDES_LOWER_BOUND = 0.0;
    private int direction = 0;

    public LinearSlides(HardwareMap hardwareMap, Button inc, Button dec, Button switchState) {
        LSleft = hardwareMap.get(DcMotorEx.class, "LinearSlidesLeft");
        LSright = hardwareMap.get(DcMotorEx.class, "LinearSlidesRight");
        LSleft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        LSright.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        linearSlidesLEVELS = linearSlidesLEVELS.LEVEL0;
        linearSlidesSTATE = linearSlidesSTATE.LEVELED;
        inc.onPress((GamepadInteractionEvent callback) -> direction++);
        dec.onPress((GamepadInteractionEvent callback) -> direction--);
        inc.onRelease((GamepadInteractionEvent callback) -> direction--);
        dec.onRelease((GamepadInteractionEvent callback) -> direction++);
        resetEncoder();
    }
    public void resetEncoder() {
        LSleft.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        LSleft.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        LSright.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        LSright.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }


    private void moveToTarget() {
        //moving position to position target
    }

    public void changeState(LinearSlidesSTATE state) {
        linearSlidesSTATE = state;
    }

    private int lastDirection = 0;
    public void operate() {
        switch (linearSlidesSTATE) {
            case LEVELED:
                //direction = Math.signum(direction);
                if (direction != lastDirection) {
                    slidesPositionTarget += linearSlidesSTATE.leveled_interval*direction;
                    linearSlidesLEVELS = linearSlidesLEVELS.values()[linearSlidesLEVELS.ordinal()+(1*direction)];
                    lastDirection = (int)direction;
                }
                moveToTarget();
                break;
            case DRIVER_CONTROLLED:
                slidesPositionTarget += linearSlidesSTATE.driver_interval*direction;
                moveToTarget();
                break;
        }
    }

}
