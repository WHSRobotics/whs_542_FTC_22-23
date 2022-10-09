package org.whitneyrobotics.ftc.teamcode.subsys;

public class DriveSignal {
    private static double velocityX;

    public double getVelocityX() {
        return velocityX;
    }

    public void setVelocityX(double velocityX) {
        DriveSignal.velocityX = velocityX;
    }

    public double getVelocityY() {
        return velocityY;
    }

    public static void setInstance(DriveSignal instance) {
        DriveSignal.instance = instance;
    }

    public void setVelocityY(double velocityY) {
       this.velocityY= velocityY;
    }

    private double velocityY;

    public double getOmega() {
        return omega;
    }

    public void setOmega(double omega) {
        this.omega = omega;
    }

    private double omega;

    private DriveSignal (){

    }

    private static DriveSignal getInstance(){
        if (instance == null) {
            synchronized (instance) {
                if (instance == null){
                    instance = new DriveSignal();
                }
            }
        }
        return instance;
    }
    private static DriveSignal instance;
}

