package org.whitneyrobotics.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.whitneyrobotics.ftc.teamcode.lib.geometry.Position;
import org.whitneyrobotics.ftc.teamcode.lib.util.SimpleTimer;
import org.whitneyrobotics.ftc.teamcode.subsys.OldOuttake2;
import org.whitneyrobotics.ftc.teamcode.subsys.WHSRobotImplOld;

@TeleOp(name = "Old TeleOp", group = "TeleOp")
public class oldTeleOp extends OpMode {
    WHSRobotImplOld robot;

    //public Toggler binToggler = new Toggler(3);
    //public Toggler outtakeToggler = new Toggler(6);

    public OldOuttake2.GoalPositions currentTarget;
    public Position currentTargetPos;

    public SimpleTimer rotateTimer = new SimpleTimer();

    public final int ROTATE_TIME = 500;
    public int shootingCase;

    public String currentTargetWord;
    public String intakeStatus;

    public final Position powershot1 = new Position(1800, -95.25); // from right to left fix later
    public final Position powershot2 = new Position(1800, -285.75);
    public final Position powershot3 = new Position(1800, -476.25);
    public final Position binsMidpoint = new Position(1800, -890.5875);

    //public Toggler shootingTimerTog = new Toggler(3);// begin on switchcase


    @Override
    public void init() {
        robot = new WHSRobotImplOld(hardwareMap);
    }

    @Override
    public void loop() {
        //Intake
        if (gamepad1.right_trigger > 0.01) {
            intakeStatus = "Normal Intake";
            //robot.intake.operate(gamepad1.right_trigger > 0.01, gamepad1.left_trigger == 0.0);
        } else if (gamepad1.left_trigger > 0.01) {
            intakeStatus = "Reverse Intake";
           // robot.intake.operate(gamepad1.right_trigger > 0.01, gamepad1.left_trigger > 0.01);
        } else {
            intakeStatus = "Power Off";
           // robot.intake.operate(gamepad1.right_trigger == 0, gamepad1.left_trigger == 0);
        }
        if (gamepad1.a) {
            //robot.intake.manualDropdown(gamepad1.a);
        }
        //Canister
        if (gamepad2.b) {
            robot.canister.operateLoader(gamepad2.x);
        } else {
            robot.canister.operateLoader(false);
        }
        //Drivetrain
        robot.estimateHeading();
        //robot.drivetrain.switchFieldCentric(gamepad1.b);
        if (gamepad1.left_bumper) {
            robot.drivetrain.operateMecanumDrive(gamepad1.left_stick_x / 2.54, gamepad1.left_stick_y / 2.54, gamepad1.right_stick_x / 2.54, robot.getCoordinate().getHeading());
        } else {
            robot.drivetrain.operateMecanumDriveScaled(gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad1.right_stick_x, robot.getCoordinate().getHeading());
        }
        //Outtake
        //binToggler.changeState(gamepad2.dpad_right, gamepad2.dpad_left);
        //outtakeToggler.changeState(gamepad2.dpad_right, gamepad2.dpad_left);
        if (gamepad2.dpad_left) {
            currentTargetWord = "Left Powershot";
            currentTarget = OldOuttake2.GoalPositions.LEFT_POWER_SHOT;
            currentTargetPos = powershot1;
        } else if (gamepad2.dpad_up) {
            currentTargetWord = "Center Powershot";
            currentTarget = OldOuttake2.GoalPositions.CENTER_POWER_SHOT;
            currentTargetPos = powershot2;
        } else if (gamepad2.dpad_right) {
            currentTargetWord = "Right Powershot";
            currentTarget = OldOuttake2.GoalPositions.RIGHT_POWER_SHOT;
            currentTargetPos = powershot3;
        } else if (gamepad2.dpad_down) {
            currentTargetPos = binsMidpoint;
            currentTarget = OldOuttake2.GoalPositions.LOW_BIN;
            currentTargetWord = "Low Bin";
        } else if (gamepad2.left_bumper) {
            currentTargetPos = binsMidpoint;
            currentTarget = OldOuttake2.GoalPositions.MEDIUM_BIN;
            currentTargetWord = "Medium Bin";
        } else if (gamepad2.right_bumper) {
            currentTargetPos = binsMidpoint;
            currentTarget = OldOuttake2.GoalPositions.HIGH_BIN;
            currentTargetWord = "High Bin";
        }

        if (gamepad2.a) {
            switch (shootingCase) {
                case 0:
                    rotateTimer.set(ROTATE_TIME);
                    shootingCase++;
                    break;
                case 1:
                    robot.rotateToTarget(robot.outtake.calculateLaunchHeading(currentTargetPos, robot.getCoordinate()), false);
                    if (rotateTimer.isExpired()) {
                        shootingCase++;
                    }
                    break;
                case 2:
                    robot.outtake.launchToTarget(currentTarget);
                    break;
                default:
                    break;
            }
        }

        //Wobble
        if (gamepad2.x || gamepad2.y) {
            robot.wobble.operateWobble(gamepad2.x, gamepad2.y);
        }
        telemetry.addData("Intake State: ", intakeStatus);
       // telemetry.addData("Intake Position", robot.intake.dropdownStatus);
        telemetry.addData("Canister Loader State: ", robot.canister.canisterState);
        telemetry.addData("Robot Current Position: ", robot.drivetrain.getAllEncoderPositions());
        telemetry.addData("Current Target: ", currentTargetWord);
        telemetry.addData("Wobble State: ", robot.wobble.wobbleDesc);
    }
}
