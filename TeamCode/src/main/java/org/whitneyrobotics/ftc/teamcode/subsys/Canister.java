package org.whitneyrobotics.ftc.teamcode.subsys;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.whitneyrobotics.ftc.teamcode.lib.util.SimpleTimer;
import org.whitneyrobotics.ftc.teamcode.lib.util.Toggler;

public class Canister {
    private Servo loader;
    private Toggler loaderToggler = new Toggler(2);


    public enum LoaderPositions {
        REST, PUSH
    }

    public double[] LOADER_POSITIONS = {0.79, 0.57}; // rest, push

    public SimpleTimer loadTimer = new SimpleTimer();
    public SimpleTimer teleTimer = new SimpleTimer();
    public SimpleTimer teleResetTimer = new SimpleTimer();

    public double teleDelay = 0.25;
    public double teleResetDelay = 0.9;
    private boolean firstCycle = true;
    private boolean shootingInProgress = false;
    public String canisterState;

    int subState = 0;
    int state = 0;
    int ringsShot = 0;
    int loadState = 0;
    int autoState = 0;


    public Canister(HardwareMap canisterMap) {
        loader = canisterMap.servo.get("canisterServo");
    }

    public void operateLoader(boolean gamepadInputLoader) {
        switch (state) {
            case 0:
                canisterState = " In pre phase";
                if (gamepadInputLoader) {
                    state++;
                }
                break;
            case 1:
                canisterState = "Loader On";
                teleTimer.set(teleDelay);
                loader.setPosition(LOADER_POSITIONS[LoaderPositions.PUSH.ordinal()]);
                state++;
                break;
            case 2:
                if (teleTimer.isExpired()) {
                    state++;
                }
                break;
            case 3:
                canisterState = "Loader Off";
                teleResetTimer.set(teleResetDelay);
                loader.setPosition(LOADER_POSITIONS[LoaderPositions.REST.ordinal()]);
                state++;
                break;
            case 4:
                if (teleResetTimer.isExpired()) {
                    ringsShot++;
                    if (firstCycle && ringsShot < 4) {
                        state = 1;
                    } else if (ringsShot >= 3) {
                        firstCycle = false;
                        state++;
                    } else {
                        firstCycle = false;
                        state = 1;
                    }
                }
                break;
            case 5:
                canisterState = "in Post phase";
                ringsShot = 0;
                state = 0;
                break;
        }
    }


    public void loadRing() {
        switch (loadState) {
            case 0:
                loadTimer.set(0.500);
                loadState++;
                break;
            case 1:
                if (!loadTimer.isExpired()) {
                    loader.setPosition(LOADER_POSITIONS[LoaderPositions.PUSH.ordinal()]);
                } else {
                    loader.setPosition(LOADER_POSITIONS[LoaderPositions.REST.ordinal()]);
                }
                break;
            default:
                break;
        }
    }

    public void setLoaderPosition(LoaderPositions position) {
        loader.setPosition(LOADER_POSITIONS[position.ordinal()]);
    }

    public void shootRing() {

        switch (autoState) {
            case 0:
                shootingInProgress = true;
                canisterState = "Loader On";
                teleTimer.set(teleDelay);
                loader.setPosition(LOADER_POSITIONS[LoaderPositions.PUSH.ordinal()]);
                autoState++;
                break;
            case 1:
                if (teleTimer.isExpired()) {
                    autoState++;
                }
                break;
            case 2:
                canisterState = "Loader Off";
                teleResetTimer.set(teleResetDelay);
                loader.setPosition(LOADER_POSITIONS[LoaderPositions.REST.ordinal()]);
                autoState++;
                break;
            case 3:
                if (teleResetTimer.isExpired()) {
                    ringsShot++;
                    if (firstCycle && ringsShot < 4) {
                        autoState = 0;
                    } else if (ringsShot >= 3) {
                        firstCycle = false;
                        autoState++;
                    } else {
                        firstCycle = false;
                        autoState = 0;
                    }
                }
                break;
            case 4:
                canisterState = "in Post phase";
                ringsShot = 0;
                autoState = 0;
                shootingInProgress = false;
                break;
        }
    }

    public boolean shootingInProgress(){
        return shootingInProgress;
    }
}