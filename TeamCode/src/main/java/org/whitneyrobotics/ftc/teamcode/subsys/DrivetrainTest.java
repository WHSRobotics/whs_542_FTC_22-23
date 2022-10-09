package org.whitneyrobotics.ftc.teamcode.subsys;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class DrivetrainTest {

    public DcMotorEx frontLeft;
    public DcMotorEx frontRight;
    public DcMotorEx backLeft;
    public DcMotorEx backRight;

    private static final double wheelRadius = 50;
    private static final double circOfWheel = wheelRadius * 2 * Math.PI;
    private static final double encoderTicksperRev = 537.6;
    private static final double gearRatio = 1.0;

    //Include encoder tick values for each wheel + gear ratio once we get values



    public class EncoderConverter {
        private double encoderTicks = 0.0;

        public EncoderConverter(double wheelRadius, double encoderTicksperRev, double gearRatio)

        double circOfWheel = wheelRadius * 2 * Math.PI;
        encoderTicks = encoderTicksperRev / (circOfWheel * gearRatio);
    }


    public static final double X_WHEEL_TO_ROBOT_CENTER = 100.0;
    public static final double Y_WHEEL_TO_ROBOT_CENTER = 100.0;

    public DrivetrainTest(HardwareMap driveMap) {
        frontLeft = driveMap.get(DcMotorEx.class, "driveFL");
        frontRight = driveMap.get(DcMotorEx.class,"driveFR");
        backLeft = driveMap.get(DcMotorEx.class,"driveBL");
        backRight = driveMap.get(DcMotorEx.class,"driveBR");

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


    }
}

