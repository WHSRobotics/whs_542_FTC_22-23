package org.whitneyrobotics.ftc.teamcode.subsys;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.whitneyrobotics.ftc.teamcode.lib.geometry.Coordinate;
import org.whitneyrobotics.ftc.teamcode.lib.geometry.Position;

@TeleOp(name="Rotate to target Jank Test", group="sus")
public class RotateToTargetJankTest extends OpMode {
    WHSRobotImpl robot;
    Position targetPos;
    @Override
    public void init() {
        robot = new WHSRobotImpl(hardwareMap);
        robot.drivetrain.resetEncoders();
        targetPos = new Position(600,600);
        robot.setInitialCoordinate(new Coordinate(0,0,0));
    }

    @Override
    public void loop() {
        robot.estimateHeading();
        robot.estimatePosition();
        if (gamepad1.a) {
            robot.driveToTarget(targetPos, false);
        }else{
            robot.drivetrain.operate(0,0);
        }

        telemetry.addData("Robot Heading", robot.getCoordinate().getHeading());
        telemetry.addData("Angle To Target", robot.angleToTargetDebug);
        //telemetry.addData("Robot position", String.format("%s,%s"), robot.getCoordinate().getX(),robot.getCoordinate().getY());
        telemetry.addData("Robot X",robot.getCoordinate().getX());
        telemetry.addData("Robot Y",robot.getCoordinate().getY());
    }
}
