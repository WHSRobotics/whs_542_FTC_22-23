package org.whitneyrobotics.ftc.teamcode.subsys;

import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.whitneyrobotics.ftc.teamcode.framework.Subsystem;

public class Grabber implements Subsystem {
    private final Servo gate;
    public final RevColorSensorV3 sensor;
    private double initializationCutoff = 5;
    private boolean override = false;

    private Servo[] servos = new Servo[2];

    //For closing the grabber, issues with going all the way down. Should decrease Close position
    public enum GrabberStates {
        OPEN(0), CLOSE(0.48);
        private double position;
        GrabberStates(double position){
            this.position = position;
        }
        public double getPosition() {
            return position;
        }
    }

    public GrabberStates currentState = GrabberStates.OPEN;

    public Grabber(HardwareMap hardwareMap){
        gate = hardwareMap.get(Servo.class,"gate");
        sensor = (RevColorSensorV3) hardwareMap.get(ColorSensor.class,"grabber_sensor");
        testForCone();
        this.tick();
    }

    public boolean testForCone(){
        boolean coneDetected = /*sensor.getDistance(DistanceUnit.CM) > 13 ||*/ sensor.getDistance(DistanceUnit.CM) < initializationCutoff; //if a cone is retained in autonomous
        if (coneDetected){
            currentState = GrabberStates.CLOSE;
        } else {
            currentState = GrabberStates.OPEN;
        }
        this.tick();
        return coneDetected;
    }


    public void toggleState(){
        currentState = (currentState == GrabberStates.OPEN ? GrabberStates.CLOSE : GrabberStates.OPEN);
        gate.setPosition(currentState.getPosition());
    }

    public void setState(boolean opened){
        currentState = (opened ? GrabberStates.CLOSE : GrabberStates.OPEN);
    }

    public void updateState(GrabberStates state){
        currentState = state;
    }

    public void setForceOpen(boolean state){
        override = state;
    }

    public void testSetPosition(double position) {
        gate.setPosition(position);
    }
    public void forceOpen(){ override = true; }

    public void tick() {
        if(override){
            gate.setPosition(GrabberStates.OPEN.getPosition());
            //override should be updated every loop
            override = false;
        } else {
            gate.setPosition(currentState.getPosition());
        }
    }

    public double getPosition(){
        return gate.getPosition();
    }

    public GrabberStates getCurrentState(){
        return currentState;
    }

    public Servo getArmServo(){
        return gate;
    }

    /**
     * Standardized reset method for resetting encoders
     */
    @Override
    public void resetEncoders() {

    }
}


