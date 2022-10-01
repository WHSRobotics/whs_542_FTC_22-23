package org.whitneyrobotics.ftc.teamcode.tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.whitneyrobotics.ftc.teamcode.lib.util.Toggler;
import org.whitneyrobotics.ftc.teamcode.subsys.Outtake;
@Config
@TeleOp (name="Outtake Test", group="Tests")
public class OuttakeTest extends OpMode {

    public Outtake outtake;
    public double power = 0.35;
    public double servoPosition;
    FtcDashboard dashboard;
    Telemetry dashboardTelemetry;
    TelemetryPacket packet = new TelemetryPacket();
    private Toggler modeTog = new Toggler(2);
    private Toggler gateTog = new Toggler(2);
    public static double level1 = 0.0;
    public static double level1_5 = 238.0;
    public static double level2 = 438;
    public static double level3 = 860;
    public static int autoDropState = 1;

    @Override
    public void init() {
        dashboard = FtcDashboard.getInstance();
        dashboardTelemetry = dashboard.getTelemetry();
        dashboardTelemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        dashboardTelemetry.setMsTransmissionInterval(10);

        outtake = new Outtake(hardwareMap);
        servoPosition = outtake.getServoPosition();
        outtake.toggleTestPositions();
    }

    public void kill() {
        outtake.operateSlides(0);
        throw new RuntimeException("Kill command issued.");
    }

    @Override
    public void loop() {
        modeTog.changeState(gamepad1.x);
        if(modeTog.currentState() == 1){
            outtake.setTestPositions(new double[]{level1,level1_5,level2,level3});
            outtake.operate(gamepad1.dpad_up, gamepad1.dpad_down);
            if (gamepad1.y) { outtake.reset(); }
        }else {
            if (gamepad1.dpad_up) {
                outtake.operateSlides(power);
            }
            else if (gamepad1.dpad_down) {
                outtake.operateSlides(-power);
            }
            else {
                outtake.operateSlides(0);
            }
        }
        if(autoDropState == 1){
            outtake.togglerServoGate(gamepad1.a);
        }
        switch(autoDropState){
            case 0:
                if(outtake.autoDrop()){
                    autoDropState++;
                }
                break;
            case 1:
                if(gamepad1.b){autoDropState = 0;}
        }

        if(gamepad1.left_bumper){
            level1 =  outtake.getSlidesPosition();
            gamepad1.rumble(250);
        }
        else if (gamepad1.right_bumper) {
            level2 = outtake.getSlidesPosition();
            gamepad1.rumble(250);
        }
        else if (gamepad1.right_trigger >= 0.01) {
            level3 = outtake.getSlidesPosition();
            gamepad1.rumble(250);
        }

        //outtake.togglerOuttake(gamepad1.b, gamepad1.a);


        //emergency kill switch
        if(gamepad1.y){kill();}

        telemetry.addData("Mode", (modeTog.currentState() == 0) ? "Manual Configure Mode" : "Test Mode");
        telemetry.addData("Amperage (Left)",outtake.getAmperageLeft());
        telemetry.addData("Amperage (Right)",outtake.getAmperageRight());
        telemetry.addData("Error",outtake.errorDebug);
        telemetry.addData("Encoder Position", outtake.getSlidesPosition());
        telemetry.addData("Current Tier (0-2)", outtake.getTier());
        telemetry.addData("Level 1", level1);
        telemetry.addData("Level 2", level2);
        telemetry.addData("Level 3", level3);

        packet.put("Mode", (modeTog.currentState() == 0) ? "Manual Configure Mode" : "Test Mode");
        packet.put("Amperage (Left)",outtake.getAmperageLeft());
        packet.put("Amperage (Right)",outtake.getAmperageRight());
        packet.put("Error",outtake.errorDebug);
        packet.put("Encoder Position", outtake.getSlidesPosition());
        packet.put("Current Tier (0-2)",outtake.getTier());
        packet.put("Level 1", level1);
        packet.put("Level 2", level2);
        packet.put("Level 3", level3);
        dashboard.sendTelemetryPacket(packet);
    }

}
