package org.whitneyrobotics.ftc.teamcode.Teaching;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.whitneyrobotics.ftc.teamcode.BetterTelemetry.LineItem;
import org.whitneyrobotics.ftc.teamcode.BetterTelemetry.ProgressBarLine;
import org.whitneyrobotics.ftc.teamcode.BetterTelemetry.TextLine;
import org.whitneyrobotics.ftc.teamcode.framework.opmodes.OpModeEx;

import java.util.function.Consumer;

@RequiresApi(api = Build.VERSION_CODES.N)
public class BetterTelemetryOpMode extends OpModeEx {

    /**
     * TextLine
     * KeyValueLine
     * MultipleChoicePoll
     * SeparatorLine
     * ProgressDisplayLine
     *  | -> Slider display line
     */

    ProgressBarLine timerProgress = new ProgressBarLine("Timer", this::getRuntime, LineItem.Color.AQUA);

    @Override
    public void initInternal() {
        initializeDashboardTelemetry(10);
        betterTelemetry.addItem(new TextLine("ABC", true, LineItem.Color.AQUA, LineItem.RichTextFormat.UNDERLINE));
        betterTelemetry.addItem(timerProgress);
        gamepad1 = gamepad1;
    }

    @Override
    protected void loopInternal() {
        betterTelemetry.addData("x", 1, LineItem.Color.YELLOW, LineItem.RichTextFormat.BOLD, LineItem.RichTextFormat.ITALICS);
        betterTelemetry.addData("lazy", () -> Math.random());
        betterTelemetry.addLine("ABC");
    }
}
