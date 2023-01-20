package org.whitneyrobotics.ftc.teamcode.lib.control;

import org.whitneyrobotics.ftc.teamcode.MotionProfile.MotionProfileTrapezoidal;

import java.util.function.Supplier;

public class PIDVAControllerNew {
    public static double kP = 1;
    public double getKP() { return kP; }
    public void setKP(double kP) { this.kP = kP; }

    public static double kI = 0;
    public double getKI() { return kI; }
    public void setKI(double kI) { this.kI = kI; }

    public static double kD = 0;
    public double getKD() { return kD; }
    public void setKD(double kD) { this.kD = kD; }

    public static double kA = 0;
    public double getKA() { return kA; }
    public void setKA(double kA) { this.kA = kA; }

    public static double kV = 0;
    public double getKV() { return kV; }
    public void setKV(double kV) { this.kV = kV; }

    public static double kStatic = 0;
    public double getkStatic() {return kStatic; }
    public void setkStatic(double kStatic) { this.kStatic = kStatic; }

    public double maxVelocity;
    public double maxAcceleration;

    public String phase = "IDLE";
    public String FTCDashboardTest = "hi";

    //private PIDCoefficientsNew.FeedForwardProvider FeedforwardCalculation = (double t, double c, long time) -> 0;
    /*public void setFeedforwardCalculation(PIDCoefficientsNew.FeedForwardProvider f) {
        this.FeedforwardCalculation = f;
    }*/

    /**
     * Create a PID Controller with given constant values.
     * @param kP
     * @param kI
     * @param kD
     * @param kV
     * @param kA
     */
    public PIDVAControllerNew(double kP, double kI, double kD, double kV, double kA, double maxVelocity, double maxAcceleration){
        this(kP, kI, kD, kV, kA, 0, maxVelocity, maxAcceleration);
    }

    public PIDVAControllerNew(double kP, double kI, double kD, double kV, double kA, double kStatic, double maxVelocity, double maxAcceleration){
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
        this.kV = kV;
        this.kA = kA;
        this.kStatic = kStatic;
        this.maxVelocity = maxVelocity;
        this.maxAcceleration = maxAcceleration;
    }


    private double lastKnownTime = 0;
    private double lastKnownError = 0;
    private double lastKnownInput = 0;

    /*
    public void setTarget(double target, double current) {
        //motionProfile = new MotionProfileTrapezoidal(0,maxAcceleration,maxVelocity);
        this.target = target;
    }
     */

    private double target = 0;

    public long getIntegral() {
        return integral;
    }

    public double getDerivative() {
        return derivative;
    }

    private long integral = 0;
    private double derivative = 0;

    /**
     * Resets the last known time, input, error, and integral of the PID controller. This method is internally called when setting a new static target.
     */
    public void reset() {
        lastKnownTime = System.nanoTime() / 1E9;
        lastKnownError = target - lastKnownInput;
        velocity = 0;
        outputAcceleration = 0;
        integral = 0;
    }

    public void setTarget(double target){
        this.target = target;
    }

    /**
     * Calculate function for a changing target.
     * @param target Current target
     * @param current Current state of the system
     * @see #calculate(double)
     */
    //public MotionProfileTrapezoidal motionProfile;
    public void calculate(double target, double current){
        setTarget(target);
        calculate(current);
    }

    public double velocity = 0;
    public double outputAcceleration = 0;

    /*
    private Supplier<Double> velocityProvider = () -> 0.0d;
    public void setVelocityProvider(Supplier<Double> velocityProvider){
        this.velocityProvider = velocityProvider;
    }
     */

    /**
     * Calculates the current error, integral, and derivative of the PID controller and caches results.
     * @param current current state of the system
     * @see #getOutput()
     */
    public void calculate(double current){
        double error = target-current;

        //Integral
        double currentTime = System.nanoTime()/1E9;
        double deltaTime =  (currentTime - lastKnownTime);
        lastKnownTime = currentTime;
        integral += ((0.5 * (lastKnownError + error))*deltaTime); //trapezoidal riemann sum

        //Derivative
        double deltaError = error - lastKnownError;
        derivative = deltaError / deltaTime;
        lastKnownError = error;
        lastKnownInput = current;

        int direction = 1;
        //direction
        if (error < 0){
            direction = -1;
        }
        double outputVelocity;

        if (Math.abs(velocity) < maxVelocity){
            outputVelocity = velocity + direction * maxAcceleration * deltaTime;
            outputAcceleration = direction * maxAcceleration;
            phase = "RAMP UP";
        } else {
            outputVelocity = direction*maxVelocity;
            outputAcceleration = 0;
            phase = "CRUISE";
        }

        if(Math.abs(error) <= Math.pow(velocity,2)/(2*maxAcceleration)){
            outputVelocity = velocity - direction * maxAcceleration * deltaTime;
            outputAcceleration = -direction * maxAcceleration;
            phase = "RAMP DOWN";
        }
        velocity = outputVelocity;
    }

    /**
     * Retrieves the value of the PID Controller with adjustments from coefficients.
     * @return Value of the PID controller. It's recommended to pass this to a scaling or clamping function before applying it to the system.
     */
    public double getOutput(){
        return (kP * lastKnownError) + (kI * integral) + (kD * derivative) + (kV * velocity) +(kA * outputAcceleration) + kStatic;
    }
}
