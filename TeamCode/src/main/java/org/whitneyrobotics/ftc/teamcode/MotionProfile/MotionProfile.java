package org.whitneyrobotics.ftc.teamcode.MotionProfile;

public abstract class MotionProfile {
    protected final double CONSTANT_JERK = 0;
    protected double maxAcceleration = 0;
    protected double usedAcceleration;
    protected double maxVelocity;

    public double initialValue;
    public double finalValue;

    public double accelTime;
    public double accelBasedMaxVelTime;
    public double velBasedMaxVelTime;

    public double preferredTime;

    public double cruiseTime = 0;

    public MotionProfile(double initialVal, double finalValue, double usedAcceleration, double maxVelocity){
        initialValue = initialVal;
        this.finalValue = finalValue;
        this.usedAcceleration = usedAcceleration;
        this.maxVelocity = maxVelocity;
    }

    public abstract double calculate();
}