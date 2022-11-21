package org.whitneyrobotics.ftc.teamcode.tests.SoftwareTests;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.whitneyrobotics.ftc.teamcode.framework.opmodes.OpModeEx;
import org.whitneyrobotics.ftc.teamcode.lib.filters.LowPassFilter;

@Config
@RequiresApi(api = Build.VERSION_CODES.N)
@TeleOp(name = "Low Pass Filter Test", group="Low Pass Filter Test")
public class LowPassFilterTest extends OpModeEx {
    public static double smoothing = 10;
    LowPassFilter filter = new LowPassFilter(0, 1.5);
    double num = 0;

    public void initInternal() {
        gamepad1.TRIANGLE.onPress(e -> num = 0);
        initializeDashboardTelemetry(50);
    }

    public void loopInternal() {
        filter.setSmoothing(smoothing);
        num += (Math.random() * 5) * (Math.random() > 0.5 ? -1 : 1);
        filter.calculate(num);

        telemetry.addData("Unsmoothed value", num);
        telemetry.addData("Smoothed value", filter.getOutput());
    }
}