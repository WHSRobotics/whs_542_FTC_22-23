package org.whitneyrobotics.ftc.teamcode.tests.HardwareTests;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.whitneyrobotics.ftc.teamcode.framework.opmodes.OpModeEx;
import org.whitneyrobotics.ftc.teamcode.subsys.Drivetrains.OmniDrivetrain;
import org.whitneyrobotics.ftc.teamcode.subsys.IMU;
import org.whitneyrobotics.ftc.teamcode.tests.Test;

@RequiresApi(api = Build.VERSION_CODES.N)
@TeleOp(name="Motor Velocity Test :P", group="Hardware Tests")
@Test(name="Velocity Test", autoTerminateAfterSeconds = 1)
public class MotorVelTest extends OpModeEx {
    OmniDrivetrain drivetrain;
    IMU imu;
    @Override
    public void initInternal() {
        imu = new IMU(hardwareMap);
        drivetrain = new OmniDrivetrain(hardwareMap, imu);
    }
    private double maxVelocity = 0d;
    @Override
    protected void loopInternal() {
        drivetrain.operateByCommand(gamepad1.LEFT_STICK_X.value(), gamepad1.LEFT_STICK_Y.value(), gamepad1.RIGHT_STICK_X.value());
        maxVelocity = Math.max(Math.abs(drivetrain.getVelocity()), maxVelocity);
        betterTelemetry.addData("Motor Velocity", drivetrain.getVelocity());
        betterTelemetry.addData("Max Velocity", maxVelocity);

    }
}
