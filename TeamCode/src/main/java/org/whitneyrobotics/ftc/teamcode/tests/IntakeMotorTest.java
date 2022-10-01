package org.whitneyrobotics.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.whitneyrobotics.ftc.teamcode.lib.util.Toggler;

public class IntakeMotorTest extends OpMode{
    private DcMotor intakeTestMotor ;
    private Toggler intakeMotorTog = new Toggler(2);

    @Override
    public void init() {
        intakeTestMotor = hardwareMap.dcMotor.get("intakeArm");
    }

    @Override
    public void loop() {

        if(gamepad1.b){
            intakeTestMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            intakeTestMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }

        intakeMotorTog.changeState(gamepad1.a);
        if(intakeMotorTog.currentState() == 1){
            intakeTestMotor.setPower(1);
        } else { intakeTestMotor.setPower(0); }

        telemetry.addData("Position: ", intakeTestMotor.getCurrentPosition());
        telemetry.addData("Motor on: ", (intakeMotorTog.currentState() == 1) ? true : false);
    }

}

