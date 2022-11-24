package org.whitneyrobotics.ftc.teamcode.tests.HardwareTests;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.whitneyrobotics.ftc.teamcode.framework.opmodes.OpModeEx;
import org.whitneyrobotics.ftc.teamcode.lib.control.PIDControlledMotor;
import org.whitneyrobotics.ftc.teamcode.tests.Test;

@RequiresApi(api = Build.VERSION_CODES.N)
@TeleOp(name = "PID Controlled Motor Test", group="Hardware Tests")
@Config
@Test(name="PID Controlled Motor Test")
public class PIDControlledMotorTest extends OpModeEx {
    public static double TARGET_VEL, kI, kD;
    public static double kP = 1.0d;

    PIDControlledMotor motor;
    @Override
    public void initInternal() {
        gamepad1.TRIANGLE.onPress(e -> {
            motor.reloadCoefficients(kP, kI, kD);
        });
        motor = new PIDControlledMotor(hardwareMap.get(DcMotorEx.class, "driveFL"), 3000);
        initializeDashboardTelemetry(50);
        betterTelemetry.useDashboardTelemetry(dashboardTelemetry);
    }

    @Override
    public void startInternal() {
        motor.reset();
    }

    @Override
    protected void loopInternal() {
        motor.setTargetVelocity(TARGET_VEL);
        motor.update();
        betterTelemetry.addData("target",TARGET_VEL);
        betterTelemetry.addData("current",motor.getMotor().getVelocity(AngleUnit.RADIANS));
        betterTelemetry.addData("output",motor.controller.getOutput());
    }
}
