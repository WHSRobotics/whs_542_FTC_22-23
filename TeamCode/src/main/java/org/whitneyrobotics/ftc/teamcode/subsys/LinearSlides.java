package org.whitneyrobotics.ftc.teamcode.subsys;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.whitneyrobotics.ftc.teamcode.GamepadEx.Button;
import org.whitneyrobotics.ftc.teamcode.GamepadEx.GamepadEx;
import org.whitneyrobotics.ftc.teamcode.GamepadEx.GamepadInteractionEvent;
import org.whitneyrobotics.ftc.teamcode.lib.control.PIDVAController;

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

    public double getSlidesPosition(){
        return ((LSleft.getCurrentPosition()+LSright.getCurrentPosition())/2.0);
    }
    private double slidesPositionTarget = 0.0;
    public boolean isOnTarget(){return Math.abs(slidesPositionTarget-getSlidesPosition())<=ACCEPTABLE_ERROR;}
    public double getSlidesVelocity(){return (LSleft.getVelocity()+LSright.getVelocity())/2.0;}

    public boolean isSlidingInProgress(){return getSlidesVelocity() != 0;}
    public double getMotorPower(){return (LSleft.getPower()+LSright.getPower())/2;}

    //Emergency Stops
    private static final double SLIDES_UPPER_BOUND = 976.0;
    private static final double SLIDES_LOWER_BOUND = 0.0;
    private static final double SLIDES_INIT = 5.0; // inches
    private static final double MAX_ACCELERATION = 5; // inches/sec^2
    private static final double MAX_VELOCITY = 50; // inches/s

    private static final double ACCEPTABLE_ERROR = 5; //ticks
    private static final double SHAFT_DIAMETER = 2; //inches
    private static final int CYCLES_PER_REVOLUTION = 7;
    private static final double GEAR_RATIO = 1.0/2.0;



    public enum LinearSlidesSTATE{
        LEVELED(SLIDES_UPPER_BOUND/3), //default, linear slides leveled control
        DRIVER_CONTROLLED(1.0); //driver custom position control
        private final double interval;
        LinearSlidesSTATE(double interval){this.interval = interval;}
    }
    private int currentLevel = 0;
    public int getCurrentLevel() {return currentLevel;}
    public LinearSlidesSTATE linearSlidesSTATE;
    private final PIDVAController pidvaController = new PIDVAController(MAX_VELOCITY,MAX_ACCELERATION,1,0,0);

    //Button inc, Button dec, Button switchState, Button reset
    public LinearSlides(HardwareMap hardwareMap, GamepadEx gamepad1) {
        Button inc = gamepad1.DPAD_UP;
        Button dec = gamepad1.DPAD_DOWN;
        Button switchState = gamepad1.BUMPER_LEFT;
        Button reset = gamepad1.X;
        LSleft = hardwareMap.get(DcMotorEx.class, "LinearSlidesLeft");
        LSright = hardwareMap.get(DcMotorEx.class, "LinearSlidesRight");
        LSleft.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        LSright.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        LSright.setDirection(DcMotorEx.Direction.REVERSE);
        linearSlidesSTATE = LinearSlidesSTATE.LEVELED;
        inc.onPress((GamepadInteractionEvent callback) -> {incrementLevel();});
        dec.onPress((GamepadInteractionEvent callback) -> {decrementLevel();});
        inc.onButtonHold((GamepadInteractionEvent callback) -> {if ((linearSlidesSTATE == LinearSlidesSTATE.DRIVER_CONTROLLED) && ((slidesPositionTarget+linearSlidesSTATE.interval) <= SLIDES_UPPER_BOUND))
            setMotorPower(0.5);});
        dec.onButtonHold((GamepadInteractionEvent callback) -> {if ((linearSlidesSTATE == LinearSlidesSTATE.DRIVER_CONTROLLED) && ((slidesPositionTarget-linearSlidesSTATE.interval) >= SLIDES_LOWER_BOUND))
            setMotorPower(-0.5);});
        inc.onRelease((GamepadInteractionEvent callback) -> {if (linearSlidesSTATE == LinearSlidesSTATE.DRIVER_CONTROLLED)
            setMotorPower(0.0);});
        dec.onRelease((GamepadInteractionEvent callback) -> {if (linearSlidesSTATE == LinearSlidesSTATE.DRIVER_CONTROLLED)
            setMotorPower(0.0);});
        switchState.onPress((GamepadInteractionEvent callback) -> {
            if (linearSlidesSTATE == LinearSlidesSTATE.LEVELED) linearSlidesSTATE = LinearSlidesSTATE.DRIVER_CONTROLLED;
            else linearSlidesSTATE = LinearSlidesSTATE.LEVELED;});
        reset.onPress((GamepadInteractionEvent callback) -> reset());
        resetEncoder();
    }
    public void setLevelTarget(int levelTarget) {
        if (levelTarget <= 3 && levelTarget >= 0) {
            slidesPositionTarget = levelTarget*linearSlidesSTATE.interval;
            currentLevel = levelTarget;
        }
    }
    public void incrementLevel(){
        if (((slidesPositionTarget+linearSlidesSTATE.interval) <= SLIDES_UPPER_BOUND) && linearSlidesSTATE == LinearSlidesSTATE.LEVELED)
            slidesPositionTarget += linearSlidesSTATE.interval;
        if (linearSlidesSTATE == LinearSlidesSTATE.LEVELED){
            currentLevel = (int)(SLIDES_UPPER_BOUND/slidesPositionTarget);}
    }
    public void decrementLevel(){
        if (((slidesPositionTarget-linearSlidesSTATE.interval) >= SLIDES_LOWER_BOUND) && linearSlidesSTATE == LinearSlidesSTATE.LEVELED)
            slidesPositionTarget -= linearSlidesSTATE.interval;
        if (linearSlidesSTATE == LinearSlidesSTATE.LEVELED) {
            currentLevel = (int)(SLIDES_UPPER_BOUND/slidesPositionTarget);}
    }

    public void reset() {
        setLevelTarget(0);
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
        /*
        pidController.calculate(slidesPositionTarget,getSlidesPosition());
        double power = Functions.map(pidController.getOutput(),-100,100,-1,1);
        setMotorPower(power);
         */
    }

    public void changeState(LinearSlidesSTATE state) {
        if (state == LinearSlidesSTATE.DRIVER_CONTROLLED) {
            currentLevel = -1;
        }
        linearSlidesSTATE = state;
    }

    public void operate() {
        if (!isOnTarget() && linearSlidesSTATE != LinearSlidesSTATE.DRIVER_CONTROLLED) {
            if (getSlidesVelocity() == 0.0) {
                pidvaController.setDesiredPos(slidesPositionTarget, getSlidesPosition(), System.nanoTime());
            }
            setMotorPower(pidvaController.output(getSlidesPosition(), System.nanoTime()));
        }
    }

}
