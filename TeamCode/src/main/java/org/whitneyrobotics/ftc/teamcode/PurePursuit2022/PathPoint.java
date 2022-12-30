package org.whitneyrobotics.ftc.teamcode.PurePursuit2022;

public class PathPoint {
    private double x;
    private double y;
    private double velocity;
    private double curvature;

    public PathPoint (double x, double y, double velocity, double curvature){
        this.x = x;
        this.y = y;
        this.velocity = velocity;
        this.curvature = curvature;
    }

    public PathPoint (double x, double y){
        this.x = x;
        this.y = y;
    }

    public double getX(){
        return x;
    }

    public double getY(){
        return y;
    }

    public void insertCurvature(double curvature){
        this.curvature = curvature;
    }

    public void calculateVelocityAtPoint(double velocity){

    }
}
