package org.whitneyrobotics.ftc.teamcode.NewTests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.whitneyrobotics.ftc.teamcode.lib.util.Toggler;

@TeleOp(name = "Motor Sus Test")
public class DrivetrainMotorTest extends OpMode {
    private Toggler speedTog = new Toggler(4);
    private double[] powers = {0.25,0.5,0.75,1};
    public DcMotorEx frontLeft;
    public DcMotorEx frontRight;
    public DcMotorEx backLeft;
    public DcMotorEx backRight;

    double frontLeftVelocity = 0;
    double frontRightVelocity = 0;
    double backRightVelocity = 0;
    double backLeftVelocity = 0;


    @Override
    public void init() {
        frontLeft = hardwareMap.get(DcMotorEx.class, "driveFL");
        frontRight = hardwareMap.get(DcMotorEx.class, "driveFR");
        backLeft = hardwareMap.get(DcMotorEx.class, "driveBL");
        backRight = hardwareMap.get(DcMotorEx.class, "driveBR");

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        //leftOdometry.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        //leftOdometry.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    @Override
    public void loop() {
        speedTog.changeState(gamepad1.left_bumper);
        double power = powers[speedTog.currentState()];

        if(gamepad1.right_bumper){
            frontLeftVelocity = 0;
            frontRightVelocity = 0;
            backLeftVelocity = 0;
            backRightVelocity = 0;
        }

        if (gamepad1.a) {
            frontLeft.setPower(power);
            frontLeft.getVelocity();
        } else {
            frontLeft.setPower(0);
        }
        if (frontLeft.getVelocity() > frontLeftVelocity) {
            frontLeftVelocity = frontLeft.getVelocity();
        }
        if (gamepad1.b) {
            frontRight.setPower(power);
            } else {
                frontRight.setPower(0);
        }
        if (frontRight.getVelocity() > frontRightVelocity) {
            frontRightVelocity = frontRight.getVelocity();
        }
            if (gamepad1.y) {
                backLeft.setPower(power);
            } else {
                backLeft.setPower(0);
            }
            if (backLeft.getVelocity() > backLeftVelocity) {
                backLeftVelocity = backLeft.getVelocity();
            }


            if (gamepad1.x) {
                backRight.setPower(power);
            } else {
                backRight.setPower(0);
            }
            if (backRight.getVelocity() > backRightVelocity) {
                backRightVelocity = backRight.getVelocity();
            }

        telemetry.addData("Motor power",power);
        telemetry.addData("FL",(frontLeft.getPower() > 0 ? true : false));
        telemetry.addData("FR",(frontRight.getPower() > 0 ? true : false));
        telemetry.addData("BL",(backLeft.getPower() > 0 ? true : false));
        telemetry.addData("BR",(backRight.getPower() > 0 ? true : false));

        telemetry.addData("Highest Velocity FL", frontLeftVelocity);
        telemetry.addData("Highest Velocity FR", frontRightVelocity);
        telemetry.addData("Highest Velocity BL", backLeftVelocity);
        telemetry.addData("Highest Velocity BR", backRightVelocity);
    }
}