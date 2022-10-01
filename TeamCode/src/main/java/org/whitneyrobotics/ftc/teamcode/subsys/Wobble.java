package org.whitneyrobotics.ftc.teamcode.subsys;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.whitneyrobotics.ftc.teamcode.autoop.AutoSwervePositions;
import org.whitneyrobotics.ftc.teamcode.lib.control.ControlConstants;
import org.whitneyrobotics.ftc.teamcode.lib.control.PIDController;
import org.whitneyrobotics.ftc.teamcode.lib.control.PIDFController;
import org.whitneyrobotics.ftc.teamcode.lib.util.Functions;
import org.whitneyrobotics.ftc.teamcode.lib.util.SimpleTimer;
import org.whitneyrobotics.ftc.teamcode.lib.util.Toggler;

public class Wobble {

    ControlConstants linearSlideConstants = new ControlConstants(16.9, 1.0, 6.5); //test for these later
    private PIDController linearSlideController = new PIDController(linearSlideConstants);

    private final double MINIMUM_LINEAR_SLIDE_POWER = 0.1; //test for this
    private final int DEADBAND_LINEAR_SLIDE_MOTION = 100; //test for this

    public String wobbleDesc;

    public Servo armRotator;
    public Servo trapDoor;
    public DcMotorEx wobbleMotor;

    public double errorDebug;
    public double powerDebug;

    private Toggler wobbleTog = new Toggler(6);
    public  int linearSlideState;
    private int autoState = 0;
    //private Toggler linearSlidePIDStateTog = new Toggler(2);
    private Toggler teleToggler = new Toggler(5);

    public SimpleTimer autoTimer = new SimpleTimer();

    public enum ArmRotatorPositions {
        IN, HALF, OUT
    }

    public enum ClawPositions {
        OPEN, CLOSE
    }

    public enum LinearSlidePositions {
        DOWN, GRABBING, MOVING, UP
    }

    public double[] ARM_ROTATOR_POSITIONS = {0.45, 0.4, 0.15}; //folded, in , out; test
    public double[] CLAW_POSITIONS = {0.65, 0.91}; // open, close;test
    public int[] LINEAR_SLIDE_POSITIONS = {0, -1790, -500,  -3800}; //down, medium, up; test

    boolean dropped = false;

    public Wobble(HardwareMap wobbleMap) {
        trapDoor = wobbleMap.servo.get("clawServo");
        armRotator = wobbleMap.servo.get("trapDoorServo");
        wobbleMotor = wobbleMap.get(DcMotorEx.class, "wobbleMotor");
        wobbleMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        wobbleMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    /*public String clawStateDescription;
    public void operateClaw(boolean gamepadInput) {
        clawToggler.changeState(gamepadInput);
        if (clawToggler.currentState() == 0) {
            armRotator.setPosition(CLAW_POSITIONS[OldWobble.ClawPositions.OPEN.ordinal()]);
            clawStateDescription = "Open";
        } else {
            armRotator.setPosition(CLAW_POSITIONS[OldWobble.ClawPositions.CLOSE.ordinal()]);
            clawStateDescription = "Close";
        }
    }*/

    public void setArmRotatorPositions(ArmRotatorPositions armRotatorPosition) {
        armRotator.setPosition(ARM_ROTATOR_POSITIONS[armRotatorPosition.ordinal()]);
    }

    public void setClawPosition(ClawPositions clawPosition) {
        trapDoor.setPosition(CLAW_POSITIONS[clawPosition.ordinal()]);
    }

    public void setLinearSlidePosition(LinearSlidePositions linearSlidePosition) {
        double currentPosition = wobbleMotor.getCurrentPosition();
        double targetPosition = LINEAR_SLIDE_POSITIONS[linearSlidePosition.ordinal()];
        double error = Math.abs(currentPosition - targetPosition);
        errorDebug = error;
        switch (linearSlideState) {
            case 0:
                wobbleMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                linearSlideController.init(error);
                linearSlideState++;
                break;
            case 1:
                linearSlideController.calculate(error);

                double linearSlidePower = Functions.map(linearSlideController.getOutput(), DEADBAND_LINEAR_SLIDE_MOTION, -LINEAR_SLIDE_POSITIONS[LinearSlidePositions.UP.ordinal()], 0.2/**/, 1.0);
                powerDebug = linearSlidePower;
                if(Math.abs(error) <  DEADBAND_LINEAR_SLIDE_MOTION){
                    wobbleMotor.setPower(0);
                }
                else if (wobbleMotor.getCurrentPosition() < LINEAR_SLIDE_POSITIONS[linearSlidePosition.ordinal()]) {
                    wobbleMotor.setPower(-linearSlidePower); // linear slide down
                } else {
                    wobbleMotor.setPower(linearSlidePower); // linear slide up
                }

                break;
            default:
                break;
        }
    }
    public void operate(boolean gamepadInput){
        teleToggler.changeState(gamepadInput);
        switch (teleToggler.currentState()){
            case 0:
                setLinearSlidePosition(LinearSlidePositions.GRABBING);
                setClawPosition(ClawPositions.OPEN);
                break;
            case 1:
                setClawPosition(ClawPositions.OPEN);
                setLinearSlidePosition(LinearSlidePositions.DOWN);
                break;
            case 2:
                setClawPosition(ClawPositions.CLOSE);
                setLinearSlidePosition(LinearSlidePositions.DOWN);
                break;
            case 3:
                setLinearSlidePosition(LinearSlidePositions.UP);
                setClawPosition(ClawPositions.CLOSE);
                break;
            case 4:
                setLinearSlidePosition(LinearSlidePositions.UP);
                setClawPosition(ClawPositions.OPEN);
                break;
        }
    }
    public void operateWobble(boolean stateFwd, boolean stateBkwd) {
        wobbleTog.changeState(stateFwd, stateBkwd);
        switch (wobbleTog.currentState()) {
            case 0:
                wobbleDesc = "Folded/Take Intake Feed"; // picking up rings
                setArmRotatorPositions(ArmRotatorPositions.IN);
                setClawPosition(ClawPositions.CLOSE);
                setLinearSlidePosition(LinearSlidePositions.DOWN);

            case 1:
                wobbleDesc = "Take Stuff"; // dropping rings on Wobble Goal
                setArmRotatorPositions(ArmRotatorPositions.OUT);
                setClawPosition(ClawPositions.OPEN);
                setLinearSlidePosition(LinearSlidePositions.GRABBING);

            case 2:
                wobbleDesc = "Carry Stuff"; // grasp Wobble Goal, holding claw near robot while moving
                setArmRotatorPositions(ArmRotatorPositions.HALF);
                setClawPosition(ClawPositions.CLOSE);
                setLinearSlidePosition(LinearSlidePositions.GRABBING);

            case 3:
                wobbleDesc = "Raise to Wall Level"; // raise up to wall height
                setLinearSlidePosition(LinearSlidePositions.UP);

            case 4:
                wobbleDesc = "Extend Out Over Wall"; // extend arm to outside wall
                setArmRotatorPositions(ArmRotatorPositions.OUT);

            case 5:
                wobbleDesc = "Release"; // release Wobble Goal outside wall
                setClawPosition(ClawPositions.OPEN);
            default:
                break;
        }
    }
    public void autoDropWobble(){
        double swingOutDelay = 1.0;
        double dropWobbleDelay = 0.5;
        double resetArmDelay = 1.5;
        switch (autoState){
            case 0:
                autoTimer.set(swingOutDelay);
                setArmRotatorPositions(ArmRotatorPositions.OUT);
                autoState++;
                break;
            case 1:
                setArmRotatorPositions(ArmRotatorPositions.OUT);
                if(autoTimer.isExpired()){
                    autoState++;
                }
                break;
            case 2:
                autoTimer.set(dropWobbleDelay);
                autoState++;
                break;
            case 3:
                setClawPosition(ClawPositions.OPEN);
                if(autoTimer.isExpired()){
                    autoTimer.set(resetArmDelay);
                    autoState++;
                }
                break;
            case 4:
                setArmRotatorPositions(ArmRotatorPositions.IN);
                if(autoTimer.isExpired()){
                    autoState++;
                }
                break;
            case 5:
                setClawPosition(ClawPositions.CLOSE);
               dropped = true;


        }
    }

    public boolean getDroppedState(){
        return dropped;
    }
}
