package org.whitneyrobotics.ftc.teamcode.lib.Hardware;

import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchDevice;
import com.qualcomm.robotcore.hardware.configuration.annotations.DeviceProperties;
import com.qualcomm.robotcore.hardware.configuration.annotations.I2cDeviceType;

@DeviceProperties(name="Adafruit DotStar (SPI Bridge)", description="Adafruit DotStar (SPI Bridge)", xmlTag = "SK9822 (SC18IS602B)")
public class AdafruitDotStarWithSPIBridge extends I2cDeviceSynchDevice<I2cDeviceSynch> {
    protected AdafruitDotStarWithSPIBridge(I2cDeviceSynch deviceClient) {


        super(deviceClient, true);

        this.deviceClient.setI2cAddress(I2cAddr.create8bit(0x0101000));

        super.registerArmingStateCallback(false);
        this.deviceClient.engage();
    }

    protected void writeShort(short value){
        //deviceClient.write();
    }
    /**
     * Actually carries out the initialization of the instance.
     *
     * @return Whether the initialization was successful or not
     */
    @Override
    protected boolean doInitialize() {
        return true;
    }

    /**
     * Returns an indication of the manufacturer of this device.
     *
     * @return the device's manufacturer
     */
    @Override
    public Manufacturer getManufacturer() {
        return Manufacturer.Adafruit;
    }

    /**
     * Returns a string suitable for display to the user as to the type of device.
     * Note that this is a device-type-specific name; it has nothing to do with the
     * name by which a user might have configured the device in a robot configuration.
     *
     * @return device manufacturer and name
     */
    @Override
    public String getDeviceName() {
        return "Adafruit DotMatrix SK9822";
    }
}
