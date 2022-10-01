package org.whitneyrobotics.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp (name = "Simple Servo Test")
public class simpleServoTest extends OpMode {

    public Servo testingServo;

    public int i = 0;

    public double currentPos;

    @Override
    public void init() {
        testingServo = hardwareMap.servo.get("0");
    }

    @Override
    public void loop() {

        i++;

        if ((i % 144 == 0) && gamepad1.a) {
            currentPos = testingServo.getPosition();
            testingServo.setPosition(currentPos + 0.01);
            currentPos = testingServo.getPosition();
        }

        if (gamepad1.b){
            currentPos = testingServo.getPosition();
            testingServo.setPosition(currentPos + 0.01);
            currentPos = testingServo.getPosition();
        } else if (gamepad1.y) {
            currentPos = testingServo.getPosition();
            testingServo.setPosition(currentPos - 0.01);
            currentPos = testingServo.getPosition();
        }



        telemetry.addData("Current Position", currentPos);
    }
}
