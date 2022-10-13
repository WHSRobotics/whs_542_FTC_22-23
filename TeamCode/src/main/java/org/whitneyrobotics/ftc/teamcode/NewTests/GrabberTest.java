package org.whitneyrobotics.ftc.teamcode.NewTests;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;
import org.whitneyrobotics.ftc.teamcode.lib.util.Toggler;


public class GrabberTest extends OpMode {
    private Servo gate;;
    private Servo[] servos = new Servo[2];
    private Toggler positionTog = new Toggler(101);
    private Toggler servoSelect = new Toggler(2);
    public boolean GrabberDrop = True;
    private static Toggler ServoGateToggle = new Toggler(2);


    @Override
    public void init() {
        servos[0] = hardwareMap.get(Servo.class, "servo 1");
        servos[1] = hardwareMap.get(Servo.class, "servo 2");
    }

    @Override
    public void loop() {
        positionTog.changeState(gamepad1.dpad_right,gamepad1.dpad_left);
        if (gamepad1.dpad_right = true) {
            positionTog.setState(positionTog.currentState()+1);
        } else if (gamepad1.dpad_left = true) {
            positionTog.setState(positionTog.currentState()-1);
        } else if (gamepad1.dpad_down) {
            servoSelect.currentState(servos[1]);
        } else if (gamepad1.dpad_up) {
            servoSelect.currentState(servos[2]);
        }
        telemetry.addData("Current Servo", servoSelect.currentState());
        telemetry.addData("Position", positionTog.currentState());
    }
    private void ServoGateToggler(boolean touch) {
        ServoGateToggle.changeState(touch);

        if (ServoGateToggle.currentState() == 0) {
            gate.setPosition(0);
            ServoGateToggle.setState(1);
        } else {
            gate.setPosition(1);
        }

    }
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
            ServoGateToggler(true);
            gate.setPosition(GrabberStates.CLOSE.getPosition());
        } else {
            ServoGateToggler(false);
            gate.setPosition(GrabberStates.OPEN.getPosition());
        }

    }

}

