package org.whitneyrobotics.ftc.teamcode.subsys;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.whitneyrobotics.ftc.teamcode.framework.Subsystem;

public class Robot implements Subsystem {
    public enum RobotStates {
        AUTONOMOUS, DRIVER_CONTROLLED
    }

    public Grabber grabber;

    public Robot(HardwareMap hardwareMap){
        grabber = new Grabber(hardwareMap);
    }

    @Override
    public void resetEncoders() {

    }
}
