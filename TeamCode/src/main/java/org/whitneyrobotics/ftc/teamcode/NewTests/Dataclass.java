package org.whitneyrobotics.ftc.teamcode.NewTests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.whitneyrobotics.ftc.teamcode.lib.util.DataToolsLite;

public class Dataclass extends OpMode {

    boolean[] robotConfig;
    Object[] robotConfigObj;

    String[] configItems = {"Drivetrain", "Intake", "Outtake", "Carousel", "PIDF", "Odometry", "ComputerVision"};
    final int configLength = configItems.length;

    @Override
    public void init() {
        robotConfig = new boolean[configLength];
        robotConfigObj = new Object[configLength];
        String[] decodedConfig = {};
        try {
            decodedConfig = DataToolsLite.decode("robotConfig.txt");
        } catch(Exception e){}
        if (decodedConfig.length != configLength) {
            throw new ArrayIndexOutOfBoundsException("The data array needs have 7 items, not " + Integer.toString(decodedConfig.length));
        }
        for (int i = 0; i < configLength; i++) {
            robotConfig[i] = Boolean.parseBoolean(decodedConfig[i]);
        }
        telemetry.addLine("Current Configuration of the robot:");
        for (int i = 0; i < configLength; i++) {
            telemetry.addData(configItems[i], robotConfig[i]);
        }


    }

    @Override
    public void loop() {
        if (gamepad1.a) {
            for (int i = 0; i < configLength; i++) {
                robotConfig[i] = false;
            }
        }
        if (gamepad1.b) {
            for (int i = 0; i < configLength; i++) {
                robotConfig[i] = true;
            }
        }

        if (gamepad1.x) {
            for (int i = 0; i < configLength; i++) {
                robotConfig[i] = !robotConfig[i];
            }
        }
        for (int i = 0;i < configLength; i++) {
            robotConfigObj[i] = robotConfig[i] ? Boolean.TRUE : Boolean.FALSE;
        }
        DataToolsLite.encode("robotConfig.txt", robotConfigObj);
    }

}
