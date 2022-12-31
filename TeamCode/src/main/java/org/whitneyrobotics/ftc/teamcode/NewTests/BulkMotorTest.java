package org.whitneyrobotics.ftc.teamcode.NewTests;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.whitneyrobotics.ftc.teamcode.BetterTelemetry.KeyValueLine;
import org.whitneyrobotics.ftc.teamcode.BetterTelemetry.LineItem;
import org.whitneyrobotics.ftc.teamcode.BetterTelemetry.SeparatorLine;
import org.whitneyrobotics.ftc.teamcode.BetterTelemetry.SliderDisplayLine;
import org.whitneyrobotics.ftc.teamcode.BetterTelemetry.TextLine;
import org.whitneyrobotics.ftc.teamcode.framework.opmodes.OpModeEx;
import org.whitneyrobotics.ftc.teamcode.lib.util.GamepadListener;
import org.whitneyrobotics.ftc.teamcode.lib.util.Toggler;

@TeleOp(name="New Motor Test",group="Tests")
@RequiresApi(api = Build.VERSION_CODES.N)
public class BulkMotorTest extends OpModeEx {
    private DcMotorEx[] motors = new DcMotorEx[8];
    private String[] failedPorts = new String[8];
    private Toggler motorTog = new Toggler(8);
    private Toggler powerTog = new Toggler(201);
    private Toggler onOffTog = new Toggler(2);
    private Toggler directionTog = new Toggler(2);
    private GamepadListener gamepadListener = new GamepadListener();
    private GamepadListener gamepadListener2 = new GamepadListener();

    private int power = 100;
    private boolean enabled = false;
    private int motor = 0;
    private boolean forward = false;

    void setupHandlers(){
        gamepad1.DPAD_RIGHT.onPress(e -> {
            motor = (motor + 1) % 8;
            power = 100;
            enabled = false;
        }
        );
        gamepad1.DPAD_LEFT.onPress(e -> {
            motor = (motor - 1) % 8;
            power = 100;
            enabled = false;
        });
        gamepad1.DPAD_UP.onPress(e -> power = power + (gamepad1.CROSS.value() ? 20 : 1) % 201);
        gamepad1.DPAD_DOWN.onPress(e -> power = power - (gamepad1.CROSS.value() ? 20 : 1) % 201);
        gamepad1.Y.onPress(e -> enabled = !enabled);
        gamepad1.X.onPress(e -> forward = !forward);
        gamepad1.RIGHT_STICK_DOWN.onPress(e -> betterTelemetry.toggleLineNumbers());
        gamepad1.BUMPER_LEFT.onPress(e -> motors[motor].setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER));
        gamepad1.BUMPER_RIGHT.onPress(e -> motors[motor].setMode(DcMotor.RunMode.RUN_USING_ENCODER));
    }

    void setupTelemetry(){
        betterTelemetry.addItem(new TextLine("Enable/disable motor with y. Use dpad up/down to change motor powers. Use  x to change direction. Left bumper to run without encoder, right bumper to run with encoder, and right trigger to reset encoder.", true, LineItem.Color.OLIVE, LineItem.RichTextFormat.ITALICS));
        betterTelemetry.addData("Port", motor).persistent();
        betterTelemetry.addItem(new KeyValueLine("Current port number",true,motors[motor]::getPortNumber, LineItem.Color.WHITE));
        betterTelemetry.addItem(new KeyValueLine("Current motor power",true,() -> (double)(power-100)/100, LineItem.Color.TEAL));
        betterTelemetry.addItem(new KeyValueLine("Current motor direction",true,() -> motors[motor].getDirection().toString(), LineItem.Color.PURPLE));
        betterTelemetry.addItem(new KeyValueLine("Encoder reading",true,motors[motor]::getCurrentPosition, LineItem.Color.LIME));
        betterTelemetry.addItem(new SliderDisplayLine("Velocity in (Rad/sec)", () -> motors[motor].getVelocity(AngleUnit.RADIANS), LineItem.Color.AQUA).setScale(500));
        betterTelemetry.addItem(new KeyValueLine("Current mAh",true,() -> motors[motor].getCurrent(CurrentUnit.MILLIAMPS), LineItem.Color.ROBOTICS));
        betterTelemetry.addItem(new SeparatorLine("blank", SeparatorLine.LineStyle.SOLID));
        betterTelemetry.addItem(new KeyValueLine("Current motor mode",true,() -> motors[motor].getMode().toString(), LineItem.Color.WHITE));
    }

    @Override
    public void initInternal() {
        for(int i = 0; i<8; i++){
            try {
                motors[i] = hardwareMap.get(DcMotorEx.class,String.format("%d",i)); //it doesn't like casting ints to strings
                motors[i].setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            } catch (Exception e){
                failedPorts[i] = String.format("Failed to instantiate DcMotor on port %d",i);
            }
        }
        powerTog.setState(101);
        setupHandlers();
        setupTelemetry();
    }

    @Override
    public void loopInternal() {
        if(motors[motor] != null){
            if (enabled){
                motors[motor].setPower(0);
            } else {
                motors[motor].setPower((double)(power-100)/100);
            }

            if(gamepad1.X.value()){ //so we're not constantly setting direction, idk what influence this has
                if(forward){
                    motors[motor].setDirection(DcMotorSimple.Direction.FORWARD);
                } else {
                    motors[motor].setDirection(DcMotorSimple.Direction.REVERSE);
                }
            }

            if(gamepad1.BUMPER_LEFT.value()){
                motors[motorTog.currentState()].setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            } else if(gamepad1.BUMPER_LEFT.value()){
                motors[motorTog.currentState()].setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            } else if(gamepad1.RIGHT_TRIGGER.value() > 0.05){
                motors[motorTog.currentState()].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            }

            /*telemetry.addLine("Enable/disable motor with y. Use dpad up/down to change motor powers. Use  x to change direction. Left bumper to run without encoder, right bumper to run with encoder, and right trigger to reset encoder.");
            telemetry.addData("Current Port Number",motors[motorTog.currentState()].getPortNumber());
            telemetry.addData("Motor power",motors[motorTog.currentState()].getPower());
            telemetry.addData("Motor Direction",motors[motorTog.currentState()].getDirection().toString());
            telemetry.addData("Encoder position",motors[motorTog.currentState()].getCurrentPosition());
            telemetry.addData("Velocity in RPM",motors[motorTog.currentState()].getVelocity(AngleUnit.DEGREES)/6); //degrees per second to rpm
            telemetry.addData("Current in mAh",motors[motorTog.currentState()].getCurrent(CurrentUnit.MILLIAMPS));
            telemetry.addLine();
            */
            betterTelemetry.addData("Manufacturer",motors[motor].getManufacturer().toString());
            betterTelemetry.addData("Model",motors[motor].getDeviceName());
            betterTelemetry.addData("Connection Info",motors[motor].getConnectionInfo());
        } else {
            betterTelemetry.addLine(failedPorts[motor]);
        }
        betterTelemetry.addLine(String.format("\n\n[%d/7]",motorTog.currentState()));
    }
}
