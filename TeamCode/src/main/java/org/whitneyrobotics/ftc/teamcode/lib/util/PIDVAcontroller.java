package org.whitneyrobotics.ftc.teamcode.lib.util;

public class PIDVAcontroller {
    double maxVelocity;
    double maxAcceleration;
    double desiredPos;
    double expectedPosChange;
    double lastPos = 0;
    double lastVel = 0;
    double lastTime = 0;

    public PIDVAcontroller(double mV, double mA)  {
        maxVelocity = mV;
        maxAcceleration = mA;
        expectedPosChange = (maxVelocity*maxVelocity)/(2*maxAcceleration);
    }

    /**
     * @implNote Call to set variables needed before output loop
     * @param desiredPos dp
     * @param lastPos lp
     * @param time currentTime
     */
    public void setDesiredPos(double desiredPos,double lastPos,double time) {
        this.desiredPos = desiredPos;
        this.lastPos = lastPos;
        this.lastTime = time;
        this.lastVel = 0;
    }

    /**
     *
     * @return power of motor
     */
    public double output(double currentPos, double currentTime) {
        double posChange = currentPos-lastPos;
        double direction = Math.signum(desiredPos - currentPos);
        double posError = Math.abs(desiredPos - currentPos);
        double timeElapsed = currentTime-lastTime;

        double currentVelocity = posChange/timeElapsed;
        double velChange = Math.abs(currentVelocity-lastVel);

        double currentAcceleration = velChange/timeElapsed;

        double realMaxVelocity = Functions.clamp(direction*Math.sqrt(currentAcceleration*posError)
                ,-maxVelocity,
                maxVelocity);
        double acceleratePos = (realMaxVelocity*realMaxVelocity)/(2*currentAcceleration);

        if (posError > 0) {
            if (posError > acceleratePos) {
                return direction;
            } else {
                return -1 * direction;
            }
        }


        lastTime = currentTime;
        lastPos = currentPos;
        lastVel = currentVelocity;
        return 0.0;
    }
}

