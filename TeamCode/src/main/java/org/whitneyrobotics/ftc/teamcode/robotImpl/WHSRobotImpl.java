package org.whitneyrobotics.ftc.teamcode.robotImpl;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.acmerobotics.dashboard.canvas.Canvas;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.openftc.easyopencv.OpenCvCamera;
import org.whitneyrobotics.ftc.teamcode.Field.Field;
import org.whitneyrobotics.ftc.teamcode.Field.Junction;
import org.whitneyrobotics.ftc.teamcode.GamepadEx.GamepadEx;
import org.whitneyrobotics.ftc.teamcode.GamepadEx.GamepadInteractionEvent;
import org.whitneyrobotics.ftc.teamcode.lib.control.PIDCoefficientsNew;
import org.whitneyrobotics.ftc.teamcode.lib.control.PIDControllerNew;
import org.whitneyrobotics.ftc.teamcode.lib.geometry.Coordinate;
import org.whitneyrobotics.ftc.teamcode.lib.util.Functions;
import org.whitneyrobotics.ftc.teamcode.lib.util.SimpleTimer;
import org.whitneyrobotics.ftc.teamcode.subsys.Drivetrains.OmniDrivetrain;
import org.whitneyrobotics.ftc.teamcode.subsys.Grabber;
import org.whitneyrobotics.ftc.teamcode.subsys.IMU;
import org.whitneyrobotics.ftc.teamcode.subsys.LinearSlides;
import org.whitneyrobotics.ftc.teamcode.subsys.LinearSlidesMeet1;
import org.whitneyrobotics.ftc.teamcode.subsys.LinearSlidesMeet3;
import org.whitneyrobotics.ftc.teamcode.subsys.Odometry.EncoderConverter;
import org.whitneyrobotics.ftc.teamcode.subsys.Odometry.HWheelOdometry;

import java.util.PriorityQueue;
import java.util.function.Consumer;
import java.util.function.Supplier;

@RequiresApi(Build.VERSION_CODES.N)
public class WHSRobotImpl {

    public LynxModule controlHub;
    public enum Alliance {
        RED, BLUE
    }
    public enum TeleOpState {
        ANGULAR_FREEDOM, SNAP_TO_TARGET
    }

    public enum Mode {
        AUTONOMOUS, TELEOP
    }

    public Supplier<Integer> colorSensorMethod;

    private Alliance currentAliiance = Alliance.RED;

    public TeleOpState teleOpState = TeleOpState.ANGULAR_FREEDOM;
    public OmniDrivetrain drivetrain;
    public LinearSlides robotLinearSlides;
    public LinearSlidesMeet3 linearSlides;
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
        robotGrabber = new Grabber(hardwareMap);
        linearSlides = new LinearSlidesMeet3(hardwareMap);
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
        controlHub = hardwareMap.get(LynxModule.class,"Control Hub");
        colorSensorMethod = robotGrabber.sensor::red;
        //leftDist = hardwareMap.get(Rev2mDistanceSensor.class,"distanceLeft");
        //rightDist = hardwareMap.get(Rev2mDistanceSensor.class,"distanceRight");
        //drivetrain.setFollower(PurePursuitFollower::new);
    }

    public void setCurrentAlliance(Alliance a){
        this.currentAliiance = a;
        colorSensorMethod = a == Alliance.RED ? robotGrabber.sensor::red : robotGrabber.sensor::blue;
    }

    public boolean isSensingCone(){
        return this.colorSensorMethod.get() > 200;
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

    private enum AutoGrabberState {
        LIFTING_TO_ESTIMATE, WAITING_UNTIL_ESTIMATE, SEARCHING_FOR_CONE,WAIT_UNTIL_READY, GRAB_CONE, LIFTING, WAIT_UNTIL_LIFTED
    }

    public AutoGrabberState grabberState = AutoGrabberState.values()[0];

    public boolean autoGrab(boolean shouldGrab, double currentConePrediction, Consumer<Double> conePredictionSetter){
        LinearSlidesMeet1.DEADBAND_ERROR = 0.5;
        double power = -0.05;
        boolean gateOpen = true;

        if(currentConePrediction < 0){
            shouldGrab = false;
        }

        if(shouldGrab){
            switch (grabberState){
                case LIFTING_TO_ESTIMATE:
                    linearSlides.setTarget(currentConePrediction);
                    grabberState = AutoGrabberState.WAITING_UNTIL_ESTIMATE;
                    break;
                case WAITING_UNTIL_ESTIMATE:
                    if(!linearSlides.isSliding()){
                        grabberState = AutoGrabberState.SEARCHING_FOR_CONE;
                    }
                    break;
                case SEARCHING_FOR_CONE:
                    if(isSensingCone()){
                        gateOpen = true;
                        grabberState = AutoGrabberState.WAIT_UNTIL_READY;
                        linearSlides.down(-1.5);
                    }
                    break;
                case WAIT_UNTIL_READY:
                    if (!linearSlides.isSliding()){
                        grabberState = AutoGrabberState.GRAB_CONE;
                    }
                case GRAB_CONE:
                    gateOpen = false;
                    grabberState = AutoGrabberState.LIFTING;
                    break;
                case LIFTING:
                    linearSlides.up(5);
                    grabberState = AutoGrabberState.WAIT_UNTIL_LIFTED;
                    //if(!isSensingCone()){
                        //grabberState = AutoGrabberState.SEARCHING_FOR_CONE;
                    //}
                    break;
                case WAIT_UNTIL_LIFTED:
                    if(!linearSlides.isSliding()){
                        grabberState = AutoGrabberState.SEARCHING_FOR_CONE;
                        linearSlides.setMode(Mode.AUTONOMOUS);
                        robotGrabber.tick();
                        linearSlides.tick();
                        conePredictionSetter.accept(currentConePrediction-2);
                        shouldGrab = false;
                        return false;
                    }
            }
            robotGrabber.setState(!gateOpen);
            robotGrabber.tick();
            linearSlides.operate(grabberState == AutoGrabberState.SEARCHING_FOR_CONE ? power : 0, false);
        }
        return shouldGrab;
    }



    public void tick(){
        robotGrabber.tick();
    }

}
