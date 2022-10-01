package org.whitneyrobotics.ftc.teamcode.lib.geometry;

import java.util.Objects;

/**
 * Class for storing positions on the field
 *
 * @see Coordinate - Alternative class, with heading
 */

public class Position {
    private double xPos;
    private double yPos;

    public Position(double x, double y) {
        xPos = x;
        yPos = y;
    }

    public void scale(double scaleFactor) {
        xPos = xPos* scaleFactor;
        yPos = yPos*scaleFactor;
    }

    public void addX(double x) {
        xPos += x;
    }

    public void addY(double y) {
        yPos += y;
    }

    public double getMagnitude() {
        return Math.hypot(xPos, yPos);
    }

    public double getX() {
        return xPos;
    }

    public double getY() {
        return yPos;
    }
/*
    public double getHeading() {
        return Double.NaN;
    }*/

    public void setX(double x) {
        xPos = x;
    }

    public void setY(double y) {
        yPos = y;
    }

    @Override
    public String toString(){
        return "(" + xPos + ", " + yPos + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return Double.compare(position.xPos, xPos) == 0 && Double.compare(position.yPos, yPos) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(xPos, yPos);
    }
}
