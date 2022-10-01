package org.whitneyrobotics.ftc.teamcode.subsys;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.whitneyrobotics.ftc.teamcode.lib.util.SimpleTimer;
import org.whitneyrobotics.ftc.teamcode.lib.util.Toggler;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

public class CarouselOld {
    private CRServo spinner;

    public CarouselOld(HardwareMap hardwareMap) {
        spinner = hardwareMap.crservo.get("spinnerServo");
    }

    /*public final double WHEEL_CIRCUMFERENCE = 6.2831853072;
    public final double CAROUSEL_CIRCUMFERENCE = 47.1238898038;
    public final double SERVO_RPM = 90;
    private double rotations = CAROUSEL_CIRCUMFERENCE/WHEEL_CIRCUMFERENCE;
    private double seconds = (rotations/SERVO_RPM*60 + 0.5)*2;*/
    private double seconds = 6;

    private boolean rotateInProgress = false;
    private boolean firstLoop = true;

    private Toggler onOff= new Toggler(2);
    private SimpleTimer timer = new SimpleTimer();
    //toggler based teleop

    /*public void spinServo(double analogValue){
        if(!rotateInProgress){ spinner.setPower(analogValue); }
    }*/

    public void togglerOperate(boolean on, boolean rev){
            if(!rotateInProgress) {
                onOff.changeState(on);
                if (onOff.currentState() == 1) {
                    spinner.setPower((rev) ? 1 : -1);
                    //rotateInProgress = true;
                } else {
                    spinner.setPower(0);
                    //rotateInProgress = false;
                }
            }
    }
    //tele-op [BUGGED]
    public void operate(boolean buttonInput) {
        //telemetry.addData("Spin ducky: ", rotateInProgress);
        if (!rotateInProgress){
            if(buttonInput){
                rotateInProgress = true;
            }
        }  else if ( rotateInProgress){
            if(firstLoop){
                timer.set(seconds);
                firstLoop = false;
            } else if (timer.isExpired()){
                spinner.setPower(0);
                firstLoop = true;
                rotateInProgress = false;
            } else {
                spinner.setPower(-1);
            }

        }

    }


    //autonomous
    public void operate() {
        //telemetry.addData("Spin ducky: ", rotateInProgress);
        if(!rotateInProgress) {
            if (firstLoop) {
                rotateInProgress = true;
                timer.set(seconds);
                firstLoop = false;
            } else if (timer.isExpired()) {
                spinner.setPower(0);
                firstLoop = true;
                rotateInProgress = false;
            } else {
                spinner.setPower(-1);
            }
        }
    }

    //public double getRotations() { return rotations; }
    public double getSeconds() { return seconds; }
    public boolean getTimer() {return timer.isExpired(); }
    public boolean rotateInProgress() { return rotateInProgress; }
    public boolean firstLoop() { return firstLoop; }
    public int getTogglerState() {return onOff.currentState();}

}
