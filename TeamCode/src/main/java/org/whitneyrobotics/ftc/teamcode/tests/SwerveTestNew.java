package org.whitneyrobotics.ftc.teamcode.tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.whitneyrobotics.ftc.teamcode.lib.geometry.Coordinate;
import org.whitneyrobotics.ftc.teamcode.lib.geometry.Position;
import org.whitneyrobotics.ftc.teamcode.lib.geometry.SwerveWaypoint;
import org.whitneyrobotics.ftc.teamcode.lib.purepursuit.FollowerConstants;
import org.whitneyrobotics.ftc.teamcode.lib.purepursuit.PathGenerator;
import org.whitneyrobotics.ftc.teamcode.lib.purepursuit.swervetotarget.SwervePath;
import org.whitneyrobotics.ftc.teamcode.lib.purepursuit.swervetotarget.SwervePathGenerationConstants;
import org.whitneyrobotics.ftc.teamcode.subsys.WHSRobotImplOld;

import java.lang.reflect.Array;
import java.util.ArrayList;
@Autonomous (name = "SwerveToTargetTest")
public class SwerveTestNew extends OpMode {
    WHSRobotImplOld robot;
    SwervePath path;
    Coordinate startingCoordinate = new Coordinate(0,0,0);
    Position p1 = new Position(-1200,0);
    Position p2 = new Position(1200, 0);
    Position p3 = new Position(4, 5);

    ArrayList<Position> posArray = new ArrayList<>();
    FollowerConstants followerConstants = new FollowerConstants(550,false);
    SwervePathGenerationConstants pathGenerationConstants = new SwervePathGenerationConstants(12,0.7,0.8,230);
    FtcDashboard dashboard;
    Telemetry dashboardTelemetry;
    TelemetryPacket packet = new TelemetryPacket();

    public void init(){
        dashboard = FtcDashboard.getInstance();
        dashboardTelemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        dashboard.sendTelemetryPacket(packet);
        posArray.add(startingCoordinate);
        posArray.add(p1);
        robot = new WHSRobotImplOld(hardwareMap);
        robot.setInitialCoordinate(startingCoordinate);
        path = PathGenerator.generateSwervePath(posArray, followerConstants, pathGenerationConstants);
        robot.updatePath(path);
        dashboardTelemetry.setMsTransmissionInterval(10);
    }
    public void loop(){
        robot.estimatePosition();
        robot.estimateHeading();
        robot.swerveToTarget();
        packet.put("X", robot.getCoordinate().getX());
        packet.put("Y", robot.getCoordinate().getY());
        for (SwerveWaypoint waypoint: path.getWaypoints()){
            packet.addLine("X: " + waypoint.getPosition().getX() + " Y: " + waypoint.getPosition().getY() + " Velocity: " + waypoint.getTangentialVelocity());
        }
        packet.put("Closest Index", robot.swerveFollower.indexOfClosest);
        dashboard.sendTelemetryPacket(packet);
    }

}
