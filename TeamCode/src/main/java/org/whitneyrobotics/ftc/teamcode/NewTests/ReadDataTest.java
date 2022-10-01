package org.whitneyrobotics.ftc.teamcode.NewTests;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.whitneyrobotics.ftc.teamcode.lib.util.DataNotFoundException;
import org.whitneyrobotics.ftc.teamcode.lib.util.DataToolsLite;

import java.util.Arrays;

@Autonomous(name="Data Parse Test",group="Tests")
public class ReadDataTest extends OpMode {
    @Override
    public void init() {

    }

    @Override
    public void loop() {
        try {
            telemetry.addData("Output", Arrays.toString(DataToolsLite.decode("autoConfig.txt")));
        } catch (DataNotFoundException e) {
            telemetry.addLine("Cannot find data file");
        }
    }
}
