package org.whitneyrobotics.ftc.teamcode.subsys;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.whitneyrobotics.ftc.teamcode.lib.control.PIDController;
import org.whitneyrobotics.ftc.teamcode.lib.geometry.Coordinate;
import org.whitneyrobotics.ftc.teamcode.lib.geometry.Position;
import org.whitneyrobotics.ftc.teamcode.lib.purepursuit.strafetotarget.StrafePath;
import org.whitneyrobotics.ftc.teamcode.lib.purepursuit.swervetotarget.SwerveFollower;
import org.whitneyrobotics.ftc.teamcode.lib.purepursuit.swervetotarget.SwervePath;
import org.whitneyrobotics.ftc.teamcode.lib.util.Functions;
import org.whitneyrobotics.ftc.teamcode.lib.util.RobotConstants;
import org.whitneyrobotics.ftc.teamcode.lib.util.Toggler;

public class WHSRobotImplDrivetrainOnly {
    //public Carousel carousel;
    public Drivetrain drivetrain;
    //public Outtake outtake;
    public IMU imu;
    //public Intake robotIntake;


    SwervePath currentSwervePath;
    public SwerveFollower swerveFollower;

    private Toggler intakeOuttakeState = new Toggler(5);
    private String stateDesc = "";

    Coordinate currentCoord;
    private double targetHeading; //field frame
    public double angleToTargetDebug;
    public double distanceToTargetDebug = 0;
    public Position vectorToTargetDebug = new Position(542, 542);
    private double lastKnownHeading = 0.1;

    private static double DEADBAND_DRIVE_TO_TARGET = RobotConstants.DEADBAND_DRIVE_TO_TARGET; //in mm
    private static double DEADBAND_ROTATE_TO_TARGET = RobotConstants.DEADBAND_ROTATE_TO_TARGET; //in degrees

    public static double DRIVE_MIN = RobotConstants.drive_min;
    public static double DRIVE_MAX = RobotConstants.drive_max;
    public static double ROTATE_MIN = RobotConstants.rotate_min;
    public static double ROTATE_MAX = RobotConstants.rotate_max;

    public PIDController rotateController = new PIDController(RobotConstants.ROTATE_CONSTANTS);
    public PIDController driveController = new PIDController(RobotConstants.DRIVE_CONSTANTS);

    public void reloadPID(){
        rotateController = new PIDController(RobotConstants.ROTATE_CONSTANTS);
        driveController = new PIDController(RobotConstants.DRIVE_CONSTANTS);
    }

    public boolean firstRotateLoop = true;
    public boolean firstDriveLoop = true;
    private boolean driveBackwards;

    private int driveSwitch = 0;

    public boolean driveToTargetInProgress = false;
    public boolean rotateToTargetInProgress = false;

    private double[] encoderDeltas = {0.0, 0.0};
    private double[] encoderValues = {0.0, 0.0};
    private double robotX;
    private double robotY;
    private double distance;

    public WHSRobotImplDrivetrainOnly (HardwareMap robotMap){
        DEADBAND_DRIVE_TO_TARGET = RobotConstants.DEADBAND_DRIVE_TO_TARGET; //in mm
        DEADBAND_ROTATE_TO_TARGET = RobotConstants.DEADBAND_ROTATE_TO_TARGET; //in degrees

        drivetrain = new Drivetrain(robotMap);
        ///carousel = new Carousel(robotMap);
        ///outtake = new Outtake(robotMap);
        // intake = new OldIntake(hardwareMap); (error so commented out fix later)
        // outtake = new OldOuttake2(hardwareMap); (error so commented out fix later)
        // canister = new Canister(hardwareMap); (error so commented out fix later)
        // wobble = new Wobble(hardwareMap); (error so commented out fix later)

        DRIVE_MIN = RobotConstants.drive_min;
        DRIVE_MAX = RobotConstants.drive_max;
        ROTATE_MIN = RobotConstants.rotate_min;
        ROTATE_MAX = RobotConstants.rotate_max;


        drivetrain.resetEncoders();
        imu = new IMU(robotMap);
        currentCoord = new Coordinate(0.0, 0.0, 0.0);
    }

    public void driveToTarget(Position targetPos, boolean backwards) {
        Position vectorToTarget = Functions.Positions.subtract(targetPos, currentCoord.getPos()); //field frame
        vectorToTarget = Functions.field2body(vectorToTarget, currentCoord); //body frame
        vectorToTargetDebug = vectorToTarget;
        double distanceToTarget = vectorToTarget.getX(); /*Functions.calculateMagnitude(vectorToTarget) * (vectorToTarget.getX() >= 0 ? 1 : -1)*/;
        distanceToTargetDebug = distanceToTarget;

        double degreesToRotate = Math.atan2(vectorToTarget.getY(), vectorToTarget.getX()); //from -pi to pi rad
        degreesToRotate = -degreesToRotate * 180 / Math.PI;
        targetHeading = Functions.normalizeAngle(currentCoord.getHeading() - degreesToRotate); //-180 to 180 deg

        switch (driveSwitch) {
            case 0:
                driveToTargetInProgress = true;
                rotateToTarget(targetHeading, backwards);
                if (!rotateToTargetInProgress()) {
                    driveSwitch = 1;
                }
                break;
            case 1:

                if (firstDriveLoop) {
                    if(Math.abs(distanceToTarget) < DEADBAND_DRIVE_TO_TARGET){
                        distanceToTarget = vectorToTarget.getY();
                    }
                    driveToTargetInProgress = true;
                    driveController.init(distanceToTarget);
                    firstDriveLoop = false;
                }

                driveController.setConstants(RobotConstants.DRIVE_CONSTANTS);
                driveController.calculate(distanceToTarget);

                double power = Functions.map(Math.abs(driveController.getOutput()), DEADBAND_DRIVE_TO_TARGET, 1500, DRIVE_MIN, DRIVE_MAX);

                // this stuff may be causing the robot to oscillate around the target position
                if (distanceToTarget < 0) {
                    power = -power;
                } else if (distanceToTarget > 0) {
                    power = Math.abs(power);
                }
                if (Math.abs(distanceToTarget) > DEADBAND_DRIVE_TO_TARGET) {
                    driveToTargetInProgress = true;
                    drivetrain.operateLeft(power);
                    drivetrain.operateRight(power);
                } else {
                    drivetrain.operateRight(0.0);
                    drivetrain.operateLeft(0.0);
                    driveToTargetInProgress = false;
                    rotateToTargetInProgress = false;
                    firstDriveLoop = true;
                    driveSwitch = 0;
                }
                // end of weird code
                break;
        }
    }

    public void rotateToTarget(double targetHeading, boolean backwards) {

        double angleToTarget = targetHeading - currentCoord.getHeading();
        /*if (backwards && angleToTarget > 90) {
            angleToTarget = angleToTarget - 180;
            driveBackwards = true;
        }
        else if (backwards && angleToTarget < -90) {
            angleToTarget = angleToTarget + 180;
            driveBackwards = true;
        }*/
        if (backwards) {
            angleToTarget = Functions.normalizeAngle(angleToTarget + 180); //-180 to 180 deg
            driveBackwards = true;
        } else {
            angleToTarget = Functions.normalizeAngle(angleToTarget);
            driveBackwards = false;
        }

        angleToTargetDebug = angleToTarget;

        if (firstRotateLoop) {
            rotateToTargetInProgress = true;
            rotateController.init(angleToTarget);
            firstRotateLoop = false;
        }

        rotateController.setConstants(RobotConstants.ROTATE_CONSTANTS);
        rotateController.calculate(angleToTarget);

        double power = (rotateController.getOutput() >= 0 ? 1 : -1) * (Functions.map(Math.abs(rotateController.getOutput()), 0, 180, ROTATE_MIN, ROTATE_MAX));

        if (Math.abs(angleToTarget) > DEADBAND_ROTATE_TO_TARGET/* && rotateController.getDerivative() < 40*/) {
            drivetrain.operateLeft(-power);
            drivetrain.operateRight(power);
            rotateToTargetInProgress = true;
        } else {
            drivetrain.operateLeft(0.0);
            drivetrain.operateRight(0.0);
            rotateToTargetInProgress = false;
            firstRotateLoop = true;
        }
    }

    public boolean driveToTargetInProgress() {
        return driveToTargetInProgress;
    }

    public boolean rotateToTargetInProgress() {
        return rotateToTargetInProgress;
    }

    public void estimateHeading() {
        double currentHeading;
        currentHeading = Functions.normalizeAngle(imu.getHeading() + imu.getImuBias()); //-180 to 180 deg
        currentCoord.setHeading(currentHeading); //updates global variable
    }

    public void estimateCoordinate() {
        double[] currentEncoderValues = drivetrain.getLRAvgEncoderPosition();
        encoderDeltas[0] = currentEncoderValues[0] - encoderValues[0];
        encoderDeltas[1] = currentEncoderValues[1] - encoderValues[1];
        double currentHeading = Functions.normalizeAngle(Math.toDegrees(drivetrain.encToMM((currentEncoderValues[1] - currentEncoderValues[0]) / 2 / Drivetrain.getTrackWidth())) + imu.getImuBias()); //-180 to 180 deg
        currentCoord.setHeading(currentHeading); //updates global variable

        double deltaS = drivetrain.encToMM((encoderDeltas[0] + encoderDeltas[1]) / 2);
        double deltaHeading = Math.toDegrees(drivetrain.encToMM((encoderDeltas[1] - encoderDeltas[0]) / Drivetrain.getTrackWidth()));
        robotX += deltaS * Functions.cosd(lastKnownHeading + deltaHeading / 2);
        robotY += deltaS * Functions.sind(lastKnownHeading + deltaHeading / 2);

        currentCoord.setX(robotX);
        currentCoord.setY(robotY);
        encoderValues[0] = currentEncoderValues[0];
        encoderValues[1] = currentEncoderValues[1];
        lastKnownHeading = currentCoord.getHeading();
    }

    public Coordinate getCoordinate() {
        return currentCoord;
    }

    public void estimatePosition() {
        encoderDeltas = drivetrain.getLRAvgEncoderDelta();
        distance = drivetrain.encToMM((encoderDeltas[0] + encoderDeltas[1]) / 2);
        robotX += distance * Functions.cosd(getCoordinate().getHeading());
        robotY += distance * Functions.sind(getCoordinate().getHeading());
        currentCoord.setX(robotX);
        currentCoord.setY(robotY);
    }

    public void setInitialCoordinate(Coordinate initCoord) {
        currentCoord = initCoord;
        robotX = initCoord.getX();
        robotY = initCoord.getY();
        imu.setImuBias(currentCoord.getHeading());
        lastKnownHeading = currentCoord.getHeading();
    }

    public void deadWheelEstimateCoordinate() {

        double deltaXRobot, deltaYRobot;

        encoderDeltas = drivetrain.getMMDeadwheelEncoderDeltas();

        double deltaXWheels = (encoderDeltas[0] - encoderDeltas[2]) / 2;
        double deltaYWheel = encoderDeltas[1];
        double deltaTheta = (-encoderDeltas[2] - encoderDeltas[0]) / (Drivetrain.getTrackWidth());

        if (deltaTheta == 0) {
            deltaXRobot = deltaXWheels;
            deltaYRobot = deltaYWheel;
        } else {
            double movementRadius = deltaXWheels / (deltaTheta);
            double strafeRadius = deltaYWheel / (deltaTheta);

            deltaXRobot = movementRadius * Math.sin(deltaTheta) + strafeRadius * (1 - Math.cos(deltaTheta));
            deltaYRobot = strafeRadius * Math.sin(deltaTheta) - movementRadius * (1 - Math.cos(deltaTheta));
        }
        Position bodyVector = new Position(deltaXRobot, deltaYRobot);
        Position fieldVector = Functions.body2field(bodyVector, currentCoord);
        currentCoord.addX(fieldVector.getX());
        currentCoord.addY(fieldVector.getY());
        currentCoord.setHeading(Functions.normalizeAngle(currentCoord.getHeading() + Math.toDegrees(deltaTheta)));
    }

    public void updatePath(SwervePath path) {
        swerveFollower = new SwerveFollower(path);
    }

    public void updatePath(StrafePath path) {

    }

    public void swerveToTarget() {
        drivetrain.operate(swerveFollower.calculateMotorPowers(getCoordinate(), drivetrain.getWheelVelocities()));
    }

    public boolean swerveInProgress() {
        return swerveFollower.inProgress();
    }

}
