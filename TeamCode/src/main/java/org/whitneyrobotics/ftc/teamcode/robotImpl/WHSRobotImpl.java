package org.whitneyrobotics.ftc.teamcode.robotImpl;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.whitneyrobotics.ftc.teamcode.GamepadEx.GamepadEx;
import org.whitneyrobotics.ftc.teamcode.subsys.Drivetrains.OmniDrivetrain;
import org.whitneyrobotics.ftc.teamcode.subsys.Grabber;
import org.whitneyrobotics.ftc.teamcode.subsys.LinearSlides;

public class WHSRobotImpl {
    OmniDrivetrain robotDriveTrain;
    LinearSlides robotLinearSlides;
    Grabber robotIntake;

    GamepadEx gamePadOne;
    GamepadEx gamePadTwo;

    public WHSRobotImpl (HardwareMap hardwareMap, GamepadEx gamepadOne, Gamepad gamepadTwo){
        robotDriveTrain = new OmniDrivetrain(hardwareMap);
        gamePadOne = new GamepadEx(Gamepad )
        robotLinearSlides = new LinearSlides(hardwareMap, gamepad1.dpad_up, gamepad1.dpad_down, gamepad1.a, gamepad1.b);

    }
}
