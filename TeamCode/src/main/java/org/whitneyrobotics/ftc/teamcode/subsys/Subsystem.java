package org.whitneyrobotics.ftc.teamcode.subsys;

import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareDevice;

import org.firstinspires.ftc.robotcontroller.external.samples.SensorBNO055IMU;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class Subsystem {
    enum SubsystemState {
        BUSY, IDLE, ERROR
    }

    protected Subsystem(HardwareDevice... devices) {
        hardwareList.addAll(0, Arrays.asList(devices));
    }

    protected void addDevice(HardwareDevice device){
        hardwareList.add(device);
    }

    private ArrayList<HardwareDevice> hardwareList = new ArrayList<>();

    protected void internalResetDevicesForOpMode() {
        for(HardwareDevice device : hardwareList){
            device.resetDeviceConfigurationForOpMode();
        }
    }

    abstract void reset();
    public void operate(){};
    public void operate(boolean button){};

    public void stop(){};
    public abstract SubsystemState getState();
    public abstract void initialize();
}
