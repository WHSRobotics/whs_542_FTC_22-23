package org.whitneyrobotics.ftc.teamcode.NewTests;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.whitneyrobotics.ftc.teamcode.BetterTelemetry.KeyValueLine;
import org.whitneyrobotics.ftc.teamcode.BetterTelemetry.LineItem;
import org.whitneyrobotics.ftc.teamcode.BetterTelemetry.SliderDisplayLine;
import org.whitneyrobotics.ftc.teamcode.framework.Vector;
import org.whitneyrobotics.ftc.teamcode.framework.opmodes.OpModeEx;
import org.whitneyrobotics.ftc.teamcode.lib.filters.RateLimitingFilter;

@TeleOp(name="Field Centric Test V2 Omni")
@RequiresApi(api = Build.VERSION_CODES.N)
public class FieldCentricTestV2Omni extends OpModeEx{
    public DcMotor frontLeft;
    public DcMotor frontRight;
    public DcMotor backLeft;
    public DcMotor backRight;
    public BNO055IMU imu;

    static RateLimitingFilter rotationRateLimiter = new RateLimitingFilter(0.45,0);

    public static boolean fieldCentric = true;

    @Override
    public void initInternal() {
        frontLeft = hardwareMap.dcMotor.get("driveFL");
        frontRight = hardwareMap.dcMotor.get("driveFR");
        backLeft = hardwareMap.dcMotor.get("driveBL");
        backRight = hardwareMap.dcMotor.get("driveBR");

        imu = hardwareMap.get(BNO055IMU.class,"imu");
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();

        parameters.angleUnit = BNO055IMU.AngleUnit.RADIANS;
        imu.initialize(parameters);

        betterTelemetry.addItem(new KeyValueLine("Field centric",true,() -> fieldCentric, LineItem.Color.ROBOTICS));

        betterTelemetry.addItem(new SliderDisplayLine("frontLeft", frontLeft::getPower, LineItem.Color.AQUA).setMin(-1));
        betterTelemetry.addItem(new SliderDisplayLine("frontRight", frontRight::getPower, LineItem.Color.AQUA).setMin(-1));
        betterTelemetry.addItem(new SliderDisplayLine("backLeft", backLeft::getPower, LineItem.Color.AQUA).setMin(-1));
        betterTelemetry.addItem(new SliderDisplayLine("backRight", backRight::getPower, LineItem.Color.AQUA).setMin(-1));

        gamepad1.TRIANGLE.onPress(e -> fieldCentric = !fieldCentric);
    }

    @Override
    protected void loopInternal() {

        double y = gamepad1.LEFT_STICK_Y.value();
        double x = gamepad1.LEFT_STICK_X.value() * 1.1;
        rotationRateLimiter.calculate(gamepad1.RIGHT_STICK_X.value());
        double rx = rotationRateLimiter.getOutput();

        Vector input = new Vector(x, y);

        double botHeading = -imu.getAngularOrientation().firstAngle;

        Vector transformed = input;
        if(fieldCentric){
            transformed = input.rotate(botHeading);
        }

        double rotX = -transformed.get(0,0);
        double rotY = transformed.get(1,0);

        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
        double frontLeftPower = (rotY + rotX + rx) / denominator;
        double backLeftPower = (rotY - rotX + rx) / denominator;
        double frontRightPower = (-rotY + rotX + rx) / denominator;
        double backRightPower = (-rotY - rotX + rx) / denominator;

        frontLeft.setPower(frontLeftPower);
        backLeft.setPower(backLeftPower);
        frontRight.setPower(frontRightPower);
        backRight.setPower(backRightPower);
    }
}