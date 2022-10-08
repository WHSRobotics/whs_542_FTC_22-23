package org.whitneyrobotics.ftc.teamcode.NewTests;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import org.whitneyrobotics.ftc.teamcode.lib.util.Toggler;


public class GrabberTest extends OpMode {
    private Servo[] servos = new Servo[2];
    private Toggler positionTog = new Toggler(101);
    private Toggler servoSelect = new Toggler(2);


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
            //servoSelect.currentState(servos[1]);
        } else if (gamepad1.dpad_up) {
            //servoSelect.currentState(servos[2]);
        }
        telemetry.addData("Current Servo", servoSelect.currentState());
        telemetry.addData("Position", positionTog.currentState());
    }
}
