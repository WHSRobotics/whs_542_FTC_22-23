package org.whitneyrobotics.ftc.teamcode.tests.FrameworkTests;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.whitneyrobotics.ftc.teamcode.BetterTelemetry.Folder;
import org.whitneyrobotics.ftc.teamcode.BetterTelemetry.LineItem;
import org.whitneyrobotics.ftc.teamcode.BetterTelemetry.MultipleChoicePoll;
import org.whitneyrobotics.ftc.teamcode.BetterTelemetry.NumberSliderPoll;
import org.whitneyrobotics.ftc.teamcode.BetterTelemetry.TextLine;
import org.whitneyrobotics.ftc.teamcode.framework.opmodes.OpModeEx;
import org.whitneyrobotics.ftc.teamcode.tests.Test;

import java.util.Arrays;

@RequiresApi(api = Build.VERSION_CODES.N)
@TeleOp(name="Interactable Test")
@Test(name="Interactable Test", description = "Testing interactable items with BetterTelemetry")
public class InteractablesTest extends OpModeEx {

    void setupTelemetry(){
        MultipleChoicePoll m = new MultipleChoicePoll("Single Select:", false, new MultipleChoicePoll.MultipleChoiceOption("Option 1", 1))
                .addOption("Option 2", 2)
                .addOption("Option 3", 3);
        m.onChangeSingle(o -> betterTelemetry.addLine(o.toString(), LineItem.Color.GRAY).persistent());
        MultipleChoicePoll m2 = new MultipleChoicePoll("Multi Select:", true, LineItem.Color.AQUA, new MultipleChoicePoll.MultipleChoiceOption("Option 1", 1))
                .addOption("Option 2",2)
                .addOption("Option 3", 3);
        m2.onChange(o -> betterTelemetry.addLine(Arrays.toString(o), LineItem.Color.BLUE).persistent());
        Folder f = new Folder("Test Folder", new TextLine("Nested Item", true, LineItem.Color.WHITE));
        Folder f2 = new Folder("Test Folder 2");
        NumberSliderPoll slider =  new NumberSliderPoll.NumberSliderPollBuilder("Slider", LineItem.Color.PURPLE)
                .setMin(-25)
                .setMax(25)
                .setSmallStep(0.5)
                .setLargeStep(5)
                .allowWrap(true)
                .build();


        betterTelemetry.addItem(f);
        betterTelemetry.addItem(f2);
        betterTelemetry.addItem(m);
        betterTelemetry.addItem(m2);
        betterTelemetry.addItem(slider);
        betterTelemetry.addLine().persistent();
    }

    void setupGamepads(){
        gamepad1.Y.onPress(e -> betterTelemetry.removeLineByCaption("Test Folder"));
    }
    /**
     * User defined init method
     * <p>
     * This method will be called once when the INIT button is pressed.
     */
    @Override
    public void initInternal() {
        setupGamepads();
        betterTelemetry.setInteractingGamepad(gamepad1);
        setupTelemetry();
    }

    /**
     * User defined loop method
     * <p>
     * This method will be called repeatedly in a loop while this op mode is running
     */
    @Override
    public void loopInternal() {

    }
}
