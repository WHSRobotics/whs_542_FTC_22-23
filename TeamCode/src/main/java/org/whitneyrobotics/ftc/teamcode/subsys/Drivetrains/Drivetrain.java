package org.whitneyrobotics.ftc.teamcode.subsys.Drivetrains;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.whitneyrobotics.ftc.teamcode.framework.Subsystem;
import org.whitneyrobotics.ftc.teamcode.framework.Vector;
import org.whitneyrobotics.ftc.teamcode.lib.pathfollowers.Follower;
import org.whitneyrobotics.ftc.teamcode.subsys.IMU;

import java.util.Hashtable;
import java.util.Map;
import java.util.function.Function;

@RequiresApi(api = Build.VERSION_CODES.N)
public abstract class Drivetrain implements Subsystem {
    protected final Map<String, DcMotorEx> motorMap;
    protected double powerReduction = 0;
    protected boolean fieldCentric = true;
    public final IMU imu;
    protected boolean slowdown = false;

    private Follower follower;

    /**
     * To construct an implementing class of Drivetrain, pass in the hardwareMap, and pass in as many motor names as you will need.
     * @param hardwareMap HardwareMap of robot hardware, pass through super().
     * @param motorNames Variadic parameter of motor names to instantiate
     * Note: To retrieve the motors in the child class instance, use the motorMap's {@link Map#get} method.
     */
    public Drivetrain(HardwareMap hardwareMap, IMU imu, String... motorNames){
        if(motorNames.length < 2){
            throw new IllegalArgumentException("Drivetrains must have at least 2 motors.");
        }
        this.imu = imu;
        motorMap = new Hashtable<>();
        for(String name : motorNames){
            DcMotorEx motor = hardwareMap.get(DcMotorEx.class,name);
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
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
    public void setSlowdown(boolean slowdown){
        this.slowdown = slowdown;
    }

    public void operateByCommand(double stickX, double stickY, double rotate){
        Vector vector2D = new Vector(stickX, stickY);
        if(fieldCentric){
            vector2D = vector2D.rotate(imu.getHeadingRadians());
        }
        double rotX = vector2D.get(0,0);
        double rotY = vector2D.get(1,0);
        applyPowersToMotors(rotX, rotY, rotate);
    }

    protected abstract void applyPowersToMotors(double rotatedX, double rotatedY, double angularRotationPower);

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

    public void setFollower(Function<Drivetrain, Follower> followerConstructor){
        followerConstructor.apply(this);
    }

    public boolean fieldCentricEnabled(){
        return fieldCentric;
    }
    public boolean slowMode(){
        return slowdown;
    }
}
