package org.whitneyrobotics.ftc.teamcode.NewTests;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.whitneyrobotics.ftc.teamcode.BetterTelemetry.LineItem;
import org.whitneyrobotics.ftc.teamcode.BetterTelemetry.ProgressBarLine;
import org.whitneyrobotics.ftc.teamcode.BetterTelemetry.SliderDisplayLine;
import org.whitneyrobotics.ftc.teamcode.framework.opmodes.OpModeEx;
import org.whitneyrobotics.ftc.teamcode.subsys.IMU;


@RequiresApi(api = Build.VERSION_CODES.N)
public class FieldSensorDriveTrainTest  extends OpModeEx{
    public DcMotor frontLeft;
    public DcMotor frontRight;
    public DcMotor backLeft;
    public DcMotor backRight;
    public BNO055IMU imu;

    @Override
    public void initInternal() {
        DcMotor frontLeft = hardwareMap.dcMotor.get("driveFl");
        DcMotor frontRight = hardwareMap.dcMotor.get("driveFR");
        DcMotor backLeft = hardwareMap.dcMotor.get("driveBL");
        DcMotor backRight = hardwareMap.dcMotor.get("driveBR");

        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);

        imu = hardwareMap.get(BNO055IMU.class,"IMU");
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();

        parameters.angleUnit = BNO055IMU.AngleUnit.RADIANS;
        imu.initialize(parameters);

        betterTelemetry.addItem(new SliderDisplayLine("frontLeft", frontLeft::getPower, LineItem.Color.AQUA));
        betterTelemetry.addItem(new SliderDisplayLine("frontRight", frontRight::getPower, LineItem.Color.AQUA));
        betterTelemetry.addItem(new SliderDisplayLine("backLeft", backLeft::getPower, LineItem.Color.AQUA));
        betterTelemetry.addItem(new SliderDisplayLine("backRight", backRight::getPower, LineItem.Color.AQUA));

    }

    @Override
    protected void loopInternal() {



    }
}
