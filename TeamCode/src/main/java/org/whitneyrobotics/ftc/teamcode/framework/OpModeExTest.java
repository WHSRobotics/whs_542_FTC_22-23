package org.whitneyrobotics.ftc.teamcode.framework;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.whitneyrobotics.ftc.teamcode.BetterTelemetry.BetterTelemetry;
import org.whitneyrobotics.ftc.teamcode.BetterTelemetry.LineItem;
import org.whitneyrobotics.ftc.teamcode.framework.opmodes.OpModeEx;

@TeleOp(name="Better Telemetry Test")
@RequiresApi(api = Build.VERSION_CODES.N)
public class OpModeExTest extends OpModeEx {
    @Override
    public void init() {
        initializeDashboardTelemetry(50);

        addTemporalCallback(resolve -> {
            playSound("slay");
            resolve.accept(true);
        }, 2000);
        addTemporalCallback(resolve -> {
            betterTelemetry.addLine("Hello!", LineItem.Color.RED);
            resolve.accept(true);
        }, 5000);
    }

    @Override
    protected void loopInternal() {

    }
}
