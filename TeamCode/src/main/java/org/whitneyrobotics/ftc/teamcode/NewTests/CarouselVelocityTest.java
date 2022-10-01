package org.whitneyrobotics.ftc.teamcode.NewTests;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.whitneyrobotics.ftc.teamcode.framework.DashboardOpMode;

@Config
@TeleOp(name="carousel velocity test",group="new tests")
public class CarouselVelocityTest extends DashboardOpMode {
    DcMotorEx wheel;
    public static double power = 0.4;

    @Override
    public void init() {
        initializeDashboardTelemetry(25);
        wheel = hardwareMap.get(DcMotorEx.class, "carouselMotor");
    }

    @Override
    public void loop() {
        wheel.setPower(power);
        telemetry.addData("Wheel velocity RPM", wheel.getVelocity());
    }
}
