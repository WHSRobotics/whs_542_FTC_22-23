package org.whitneyrobotics.ftc.teamcode.autoop;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.whitneyrobotics.ftc.teamcode.framework.opmodes.OpModeEx;
import org.whitneyrobotics.ftc.teamcode.lib.file.RobotDataUtil;
import org.whitneyrobotics.ftc.teamcode.lib.file.WHSRobotData;
import org.whitneyrobotics.ftc.teamcode.robotImpl.WHSRobotImpl;
import org.whitneyrobotics.ftc.teamcode.visionImpl.AprilTagScanner2022;

@RequiresApi(api = Build.VERSION_CODES.N)
@Autonomous(name="PowerPlay Auto RED", group="A", preselectTeleOp = "PowerPlay TeleOp")
public class PowerPlayAutoRED extends OpModeEx {
    String state = "Junction Placement";
    WHSRobotImpl robot;
    int scannedZone = -1; //-1: none, 0: left, 1: middle, 2: right
    AprilTagScanner2022 aprilTagScanner = new AprilTagScanner2022(hardwareMap);

    @Override
    public void initInternal() {
        // state and timing management
        addTemporalCallback(resolve -> {
            this.state = "Moving to InitPosition";
            }, 1500);

        addTemporalCallback(e -> {
            RobotDataUtil.save(WHSRobotData.class,true);
        },29000);
        addTemporalCallback(e -> requestOpModeStop(),31000);
        robot = new WHSRobotImpl(hardwareMap);
    }

    @Override
    protected void loopInternal() {
        if (state == "Junction Placement"){
            robot.drivetrain.operateByCommand(-0.4,0,0);
        }
        else if (state == "Moving to InitPosition"){
            robot.drivetrain.operateByCommand(0.4,0,0);
        }

        WHSRobotData.heading = robot.imu.getHeading();
    }
}
