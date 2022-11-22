package org.whitneyrobotics.ftc.teamcode.MotionProfile;

public class MotionProfileConstantJerk extends MotionProfile {

    public MotionProfileConstantJerk(double initialVal, double finalValue, double usedAcceleration, double maxVelocity) {
        super(initialVal, finalValue, usedAcceleration, maxVelocity);
    }

    public double calculateAccelerationTime(){
        accelBasedMaxVelTime = maxAcceleration / CONSTANT_JERK;
        return accelBasedMaxVelTime;
    }

    public double calculateVelocityTime(){
        velBasedMaxVelTime = Math.sqrt(maxVelocity / CONSTANT_JERK);
        return velBasedMaxVelTime;
    }

    public void calculateUsedTime(){
        preferredTime = Math.max(accelBasedMaxVelTime, velBasedMaxVelTime);
    }

    @Override
    public double calculate(double currentPos, double targetPos) {
        double valChange = Math.abs(finalValue - initialValue);
        boolean cruiseNecessary = ((valChange / 2) - (CONSTANT_JERK * Math.pow(accelBasedMaxVelTime, 3)) - (CONSTANT_JERK * Math.pow(velBasedMaxVelTime, 3)) > valChange);
        if (cruiseNecessary){
            cruiseTime = (valChange - (valChange / 2) - (CONSTANT_JERK * Math.pow(accelBasedMaxVelTime, 3)) - (CONSTANT_JERK * Math.pow(velBasedMaxVelTime, 3))/(CONSTANT_JERK * Math.pow(velBasedMaxVelTime, 2)));
        } else {
            cruiseTime = 0;
            preferredTime = Math.max(((-2 * maxVelocity) + ((Math.pow(maxVelocity, 2)) - (4 * maxAcceleration * -valChange))) / (4 * maxAcceleration), ((-2 * maxVelocity) + ((Math.pow(maxVelocity, 2)) - (4 * maxAcceleration * -valChange))) / (4 * maxAcceleration));
        }
        return Math.signum(valChange);
    }
}
