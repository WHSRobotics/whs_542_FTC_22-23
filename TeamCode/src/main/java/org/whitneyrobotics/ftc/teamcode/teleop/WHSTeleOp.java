package org.whitneyrobotics.ftc.teamcode.teleop;

import static org.whitneyrobotics.ftc.teamcode.lib.util.RumbleEffects.endgame;
import static org.whitneyrobotics.ftc.teamcode.lib.util.RumbleEffects.matchEnd;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.whitneyrobotics.ftc.teamcode.BetterTelemetry.LineItem;
import org.whitneyrobotics.ftc.teamcode.BetterTelemetry.SliderDisplayLine;
import org.whitneyrobotics.ftc.teamcode.BetterTelemetry.TextLine;
import org.whitneyrobotics.ftc.teamcode.framework.opmodes.OpModeEx;
import org.whitneyrobotics.ftc.teamcode.robotImpl.WHSRobotImpl;
import org.whitneyrobotics.ftc.teamcode.subsys.Robot;

@TeleOp(name="PowerPlay TeleOp", group="A")
@RequiresApi(api = Build.VERSION_CODES.N)
public class WHSTeleOp extends OpModeEx {
    WHSRobotImpl robot;

    void setupGamepads(){
        gamepad1.BUMPER_RIGHT.onPress(e -> robot.drivetrain.toggleFieldCentric());
        gamepad1.SQUARE.onPress(e -> robot.imu.zeroHeading());
        gamepad1.LEFT_TRIGGER.onInteraction(e -> robot.drivetrain.setPowerReduction(e.floatVal));
    }

    void setupNotifications(){
        addTemporalCallback(resolve -> {
            playSound("endgame",100f);
            gamepad1.getEncapsulatedGamepad().runRumbleEffect(endgame);
            gamepad2.getEncapsulatedGamepad().runRumbleEffect(endgame);
            betterTelemetry.addItem(new TextLine("Endgame approaching soon!",true, LineItem.Color.ROBOTICS, LineItem.RichTextFormat.BOLD));
            resolve.accept(!gamepad1.getEncapsulatedGamepad().isRumbling() && gamepad2.getEncapsulatedGamepad().isRumbling());
        }, 5000);

        addTemporalCallback(resolve -> {
            betterTelemetry.removeLineByCaption("Endgame approaching soon!");
            resolve.accept(true);
        },10000);

        addTemporalCallback(resolve -> {
            playSound("matchend",100f);
            gamepad1.getEncapsulatedGamepad().runRumbleEffect(matchEnd);
            gamepad2.getEncapsulatedGamepad().runRumbleEffect(matchEnd);
            betterTelemetry.addItem(new TextLine("Match ends in 5 seconds!",true, LineItem.Color.FUCHSIA, LineItem.RichTextFormat.BOLD));
            resolve.accept(!gamepad1.getEncapsulatedGamepad().isRumbling() && gamepad2.getEncapsulatedGamepad().isRumbling());
        }, 12000);

        addTemporalCallback(resolve -> {
            playSound("slay",100f);
            betterTelemetry.removeLineByCaption("Match ends in 5 seconds!");
            betterTelemetry.addItem(new TextLine("Match has ended.", true, LineItem.Color.LIME, LineItem.RichTextFormat.ITALICS));
            resolve.accept(true);
        },19000);
    }

    @Override
    public void initInternal() {
        robot = new WHSRobotImpl(hardwareMap, gamepad1);
        setupGamepads();
        setupNotifications();
    }

    @Override
    protected void loopInternal() {
        double xPower = Math.pow(gamepad1.LEFT_STICK_X.value(),3);
        double yPower = Math.pow(gamepad1.LEFT_STICK_Y.value(),3);
        double rotPower = Math.pow(gamepad1.RIGHT_STICK_X.value(),3);
        if(gamepad1.BUMPER_LEFT.value()){
            xPower = gamepad1.LEFT_STICK_X.value()/3;
            yPower = gamepad1.LEFT_STICK_Y.value()/3;
            rotPower = gamepad1.RIGHT_STICK_X.value()/3;
        }

        robot.drivetrain.operateByCommand(xPower, yPower, rotPower);
        betterTelemetry.addData("Field centric",robot.drivetrain.fieldCentricEnabled());
    }
}
