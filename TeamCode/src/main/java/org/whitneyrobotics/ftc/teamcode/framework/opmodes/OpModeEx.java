package org.whitneyrobotics.ftc.teamcode.framework.opmodes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.Queue;
import java.util.concurrent.SynchronousQueue;

public abstract class OpModeEx extends OpMode {

    private FtcDashboard dashboard;
    private Telemetry dashboardTelemetry;
    protected TelemetryPacket packet = new TelemetryPacket();

    protected void initializeDashboardTelemetry(int msTransmissionInterval) {
        dashboard = FtcDashboard.getInstance();
        dashboardTelemetry = dashboard.getTelemetry();
        telemetry = new MultipleTelemetry(dashboardTelemetry,telemetry);
        telemetry.setMsTransmissionInterval(msTransmissionInterval);
    }

    public OpModeEx(boolean useFTCDashboard){
        this(useFTCDashboard,100);
    }

    public OpModeEx(boolean useFTCDashboard, int msTransmissionInterval){

    }

    public static class Timeable {

        @FunctionalInterface
        public interface ConditionalCallback {
            boolean call();
        }
        public ConditionalCallback callback;
        public boolean persistent = false;
        public double timeoutMs;
        Timeable(ConditionalCallback callback, double timeoutMs){
            this.callback = callback;
            this.timeoutMs = timeoutMs;
        }
        Timeable(ConditionalCallback callback, double timeoutMs, boolean persistent){
            this.callback = callback;
            this.timeoutMs = timeoutMs;
            this.persistent = persistent;
        }
    }

    private SynchronousQueue<Timeable> queue = new SynchronousQueue<Timeable>();
    protected Timeable current;

    @Override
    public void loop(){
        loopInternal();
        processQueue();
    }

    abstract void loopInternal();

    public void test(){
        addTemporalCallback(() -> {
            gamepad1.rumble(2);
            return true;
        }, 10000);
    }

    private void processQueue(){
        if(current == null) {
            if(queue.size() < 1) return;
            if(getRuntime() >= queue.peek().timeoutMs) return;
            current = queue.remove();
            current.callback.call();
            if(!current.persistent) current = null;
        } else {
            if (current.callback.call()) current = null;
        }
    }

    public OpModeEx addTemporalCallback(Timeable t){
        queue.add(t);
        return this;
    }

    public OpModeEx addTemporalCallback(Timeable.ConditionalCallback callback, double timeoutMs){
        queue.add(new Timeable(callback, timeoutMs));
        return this;
    }
}
