package org.whitneyrobotics.ftc.teamcode.NewTests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.whitneyrobotics.ftc.teamcode.subsys.Carousel;

@TeleOp(name="New Carousel Test", group="Tests")
public class CarouselTest extends OpMode {
    private Carousel carousel;
    private int state = 0;

    @Override
    public void init() {
        carousel = new Carousel(hardwareMap);
    }

    @Override
    public void loop() {
        carousel.operate(gamepad1.a,gamepad1.x,gamepad1.b);
        /*switch(state){
            case 0:
                carousel.operateAuto(gamepad1.b);
                if(!carousel.isCarouselInProgress()){
                    state++;
                }
                break;
            case 1:
                if(gamepad1.a){state = 0;}
                break;

        }*/
        carousel.togglerOperate(gamepad1.right_bumper,gamepad1.b);
        telemetry.addData("Alliance",carousel.getAlliance());
        telemetry.addData("Carousel in Progress",carousel.isTimerCarouselInProgress());
    }
}
