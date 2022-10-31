package org.whitneyrobotics.ftc.teamcode.tests;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.whitneyrobotics.ftc.teamcode.framework.opmodes.OpModeEx;

@TeleOp(name="IMU Rotation Test", group="Z")
@RequiresApi(api = Build.VERSION_CODES.N)
public class IMURotationTest extends OpModeEx {
    BNO055IMU imu;

    @Override
    public void initInternal() {
        imu = hardwareMap.get(BNO055IMU.class, "imu");
        initializeDashboardTelemetry(10);
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.RADIANS;
        imu.initialize(parameters);
    }

    @Override
    protected void loopInternal() {
        double theta = imu.getAngularOrientation().firstAngle;

        packet.fieldOverlay().clear();
        packet.fieldOverlay()
                .setStrokeWidth(1)
                .setStroke("#00000044")
                .strokeCircle(0,0,50)
                .setStroke("blue")
                .strokeLine(0,0,50*Math.cos(theta), 50*Math.sin(theta))
                .setStroke("red")
                .strokeLine(0,0,50,0);
        packet.put("x",300*Math.cos(theta));
        packet.put("y",300*Math.sin(theta));
        telemetry.addData("theta",theta);
    }
}
