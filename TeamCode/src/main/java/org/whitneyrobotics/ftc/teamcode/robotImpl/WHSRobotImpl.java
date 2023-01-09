package org.whitneyrobotics.ftc.teamcode.robotImpl;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.acmerobotics.dashboard.canvas.Canvas;
import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.openftc.apriltag.AprilTagDetection;
import org.openftc.easyopencv.OpenCvCamera;
import org.whitneyrobotics.ftc.teamcode.Field.Field;
import org.whitneyrobotics.ftc.teamcode.Field.Junction;
import org.whitneyrobotics.ftc.teamcode.GamepadEx.GamepadEx;
import org.whitneyrobotics.ftc.teamcode.GamepadEx.GamepadInteractionEvent;
import org.whitneyrobotics.ftc.teamcode.framework.opmodes.OpModeEx;
import org.whitneyrobotics.ftc.teamcode.lib.control.PIDCoefficientsNew;
import org.whitneyrobotics.ftc.teamcode.lib.control.PIDControllerNew;
import org.whitneyrobotics.ftc.teamcode.lib.geometry.Coordinate;
import org.whitneyrobotics.ftc.teamcode.lib.pathfollowers.purepursuit.PurePursuitFollower;
import org.whitneyrobotics.ftc.teamcode.lib.util.Functions;
import org.whitneyrobotics.ftc.teamcode.lib.util.SimpleTimer;
import org.whitneyrobotics.ftc.teamcode.subsys.Drivetrains.OmniDrivetrain;
import org.whitneyrobotics.ftc.teamcode.subsys.Grabber;
import org.whitneyrobotics.ftc.teamcode.subsys.IMU;
import org.whitneyrobotics.ftc.teamcode.subsys.LinearSlides;
import org.whitneyrobotics.ftc.teamcode.subsys.LinearSlidesMeet1;
import org.whitneyrobotics.ftc.teamcode.subsys.Odometry.EncoderConverter;
import org.whitneyrobotics.ftc.teamcode.subsys.Odometry.HWheelOdometry;
import org.whitneyrobotics.ftc.teamcode.visionImpl.AprilTagDetectionPipeline;

import java.util.ArrayList;
import java.util.PriorityQueue;

@RequiresApi(Build.VERSION_CODES.N)
public class WHSRobotImpl {
    public enum Alliance {
        RED, BLUE
    }
    public enum TeleOpState {
        ANGULAR_FREEDOM, SNAP_TO_TARGET
    }

    private Alliance currentAliiance = Alliance.RED;

    public void setAlliance(Alliance a){this.currentAliiance = a;}
    public TeleOpState teleOpState = TeleOpState.ANGULAR_FREEDOM;
    public OmniDrivetrain drivetrain;
    public LinearSlides robotLinearSlides;
    public LinearSlidesMeet1 robotLinearSlidesMeet1;
    public Grabber robotGrabber;
    public IMU imu;
    public OpenCvCamera camera;
    public Rev2mDistanceSensor leftDist, rightDist;
//    public AprilTagDetectionPipeline aprilTagDetectionPipeline;

    public static double odometryWeight = 0.7;
    public static double imuWeight = 0.3;

    public Junction nearestJunction = null;

    public Coordinate robotCoordinate;

    public HWheelOdometry odometry;
    SimpleTimer autoTimer;
    GamepadEx gamepad;
    public WHSRobotImpl (HardwareMap hardwareMap){
        gamepad = gamepad;
        imu = new IMU(hardwareMap);
        drivetrain = new OmniDrivetrain(hardwareMap, imu);
        //robotLinearSlides = new LinearSlides(hardwareMap, gamepadOne);
        robotLinearSlidesMeet1 = new LinearSlidesMeet1(hardwareMap);
        robotGrabber = new Grabber(hardwareMap);
        //robotIntake.resetEncoders();
        odometry = new HWheelOdometry(
                new EncoderConverter.EncoderConverterBuilder()
                        .setEncoderMotor(hardwareMap.get(DcMotorEx.class,"driveBL"))
                        .setTicksPerRev(8192)
                        .setUnit(DistanceUnit.INCH)
                        .setWheelDiameter(2)
                        .setRevEncoder(true)
                        .build(),
                new EncoderConverter.EncoderConverterBuilder()
                        .setEncoderMotor(hardwareMap.get(DcMotorEx.class,"driveFR"))
                        .setTicksPerRev(8192)
                        .setUnit(DistanceUnit.INCH)
                        .setWheelDiameter(2)
                        .setRevEncoder(true)
                        .build(),
                new EncoderConverter.EncoderConverterBuilder()
                        .setEncoderMotor(hardwareMap.get(DcMotorEx.class,"driveBR"))
                        .setTicksPerRev(8192)
                        .setUnit(DistanceUnit.INCH)
                        .setWheelDiameter(2)
                        .setRevEncoder(true)
                        .build(),
                12.23,
                6.04
        );

        drivetrain.setFollower(PurePursuitFollower::new);
    }

    public void setCurrentAliiance(Alliance a){
        this.currentAliiance = a;
    }

    public void setInitialCoordinate(Coordinate c){
        this.robotCoordinate = c;
        this.odometry.setInitialPose(c);
        //this.drivetrain.imu.setImuBias(c.getHeading());
    }

    public Coordinate estimateCoordinate() {
        odometry.update();
        Coordinate position = odometry.getCurrentPosition();
        double headingAverage = /*drivetrain.imu.getHeadingRadians() * imuWeight +*/ Functions.noramlizeAngleRadians(position.getHeading() /** odometryWeight*/);
        robotCoordinate = new Coordinate(position.getX(), position.getY(),headingAverage);
        return robotCoordinate;
    }

    public void drawOverlay(Canvas fieldOverlay){
        fieldOverlay
                .setStrokeWidth(1)
                .setStroke(currentAliiance == Alliance.BLUE ? "blue" : "red")
                .strokeLine(robotCoordinate.getX(), robotCoordinate.getY(), robotCoordinate.getX()+(odometry.trackWidth/2)*Math.sin(robotCoordinate.getHeading()), robotCoordinate.getY()+(odometry.trackWidth/2)*Math.cos(robotCoordinate.getHeading()))
                .strokeCircle(robotCoordinate.getX(), robotCoordinate.getY(), odometry.trackWidth/2);
        if (nearestJunction != null) {
            fieldOverlay
                    .setStroke("green")
                    .strokeCircle(nearestJunction.pos.getX(),nearestJunction.pos.getY(),1);
        }
    }

    boolean snapToTargetInitialized;
    PIDControllerNew snapToTargetController = new PIDControllerNew(new PIDCoefficientsNew(PowerPlayRobotConfig.STTKP, PowerPlayRobotConfig.STTKI, PowerPlayRobotConfig.STTKD));

    public void resetConstants(GamepadInteractionEvent e){
        snapToTargetController.setKP(PowerPlayRobotConfig.STTKP);
        snapToTargetController.setKI(PowerPlayRobotConfig.STTKI);
        snapToTargetController.setKD(PowerPlayRobotConfig.STTKD);
    }

    public void operateDrivetrain(double xPower, double yPower, double rotPower){
        switch (teleOpState) {
            case ANGULAR_FREEDOM:
                drivetrain.operateByCommand(xPower, yPower, rotPower);
                break;
            case SNAP_TO_TARGET:
                locateNearestJunction();
                double pidRotPower = 0.0d;
                if(!snapToTargetInitialized){
                    snapToTargetController.reset();
                }
                double targetAngle = Math.atan2(nearestJunction.pos.getY()-robotCoordinate.getY(), nearestJunction.pos.getX()-robotCoordinate.getX());

                if(robotCoordinate.getX()>nearestJunction.pos.getX()) {
                    //quadrant II correction
                    if (robotCoordinate.getY() > nearestJunction.pos.getY())
                        targetAngle-=Math.PI;
                    else targetAngle = Math.PI-targetAngle;
                }
                snapToTargetController.calculate(targetAngle,robotCoordinate.getHeading());
                pidRotPower = Functions.clamp(snapToTargetController.getOutput(),-1,1);
                drivetrain.operateByCommand(xPower, yPower, pidRotPower);
                break;
        }
    }

    public void switchDriveMode(GamepadInteractionEvent e){
        this.teleOpState = (teleOpState == TeleOpState.ANGULAR_FREEDOM) ? TeleOpState.SNAP_TO_TARGET : TeleOpState.ANGULAR_FREEDOM;
        snapToTargetInitialized = false;
        if(this.teleOpState==TeleOpState.ANGULAR_FREEDOM){
            nearestJunction = null;
        }
    }

    public void locateNearestJunction(){
        Junction oldNearestJunction = nearestJunction;
        PriorityQueue<Junction> junctions = new PriorityQueue<Junction>((Junction j1, Junction j2) -> {
            double distToJ1 = Math.pow((j1.pos.getX()-robotCoordinate.getX()),2)+Math.pow((j1.pos.getY()-robotCoordinate.getY()),2);
            double distToJ2 = Math.pow((j2.pos.getX()-robotCoordinate.getX()),2)+Math.pow((j2.pos.getY()-robotCoordinate.getY()),2);
            if(distToJ1==distToJ2) return 0;
            if(distToJ1<distToJ2) return -1;
            return 1;
        });
        for(Junction j : Field.junctions){
            junctions.add(j);
        }
        nearestJunction = junctions.peek();
        if(oldNearestJunction != nearestJunction){
            snapToTargetInitialized = false;
        }
    }



    public void tick(){
        robotGrabber.tick();
    }

}
