package org.whitneyrobotics.ftc.teamcode.NewTests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp (name = "OGTOuttakeGateTest", group = "Tests")
public class OGTOuttakeGateTest extends OpMode {

    public Servo gate;

    public double gatePos = 0.5;

    @Override
    public void init() {
        gate = hardwareMap.servo.get("gateServo");
    }

    @Override
    public void loop() {
        if (gamepad1.a){
            gate.setPosition(gatePos + 0.01);
        } else if (gamepad1.b){
            gate.setPosition(gatePos - 0.01);
        } else {
            gate.setPosition(0);
        }
        gatePos = gate.getPosition();
    }
}
