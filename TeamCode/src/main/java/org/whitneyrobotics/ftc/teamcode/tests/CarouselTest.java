package org.whitneyrobotics.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.whitneyrobotics.ftc.teamcode.subsys.CarouselOld;

@TeleOp(name = "CarouselTest")
public class CarouselTest extends OpMode {

    private CarouselOld carousel;

    @Override
    public void init() {
        carousel = new CarouselOld(hardwareMap);
    }

    @Override
    public void loop() {
        //telemetry.addData("Rotations", carousel.getRotations());
        telemetry.addData("Timer",carousel.getTimer());
        telemetry.addData("Rotate in progress", carousel.rotateInProgress());
        telemetry.addData("First loop: ", carousel.rotateInProgress());
        telemetry.addData("Toggler State: ", carousel.getTogglerState());

        carousel.operate(gamepad1.y);
        carousel.togglerOperate(gamepad1.a,false);
    }
}
