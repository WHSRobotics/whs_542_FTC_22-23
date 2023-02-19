package org.whitneyrobotics.ftc.teamcode.tests.SoftwareTests;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.whitneyrobotics.ftc.teamcode.framework.opmodes.OpModeEx;
import org.whitneyrobotics.ftc.teamcode.lib.file.RobotDataUtil;
import org.whitneyrobotics.ftc.teamcode.lib.file.WHSRobotData;

@Autonomous(group="Z")
public class ReadOrientation extends OpModeEx {
    @Override
    public void initInternal() {
        RobotDataUtil.load(WHSRobotData.class);
    }

    @Override
    protected void loopInternal() {
        betterTelemetry.addLine(String.valueOf(WHSRobotData.heading));
    }
}
