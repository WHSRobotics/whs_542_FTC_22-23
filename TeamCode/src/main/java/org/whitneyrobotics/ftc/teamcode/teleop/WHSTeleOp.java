package org.whitneyrobotics.ftc.teamcode.teleop;

import static org.whitneyrobotics.ftc.teamcode.lib.util.RumbleEffects.endgame;
import static org.whitneyrobotics.ftc.teamcode.lib.util.RumbleEffects.matchEnd;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.whitneyrobotics.ftc.teamcode.BetterTelemetry.LineItem;
import org.whitneyrobotics.ftc.teamcode.BetterTelemetry.SliderDisplayLine;
import org.whitneyrobotics.ftc.teamcode.BetterTelemetry.TextLine;
import org.whitneyrobotics.ftc.teamcode.framework.opmodes.OpModeEx;
import org.whitneyrobotics.ftc.teamcode.lib.file.RobotDataUtil;
import org.whitneyrobotics.ftc.teamcode.lib.file.WHSRobotData;
import org.whitneyrobotics.ftc.teamcode.robotImpl.WHSRobotImpl;
import org.whitneyrobotics.ftc.teamcode.subsys.Grabber;
import org.whitneyrobotics.ftc.teamcode.subsys.LinearSlidesMeet1;

@TeleOp(name="PowerPlay TeleOp", group="A")
@RequiresApi(api = Build.VERSION_CODES.N)
public class WHSTeleOp extends OpModeEx {
    WHSRobotImpl robot;

    void setupGamepads(){
        gamepad1.CIRCLE.onPress(e -> robot.drivetrain.toggleFieldCentric());
        gamepad1.SQUARE.onPress(e -> robot.imu.zeroHeading());
        gamepad1.LEFT_TRIGGER.onInteraction(e -> robot.drivetrain.setPowerReduction(e.floatVal));
        gamepad1.BUMPER_RIGHT.onPress(e -> robot.robotGrabber.toggleState());
        gamepad2.BUMPER_RIGHT.onPress(e -> robot.robotGrabber.toggleState());
        gamepad2.SHARE.onPress(e -> {
            playSound("emergency", 100.00f);
            requestOpModeStop();
        });

        gamepad2.SQUARE.onPress(e -> robot.robotLinearSlidesMeet1.setTarget(LinearSlidesMeet1.Target.LOW));
        gamepad2.TRIANGLE.onPress(e -> robot.robotLinearSlidesMeet1.setTarget(LinearSlidesMeet1.Target.MEDIUM));
        gamepad2.CIRCLE.onPress(e -> robot.robotLinearSlidesMeet1.setTarget(LinearSlidesMeet1.Target.HIGH));
        gamepad2.CROSS.onPress(e -> robot.robotLinearSlidesMeet1.setTarget(LinearSlidesMeet1.Target.LOWERED));
        gamepad2.START.onPress(e-> robot.robotLinearSlidesMeet1.reloadPIDCoefficients());
    }

    void setupNotifications(){
        addTemporalCallback(resolve -> {
            playSound("endgame",100f);
            gamepad1.getEncapsulatedGamepad().runRumbleEffect(endgame);
            gamepad2.getEncapsulatedGamepad().runRumbleEffect(endgame);
            betterTelemetry.addLine("Endgame approaching soon!", LineItem.Color.ROBOTICS, LineItem.RichTextFormat.BOLD).persistent();
            resolve.accept(!gamepad1.getEncapsulatedGamepad().isRumbling() && gamepad2.getEncapsulatedGamepad().isRumbling());
        }, 85000);

        addTemporalCallback(resolve -> {
            betterTelemetry.removeLineByCaption("Endgame approaching soon!");
            resolve.accept(true);
        },90000);

        addTemporalCallback(resolve -> {
            playSound("matchend",100f);
            gamepad1.getEncapsulatedGamepad().runRumbleEffect(matchEnd);
            gamepad2.getEncapsulatedGamepad().runRumbleEffect(matchEnd);
            betterTelemetry.addLine("Match ends in 5 seconds!", LineItem.Color.FUCHSIA, LineItem.RichTextFormat.BOLD).persistent();
            resolve.accept(!gamepad1.getEncapsulatedGamepad().isRumbling() && gamepad2.getEncapsulatedGamepad().isRumbling());
        }, 113000);

        addTemporalCallback(resolve -> {
            playSound("slay",100f);
            betterTelemetry.removeLineByCaption("Match ends in 5 seconds!");
            betterTelemetry.addLine("Match has ended.", LineItem.Color.LIME, LineItem.RichTextFormat.ITALICS).persistent();
            resolve.accept(true);
        },120000);
    }

    @Override
    public void initInternal() {
        RobotDataUtil.load(WHSRobotData.class);
        robot = new WHSRobotImpl(hardwareMap);
        robot.imu.zeroHeading(WHSRobotData.heading);
        setupGamepads();
        setupNotifications();
        betterTelemetry.addItem(new SliderDisplayLine("Slides position", robot.robotLinearSlidesMeet1::getPosition, LineItem.Color.AQUA)
                .setScale(36));
        betterTelemetry.useDashboardTelemetry(dashboardTelemetry);
    }

    @Override
    protected void loopInternal() {
        if(gamepad2.DPAD_UP.value()){
            robot.robotGrabber.forceOpen();
        }
        robot.tick();
        double xPower = Math.pow(gamepad1.LEFT_STICK_X.value(),3);
        double yPower = Math.pow(gamepad1.LEFT_STICK_Y.value(),3);
        double rotPower = Math.pow(gamepad1.RIGHT_STICK_X.value(),3);
        if(gamepad1.BUMPER_LEFT.value() || gamepad2.BUMPER_LEFT.value()){
            xPower = gamepad1.LEFT_STICK_X.value()/3;
            yPower = gamepad1.LEFT_STICK_Y.value()/3;
            rotPower = gamepad1.RIGHT_STICK_X.value()/3;
        }
        robot.robotLinearSlidesMeet1.operate(gamepad2.LEFT_STICK_Y.value(),gamepad2.BUMPER_LEFT.value());

        robot.drivetrain.operateByCommand(xPower, yPower, rotPower);
        betterTelemetry.addData("Field centric",robot.drivetrain.fieldCentricEnabled());
        betterTelemetry.addData("Slides phase",robot.robotLinearSlidesMeet1.getPhase());
        betterTelemetry.addData("is sliding",robot.robotLinearSlidesMeet1.isSliding());
        betterTelemetry.addData("is raised",robot.robotLinearSlidesMeet1.isRaised());
        betterTelemetry.addLine("");

        betterTelemetry.addData("current target slides vel",robot.robotLinearSlidesMeet1.velocity);
        betterTelemetry.addData("current slides vel",robot.robotLinearSlidesMeet1.getPIDL().getMotor().getVelocity(AngleUnit.RADIANS));
    }
}
