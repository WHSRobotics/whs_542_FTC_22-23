package org.whitneyrobotics.ftc.teamcode.Teaching;
import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.whitneyrobotics.ftc.teamcode.framework.DashboardOpMode;
import org.whitneyrobotics.ftc.teamcode.lib.libraryProto.PIDController;
import org.whitneyrobotics.ftc.teamcode.lib.util.Functions;

@TeleOp(
        name="MotorPIDTest",
        group="Tests"
)
@Config
public class MotorPIDTest extends DashboardOpMode {
    private DcMotorEx motor;
    public static double kP = 1;
    public static double kD = 0;
    public static double kI = 0;
    //private PIDCoefficients pidConstants = new PIDCoefficients(1,0,0);
    private PIDController pid = new PIDController();

    /**
     * User defined init method
     * <p>
     * This method will be called once when the INIT button is pressed.
     */
    @Override
    public void init() {
        initializeDashboardTelemetry(10);
        motor = hardwareMap.get(DcMotorEx.class, "motor");
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    @Override
    public void start(){
        pid.reset();

    }

    /**
     * User defined loop method
     * <p>
     * This method will be called repeatedly in a loop while this op mode is running
     */
    @Override
    public void loop() {
        pid.setKP(kP);
        pid.setKD(kD);
        pid.setKI(kI);

        double target = 10*Math.sin((Math.PI/10) * getRuntime());
        pid.setTarget(target);
        pid.calculate(motor.getVelocity(AngleUnit.RADIANS));
        double output = pid.getOutput();
        double appliedPower = Math.signum(output) * Functions.map(Math.abs(output),0,15,0,1);
        motor.setPower(Math.pow(appliedPower,(1/3)));

        telemetry.addData("Motor Velocity",motor.getVelocity(AngleUnit.RADIANS));
        telemetry.addData("Target",target);
        telemetry.addData("Output",output);
        telemetry.addData("Applied power", appliedPower);
        telemetry.addData("Error",target-motor.getVelocity(AngleUnit.RADIANS));
        telemetry.addData("Integral",pid.getIntegral());
        telemetry.addData("Derivative",pid.getDerivative());
    }
}
