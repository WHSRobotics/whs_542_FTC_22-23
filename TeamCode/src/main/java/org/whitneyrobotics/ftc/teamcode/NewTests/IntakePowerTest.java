package org.whitneyrobotics.ftc.teamcode.NewTests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.whitneyrobotics.ftc.teamcode.framework.DashboardOpMode;
import org.whitneyrobotics.ftc.teamcode.lib.util.Toggler;

@TeleOp(name="Intake Power Test", group="Tests")
public class IntakePowerTest extends DashboardOpMode {
    private DcMotorEx surgicalTubes;
    double power = 0;
    private Toggler powerSelector = new Toggler(201);

    @Override
    public void init() {
        initializeDashboardTelemetry(25);
        surgicalTubes = hardwareMap.get(DcMotorEx.class,"intakeMotor");
        surgicalTubes.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        powerSelector.setState(101); //0
    }

    @Override
    public void loop() {
        powerSelector.changeState(gamepad1.dpad_up,gamepad1.dpad_down);

        if(gamepad1.y){
            throw new RuntimeException("Emergency Stop");
        }

        if(gamepad1.b){
            surgicalTubes.setPower(0);
        } else {
            power = (powerSelector.currentState()-101)/100;
            surgicalTubes.setPower(power);
        }

        telemetry.addData("Motor Power",power);
        telemetry.addData("Motor Current in mA",surgicalTubes.getCurrent(CurrentUnit.MILLIAMPS));
        telemetry.addData("Motor Velocity in Ticks/Sec",surgicalTubes.getVelocity());
        telemetry.addData("Motor velocity in RPM",surgicalTubes.getVelocity(AngleUnit.DEGREES)/6); //degress per second to rotations per minute
    }
}
