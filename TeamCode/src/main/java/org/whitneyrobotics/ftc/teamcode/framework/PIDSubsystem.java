package org.whitneyrobotics.ftc.teamcode.framework;

import org.whitneyrobotics.ftc.teamcode.lib.util.RobotConstants;
/**
 * Created by Perry on 2/15/2021
 * */
public interface PIDSubsystem extends Subsystem {

    /**
     * Reassign PID controllers to updated definitions in RobotConstants after FTC Dashboard has edited them.
     @see RobotConstants */
    void reloadConstants();

}
