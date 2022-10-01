package org.whitneyrobotics.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.whitneyrobotics.ftc.teamcode.autoop.AutoSwervePositions;
import org.whitneyrobotics.ftc.teamcode.lib.geometry.Coordinate;
import org.whitneyrobotics.ftc.teamcode.lib.geometry.Position;
import org.whitneyrobotics.ftc.teamcode.lib.util.Toggler;
import org.whitneyrobotics.ftc.teamcode.subsys.WHSRobotImpl;

@TeleOp(name="Drivetrain Test" , group="Tests" )
public class DrivetrainTest extends OpMode  {
    public WHSRobotImpl robot;
    // yeetboi is robot
    public Toggler posTog;
    public Toggler topBottomTog;
    public Toggler allianceTog;
    public int posStateNum;
    public int allianceNum;
    public int topOrBottom;

    private Position[][] startingPositions = new Position[2][2];
    private Position[] shippingHubPosition = new Position[2];
    private Position[] sharedShippingHub = new Position[2];
    private Position[] warehouse = new Position[2];
    private Position[][] finalParkingPosition = new Position[2][2];
    private Position[] carouselPositions = new Position[2];

    private int RED = 0;
    private int BLUE = 1;
    private int BOTTOM = 0;
    private int TOP = 1;

    @Override
    public void init() {
        robot = new WHSRobotImpl(hardwareMap);
        posTog = new Toggler(5);
        allianceTog = new Toggler(2);
        topBottomTog = new Toggler(2);

        startingPositions[RED][BOTTOM] = new Position(-1647.6,900);
        startingPositions[RED][TOP] = new Position(-1647.6,-300);
        startingPositions[BLUE][BOTTOM] = new Position(-1647.6,-900);
        startingPositions[BLUE][TOP] = new Position(-1647.6,300);

        shippingHubPosition[RED] = new Position(-752.4,452.4);
        shippingHubPosition[BLUE] = new Position(-752.4,-452.4);

        sharedShippingHub[RED] = new Position(-152.4, -1200);
        sharedShippingHub[BLUE] = new Position(-152.4, 1200);

        warehouse[RED] = new Position(-1500,-1122.6);
        warehouse[BLUE] = new Position(-1500,1122.6);

        finalParkingPosition[RED][TOP] = new Position(-1500,-1122.6);
        finalParkingPosition[RED][BOTTOM] = new Position(-900,1500);
        finalParkingPosition[BLUE][TOP] = new Position(-1500,1122.6);
        finalParkingPosition[BLUE][BOTTOM] = new Position(-900, -1500);

        carouselPositions[RED] = new Position(-1400,1400);
        carouselPositions[BLUE] = new Position(-1400, -1400);
    }

    @Override
    public void loop() {
        posTog.changeState(gamepad1.dpad_right, gamepad1.dpad_left);
        allianceTog.changeState(gamepad1.a);
        topBottomTog.changeState(gamepad1.dpad_up, gamepad1.dpad_down);

        posStateNum = posTog.currentState();
        allianceNum = allianceTog.currentState();
        topOrBottom = topBottomTog.currentState();

        switch (posStateNum){
            case 0:
                if(gamepad1.x){
                    Coordinate initial = new Coordinate(startingPositions[allianceTog.currentState()][topBottomTog.currentState()],90);
                    robot.setInitialCoordinate(initial);
                }
                break;
            case 1:
                robot.driveToTarget(carouselPositions[allianceNum], true);
                break;
            case 2:
                robot.driveToTarget(shippingHubPosition[allianceNum], true);
                break;
            case 3:
                robot.driveToTarget(warehouse[allianceNum], false);
                break;
            case 4:
                robot.driveToTarget(finalParkingPosition[allianceNum][topOrBottom],false);
                break;

        }
        
        telemetry.addData("Position State: ", posStateNum);
        telemetry.addData("Alliance State: ", allianceNum);
        telemetry.addData("Top or Bottom",topOrBottom);
    }
}
