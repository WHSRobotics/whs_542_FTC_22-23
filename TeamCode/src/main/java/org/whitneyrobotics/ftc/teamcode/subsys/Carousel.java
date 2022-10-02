package org.whitneyrobotics.ftc.teamcode.subsys;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.whitneyrobotics.ftc.teamcode.lib.util.SimpleTimer;
import org.whitneyrobotics.ftc.teamcode.lib.util.Toggler;

public class Carousel {
    private DcMotorEx wheel;
    private final static double power = 0.4;
    private final static double seconds = 2.66;
    private SimpleTimer timer = new SimpleTimer();
    private Toggler allianceSwitch = new Toggler(2);
    private boolean timerCarouselInProgress = false;
    public String carouselAlliance;
    public int carouselState = 0;
    private Toggler powerSwitch = new Toggler(2);

    public Carousel(HardwareMap hardwareMap){
        wheel = hardwareMap.get(DcMotorEx.class,"carouselMotor");
        wheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        wheel.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void operate(boolean start, boolean toggle, boolean changeAlliance){
        allianceSwitch.changeState(changeAlliance);
        boolean blue = allianceSwitch.currentState() == 1;
        if(start && !timerCarouselInProgress) {
            timer.set(seconds);
            timerCarouselInProgress = true;
            powerSwitch.setState(1); //auto switches on carousel
        } else if(powerSwitch.currentState() == 0 && !timer.isExpired()){ //stops carousel from randomly stopping because of timer if you switch it off prematurely
            timer.clear();
            timerCarouselInProgress = false;
        } else if (timer.isExpired() && timerCarouselInProgress) {
            powerSwitch.setState(0);
        }
        togglerOperate(toggle,changeAlliance);
            /*switch (carouselState) {
                case 1:
                    if (!timer.isExpired()) {
                        wheel.setPower(slowPower * (blue ? -1 : 1));
                    } else {
                        carouselState++;
                    }
                    break;
                case 2:
                    timer.set(secondsForFast);
                    wheel.setPower(zoomPower * (blue ? -1 : 1));
                    carouselState++;
                    break;
                case 3:
                    if (!timer.isExpired()) {
                        wheel.setPower(zoomPower * (blue ? -1 : 1));
                    } else {
                        carouselState++;
                    }
                    break;
                case 4:
                    wheel.setPower(0);
                    carouselInProgress = false;
                    carouselState = 0;
                    break;
            }*/

    }


    public void getCarouselAlliance(){
        carouselAlliance = allianceSwitch.currentState() == 0 ? "Red" : "Blue";
    }

    public void operateAuto(boolean blue){
        switch (carouselState){
            case 0:
                timer.set(seconds);
                timerCarouselInProgress = true;
                carouselState++;
                break;
            case 1:
                if (!timer.isExpired()){
                    wheel.setPower(power * (blue ? -1 : 1));
                } else {
                    carouselState++;
                }
                break;
            case 2:
                wheel.setPower(0);
                timerCarouselInProgress = false;
                carouselState = 0;
                break;
        }
    }

    public void togglerOperate(boolean input, boolean blue){
        //allianceSwitch.changeState(alliance);
        powerSwitch.changeState(input);
        if(powerSwitch.currentState() == 0){
            wheel.setPower(0);
            timerCarouselInProgress = false;
        } else {
            wheel.setPower(power * ((blue) ? -1 : 1));
        }
    }

    public void setAlliance(int alliance){allianceSwitch.setState(alliance);}
    public String getAlliance(){return (allianceSwitch.currentState() == 0) ? "Red" : "Blue";}
    public boolean isTimerCarouselInProgress() {return timerCarouselInProgress;}
}
