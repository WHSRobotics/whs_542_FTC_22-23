package org.whitneyrobotics.ftc.teamcode.subsys.Odometry;

import org.whitneyrobotics.ftc.teamcode.lib.geometry.Coordinate;

public interface Odometry {
    void update();
    void setInitialPose(Coordinate coordinate);
}
