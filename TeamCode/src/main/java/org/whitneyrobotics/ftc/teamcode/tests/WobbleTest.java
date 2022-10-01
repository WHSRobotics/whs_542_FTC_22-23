package org.whitneyrobotics.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.whitneyrobotics.ftc.teamcode.lib.util.Toggler;
import org.whitneyrobotics.ftc.teamcode.subsys.Wobble;

@TeleOp(name = "Wobble Test", group = "Tests")
public class WobbleTest extends OpMode {


    public Toggler armRotatorTog;
    public Toggler clawTog;
    public Toggler wobbleMotorTog;
    public Wobble testWobble;



    @Override
    public void init() {
        testWobble = new Wobble(hardwareMap);
        armRotatorTog = new Toggler(100);
        clawTog = new Toggler(100);
        wobbleMotorTog = new Toggler(100);
    }

    @Override
    public void loop() {
        testWobble.autoDropWobble();
       telemetry.addData("Position", testWobble.wobbleMotor.getCurrentPosition());
       telemetry.addData("State", testWobble.linearSlideState);
       telemetry.addData("Error", testWobble.errorDebug);
       telemetry.addData("Power Debug", testWobble.powerDebug);
       telemetry.addData("Dropped", testWobble.getDroppedState());
    }
}
