package org.whitneyrobotics.ftc.teamcode.subsys;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.whitneyrobotics.ftc.teamcode.MotionProfile.MotionProfileTrapezoidal;
import org.whitneyrobotics.ftc.teamcode.lib.control.PIDCoefficientsNew;
import org.whitneyrobotics.ftc.teamcode.lib.control.PIDControlledMotor;

@RequiresApi(api = Build.VERSION_CODES.N)
@Config
public class LinearSlidesMeet1  {
    public static double DEADBAND_ERROR = 1.5;
    public static double acceleration = 5;
    public static final double TICKS_PER_INCH = 100;

    public static double kP = 0.6;
    public static double kD = 0.0025;
    public static double kI = 0.00;

    public enum Target {
        LOWERED(0), CONES(2),LOW(12.5), MEDIUM(22.5), HIGH(38.5);

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

    private State currentState = State.IDLE;
    private Target currentTarget = Target.LOWERED;
    private MotionProfileTrapezoidal currentMotionProfile = null;

    DcMotorEx slidesL, slidesR;
    PIDControlledMotor pidL, pidR;
    public double velocity;

    public LinearSlidesMeet1(HardwareMap hardwareMap){
        slidesL = hardwareMap.get(DcMotorEx.class, "slidesL");
        slidesR = hardwareMap.get(DcMotorEx.class,"slidesR");
        slidesR.setDirection(DcMotorSimple.Direction.REVERSE);
        slidesL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        slidesR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        resetEncoders();
        pidL = new PIDControlledMotor(slidesL,5, new PIDCoefficientsNew(kP,kI,kD));
        pidR = new PIDControlledMotor(slidesR,5,new PIDCoefficientsNew(kP,kI,kD));
    }

    public void setTarget(Target t){
        this.currentTarget = t;
        currentState = State.PID_CONTROLLED;
        currentMotionProfile = new MotionProfileTrapezoidal((getRawPosition()*2*Math.PI)/TICKS_PER_INCH, acceleration,5,DEADBAND_ERROR);
    }

    public void resetEncoders(){
        slidesL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slidesL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        slidesR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slidesR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }
    public void operate(double power, boolean slow){
        if(Math.abs(power) > 0.1){ cancelMovement();} // signal filtering
        switch (currentState) {
            case IDLE:
                slidesL.setPower(power * (slow ? 0.5 : 1));
                slidesR.setPower(power * (slow ? 0.5 : 1));
                break;
            case PID_CONTROLLED:
                if(currentMotionProfile == null || currentMotionProfile.isFinished()){
                    cancelMovement();
                    break;
                }
                velocity = currentMotionProfile.calculate((getRawPosition()*2*Math.PI)/TICKS_PER_INCH, (currentTarget.getPosition()*2*Math.PI));
                pidL.setTargetVelocity(velocity);
                pidR.setTargetVelocity(velocity);
                pidL.update();
                pidR.update();
        }
    }

    public String getPhase(){
        if (currentMotionProfile != null) {
            return currentMotionProfile.phase;
        }
        return "IDLE";
    }
    public boolean isSliding(){
        if (currentMotionProfile != null) {
            return !currentMotionProfile.isFinished();
        }
        return false;
    }
    public boolean isRaised(){return getRawPosition() > 250;}
    public void reloadPIDCoefficients(){
        pidL.reloadCoefficients(kP,kI,kD);
        pidR.reloadCoefficients(kP,kI,kD);
    }

    public double getRawPosition(){
        return (slidesL.getCurrentPosition() + slidesR.getCurrentPosition())/2;
    }

    public double getPosition(){
        return getRawPosition()/TICKS_PER_INCH;
    }

    private void cancelMovement(){
        this.currentState = State.IDLE;
        this.currentMotionProfile =null;
    }

    public PIDControlledMotor getPIDL(){return pidL;}
}
