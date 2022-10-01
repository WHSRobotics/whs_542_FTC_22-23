package org.whitneyrobotics.ftc.teamcode.NewTests;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.VoltageSensor;

@TeleOp(name="Voltage Test")
@Disabled
public class Voltage extends OpMode {
    private VoltageSensor voltageSensor;

    @Override
    public void init() {
        //voltageSensor = hardwareMap.get(VoltageSensor.class, "Motor Controller 1");
    }

    @Override
    public void loop() {
        telemetry.addData("voltage", getBatteryVoltage());
    }

    public double getBatteryVoltage() {
        double result = Double.POSITIVE_INFINITY;
        for (VoltageSensor sensor : hardwareMap.voltageSensor) {
            double voltage = sensor.getVoltage();
            if (voltage > 0) {
                result = Math.min(result, voltage);
            }
        }
        return result;
    }
}
