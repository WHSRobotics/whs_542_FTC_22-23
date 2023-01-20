package org.whitneyrobotics.ftc.teamcode.tests.SoftwareTests;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.whitneyrobotics.ftc.teamcode.framework.opmodes.OpModeEx;
import org.whitneyrobotics.ftc.teamcode.robotImpl.WHSRobotImpl;
import org.whitneyrobotics.ftc.teamcode.tests.TelemetryData;
import org.whitneyrobotics.ftc.teamcode.tests.Test;

@Test(name="AutoGrabTest")
@TeleOp(name="AutoGrabTest", group = "SoftwareTests")
@Config
public class AutoGrabTest extends OpModeEx {
    WHSRobotImpl robot;
    @TelemetryData
    public static double conePrediction = 11;
    public void setConePrediction(double conePrediction){this.conePrediction=conePrediction;}
    @TelemetryData
    public static boolean shouldGrab = false;

    @Override
    public void initInternal() {
        gamepad1.CIRCLE.onPress(e -> {shouldGrab=true;});
        robot = new WHSRobotImpl(hardwareMap);
        betterTelemetry.useDashboardTelemetry(dashboardTelemetry);
        robot.setCurrentAlliance(WHSRobotImpl.Alliance.RED);
    }

    @Override
    protected void loopInternal() {
        shouldGrab = robot.autoGrab(shouldGrab, conePrediction, this::setConePrediction);
        betterTelemetry.addData("Grabber height", robot.linearSlides.getPosition());
        betterTelemetry.addData("Grabber phase", robot.linearSlides.getPhase());
        betterTelemetry.addData("Grabber velocity", robot.linearSlides.getVelocity());
        betterTelemetry.addData("Grabber State", robot.grabberState);
        betterTelemetry.addData("Cone detected",robot.isSensingCone());
    }
}
