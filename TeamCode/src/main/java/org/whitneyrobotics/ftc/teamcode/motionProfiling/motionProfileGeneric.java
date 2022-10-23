package org.whitneyrobotics.ftc.teamcode.motionProfiling;

public class motionProfileGeneric {
    protected double constantJerk;
    protected double maxAcceleration;
    protected double usedAcceleration;
    protected double maxVelocity;

    public double initialValue;
    public double finalValue;

    public double accelTime;
    public double cruiseTime = 0;

    public motionProfileGeneric (double initialVal, double finalValue, double usedAcceleration, double maxVelocity){
        
    }
}