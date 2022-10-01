package org.whitneyrobotics.ftc.teamcode.menus;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.whitneyrobotics.ftc.teamcode.lib.util.DataToolsLite;
import org.whitneyrobotics.ftc.teamcode.lib.util.SelectionMenu;

import java.util.Arrays;

@TeleOp(name="TeleOp Configuration", group="Config")
public class ConfigureTele extends OpMode {
    private SelectionMenu teleOpConfig;
    private boolean saved = false;
    private String fileName = "teleOpConfig.txt";

    @Override
    public void init() {
        SelectionMenu.Prompt AutoEndTele = new SelectionMenu.Prompt("Auto Terminate TeleOp")
                .addSelection("YES",true)
                .addSelection("NO",false);
        SelectionMenu.Prompt IncludeEndgameTime = new SelectionMenu.Prompt("Include Endgame time (30 sec)")
                .addSelection("YES",true)
                .addSelection("NO",false);
        SelectionMenu.Prompt Countdown = new SelectionMenu.Prompt("Teleop Countdown")
                .addSelection("NO",false)
                .addSelection("YES",true);
        SelectionMenu.Slider SecondBeforeEndgameNotif = new SelectionMenu.Slider("Seconds before endgame for notification (3 second duration)",0,11);
        SelectionMenu.Slider SecondsBeforeMatchEndNotif = new SelectionMenu.Slider("Seconds before match end for notification (5 second duration)",0,16);
        teleOpConfig = new SelectionMenu("TeleOp Configuration",AutoEndTele, IncludeEndgameTime,Countdown,SecondBeforeEndgameNotif,SecondsBeforeMatchEndNotif)
                .addInstructions("Press A & B to save. Use dpad L/R to navigate prompts and use up and down to select.");
        teleOpConfig.init();
    }

    @Override
    public void loop() {
        teleOpConfig.run(gamepad1.dpad_right,gamepad1.dpad_left,gamepad1.dpad_up,gamepad1.dpad_down);

        if(gamepad1.a && gamepad1.b){
            saved = true;
            DataToolsLite.encode(fileName, teleOpConfig.getOutputs());
        }

        if(teleOpConfig.hasChanged()){
            saved = false;
        }

        telemetry.addLine(teleOpConfig.formatDisplay());
        telemetry.addData("Preview", Arrays.toString(teleOpConfig.getOutputs()));
        telemetry.addData("Saved",saved);
    }

}
