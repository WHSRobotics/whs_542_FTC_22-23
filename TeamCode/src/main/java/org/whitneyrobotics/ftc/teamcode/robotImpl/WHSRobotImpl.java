package org.whitneyrobotics.ftc.teamcode.robotImpl;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.whitneyrobotics.ftc.teamcode.GamepadEx.GamepadEx;
import org.whitneyrobotics.ftc.teamcode.GamepadEx.GamepadInteractionEvent;
import org.whitneyrobotics.ftc.teamcode.lib.util.SimpleTimer;
import org.whitneyrobotics.ftc.teamcode.subsys.Drivetrains.OmniDrivetrain;
import org.whitneyrobotics.ftc.teamcode.subsys.Grabber;
import org.whitneyrobotics.ftc.teamcode.subsys.IMU;
import org.whitneyrobotics.ftc.teamcode.subsys.LinearSlides;

@RequiresApi(Build.VERSION_CODES.N)
public class WHSRobotImpl {
    public OmniDrivetrain drivetrain;
    public LinearSlides robotLinearSlides;
    public Grabber robotGrabber;
    public IMU imu;

    GamepadEx gamePadOne;
    GamepadEx gamePadTwo;

    SimpleTimer autoTimer;

    public WHSRobotImpl (HardwareMap hardwareMap, GamepadEx gamepadOne){
        imu = new IMU(hardwareMap);
        drivetrain = new OmniDrivetrain(hardwareMap, imu);
        //robotLinearSlides = new LinearSlides(hardwareMap, gamepadOne);
        robotGrabber = new Grabber(hardwareMap);
        //robotIntake.resetEncoders();
    }

    public void autoGrabber(int waitTime){
        if (robotGrabber.currentState == Grabber.GrabberStates.CLOSE){
            robotGrabber.toggleState();
            autoTimer.set(waitTime);
        }
        if (autoTimer.isExpired() && (robotGrabber.currentState == Grabber.GrabberStates.OPEN)){
            robotGrabber.toggleState();
        }
    }

    public void teleOpGrabber(GamepadEx gamepadOne) {
        gamepadOne.BUMPER_LEFT.onButtonHold((GamepadInteractionEvent callback) -> robotGrabber.forceOpen());
        gamepadOne.A.onButtonHold((GamepadInteractionEvent callback) -> robotGrabber.setState(true));
        gamepadOne.A.onRelease((GamepadInteractionEvent callback) -> robotGrabber.setState(false));
    }


    public void autoLinearSlides(LinearSlides.LinearSlidesSTATE state, int waitTime) {

        robotLinearSlides.changeState(state);
        robotLinearSlides.operate();

        if (robotLinearSlides.getCurrentLevel() == 0) {
            autoTimer.set(waitTime);
        }

        if (autoTimer.isExpired() && (robotLinearSlides.isSlidingInProgress() == false)){
            robotLinearSlides.reset();
        }
    }

    public void tick(){
        robotGrabber.tick();
    }

}
