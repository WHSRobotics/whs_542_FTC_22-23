package org.whitneyrobotics.ftc.teamcode.robotImpl;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.openftc.apriltag.AprilTagDetection;
import org.openftc.easyopencv.OpenCvCamera;
import org.whitneyrobotics.ftc.teamcode.GamepadEx.GamepadEx;
import org.whitneyrobotics.ftc.teamcode.GamepadEx.GamepadInteractionEvent;
import org.whitneyrobotics.ftc.teamcode.framework.opmodes.OpModeEx;
import org.whitneyrobotics.ftc.teamcode.lib.pathfollowers.purepursuit.PurePursuitFollower;
import org.whitneyrobotics.ftc.teamcode.lib.util.SimpleTimer;
import org.whitneyrobotics.ftc.teamcode.subsys.Drivetrains.OmniDrivetrain;
import org.whitneyrobotics.ftc.teamcode.subsys.Grabber;
import org.whitneyrobotics.ftc.teamcode.subsys.IMU;
import org.whitneyrobotics.ftc.teamcode.subsys.LinearSlides;
import org.whitneyrobotics.ftc.teamcode.subsys.LinearSlidesMeet1;

@RequiresApi(Build.VERSION_CODES.N)
public class WHSRobotImpl {
    public OmniDrivetrain drivetrain;
    public LinearSlides robotLinearSlides;
    public LinearSlidesMeet1 robotLinearSlidesMeet1;
    public Grabber robotGrabber;
    public IMU imu;
    public OpenCvCamera camera;
//    public AprilTagDetectionPipeline aprilTagDetectionPipeline;


    public enum TeleOpState {
            ANGULAR_FREEDOM, SNAP_TO_TARGET
    }
    SimpleTimer autoTimer;

    GamepadEx gamepad;
    public WHSRobotImpl (HardwareMap hardwareMap, GamepadEx gamepad){
        gamepad = gamepad;
        imu = new IMU(hardwareMap);
        drivetrain = new OmniDrivetrain(hardwareMap, imu);
        //robotLinearSlides = new LinearSlides(hardwareMap, gamepadOne);
        robotLinearSlidesMeet1 = new LinearSlidesMeet1(hardwareMap);
        robotGrabber = new Grabber(hardwareMap);
        //robotIntake.resetEncoders();

        drivetrain.setFollower(PurePursuitFollower::new);
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
