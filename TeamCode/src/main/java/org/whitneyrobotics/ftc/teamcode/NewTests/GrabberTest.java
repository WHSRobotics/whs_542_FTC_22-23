package org.whitneyrobotics.ftc.teamcode.NewTests;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import org.whitneyrobotics.ftc.teamcode.lib.util.Toggler;


public class GrabberTest extends OpMode {
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
<<<<<<< HEAD
        } else if (gamepad1.dpad_down) {
            //servoSelect.currentState(servos[1]);
        } else if (gamepad1.dpad_up) {
            //servoSelect.currentState(servos[2]);
=======
>>>>>>> main
        }
        telemetry.addData("Position", positionTog.currentState());
    }
//    private void ServoGateToggler(boolean touch) {
//        ServoGateToggle.changeState(touch);
//
//        gate.setPosition(ServoGateToggle.currentState());
//
//    }
    public enum GrabberStates {
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
            gate.setPosition(GrabberStates.CLOSE.getPosition());
        } else {
            ServoGateToggle.setState(1);
            gate.setPosition(GrabberStates.OPEN.getPosition());
        }

    }
    public void GripPosition() {
        if (GripOpen) {
            GripOpen = false;
            ServoGrip.setPosition(GrabberStates.CLOSE.getPosition());
        } else {
            GripOpen = true;
            ServoGrip.setPosition(GrabberStates.OPEN.getPosition());
        }
    }

}

