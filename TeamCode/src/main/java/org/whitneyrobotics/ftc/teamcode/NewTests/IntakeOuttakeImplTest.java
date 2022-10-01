package org.whitneyrobotics.ftc.teamcode.NewTests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.whitneyrobotics.ftc.teamcode.lib.util.Toggler;
import org.whitneyrobotics.ftc.teamcode.subsys.Intake;
import org.whitneyrobotics.ftc.teamcode.subsys.Outtake;

@TeleOp(name="Intake Outtake Impl Test", group="tests")
public class IntakeOuttakeImplTest extends OpMode {
    //private Intake robotIntake;
    private Outtake robotOuttake;
    private Toggler intakeOuttakeState = new Toggler(5);
    private String stateDesc = "";

    public void operateIntakeOuttake(boolean changeState, boolean intakePower, boolean intakeReverse, boolean outtakeUp, boolean outtakeDown, boolean reset){
        if(reset){intakeOuttakeState.setState(0);}
        switch(intakeOuttakeState.currentState()){
            case 0:
                stateDesc = "Initial";
                //robotIntake.disable();
                robotOuttake.reset();
                //if(changeState){robotIntake.enable();}
                intakeOuttakeState.changeState(changeState);
                break;
            case 1:
                stateDesc = "Intaking";
                //robotIntake.operate(intakePower,intakeReverse);
                robotOuttake.reset();
                intakeOuttakeState.changeState(changeState);
                /*
                if(changeState){
                    intakeOuttakeState.setState(3);
                }
                 */
                break;
            case 2:
                stateDesc = "Intake rejection";
                //if(robotIntake.reject(1500)){intakeOuttakeState.setState(3)};
            case 3:
                stateDesc = "Outtake Level Selection";
                //robotIntake.disable();
                robotOuttake.operate(outtakeUp,outtakeDown);
                intakeOuttakeState.changeState(changeState);
                break;
            case 4:
                stateDesc = "Depositing Item";
                if(robotOuttake.autoDrop()){intakeOuttakeState.setState(4);}
                break;
            case 5:
                stateDesc = "Moving outtake to level 3";
                robotOuttake.operateWithoutGamepad(2);
                if(!robotOuttake.slidingInProgress){
                    intakeOuttakeState.setState(0);
                }
        }
    }

    @Override
    public void init() {
        robotOuttake = new Outtake(hardwareMap);
        //robotIntake = new Intake(hardwareMap);
    }

    @Override
    public void loop() {
        if(gamepad1.y){throw new RuntimeException("Bad");}
        operateIntakeOuttake(gamepad1.a,gamepad1.b,gamepad1.right_bumper,gamepad1.dpad_up,gamepad1.dpad_down,gamepad1.x);
        telemetry.addData("State Desc",stateDesc);
    }
}
