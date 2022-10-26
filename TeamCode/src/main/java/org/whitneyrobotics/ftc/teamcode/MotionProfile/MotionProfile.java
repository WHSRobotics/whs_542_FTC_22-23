package org.whitneyrobotics.ftc.teamcode.MotionProfile;

public abstract class MotionProfile {
    protected double constantJerk;
    protected double maxAcceleration;
    protected double usedAcceleration;
    protected double maxVelocity;

    public double initialValue;
    public double finalValue;

    public double accelTime;
    public double cruiseTime = 0;

    public MotionProfile(double initialVal, double finalValue, double usedAcceleration, double maxVelocity){

    }

    public abstract double calculate(double time);
}