package org.whitneyrobotics.ftc.teamcode.tests.HardwareTests;

import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.whitneyrobotics.ftc.teamcode.framework.opmodes.OpModeEx;

@Autonomous(group="Z")
public class DistanceSensorTest extends OpModeEx {
    public Rev2mDistanceSensor left, right;

    @Override
    public void initInternal() {
        betterTelemetry.useDashboardTelemetry(dashboardTelemetry);
        left = hardwareMap.get(Rev2mDistanceSensor.class,"distanceLeft");
        right = hardwareMap.get(Rev2mDistanceSensor.class,"distanceRight");
    }

    @Override
    protected void loopInternal() {
        betterTelemetry.addData("left",left.getDistance(DistanceUnit.CM));
        betterTelemetry.addData("right",right.getDistance(DistanceUnit.CM));
    }
}
