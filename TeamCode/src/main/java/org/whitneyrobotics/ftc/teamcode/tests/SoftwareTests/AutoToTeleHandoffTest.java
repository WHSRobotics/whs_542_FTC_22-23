package org.whitneyrobotics.ftc.teamcode.tests.SoftwareTests;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.whitneyrobotics.ftc.teamcode.framework.opmodes.OpModeEx;
import org.whitneyrobotics.ftc.teamcode.lib.file.RobotDataUtil;
import org.whitneyrobotics.ftc.teamcode.lib.file.WHSRobotData;
import org.whitneyrobotics.ftc.teamcode.subsys.IMU;
import org.whitneyrobotics.ftc.teamcode.tests.Test;

@TeleOp
@Test(name="AutoToTeleHandoff", autoTerminateAfterSeconds = 10)
public class AutoToTeleHandoffTest extends OpModeEx {
    IMU imu;
    public double heading;

    @Override
    public void initInternal() {
        imu = new IMU(hardwareMap);
        imu.zeroHeading();
    }

    @Override
    protected void loopInternal() {
        heading = imu.getHeading();
    }

    @Override
    public void stop() {
        WHSRobotData.heading = heading;
        RobotDataUtil.save(WHSRobotData.class);
    }
}
