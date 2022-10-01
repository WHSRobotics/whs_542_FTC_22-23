package org.whitneyrobotics.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.whitneyrobotics.ftc.teamcode.lib.util.Toggler;
import org.whitneyrobotics.ftc.teamcode.subsys.OldIntake;

@TeleOp(name = "Depreciated Intake Test", group = "Tests")
public class OldIntakeTest extends OpMode {
    public OldIntake testIntake;
    public Toggler powerTog;
    public Toggler dropdownTog;
    int i;
    double power = 0;
    double position =0;

    @Override
    public void init() {
        testIntake = new OldIntake(hardwareMap);
        i = 0;

    }

    @Override
    public void loop() {
        i++;
        if(i%10 == 0){
            if(gamepad1.a && power < 1){
                power += 0.01;
            }else if(gamepad1.b && power > -1)
            {
                power -= 0.01;
            }

            if(gamepad1.x && position < 1){
                position += 0.01;
            }else if(gamepad1.y && position > 0)
            {
                position -= 0.01;
            }
        }
        //testIntake.setIntakePower(power);
        testIntake.autoDropIntake();
        telemetry.addData("Wheel Power: ", power);
        telemetry.addData("Dropdown Position", position);

    }
}
