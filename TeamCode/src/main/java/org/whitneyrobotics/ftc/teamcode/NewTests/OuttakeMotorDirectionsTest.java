package org.whitneyrobotics.ftc.teamcode.NewTests;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.whitneyrobotics.ftc.teamcode.framework.DashboardOpMode;
import org.whitneyrobotics.ftc.teamcode.lib.util.Toggler;
import org.whitneyrobotics.ftc.teamcode.subsys.Outtake;

@TeleOp(name="Outtake Direction Test", group="New Tests")
public class OuttakeMotorDirectionsTest extends DashboardOpMode {
    private DcMotorEx left, right;
    private Toggler leftDirectionTog = new Toggler(2);
    private Toggler rightDirectionTog = new Toggler(2);
    public Outtake outtake;

    @Override
    public void init() {
        initializeDashboardTelemetry(25);
        //left = hardwareMap.get(DcMotorEx.class, "outtakeMotorLeft");
        //right = hardwareMap.get(DcMotorEx.class, "outtakeMotorRight");
        outtake = new Outtake(hardwareMap);
    }

    @Override
    public void loop() {
        outtake.linearSlidesLeft.setPower(-gamepad1.left_stick_y/2);
        outtake.linearSlidesRight.setPower(-gamepad1.right_stick_y/2);
        leftDirectionTog.changeState(gamepad1.x);
        rightDirectionTog.changeState(gamepad1.b);
        if (leftDirectionTog.currentState() == 1){
            outtake.linearSlidesLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        } else {
            outtake.linearSlidesLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        }

        if(rightDirectionTog.currentState() == 1){
            outtake.linearSlidesRight.setDirection(DcMotorSimple.Direction.REVERSE);
        } else {
            outtake.linearSlidesRight.setDirection(DcMotorSimple.Direction.FORWARD);
        }

        if(gamepad1.left_bumper) {
            outtake.linearSlidesLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            outtake.linearSlidesLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
        if(gamepad1.right_bumper){
            outtake.linearSlidesRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            outtake.linearSlidesRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }

        telemetry.addData("Left Direction",outtake.linearSlidesLeft.getDirection().name());
        telemetry.addData("Right Direction",outtake.linearSlidesRight.getDirection().name());
        telemetry.addData("Average Encoder",outtake.getSlidesPosition());
        telemetry.addData("Left Encoder",outtake.linearSlidesLeft.getCurrentPosition());
        telemetry.addData("Right Encoder Direction",outtake.linearSlidesRight.getCurrentPosition());
    }
}
