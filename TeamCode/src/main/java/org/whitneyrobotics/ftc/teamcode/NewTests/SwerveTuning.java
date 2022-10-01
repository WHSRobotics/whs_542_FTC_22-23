package org.whitneyrobotics.ftc.teamcode.NewTests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.whitneyrobotics.ftc.teamcode.lib.geometry.Coordinate;
import org.whitneyrobotics.ftc.teamcode.lib.geometry.Position;
import org.whitneyrobotics.ftc.teamcode.lib.purepursuit.FollowerConstants;
import org.whitneyrobotics.ftc.teamcode.lib.purepursuit.swervetotarget.SwervePath;
import org.whitneyrobotics.ftc.teamcode.lib.purepursuit.swervetotarget.SwervePathGenerationConstants;
import org.whitneyrobotics.ftc.teamcode.lib.util.Functions;
import org.whitneyrobotics.ftc.teamcode.lib.util.Toggler;
import org.whitneyrobotics.ftc.teamcode.subsys.WHSRobotImpl;
import org.whitneyrobotics.ftc.teamcode.subsys.WHSRobotImplDrivetrainOnly;

import static org.whitneyrobotics.ftc.teamcode.lib.purepursuit.PathGenerator.generateSwervePath;

import java.util.ArrayList;

@TeleOp(name="Swerve Tuning", group="New Tests")
public class SwerveTuning extends OpMode {
    private WHSRobotImpl robot;
    private FtcDashboard dashboard;
    private Telemetry dashboardTelemetry;
    private TelemetryPacket packet = new TelemetryPacket();
    static SwervePath path1;
    static SwervePath path2;
    static SwervePath[] paths = {path1, path2};
    private String[] labels = {"Path 1", "Path2"};
    private Toggler pathSelector;
    private boolean squidGame = true;
    private int swerveState = 0;

    @Override
    public void init() {
        robot = new WHSRobotImpl(hardwareMap);
        robot.drivetrain.resetEncoders();
        robot.drivetrain.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        robot.setInitialCoordinate(new Coordinate(0,0,0));
        ArrayList<Position> path1Positions = new ArrayList<>();
        path1Positions = Functions.instantiatePath(new Position(0,0),new Position(1000,0));

        FollowerConstants path1FollowerConstants = new FollowerConstants(250, false);
        SwervePathGenerationConstants path1PathGenerationConstants = new SwervePathGenerationConstants(12,0.5,2.5,500);
        path1 = generateSwervePath(path1Positions,path1FollowerConstants,path1PathGenerationConstants);

        ArrayList<Position> path2Positions = new ArrayList<>();
        path2Positions = Functions.instantiatePath(new Position(0,0),new Position(500,0),new Position(500,500),new Position(800,0));

        FollowerConstants path2FollowerConstants = new FollowerConstants(250, false);
        SwervePathGenerationConstants path2PathGenerationConstants = new SwervePathGenerationConstants(12,0.7,1,500);
        path2 = generateSwervePath(path2Positions,path2FollowerConstants,path2PathGenerationConstants);

        paths = new SwervePath[]{path1, path2};
        pathSelector = new Toggler(paths.length);

        dashboard = FtcDashboard.getInstance();
        dashboardTelemetry = dashboard.getTelemetry();
        dashboardTelemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        dashboard.sendTelemetryPacket(packet);
        dashboardTelemetry.setMsTransmissionInterval(10);
        robot.updatePath(paths[pathSelector.currentState()]);
    }

    @Override
    public void loop() {
        robot.estimateHeading();
        robot.estimatePosition();

        pathSelector.changeState(gamepad1.dpad_right||gamepad2.dpad_right, gamepad1.dpad_left||gamepad2.dpad_left);
        if(gamepad1.b || gamepad2.b){ //red light
            squidGame = false;
        } else if(gamepad1.a || gamepad2.a){ //green light
            squidGame = true;
        }
        if(!squidGame){
            robot.drivetrain.operate(0,0);
        } else {
            switch(swerveState){
                case 0:
                    robot.updatePath(paths[pathSelector.currentState()]);
                    robot.swerveToTarget();
                    if(!robot.swerveInProgress()){
                        swerveState++;
                    }
                    break;
                case 1:
                    robot.drivetrain.operate(0,0);
                    break;
            }
        }
        if(gamepad1.b){
            robot.setInitialCoordinate(new Coordinate(0,0,0));
            robot.drivetrain.resetEncoders();
            swerveState = 0;
        }

        if(gamepad1.y){
            throw new RuntimeException("Player 542-Eliminated.");
        }

        packet.put("A - Selected path",labels[pathSelector.currentState()]);
        packet.put("B - Swerve in progress",robot.swerveInProgress());
        packet.put("C - Drivetrain left velocity", robot.drivetrain.getWheelVelocities()[0]);
        packet.put("D - Drivetrain right velocity",robot.drivetrain.getAllWheelVelocities()[1]);
        packet.put("E - Target left velocity",robot.swerveFollower.getCurrentTargetWheelVelocities()[0]);
        packet.put("F - Target right velocity",robot.swerveFollower.getCurrentTargetWheelVelocities()[1]);
        packet.put("G - Motor powers",robot.swerveFollower.calculateMotorPowers(robot.getCoordinate(),robot.drivetrain.getWheelVelocities()).toString());
        dashboard.sendTelemetryPacket(packet);
    }
}
