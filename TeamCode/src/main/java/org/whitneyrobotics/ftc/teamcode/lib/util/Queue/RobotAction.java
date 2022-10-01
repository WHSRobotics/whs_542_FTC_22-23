package org.whitneyrobotics.ftc.teamcode.lib.util.Queue;

/**
 * Take none return none functional interface that stores void robot actions
 */
@FunctionalInterface
public interface RobotAction {
    void invoke ();
}
