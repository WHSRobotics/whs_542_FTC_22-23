package org.whitneyrobotics.ftc.teamcode.NewTests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.whitneyrobotics.ftc.teamcode.lib.util.GamepadListener;
import org.whitneyrobotics.ftc.teamcode.lib.util.Toggler;

@TeleOp(name="New Motor Test",group="Tests")
public class GeneralMotorTest extends OpMode {
    private DcMotorEx[] motors = new DcMotorEx[8];
    private String[] failedPorts = new String[8];
    private Toggler motorTog = new Toggler(8);
    private Toggler powerTog = new Toggler(201);
    private Toggler onOffTog = new Toggler(2);
    private Toggler directionTog = new Toggler(2);
    private GamepadListener gamepadListener = new GamepadListener();
    private GamepadListener gamepadListener2 = new GamepadListener();

    @Override
    public void init() {
        for(int i = 0; i<8; i++){
            try {
                motors[i] = hardwareMap.get(DcMotorEx.class,String.format("%d",i)); //it doesn't like casting ints to strings
                motors[i].setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            } catch (Exception e){
                failedPorts[i] = String.format("Failed to instatiate DcMotor on port %d",i);
            }
        }
        powerTog.setState(101);
    }

    @Override
    public void loop() {
        motorTog.changeState(gamepad1.dpad_right || gamepad2.dpad_right, gamepad1.dpad_left || gamepad2.dpad_left);
        if(motors[motorTog.currentState()] != null){
            powerTog.changeState(gamepadListener.shortPress(gamepad1.dpad_up || gamepad2.dpad_up,500), gamepadListener2.shortPress(gamepad1.dpad_down||gamepad2.dpad_down,500));
            if (gamepadListener.longPress(gamepad1.dpad_up || gamepad2.dpad_up,500)){
                powerTog.setState(powerTog.currentState() + 20);
            }
            if (gamepadListener2.longPress(gamepad1.dpad_down || gamepad2.dpad_down,500)){
                powerTog.setState(powerTog.currentState() - 20);
            }
            onOffTog.changeState(gamepad1.y || gamepad2.y);
            if (onOffTog.currentState() == 0){
                motors[motorTog.currentState()].setPower(0);
            } else {
                motors[motorTog.currentState()].setPower((double)(powerTog.currentState()-100)/100);
            }

            directionTog.changeState(gamepad1.x);
            if(gamepad1.x){ //so we're not constantly setting direction, idk what influence this has
                if(directionTog.currentState() == 0){
                    motors[motorTog.currentState()].setDirection(DcMotorSimple.Direction.FORWARD);
                } else {
                    motors[motorTog.currentState()].setDirection(DcMotorSimple.Direction.REVERSE);
                }
            }

            if(gamepad1.left_bumper){
                motors[motorTog.currentState()].setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            } else if(gamepad1.right_bumper){
                motors[motorTog.currentState()].setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            } else if(gamepad1.right_trigger > 0.05){
                motors[motorTog.currentState()].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            }

            telemetry.addLine("Enable/disable motor with y. Use dpad up/down to change motor powers. Use  x to change direction. Left bumper to run without encoder, right bumper to run with encoder, and right trigger to reset encoder.");
            telemetry.addData("Current Port Number",motors[motorTog.currentState()].getPortNumber());
            telemetry.addData("Motor power",motors[motorTog.currentState()].getPower());
            telemetry.addData("Motor Direction",motors[motorTog.currentState()].getDirection().toString());
            telemetry.addData("Encoder position",motors[motorTog.currentState()].getCurrentPosition());
            telemetry.addData("Velocity in RPM",motors[motorTog.currentState()].getVelocity(AngleUnit.DEGREES)/6); //degrees per second to rpm
            telemetry.addData("Current in mAh",motors[motorTog.currentState()].getCurrent(CurrentUnit.MILLIAMPS));
            telemetry.addLine();
            telemetry.addData("Manufacturer",motors[motorTog.currentState()].getManufacturer().toString());
            telemetry.addData("Model",motors[motorTog.currentState()].getDeviceName());
            telemetry.addData("Connection Info",motors[motorTog.currentState()].getConnectionInfo());
        } else {
            telemetry.addLine(failedPorts[motorTog.currentState()]);
        }
        telemetry.addLine(String.format("\n\n[%d/7]",motorTog.currentState()));
    }
}
