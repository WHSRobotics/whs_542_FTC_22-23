package org.whitneyrobotics.ftc.teamcode.tests.HardwareTests;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.whitneyrobotics.ftc.teamcode.framework.Alias;
import org.whitneyrobotics.ftc.teamcode.framework.opmodes.OpModeEx;
import org.whitneyrobotics.ftc.teamcode.subsys.LinearSlidesMeet3;
import org.whitneyrobotics.ftc.teamcode.tests.Test;

@Test(name="Linear Slides Meet 3")
@TeleOp(name="Linear Slides Meet 3 Test", group="Hardware Tests")
public class LinearSlidesMeet3Test extends OpModeEx {
    public LinearSlidesMeet3 slides;

    @Override
    public void initInternal() {
        slides = new LinearSlidesMeet3(hardwareMap);
        //slides.EmptyConstants();
        betterTelemetry.useDashboardTelemetry(dashboardTelemetry);
        addTelemetryFields(slides, "velocity","currentTarget","");
        addTelemetryFields(slides.slidingController,"velocity");
        gamepad1.CIRCLE.onPress(e -> slides.setTarget(LinearSlidesMeet3.Target.MEDIUM));
        gamepad1.CROSS.onPress(e -> slides.setTarget(LinearSlidesMeet3.Target.LOWERED));
    }

    @Override
    protected void loopInternal() {
        slides.operate(gamepad1.LEFT_STICK_Y.value(),gamepad1.BUMPER_RIGHT.value());
        betterTelemetry.addData("current position",slides.getPosition());

        betterTelemetry.addData("current velocity",slides.velocity);
        betterTelemetry.addData("target velocity",slides.slidingController.velocity);
        betterTelemetry.addData("currentTarget",slides.currentTarget);
        betterTelemetry.addData("PIDVA Controller phase",slides.slidingController.phase);
        betterTelemetry.addData("PID Controller output", slides.output);
        betterTelemetry.addData("sliding Controller output", slides.slidingController.getOutput());
        betterTelemetry.addData("Error",slides.error);
        betterTelemetry.addData("Mode",slides.currentState);
        betterTelemetry.addData("message",slides.slidingController.FTCDashboardTest);
    }
}
