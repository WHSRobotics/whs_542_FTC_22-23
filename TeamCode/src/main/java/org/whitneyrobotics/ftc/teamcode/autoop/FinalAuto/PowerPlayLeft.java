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
        Pose2d startPose = new Pose2d(-36, -65, Math.toRadians(90));
        drive.getLocalizer().setPoseEstimate(startPose);
        Vector2d westHigh = new Vector2d(-24 - (8 * Math.cos(Math.toRadians(60))), 0 - (8 * Math.sin(Math.toRadians(60))));
        Pose2d approachWestHigh = new Pose2d(-24 - (11 * Math.cos(Math.toRadians(60))),0 - (11 * Math.sin(Math.toRadians(60))), Math.toRadians(60));
        baseTrajectoryBuilder = drive.trajectorySequenceBuilder(startPose)
                .forward(72)
                .back(26)
                .setReversed(false)
                .addTemporalMarker(3.5,()-> robot.linearSlides.setTarget(LinearSlidesMeet3.Target.HIGH.getPosition()+1.5))
                .splineTo(westHigh, Math.toRadians(60))
                .waitSeconds(0.25)
                .addDisplacementMarker(()->grabberMode = GrabberMode.RELEASE)

                .back(3)
                .lineToLinearHeading(new Pose2d(-36,-16,Math.toRadians(160)))
                .addDisplacementMarker(()-> {
                    grabberMode = GrabberMode.GRAB_ON_DETECT;
                    robot.linearSlides.setTarget(currentConePrediction+2);
                })
                .splineTo(new Vector2d(-61,-12.25),Math.toRadians(180))
                .forward(4)
                .addDisplacementMarker(()->robot.linearSlides.setTarget(currentConePrediction))
                .waitSeconds(0.5)
                .addDisplacementMarker(()->robot.linearSlides.setTarget(currentConePrediction+5))
                .waitSeconds(0.5)
                .setReversed(true)
                .back(4)
                .addDisplacementMarker(() -> robot.linearSlides.setTarget(LinearSlidesMeet3.Target.HIGH))
                .splineTo(new Vector2d(-36,-16),Math.toRadians(-15))
                .setReversed(false)
                .lineToLinearHeading(approachWestHigh)
                .splineTo(westHigh, Math.toRadians(60))
                .addDisplacementMarker(()->grabberMode = GrabberMode.RELEASE)
                .waitSeconds(0.5)
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
                .back(3)
                .splineToConstantHeading(new Vector2d(-36,-15),Math.toRadians(-90))
                .addDisplacementMarker(()->robot.linearSlides.setTarget(LinearSlidesMeet3.Target.LOWERED));
    }

    @Override
    public void initInternal() {
        robot = new WHSRobotImpl(hardwareMap,false);
        robot.setCurrentAlliance(RED);
        drivetrain = new RoadrunnerOmniDrive(hardwareMap);
        setupTrajectories(drivetrain);
        aprilTagScanner = new AprilTagScanner2022(hardwareMap, betterTelemetry);
        testManager = betterTelemetry.useTestManager()
                .addTest("Gamepad 1 Initialization", () -> Tests.assertGamepadSetup(gamepad1, "Gamepad 1"))
                .addTest("Gamepad 2 Initialization", () -> Tests.assertGamepadSetup(gamepad1, "Gamepad 2"))
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
        switch(pos){
            case 1:
                trajectory = baseTrajectoryBuilder
                        .lineToLinearHeading(new Pose2d(-60,-12, Math.toRadians(180))).build();
                break;
            case 3:
                trajectory = baseTrajectoryBuilder
                        .lineToLinearHeading(new Pose2d(-12.5,-12.5, Math.toRadians(-90))).build();
                break;
            default:
                trajectory = baseTrajectoryBuilder.turn(Angle.normDelta(Math.toRadians(-90)-drivetrain.getLocalizer().getPoseEstimate().getHeading()))
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
