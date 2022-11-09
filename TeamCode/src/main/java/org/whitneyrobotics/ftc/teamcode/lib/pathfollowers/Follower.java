package org.whitneyrobotics.ftc.teamcode.lib.pathfollowers;

import org.whitneyrobotics.ftc.teamcode.subsys.Drivetrains.Drivetrain;

public abstract class Follower {
    protected Drivetrain drivetrain;

    protected Follower(Drivetrain drivetrain){
        this.drivetrain = drivetrain;
    }
}