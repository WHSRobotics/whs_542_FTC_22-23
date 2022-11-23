package org.whitneyrobotics.ftc.teamcode.MotionProfile;

public class MotionProfileTrapezoidal extends MotionProfile{
    public double lastRecordedTime;
    private double deadband;
    public String phase = "REST";
    public boolean finished = false;
    public boolean firstCall = true;
    public double maxVelocity; //janky overload
    public double currentVelocity = 0.0d;
    public double currentAcceleration = 0.0d;

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
        int direction = (targetPos - currentPos < 0) ? -1 : 1;
        double currentTime = System.nanoTime()/1E9;
        double deltaTime = currentTime - lastRecordedTime;
        double output;

        if (Math.abs(currentVelocity) >= maxVelocity){
            output = direction * maxVelocity;
            currentAcceleration = 0;
            phase = "Cruise";
        } else {
            output = currentVelocity + direction * usedAcceleration * (deltaTime);
            currentAcceleration = direction*usedAcceleration;
            phase = "Ramp up";
        }

        if((Math.pow(currentVelocity, 2)/(2*usedAcceleration)) >= Math.abs(targetPos-currentPos)){
            output = currentVelocity  - (direction * usedAcceleration * deltaTime);
            currentAcceleration = -usedAcceleration * direction;
            phase = "Ramp down";
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
