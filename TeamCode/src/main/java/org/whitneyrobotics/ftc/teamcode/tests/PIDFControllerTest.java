package org.whitneyrobotics.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import org.whitneyrobotics.ftc.teamcode.lib.control.ControlConstants;
import org.whitneyrobotics.ftc.teamcode.lib.control.PIDFController;
import org.whitneyrobotics.ftc.teamcode.lib.util.RobotConstants;

public class PIDFControllerTest extends OpMode {
    PIDFController testController;
    ControlConstants.FeedforwardFunction kF = (double currentPosition, double currentVelocity) ->  1/RobotConstants.OUTTAKE_MAX_VELOCITY;
    ControlConstants constants = new ControlConstants(0.1, 0.2, 0.3, kF);
    @Override
    public void init() {
        testController = new PIDFController(constants);
    }

    @Override
    public void loop() {
        testController.calculate(1, 1, 1);
        testController.getOutput();
    }
}
