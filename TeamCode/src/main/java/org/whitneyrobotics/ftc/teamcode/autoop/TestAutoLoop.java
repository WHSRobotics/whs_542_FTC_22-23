package org.whitneyrobotics.ftc.teamcode.autoop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.whitneyrobotics.ftc.teamcode.lib.geometry.Coordinate;
import org.whitneyrobotics.ftc.teamcode.lib.geometry.Position;
import org.whitneyrobotics.ftc.teamcode.subsys.WHSRobotImplDrivetrainOnly;

@TeleOp(name="Test Auto Loop",group="Tests")
public class TestAutoLoop extends OpMode {

    public Position initP = new Position(-1647.6,900);
    public Position one = new Position(-1207.6,1340);
    public Position two = new Position(-1000,1340);
    public Position three = new Position(-600,900);
    private WHSRobotImplDrivetrainOnly robot;
    private int caseCounter = 0;
    private String destination = "";
    private Position currentTarget;

    @Override
    public void init(){
        robot = new WHSRobotImplDrivetrainOnly(hardwareMap);
        robot.drivetrain.resetEncoders();
        robot.drivetrain.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        Coordinate init = new Coordinate(initP,0);
        robot.setInitialCoordinate(init);
        currentTarget = one;
    }

    @Override
    public void loop(){
        robot.estimateHeading();
        robot.estimatePosition();

        if (gamepad1.y){
            throw new RuntimeException("Bad");
        }

        if(gamepad1.a){
            robot.drivetrain.operate(0,0);
        } else{
            switch(caseCounter){
                case 0:
                    destination = "One, " + one.getX() + ", " + one.getY();
                    robot.driveToTarget(one,false);
                    if(!robot.driveToTargetInProgress()){
                        caseCounter++;
                    }
                    break;
                case 1:
                    currentTarget = two;
                    destination = "Two, " + two.getX() + ", " + two.getY();
                    robot.driveToTarget(two,false);
                    if(!robot.driveToTargetInProgress()){
                        caseCounter++;
                    }
                    break;
                case 2:
                    currentTarget = three;
                    destination = "Three, " + three.getX() + ", " + three.getY();
                    robot.driveToTarget(three,false);
                    if(!robot.driveToTargetInProgress()){
                        caseCounter++;
                    }
                    break;
                case 3:
                    currentTarget = initP;
                    destination = "Initial " + initP.getX() + ", " + initP.getY();
                    robot.driveToTarget(initP,true);
                    if(!robot.driveToTargetInProgress()){
                        caseCounter = 0;
                    }
                default:
                    destination = "Done uwu";
                    break;
            }
        }

        telemetry.addData("Destination",destination);
        telemetry.addData("Drive to target",robot.driveToTargetInProgress());
        telemetry.addData("Rotate to target",robot.rotateToTargetInProgress());
        telemetry.addData("Robot X",robot.getCoordinate().getX());
        telemetry.addData("Robot Y",robot.getCoordinate().getY());
        telemetry.addData("Robot heading",robot.getCoordinate().getHeading());
        telemetry.addData("Error X", currentTarget.getX()-robot.getCoordinate().getX());
        telemetry.addData("Error Y",currentTarget.getY()-robot.getCoordinate().getY());
        telemetry.addData("Average Error",((currentTarget.getX()-robot.getCoordinate().getX())+(currentTarget.getY()-robot.getCoordinate().getY())/2));
    }
}
