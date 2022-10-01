//package org.whitneyrobotics.ftc.teamcode.tests;
//
//import com.acmerobotics.dashboard.FtcDashboard;
//import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
//import com.qualcomm.robotcore.eventloop.opmode.OpMode;
//import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
//import com.qualcomm.robotcore.hardware.DcMotor;
//import com.qualcomm.robotcore.hardware.DcMotorSimple;
//import com.qualcomm.robotcore.hardware.HardwareMap;
//import com.qualcomm.robotcore.hardware.Servo;
//
//import org.whitneyrobotics.ftc.teamcode.lib.util.SimpleTimer;
//import org.whitneyrobotics.ftc.teamcode.lib.util.Toggler;
//import org.whitneyrobotics.ftc.teamcode.subsys.Intake;
//
//    @TeleOp(name = "Intake Test", group = "Tests")
//    public class IntakeTestBruhVersion extends OpMode {
//        public Intake testIntake = new Intake();
//        public Toggler intakeState = Toggler();
//        public Toggler rollerState;
//
//        private FtcDashboard dashboard;
//        private TelemetryPacket packet = new TelemetryPacket();
//
//        int i;
//        double power = 0;
//        double position =0;
//
//        @Override
//        public void init() {
//            //testIntake = new Intake();
//            i = 0;
//        }
//
//        @Override
//        public void loop() {
//            i++;
//            if(i%10 == 0){
//                if(gamepad1.a && power < 1){
//                    power += 0.01;
//                }else if(gamepad1.b && power > -1)
//                {
//                    power -= 0.01;
//                }
//
//                if(gamepad1.x && position < 1){
//                    position += 0.01;
//                }else if(gamepad1.y && position > 0)
//                {
//                    position -= 0.01;
//                }
//            }
//            //testIntake.setIntakePower(power);
//            telemetry.addData("Roller Power: ", power);
//            telemetry.addData("Arm Position: ", position);
//
//            packet.put("Roller Power: ", power);
//            packet.put("Arm Position: ", position);
//            dashboard.sendTelemetryPacket(packet);
//
//        }
//
//        private DcMotor surgicalTubes;
//        private DcMotor arm;
//
//        // 5281.1 ticks per revolution
//
//        private Servo eject;
//        public SimpleTimer armTimer = new SimpleTimer();
//        public double armTimerDelay = 1; //change based on testing
//        int state = 0;
//        public boolean intakeAutoDone = false;
//
//        public int[] armPositions = {500, 0}; //change numbers later
//        public enum ArmPositions {
//            DOWN, UP
//        }
//
//        public double[] pusherPositions = {0.2, 0.8, 1};
//        public enum PusherPositions {
//            IN, OUT, FULLY_OUT
//        }
//
//        private Toggler intakeStateTog = new Toggler(2);
//        private Toggler armPositionTog = new Toggler(2);
//        private Toggler ejectStateTog = new Toggler(2);
//
//        private Toggler armPositionTestingTogTuning = new Toggler(52);
//        private Toggler armPositionTestingTogFineTune = new Toggler(70);
//
//        public Intake(HardwareMap map) {
//            surgicalTubes = map.dcMotor.get("intakeMotor");
//            arm = map.dcMotor.get("armMotor");
//            eject = map.servo.get("ejectServo");
//            arm.setDirection(DcMotorSimple.Direction.REVERSE);
//        }
//
//        public void resetAllEncoder(){
//            surgicalTubes.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//            arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//            surgicalTubes.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//            arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        }
//
//        public void operate(boolean armState, boolean togOnOff, boolean reverse, boolean deposit){
//            if(deposit){
//                eject.setPosition(pusherPositions[Intake.PusherPositions.FULLY_OUT.ordinal()]);
//            } else {
//                eject.setPosition(pusherPositions[Intake.PusherPositions.IN.ordinal()]);
//            }
//            armPositionTog.changeState(armState);
//            intakeStateTog.changeState(togOnOff);
//            if (reverse) {
//                surgicalTubes.setPower(-1);
//            } else if (intakeStateTog.currentState() == 0) {
//                if(armPositionTog.currentState() != 0 && !deposit){
//                    surgicalTubes.setPower(1);
//                    eject.setPosition(pusherPositions[Intake.PusherPositions.OUT.ordinal()]);
//                } else {
//                    surgicalTubes.setPower(0);
//                }
//            } else if (intakeStateTog.currentState() == 1 && armPositionTog.currentState() != 0) {
//                surgicalTubes.setPower(1);
//            } else {
//                surgicalTubes.setPower(0);
//            }
//
//            if(armPositionTog.currentState() == 0){
//                arm.setTargetPosition(armPositions[Intake.ArmPositions.UP.ordinal()]);
//            } else {
//                arm.setTargetPosition(armPositions[Intake.ArmPositions.DOWN.ordinal()]);
//            }
//        }
//
//        public void setIntakePower(double power) { surgicalTubes.setPower(power);}
//
//        public void armTest(boolean hundreds, boolean ones) {
//            resetAllEncoder();
//            arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//            armPositionTestingTogTuning.changeState(hundreds);
//            armPositionTestingTogFineTune.changeState(ones);
//            arm.setTargetPosition(armPositionTestingTogFineTune.currentState() + (armPositionTestingTogTuning.currentState()*100));
//            arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        }
//
//        public void autoDropIntake(){
//            switch(state){
//                case 0:
//                    intakeAutoDone = false;
//                    armTimer.set(armTimerDelay);
//                    state++;
//                    break;
//                case 1:
//                    if(armTimer.isExpired()){
//                        armPositionTog.changeState(true);
//                        surgicalTubes.setPower(0);
//                        resetAllEncoder();
//                        arm.setTargetPosition(0);
//                        arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//                        intakeAutoDone = true;
//                        state = 0;
//                    }
//                    break;
//            }
//
//        }
//
//    }
//
