package org.whitneyrobotics.ftc.teamcode.tests.HardwareTests;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import org.whitneyrobotics.ftc.teamcode.framework.opmodes.OpModeEx;
@Config
@TeleOp(group="HardwareTests")
public class GrabberServoTest extends OpModeEx {
    public static double pos;

    Servo gate;
    @Override
    public void initInternal() {
        gate = hardwareMap.get(Servo.class,"gate");
    }

    @Override
    protected void loopInternal() {
        gate.setPosition(pos);
    }
}
