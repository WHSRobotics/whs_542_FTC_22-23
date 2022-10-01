package org.whitneyrobotics.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.whitneyrobotics.ftc.teamcode.subsys.WHSRobotImplOld;
@TeleOp(name = "Acceleration Test")
public class AccelerationTest extends OpMode {
    WHSRobotImplOld robot;
    double max = 0;
    @Override
    public void init() {
        robot = new WHSRobotImplOld(hardwareMap);
    }

    @Override
    public void loop() {
        robot.drivetrain.operateMecanumDrive(gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad1.right_stick_x, 0);
        double currentAccel = Math.abs(robot.imu.getYAcceleration());
        if(currentAccel > max){
            max = currentAccel;
        }
        telemetry.addData("Max", max);
    }
}
