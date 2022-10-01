package org.whitneyrobotics.ftc.teamcode.NewTests;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.whitneyrobotics.ftc.teamcode.lib.util.DataTools;
import org.whitneyrobotics.ftc.teamcode.lib.util.DataToolsLite;
import org.whitneyrobotics.ftc.teamcode.lib.util.SelectionMenu;

import java.util.Arrays;

@TeleOp(group = "Tests",name="Menu Selector Test")
public class TestMenu extends OpMode {
    private SelectionMenu configureAuto;
    private boolean firstLoop = true;
    private String fileName = "testConfig.txt";
    private boolean saved = false;

    @Override
    public void init() {
        //configureAuto = new SelectionMenu("Auto configuration");
        SelectionMenu.Selection<Integer> RED = new SelectionMenu.Selection<Integer>("Red",0);
        SelectionMenu.Selection<Integer> BLUE = new SelectionMenu.Selection<Integer>("Blue",1);
        SelectionMenu.Prompt color = new SelectionMenu.Prompt("Select alliance color: ", RED, BLUE);
        SelectionMenu.Prompt startingPosition = new SelectionMenu.Prompt("Select starting position: ")
                .addSelection("Top",0)
                .addSelection("Bottom",1);
        SelectionMenu.Prompt rotateCarousel = new SelectionMenu.Prompt("Rotate Carousel State: ")
                .addSelection("On",false)
                .addSelection("Off", true);
        SelectionMenu.Prompt doShippingHub = new SelectionMenu.Prompt("Shipping Hub State: ")
                .addSelection("On", true)
                .addSelection("Off",false);
        SelectionMenu.Prompt doWarehouse = new SelectionMenu.Prompt("Warehouse State: ")
                .addSelection("On",true)
                .addSelection("Off",false);
        SelectionMenu.Prompt park = new SelectionMenu.Prompt("Park State: ")
                .addSelection("On", true)
                .addSelection("Off", false);
        SelectionMenu.Slider slider = new SelectionMenu.Slider("Test Slider",0,100);
        configureAuto = new SelectionMenu("Autonomous Configuration", color, startingPosition, rotateCarousel, doShippingHub, doWarehouse)
                .addPrompt(park) //just for fun ;)
                .addPrompt(slider);
        configureAuto.init();
    }

    @Override
    public void init_loop() {

    }

    @Override
    public void loop() {
        if(gamepad1.a){
            saved = true;
            DataTools.Data data = new DataTools.Data();
            for(int i = 0; i<configureAuto.getOutputs().length; i++){
                data.put(i,configureAuto.getOutputs()[i]);
            }
            DataToolsLite.encode(fileName,data);
        }

        if(configureAuto.hasChanged()){
            saved = false;
        }

        configureAuto.run(gamepad1.dpad_right,gamepad1.dpad_left,gamepad1.dpad_down,gamepad1.dpad_up);
        telemetry.addLine(configureAuto.formatDisplay());
        telemetry.addData("Selection output", Arrays.toString(configureAuto.getOutputs()));
        //telemetry.addData("Value of color",configureAuto.getPrompts().get(0).getValueOfActive());
        //telemetry.addData("# of prompts",configureAuto.prompts);

        telemetry.addData("Saved",saved);

    }
}
