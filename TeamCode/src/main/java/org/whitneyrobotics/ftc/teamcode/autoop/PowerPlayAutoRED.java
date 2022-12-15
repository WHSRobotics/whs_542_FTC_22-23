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
    AprilTagScanner2022 aprilTagScanner = new AprilTagScanner2022(hardwareMap);

    @Override
    public void initInternal() {
        // auto step/timing management
        addTemporalCallback(resolve -> {
            this.state = "Moving to InitPosition";
            }, 1500);
        addTemporalCallback(resolve -> {
            this.state = "Moving to parking position";
            }, 3000);
        addTemporalCallback(resolve -> {
            this.state = "Parking";
        }, 6500);
        addTemporalCallback(resolve -> {
            this.state = "Idle";
        }, 9500);

        addTemporalCallback(e -> {
            RobotDataUtil.save(WHSRobotData.class,true);
        },29000);
        addTemporalCallback(e -> requestOpModeStop(),31000);
        robot = new WHSRobotImpl(hardwareMap);
    }

    @Override
    public void init_loop(){
        if (aprilTagScanner.scan() != -1) {
            aprilTagScanner.latestTagToTelemetry();
            return; //loop until scan
        }
    }

    @Override
    protected void loopInternal() {
        switch (state) {
            case "Junction Placement":
                robot.drivetrain.operateByCommand(-0.4, 0, 0);
            case "Moving to InitPosition":
                robot.drivetrain.operateByCommand(0.4, 0, 0);
            case "Moving to Parking Position":
                robot.drivetrain.operateByCommand(0, 0.2, 0);
            case "Parking" :
                robot.drivetrain.operateByCommand(0.2 * (aprilTagScanner.getLatestTag()-2),0,0);
        }

        WHSRobotData.heading = robot.imu.getHeading();
    }
}
