package org.whitneyrobotics.ftc.teamcode.tests.SoftwareTests;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.robot.Robot;

import org.whitneyrobotics.ftc.teamcode.framework.opmodes.OpModeEx;
import org.whitneyrobotics.ftc.teamcode.lib.file.RobotDataUtil;
import org.whitneyrobotics.ftc.teamcode.lib.file.WHSRobotData;
import org.whitneyrobotics.ftc.teamcode.subsys.IMU;
import org.whitneyrobotics.ftc.teamcode.tests.TelemetryData;
import org.whitneyrobotics.ftc.teamcode.tests.Test;

@TeleOp(name="AutoHandoff", group="Software Tests")
@Test(name="AutoTeleHandoff")
public class AutoTeleHandoffTest extends OpModeEx {
    public IMU imu;
    @TelemetryData
    public double heading;
    @Override
    public void initInternal() {
        imu = new IMU(hardwareMap);
    }

    @Override
    protected void loopInternal() {
        heading = imu.getHeading();
        betterTelemetry.addData("heading",heading);
    }

    @Override
    public void stop() {
        WHSRobotData.heading = heading;
        RobotDataUtil.save(WHSRobotData.class);
    }
}
