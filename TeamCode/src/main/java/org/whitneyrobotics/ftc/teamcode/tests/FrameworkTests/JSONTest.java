package org.whitneyrobotics.ftc.teamcode.tests.FrameworkTests;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ReadWriteFile;

import org.json.JSONException;
import org.json.JSONObject;
import org.whitneyrobotics.ftc.teamcode.BetterTelemetry.LineItem;
import org.whitneyrobotics.ftc.teamcode.BetterTelemetry.TextLine;
import org.whitneyrobotics.ftc.teamcode.framework.opmodes.OpModeEx;
import org.whitneyrobotics.ftc.teamcode.lib.file.RobotDataUtil;
import org.whitneyrobotics.ftc.teamcode.lib.file.WHSRobotData;
import org.whitneyrobotics.ftc.teamcode.subsys.IMU;

import java.util.Arrays;

@TeleOp(name="Persistent Data Store Test", group="Z")
@RequiresApi(api = Build.VERSION_CODES.N)
public class JSONTest extends OpModeEx {
    IMU imu;

    @Override
    public void initInternal() {
        WHSRobotData.reset();
        imu = new IMU(hardwareMap);
        initializeDashboardTelemetry(10);
        RobotDataUtil.load(WHSRobotData.class);
        imu.zeroHeading(WHSRobotData.heading);
        gamepad1.TRIANGLE.onPress(e -> {
            RobotDataUtil.save(WHSRobotData.class);
            requestOpModeStop();
        });

        gamepad1.SQUARE.onPress(e -> imu.zeroHeading());

        String jsonContent = ReadWriteFile.readFile(RobotDataUtil.loadFile(WHSRobotData.class.getName(), ".json"));
        betterTelemetry.addItem(new TextLine(jsonContent,true, LineItem.Color.LIME));
        try {
            betterTelemetry.addItem(new TextLine(new Boolean(new JSONObject(jsonContent).has("heading")).toString(), true, LineItem.Color.GRAY));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void loopInternal() {
        WHSRobotData.heading = imu.getHeading();
        telemetry.addData("Heading",WHSRobotData.heading);
        betterTelemetry.addLine(RobotDataUtil.getClassWriteableFields(WHSRobotData.class)[0].getName(), LineItem.Color.ROBOTICS);
    }
}
