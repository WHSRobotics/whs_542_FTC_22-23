public class motionProfileGeneric {
    protected double constantJerk;
    protected double maxAcceleration;
    protected double usedAcceleration;
    protected double maxVelocity;

    public double initialValue;
    public double finalValue;

    public double accelTime;
    public double cruiseTime = 0;

    public MotionProfileGeneric (double initialVal, double finalValue, double usedAcceleration, double maxVelocity){
        
    }
}