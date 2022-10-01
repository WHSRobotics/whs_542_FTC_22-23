package org.whitneyrobotics.ftc.teamcode.NewTests;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.whitneyrobotics.ftc.teamcode.framework.DashboardOpMode;

@Config
@TeleOp(name="Intake Manual Power Test", group="New Tests")
public class IntakePowerTestJank extends DashboardOpMode {
    private double intakePower = 1;
    private DcMotor intake;

    @Override
    public void init() {
        intake = hardwareMap.get(DcMotor.class, "intakeMotor");
    }

    @Override
    public void loop() {
        intake.setPower(intakePower);
    }
}
