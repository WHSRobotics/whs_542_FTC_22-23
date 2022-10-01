package org.whitneyrobotics.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name = "Wobble Motor Test")
public class WobbleMotorTest extends OpMode {

    public DcMotor wobbleMotor;
    int i = 0;
    int position = 0;
    @Override
    public void init() {
        wobbleMotor = hardwareMap.dcMotor.get("wobbleMotor");
        wobbleMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        wobbleMotor.setTargetPosition(0);
        wobbleMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    @Override
    public void loop() {
/*
        i++;
        if(i%20 == 0){
            if(gamepad1.a){
                position ++;
            } else if(gamepad1.b){
                position--;
            }
        }
*/
        wobbleMotor.setTargetPosition(1500);

        wobbleMotor.setPower(0.1);;
        telemetry.addData("Target Position", wobbleMotor.getTargetPosition());
        telemetry.addData("Position", wobbleMotor.getCurrentPosition());
    }
}
