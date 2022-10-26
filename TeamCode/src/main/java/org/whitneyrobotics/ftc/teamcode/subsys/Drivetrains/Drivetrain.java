package org.whitneyrobotics.ftc.teamcode.subsys.Drivetrains;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.whitneyrobotics.ftc.teamcode.framework.Subsystem;
import org.whitneyrobotics.ftc.teamcode.framework.Vector;

import java.util.Hashtable;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.N)
public abstract class Drivetrain implements Subsystem {
    protected final Map<String, DcMotorEx> motorMap;
    protected double powerReduction = 0;
    protected boolean fieldCentric = true;

    /**
     * To construct an implementing class of Drivetrain, pass in the hardwareMap, and pass in as many motor names as you will need.
     * @param hardwareMap HardwareMap of robot hardware, pass through super().
     * @param motorNames Variadic parameter of motor names to instantiate
     * Note: To retrieve the motors in the child class instance, use the motorMap's {@link Map#get} method.
     */
    public Drivetrain(HardwareMap hardwareMap, String... motorNames){
        if(motorNames.length < 2){
            throw new IllegalArgumentException("Drivetrains must have at least 2 motors.");
        }
        motorMap = new Hashtable<>();
        for(String name : motorNames){
            DcMotorEx motor = hardwareMap.get(DcMotorEx.class,name);
            motorMap.put(name, motor);
        }
    }

    public void toggleFieldCentric(){
        fieldCentric = !fieldCentric;
    }

    /**
     * Modifies the power reduction for actions like braking.
     */
    public void setPowerReduction(double reduction){
        powerReduction = reduction;
    }

    public void operateByCommand(double stickX, double stickY, double rotate){
        Vector vector2D = new Vector(stickX, stickY);
    }

    protected abstract void applyPowersToMotors(double translatedX, double translatedY, double angularRotationPower);

    /**
     * Standardized reset method for resetting encoders
     */
    @Override
    public void resetEncoders() {
        for(DcMotor motor : motorMap.values()){
            if(motor != null) {
                motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            }
        }
    }
}
