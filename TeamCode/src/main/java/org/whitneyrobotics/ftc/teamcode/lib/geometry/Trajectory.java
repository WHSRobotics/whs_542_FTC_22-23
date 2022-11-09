package org.whitneyrobotics.ftc.teamcode.lib.geometry;

public interface Trajectory {
    double length();
    Position closestPositionTo(Position currentPos);
}
