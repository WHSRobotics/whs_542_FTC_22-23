package org.whitneyrobotics.ftc.teamcode.subsys.Drivetrains;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.whitneyrobotics.ftc.teamcode.framework.Subsystem;
import org.whitneyrobotics.ftc.teamcode.subsys.IMU;

@RequiresApi(api = Build.VERSION_CODES.N)
public class OmniDrivetrain extends Drivetrain {
    DcMotorEx driveFL, driveFR, driveBL, driveBR;

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

    @Override
    protected void applyPowersToMotors(double rotatedX, double rotatedY, double angularRotationPower) {
        double denominator = Math.max(Math.abs(rotatedX) + Math.abs(rotatedY) + Math.abs(angularRotationPower), 1);
        driveFL.setPower((rotatedX+rotatedY+angularRotationPower) * (1-powerReduction)/denominator);
        driveFR.setPower((rotatedX-rotatedY+angularRotationPower) * (1-powerReduction)/denominator);
        driveBL.setPower((-rotatedX+rotatedY+angularRotationPower) * (1-powerReduction)/denominator);
        driveBR.setPower((-rotatedX-rotatedY+angularRotationPower) * (1-powerReduction)/denominator);
    }
}
