package org.whitneyrobotics.ftc.teamcode.framework.opmodes;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.ftccommon.SoundPlayer;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.whitneyrobotics.ftc.teamcode.BetterTelemetry.BetterTelemetry;
import org.whitneyrobotics.ftc.teamcode.BetterTelemetry.LineItem;
import org.whitneyrobotics.ftc.teamcode.GamepadEx.GamepadEx;
import org.whitneyrobotics.ftc.teamcode.tests.Test;

import java.util.PriorityQueue;
import java.util.function.Consumer;

@RequiresApi(api = Build.VERSION_CODES.N)
public abstract class OpModeEx extends OpMode {

    protected GamepadEx gamepad1;
    protected GamepadEx gamepad2;

    protected BetterTelemetry betterTelemetry = BetterTelemetry.setOpMode(this);

    private FtcDashboard dashboard = FtcDashboard.getInstance();
    protected Telemetry telemetry;
    protected TelemetryPacket packet = new TelemetryPacket();

    protected void initializeDashboardTelemetry(int msTransmissionInterval) {
        telemetry = dashboard.getTelemetry();
        telemetry = new MultipleTelemetry(telemetry,telemetry);
        telemetry.setMsTransmissionInterval(msTransmissionInterval);
    }
/*    public OpModeEx(boolean useFTCDashboard){
        this(useFTCDashboard,100);
    }

    public OpModeEx(boolean useFTCDashboard, int msTransmissionInterval){
        if(useFTCDashboard) initializeDashboardTelemetry(msTransmissionInterval);
    }*/

    public static class Timeable {

        @FunctionalInterface
        public interface TemporalCallback {
            void call(Consumer<Boolean> resolve);
        }
        public TemporalCallback callback;
        public boolean persistent = false;
        public double timeoutMs;
        Timeable(TemporalCallback callback, double timeoutMs){
            this.callback = callback;
            this.timeoutMs = timeoutMs;
        }
        Timeable(TemporalCallback callback, double timeoutMs, boolean persistent){
            this.callback = callback;
            this.timeoutMs = timeoutMs;
            this.persistent = persistent;
        }
    }

    private PriorityQueue<Timeable> queue = new PriorityQueue<>(1, (o1, o2) -> {
        if(o1.timeoutMs == o2.timeoutMs) return 0;
        if(o1.timeoutMs < o2.timeoutMs) return -1;
        return 1;
    });
    protected Timeable current;

    @Override
    public final void init(){
        Test testingAnnotation = this.getClass().getDeclaredAnnotation(Test.class);
        if(testingAnnotation != null){
            if(testingAnnotation.autoTerminateAfterSeconds() > 0){
                addTemporalCallback(resolve -> requestOpModeStop(), testingAnnotation.autoTerminateAfterSeconds()*1000);
            }
        }
        gamepad1 = new GamepadEx(super.gamepad1);
        gamepad2 = new GamepadEx(super.gamepad2);
        initInternal();
    }

    public abstract void initInternal();

    @Override
    public final void start() {
        resetRuntime();
        startInternal();
    }

    public void startInternal(){

    }

    @Override
    public final void loop(){
        packet = new TelemetryPacket();
        loopInternal();
        processQueue();
        betterTelemetry.update();
        gamepad1.update();
        gamepad2.update();
        dashboard.sendTelemetryPacket(packet);
    }

    protected abstract void loopInternal();

    private void advanceQueue(boolean shouldAdvance){
        if(shouldAdvance) current = null;
    }

    private void processQueue(){
        if(current == null) {
            if(queue.size() < 1) return;
            if(getRuntime() <= (queue.peek().timeoutMs)/1000) return;
            current = queue.remove();
            current.callback.call(this::advanceQueue);
            if(current != null && !current.persistent) current = null;
        }
    }

    public OpModeEx addTemporalCallback(Timeable t){
        queue.add(t);
        return this;
    }

    public OpModeEx addTemporalCallback(Timeable.TemporalCallback callback, double timeoutMs){
        queue.add(new Timeable(callback, timeoutMs));
        return this;
    }

    protected void playSound(String identifier){ playSound(identifier,"raw",50.0f); }

    protected void playSound(String identifier, float volume){
        playSound(identifier, "raw", volume);
    }

    protected void playSound(String identifier, String folder, float volume){
        int id = hardwareMap.appContext.getResources().getIdentifier(identifier, folder, hardwareMap.appContext.getPackageName());
        if(id == 0){
            betterTelemetry.addLine(String.format("Sound '%s' not found in '%s'", identifier,folder), LineItem.Color.MAROON);
        } else {
            SoundPlayer.getInstance().setMasterVolume(volume);
            SoundPlayer.getInstance().startPlaying(hardwareMap.appContext, id);
        }
    }
}
