package org.whitneyrobotics.ftc.teamcode.autoop;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.whitneyrobotics.ftc.teamcode.framework.opmodes.OpModeEx;
import org.whitneyrobotics.ftc.teamcode.robotImpl.WHSRobotImpl;

@RequiresApi(api = Build.VERSION_CODES.N)
@Autonomous(name="PowerPlay Auto Meet 2 RIGHT", group="A", preselectTeleOp = "PowerPlay TeleOp")
public class PowerPlayAutoRIGHT extends OpModeEx {
    WHSRobotImpl robot;
    private int firstIteration = 0;
    String state = "Nothing";

    @Override
    public void initInternal() {
        robot = new WHSRobotImpl(hardwareMap, gamepad2);
        addTemporalCallback(resolve -> {
            this.state = "Moving";
        }, 1500);
        addTemporalCallback(resolve -> {
            this.state = "Idle";
        }, 6500);
    }

    @Override
    protected void loopInternal() {
        switch (state) {
            case "Moving":
                robot.drivetrain.operateByCommand(0.3, 0, 0);
            case "Idle":
                robot.drivetrain.operateByCommand(0, 0, 0);
            default:
        }
    }
}
