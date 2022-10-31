package org.whitneyrobotics.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.whitneyrobotics.ftc.teamcode.lib.geometry.Coordinate;
import org.whitneyrobotics.ftc.teamcode.lib.util.RobotConstants;
import org.whitneyrobotics.ftc.teamcode.lib.util.Toggler;
import org.whitneyrobotics.ftc.teamcode.subsys.*;

@TeleOp(name = "Canister Test", group = "Tests")
@Disabled
public class CanisterTest extends OpMode {
/*    private Canister testCanister;
    private Outtake testOuttake;
    private Intake testIntake;
    private Drivetrain testDrivetrain;*/
    public Toggler intakeToggler;
    public Toggler outtakeToggler;
    public WHSRobotImplOld robot;


    @Override
    public void init() {
        robot = new WHSRobotImplOld(hardwareMap);
        intakeToggler = new Toggler(2);
        outtakeToggler = new Toggler(2);

    }

    @Override
    public void loop() {
        robot.estimateHeading();
        if(gamepad1.left_bumper){
            robot.drivetrain.frontLeft.setPower(-0.5);
            robot.drivetrain.backLeft.setPower(-0.5);
        }else if(gamepad2.left_bumper) {
            robot.drivetrain.operateMecanumDrive(-gamepad2.left_stick_x/4, -gamepad2.left_stick_y/4, -gamepad2.right_stick_x/4, 180);
        }else{
            robot.drivetrain.operateMecanumDrive(-gamepad2.left_stick_x, -gamepad2.left_stick_y, -gamepad2.right_stick_x, 180);
        }
        intakeToggler.changeState(gamepad1.right_bumper);
        //robot.intake.operate(gamepad1.right_bumper, gamepad1.left_bumper || gamepad2.right_bumper);
        robot.shootHighGoal(gamepad1.x);
        robot.shootHighGoal2(gamepad1.y);

        //testOuttake.setLauncherPower(0.77);
        /*if(outtakeToggler.currentState() == 1) {
            robot.outtake.operateFlywheel(Outtake.GoalPositions.HIGH_BIN);
        }else{
            robot.outtake.setLauncherPower(0.0);
        }*/
        robot.shootPowerShots(gamepad1.b);
        if(gamepad1.dpad_down){
            robot.setInitialCoordinate(new Coordinate(robot.getCoordinate().getPos(), 0));
        }
        telemetry.addData("Heading", robot.getCoordinate().getHeading());
        telemetry.addData("Current Velocity", robot.outtake.flywheel.getVelocity());
        telemetry.addData("Power: ", robot.outtake.flywheel.getPower());
        telemetry.addData("Loader Setting:", robot.canister.canisterState);
        telemetry.addData("Error", robot.outtake.errorDebug);
        telemetry.addData("Target", robot.outtake.targetVelocityDebug);
        telemetry.addData("Current", robot.outtake.currentVelocityDebug);
        telemetry.addData("P Term", RobotConstants.FLYWHEEL_CONSTANTS.kP * robot.outtake.errorDebug);
    }
}
