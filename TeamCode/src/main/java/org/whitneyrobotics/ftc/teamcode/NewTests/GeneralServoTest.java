package org.whitneyrobotics.ftc.teamcode.NewTests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.whitneyrobotics.ftc.teamcode.lib.util.GamepadListener;
import org.whitneyrobotics.ftc.teamcode.lib.util.Toggler;

@TeleOp(name="New Servo Test",group="Tests")
public class GeneralServoTest extends OpMode {
    private Servo[] servos = new Servo[12];
    private String[] failedPorts = new String[12];
    private Toggler servoSelect = new Toggler(8);
    private Toggler positionTog = new Toggler(101);
    private Toggler directionTog = new Toggler(2);

    private GamepadListener gamepadListener = new GamepadListener();
    private GamepadListener gamepadListener2 = new GamepadListener();

    @Override
    public void init() {
        for(int i = 0; i<12; i++){
            try {
                servos[i] = hardwareMap.get(Servo.class,String.format("%d",i)); //it doesn't like casting ints to strings
                servos[i].setDirection(Servo.Direction.FORWARD);
            } catch (Exception e){
                failedPorts[i] = String.format("Failed to instatiate Servo on port %d",i);
                telemetry.addLine(String.format("Servo %d not plugged in or located.", i));
            }
        }
    }

    @Override
    public void loop() {
        servoSelect.changeState(gamepad1.dpad_right || gamepad2.dpad_right, gamepad1.dpad_left || gamepad2.dpad_left);
        if(servos[servoSelect.currentState()] != null){
            positionTog.changeState(gamepadListener.shortPress(gamepad1.dpad_up || gamepad2.dpad_up, 500), gamepadListener2.shortPress(gamepad1.dpad_down || gamepad2.dpad_down, 500));
            if(gamepadListener.longPress(gamepad1.dpad_up || gamepad2.dpad_up, 500)){
                positionTog.setState(positionTog.currentState() + 20);
            } else if (gamepadListener2.longPress(gamepad1.dpad_down || gamepad2.dpad_down,500)){
                positionTog.setState(positionTog.currentState() - 20);
            }

            servos[servoSelect.currentState()].setPosition((double)(positionTog.currentState())/100);

            directionTog.changeState(gamepad1.x);
            if (directionTog.currentState() == 1){
                servos[servoSelect.currentState()].setDirection(Servo.Direction.REVERSE);
            } else {
                servos[servoSelect.currentState()].setDirection(Servo.Direction.FORWARD);
            }
            telemetry.addLine("Use dpad up/down to change position. Use X to reverse.");
            telemetry.addData("Current Port Number", servos[servoSelect.currentState()].getPortNumber());
            telemetry.addData("Servo position", servos[servoSelect.currentState()].getPosition());
            telemetry.addData("Direction",servos[servoSelect.currentState()].getDirection().toString());
            telemetry.addLine();
            telemetry.addData("Manufacturer", servos[servoSelect.currentState()].getManufacturer().toString());
            telemetry.addData("Model", servos[servoSelect.currentState()].getDeviceName());
            telemetry.addData("Connection Info", servos[servoSelect.currentState()].getConnectionInfo());
        } else {
            telemetry.addLine(failedPorts[servoSelect.currentState()]);
        }
        telemetry.addLine(String.format("\n\n[%d/11]",servoSelect.currentState()));
    }
}

