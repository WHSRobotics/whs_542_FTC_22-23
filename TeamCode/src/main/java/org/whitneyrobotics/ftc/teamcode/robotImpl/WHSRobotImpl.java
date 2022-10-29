package org.whitneyrobotics.ftc.teamcode.robotImpl;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.whitneyrobotics.ftc.teamcode.GamepadEx.GamepadEx;
import org.whitneyrobotics.ftc.teamcode.GamepadEx.GamepadInteractionEvent;
import org.whitneyrobotics.ftc.teamcode.lib.util.SimpleTimer;
import org.whitneyrobotics.ftc.teamcode.subsys.Drivetrains.OmniDrivetrain;
import org.whitneyrobotics.ftc.teamcode.subsys.Grabber;
import org.whitneyrobotics.ftc.teamcode.subsys.LinearSlides;

@RequiresApi(Build.VERSION_CODES.N)
public class WHSRobotImpl {
    OmniDrivetrain robotDriveTrain;
    LinearSlides robotLinearSlides;
    Grabber robotIntake;

    GamepadEx gamePadOne;
    GamepadEx gamePadTwo;

    SimpleTimer autoTimer;

    public WHSRobotImpl (HardwareMap hardwareMap, GamepadEx gamepadOne){
        robotDriveTrain = new OmniDrivetrain(hardwareMap);
        robotLinearSlides = new LinearSlides(hardwareMap, gamepadOne);
        robotIntake = new Grabber(hardwareMap);

        robotDriveTrain.resetEncoders();
        robotIntake.resetEncoders();
    }

    public void autoGrabber(int waitTime){
        if (robotIntake.currentState == Grabber.GrabberStates.CLOSE){
            robotIntake.toggleState();
            autoTimer.set(waitTime);
        }
        if (autoTimer.isExpired() && (robotIntake.currentState == Grabber.GrabberStates.OPEN)){
            robotIntake.toggleState();
        }
    }

    public void teleOpGrabber(GamepadEx gamepadOne) {
        gamepadOne.BUMPER_LEFT.onButtonHold((GamepadInteractionEvent callback) -> robotIntake.forceOpen());
        gamepadOne.A.onButtonHold((GamepadInteractionEvent callback) -> robotIntake.setState(true));
        gamepadOne.A.onRelease((GamepadInteractionEvent callback) -> robotIntake.setState(false));
    }

<<<<<<< Updated upstream
    public void autoLinearSlides(LinearSlidesSTATE state, int waitTime) {
        robotLinearSlides.changeState(state);
        robotLinearSlides.operate();

        if (robotLinearSlides.currentLevel == 0) {
            autoTimer.set(waitTime);
        }

        if (autoTimer.isExpired() && (robotLinerSlides.slidingInProgress == False)){
            robotLinearSlides.reset();
        }
    }

    public void teleOpLinearSlides() {

    }
=======

>>>>>>> Stashed changes

}
