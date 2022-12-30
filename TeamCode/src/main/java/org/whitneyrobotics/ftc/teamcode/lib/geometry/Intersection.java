package org.whitneyrobotics.ftc.teamcode.lib.geometry;

public class Intersection {
    public boolean intersects = false;
    public double distanceFromCenter = 0.0d;

    public Intersection(){}
    public Intersection(boolean intersects, double distanceFromCenter){
        this.intersects = intersects;
        this.distanceFromCenter = distanceFromCenter;
    }
}
