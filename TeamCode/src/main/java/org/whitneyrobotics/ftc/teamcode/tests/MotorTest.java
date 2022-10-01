package org.whitneyrobotics.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.whitneyrobotics.ftc.teamcode.lib.util.Toggler;

@TeleOp(name="Motor Test",group="Tests")
public class MotorTest extends OpMode {

    private DcMotor testingMotor;
    private Toggler motorTog = new Toggler(2);

    @Override
    public void init() {
        testingMotor = hardwareMap.dcMotor.get("driveFL");
    }

    @Override
    public void loop() {

        if(gamepad1.b){
            testingMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            testingMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }

        motorTog.changeState(gamepad1.a);
        if(motorTog.currentState() == 1){
            testingMotor.setPower(1);
        } else { testingMotor.setPower(0); }

        telemetry.addData("Position: ", testingMotor.getCurrentPosition());
        telemetry.addData("Motor on: ", (motorTog.currentState() == 1) ? true : false);
    }

}
