package org.whitneyrobotics.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.whitneyrobotics.ftc.teamcode.lib.geometry.Coordinate;
import org.whitneyrobotics.ftc.teamcode.lib.util.DataToolsLite;
import org.whitneyrobotics.ftc.teamcode.lib.util.GamepadListener;
import org.whitneyrobotics.ftc.teamcode.lib.util.SimpleTimer;
import org.whitneyrobotics.ftc.teamcode.subsys.Outtake;
import org.whitneyrobotics.ftc.teamcode.subsys.WHSRobotImpl;

import java.text.DecimalFormat;

@TeleOp(name = "WHS TeleOp Drivetrain Disabled", group = "TeleOp")
public class TeleOpDrivetrainDisabled extends OpMode {
    WHSRobotImpl robot;
    private GamepadListener gamepadListener1 = new GamepadListener();
    private int autoDropState = 1;
    private Outtake outtake;
    private int autoDropStateTele = 1;
    private boolean autoEndTele = false;
    private boolean firstTeleMatchLoop = true;
    private int teleState = 0;
    private double teleOpTime = 90.3;
    private SimpleTimer teleTimer = new SimpleTimer();
    private SimpleTimer matchTimer = new SimpleTimer();
    private boolean teleStarted = false;
    private SimpleTimer teleCountdown = new SimpleTimer();
    private int countdownState = 0;
    private boolean useCountdown = false;

    private boolean rotateCommand = false;
    private boolean rotateBackwards = false;

    private int outtakeAClicks = 0;

    private boolean[] notificationsPushed = {false, false}; //first for endgame, second for match end
    private double[] notificationTimes = {90.0,120.0};
    Gamepad.RumbleEffect endgame = new Gamepad.RumbleEffect.Builder()
            .addStep(0.5,0.5,500)
            .addStep(0,0,500)
            .addStep(0.5,0.5,500)
            .addStep(0,0,500)
            .addStep(0.5,0.5,500)
            .addStep(0,0,500)
            .build();
    Gamepad.RumbleEffect matchEnd = new Gamepad.RumbleEffect.Builder()
            .addStep(0.20,0.20,350)
            .addStep(0,0,150)
            .addStep(0.40,0.40,350)
            .addStep(0,0,150)
            .addStep(0.60,0.60,350)
            .addStep(0,0,150)
            .addStep(0.80,0.80,350)
            .addStep(0,0,150)
            .addStep(1,1,350)
            .build();

    private long startTime;
    String[] outtakeLabels = new String[]{"Level 1","Level 1.5","Level 2", "Level 3"};

    private long lastRecordedTime;

    @Override
    public void init() {
        /*Outtake.MotorLevels.LEVEL3.changePosition(840);
        Outtake.MotorLevels.LEVEL1.changePosition(-30);*/
        telemetry.setAutoClear(false);
        robot = new WHSRobotImpl(hardwareMap);
        try {
            String[] data = DataToolsLite.decode("autoConfig.txt");
            String[] teleData = DataToolsLite.decode("teleOpConfig.txt");
            robot.setInitialCoordinate(new Coordinate(0,0,Double.parseDouble(DataToolsLite.decode("heading.txt")[0])));
            robot.carousel.setAlliance((int)Integer.parseInt(data[0],10));
            autoEndTele = ((boolean)Boolean.parseBoolean(teleData[0]));
            teleOpTime += ((boolean)Boolean.parseBoolean((teleData[1])) ? 30.0 : 0.0);
            useCountdown = ((boolean)Boolean.parseBoolean(teleData[2]));
            notificationTimes[0] -= Double.parseDouble(teleData[3]);
            notificationTimes[1] -= Double.parseDouble(teleData[4]);

            telemetry.addLine("Auto set alliance to " + robot.carousel.getAlliance());
            telemetry.addData("TeleOp Timer",autoEndTele);
            telemetry.addData("Endgame time included", ((boolean)Boolean.parseBoolean((teleData[1]))));
            telemetry.addData("Countdown",useCountdown);
            telemetry.addLine("I will notify you about endgame at " + notificationTimes[0] + "seconds into TeleOp");
            telemetry.addLine("I will notify you about match end at " + notificationTimes[1] + "seconds into TeleOp");
        } catch (Exception e){
            telemetry.addLine("There was a problem decoding some data");
            telemetry.addLine(e.getMessage());
            System.out.println("There was a problem decoding some data");
        }
        lastRecordedTime=lastRecordedTime = System.nanoTime();
        robot.levelSelector.setState(robot.levelSelector.howManyStates());
    }

    // Driver 1 (Gamepad 1): Drivetrain, Intake
    // Driver 2 (Gamepad 2): Intake Reverse, Carousel, Outtake

    @Override
    public void loop() {
        if (outtakeAClicks >= 2){
            Outtake.MotorLevels.LEVEL3.changePosition(840);
            Outtake.MotorLevels.LEVEL1.changePosition(-30);
        }
        if(!autoEndTele){
            if(firstTeleMatchLoop){
                matchTimer.set(150.3);
                firstTeleMatchLoop = false;
            } else {
                if(matchTimer.getTimeElapsed()>notificationTimes[0] && !notificationsPushed[0]){
                    gamepad1.runRumbleEffect(endgame);
                    gamepad2.runRumbleEffect(endgame);
                    notificationsPushed[0] = false;
                }
                if(matchTimer.getTimeElapsed()>notificationTimes[1] && !notificationsPushed[1]){
                    gamepad1.runRumbleEffect(matchEnd);
                    gamepad2.runRumbleEffect(matchEnd);
                    notificationsPushed[1] = false;
                }
                //if(matchTimer.isExpired()) requestOpModeStop(); //Dunno if we should keep this, kills the OpMode gracefully if teleop is done
            }
        }

        telemetry.setAutoClear(true);
        robot.estimateHeading();

        // DRIVER 1 CONTROLS
        // Drivetrain
        switch(teleState) {
            case 0:
                if(useCountdown){
                    switch(countdownState){
                        case 0:
                            teleCountdown.set(3);
                            countdownState++;
                            break;
                        case 1:
                            telemetry.addLine("Drivers, pick up your controllers.");
                            if(teleCountdown.isExpired()){
                                teleCountdown.set(1);
                                countdownState++;
                            }
                            break;
                        case 2:
                            telemetry.addLine("3");
                            if(teleCountdown.isExpired()){
                                teleCountdown.set(1);
                                countdownState++;
                            }
                            break;
                        case 3:
                            telemetry.addLine("2");
                            if(teleCountdown.isExpired()){
                                teleCountdown.set(1);
                                countdownState++;
                            }
                            break;
                        case 4:
                            telemetry.addLine("1");
                            if(teleCountdown.isExpired()){
                                teleCountdown.set(10);
                                countdownState++;
                                teleState = 1;

                                //if(!teleStarted){
                                startTime = System.nanoTime();
                                teleTimer.set(teleOpTime);
                                teleStarted = true;
                                //}
                            }
                            break;
                    }
                } else {
                    teleState = 1;
                    //if(!teleStarted){
                    startTime = System.nanoTime();
                    teleTimer.set(teleOpTime);
                    teleStarted = true;
                    //}
                }
                break;


            case 1:
                if(!teleCountdown.isExpired()){
                    telemetry.addLine("*fanfare*");
                }
                if (gamepad1.back) {
                    robot.drivetrain.switchFieldCentric(gamepad1.back);
                }
                //experimental feature
                robot.drivetrain.brake(gamepad1.left_trigger);
                /*
                if(gamepad1.a){
                    rotateCommand=true;
                    rotateBackwards = false;
                } else if(gamepad1.b){
                    rotateCommand = true;
                    rotateBackwards=true;
                } else if(gamepad1.x){
                    rotateCommand = false;
                }

                if(rotateCommand){
                    robot.rotateToTarget(0,rotateBackwards );
                }
                if(!rotateCommand){
                    if (gamepad1.left_bumper) {
                        robot.drivetrain.operateMecanumDrive(-gamepad1.left_stick_x / 3, gamepad1.left_stick_y / 3, -gamepad1.right_stick_x / 3, robot.getCoordinate().getHeading());
                    } else {
                        robot.drivetrain.operateMecanumDriveScaled(-gamepad1.left_stick_x, gamepad1.left_stick_y, -gamepad1.right_stick_x, robot.getCoordinate().getHeading());
                    }
                }*/

                if(Math.abs(gamepad1.left_stick_x)<0.05 || Math.abs(gamepad1.left_stick_y)<0.05 || Math.abs(gamepad1.right_stick_x) < 0.05 || Math.abs(gamepad1.right_stick_y) < 0.05 || !robot.rotateToTargetInProgress() || gamepad1.x){
                    rotateCommand = false;
                    robot.firstRotateLoop = false;
                }
                // Intake
                robot.intake.operate(gamepad1.right_bumper || gamepad2.right_bumper, gamepad1.right_trigger > 0.05 || gamepad2.right_trigger > 0.05); //just so player 2 can reverse

                // DRIVER 2 CONTROLS
                // Intake
                // - Extend arm/unextend arm
                robot.outtakeInStates(gamepad2.a, gamepad2.b, gamepad2.dpad_up, gamepad2.dpad_down, gamepad2.x, -gamepad2.left_stick_y, gamepad2.dpad_left, gamepad2.b);
                // Outtake
                // - Adjust levels

                //robot.robotOuttake.togglerOuttake(gamepad2.dpad_right, gamepad2.dpad_left);
                //robot.outtake.operate(gamepad2.dpad_up,gamepad2.dpad_down);

                //In case line outtake levels don't work properly
            /*if(gamepad1.dpad_up){
                robot.robotOuttake.linearSlides.setPower(-0.2);
            } else if (gamepad1.dpad_down){
                robot.robotOuttake.linearSlides.setPower(0.2);
            } else {
                robot.robotOuttake.linearSlides.setPower(0);
            }*/

                //if(gamepad2.a) { robot.robotOuttake.reset(); }
                robot.carousel.operate(gamepad2.left_bumper||gamepad2.left_trigger>0.01,gamepad2.left_bumper);
                // - Hatch | Servo gate
            /*if(gamepad2.b){
                autoDropState = 1;
            }


                switch(autoDropStateTele){
                    case 0:
                        if(robot.outtake.autoDrop()){
                            autoDropStateTele++;
                        }
                        break;
                    case 1:
                        if (gamepad2.y && !robot.outtake.isGateBusy()){
                            autoDropStateTele = 0;
                            break;
                        }
                }*/
                telemetry.addData("Drive mode",robot.drivetrain.getFieldCentric());
                telemetry.addData("Selected Outtake Level",robot.outtakeLevel());
                telemetry.addData("Sliding in Progress",robot.outtake.slidingInProgress);
                telemetry.addData("Outtake state",robot.stateDesc);
                telemetry.addData("Carousel Alliance",robot.carousel.getAlliance());
                telemetry.addData("Outtake encoder position",robot.outtake.getSlidesPosition());
                telemetry.addData("Outtake Level",outtakeLabels[robot.outtake.getTier()]);
                telemetry.addLine();
                telemetry.addData("Gate Position", robot.outtake.gate.getPosition());
                telemetry.addData("Gamepad 2 Back",gamepad2.back);
                telemetry.addData("Current processing latency: ", (Math.ceil(System.nanoTime()-lastRecordedTime)/1E6) + "ms");
                telemetry.addData("Time Elapsed",calculateAndFormatTimeElapsed(startTime));
                if(autoEndTele && teleTimer.isExpired()){
                    teleState = 2;
                }
                break;
            case 2:
                robot.drivetrain.operate(0,0);
                robot.intake.disable();
                robot.outtake.operateSlides(0);
                telemetry.addLine("Tele disabled in DP mode. Press start on both controllers to re-enable.");
                if(gamepad1.start && gamepad2.start){
                    teleState = 0;
                    teleStarted = false;
                }
                break;
        }

        robot.outtake.updateGateOverride(gamepad2.dpad_left);
        if(gamepadListener1.longPress(gamepad2.back,1000)){
            throw new RuntimeException("UnknownException - TeamCode terminated with a non-zero exit code.");
        }

        lastRecordedTime = System.nanoTime();

    }

    private String calculateAndFormatTimeElapsed(long initTime){
        long ElapsedSeconds = Math.round((System.nanoTime()-initTime)/1E9);
        DecimalFormat secondsFormat = new DecimalFormat("##");
        return String.format("%s:%s",(int)Math.floor(ElapsedSeconds/60.0),secondsFormat.format(ElapsedSeconds%60));
    }
}

