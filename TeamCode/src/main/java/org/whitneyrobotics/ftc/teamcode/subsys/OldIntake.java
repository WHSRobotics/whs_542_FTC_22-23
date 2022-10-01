package org.whitneyrobotics.ftc.teamcode.subsys;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.whitneyrobotics.ftc.teamcode.lib.util.SimpleTimer;
import org.whitneyrobotics.ftc.teamcode.lib.util.Toggler;

public class OldIntake {

    private DcMotor wheelIntake;
    private DcMotor wheelIntake2;

    private Servo dropdown;

    private Toggler wheelToggler = new Toggler(2);

    private Toggler dropdownToggler = new Toggler(2);

    public double INTAKE_POWER = 1.0; //change to final after testing

    public String dropdownStatus;
    public String intakeStateDescription;

    public SimpleTimer dropdownTimer = new SimpleTimer();
    public double dropdownDelay = 1.0;
    int state = 0;

    public enum DropdownPositions {
        UP, DOWN
    }

    public double[] dropdownPositions = {0.97, 0.66};//placeholders test pls

    public OldIntake(HardwareMap intakeMap) {
        wheelIntake = intakeMap.dcMotor.get("intakeMotor");
        wheelIntake2 = intakeMap.dcMotor.get("intakeMotor2");
        dropdown = intakeMap.servo.get("intakePusher");
    }


    /*public static final double DROPDOWN_UP = 0; //placeholder
    public static final double DROPDOWN_DOWN = 0.5; //placeholder*/

    /*public double dropdownUp = dropdownPositions[DropdownPositions.UP.ordinal()];
    public  double dropdownDown = dropdownPositions[DropdownPositions.DOWN.ordinal()];*/

    public void operate(boolean intakeWheelOnOffInput, boolean intakeWheelDirectionInput) {
        wheelToggler.changeState(intakeWheelOnOffInput);
        if (intakeWheelDirectionInput) {
            wheelIntake.setPower(-INTAKE_POWER);
            wheelIntake2.setPower(-INTAKE_POWER);
            intakeStateDescription = "Reverse Intake";
        } else if (wheelToggler.currentState() == 1) {
            wheelIntake.setPower(INTAKE_POWER);
            wheelIntake2.setPower(INTAKE_POWER);
            intakeStateDescription = "Forward Intake";
        } else {
            wheelIntake.setPower(0.0);
            wheelIntake2.setPower(0.0);
            intakeStateDescription = "Intake Off";
        }

    }
    // for use in Auto
    /*public void setDropdown(double position){ ;
        dropdown.setPosition(position);
    }*/

    // For use in Auto
    public void setDropdown(DropdownPositions dropdownPosition) {
        dropdown.setPosition(dropdownPositions[dropdownPosition.ordinal()]);
    }

    //TeleOp
    public void manualDropdown(boolean dropdownInput) {
        dropdownToggler.changeState(dropdownInput);
        if (dropdownToggler.currentState() == 0) {
            dropdown.setPosition(dropdownPositions[DropdownPositions.UP.ordinal()]);
            dropdownStatus = "Intake Up";
        } else {
            dropdown.setPosition(dropdownPositions[DropdownPositions.DOWN.ordinal()]);
            dropdownStatus = "Intake Down";
        }
    }

    //testing
    public void setIntakePower(double power) { wheelIntake.setPower(power); wheelIntake2.setPower(power);}

    public void setDropdownPosition(double position) { dropdown.setPosition(position); }

    public void autoDropIntake(){
        switch(state){
            case 0:
                dropdownTimer.set(dropdownDelay);
                state++;
                break;
            case 1:

                if(dropdownTimer.isExpired()){
                    setDropdown(DropdownPositions.UP);
                }else{
                    setDropdown(DropdownPositions.DOWN);
                }
                break;
        }

    }
}
