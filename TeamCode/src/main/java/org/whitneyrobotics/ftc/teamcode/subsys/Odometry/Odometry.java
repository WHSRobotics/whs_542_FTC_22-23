package org.whitneyrobotics.ftc.teamcode.subsys.Odometry;

public interface Odometry {
    void update();
    void setBias(double angleRad);
}
