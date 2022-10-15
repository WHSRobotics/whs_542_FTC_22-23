package org.whitneyrobotics.ftc.teamcode.lib.control;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.whitneyrobotics.ftc.teamcode.lib.libraryProto.PIDCoefficientsNew;
import org.whitneyrobotics.ftc.teamcode.lib.libraryProto.PIDControllerNew;
import org.whitneyrobotics.ftc.teamcode.lib.util.Functions;

public class PIDControlledMotor {
    public final double MAX_VELOCITY;
    private final DcMotorEx motor;

    private double lastPower = 0.0d;
    private double brakePower = 0.0d;
    private double targetVelocity = 0.0d;
    public PIDControllerNew controller;

    private static PIDCoefficientsNew defaultCoefficients = new PIDCoefficientsNew(1,0,0);

    public PIDControlledMotor(DcMotorEx motor, double maxVelocity){
        this(motor, maxVelocity, defaultCoefficients);
    }

    public PIDControlledMotor(DcMotorEx motor, double maxVelocity, PIDCoefficientsNew pidCoefficients){
        MAX_VELOCITY = maxVelocity;
        this.motor = motor;
        controller = new PIDControllerNew(pidCoefficients);
        controller.reset();
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void reset() {
        controller.reset();
    }

    public void reloadCoefficients(PIDCoefficientsNew coefficients){
        controller = new PIDControllerNew(coefficients);
    }

    public void reloadCoefficients(double kP, double kI, double kD){
        controller = new PIDControllerNew(kP,kI,kD);
    }

    public void brake(double brakePower){
        this.brakePower = brakePower;
    }

    public DcMotorEx getMotor() {
        return motor;
    }

    public void setTargetVelocity(double targetVelocity){
        this.targetVelocity = Functions.clamp(targetVelocity, -MAX_VELOCITY, MAX_VELOCITY);
        controller.setTarget(targetVelocity);
    }

    public void update(){
        controller.calculate(motor.getVelocity(AngleUnit.RADIANS));
        double output = controller.getOutput();
        lastPower += Math.signum(output) * Functions.map(Math.abs(output), 0,MAX_VELOCITY,0,1);
        motor.setPower(lastPower * brakePower);
    }
}
