package org.whitneyrobotics.ftc.teamcode.subsys;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.whitneyrobotics.ftc.teamcode.lib.util.SimpleTimer;
import org.whitneyrobotics.ftc.teamcode.lib.util.Toggler;

public class SussyOuttake {
    public Servo gate;
    public DcMotor linearSlides;

    public SussyOuttake(HardwareMap outtakeMap) {
        gate = outtakeMap.servo.get("gateServo");
        linearSlides = outtakeMap.get(DcMotorEx.class, "linearSlides");
        gate.setPosition(gatePositions[GatePositions.CLOSE.ordinal()]);
        linearSlides.setDirection(DcMotor.Direction.REVERSE);
        resetEncoder();
    }

    public double level1 = 0;
    public double level2 = -1296;
    public double level3 = -2092;
    private double motorSpeed = 0.10;
    private double acceptableError = 100;
    private double[] orderedLevels = {level1, level2, level3};

    private Toggler servoGateTog = new Toggler(2);
    private Toggler linearSlidesTog = new Toggler(3);
    private SimpleTimer outtakeGateTimer = new SimpleTimer();

    private enum GatePositions{
        CLOSE, OPEN
    }
    private double[] gatePositions = {1,0.6};

    public boolean slidingInProgress = false;
    public boolean dropFirstLoop = true; //for setting drop timer
    //private boolean outtakeTimerSet = true; <<I don't know what this is used for

    //toggler based teleop
    public void togglerOuttake(boolean up, boolean down) {
        if (!slidingInProgress){linearSlidesTog.changeState(up, down);}

        double currentTarget = orderedLevels[linearSlidesTog.currentState()];

        if(Math.abs(linearSlides.getCurrentPosition()-currentTarget) <= acceptableError){
            linearSlides.setPower(0);
            slidingInProgress = false;
        } else if(linearSlides.getCurrentPosition()<currentTarget && linearSlides.getCurrentPosition()<10){
            linearSlides.setPower(motorSpeed);
            slidingInProgress = true;
        } else if(!(linearSlides.getCurrentPosition()<-2500)){
            linearSlides.setPower(-motorSpeed);
            slidingInProgress = true;
        }
    }

    public void togglerOuttakeOld(boolean up,boolean down){
        linearSlidesTog.changeState(up,down);
        if (linearSlidesTog.currentState() == 0) {
            if(linearSlides.getCurrentPosition()>level1){
                linearSlides.setPower(-motorSpeed);
            } else if (linearSlides.getCurrentPosition()<level1) {
                linearSlides.setPower(motorSpeed);
            } else {
                linearSlides.setPower(0);
            }
            if (linearSlides.getCurrentPosition() == level1) {
                slidingInProgress = false;
            } else {slidingInProgress = true;}
        } else if (linearSlidesTog.currentState() == 1) {
            if(linearSlides.getCurrentPosition()>level2){
                linearSlides.setPower(-motorSpeed);
            } else if (linearSlides.getCurrentPosition()<level2){
                linearSlides.setPower(motorSpeed);
            } else {
                linearSlides.setPower(0);
            }
            if (linearSlides.getCurrentPosition() == level2) {
                slidingInProgress = false;
            } else {slidingInProgress = true;}
        } else if (linearSlidesTog.currentState() == 2) {
            if(linearSlides.getCurrentPosition()>level3){
                linearSlides.setPower(-motorSpeed);
            } else if (linearSlides.getCurrentPosition()<level3){
                linearSlides.setPower(motorSpeed);
            } else {
                linearSlides.setPower(0);
            }
            if (linearSlides.getCurrentPosition() == level3) {
                slidingInProgress = false;
            } else {slidingInProgress = true;}
        }
    }
    public void togglerServoGate(boolean pressed){
        servoGateTog.changeState(pressed);
        if (servoGateTog.currentState() == 0) {
            gate.setPosition(gatePositions[GatePositions.CLOSE.ordinal()]);
        } else {
            gate.setPosition(gatePositions[GatePositions.OPEN.ordinal()]);
        }
    }

    public void autoControl(int levelIndex) {
        double currentTarget = orderedLevels[levelIndex];

        if(Math.abs(linearSlides.getCurrentPosition()-currentTarget) <= acceptableError){
            linearSlides.setPower(0);
            slidingInProgress = false;
        } else if(linearSlides.getCurrentPosition()>currentTarget){
            linearSlides.setPower(-motorSpeed);
            slidingInProgress = true;
        } else {
            linearSlides.setPower(motorSpeed);
            slidingInProgress = true;
        }
    }

    public boolean autoDrop() { //boolean so our autoop knows if its done
        if(dropFirstLoop) {
            togglerServoGate(true);
            outtakeGateTimer.set(500); /*ms to keep the flap open*/
            dropFirstLoop = false;
        }

        if(outtakeGateTimer.isExpired()){
            togglerServoGate(false);
            togglerServoGate(true);
            dropFirstLoop = true;
            return true;
        }
        return false;
    }

    public void reset() {
        linearSlidesTog.setState(0);
    }

    public void resetEncoder() {
        linearSlides.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        linearSlides.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public int getTier() { return linearSlidesTog.currentState(); }
}

