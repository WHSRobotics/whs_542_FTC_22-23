package org.whitneyrobotics.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.whitneyrobotics.ftc.teamcode.framework.opmodes.OpModeEx;
import org.whitneyrobotics.ftc.teamcode.lib.file.RobotDataUtil;
import org.whitneyrobotics.ftc.teamcode.lib.file.WHSRobotData;

@TeleOp(name="ResetPose", group="A")
public class ResetPose extends OpModeEx {
    @Override
    public void initInternal() {
        WHSRobotData.lastX = 0;
        WHSRobotData.lastY = 0;
        WHSRobotData.heading = 0;
        WHSRobotData.slidesHeight = 0;
        RobotDataUtil.save(WHSRobotData.class, true);
    }

    @Override
    protected void loopInternal() {
        requestOpModeStop();
    }
}
