package org.whitneyrobotics.ftc.teamcode.subsys.Drivetrains;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.whitneyrobotics.ftc.teamcode.framework.Subsystem;

public class OmniDrivetrain implements Subsystem {

    protected enum DrivetrainState {
        DRIVER_CONTROLLED, AUTONOMOUS
    }

    public DcMotorEx fL, fR, bL, bR;
    public DcMotorEx[] motors;

    public OmniDrivetrain(HardwareMap hardwareMap){
        fL = hardwareMap.get(DcMotorEx.class, "driveFL");
        fL.setDirection(DcMotorSimple.Direction.REVERSE);
        fR = hardwareMap.get(DcMotorEx.class, "driveFR");
        bL = hardwareMap.get(DcMotorEx.class, "driveBL");
        bL.setDirection(DcMotorSimple.Direction.REVERSE);
        bR = hardwareMap.get(DcMotorEx.class, "drive BR");
        motors = new DcMotorEx[] {fL, fR, bL, bR};
        resetEncoders();
    }

    /**
     * Standardized reset method for resetting encoders
     */
    @Override
    public void resetEncoders() {
        for(DcMotorEx motor : motors) {
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
    }



}
