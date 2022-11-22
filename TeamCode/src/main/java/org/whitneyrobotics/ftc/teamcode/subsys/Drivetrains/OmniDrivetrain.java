package org.whitneyrobotics.ftc.teamcode.subsys.Drivetrains;

import static org.whitneyrobotics.ftc.teamcode.lib.util.RobotConstants.DEADBAND_ROTATE_TO_TARGET;
import static org.whitneyrobotics.ftc.teamcode.lib.util.RobotConstants.drive_max;
import static org.whitneyrobotics.ftc.teamcode.lib.util.RobotConstants.drive_min;
import static org.whitneyrobotics.ftc.teamcode.lib.util.RobotConstants.rotate_max;
import static org.whitneyrobotics.ftc.teamcode.lib.util.RobotConstants.rotate_min;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.whitneyrobotics.ftc.teamcode.framework.Subsystem;
import org.whitneyrobotics.ftc.teamcode.framework.Vector;
import org.whitneyrobotics.ftc.teamcode.lib.control.PIDController;
import org.whitneyrobotics.ftc.teamcode.lib.file.WHSRobotData;
import org.whitneyrobotics.ftc.teamcode.lib.geometry.Position;
import org.whitneyrobotics.ftc.teamcode.lib.util.Functions;
import org.whitneyrobotics.ftc.teamcode.lib.util.RobotConstants;
import org.whitneyrobotics.ftc.teamcode.subsys.IMU;

@RequiresApi(api = Build.VERSION_CODES.N)
public class OmniDrivetrain extends Drivetrain {
    DcMotorEx driveFL, driveFR, driveBL, driveBR;

    Vector drivetoTargetVector;
    double driveError;
    double rotateTheta;
    double rotateDirection;

    double drivePower;
    double rotatePower;

    RobotConstants robotConstants;

    PIDController rotateController = new PIDController(robotConstants.ROTATE_CONSTANTS);
    PIDController driveController = new PIDController(robotConstants.DRIVE_CONSTANTS);

    protected enum OmniDrivetrainState {
        DRIVER_CONTROLLED, AUTONOMOUS
    }

    public DcMotorEx fL, fR, bL, bR;
    public DcMotorEx[] motors;

    public OmniDrivetrain(HardwareMap hardwareMap, IMU imu){
        super(hardwareMap, imu, "driveFL", "driveFR", "driveBL","driveBR");
        driveFL = motorMap.get("driveFL");
        driveFR = motorMap.get("driveFR");
        driveBL = motorMap.get("driveBL");
        driveBR = motorMap.get("driveBR");
        resetEncoders();
    }

    public double getVelocity(){
        return driveFL.getVelocity(AngleUnit.RADIANS);
    }

    @Override
    protected void applyPowersToMotors(double rotatedX, double rotatedY, double angularRotationPower) {
        double denominator = Math.max(Math.abs(rotatedX) + Math.abs(rotatedY) + Math.abs(angularRotationPower), 1);
        driveFL.setPower((rotatedX+rotatedY+angularRotationPower) * (1-powerReduction)/denominator);
        driveFR.setPower((rotatedX-rotatedY+angularRotationPower) * (1-powerReduction)/denominator);
        driveBL.setPower((-rotatedX+rotatedY+angularRotationPower) * (1-powerReduction)/denominator);
        driveBR.setPower((-rotatedX-rotatedY+angularRotationPower) * (1-powerReduction)/denominator);
    }

    public void rotateToTarget(double theta, boolean backwards){
        if (backwards) {
            if (Math.min(Math.abs(theta - Math.PI), theta + Math.PI) == Math.abs(theta - Math.PI)) {
                rotateDirection = Math.signum(theta - Math.PI);
            } else if (Math.min(Math.abs(theta - Math.PI), theta + Math.PI) == Math.abs(theta + Math.PI)) {
                rotateDirection = Math.signum(theta + Math.PI);
            }
        }

        rotateController.calculate(Math.min(Math.abs(rotateTheta - Math.PI), rotateTheta + Math.PI) * rotateDirection);
        rotatePower = Functions.map(rotateController.getOutput(), 0, 360, rotate_min, rotate_max);

        applyPowersToMotors(0, 0, Math.max(rotatePower, 0.001));
    }

    public void driveToTarget(Position targetPosition, boolean backwards){
        drivetoTargetVector = new Vector(targetPosition.getX() - WHSRobotData.lastX, targetPosition.getY() - WHSRobotData.lastY);
        rotateTheta = (2 * Math.PI) - (drivetoTargetVector.getDirection() - WHSRobotData.heading);

        rotateToTarget(rotateTheta, backwards);

        if (rotateTheta <= DEADBAND_ROTATE_TO_TARGET){
            driveError = drivetoTargetVector.getMagnitude();
        } else {
            driveError = 0;
        }

        driveController.calculate(driveError);
        drivePower = Functions.map(driveController.getOutput(), 0, 3600, drive_min, drive_max) * (backwards ? -1 : 1);

        applyPowersToMotors(0, drivePower, 0);
    }
}

/*
sussy dwivetwain
  ________
/   ______\
|   |________|
|          /
|   ____   |
|___|  |___|
(driveToTarget & rotateToTarget) created by Nairrit, Arun, and Sid - 11/11/2022
 */