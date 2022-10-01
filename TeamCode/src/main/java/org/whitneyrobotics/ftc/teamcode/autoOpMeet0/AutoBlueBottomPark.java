/*
package org.whitneyrobotics.ftc.teamcode.autoOpMeet0;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.sun.tools.javac.util.List;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.whitneyrobotics.ftc.teamcode.lib.geometry.Coordinate;
import org.whitneyrobotics.ftc.teamcode.lib.geometry.Position;
import org.whitneyrobotics.ftc.teamcode.subsys.WHSRobotImpl;


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.sun.tools.javac.util.List;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.whitneyrobotics.ftc.teamcode.lib.geometry.Position;

@Autonomous (name = "Auto Blue Bottom Park")
public class AutoBlueBottomPark extends OpMode {
    public WHSRobotImpl robot;

    static final int RED = 0;
    static final int BLUE = 1;
    static final int BOTTOM = 0;
    static final int TOP = 1;

    final int STARTING_ALLIANCE = BLUE;
    final int STARTING_SIDE = BOTTOM;

    Position[][] startingPositions = new Position[2][2];
    Position[][] startingOffsetPositions = new Position[2][2];
    Position[] carouselPositions = new Position[2];
    Position[] shippingHubPosition = new Position[2];
    Position[] sharedShippingHub = new Position[2];
    Position[] warehouse = new Position[2];
    Position[][] finalParkingPosition = new Position[2][2];

    static final int INIT = 0;
    static final int ROTATE_CAROUSEL = 1;
    static final int SHIPPING_HUB = 2;
    static final int WAREHOUSE = 3;
    static final int PARK = 4;

    static final int NUMBER_OF_STATES = 5;

    int state = INIT;
    int subState = 0;

    private int scanLevel = 2;

    boolean[] stateEnabled = new boolean[NUMBER_OF_STATES];

    public void defineStatesEnabled(){
        stateEnabled[INIT] = true;
        stateEnabled[ROTATE_CAROUSEL] = false;
        stateEnabled[SHIPPING_HUB] = false;
        stateEnabled[WAREHOUSE] = false;
        stateEnabled[PARK] = true;
    }

    public String[] stateNames = {"Init", "Rotate Carousel", "Shipping Hub", "Warehouse", "Park"};

    public void advanceState(){
        if (stateEnabled[state + 1]){
            subState = 0;
            state++;
        } else {
            state++;
            advanceState();
        }
    }

    public long lastRecordedTime = System.nanoTime();


    @Override
    public void init() {
        robot = new WHSRobotImpl(hardwareMap);
        robot.robotDrivetrain.resetEncoders();
        // add outtake reset
        defineStatesEnabled();

        // figure out actual values for this
        startingPositions[RED][BOTTOM] = new Position(-1647.6, 900);
        startingPositions[RED][TOP] = new Position(-1647.6, -300);
        startingPositions[BLUE][BOTTOM] = new Position(-1647.6, -900);
        startingPositions[BLUE][TOP] = new Position(-1647.6, 300);

        startingOffsetPositions[RED][BOTTOM] = new Position(-1547.6,900);
        startingOffsetPositions[RED][TOP] = new Position(-1547.6,-300);
        startingOffsetPositions[BLUE][BOTTOM] = new Position(-1547.6,-900);
        startingOffsetPositions[BLUE][TOP] = new Position(-1547.6,300);

        shippingHubPosition[RED] = new Position(-752.4, 452.4);
        shippingHubPosition[BLUE] = new Position(-752.4, -452.4);

        sharedShippingHub[RED] = new Position(-152.4, -1200);
        sharedShippingHub[BLUE] = new Position(-152.4, 1200);

        warehouse[RED] = new Position(-1500, -1122.6);
        warehouse[BLUE] = new Position(-1500, 1122.6);

        finalParkingPosition[RED][TOP] = new Position(-1500, -1122.6);
        finalParkingPosition[RED][BOTTOM] = new Position(-900, 1500);
        finalParkingPosition[BLUE][TOP] = new Position(-1500, 1122.6);
        finalParkingPosition[BLUE][BOTTOM] = new Position(-900, -1500);

        carouselPositions[RED] = new Position(-1400, 1400);
        carouselPositions[BLUE] = new Position(-1400, -1400);
    }

    @Override
    public void loop() {
        switch (state){
            case INIT:
                switch (subState){
                    case 0:
                        robot.robotDrivetrain.resetEncoders();
                        //advanceState();
                        Coordinate initial = new Coordinate(startingPositions[STARTING_ALLIANCE][STARTING_SIDE],90);
                        robot.setInitialCoordinate(initial);
                        robot.driveToTarget(startingPositions[STARTING_ALLIANCE][STARTING_SIDE], false);
                        subState++;
                        break;
                    case 1:
                        robot.driveToTarget(startingOffsetPositions[STARTING_ALLIANCE][STARTING_SIDE],false);
                        if (!robot.driveToTargetInProgress()){
                            subState++;
                        }
                }
            case ROTATE_CAROUSEL:
                switch (subState){
                    case 0:
                        robot.driveToTarget(carouselPositions[STARTING_ALLIANCE], false);
                        if (!robot.driveToTargetInProgress()){
                            subState++;
                        }
                        break;
                    case 1:
                        robot.robotCarousel.operate();
                        if (!robot.robotCarousel.rotateInProgress()){
                            advanceState();
                            subState++;
                        }
                        break;
                }
            case SHIPPING_HUB:
                switch (subState) {
                    case 0:
                        robot.driveToTarget(sharedShippingHub[STARTING_ALLIANCE], true); //check if outtake is on the back
                        if (!robot.driveToTargetInProgress()) {
                            subState++;
                        }
                        break;
                    case 1:
                        robot.robotOuttake.autoControl(scanLevel);
                        if (!robot.robotOuttake.slidingInProgress) {
                            if (robot.robotOuttake.autoDrop()) {
                                subState++;
                            }
                            break;
                        }
                    case 2:
                        robot.robotOuttake.reset();
                        advanceState();
                        break;
                }
            case WAREHOUSE:
                switch (subState){
                }
            case PARK:
                switch (subState){
                    case 0:
                        robot.driveToTarget(finalParkingPosition[STARTING_ALLIANCE][STARTING_SIDE], false);
                        if (!robot.driveToTargetInProgress()){
                            subState++;
                        }
                        break;
                }
            default:
                break;
        }
        telemetry.addData("Current state: ",stateNames[state]);
        telemetry.addData("Substate: ", subState);
        telemetry.addData("Drive to target:", robot.driveToTargetInProgress());
        telemetry.addData("Rotate to target:", robot.rotateToTargetInProgress());
        telemetry.addData("Outtake extension: ", robot.robotOuttake.slidingInProgress);
        telemetry.addData("Intaking item from warehouse: ", robot.robotIntake.intakeAutoDone);

        //lag output
        telemetry.addData("Current processing latency: ", (lastRecordedTime-System.nanoTime())*1000 + "ms");
        lastRecordedTime = System.nanoTime();
    }
}
*/
