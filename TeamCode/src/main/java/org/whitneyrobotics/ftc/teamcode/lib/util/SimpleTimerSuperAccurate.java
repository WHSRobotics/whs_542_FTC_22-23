package org.whitneyrobotics.ftc.teamcode.lib.util;

import com.qualcomm.robotcore.util.RobotLog;

public class SimpleTimerSuperAccurate extends SimpleTimer {

    public SimpleTimerSuperAccurate(){super();}
    @Override
    public void set(double timerDuration){
        startTime = (double)System.nanoTime() / 1E9;
        expirationTime = startTime + timerDuration;
    }

    @Override
    public void clear(){
        expirationTime = (double) System.nanoTime() / 1E9;
    }

    @Override
    public double getTime(){
        return (double) System.nanoTime() / 1E9 - startTime;
    }

    @Override
    public double getTimeElapsed(){
        return getTime()-expirationTime;
    }

    @Override
    public boolean isExpired()
    {
        RobotLog.a("hi");
        //DbgLog.msg("whs isExpired entered");
        double currentTime = (double)System.nanoTime() / 1E9;//(double) System.currentTimeMillis() / 1000; //time in seconds
        //DbgLog.msg("whs currentTime found");
        if(expirationTime < currentTime)
        {
            //DbgLog.msg("already expired");
        }
        return (currentTime > expirationTime);
    }


}
