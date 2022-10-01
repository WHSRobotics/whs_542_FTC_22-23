package org.whitneyrobotics.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.whitneyrobotics.ftc.teamcode.lib.util.Toggler;
/**
 * Created by Sid Shah on 11/23/2021.
 */
@TeleOp(name="Intake Motor Velocity Test", group = "Tests")
public class IntakeMotorSpeedTest extends OpMode {
    private DcMotor intakeTestMotor;
    private final Toggler intakeMotorTog = new Toggler(2);
    private double motorPower = 0.0;
    private final Toggler reversed = new Toggler(2);
    @Override
    public void init() {
        intakeTestMotor = hardwareMap.dcMotor.get("intakeArm");
    }

    @Override
    public void loop() {

        reversed.changeState(gamepad1.left_bumper);
        int direction = reversed.currentState() == 1 ? -1 : 1;

        if (gamepad1.right_bumper) {
            motorPower = direction * (1 - gamepad1.right_trigger);
        }

        if (gamepad1.a) {
            intakeTestMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            intakeTestMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }

        intakeMotorTog.changeState(gamepad1.b);

        if(intakeMotorTog.currentState() == 1 ){
            intakeTestMotor.setPower(motorPower);
        } else {
            intakeTestMotor.setPower(0);
        }

        telemetry.addData("Position: ", intakeTestMotor.getCurrentPosition());
        telemetry.addData("Motor on: ", intakeMotorTog.currentState() == 1);
        telemetry.addData("Velocity: ", (motorPower));
    }

}