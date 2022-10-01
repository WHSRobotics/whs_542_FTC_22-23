package org.whitneyrobotics.ftc.teamcode.menus;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.whitneyrobotics.ftc.teamcode.lib.util.DataToolsLite;
import org.whitneyrobotics.ftc.teamcode.lib.util.SelectionMenu;

import java.util.Arrays;

@TeleOp(name="Autonomous Configuration", group="Config")
public class ConfigureAuto extends OpMode {
    private SelectionMenu autoConfig;
    private boolean saved = false;
    private String fileName = "autoConfig.txt";

    @Override
    public void init() {
        SelectionMenu.Prompt alliance = new SelectionMenu.Prompt("Alliance Color")
                .addSelection("RED",0)
                .addSelection("BLUE",1);
        SelectionMenu.Prompt startingPosition = new SelectionMenu.Prompt("Starting Position")
                .addSelection("BOTTOM",0)
                .addSelection("TOP",1);
        SelectionMenu.Prompt scanEnabled = new SelectionMenu.Prompt("Use Webcam to Scan Barcode [Disabled]")
                .addSelection("YES",true)
                .addSelection("NO",false);
        SelectionMenu.Prompt rotateCarousel = new SelectionMenu.Prompt("Rotate Carousel in Auto: ")
                .addSelection("On",true)
                .addSelection("Off", false);
        SelectionMenu.Prompt doShippingHub = new SelectionMenu.Prompt("Deposit Pre-Load: ")
                .addSelection("On", true)
                .addSelection("Off",false);
        SelectionMenu.Prompt doWarehouse = new SelectionMenu.Prompt("Intake/Outtake Cycle to Level 3: ")
                .addSelection("On",true)
                .addSelection("Off",false);
        SelectionMenu.Slider howManyCycles = new SelectionMenu.Slider("Choose number of cycles",0,3);
        SelectionMenu.Prompt park = new SelectionMenu.Prompt("Park State: ")
                .addSelection("On", true)
                .addSelection("Off", false);
        SelectionMenu.Prompt parkDestination = new SelectionMenu.Prompt("Park destination")
                .addSelection("Warehouse",0)
                .addSelection("Storage Unit",1);
        SelectionMenu.Prompt saveHeading = new SelectionMenu.Prompt("Save Heading at end of Auto")
                .addSelection("YES",true)
                .addSelection("NO",false);
        autoConfig = new SelectionMenu("Autonomous Configuration",alliance,startingPosition,scanEnabled,rotateCarousel,doShippingHub,doWarehouse,howManyCycles,park,parkDestination,saveHeading)
                .addInstructions("Press A & B to save. Use dpad L/R to navigate prompts and use up and down to select.");
        autoConfig.init();
    }

    @Override
    public void loop() {
        autoConfig.run(gamepad1.dpad_right,gamepad1.dpad_left,gamepad1.dpad_up,gamepad1.dpad_down);

        if(gamepad1.a && gamepad1.b){
            saved = true;
            DataToolsLite.encode(fileName,autoConfig.getOutputs());
        }

        if(autoConfig.hasChanged()){
            saved = false;
        }

        telemetry.addLine(autoConfig.formatDisplay());
        telemetry.addData("Preview", Arrays.toString(autoConfig.getOutputs()));
        telemetry.addData("Saved",saved);
    }
}
