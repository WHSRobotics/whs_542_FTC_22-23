package org.whitneyrobotics.ftc.teamcode.autoop;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.whitneyrobotics.ftc.teamcode.framework.opmodes.OpModeEx;
import org.whitneyrobotics.ftc.teamcode.lib.file.RobotDataUtil;
import org.whitneyrobotics.ftc.teamcode.lib.file.WHSRobotData;
import org.whitneyrobotics.ftc.teamcode.robotImpl.WHSRobotImpl;

@RequiresApi(api = Build.VERSION_CODES.N)
@Autonomous(name="PowerPlay Auto RED", group="A", preselectTeleOp = "PowerPlay TeleOp")
public class PowerPlayAutoRED extends OpModeEx {
    boolean finished = false;
    WHSRobotImpl robot;

    @Override
    public void initInternal() {
        addTemporalCallback(resolve -> {
            this.finished = true;
            resolve.accept(true);
            }, 1500);
        addTemporalCallback(e -> {
            RobotDataUtil.save(WHSRobotData.class,true);
        },29000);
        addTemporalCallback(e -> requestOpModeStop(),31000);
        robot = new WHSRobotImpl(hardwareMap, gamepad1);
    }

    @Override
    protected void loopInternal() {
        if (!finished){
            robot.drivetrain.operateByCommand(-0.4,0,0);
        } else {
            robot.drivetrain.operateByCommand(0,0,0);
        }
        WHSRobotData.heading = robot.imu.getHeading();
    }
}
