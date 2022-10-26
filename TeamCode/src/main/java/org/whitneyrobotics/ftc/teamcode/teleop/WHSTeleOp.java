package org.whitneyrobotics.ftc.teamcode.teleop;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.whitneyrobotics.ftc.teamcode.BetterTelemetry.LineItem;
import org.whitneyrobotics.ftc.teamcode.BetterTelemetry.SliderDisplayLine;
import org.whitneyrobotics.ftc.teamcode.framework.opmodes.OpModeEx;
import org.whitneyrobotics.ftc.teamcode.subsys.Robot;

@RequiresApi(api = Build.VERSION_CODES.N)
public class WHSTeleOp extends OpModeEx {
    Robot robot;

    void setupGamepads(){
        gamepad1.SQUARE.onPress(e -> {
            robot.grabber.toggleState();
        });
    }

    @Override
    public void initInternal() {
        robot = new Robot(hardwareMap);
        betterTelemetry.addItem(new SliderDisplayLine("Servo position",robot.grabber::getPosition, LineItem.Color.BLUE));
        setupGamepads();
    }

    @Override
    protected void loopInternal() {
        betterTelemetry.addData("Grabber state",robot.grabber.getCurrentState(), LineItem.Color.RED, LineItem.RichTextFormat.BOLD);
        robot.grabber.setForceOpen(gamepad1.A.value());
    }
}
