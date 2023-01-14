package org.whitneyrobotics.ftc.teamcode.autoop.FinalAuto;

import static org.whitneyrobotics.ftc.teamcode.robotImpl.WHSRobotImpl.Alliance.RED;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.constraints.TrajectoryVelocityConstraint;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.internal.system.Watchdog;
import org.whitneyrobotics.ftc.teamcode.autoop.AutoSetupTesting.TestManager;
import org.whitneyrobotics.ftc.teamcode.autoop.AutoSetupTesting.Tests;
import org.whitneyrobotics.ftc.teamcode.drive.RoadrunnerOmniDrive;
import org.whitneyrobotics.ftc.teamcode.framework.opmodes.OpModeEx;
import org.whitneyrobotics.ftc.teamcode.robotImpl.WHSRobotImpl;
import org.whitneyrobotics.ftc.teamcode.subsys.Drivetrains.Drivetrain;
import org.whitneyrobotics.ftc.teamcode.trajectorysequence.TrajectorySequence;
import org.whitneyrobotics.ftc.teamcode.visionImpl.AprilTagScanner2022;

@Autonomous(name="Bottom left", group="A")
public class PowerPlayRedLeft extends OpModeEx {
    WHSRobotImpl robot;
    RoadrunnerOmniDrive drivetrain;
    AprilTagScanner2022 aprilTagScanner;
    TrajectorySequence trajectory;
    TestManager testManager;
    boolean firstCall = true;
    boolean grabberEngage = false;
    double currentConePrediction = 11;

    public void setCurrentConePrediction(double pred){currentConePrediction=pred;}

    void setupTrajectories(RoadrunnerOmniDrive drive) {
        Pose2d startPose = new Pose2d(-36, -65, Math.toRadians(90));
        drive.getLocalizer().setPoseEstimate(startPose);
        Vector2d approachWestHigh = new Vector2d(-24 - (7 * Math.sin(Math.toRadians(30))), 0 - (7 * Math.cos(Math.toRadians(30))));
        trajectory = drive.trajectorySequenceBuilder(startPose)
                .forward(16)
                .splineTo(approachWestHigh, Math.toRadians(60))
                .waitSeconds(1)

                .back(2)
                .splineToConstantHeading(new Vector2d(-36,-12),Math.toRadians(180))
                .lineToLinearHeading(new Pose2d(-61,-12, Math.toRadians(180)))
                .waitSeconds(1)
                .lineToSplineHeading(new Pose2d(-40, -12, Math.toRadians(360)))
                .splineTo(approachWestHigh, Math.toRadians(60))
                //.splineToLinearHeading(new Pose2d(-24-(7*Math.sin(Math.toRadians(30))),0-(7*Math.cos(Math.toRadians(30))), Math.toRadians(60)), Math.toRadians(60))
                .waitSeconds(1)

                .back(2)
                .splineToConstantHeading(new Vector2d(-36,-12),Math.toRadians(180))
                .lineToLinearHeading(new Pose2d(-61,-12, Math.toRadians(180)))
                //.addDisplacementMarker(() -> grabberEngage=true)
                .waitSeconds(1)
                .lineToSplineHeading(new Pose2d(-40, -12, Math.toRadians(360)))
                .splineTo(approachWestHigh, Math.toRadians(60))
                //.splineToLinearHeading(new Pose2d(-24-(7*Math.sin(Math.toRadians(30))),0-(7*Math.cos(Math.toRadians(30))), Math.toRadians(60)), Math.toRadians(60))

                .waitSeconds(1)

                .back(2)
                .splineToConstantHeading(new Vector2d(-36,-12),Math.toRadians(180))
                .lineToLinearHeading(new Pose2d(-61,-12, Math.toRadians(180)))
                .waitSeconds(1)
                .lineToSplineHeading(new Pose2d(-40, -12, Math.toRadians(360)))
                .splineTo(approachWestHigh, Math.toRadians(60))
                //.lineToLinearHeading(new Pose2d(-40, -12, Math.toRadians(60)))
                //.splineToLinearHeading(new Pose2d(-24-(7*Math.sin(Math.toRadians(30))),0-(7*Math.cos(Math.toRadians(30))), Math.toRadians(60)), Math.toRadians(60))
                .waitSeconds(1)

                .back(2)
                .splineToConstantHeading(new Vector2d(-36,-12),Math.toRadians(180))
                .lineToLinearHeading(new Pose2d(-60, -12, Math.toRadians(180)))
                .build();
    }

    @Override
    public void initInternal() {
        robot = new WHSRobotImpl(hardwareMap);
        robot.setCurrentAlliance(RED);
        drivetrain = new RoadrunnerOmniDrive(hardwareMap);
        setupTrajectories(drivetrain);
        aprilTagScanner = new AprilTagScanner2022(hardwareMap, betterTelemetry);
        testManager = betterTelemetry.useTestManager()
                .addTest("Gamepad 1 Initialization", () -> Tests.assertGamepadSetup(gamepad1, "Gamepad 1"))
                .addTest("Gamepad 2 Initialization", () -> Tests.assertGamepadSetup(gamepad1, "Gamepad 1"))
                .addTest("Battery voltage test", () -> Tests.assertBatteryCharged(robot.controlHub))
                .addTest("Setup Cone Preload", () -> Tests.assertTrue(robot.robotGrabber.testForCone()));
    }

    @Override
    public void initInternalLoop() {
       testManager.run();
                //.addTest("Left wall setup distance", () -> Tests.assertDistanceInRange(robot.leftDist, DistanceUnit.INCH,28.5, 29.5));
        aprilTagScanner.scan();
    }

    @Override
    public void startInternal(){
        //drivetrain.followTrajectorySequence(trajectory);
    }

    @Override
    protected void loopInternal() {
        if(firstCall){
            drivetrain.followTrajectorySequenceAsync(trajectory);
            firstCall = false;
        }
        if(drivetrain.isBusy()){
            drivetrain.update();
        }
        grabberEngage = robot.autoGrab(grabberEngage, currentConePrediction, this::setCurrentConePrediction);
    }
}
