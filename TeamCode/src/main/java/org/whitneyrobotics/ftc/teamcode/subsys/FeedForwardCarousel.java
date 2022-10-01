package org.whitneyrobotics.ftc.teamcode.subsys;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.whitneyrobotics.ftc.teamcode.framework.PIDSubsystem;
import org.whitneyrobotics.ftc.teamcode.lib.control.PIDController;
import org.whitneyrobotics.ftc.teamcode.lib.control.PIDFController;
import org.whitneyrobotics.ftc.teamcode.lib.motion.RateLimiter;
import org.whitneyrobotics.ftc.teamcode.lib.util.Functions;
import org.whitneyrobotics.ftc.teamcode.lib.util.RobotConstants;
import org.whitneyrobotics.ftc.teamcode.lib.util.SimpleTimer;
import org.whitneyrobotics.ftc.teamcode.lib.util.SimpleTimerSuperAccurate;
import org.whitneyrobotics.ftc.teamcode.lib.util.Toggler;

@Config
public class FeedForwardCarousel implements PIDSubsystem {
    public final DcMotorEx wheel;
    public double currentVelocityDebug;
    public double targetVelocityDebug;
    public double errorDebug;
    public Alliance alliance;
    public boolean carouselInProgress;
    public boolean timedEnd = false;
    public String stateDebug;

    public static boolean disableFeedForward = false;

    //carousel speedup time
    public double SPEEDUP_TIME = 1.2;
    public double END_TIME = 1.8;
    //custom class definitions
    //private PIDFController carouselController;
    private PIDFController carouselController;
    private PIDController carouselController2;
    private final Toggler allianceTog = new Toggler(2);
    private final Toggler powerTog = new Toggler(2);
    private final SimpleTimerSuperAccurate carouselTimer = new SimpleTimerSuperAccurate();
    private int carouselState;

    private int manualState = 0;

    public double[] targetRPM = {0,1770,2250}; //find experimentally

    @Override
    public void reloadConstants() {
        RobotConstants.updateConstants();
        carouselController = new PIDFController(RobotConstants.CAROUSEL_CONSTANTS);
        carouselController2 = new PIDController(RobotConstants.CAROUSEL_CONSTANTS);
    }

    @Override
    public void resetEncoders(){
        wheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        wheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public enum CarouselStage {
        OFF, SLOW, FAST
    }

    public enum Alliance {
        RED, BLUE
    }

    public FeedForwardCarousel(HardwareMap hardwareMap){
        wheel = hardwareMap.get(DcMotorEx.class,"carouselMotor");
        wheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        wheel.setDirection(DcMotorSimple.Direction.REVERSE);
        carouselController = new PIDFController(RobotConstants.CAROUSEL_CONSTANTS);
        carouselController2 = new PIDController(RobotConstants.CAROUSEL_CONSTANTS);
    }

    public void changeAlliance(boolean changeAlliance){
        allianceTog.changeState(changeAlliance);
    }

    public void updateCarouselTimes(double slowTIme, double fastTime){
        SPEEDUP_TIME = slowTIme;
        END_TIME = fastTime+slowTIme;
    }

    /**
     * Sets the initial alliance and overrides the toggler.
     * @param alliance The Alliance to use
     * @see Alliance
     * */
    public void setAlliance(Alliance alliance){
        this.alliance = alliance;
        allianceTog.setState(alliance.ordinal());
    }

    public void setAlliance(int allianceIndex){
        this.alliance = Alliance.values()[allianceIndex];
        allianceTog.setState(allianceIndex);
    }

    /**
     * Returns whether or not the carousel is in progress
     * @return Whether or not the carousel is in progress
     * */
    public boolean carouselInProgress(){
        return carouselInProgress;
    }
    /**
     * Operates the carousel with the speed specified by the specific CarouselStage enum.
     * @param carouselStage The specific stage of the carousel operation
     * */
    public void operateCarousel(CarouselStage carouselStage){
        stateDebug = carouselStage.name();
        //absolute valued current and target to ignore wheel direction
        double currentVelocity = (wheel.getVelocity());
        currentVelocityDebug = currentVelocity;
        double targetVelocity = (targetRPM[carouselStage.ordinal()]) * (allianceTog.currentState()==1 ? -1 : 1);
        targetVelocityDebug = targetVelocity;
        double error = targetVelocity - currentVelocity;
        errorDebug = error;
        //carouselController.calculate(error,(disableFeedForward? 0:targetVelocity),currentVelocity); //placeholder for PIDF lambda
        carouselController2.calculate(error);
        double power = (carouselController2.getOutput() >= 0 ? 1 : -1) * Functions.map(Math.abs(carouselController2.getOutput()),0,RobotConstants.carouselMaxRPM,0,1);
        power = (power + (disableFeedForward? 0:targetVelocity)/RobotConstants.carouselMaxRPM);
        power = Functions.constrain(power,-0.80,0.80);
        operateWheel(power);
    }

    /**
     * Method to spin the carousel wheel at a certain power, with the alliance rotation orientation being taken into consideration.
     * @param power Power (between 0 and 1) to feed into the wheel*/
    public void operateWheel(double power){
        wheel.setPower(power /* (allianceTog.currentState()==1 ? -1 : 1)*/);
    }

    /**
     * Timer method to automate the operate procedure, along with controlling the toggler-based operate method. This is the primary method in TeleOp
     * @param start Button to start the timer. This button will become unresponsive while the carousel is running.
     * @param toggle Button to toggle carousel on/off. This can stop the timed operation prematurely and can also start it.
     * @see FeedForwardCarousel#operateStates(boolean) */
    /*public void operate(boolean start,boolean toggle){
        if(start && !carouselInProgress){
            carouselInProgress=true;
            powerTog.setState(1);
        }
        if(!carouselTimer.isExpired()) {
            carouselInProgress = false;
            powerTog.setState(0);
        }
        operateStates(false); //only for listening to stop commands
    }*/

    /**
     * Code for both autonomous carousel operation and driver-controlled
     * This will automate the carousel process, and due to it's stateful nature, it will <u>not</u> cause conflict with timer-controlled and toggler operation.
     * @param toggleOnOff Command to listen for for start/stop. Can also be used to halt timer-based operation
     * */
    public void operateStates(boolean toggleOnOff){
        powerTog.changeState(toggleOnOff);
        if(powerTog.currentState() == 0){
            carouselState = 0;
        }
        switch(carouselState){
            case 0:
                operateWheel(0);
                if(powerTog.currentState() == 1){
                    carouselController.init(targetRPM[CarouselStage.SLOW.ordinal()]);
                    carouselState++;
                    carouselTimer.set(END_TIME);
                    carouselInProgress = true;
                }
                break;
            case 1:
                if(powerTog.currentState() == 0 || !carouselInProgress){
                    carouselState++;
                    break;
                }
                if(carouselTimer.getTimeElapsed()<=SPEEDUP_TIME){
                    operateCarousel(CarouselStage.SLOW);
                } else {
                    operateCarousel(CarouselStage.FAST);
                }
                break;
            case 2:
                operateWheel(0);
                carouselState = 0;
                carouselInProgress = false;
                break;
            default:
                operateWheel(0);
                carouselState = 0;
                break;
        }
    }

    public void operateAuto(){
        if (powerTog.currentState() == 0) {
            targetVelocityDebug=0;
            errorDebug = 0;
            stateDebug = "Idle";
            carouselTimer.clear();
            carouselTimer.set(END_TIME);
            carouselInProgress = false;
            wheel.setPower(0);
            powerTog.setState(1);
        } else {
            carouselInProgress = true;
            if (carouselTimer.isExpired()) {
                powerTog.setState(0);
                errorDebug = 0;
                stateDebug = "Idle";
                wheel.setPower(0);
            }
            if (carouselTimer.getTime() <= SPEEDUP_TIME){
                operateCarousel(FeedForwardCarousel.CarouselStage.SLOW);
            }  else {
                operateCarousel(FeedForwardCarousel.CarouselStage.FAST);
            }
        }
    }
    public void operate (boolean toggle, boolean useTimer){
        powerTog.changeState(toggle);
                if (powerTog.currentState() == 0) {
                    targetVelocityDebug=0;
                    errorDebug = 0;
                    stateDebug = "Idle";
                    carouselTimer.clear();
                    carouselTimer.set(END_TIME);
                    carouselInProgress = false;
                    timedEnd = false;
                    wheel.setPower(0);
                } else {
                    if(useTimer){
                        timedEnd=true;
                    }
                    carouselInProgress = true;
                    if (timedEnd && carouselTimer.isExpired()) {
                        powerTog.setState(0);
                    }
                    if (carouselTimer.getTime() <= SPEEDUP_TIME){
                        operateCarousel(FeedForwardCarousel.CarouselStage.SLOW);
                    }  else {
                    operateCarousel(FeedForwardCarousel.CarouselStage.FAST);
                    }
                }
    }

    public String getAlliance() {return alliance.name();}

    public double getPIDFOutput(){
        return carouselController2.getOutput();
    }

    public double getTime(){
        return carouselTimer.getTime();
    }
}
