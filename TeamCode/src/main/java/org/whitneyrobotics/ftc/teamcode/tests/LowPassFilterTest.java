package org.whitneyrobotics.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "TrentonTest")
public class TrentonTest extends OpMode {

    DcMotor motor1;

    Servo regServo;
    CRServo crServo;
    
    DcMotor motor2;
    DcMotor motor4;

    @Override
    public void init() {
        motor1 = hardwareMap.dcMotor.get("motor1");
        regServo = hardwareMap.servo.get("regularServo");
        motor2 = hardwareMap.dcMotor.get("motor2");
        crServo = hardwareMap.crservo.get("continuousRotationServo");
    }

    @Override
    public void init_loop(){

    }

    @Override
    public void start(){
        //Runs once when you press the play button
    }
    @Override
    public void loop() {
        int currentPosition = motor1.getCurrentPosition();
        motor1.setTargetPosition(1000);
        motor1.setPower(0.5);

        regServo.setPosition(0.5);

        crServo.setPower(-1.0);
        crServo.setDirection(DcMotorSimple.Direction.FORWARD);

    }

    @Override
    public void stop(){
        //This will run once when the stop button is called
    }
}
