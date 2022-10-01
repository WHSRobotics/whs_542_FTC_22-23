package org.whitneyrobotics.ftc.teamcode.NewTests;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.whitneyrobotics.ftc.teamcode.framework.DashboardOpMode;
import org.whitneyrobotics.ftc.teamcode.subsys.IMU;

@Autonomous
public class ImuInitializationTest extends DashboardOpMode {
    public IMU imu;

    @Override
    public void init() {
        initializeDashboardTelemetry(25);
        do {
            imu = new IMU(hardwareMap);
        } while (imu.hasError());
    }

    @Override
    public void loop() {
        telemetry.addData("Imu successful?",!imu.hasError());
        telemetry.addData("Heading",imu.getHeading());
    }
}
