package org.whitneyrobotics.ftc.teamcode.NewTests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.whitneyrobotics.ftc.teamcode.subsys.WHSRobotImpl;

@TeleOp(name="Velocity Test",group="Swerve Tests")
public class VelocityTest extends OpMode {
    FtcDashboard dashboard;
    Telemetry telemetry;
    TelemetryPacket packet = new TelemetryPacket();
    WHSRobotImpl robot;
    double highestLeftVelocity = 0.0;
    double highestRightVelocity = 0.0;
    double highestAvgVelocity;

    @Override
    public void init() {
        dashboard = FtcDashboard.getInstance();
        telemetry = dashboard.getTelemetry();
        telemetry.setMsTransmissionInterval(10);
        dashboard.sendTelemetryPacket(packet);

        robot = new WHSRobotImpl(hardwareMap);
        robot.drivetrain.resetEncoders();
        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());
    }

    @Override
    public void loop() {
        robot.drivetrain.operateMecanumDriveScaled(-gamepad1.left_stick_x, gamepad1.left_stick_y, -gamepad1.right_stick_x, robot.getCoordinate().getHeading());

        highestLeftVelocity = Math.max(highestLeftVelocity, robot.drivetrain.getWheelVelocities()[0]);
        highestRightVelocity = Math.max(highestRightVelocity, robot.drivetrain.getWheelVelocities()[1]);
        double avgVelocity = (robot.drivetrain.getWheelVelocities()[0]+robot.drivetrain.getWheelVelocities()[1])/2;
        highestAvgVelocity = Math.max(highestAvgVelocity,avgVelocity);
        packet.put("A - Top left velocity", highestLeftVelocity);
        packet.put("B - Top right velocity", highestRightVelocity);
        packet.put("C - Top avg velocity",highestAvgVelocity);
        packet.put("D - Drivetrain left velocity", robot.drivetrain.getWheelVelocities()[0]);
        packet.put("E - Drivetrain right velocity",robot.drivetrain.getWheelVelocities()[1]);
        packet.put("F - Drivetrain Average Velocity", avgVelocity);
        telemetry.addData("Telemetry test","If this shows up on both DS and FTC Dash then this is the correct telemetry implementation.");
        dashboard.sendTelemetryPacket(packet);
    }
}
