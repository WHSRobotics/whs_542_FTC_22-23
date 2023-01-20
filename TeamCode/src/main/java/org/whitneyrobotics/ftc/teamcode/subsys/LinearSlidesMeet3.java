package org.whitneyrobotics.ftc.teamcode.subsys;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.whitneyrobotics.ftc.teamcode.framework.Alias;
import org.whitneyrobotics.ftc.teamcode.lib.control.PIDControllerNew;
import org.whitneyrobotics.ftc.teamcode.lib.control.PIDVAControllerNew;
import org.whitneyrobotics.ftc.teamcode.lib.filters.RateLimitingFilter;
import org.whitneyrobotics.ftc.teamcode.lib.util.Functions;
import org.whitneyrobotics.ftc.teamcode.robotImpl.WHSRobotImpl;

@Config
public class LinearSlidesMeet3  {
    public static double DEADBAND_ERROR = 0.5;
    public static double maxVelocity = 4.2;
    public static double acceleration = 9;
    public static double TICKS_PER_INCH = 100;
    public static double SPOOL_RADIUS = 0.75;


    //coefficients to control slidesVelocity
    public static double PIdle = 3;
    public static double IIdle = 0.00001;
    public static double DIdle = 0.008;
    public static PIDControllerNew idleController = new PIDControllerNew(PIdle, IIdle, DIdle);

    public static double kP = 0.02;
    public static double kD = 0.0012;
    public static double kI = 0.00;
    public static double kV = 0.65;
    public static double kA = 0.012;
    public static double kStatic = 0.5;

    public static double TRUE_VELOCITY = 4;
    public static PIDVAControllerNew slidingController = new PIDVAControllerNew(kP, kI, kD, kV, kA, kStatic, maxVelocity, acceleration);
    RateLimitingFilter velocityLimiter = new RateLimitingFilter(acceleration,0);



    public enum Target {
        LOWERED(0), GROUND(2), RAISED(4),LOW(12.5), MEDIUM(22.5), HIGH(35.5), LOWMED(8);

        Target(double position){
            this.position = position;
        }
        private double position;

        public double getPosition() {
            return position;
        }
    }

    private enum State {
        PID_CONTROLLED, IDLE
    }

    public State currentState = State.IDLE;
    public double currentTarget = Target.LOWERED.position;

    private DcMotorEx slidesL, slidesR;
    public WHSRobotImpl.Mode mode = WHSRobotImpl.Mode.TELEOP;
    private WHSRobotImpl.Alliance alliance = WHSRobotImpl.Alliance.RED;


    @Alias("Current Velocity")
    public double velocity;

    public void setAlliance(WHSRobotImpl.Alliance alliance){
        this.alliance = alliance;
    }

    public void setMode(WHSRobotImpl.Mode mode){
        this.mode = mode;
    }

    public double getVelocity(){
        velocity = (slidesL.getVelocity(AngleUnit.RADIANS) + slidesR.getVelocity(AngleUnit.RADIANS))/2 * SPOOL_RADIUS;
        return velocity;
    }

    public double currentVelocity = 0.0d;
    public LinearSlidesMeet3(HardwareMap hardwareMap){
        slidesL = hardwareMap.get(DcMotorEx.class, "slidesL");
        slidesR = hardwareMap.get(DcMotorEx.class,"slidesR");
        slidesR.setDirection(DcMotorSimple.Direction.REVERSE);
        slidesL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        slidesR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        resetEncoders();
        //pidL = new PIDControlledMotor(slidesL,5, new PIDCoefficientsNew(kP,kI,kD));
        //pidR = new PIDControlledMotor(slidesR,5,new PIDCoefficientsNew(kP,kI,kD));
    }

    public void EmptyConstants(){
        idleController = new PIDControllerNew(0, 0, 0);
        slidingController = new PIDVAControllerNew(0, 0, 0, 0, 0, 0, maxVelocity, acceleration);
    }

    public void setTarget(double t){
        this.currentTarget = t;
        slidingController.reset();
        slidingController.setTarget(t);
        currentState = State.PID_CONTROLLED;
    }

    public void up(double inches){
        setTarget(getPosition() + inches);
    }

    public void down(double inches){
        setTarget(getPosition() - inches);
    }

    public void setTarget(Target t){
        setTarget(t.position);
    }

    public void resetEncoders(){
        slidesL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slidesL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        slidesR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slidesR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    /*
    public static int scaleDegree = 4;

    public void calculateStaticCompensation(){
        double pos = this.getPosition();
        if(pos < 0) return;
        staticCompensation = Math.min(Math.pow(pos,scaleDegree)/Target.HIGH.getPosition() * kStatic,kStatic);
    }
    */

    public void tick(){
        operate(0,false);
    }

    public double error;
    public static double output;
    public void operate(double power, boolean slow){
        getVelocity();
        //calculateStaticCompensation();
        if(Math.abs(power) > 0.1){ cancelMovement();} // signal filtering
        switch (currentState) {
            case IDLE:
                double targetVelocity = power * TRUE_VELOCITY * (slow ? 0.5 : 1);
                velocityLimiter.calculate(targetVelocity); //synthetic acceleration
                targetVelocity = velocityLimiter.getOutput();
                idleController.calculate(targetVelocity, velocity);
                output = Math.signum(idleController.getOutput()) * Functions.map(Math.abs(idleController.getOutput()),0,10,0,1);
                break;
            case PID_CONTROLLED:
                error = currentTarget - getPosition();
                if(Math.abs(error)<DEADBAND_ERROR){
                    cancelMovement();
                    break;
                }
                slidingController.calculate(getPosition());
                output =  Math.signum(slidingController.getOutput()) * Functions.map(Math.abs(slidingController.getOutput()),0,maxVelocity,0,1);
                break;
        }


        slidesL.setPower(output);
        slidesR.setPower(output);
    }

    public String getPhase(){
        return currentState ==  State.PID_CONTROLLED ? slidingController.phase : "IDLE";
    }
    public boolean isSliding(){
        return currentState == State.PID_CONTROLLED;
    }
    public boolean isRaised(){return getPosition() > 1;}

    public double getRawPosition(){
        return (slidesL.getCurrentPosition() + slidesR.getCurrentPosition())/2;
    }

    public double getPosition(){
        return getRawPosition()/TICKS_PER_INCH;
    }

    private void cancelMovement(){
        this.currentState = State.IDLE;
        idleController.reset();
    }

}
