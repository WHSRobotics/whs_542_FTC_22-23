package org.whitneyrobotics.ftc.teamcode.NewTests;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.whitneyrobotics.ftc.teamcode.framework.DashboardOpMode;
@Disabled
@TeleOp(name="distance sensor test", group="new tests")
public class distanceDeleteLater extends DashboardOpMode {

    DistanceSensor distanceSensor;
    @Override
    public void init() {
        initializeDashboardTelemetry(10);
        distanceSensor = hardwareMap.get(DistanceSensor.class,"distanceSensor");
    }

    @Override
    public void loop() {
        telemetry.addData("Distance: ", distanceSensor.getDistance(DistanceUnit.INCH));
    }
}
