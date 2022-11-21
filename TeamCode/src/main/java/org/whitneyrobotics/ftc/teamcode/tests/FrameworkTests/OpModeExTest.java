package org.whitneyrobotics.ftc.teamcode.tests.FrameworkTests;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.whitneyrobotics.ftc.teamcode.BetterTelemetry.BetterTelemetry;
import org.whitneyrobotics.ftc.teamcode.BetterTelemetry.LineItem;
import org.whitneyrobotics.ftc.teamcode.BetterTelemetry.ProgressBarLine;
import org.whitneyrobotics.ftc.teamcode.BetterTelemetry.SliderDisplayLine;
import org.whitneyrobotics.ftc.teamcode.BetterTelemetry.TextLine;
import org.whitneyrobotics.ftc.teamcode.framework.opmodes.OpModeEx;
import org.whitneyrobotics.ftc.teamcode.lib.util.Functions;

@TeleOp(name="Better Telemetry Test")
@RequiresApi(api = Build.VERSION_CODES.N)
public class OpModeExTest extends OpModeEx {

    private double progress = 0.0d;
    private double scroll = 0.0;

    private double power = 0.0d;

    public double getProgress(){
        return getRuntime();
    }

    @Override
    public void initInternal() {
        gamepad1.A.onPress(event -> betterTelemetry.toggleLineNumbers());

        initializeDashboardTelemetry(50);

        addTemporalCallback(resolve -> {
            playSound("slay",50.0f);
            resolve.accept(true);
        }, 2000);
        addTemporalCallback(resolve -> {
            betterTelemetry.addLine("Hello!", LineItem.Color.RED);
            resolve.accept(true);
        }, 5000);

        ProgressBarLine timeProgress = new ProgressBarLine("Time",this::getProgress, LineItem.Color.FUCHSIA).setScale(20);
        betterTelemetry.addItem(timeProgress);

        SliderDisplayLine scrollSlider = new SliderDisplayLine("scrollSlider",() -> scroll, LineItem.Color.AQUA);
        scrollSlider.setScale(1000);
        betterTelemetry.addItem(scrollSlider);

        SliderDisplayLine rightToggle = new SliderDisplayLine("Right Trigger", () -> (double) gamepad1.RIGHT_TRIGGER.value(), LineItem.Color.LIME);
        betterTelemetry.addItem(rightToggle);
        Double.valueOf(0.1f);
        gamepad1.RIGHT_STICK_X.onInteraction(event -> {
            //betterTelemetry.addLine(String.format("Stick moved to %s", event.floatVal), LineItem.Color.ROBOTICS);
            scroll = Functions.clamp((scroll + event.floatVal) * (gamepad1.CIRCLE.value() ? 5 : 1) ,0,1000);
        });

        gamepad1.TRIANGLE.onDoublePress(event -> {
            betterTelemetry.addLine(String.format("Button pressed %s times",event.consecutivePresses), LineItem.Color.PURPLE, LineItem.RichTextFormat.BOLD)
                    .persistent();
        });

        gamepad1.SQUARE.onButtonHold(event -> {
            betterTelemetry.addItem(new TextLine(String.format("Button held!",event.consecutivePresses), true, LineItem.Color.FUCHSIA, LineItem.RichTextFormat.BOLD));
        });

        Gamepad og = gamepad1.getEncapsulatedGamepad();
        og.setLedColor(255,221,0,5000);
    }

    @Override
    protected void loopInternal() {
        //double magnitude = Math.abs(gamepad1.RIGHT_STICK_X.value());
        //gamepad1.getEncapsulatedGamepad().rumble(magnitude, magnitude, 10);
    }
}
