package org.whitneyrobotics.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.whitneyrobotics.ftc.teamcode.subsys.WHSRobotImplOld;
@TeleOp(name = "Odometry Test Deadwheel")
@Disabled
public class DeadwheelEncoderTest extends OpMode {
    WHSRobotImplOld robot;

    @Override
    public void init() {
        robot = new WHSRobotImplOld(hardwareMap);
    }

    @Override
    public void loop() {
        robot.deadWheelEstimateCoordinate();
        telemetry.addData("Left: ", robot.drivetrain.getMMDeadwheelEncoderDeltas()[0]);
        telemetry.addData("Middle: ", robot.drivetrain.getMMDeadwheelEncoderDeltas()[1]);
        telemetry.addData("Right: ", robot.drivetrain.getMMDeadwheelEncoderDeltas()[2]);

        //telemetry.addData("Encoder Left: ", robot.drivetrain.leftOdometry.getCurrentPosition());
        telemetry.addData("Encoder Middle: ", robot.drivetrain.frontRight.getCurrentPosition());
        telemetry.addData("Encoder Right: ", robot.drivetrain.frontLeft.getCurrentPosition());

        telemetry.addData("X: ", robot.getCoordinate().getX());
        telemetry.addData("Y: ", robot.getCoordinate().getY());
        telemetry.addData("Heading: ", robot.getCoordinate().getHeading());
    }
}
