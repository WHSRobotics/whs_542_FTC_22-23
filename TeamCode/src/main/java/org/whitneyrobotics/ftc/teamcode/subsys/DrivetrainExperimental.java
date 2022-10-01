package org.whitneyrobotics.ftc.teamcode.subsys;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.whitneyrobotics.ftc.teamcode.lib.util.Toggler;

/**
 * Created by Jason on 10/20/2017.
 */

public class DrivetrainExperimental {
    // if all drivetrain motors consistent, remove implementation of these reductions
    private double motorReductionForFRAndBL = 1;
    private double motorReductionForBR = 1;

    public DcMotorEx frontLeft;
    public DcMotorEx frontRight;
    public DcMotorEx backLeft;
    public DcMotorEx backRight;
    //public DcMotor leftOdometry;

    public double brakePower = 0.0;

    private Toggler orientationSwitch = new Toggler(2);
    private Toggler fieldCentricSwitch = new Toggler(2);

    private static final double TRACK_WIDTH = 360.6;

    //TODO: measure actual wheel base
    private static final double WHEEL_BASE = 450;
    public static final double L_DEAD_WHEEL_TO_ROBOT_CENTER = 178.0;
    public static final double B_DEAD_WHEEL_TO_ROBOT_CENTER = 103.0;
    private static final double RADIUS_OF_WHEEL = 50;               //in mm
    private static final double CIRC_OF_WHEEL = RADIUS_OF_WHEEL * 2 * Math.PI;
    private static final double ENCODER_TICKS_PER_REV = 537.6; // <-- omnis  (537.6;      //Orbital 20))
    private static final double GEAR_RATIO = 1.0;
    private static final double ENCODER_TICKS_PER_MM = ENCODER_TICKS_PER_REV / (CIRC_OF_WHEEL * GEAR_RATIO);

    public class EncoderConverter {
        private double encoderTicksPerMM = 0.0;

        public EncoderConverter(double wheelRadius, double encoderTicksPerRev, double gearRatio) {
            double circOfWheel = wheelRadius * 2 * Math.PI;
            encoderTicksPerMM = encoderTicksPerRev / (circOfWheel * gearRatio);
        }

        public double encToMM(double encoderTicks) {
            return encoderTicks / encoderTicksPerMM;
        }
    }

    private static final double DEAD_WHEEL_RADIUS = 24; //mm
    private static final double L_WHEEL_ENC_TICKS_PER_REV = 1165;   //determined experimentally
    private static final double R_WHEEL_ENC_TICKS_PER_REV = 1440;
    private static final double LR_WHEEL_ENC_TICKS_PER_REV = 1440;
    private static final double BACK_WHEEL_ENC_TICKS_PER_REV = 1200;

    public EncoderConverter lWheelConverter = new EncoderConverter(DEAD_WHEEL_RADIUS, L_WHEEL_ENC_TICKS_PER_REV, 1.0);
    public EncoderConverter rWheelConverter = new EncoderConverter(DEAD_WHEEL_RADIUS, R_WHEEL_ENC_TICKS_PER_REV, 1.0);
    public EncoderConverter lrWheelConverter = new EncoderConverter(DEAD_WHEEL_RADIUS, LR_WHEEL_ENC_TICKS_PER_REV, 1.0);
    public EncoderConverter backWheelConverter = new EncoderConverter(DEAD_WHEEL_RADIUS, BACK_WHEEL_ENC_TICKS_PER_REV, 1.0);


    public static final double X_WHEEL_TO_ROBOT_CENTER = 100.0;
    public static final double Y_WHEEL_TO_ROBOT_CENTER = 100.0;

    private double[] encoderValues = {0.0, 0.0};
    private int[] deadWheelEncoderValues = {0,0,0};
    private double[] allEncoderValues = {0.0, 0.0, 0.0, 0.0};

    private double vFL;
    private double vFR;
    private double vBL;
    private double vBR;
    private double rightX;
    private double robotAngle;
    private double r;

    private double[] lastKnownEncoderValues = {0, 0, 0, 0};

    public DrivetrainExperimental(HardwareMap driveMap) {

        frontLeft = driveMap.get(DcMotorEx.class, "driveFL");
        frontRight = driveMap.get(DcMotorEx.class, "driveFR");
        backLeft = driveMap.get(DcMotorEx.class, "driveBL");
        backRight = driveMap.get(DcMotorEx.class, "driveBR");
        //leftOdometry = driveMap.dcMotor.get("leftOdometry");

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        //For 40s. TODO: Change this when we get more 20s.
        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);


        setRunMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        //leftOdometry.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setRunMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        //leftOdometry.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        orientationSwitch.setState(1);
    }


    public void operateWithOrientation(double leftPower, double rightPower) {
        switch (orientationSwitch.currentState()) {
            case 0:
                operateLeft(leftPower);
                operateRight(rightPower);
                break;
            case 1:
                operateLeft(-leftPower);
                operateRight(-rightPower);
                break;
        }
    }


    public void operateWithOrientationScaled(double leftPower, double rightPower) {
        double leftScaledPower = Math.pow(leftPower, 3);
        double rightScaledPower = Math.pow(rightPower, 3);

        operateWithOrientation(leftScaledPower, rightScaledPower);
    }


    public void operate(double leftPower, double rightPower) {
        frontLeft.setPower(leftPower);
        backLeft.setPower(leftPower * motorReductionForFRAndBL);
        frontRight.setPower(rightPower * motorReductionForFRAndBL);
        backRight.setPower(rightPower * motorReductionForBR);
    }


    public void operate(double[] powers) {
        if ((powers.length != 2) && (powers.length != 4)) {
            throw new IllegalArgumentException("drivetrain power array not 2 or 4 in length");
        }
        if (powers.length == 2) {
            frontLeft.setPower(powers[0]);
            backLeft.setPower(powers[0] * motorReductionForFRAndBL);
            frontRight.setPower(powers[1] * motorReductionForFRAndBL);
            backRight.setPower(powers[1] * motorReductionForBR);
        } else if (powers.length == 4) {
            frontLeft.setPower(powers[0]);
            frontRight.setPower(powers[1] * motorReductionForFRAndBL);
            backLeft.setPower(powers[2] * motorReductionForFRAndBL);
            backRight.setPower(powers[3] * motorReductionForBR);
        }
    }


    public void operateLeft(double leftPower) {
        frontLeft.setPower(leftPower);
        backLeft.setPower(leftPower);
    }


    public void operateRight(double rightPower) {
        frontRight.setPower(rightPower);
        backRight.setPower(rightPower);
    }


    public void switchOrientation(boolean gamepadInput) {
        orientationSwitch.changeState(gamepadInput);
    }


    public String getOrientation() {
        return orientationSwitch.currentState() == 0 ? "normal" : "reversed";
    }

    public static double getTrackWidth() {
        return TRACK_WIDTH;
    }

    public static double getWheelBase() {
        return WHEEL_BASE;
    }

    public double getLAvgEncoderPosition() {
        double leftTotal = backLeft.getCurrentPosition() + frontLeft.getCurrentPosition();
        return leftTotal * 0.5;
        //return backLeft.getCurrentPosition();
    }

    public double getRAvgEncoderPosition() {
        double rightTotal = backRight.getCurrentPosition() + frontRight.getCurrentPosition();
        return rightTotal * 0.5;
        //return backRight.getCurrentPosition();
    }

    public double[] getLRAvgEncoderPosition() {
        return new double[]{getLAvgEncoderPosition(), getRAvgEncoderPosition()};
    }

    public double[] getAllEncoderPositions() {
        double[] encoderValues = {frontLeft.getCurrentPosition(), frontRight.getCurrentPosition(), backLeft.getCurrentPosition(), backRight.getCurrentPosition()};
        return encoderValues;
    }

    public double[] getAllEncoderDelta() {
        double[] encoderDeltas = {0.0, 0.0, 0.0, 0.0};
        double[] currentEncoderValues = getAllEncoderPositions();
        for (int i = 0; i < 4; i++) {
            encoderDeltas[i] = currentEncoderValues[i] - allEncoderValues[i];
        }
        allEncoderValues = currentEncoderValues;
        return encoderDeltas;
    }

    public double[] getLRAvgEncoderDelta() {
        double currentLeft = getLAvgEncoderPosition();
        double currentRight = getRAvgEncoderPosition();

        double[] encoderDistances = {currentLeft - encoderValues[0], currentRight - encoderValues[1]};

        encoderValues[0] = currentLeft; //Change in the X Odometry Wheel
        encoderValues[1] = currentRight; //Change in the Y Odometry wheel

        return encoderDistances;
    }

    /*public double getFrontRightWheelVelocity() {
        return backRight.getVelocity();
    }*/

    public double[] getWheelVelocities() {
        double[] wheelVelocities = {encToMM(backLeft.getVelocity(AngleUnit.DEGREES)), encToMM(backRight.getVelocity(AngleUnit.DEGREES))};
        return wheelVelocities;
    }

    public double[] getAllWheelVelocities() {
        double[] wheelVelocities = {encToMM(frontLeft.getVelocity()), encToMM(frontRight.getVelocity()), encToMM(backLeft.getVelocity()), encToMM(backRight.getVelocity())};
        return wheelVelocities;
    }

    public static double encToMM(double encoderTicks) {
        return encoderTicks * (1/ENCODER_TICKS_PER_MM);
    }


    public void setRunMode(DcMotor.RunMode runMode) {
        frontLeft.setMode(runMode);
        frontRight.setMode(runMode);
        backLeft.setMode(runMode);
        backRight.setMode(runMode);
    }

    public void setZeroPowerBehavior(DcMotor.ZeroPowerBehavior zeroPowerBehavior) {
        frontLeft.setZeroPowerBehavior(zeroPowerBehavior);
        frontRight.setZeroPowerBehavior(zeroPowerBehavior);
        backLeft.setZeroPowerBehavior(zeroPowerBehavior);
        backRight.setZeroPowerBehavior(zeroPowerBehavior);
    }

    public double getAbsPowerAverage() {
        return (Math.abs(frontLeft.getPower()) + Math.abs(frontRight.getPower())) / 2;
    }

    public void operateMecanumDrive(double gamepadInputX, double gamepadInputY, double gamepadInputTurn, double heading) {
        r = Math.hypot(-gamepadInputX, -gamepadInputY);
        robotAngle = (Math.atan2(-gamepadInputY, -gamepadInputX) - Math.PI / 4);
        if (fieldCentricSwitch.currentState() == 1) {
            robotAngle -= heading * Math.PI / 180;
            double a = WHEEL_BASE / 2;
            double b = TRACK_WIDTH / 2;
            rightX = gamepadInputTurn;
            vFL = r * Math.cos(robotAngle) + rightX;
            vFR = r * Math.sin(robotAngle) - rightX;
            vBL = r * Math.sin(robotAngle) + rightX;
            vBR = r * Math.cos(robotAngle) - rightX;
        } else {
            vFL = -gamepadInputY + gamepadInputX - gamepadInputTurn;
            vFR = -gamepadInputY - gamepadInputX + gamepadInputTurn;
            vBL = -gamepadInputY - gamepadInputX - gamepadInputTurn;
            vBR = -gamepadInputY + gamepadInputX + gamepadInputTurn;
        }

        /* INVERTED
        vFL = -gamepadInputY + gamepadInputX + gamepadInputTurn;
        vFR = -gamepadInputY - gamepadInputX - gamepadInputTurn;
        vBL = -gamepadInputY - gamepadInputX + gamepadInputTurn;
        vBR = -gamepadInputY + gamepadInputX - gamepadInputTurn;
         */
        frontLeft.setPower(vFL * (1-brakePower));
        frontRight.setPower(vFR * motorReductionForFRAndBL * (1-brakePower));
        backLeft.setPower(vBL * motorReductionForFRAndBL * (1-brakePower));
        backRight.setPower(vBR * motorReductionForBR * (1-brakePower));
    }

    public void operateMecanumDriveScaled(double gamepadInputX, double gamepadInputY, double gamepadInputTurn, double heading) {
        r = Math.hypot(-Math.pow(gamepadInputX,3), -Math.pow(gamepadInputY,3));
        robotAngle = (Math.atan2(-Math.pow(gamepadInputY,3), -Math.pow(gamepadInputX,3)) - Math.PI / 4);
        if (fieldCentricSwitch.currentState() == 1) {
            robotAngle -= heading * Math.PI / 180;
            rightX = Math.pow(gamepadInputTurn,3);
            vFL = r * Math.cos(robotAngle) + rightX;
            vFR = r * Math.sin(robotAngle) - rightX;
            vBL = r * Math.sin(robotAngle) + rightX;
            vBR = r * Math.cos(robotAngle) - rightX;
        } else {
            double scaledY = Math.pow(gamepadInputY, 3);
            double scaledX = Math.pow(gamepadInputX, 3);
            double scaledTurn = Math.pow(gamepadInputTurn, 3);

            vFL = -scaledY + scaledX - scaledTurn;
            vFR = -scaledY - scaledX + scaledTurn;
            vBL = -scaledY - scaledX - scaledTurn;
            vBR = -scaledY + scaledX + scaledTurn;
        }

        /*INVERTED:
        vFL = -scaledY + scaledX + scaledTurn;
        vFR = -scaledY - scaledX - scaledTurn;
        vBL = -scaledY - scaledX + scaledTurn;
        vBR = -scaledY + scaledX - scaledTurn;
         */
        frontLeft.setPower(vFL * (1-brakePower));
        frontRight.setPower(vFR * motorReductionForFRAndBL * (1-brakePower));
        backLeft.setPower(vBL * motorReductionForFRAndBL * (1-brakePower));
        backRight.setPower(vBR * motorReductionForBR * (1-brakePower));
    }

    public void switchFieldCentric(boolean gamepadInput) {
        fieldCentricSwitch.changeState(gamepadInput);
    }

    public String getFieldCentric() {
        return fieldCentricSwitch.currentState() == 0 ? "Robot Centric" : "Field Centric";
    } //deal with later


    public double[] getMecanumEncoderDelta() {
        double currentFLBR = (frontLeft.getCurrentPosition() + backRight.getCurrentPosition()) / 2;
        double currentFRBL = (frontRight.getCurrentPosition() + backLeft.getCurrentPosition()) / 2;

        double[] encoderDistances = {currentFLBR - encoderValues[0], currentFRBL - encoderValues[1]};

        encoderValues[0] = currentFLBR;
        encoderValues[1] = currentFRBL;

        return encoderDistances;
    }

    public void resetEncoders() {
        setRunMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setRunMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void setTargetPosition(int targetPosition, double power) {
        frontLeft.setTargetPosition(targetPosition);
        frontRight.setTargetPosition(targetPosition);
        backLeft.setTargetPosition(targetPosition);
        backRight.setTargetPosition(targetPosition);

        frontLeft.setPower(power);
        frontRight.setPower(power * motorReductionForFRAndBL);
        backLeft.setPower(power * motorReductionForFRAndBL);
        backRight.setPower(power * motorReductionForBR);

    }

    public double[] getMMDeadwheelEncoderDeltas(){
        EncoderConverter deadwheelConverter = new EncoderConverter(25.4, 8192/4 , 1);

        int leftDelta = 0;
        int middleDelta = frontRight.getCurrentPosition() - deadWheelEncoderValues[1];
        int rightDelta = frontLeft.getCurrentPosition() - deadWheelEncoderValues[2];

        deadWheelEncoderValues = new int[]{0, frontRight.getCurrentPosition(), frontLeft.getCurrentPosition()};

        double leftMM = deadwheelConverter.encToMM(leftDelta);
        double middleMM = deadwheelConverter.encToMM(middleDelta);
        double rightMM = deadwheelConverter.encToMM(rightDelta);

        return new double[] {leftMM, middleMM, rightMM};
    }

    public void brake(double brakePower){
        this.brakePower = Math.min(brakePower,1);
    }
}
