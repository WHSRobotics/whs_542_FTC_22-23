package org.whitneyrobotics.ftc.teamcode.autoBasic;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.whitneyrobotics.ftc.teamcode.lib.util.SimpleTimer;
import org.whitneyrobotics.ftc.teamcode.subsys.WHSRobotImpl;

@Autonomous(name="AutoDownRED",group="A")
public class AutoDownRED extends OpMode {
    SimpleTimer step1 = new SimpleTimer();
    SimpleTimer step2 = new SimpleTimer();
    SimpleTimer step3 = new SimpleTimer();
    WHSRobotImpl robot;
    boolean step1B = false;
    boolean step2B= false;
    boolean step3B = true;
    int state = 0;

    @Override
    public void init() {
         robot = new WHSRobotImpl(hardwareMap);
    }

    @Override
    public void loop() {
        switch(state){
            case 0:
                if (!step1B){
                    step1.set(2.5);
                    step1B = true;
                }
                robot.drivetrain.operateLeft(-0.25);
                robot.drivetrain.operateRight(-0.25);
                if(step1.isExpired()){
                    robot.drivetrain.operateLeft(0);
                    robot.drivetrain.operateRight(0);
                    state++;
                }
                break;
            case 1:
                if (!step2B){
                    step2.set(4);
                    step2B = true;
                }
                robot.drivetrain.operateLeft(0.25);
                robot.drivetrain.operateRight(-0.25);
                if(step2.isExpired()){
                    robot.drivetrain.operateLeft(0);
                    robot.drivetrain.operateRight(0);
                    state++;
                }
                break;
            /*case 2:
                if (!step3B){
                    step3.set(6);
                    step3B = true;
                }
                robot.robotDrivetrain.operateLeft(-0.25);
                robot.robotDrivetrain.operateRight(-0.25);
                if(step3.isExpired()){
                    robot.robotDrivetrain.operateLeft(0);
                    robot.robotDrivetrain.operateRight(0);
                    state++;
                }
                break;*/
            default:
                robot.drivetrain.operateLeft(-0.1);
                robot.drivetrain.operateRight(-0.1);
        }
    }
}
