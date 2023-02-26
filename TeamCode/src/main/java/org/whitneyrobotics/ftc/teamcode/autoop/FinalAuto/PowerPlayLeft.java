package org.whitneyrobotics.ftc.teamcode.autoop.FinalAuto;

import static org.whitneyrobotics.ftc.teamcode.robotImpl.WHSRobotImpl.Alliance.RED;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.constraints.TrajectoryVelocityConstraint;
import com.acmerobotics.roadrunner.util.Angle;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.stream.CameraStreamSource;
import org.firstinspires.ftc.robotcore.internal.system.Watchdog;
import org.whitneyrobotics.ftc.teamcode.autoop.AutoSetupTesting.TestManager;
import org.whitneyrobotics.ftc.teamcode.autoop.AutoSetupTesting.Tests;
import org.whitneyrobotics.ftc.teamcode.drive.RoadrunnerOmniDrive;
import org.whitneyrobotics.ftc.teamcode.framework.opmodes.OpModeEx;
import org.whitneyrobotics.ftc.teamcode.lib.file.RobotDataUtil;
import org.whitneyrobotics.ftc.teamcode.lib.file.WHSRobotData;
import org.whitneyrobotics.ftc.teamcode.lib.filters.LowPassFilter;
import org.whitneyrobotics.ftc.teamcode.robotImpl.WHSRobotImpl;
import org.whitneyrobotics.ftc.teamcode.subsys.Drivetrains.Drivetrain;
import org.whitneyrobotics.ftc.teamcode.subsys.LinearSlidesMeet3;
import org.whitneyrobotics.ftc.teamcode.tests.TelemetryData;
import org.whitneyrobotics.ftc.teamcode.trajectorysequence.TrajectorySequence;
import org.whitneyrobotics.ftc.teamcode.trajectorysequence.TrajectorySequenceBuilder;
import org.whitneyrobotics.ftc.teamcode.visionImpl.AprilTagScanner2022;

@Config
@Autonomous(name="Left", group="A", preselectTeleOp = "PowerPlayTeleOp")
public class PowerPlayLeft extends OpModeEx {
    enum GrabberMode {
        GRAB_ON_DETECT, RELEASE
    }
    @TelemetryData
    public GrabberMode grabberMode = GrabberMode.GRAB_ON_DETECT;

    WHSRobotImpl robot;
    RoadrunnerOmniDrive drivetrain;
    AprilTagScanner2022 aprilTagScanner;
    TrajectorySequenceBuilder baseTrajectoryBuilder;
    TrajectorySequence trajectory;
    TestManager testManager;

    LowPassFilter distanceSensorFilter = new LowPassFilter(33, smoothing);
    boolean firstCall = true;
    @TelemetryData
    public static double currentConePrediction = 3.75;
    @TelemetryData
    public static int pos = 1;

    public static double smoothing = 0.2;
    @TelemetryData
    boolean coneDetected;
    @TelemetryData
    public static boolean forceDetect;


    public void setCurrentConePrediction(double pred){currentConePrediction=pred;}

    void setupTrajectories(RoadrunnerOmniDrive drive) {
        Pose2d startPose = new Pose2d(-36, -65, Math.toRadians(90));
        drive.getLocalizer().setPoseEstimate(startPose);
        Vector2d westHigh = new Vector2d(-24 - (8 * Math.cos(Math.toRadians(60))), 0 - (8 * Math.sin(Math.toRadians(60))));
        Pose2d approachWestHigh = new Pose2d(-24 - (11 * Math.cos(Math.toRadians(60))),0 - (11 * Math.sin(Math.toRadians(60))), Math.toRadians(60));
        baseTrajectoryBuilder = drive.trajectorySequenceBuilder(startPose)
                .addDisplacementMarker(56,()-> robot.linearSlides.setTarget(LinearSlidesMeet3.Target.HIGH.getPosition()+1))
                .forward(60)
                .back(16)
                .setReversed(false)
                .splineTo(westHigh, Math.toRadians(60))
                .addDisplacementMarker(()->grabberMode = GrabberMode.RELEASE)
                .waitSeconds(0.3)
                .back(11.5)
                //.lineToLinearHeading(new Pose2d(-36,-13,Math.toRadians(160)))
                .addDisplacementMarker(()-> {
                    grabberMode = GrabberMode.GRAB_ON_DETECT;
                    robot.linearSlides.setTarget(currentConePrediction+2);
                })
                .waitSeconds(0.2)
                .addDisplacementMarker(()-> robot.linearSlides.setTarget(currentConePrediction+2))

                .setReversed(true)
                .lineToLinearHeading(new Pose2d(-52,-14,Math.toRadians(180)))
                .setReversed(false)
                .lineToLinearHeading(new Pose2d(-66,-12,Math.toRadians(180)))
                .addDisplacementMarker(()->robot.linearSlides.setTarget(currentConePrediction))
                .waitSeconds(0.1)
                .addDisplacementMarker(()->robot.linearSlides.setTarget(currentConePrediction+4.5))
                .waitSeconds(0.3)
                .addDisplacementMarker(() -> robot.linearSlides.setTarget(LinearSlidesMeet3.Target.HIGH.getPosition()+1))
                .setReversed(true)
                .splineTo(new Vector2d(-52,-14),Math.toRadians(0))
                .splineTo(new Vector2d(-36,-14),Math.toRadians(0))
                .setReversed(false)
                .lineToLinearHeading(approachWestHigh)
                //.lineToLinearHeading(new Pose2d(-24,-14, Math.toRadians(90)))
                //.forward(3)
                .splineTo(westHigh, Math.toRadians(60))
                .addDisplacementMarker(()-> {
                    grabberMode = GrabberMode.RELEASE;
                })
                .waitSeconds(0.3)
                .back(10)
                .addDisplacementMarker(()->robot.linearSlides.setTarget(LinearSlidesMeet3.Target.LOWERED));
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
                //.splineToConstantHeading(new Vector2d(-36,-15),Math.toRadians(-90));
    }

    @Override
    public void initInternal() {
        betterTelemetry.useDashboardTelemetry(dashboardTelemetry);
        LinearSlidesMeet3.useIdleStatic = false;
        robot = new WHSRobotImpl(hardwareMap,false);
        robot.setCurrentAlliance(RED);
        drivetrain = new RoadrunnerOmniDrive(hardwareMap);
        setupTrajectories(drivetrain);
        aprilTagScanner = new AprilTagScanner2022(hardwareMap, betterTelemetry);
        testManager = betterTelemetry.useTestManager()
                .addTest("Gamepad 1 Initialization", () -> Tests.assertGamepadSetup(gamepad1, "Gamepad 1"))
                .addTest("Gamepad 2 Initialization", () -> Tests.assertGamepadSetup(gamepad1, "Gamepad 2"))
                .addTest("Left wall setup distance", () -> Tests.assertDistanceInRange(robot.leftDist, DistanceUnit.INCH,32, 34, distanceSensorFilter))
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
        },29500);
        startDriverStationWebcamStream(aprilTagScanner.camera);
    }

    @Override
    public void initInternalLoop() {
       testManager.run();
                //.addTest("Left wall setup distance", () -> Tests.assertDistanceInRange(robot.leftDist, DistanceUnit.INCH,28.5, 29.5));
        int lastPos = pos;
        if(!forceDetect) pos = aprilTagScanner.scan();
        if(lastPos != pos){

        }
        betterTelemetry.addData("park pos",pos);
        betterTelemetry.addData("distance",distanceSensorFilter.getOutput());
    }

    @Override
    public void startInternal(){
        LinearSlidesMeet3.useIdleStatic = true;
        switch(pos){
            case 1:
                trajectory = baseTrajectoryBuilder
                        .lineToLinearHeading(new Pose2d(-60,-14, Math.toRadians(180))).build();
                break;
            case 3:
                trajectory = baseTrajectoryBuilder
                        .lineToLinearHeading(new Pose2d(-12.5,-14, Math.toRadians(-90))).build();
                break;
            default:
                trajectory = baseTrajectoryBuilder
                //        .lineToSplineHeading(new Pose2d(-36,-14,Math.toRadians(0)))
                        .lineToSplineHeading(new Pose2d(-36,-36,-Math.toRadians(-90)))
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
                robot.robotGrabber.setState(false);
                robot.robotGrabber.tick();
                break;
        }
        robot.linearSlides.tick();
    }

    @Override
    public void stop() {
        Pose2d poseEstimate = drivetrain.getLocalizer().getPoseEstimate();
        WHSRobotData.heading = Math.toDegrees(poseEstimate.getHeading());
        WHSRobotData.lastX = poseEstimate.getX();
        WHSRobotData.lastY = poseEstimate.getY();
        WHSRobotData.slidesHeight = robot.linearSlides.getPosition();
        RobotDataUtil.save(WHSRobotData.class, true);
    }
}
