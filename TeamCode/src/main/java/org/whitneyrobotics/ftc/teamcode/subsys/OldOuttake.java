package org.whitneyrobotics.ftc.teamcode.subsys;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.whitneyrobotics.ftc.teamcode.lib.geometry.Coordinate;
import org.whitneyrobotics.ftc.teamcode.lib.geometry.Position;
import org.whitneyrobotics.ftc.teamcode.lib.util.Functions;
import org.whitneyrobotics.ftc.teamcode.lib.util.Toggler;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

public class OldOuttake {
    public final double POWER_SHOT_TARGET_HEIGHT = 784.225;
    public final double MID_GOAL_HEIGHT = 687.3875;
    public final double HIGH_TARGET_HEIGHT = 901.7; //haven't checked this
    public DcMotorEx launcher;
    public Servo flap;
    public enum LaunchTargets{
        POWERSHOT1, POWERSHOT2, POWERSHOT3, BINS
    }
    public enum LaunchAngles{
        BIN25, BIN50, BIN75, BIN100
    }
    public final Position powershot1 = new Position(1800,-95.25); // from right to left fix later
    public final Position powershot2 = new Position(1800,-285.75);
    public final Position powershot3 = new Position(1800,-476.25);
    public final Position binsMidpoint = new Position(1800,-890.5875);
    public Position[] Target_Positions = {powershot1, powershot2, powershot3, binsMidpoint};
    public final Position Pow1 = Target_Positions[LaunchTargets.POWERSHOT1.ordinal()];
    public final Position Pow2 = Target_Positions[LaunchTargets.POWERSHOT2.ordinal()];
    public final Position Pow3 = Target_Positions[LaunchTargets.POWERSHOT3.ordinal()];
    public final Position Bin = Target_Positions[LaunchTargets.BINS.ordinal()];
    public final double FLYWHEEL_POWER = 0.5;
    public OldOuttake(HardwareMap flyMap) {
        launcher = flyMap.get(DcMotorEx.class, "FlyWheel");
        flap =  hardwareMap.servo.get("Flap");
    }
    public Toggler flyWheelTog = new Toggler(2);
    public Toggler flapTog = new Toggler (4);
    public int[] FLAP_POSITIONS = {25,50,75,100};
    public final int SERVO_ONE = FLAP_POSITIONS[LaunchAngles.BIN25.ordinal()];
    public final int SERVO_TWO = FLAP_POSITIONS[LaunchAngles.BIN50.ordinal()];
    public final int SERVO_THREE = FLAP_POSITIONS[LaunchAngles.BIN75.ordinal()];
    public final int SERVO_BIN = FLAP_POSITIONS[LaunchAngles.BIN100.ordinal()];
    public Position triangle;
    public double robotToTriangle;
    public double targetToTriangle;
    public double hypotenuse;
    public double targetHeading;
    public boolean negativeAngleRequired;
    public double headingToTarget;
    public static final double INITIAL_VELOCITY = 7.07;
    public static final double GRAVITY = -9.8;
    public static final double LAUNCHER_HEIGHT = 300;
    public double calculateLaunchHeading(Position target, Coordinate robotPos){
        if (robotPos.getY()>target.getY()){
            negativeAngleRequired = true;
        }
        else {
            negativeAngleRequired = false;
        }
        triangle = new Position(robotPos.getX(), target.getY());
        robotToTriangle = Math.abs(triangle.getY()- robotPos.getY());
        targetToTriangle = Math.abs(triangle.getX()- target.getX());
        targetHeading = Math.atan(targetToTriangle/robotToTriangle);
        if (negativeAngleRequired){
            headingToTarget = -(360 - (270 + targetHeading));
        }
        else{
            headingToTarget = 90 - targetHeading;
        }
        return headingToTarget;
    }
    public double calculateLaunchSetting (double xDistance, double yDistance){
       // calculates angle based on that formula and then determines servo setting
      double angle;
      double launchSetting;
      double repeatedTerm = ((GRAVITY*yDistance*Math.pow(xDistance,2))-(GRAVITY*LAUNCHER_HEIGHT* Math.pow(xDistance, 2)))/Math.pow(INITIAL_VELOCITY, 2);
      double term1 = Math.pow(xDistance, 2) - repeatedTerm; //x^2 - repeated term
      double term2 = Math.pow((repeatedTerm-Math.pow(xDistance, 2)), 2); // first half under the smaller square root
      double term3 = ((Math.pow(GRAVITY, 2)*Math.pow(xDistance, 2))/Math.pow(INITIAL_VELOCITY, 4))*(Math.pow(xDistance, 2)+Math.pow(yDistance, 2)+ Math.pow(LAUNCHER_HEIGHT, 2)-(2*yDistance*LAUNCHER_HEIGHT));// rest of stuff under small rad
      double smallRoot = Math.sqrt(term2-term3);
      double top = term1 + smallRoot; // this could be + or - only testing will tell
      double bottom = 2 * (Math.pow(xDistance, 2) + Math.pow(yDistance, 2) + Math.pow(LAUNCHER_HEIGHT, 2) - (2 * yDistance * LAUNCHER_HEIGHT));
      double inputForArccos = Math.sqrt(top/bottom);
      angle = Math.acos(inputForArccos);
      launchSetting = angle / 90;
      return launchSetting;
    }

    public double calculateDistanceToTarget(Position target, Coordinate robot){
        // calculates distacnce between robot and target
        return Functions.distanceFormula(target.getX(), robot.getX(), target.getX(), robot.getY());
    }

   /* public static double calculateLaunchSetting (double angle){
        // uses angleCalculator's output to return servo setting
        double launchSetting = 0.0
        return launchSetting;
    }*/

    public int launchState;
    public String launchStateDescription;

    public void operateTargetLaunch(boolean gamepadInput){
        flyWheelTog.changeState(gamepadInput);
        launchState = flyWheelTog.currentState();
        switch (launchState){
            case 0:
                launchStateDescription="Launcher Off";
                launcher.setPower(0);
                break;
            case 1:
                launchStateDescription="Launcher On";
                launcher.setPower(FLYWHEEL_POWER);
                break;
        }
    }

    public int angleState;
    public String angleStateDescription;

    public void operateLaunchAngle(boolean gamepadinput1, boolean gamepadinput2){
      flapTog.changeState(gamepadinput1, gamepadinput2);
      angleState = flapTog.currentState();
      switch (angleState) {
          case 0:
              angleStateDescription="Angle 25";
              flap.setPosition(SERVO_ONE);
              break;
          case 1:
              angleStateDescription="Angle 50";
              flap.setPosition(SERVO_TWO);
              break;
          case 2:
              angleStateDescription="Angle 75";
              flap.setPosition(SERVO_THREE);
              break;
          default:
              angleStateDescription="Angle 100";
              flap.setPosition(SERVO_BIN);
      }
    }

    public void setLaunchAngle(LaunchAngles launchAngle){
        flap.setPosition(FLAP_POSITIONS[launchAngle.ordinal()]);
    }

    public void setLauncherPower(double power){
        launcher.setPower(power);
    }


}
