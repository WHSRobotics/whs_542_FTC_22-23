package org.whitneyrobotics.ftc.teamcode.NewTests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.whitneyrobotics.ftc.teamcode.lib.geometry.Coordinate;
import org.whitneyrobotics.ftc.teamcode.lib.util.GamepadListener;
import org.whitneyrobotics.ftc.teamcode.lib.util.SelectionMenu;
import org.whitneyrobotics.ftc.teamcode.lib.util.SimpleTimer;
import org.whitneyrobotics.ftc.teamcode.subsys.Drivetrain;
import org.whitneyrobotics.ftc.teamcode.subsys.WHSRobotImplDrivetrainOnly;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedHashMap;

@TeleOp(name="Drivetrain Motor Calibration",group="Tests")
public class DrivetrainMotorCalibration extends OpMode {
    private WHSRobotImplDrivetrainOnly robot;
    private double FLReduction = 1;
    private double FRReduction = 1;
    private double BLReduction = 1;
    private double BRReduction = 1;

    private double deltaReductionFL = 0;
    private double deltaReductionFR = 0;
    private double deltaReductionBL = 0;
    private double deltaReductionBR = 0;

    private double lastRecAverage = 0;
    private double deltaAverage = 0;

    private long startTime;

    private int state = 0;
    private int substate = 0;

    private static double MARGIN_OF_ERROR = 20;
    private int consecutiveAverageLoops = 0; //Counts how many loops the average has remained relatively stable, used to auto end calibration
    private int endAfterXStableAvgLoops = 50; //Ends after change in average has remained in the margin of error for x amount of loops

    private SelectionMenu config;
    private SimpleTimer accelerationTime = new SimpleTimer();
    private GamepadListener gamepadListener = new GamepadListener();

    private double motorPower;
    private int marginOfError;
    private boolean onMat = true;

    private final static int CONFIG = 0;
    private final static int ACCELERATE = 1;
    private final static int CALIBRATE = 2;
    private final static int REVERSE = 3;
    private final static int END = 4;

    private FtcDashboard dashboard;
    Telemetry dashboardTelemetry;
    TelemetryPacket packet = new TelemetryPacket();

    @Override
    public void init() {
        dashboard = FtcDashboard.getInstance();
        dashboardTelemetry = dashboard.getTelemetry();

        SelectionMenu.Prompt speed = new SelectionMenu.Prompt("Motor speed to calibrate at")
                .addSelection("0.25",0.25)
                .addSelection("0.5",0.5)
                .addSelection("0.75",0.75)
                .addSelection("1",1);
        SelectionMenu.Slider errorMargin = new SelectionMenu.Slider("Margin of error (ticks/s) in both directions",1,251);
        SelectionMenu.Prompt onGround = new SelectionMenu.Prompt("Is the robot on the floor and upright?")
                .addSelection("Yes",true)
                .addSelection("No",false);
        config = new SelectionMenu("Calibration configuration",speed,errorMargin,onGround);
        config.addInstructions("Press A+B to begin calibration. Press X to end calibration early.");

        robot = new WHSRobotImplDrivetrainOnly(hardwareMap);
        robot.drivetrain.resetEncoders();
        robot.setInitialCoordinate(new Coordinate(0,0,0));
    }

    @Override
    public void loop() {
        robot.estimatePosition();
        switch(state){
            case CONFIG:
                config.run(gamepad1.dpad_right,gamepad1.dpad_left,gamepad1.dpad_down,gamepad1.dpad_up);
                if(gamepad1.a && gamepad1.b){
                    Object[] results = config.getOutputs();
                    motorPower = (double)Double.parseDouble(results[0].toString());
                    marginOfError = (int)Integer.parseInt(results[1].toString());
                    onMat = (boolean)Boolean.parseBoolean(results[2].toString());
                    startTime = System.nanoTime();
                    advanceState();
                }
                telemetry.addLine(config.formatDisplay());
                packet.addLine(config.formatDisplay());
                break;
            case ACCELERATE:
                robot.drivetrain.operate(new double[]{motorPower*FLReduction,motorPower*FRReduction,motorPower*BLReduction,motorPower*BRReduction});
                switch(substate){
                    case 0:
                        accelerationTime.set(0.2);
                        substate++;
                        break;
                    case 1:
                        if(accelerationTime.isExpired()){
                            advanceState();
                        }
                }
                telemetry.addData("Elapsed time",calculateAndFormatTimeElapsed(startTime));
                telemetry.addLine("Accelerating...");
                packet.put("Elapsed time",calculateAndFormatTimeElapsed(startTime));
                packet.addLine("Accelerating...");
                break;
            case CALIBRATE:
                if(robot.getCoordinate().getX()>1800 && onMat){
                    state = REVERSE;
                    break;
                }

                robot.drivetrain.operate(new double[]{motorPower*FLReduction,motorPower*FRReduction,motorPower*BLReduction,motorPower*BRReduction});

                double[] velocities = robot.drivetrain.getAllWheelVelocitiesTPS();
                double avgVelocity = 0.25*(velocities[0]+velocities[1]+velocities[2]+velocities[3]);

                //only lower motor powers if faster than normal
                if(velocities[0]-avgVelocity > marginOfError){
                    FLReduction -= 0.001;
                }
                if(velocities[1]-avgVelocity > marginOfError){
                    FRReduction -= 0.001;
                }
                if(velocities[2]-avgVelocity > marginOfError){
                    BLReduction -= 0.001;
                }
                if(velocities[3]-avgVelocity > marginOfError){
                    BRReduction -= 0.001;
                }

                deltaAverage = avgVelocity - lastRecAverage;
                if(Math.abs(deltaAverage)<marginOfError){
                 consecutiveAverageLoops++;
                } else {
                    consecutiveAverageLoops = 0;
                }

                if(consecutiveAverageLoops >= endAfterXStableAvgLoops){
                    state = END;
                }
                telemetry.addData("Elapsed time",calculateAndFormatTimeElapsed(startTime));
                telemetry.addData("Motor power",motorPower);
                telemetry.addData("Margin of Error",marginOfError);
                telemetry.addData("On mats", onMat);
                telemetry.addLine();
                telemetry.addData("FL Reduction",FLReduction);
                telemetry.addData("FR Reduction",FRReduction);
                telemetry.addData("BL Reduction",BLReduction);
                telemetry.addData("BR Reduction",BRReduction);
                telemetry.addLine();
                telemetry.addData("Waiting for average to stabilize",String.format("(%d/%d)",consecutiveAverageLoops,endAfterXStableAvgLoops));

                packet.put("Elapsed time",calculateAndFormatTimeElapsed(startTime));
                packet.put("Motor power",motorPower);
                packet.put("Margin of Error",marginOfError);
                packet.put("On mats", onMat);
                packet.addLine("");
                packet.put("FL Reduction",FLReduction);
                packet.put("FR Reduction",FRReduction);
                packet.put("BL Reduction",BLReduction);
                packet.put("BR Reduction",BRReduction);
                packet.addLine("");
                packet.put("Waiting for average to stabilize",String.format("(%d/%d)",consecutiveAverageLoops,endAfterXStableAvgLoops));
                break;
            case REVERSE:
                if(robot.getCoordinate().getX() >= 0){
                    double power = (motorPower/2 > 0.25) ? motorPower/2 : 0.25;
                    robot.drivetrain.operate(-power,-power); //go back until you are behind the start line
                } else {
                    state = ACCELERATE;
                    substate = 0;
                }
                telemetry.addData("Elapsed time",calculateAndFormatTimeElapsed(startTime));
                telemetry.addLine("Reversing...");
                packet.put("Elapsed time",calculateAndFormatTimeElapsed(startTime));
                packet.addLine("Reversing...");
                break;
            case END:
                robot.drivetrain.operate(0,0);
                telemetry.addData("Motor power",motorPower);
                telemetry.addData("Margin of Error",marginOfError);
                telemetry.addData("On mats", onMat);
                telemetry.addLine();
                telemetry.addData("FL Reduction",FLReduction);
                telemetry.addData("FR Reduction",FRReduction);
                telemetry.addData("BL Reduction",BLReduction);
                telemetry.addData("BR Reduction",BRReduction);
                telemetry.addLine();
                telemetry.addLine("Hold X to recalibrate.");

                packet.put("Motor power",motorPower);
                packet.put("Margin of Error",marginOfError);
                packet.put("On mats", onMat);
                packet.addLine("");
                packet.put("FL Reduction",FLReduction);
                packet.put("FR Reduction",FRReduction);
                packet.put("BL Reduction",BLReduction);
                packet.put("BR Reduction",BRReduction);
                packet.addLine("");
                packet.addLine("Hold X to recalibrate.");
                if(gamepadListener.longPress(gamepad1.x,3000)){
                    state = 0;
                    substate = 0;
                    break;
                }
                break;
        }
        dashboard.sendTelemetryPacket(packet);
    }

    private void advanceState(){
        state++;
        substate = 0;
    }

    private String calculateAndFormatTimeElapsed(long initTime){
        long ElapsedSeconds = Math.round((System.nanoTime()-initTime)/1E9);
        DecimalFormat secondsFormat = new DecimalFormat("##");
        return String.format("%s:%s",(int)Math.floor(ElapsedSeconds/60.0),secondsFormat.format(ElapsedSeconds%60));
    }
}
