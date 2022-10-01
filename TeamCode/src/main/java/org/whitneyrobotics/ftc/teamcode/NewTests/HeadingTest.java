package org.whitneyrobotics.ftc.teamcode.NewTests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.whitneyrobotics.ftc.teamcode.subsys.IMU;

@TeleOp(name="Heading Test",group="Tests")
public class HeadingTest extends OpMode {
    private IMU imu;
    private FtcDashboard dashboard;
    private Telemetry dashboardTelemetry;
    private TelemetryPacket packet = new TelemetryPacket();

    @Override
    public void init() {
        dashboard = FtcDashboard.getInstance();
        dashboardTelemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());

        dashboardTelemetry.setMsTransmissionInterval(10);
        dashboard.sendTelemetryPacket(packet);

        imu = new IMU(hardwareMap);
        imu.setImuBias(0);
    }

    @Override
    public void loop() {
        telemetry.addData("Heading",imu.getHeading()+imu.getImuBias());
        packet.put("Heading",imu.getHeading()+imu.getImuBias());
        dashboard.sendTelemetryPacket(packet);
    }
}
