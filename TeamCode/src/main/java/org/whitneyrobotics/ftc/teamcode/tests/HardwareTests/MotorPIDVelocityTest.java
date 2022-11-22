package org.whitneyrobotics.ftc.teamcode.tests.HardwareTests;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.whitneyrobotics.ftc.teamcode.MotionProfile.MotionProfileTrapezoidal;
import org.whitneyrobotics.ftc.teamcode.framework.DashboardOpMode;
import org.whitneyrobotics.ftc.teamcode.framework.opmodes.OpModeEx;
import org.whitneyrobotics.ftc.teamcode.lib.control.PIDControlledMotor;
import org.whitneyrobotics.ftc.teamcode.tests.Test;

@RequiresApi(api = Build.VERSION_CODES.N)
@TeleOp(name="Motor PID Velocity Test", group = "PID Test")
@Test(name="PIDMotor Vel Test")
@Config
public class MotorPIDVelocityTest extends OpModeEx {
    public static double kP = 1;
    public static double kI = 0;
    public static double kD = 0;

    /**
     * User defined init method
     * <p>
     * This method will be called once when the INIT button is pressed.
     */
    private final double maxVelocity = 2000;
    private final int ticksToInch = 300;
    private int targetPosition = 50;
    MotionProfileTrapezoidal motionProfile = new MotionProfileTrapezoidal(0, 100, maxVelocity);
    public PIDControlledMotor ilMottore; // Dottore is so cool!!
    public boolean secondPass = false;

    DcMotorEx motor;

    void setupGamepads(){
        gamepad1.Y.onPress(e -> ilMottore.reloadCoefficients(kP, kI, kD));
    }

    @Override
    public void initInternal() {
        setupGamepads();
        motor = hardwareMap.get(DcMotorEx.class,"driveFL");
        ilMottore = new PIDControlledMotor(motor, maxVelocity);
        initializeDashboardTelemetry(50);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        //motor.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public double getPosition() {
        return -ilMottore.getMotor().getCurrentPosition()/ticksToInch;
    }


    @Override
    public void startInternal() {
        ilMottore.reset();
    }

    /**
     * User defined loop method
     * <p>
     * This method will be called repeatedly in a loop while this op mode is running
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void loopInternal() {
        double targetVel = 0;
        if (motionProfile.isFinished() && secondPass) {
            betterTelemetry.addLine("Completed. Hopefully.");
        }
        //        if (gamepad1.left_bumper) {
        //            ilMottore.reloadCoefficients();
        //        }
        else {
            betterTelemetry.addLine("code is runing");
            targetVel = motionProfile.calculate(getPosition(), targetPosition);
            ilMottore.setTargetVelocity(targetVel * (50.8/72)); //inches / sec to radians/sec
            if (motionProfile.isFinished()) {
                targetPosition = 0;
                secondPass = true;
                motionProfile = new MotionProfileTrapezoidal(5, 2, maxVelocity);
            }
            ilMottore.update();
        }
        try {
            betterTelemetry.addData("Current Pos", getPosition());
            betterTelemetry.addData("Target Pos", targetPosition);
            betterTelemetry.addData("Current Velocity", ilMottore.getMotor().getVelocity());
            betterTelemetry.addData("Target Velocity", targetVel * (50.8/72));
            betterTelemetry.addData("Output", ilMottore.controller.getOutput());
            betterTelemetry.addData("Integral", ilMottore.controller.getIntegral());
            betterTelemetry.addData("Derivative", ilMottore.controller.getDerivative());
        } catch (NullPointerException e) {
            telemetry.addLine("something doesn't exist");
        }
    }
}
