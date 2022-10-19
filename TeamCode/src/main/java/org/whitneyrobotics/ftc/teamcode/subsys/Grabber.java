package org.whitneyrobotics.ftc.teamcode.subsys;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import org.whitneyrobotics.ftc.teamcode.lib.util.Toggler;

public class Grabber extends OpMode{
    private Servo gate;
    private Servo[] servos = new Servo[2];
    private Toggler positionTog = new Toggler(101);
    public boolean GrabberDrop = true;
    private Servo ServoGrip = null;
    private boolean GripOpen = false;
    private static Toggler ServoGateToggle = new Toggler(2);
    private Toggler ArmServos;

    @Override
    public void init() {
        ArmServos = hardwareMap.get(Servo.class, "servo 1");
        ArmServos = new Toggler(1);
    }

    @Override
    public void loop() {
        positionTog.changeState(gamepad1.dpad_right,gamepad1.dpad_left);
        if (gamepad1.dpad_right = true) {
            positionTog.setState(positionTog.currentState()+1);
        } else if (gamepad1.dpad_left = true) {
            positionTog.setState(positionTog.currentState()-1);
        }
        telemetry.addData("Position", positionTog.currentState());
    }
    //    private void ServoGateToggler(boolean touch) {
//        ServoGateToggle.changeState(touch);
//
//        gate.setPosition(ServoGateToggle.currentState());
//
//    }
    private enum GrabberStates {
        CLOSE(0),
        OPEN(1);

        double val;
        GrabberStates(double val){
            this.val = val;
        }

        double getPosition(){
            return val;
        }
    }
    public void armDrop() {
        if (GrabberDrop) {
            ServoGateToggle.setState(1);
            gate.setPosition(org.whitneyrobotics.ftc.teamcode.NewTests.GrabberTest.GrabberStates.CLOSE.getPosition());
        } else {
            ServoGateToggle.setState(1);
            gate.setPosition(org.whitneyrobotics.ftc.teamcode.NewTests.GrabberTest.GrabberStates.OPEN.getPosition());
        }

    }
    public void GripPosition() {
        if (GripOpen) {
            GripOpen = false;
            ServoGrip.setPosition(org.whitneyrobotics.ftc.teamcode.NewTests.GrabberTest.GrabberStates.CLOSE.getPosition());
        } else {
            GripOpen = true;
            ServoGrip.setPosition(org.whitneyrobotics.ftc.teamcode.NewTests.GrabberTest.GrabberStates.OPEN.getPosition());
        }
    }

}


