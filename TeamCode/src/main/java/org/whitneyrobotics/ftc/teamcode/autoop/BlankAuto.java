package org.whitneyrobotics.ftc.teamcode.autoop;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.whitneyrobotics.ftc.teamcode.framework.opmodes.OpModeEx;

@RequiresApi(api = Build.VERSION_CODES.N)
@Autonomous(name="Blank Auto", group="A", preselectTeleOp = "PowerPlay TeleOp")
public class BlankAuto extends OpModeEx {
    @Override
    public void initInternal() {

    }

    @Override
    protected void loopInternal() {

    }
}
