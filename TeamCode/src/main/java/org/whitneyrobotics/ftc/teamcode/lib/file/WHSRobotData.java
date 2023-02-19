package org.whitneyrobotics.ftc.teamcode.lib.file;
import org.whitneyrobotics.ftc.teamcode.lib.util.Alliance;

public class WHSRobotData {
    public static double heading = 0.0d;
    public static Alliance alliance = Alliance.RED;
    public static double lastX = 0.0d;
    public static double lastY = 0.0d;
    public static double slidesHeight = 0.0d;
    //simulate power cycling
    public static void reset() {
        heading = 0.0d;
    }
}
