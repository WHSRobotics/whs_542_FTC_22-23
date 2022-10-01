package org.whitneyrobotics.ftc.teamcode.visionImpl;

import org.whitneyrobotics.ftc.teamcode.framework.DashboardOpMode;
import org.whitneyrobotics.ftc.teamcode.lib.util.DataToolsLite;
import org.whitneyrobotics.ftc.teamcode.lib.util.SelectionMenu;

import java.util.Arrays;

public class VisionConfig extends DashboardOpMode {
    private SelectionMenu config;
    private boolean saved = false;
    private String fileName = "visionConfig.txt";

    @Override
    public void init() {
        SelectionMenu.Prompt resolution = new SelectionMenu.Prompt("Select Frame Resolution")
                .addSelection("640x360","640x360")
                .addSelection("1280x720","1280x720")
                .addSelection("1920x1080","1920x1080");
        SelectionMenu.Slider webcamTimeout = new SelectionMenu.Slider("Timeout duration (ms)",100,10100)
                .setStep(100);
        config = new SelectionMenu("Vision Config", resolution,webcamTimeout)
                .addInstructions("Use pad left/dpad right to navigate between selections, and up/down to make a choice. A+B to save");
    }

    @Override
    public void loop() {
        config.run(gamepad1.dpad_right||gamepad2.dpad_right,gamepad1.dpad_left||gamepad2.dpad_left,gamepad1.dpad_up||gamepad2.dpad_up,gamepad1.dpad_down||gamepad2.dpad_down);
        if(gamepad1.a && gamepad1.b || gamepad2.a && gamepad2.b){
            saved = true;
            DataToolsLite.encode(fileName,config.getOutputs());
        }

        if(config.hasChanged()){
            saved = false;
        }

        telemetry.addLine(config.formatDisplay());
        telemetry.addData("Preview", Arrays.toString(config.getOutputs()));
        telemetry.addData("Saved",saved);
    }
}
