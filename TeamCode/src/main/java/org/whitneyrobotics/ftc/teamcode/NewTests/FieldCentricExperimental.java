package org.whitneyrobotics.ftc.teamcode.NewTests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.whitneyrobotics.ftc.teamcode.lib.geometry.Coordinate;
import org.whitneyrobotics.ftc.teamcode.lib.geometry.Position;
import org.whitneyrobotics.ftc.teamcode.lib.util.DataTools;
import org.whitneyrobotics.ftc.teamcode.lib.util.DataToolsLite;
import org.whitneyrobotics.ftc.teamcode.subsys.DrivetrainExperimental;
import org.whitneyrobotics.ftc.teamcode.subsys.WHSRobotImplDrivetrainFC;

@TeleOp(name="Test Field Centric",group="Tests")
public class FieldCentricExperimental extends OpMode {
    private WHSRobotImplDrivetrainFC robot;

    @Override
    public void init() {
        robot = new WHSRobotImplDrivetrainFC(hardwareMap);
        double autoHeading;
        try {
            autoHeading = Double.parseDouble(DataToolsLite.decode("heading.txt")[0]);
        } catch (Exception e){
            autoHeading = 0;
        }
        robot.setInitialCoordinate(new Coordinate(0,0,autoHeading));
        robot.drivetrain.resetEncoders();
    }

    @Override
    public void loop() {
        robot.estimateHeading();
        robot.estimatePosition();
        robot.drivetrain.switchFieldCentric(gamepad1.x);
        if(gamepad1.left_bumper) {
            robot.drivetrain.operateMecanumDrive(-gamepad1.left_stick_x/2.54, gamepad1.left_stick_y/2.54, -gamepad1.right_stick_x/2.54, robot.getCoordinate().getHeading());
        } else {
            robot.drivetrain.operateMecanumDriveScaled(-gamepad1.left_stick_x,gamepad1.left_stick_y,gamepad1.right_stick_x,robot.getCoordinate().getHeading());
        }

        if(gamepad1.y){
            throw new RuntimeException("ロボットは　しにました　:(");
        }
    }
}
