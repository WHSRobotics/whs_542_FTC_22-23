package org.whitneyrobotics.ftc.teamcode.subsys;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.whitneyrobotics.ftc.teamcode.lib.control.PIDFController;
import org.whitneyrobotics.ftc.teamcode.lib.geometry.Coordinate;
import org.whitneyrobotics.ftc.teamcode.lib.geometry.Position;
import org.whitneyrobotics.ftc.teamcode.lib.util.Functions;
import org.whitneyrobotics.ftc.teamcode.lib.util.RobotConstants;
import org.whitneyrobotics.ftc.teamcode.lib.util.SimpleTimer;

public class OldOuttake2 {

    public DcMotorEx flywheel;
    //public Servo flap;
    public SimpleTimer launchTimer = new SimpleTimer();
    public final double LAUNCH_TIME = 500; //millisecs

    public double errorDebug;
    public double targetVelocityDebug;
    public double currentVelocityDebug;

    //public final static double FLYWHEEL_POWER = 0.5;
    //public int[] FLAP_POSITIONS = {25,50,75,100};

    /*public final double POWER_SHOT_TARGET_HEIGHT = 784.225;
    public final double MID_GOAL_HEIGHT = 687.3875;
    public final double HIGH_GOAL_HEIGHT = 901.7;*/


    public PIDFController outtakeController;


    public enum GoalPositions {
        LEFT_POWER_SHOT, CENTER_POWER_SHOT, RIGHT_POWER_SHOT, LOW_BIN, MEDIUM_BIN, HIGH_BIN, HIGH_BIN_FAR
    }
    /*public enum LaunchAngles{
        LOW_BIN, MEDIUM_BIN, HIGH_BIN, P1, P2, P3
    }*/

    /*public Position[] Target_Positions = {powershot1, powershot2, powershot3, binsMidpoint};
    public final Position Pow1 = Target_Positions[GoalPositions.POWER_SHOT_TARGET_ONE.ordinal()];
    public final Position Pow2 = Target_Positions[GoalPositions.POWER_SHOT_TARGET_TWO.ordinal()];
    public final Position Pow3 = Target_Positions[GoalPositions.POWER_SHOT_TARGET_THREE.ordinal()];
    public final Position Bin = Target_Positions[GoalPositions.BIN_GOALS.ordinal()];*/
    public double calculateLaunchHeading(Position target, Coordinate robotPos) {
        // calculates heading to launch at target
        double headingToTarget;
        Position triangle = new Position(robotPos.getX(), target.getY());
        double robotToTriangle = Math.abs(triangle.getY() - robotPos.getY());
        double targetToTriangle = Math.abs(triangle.getX() - target.getX());
        double targetAngle = Math.atan(targetToTriangle / robotToTriangle);
        if (robotPos.getY() > target.getY()) {
            headingToTarget = targetAngle - 90;
        } else {
            headingToTarget = 90 - targetAngle;
        }
        return headingToTarget;
    }

    //LEFT, CENTER, RIGHT, LOW, MEDIUM, HIGH
    public double[] flapServoPositions = {0.0, 0.25, 0.45, 0.5, 0.65, 0.75}; //test
    public double[] targetMotorVelocities = {1470, 1460, 1455, 0.66, 0.77, 1665, 1705}; //test to get to 7.07 m/s

    public OldOuttake2(HardwareMap outtakeMap) {
        flywheel = outtakeMap.get(DcMotorEx.class, "outtakeMotor");
       // flap = outtakeMap.servo.get("flapServo");
        outtakeController = new PIDFController(RobotConstants.FLYWHEEL_CONSTANTS);
    }

    public void operate(GoalPositions goalPosition) {
        operateFlywheel(goalPosition);
        //setFlapServoPositions(goalPosition);
    }

    /*public void setFlapServoPositions(GoalPositions goalPosition) {
        flap.setPosition(flapServoPositions[goalPosition.ordinal()]);
    }*/

    public void operateFlywheel(GoalPositions goalPosition) {
        double currentVelocity = -flywheel.getVelocity();
        currentVelocityDebug = currentVelocity;
        double targetVelocity = targetMotorVelocities[goalPosition.ordinal()];
        targetVelocityDebug = targetVelocity;
        double error = targetVelocity - currentVelocity;
        errorDebug = error;
        outtakeController.calculate(error, 10, currentVelocity);
        double power = Functions.map(outtakeController.getOutput(), 0, RobotConstants.OUTTAKE_MAX_VELOCITY, 0.15, 1.0);
        power = Functions.constrain(power, -0.82, 0.82);
        flywheel.setPower(power);
    }

    /* public void setLaunchAngle(OldOuttake.LaunchAngles launchAngle){
         flap.setPosition(FLAP_POSITIONS[launchAngle.ordinal()]);
     }*/
    public void launchToTarget(GoalPositions goal) {
        // launch at goal
        int launchState = 0;
        switch (launchState) {
            case 0:
                launchTimer.set(LAUNCH_TIME);
                launchState++;
                break;
            case 1:
                if (!launchTimer.isExpired()) {
                    operate(goal);
                } else {
                    setLauncherPower(0);
                }
                break;
            default:
                break;
        }

    }

    public void setLauncherPower(double power) { flywheel.setPower(power); }

    //public void setFlapPosition(double position) { flap.setPosition(position); }

}
