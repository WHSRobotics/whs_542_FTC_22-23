package org.whitneyrobotics.ftc.teamcode.lib.util;

public class PIDVAcontroller {
    double maxVelocity;
    double maxAcceleration;
    double desiredPos;
    double expectedPosChange;
    double lastPos = 0;
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
    }

    /**
     *
     * @return
     */
    public double output(double currentPos, double currentTime) {
        double posChange = currentPos-lastPos;
        double posError = desiredPos - currentPos;
        double timeElapsed = currentTime-lastTime;

        double currentVelocity = posChange/timeElapsed;

        if (posError != 0) {
            if (posError > expectedPosChange) {
                return 1.0;
            }
        }

        lastTime = currentTime;
        lastPos = currentPos;

        return 0.0;
    }
}

