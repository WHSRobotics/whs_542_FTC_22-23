package org.whitneyrobotics.ftc.teamcode.subsys;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Supplier;
import org.whitneyrobotics.ftc.teamcode.GamepadEx.Button;
import org.whitneyrobotics.ftc.teamcode.GamepadEx.GamepadInteractionEvent;
import org.whitneyrobotics.ftc.teamcode.lib.libraryProto.PIDController;

//To do: Find level positions, find lower and upper bounds
//Make motor PID controller

//Add PIDcontrolling
/**
 * Create by Gavin-kai Vida on 10/5/2022
 */
@RequiresApi(api = Build.VERSION_CODES.N) //
public class LinearSlides {
    public final DcMotorEx LSleft;
    public final DcMotorEx LSright;

    public double getSlidesPosition(){return ((LSleft.getCurrentPosition()+LSright.getCurrentPosition())/2);}
    private double slidesPositionTarget = 0.0;
    public boolean isOnTarget(){return slidesPositionTarget == ((LSleft.getCurrentPosition()+LSright.getCurrentPosition())/2);}
    private double slidesVelocity = 0.0;
    public double getSlidesVelocity(){return slidesVelocity;}

    private boolean slidingInProgress = false;
    public boolean isSlidingInProgress(){return slidingInProgress;}
    public double getMotorPower(){return (LSleft.getPower()+LSright.getPower())/2;}


    //Emergency Stops
    private static final double SLIDES_UPPER_BOUND = 976.0;
    private static final double SLIDES_LOWER_BOUND = 0.0;
    private static final int CYCLES_PER_REVOLUTION = 7;
    private static final double GEAR_RATIO = 1;

    private enum LinearSlidesSTATE{
        LEVELED(SLIDES_UPPER_BOUND/3), //default, linear slides leveled control
        DRIVER_CONTROLLED(1.0); //driver custom position control
        private final double interval;
        LinearSlidesSTATE(double interval){this.interval = interval;}
    }
    private int currentLevel = 0;
    private LinearSlidesSTATE linearSlidesSTATE;
    private PIDController pidController = new PIDController(1,0,0);

    public LinearSlides(HardwareMap hardwareMap, Button inc, Button dec, Button switchState) {
        LSleft = hardwareMap.get(DcMotorEx.class, "LinearSlidesLeft");
        LSright = hardwareMap.get(DcMotorEx.class, "LinearSlidesRight");
        LSleft.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        LSright.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        LSright.setDirection(DcMotorEx.Direction.REVERSE);
        linearSlidesSTATE = linearSlidesSTATE.LEVELED;
        inc.onPress((GamepadInteractionEvent callback) -> {if (((slidesPositionTarget+linearSlidesSTATE.interval) <= SLIDES_UPPER_BOUND) && linearSlidesSTATE == LinearSlidesSTATE.LEVELED)
            slidesPositionTarget += linearSlidesSTATE.interval;
            if (linearSlidesSTATE == linearSlidesSTATE.LEVELED){
                currentLevel = (int)(SLIDES_UPPER_BOUND/slidesPositionTarget);}});
        dec.onPress((GamepadInteractionEvent callback) -> {if (((slidesPositionTarget-linearSlidesSTATE.interval) >= SLIDES_LOWER_BOUND) && linearSlidesSTATE == LinearSlidesSTATE.LEVELED)
            slidesPositionTarget -= linearSlidesSTATE.interval;
            if (linearSlidesSTATE == LinearSlidesSTATE.LEVELED) {
                currentLevel = (int)(SLIDES_UPPER_BOUND/slidesPositionTarget);}});
        inc.onButtonHold((GamepadInteractionEvent callback) -> {if ((linearSlidesSTATE == linearSlidesSTATE.DRIVER_CONTROLLED) && ((slidesPositionTarget+linearSlidesSTATE.interval) <= SLIDES_UPPER_BOUND))
            setMotorPower(1.0);});
        dec.onButtonHold((GamepadInteractionEvent callback) -> {if ((linearSlidesSTATE == linearSlidesSTATE.DRIVER_CONTROLLED) && ((slidesPositionTarget-linearSlidesSTATE.interval) >= SLIDES_LOWER_BOUND))
            setMotorPower(-1.0);});
        inc.onRelease((GamepadInteractionEvent callback) -> {if (linearSlidesSTATE == LinearSlidesSTATE.DRIVER_CONTROLLED)
            setMotorPower(0.0);});
        dec.onRelease((GamepadInteractionEvent callback) -> {if (linearSlidesSTATE == LinearSlidesSTATE.DRIVER_CONTROLLED)
            setMotorPower(0.0);});
        resetEncoder();
    }
    private void setMotorPower(double power) {
        LSleft.setPower(power);
        LSright.setPower(power);
    }
    public void resetEncoder() {
        LSleft.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        LSleft.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        LSright.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        LSright.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
    }


    private void moveToTarget() {
        //moving position to position target
        pidController.calculate(slidesPositionTarget,getSlidesPosition());
    }

    public void changeState(LinearSlidesSTATE state) {
        if (state == linearSlidesSTATE.DRIVER_CONTROLLED) {
            currentLevel = -1;
        }
        linearSlidesSTATE = state;
    }

    private Supplier<Double> targetPositionEventListener = () -> 0.0;

    public void addTargetPositionListener(Supplier<Double> eventListener){
        targetPositionEventListener = eventListener;
    }

    public void operate() {
        if (!isOnTarget()) {
            slidingInProgress = true;
            moveToTarget();
        }
        else {
            slidingInProgress = false;
        }
    }

}
