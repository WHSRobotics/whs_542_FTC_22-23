package org.whitneyrobotics.ftc.teamcode.autoop.FinalAuto;

import static org.whitneyrobotics.ftc.teamcode.robotImpl.WHSRobotImpl.Alliance.RED;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.constraints.TrajectoryVelocityConstraint;
import com.acmerobotics.roadrunner.util.Angle;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.internal.system.Watchdog;
import org.whitneyrobotics.ftc.teamcode.autoop.AutoSetupTesting.TestManager;
import org.whitneyrobotics.ftc.teamcode.autoop.AutoSetupTesting.Tests;
import org.whitneyrobotics.ftc.teamcode.drive.RoadrunnerOmniDrive;
import org.whitneyrobotics.ftc.teamcode.framework.opmodes.OpModeEx;
import org.whitneyrobotics.ftc.teamcode.lib.file.RobotDataUtil;
import org.whitneyrobotics.ftc.teamcode.lib.file.WHSRobotData;
import org.whitneyrobotics.ftc.teamcode.robotImpl.WHSRobotImpl;
import org.whitneyrobotics.ftc.teamcode.subsys.Drivetrains.Drivetrain;
import org.whitneyrobotics.ftc.teamcode.subsys.LinearSlidesMeet3;
import org.whitneyrobotics.ftc.teamcode.tests.TelemetryData;
import org.whitneyrobotics.ftc.teamcode.trajectorysequence.TrajectorySequence;
import org.whitneyrobotics.ftc.teamcode.trajectorysequence.TrajectorySequenceBuilder;
import org.whitneyrobotics.ftc.teamcode.visionImpl.AprilTagScanner2022;

@Autonomous(name="Right", group="A",preselectTeleOp = "PowerPlay TeleOp")
public class PowerPlayRight extends OpModeEx {
    enum GrabberMode {
        GRAB_ON_DETECT, RELEASE
    }
    @TelemetryData
    public PowerPlayLeft.GrabberMode grabberMode = PowerPlayLeft.GrabberMode.GRAB_ON_DETECT;

    WHSRobotImpl robot;
    RoadrunnerOmniDrive drivetrain;
    AprilTagScanner2022 aprilTagScanner;
    TrajectorySequenceBuilder baseTrajectoryBuilder;
    TrajectorySequence trajectory;
    TestManager testManager;
    boolean firstCall = true;
    boolean grabberEngage = false;
    @TelemetryData
    public double currentConePrediction = 4.25;
    @TelemetryData
    public int pos = 1;
    @TelemetryData
    boolean coneDetected;

    public void setCurrentConePrediction(double pred){currentConePrediction=pred;}

    void setupTrajectories(RoadrunnerOmniDrive drive) {
        Pose2d startPose = new Pose2d(36, -65, Math.toRadians(90));
        drive.getLocalizer().setPoseEstimate(startPose);
        Vector2d eastHigh = new Vector2d(+24 + (8 * Math.cos(Math.toRadians(60))), 0 - (8 * Math.sin(Math.toRadians(60))));
        Pose2d approachEastHigh = new Pose2d(24 + (11 * Math.cos(Math.toRadians(60))),0 - (11 * Math.sin(Math.toRadians(60))), Math.toRadians(120));
        baseTrajectoryBuilder = drive.trajectorySequenceBuilder(startPose)
                .addDisplacementMarker(56,()-> robot.linearSlides.setTarget(LinearSlidesMeet3.Target.HIGH.getPosition()+1))
                .forward(64)
                .back(18)
                .setReversed(false)
                .splineTo(eastHigh, Math.toRadians(60))
                .addDisplacementMarker(()->grabberMode = PowerPlayLeft.GrabberMode.RELEASE)
                .back(4)
                .lineToLinearHeading(new Pose2d(-36,-13,Math.toRadians(160)))
                .addDisplacementMarker(()-> {
                    grabberMode = PowerPlayLeft.GrabberMode.GRAB_ON_DETECT;
                    robot.linearSlides.setTarget(currentConePrediction+2);
                })
                .waitSeconds(0.5)
                .addDisplacementMarker(()-> robot.linearSlides.setTarget(currentConePrediction+2))
                .splineTo(new Vector2d(-61,-11),Math.toRadians(180))
                .forward(6)
                .addDisplacementMarker(()->robot.linearSlides.setTarget(currentConePrediction))
                .waitSeconds(0.1)
                .addDisplacementMarker(()->robot.linearSlides.setTarget(currentConePrediction+6))
                .waitSeconds(0.5)
                .setReversed(true)
                .back(5)
                .addDisplacementMarker(() -> robot.linearSlides.setTarget(LinearSlidesMeet3.Target.HIGH.getPosition()+1))
                .splineTo(new Vector2d(-36,-14),Math.toRadians(-15))
                .setReversed(false)
                .lineToLinearHeading(approachEastHigh)
                .splineTo(eastHigh, Math.toRadians(60))
                .addDisplacementMarker(()-> {
                    grabberMode = PowerPlayLeft.GrabberMode.RELEASE;
                })
/*
                .back(3)
                .lineToLinearHeading(new Pose2d(-36,-16,Math.toRadians(160)))
                .splineTo(new Vector2d(-60,-12),Math.toRadians(180))
                .addTemporalMarker(0.2, () -> {
                    grabberEngage = true;
                })
                .forward(1)
                .waitSeconds(1)
                .setReversed(true)
                .back(1)
                .splineTo(new Vector2d(-36,-16),Math.toRadians(-15))
                .setReversed(false)
                .lineToLinearHeading(approachWestHigh)
                .addDisplacementMarker(() -> robot.linearSlides.setTarget(LinearSlidesMeet3.Target.HIGH))
                .splineTo(westHigh, Math.toRadians(60))
                .waitSeconds(0.5)
*/
                .back(5)
                .addDisplacementMarker(()->robot.linearSlides.setTarget(LinearSlidesMeet3.Target.LOWERED))
                .splineToConstantHeading(new Vector2d(-36,-15),Math.toRadians(-90));
    }

    @Override
    public void initInternal() {
        LinearSlidesMeet3.useIdleStatic = false;
        robot = new WHSRobotImpl(hardwareMap,false);
        robot.setCurrentAlliance(RED);
        drivetrain = new RoadrunnerOmniDrive(hardwareMap);
        setupTrajectories(drivetrain);
        aprilTagScanner = new AprilTagScanner2022(hardwareMap, betterTelemetry);
        testManager = betterTelemetry.useTestManager()
                .addTest("Gamepad 1 Initialization", () -> Tests.assertGamepadSetup(gamepad1, "Gamepad 1"))
                .addTest("Gamepad 2 Initialization", () -> Tests.assertGamepadSetup(gamepad1, "Gamepad 1"))
                .addTest("Battery voltage test", () -> Tests.assertBatteryCharged(robot.controlHub))
                .addTest("Setup Cone Preload", () -> Tests.assertTrue(robot.robotGrabber.testForCone()));

        addTemporalCallback(resolve -> {
            Pose2d poseEstimate = drivetrain.getLocalizer().getPoseEstimate();
            WHSRobotData.heading = Math.toDegrees(poseEstimate.getHeading());
            WHSRobotData.lastX = poseEstimate.getX();
            WHSRobotData.lastY = poseEstimate.getY();
            WHSRobotData.slidesHeight = robot.linearSlides.getPosition();
            RobotDataUtil.save(WHSRobotData.class, true);
            resolve.accept(true);
        },29000);
    }

    @Override
    public void initInternalLoop() {
        testManager.run();
        //.addTest("Left wall setup distance", () -> Tests.assertDistanceInRange(robot.leftDist, DistanceUnit.INCH,28.5, 29.5));
        int lastPos = pos;
        pos = aprilTagScanner.scan();
        if(lastPos != pos){

        }
        betterTelemetry.addData("park pos",pos);
    }

    @Override
    public void startInternal(){
        LinearSlidesMeet3.useIdleStatic = true;
        switch(pos){
            case 3:
                trajectory = baseTrajectoryBuilder
                        .lineToLinearHeading(new Pose2d(60,-12, Math.toRadians(0))).build();
                break;
            case 1:
                trajectory = baseTrajectoryBuilder
                        .lineToLinearHeading(new Pose2d(12.5,-12.5, Math.toRadians(-90))).build();
                break;
            default:
                trajectory = baseTrajectoryBuilder.
                        lineToSplineHeading(new Pose2d(36,-36,-Math.toRadians(90)))
                        .build();
        }
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
        switch (grabberMode){
            case GRAB_ON_DETECT:
                //grabberEngage = robot.autoGrab(grabberEngage, currentConePrediction, this::setCurrentConePrediction);
                coneDetected = robot.robotGrabber.testForCone();
                break;
            case RELEASE:
                robot.robotGrabber.forceOpen();
                robot.robotGrabber.tick();
                break;
        }

        if(!robot.linearSlides.isSliding()){
            robot.linearSlides.operate(0.3,false);
        }
        robot.linearSlides.tick();
    }
}
