package org.whitneyrobotics.ftc.teamcode.subsys;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.whitneyrobotics.ftc.teamcode.lib.control.PIDController;
import org.whitneyrobotics.ftc.teamcode.lib.util.Functions;
import org.whitneyrobotics.ftc.teamcode.lib.util.RobotConstants;
import org.whitneyrobotics.ftc.teamcode.lib.util.SimpleTimer;
import org.whitneyrobotics.ftc.teamcode.lib.util.Toggler;

public class Outtake {
    public Servo gate;
    public DcMotorEx linearSlidesLeft;
    public DcMotorEx linearSlidesRight;
    private boolean useTestPositions = false;
    private boolean gateOverride = false;


    private int resetState = 0;
    private int outtakeState = 0;

    public Outtake(HardwareMap outtakeMap) {
        gate = outtakeMap.get(Servo.class,"gateServo");
        linearSlidesLeft = outtakeMap.get(DcMotorEx.class, "outtakeMotorLeft");
        linearSlidesRight = outtakeMap.get(DcMotorEx.class, "outtakeMotorRight");
        gate.setPosition(GatePositions.CLOSE.getPosition());
        //linearSlidesLeft.setDirection(DcMotor.Direction.REVERSE);
        linearSlidesLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        linearSlidesRight.setDirection(DcMotor.Direction.FORWARD);
        linearSlidesRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        resetEncoder();
    }

    private Toggler servoGateTog = new Toggler(2);
    private Toggler linearSlidesTog = new Toggler(4);
    private SimpleTimer outtakeGateTimer = new SimpleTimer();
    public double errorDebug = 0;

    //Emergency Stop Cases
    private static final double SLIDES_UPPER_BOUND = 3500;
    private static final double SLIDES_LOWER_BOUND = -100;

    private enum GatePositions{
        CLOSE(0.5),
        OPEN(0.2);
        private double position;
        GatePositions(double position){
            this.position = position;
        }
        public double getPosition(){return this.position;}
    }

    public enum MotorLevels{
        LEVEL1(0.0),
        LEVEL1_5(238),
        LEVEL2(438),
        LEVEL3(840);
        private double encoderPos;
        MotorLevels(double encoderPos){
            this.encoderPos = encoderPos;
        }
        public double getPosition(){return this.encoderPos;}
        public void changePosition(double changePos){this.encoderPos = changePos;}

    }
    private double[] orderedPositions = {MotorLevels.LEVEL1.getPosition(), MotorLevels.LEVEL1_5.getPosition(),MotorLevels.LEVEL2.getPosition(),MotorLevels.LEVEL3.getPosition()};
    public boolean slidingInProgress = false;
    public PIDController slidesController = new PIDController(RobotConstants.SLIDE_CONSTANTS);
    public boolean slidesFirstLoop = true;
    public boolean dropFirstLoop = true; //for setting drop timer
    public double gateDelay = 1;
    //private boolean outtakeTimerSet = true; <<I don't know what this is used for

    //toggler based teleop
    public void operate(boolean up, boolean down) {
        if (!slidingInProgress){linearSlidesTog.changeState(up, down);}
        operateWithoutGamepad(linearSlidesTog.currentState());
    }

    public void togglerServoGate(boolean pressed){
        servoGateTog.changeState(pressed);
        if (servoGateTog.currentState() == 0 && !gateOverride) {
            gate.setPosition(GatePositions.CLOSE.getPosition());
        } else {
            gate.setPosition(GatePositions.OPEN.getPosition());
        }
    }


    public void operateWithoutGamepad(int levelIndex) {
        double currentTarget = orderedPositions[levelIndex];
        double error = currentTarget-getSlidesPosition();
        errorDebug = error;
        if (slidesFirstLoop){
            slidingInProgress = true;
            slidesController.init(error);
            slidesFirstLoop = false;
        }

        slidesController.calculate(error);

        double power = (slidesController.getOutput() >= 0 ? 1 : -1) * (Functions.map(Math.abs(slidesController.getOutput()), RobotConstants.DEADBAND_SLIDE_TO_TARGET, 3000, RobotConstants.slide_min, RobotConstants.slide_max));

        if(Math.abs(error) <= RobotConstants.DEADBAND_SLIDE_TO_TARGET ){
            linearSlidesLeft.setPower(0);
            linearSlidesRight.setPower(0);
            slidingInProgress = false;
            slidesFirstLoop = true;
        }
        else {
            linearSlidesLeft.setPower(power);
            linearSlidesRight.setPower(power);
            slidingInProgress = true;
        }
    }

    public boolean autoDrop() { //boolean so our autoop knows if its done
        if(dropFirstLoop) {
            servoGateTog.setState(1);
            gate.setPosition(GatePositions.OPEN.getPosition());
            outtakeGateTimer.set(gateDelay); /*s to keep the flap open*/
            dropFirstLoop = false;
        }

        if(outtakeGateTimer.isExpired() && !gateOverride){
            servoGateTog.setState(0);
            gate.setPosition(GatePositions.CLOSE.getPosition());
            dropFirstLoop = true;
            return true;
        } if(gateOverride){
            gate.setPosition(GatePositions.CLOSE.getPosition());
        }
        return false;
    }

    public void reset() {
        linearSlidesTog.setState(0);
        if(Math.abs(getSlidesPosition() - MotorLevels.LEVEL1.getPosition()) > RobotConstants.DEADBAND_SLIDE_TO_TARGET){
            resetState = 0;
        } else {
            resetState = 1;
        }
        switch(resetState){
            case 0:
                operateWithoutGamepad(0);
                if(!slidingInProgress){
                    resetState++;
                }
                break;
            case 1:
                operateSlides(0);
                linearSlidesTog.setState(0);
                break;
        }
    }

    public void resetEncoder() {
        linearSlidesLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        linearSlidesLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        linearSlidesRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        linearSlidesRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public int getTier() { return linearSlidesTog.currentState(); }

    public double getServoPosition(){return gate.getPosition();}

    public double getAmperageLeft(){return linearSlidesLeft.getCurrent(CurrentUnit.MILLIAMPS);}
    public double getAmperageRight(){return linearSlidesRight.getCurrent(CurrentUnit.MILLIAMPS);}

    public void operateSlides(double power){
        /*if(linearSlides.getCurrentPosition() >= SLIDES_UPPER_BOUND){
            if(power < 0){
                linearSlides.setPower(power);
            } else {
                linearSlides.setPower(0);
            }
        } else if (linearSlides.getCurrentPosition() <= SLIDES_LOWER_BOUND){
            if(power > 0) {
                linearSlides.setPower(power);
            } else {
                linearSlides.setPower(0);
            }
        } else {*/
        linearSlidesLeft.setPower(power);
        linearSlidesRight.setPower(power);
        //}
    }

    public void operateSlidesSafe(double power){
        if(power > 0 && linearSlidesLeft.getCurrentPosition() < -100) return;
        if(power < 0 && linearSlidesLeft.getCurrentPosition() > 1000) return;
                linearSlidesLeft.setPower(power*0.6);
        linearSlidesRight.setPower(power*0.6);
    }

//    public double getSlidesPosition(){return linearSlidesLeft.getCurrentPosition();}
    public double getSlidesPosition(){return (-linearSlidesLeft.getCurrentPosition() + linearSlidesRight.getCurrentPosition()) / 2.0d;}

    public void toggleTestPositions(){useTestPositions = !(useTestPositions);}

    public boolean useTestPositions(){return useTestPositions;}

    public void updateGateOverride(boolean override){
        if(override){
            gate.setPosition(GatePositions.OPEN.getPosition());
        } else if(outtakeGateTimer.isExpired()){
            gate.setPosition(GatePositions.CLOSE.getPosition());
        }
        gateOverride = override;
    }

    public boolean isGateBusy(){
        return gate.getPosition() > GatePositions.CLOSE.getPosition() + 0.05;
    }

    public void setTestPositions(double[] levels){
        if(levels.length != 4) {
            useTestPositions = false;
        } else {
            if(levels[0] > levels[1]){
                throw new IllegalArgumentException("Second position cannot be greater than first.");
            } else if(levels[2] > levels[3]){
                throw new IllegalArgumentException("Third position cannot be greater than second.");
            } else {
                if(useTestPositions){
                    orderedPositions = levels;
                } else {
                    orderedPositions = new double[]{MotorLevels.LEVEL1.getPosition(), MotorLevels.LEVEL1_5.getPosition(),MotorLevels.LEVEL2.getPosition(), MotorLevels.LEVEL3.getPosition()};
                }
            }
        }
    }
}

