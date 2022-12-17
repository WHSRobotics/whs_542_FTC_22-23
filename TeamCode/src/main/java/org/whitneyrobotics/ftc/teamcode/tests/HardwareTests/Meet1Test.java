package org.whitneyrobotics.ftc.teamcode.tests.HardwareTests;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.whitneyrobotics.ftc.teamcode.GamepadEx.Button;
import org.whitneyrobotics.ftc.teamcode.framework.opmodes.OpModeEx;
import org.whitneyrobotics.ftc.teamcode.tests.Test;

@RequiresApi(api = Build.VERSION_CODES.N)
@Test(name="Meet 1 Test")
@Config
@TeleOp(name="Meet 1 Hardware Test",group="Hardware Tests")
public class Meet1Test extends OpModeEx {
    DcMotorEx fL, fR, bL, bR, sR, sL;
    Servo gate;
    BNO055IMU imu;
    public static double testPower = 0.25;
    double servoPosition = 0.0d;

    @Override
    public void initInternal() {
        gamepad1.DPAD_UP.onPress(e->servoPosition+=0.01);
        gamepad1.DPAD_DOWN.onPress(e->servoPosition-=0.01);

        fL = hardwareMap.get(DcMotorEx.class, "driveFL");
        fL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        fR = hardwareMap.get(DcMotorEx.class, "driveFR");
        fR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        bL = hardwareMap.get(DcMotorEx.class, "driveBL");
        bL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        bR = hardwareMap.get(DcMotorEx.class, "driveBR");
        bR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        sR = hardwareMap.get(DcMotorEx.class, "slidesR");
        sR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        sR.setDirection(DcMotorSimple.Direction.REVERSE);
        sL = hardwareMap.get(DcMotorEx.class, "slidesL");
        sL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        sL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        sL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        sR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        gate = hardwareMap.get(Servo.class,"gate");
        imu = hardwareMap.get(BNO055IMU.class,"imu");

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        imu.initialize(parameters);
    }

    public void testMotor(Button b, DcMotor m, String motor){
        if(b.value()){
            m.setPower(testPower);
            betterTelemetry.addLine("Motor " + motor + " running.");
        } else {
            m.setPower(0);
        }
    }

    @Override
    protected void loopInternal() {
        testMotor(gamepad1.CROSS,fL,"fL");
        testMotor(gamepad1.CIRCLE,fR,"fR");
        testMotor(gamepad1.SQUARE,bL,"bL");
        testMotor(gamepad1.TRIANGLE,bR,"bR");

        sL.setPower(gamepad1.LEFT_STICK_Y.value());
        sR.setPower(gamepad1.LEFT_STICK_Y.value());
        gate.setPosition(servoPosition);

        betterTelemetry.addData("servo position",servoPosition);
        betterTelemetry.addData("sL reading",sL.getCurrentPosition());
        betterTelemetry.addData("sR reading", sR.getCurrentPosition());

        betterTelemetry.addData("first angle",imu.getAngularOrientation().firstAngle);
        betterTelemetry.addData("second angle",imu.getAngularOrientation().secondAngle);
        betterTelemetry.addData("third angle",imu.getAngularOrientation().thirdAngle);
        betterTelemetry.addData("slidesVelocity",sL.getVelocity(AngleUnit.RADIANS));

    }
}
