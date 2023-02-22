package org.whitneyrobotics.ftc.teamcode.tests.SoftwareTests;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.whitneyrobotics.ftc.teamcode.framework.opmodes.OpModeEx;
import org.whitneyrobotics.ftc.teamcode.subsys.Grabber;

@TeleOp(group="SoftwareTests")
public class GrabberDisconnectFix extends OpModeEx {
    public Grabber grabber;

    @Override
    public void initInternal() {
        grabber = new Grabber(hardwareMap);
    }

    @Override
    protected void loopInternal() {
        betterTelemetry.addData("grabber disconnected",!grabber.sensor.isLightOn());
        betterTelemetry.addData("coneDetected",grabber.testForCone());
        betterTelemetry.addData("distance",grabber.sensor.getDistance(DistanceUnit.CM));
    }
}
