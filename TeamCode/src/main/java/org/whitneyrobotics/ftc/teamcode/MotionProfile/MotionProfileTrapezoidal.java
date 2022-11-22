package org.whitneyrobotics.ftc.teamcode.MotionProfile;

public class MotionProfileTrapezoidal extends MotionProfile{
    double lastRecordedTime;
    private double deadband;
    String phase = "REST";
    public boolean finished = false;
    public boolean firstCall = true;
    public double maxVelocity; //janky overload
    public double currentVelocity = 0.0d;
    public MotionProfileTrapezoidal(double initialPos, double constantAccel, double maxVelocity, double deadband) {
        super(initialPos, initialPos, constantAccel, maxVelocity);
        lastRecordedTime = System.nanoTime()/1E9;
        this.maxVelocity = maxVelocity;
        this.deadband = deadband;
    }

    public MotionProfileTrapezoidal(double initialPos, double constantAccel, double maxVelocity){
        this(initialPos, constantAccel, maxVelocity, 1.0d);
        lastRecordedTime = System.nanoTime()/1E9;
    }

    public boolean isFinished(){
        return finished;
    }

    @Override
    public double calculate(double currentPos, double targetPos) {
        if(firstCall){
            lastRecordedTime = System.nanoTime()/1E9;
            firstCall = false;
            return 0.0d;
        }
        int direction = (currentPos > targetPos) ? -1 : 1;
        double currentTime = System.nanoTime()/1E9;
        double deltaTime = currentTime - lastRecordedTime;
        double output;

        if(currentVelocity < maxVelocity){
            output = currentVelocity + direction * usedAcceleration * (deltaTime);
        } else {
            output = maxVelocity;
        }

        if(Math.pow(currentVelocity, 2)/(2*maxAcceleration) >= Math.abs(targetPos-currentPos)){
            output = currentVelocity - direction * usedAcceleration * deltaTime;
        }

        if(Math.abs(targetPos-currentPos)<=deadband){
            finished = true;
            return 0.0d;
        }
        currentVelocity = output;

        lastRecordedTime = currentTime;
        return output;
    }
}
