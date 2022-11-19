package org.whitneyrobotics.ftc.teamcode.subsys.Drivetrains;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.whitneyrobotics.ftc.teamcode.lib.geometry.Position;
import org.whitneyrobotics.ftc.teamcode.subsys.IMU;

@RequiresApi(api = Build.VERSION_CODES.N)
public class driveAndRotateTests extends OpMode {
    Position testPostionDrive = new Position(0, 600);
    Position testPositionRotate = new Position(600, 0);

    IMU imu;
    OmniDrivetrain testTrain;

    int testMode = 0;

    @Override
    public void init() {
        IMU imu = new IMU(hardwareMap);
        OmniDrivetrain testTrain = new OmniDrivetrain(hardwareMap, imu);
    }

    @Override
    public void loop() {
        if (gamepad1.a){
            testTrain.resetEncoders();
        }

        if (gamepad1.b){
            testTrain.resetEncoders();
            testMode++;
        }

        if (testMode == 1) {
            testTrain.driveToTarget(testPostionDrive, false);
        } else if (testMode == 2) {
            testTrain.driveToTarget(testPositionRotate, false);
        } else if (testMode == 3) {
            testTrain.driveToTarget(testPostionDrive, true);
        } else if (testMode == 4) {
            testTrain.driveToTarget(testPositionRotate, true);
        } else if (testMode > 4) {
            testMode = 0;
        }

        telemetry.addData("Current Test Mode: ", testMode);
    }
}
