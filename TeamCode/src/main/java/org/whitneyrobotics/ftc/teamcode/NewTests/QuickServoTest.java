package org.whitneyrobotics.ftc.teamcode.NewTests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImpl;

import org.whitneyrobotics.ftc.teamcode.lib.util.Toggler;

@TeleOp(name="servo")
public class QuickServoTest extends OpMode {
    public Servo testingServo;

    private double[] testingPos = {0.0, 0.5, 1.0};

    private Toggler quickServoTestTog = new Toggler(3);

    @Override
    public void init() {
        testingServo = hardwareMap.get(Servo.class, "A");
    }

    @Override
    public void loop() {
        if (gamepad1.a || gamepad1.b){
            quickServoTestTog.changeState(gamepad1.a, gamepad1.b);
        }
        testingServo.setPosition(testingPos[quickServoTestTog.currentState()]);
        telemetry.addData("", quickServoTestTog.currentState());
    }
}
