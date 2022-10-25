package org.whitneyrobotics.ftc.teamcode.tests;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorImpl;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.LED;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.whitneyrobotics.ftc.teamcode.framework.opmodes.OpModeEx;
import org.whitneyrobotics.ftc.teamcode.lib.Hardware.DigitalLedIndicator;

@TeleOp(name="Sensor Test")
@RequiresApi(api = Build.VERSION_CODES.N)
public class SensorTest extends OpModeEx {
    public DigitalChannel green, red;
    public DigitalLedIndicator led;
    public RevColorSensorV3 color;
    public Rev2mDistanceSensor distance;
    public TouchSensor magnet;
    public DcMotor encoder;

    @Override
    public void initInternal() {
        green = hardwareMap.get(DigitalChannel.class, "green");
        red = hardwareMap.get(DigitalChannel.class, "red");
        led = new DigitalLedIndicator(red, green);
        color = hardwareMap.get(RevColorSensorV3.class, "color");
        distance = hardwareMap.get(Rev2mDistanceSensor.class, "distance");
        magnet = hardwareMap.get(TouchSensor.class, "magnet");
        encoder = hardwareMap.get(DcMotor.class, "encoder");

        led.off();

        gamepad1.TRIANGLE.onPress(e -> green.setState(!green.getState()));
        gamepad1.SQUARE.onPress(e -> red.setState(!red.getState()));
    }

    @Override
    protected void loopInternal() {
        telemetry.addData("distance", distance.getDistance(DistanceUnit.CM));
        telemetry.addData("color",color.getLightDetected());
        telemetry.addData("color distance", color.getDistance(DistanceUnit.CM));
        telemetry.addData("hall effect", magnet.isPressed());
        telemetry.addData("encoder",encoder.getCurrentPosition());
    }
}
