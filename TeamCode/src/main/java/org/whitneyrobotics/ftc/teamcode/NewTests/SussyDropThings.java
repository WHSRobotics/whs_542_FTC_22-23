package org.whitneyrobotics.ftc.teamcode.NewTests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.whitneyrobotics.ftc.teamcode.subsys.Outtake;

@TeleOp(name="SussyDropThings",group="New Tests")
public class SussyDropThings extends OpMode {
    Servo gate;

    public double testOuttakePos;

    public SussyDropThings (HardwareMap hardwareMap){
        gate = hardwareMap.get(Servo.class, "gateServo");
    }

    @Override
    public void init(){
        telemetry.addLine("Test.");
    }

    @Override
    public void loop() {
        testOuttakePos = gate.getPosition();
        if (gamepad1.dpad_up){
            gate.setPosition(testOuttakePos + 1);
        } else if (gamepad1.dpad_down){
            gate.setPosition(testOuttakePos - 1);
        }
    }


}
