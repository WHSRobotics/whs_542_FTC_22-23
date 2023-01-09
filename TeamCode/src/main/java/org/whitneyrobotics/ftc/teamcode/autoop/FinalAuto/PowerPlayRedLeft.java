package org.whitneyrobotics.ftc.teamcode.autoop.FinalAuto;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.constraints.TrajectoryVelocityConstraint;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.whitneyrobotics.ftc.teamcode.autoop.AutoSetupTesting.Tests;
import org.whitneyrobotics.ftc.teamcode.drive.RoadrunnerOmniDrive;
import org.whitneyrobotics.ftc.teamcode.framework.opmodes.OpModeEx;
import org.whitneyrobotics.ftc.teamcode.robotImpl.WHSRobotImpl;
import org.whitneyrobotics.ftc.teamcode.subsys.Drivetrains.Drivetrain;
import org.whitneyrobotics.ftc.teamcode.visionImpl.AprilTagScanner2022;

public class PowerPlayRedLeft extends OpModeEx {
    WHSRobotImpl robot;
    RoadrunnerOmniDrive drivetrain;
    AprilTagScanner2022 aprilTagScanner = new AprilTagScanner2022(hardwareMap, betterTelemetry);

    void setupTrajectories(RoadrunnerOmniDrive drive) {
        Pose2d startPose = new Pose2d(-29,-55, Math.toRadians(90));
        drive.getLocalizer().setPoseEstimate(startPose);
        drive.trajectorySequenceBuilder(startPose)
                .forward(4);
    }

    @Override
    public void initInternal() {
        robot = new WHSRobotImpl(hardwareMap);
        drivetrain = new RoadrunnerOmniDrive(hardwareMap);
    }

    @Override
    public void init_loop() {
        betterTelemetry.useTestManager()
                .addTest("Gamepad 1 Initialization", () -> Tests.assertGamepadSetup(gamepad1, "Gamepad 1"))
                .addTest("Gamepad 2 Initialization", () -> Tests.assertGamepadSetup(gamepad1, "Gamepad 1"))
                .addTest("Setup Cone Preload", () -> Tests.assertTrue(robot.robotGrabber.testForCone()))
                .addTest("Left wall setup distance", () -> Tests.assertDistanceInRange(robot.leftDist, DistanceUnit.INCH,28.5, 29.5));
        aprilTagScanner.scan();
    }

    @Override
    protected void loopInternal() {

    }
}
