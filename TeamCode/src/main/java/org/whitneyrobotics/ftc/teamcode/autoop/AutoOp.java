package org.whitneyrobotics.ftc.teamcode.autoop;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.PtzControl;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;
import org.whitneyrobotics.ftc.teamcode.framework.DashboardOpMode;
import org.whitneyrobotics.ftc.teamcode.lib.geometry.Coordinate;
import org.whitneyrobotics.ftc.teamcode.lib.geometry.Position;
import org.whitneyrobotics.ftc.teamcode.lib.util.DataToolsLite;
import org.whitneyrobotics.ftc.teamcode.lib.util.SimpleTimer;
import org.whitneyrobotics.ftc.teamcode.subsys.WHSRobotImpl;
import org.whitneyrobotics.ftc.teamcode.visionImpl.BarcodeScanner;

import java.util.ArrayList;

@Autonomous (name="WHS Freight Frenzy Auto",preselectTeleOp = "WHS TeleOp")
public class AutoOp extends DashboardOpMode {

    public WHSRobotImpl robot;

    static final int RED = 0;
    static final int BLUE = 1;
    static final int BOTTOM = 0;
    static final int TOP = 1;

    // values for our array positions
    static final int TESTED_LEFT = 0;
    static final int TESTED_TOP = 1;
    static final int TESTED_RIGHT = 2;
    static final int TESTED_BOTTOM = 3;

    public float CAMERA_LEFT;
    public float CAMERA_TOP;
    public float CAMERA_RIGHT;
    public float CAMERA_BOTTOM;

    public final float ERROR_MARGIN = 15;

    private double tempHeading;

    private SimpleTimer generalTimer = new SimpleTimer();

    int STARTING_ALLIANCE = RED;
    int STARTING_SIDE = BOTTOM;

    private boolean useScanner = false;
    private int scanLevel = 3;
    private int numCycles = 0;
    private int cycleCounter = 0;

    Position[][] startingPositions = new Position[2][2];
    Position[][] startingOffsetPositions = new Position[2][2];
    Position[] carouselApproach = new Position[2];
    Position[] carouselPositions = new Position[2];
    Position[] gapApproach = new Position[2];
    Position[] gapCrossPositions = new Position[2];
    Position[] shippingHubApproach = new Position[2];
    Position[] shippingHubPosition = new Position[2];
    Position[] sharedShippingHub = new Position[2];
    Position[] warehouse = new Position[2];
    Position[] storageUnitPositions = new Position[2];
    Position[] shippingHubDepositApproach = new Position[2];
    Position[] shippingHubDeposit = new Position[2];

    static final int INIT = 0;
    static final int ROTATE_CAROUSEL = 1;
    static final int PRELOAD = 2;
    static final int WAREHOUSE = 3;
    static final int PARK = 4;
    static final int STOP = 5;

    static final int NUMBER_OF_STATES = 6;

    int state = INIT;
    int subState = 0;
    int superSubState = 0;

    boolean[] stateEnabled = new boolean[NUMBER_OF_STATES];

    public void defineStatesEnabled(){
        stateEnabled[INIT] = true;
        stateEnabled[ROTATE_CAROUSEL] = true;
        stateEnabled[PRELOAD] = true;
        stateEnabled[WAREHOUSE] = false;
        stateEnabled[PARK] = true;
        stateEnabled[STOP] = true;
    }


    public BarcodeScanner.Barcode result = BarcodeScanner.Barcode.RIGHT;
    private int parkLocation = 0;
    private boolean saveHeading = true;

    public String[] stateNames = {"Init", "Rotate Carousel", "Pre-load", "Warehouse", "Park", "Stop"};

    public float[][] barcodeLocation = new float[3][4];

    public double[][][] barcodeArrangements= {
        {{2,2,3}, {3,2,2}},
            {{2,2,4}, {4,2,2}}
    };

    public void advanceState(){
        if (stateEnabled[state + 1]){
            superSubState = 0;
            subState = 0;
            state++;
        } else {
            state++;
            advanceState();
        }
    }

    public long lastRecordedTime = System.nanoTime();

    // Camera INIT Methods
    private static final String TFOD_MODEL_ASSET = "FreightFrenzy_BCDM.tflite";

    private static final String[] LABELS = {
            "Ball",
            "Cube",
            "Duck",
            "Marker"
    };

    private static final String VUFORIA_KEY =
            "AWYX8QX/////AAABmQ8w8KJJuEsNlO9fxNmHDg1BoH/L5lzniFIDqLd+XlCF9gXWlYeddle27IIm9DH8mtLY2CLX9LW3uAzD8IH5Stmf+NoLjfm+m4jnj7KmR+v+xGuUEgP3Aj8sez5uhtsKarKiv94URMVnf39sjHW3xhiUBI30M762Ee6bEy69ZHQSOHLNxMwm9lnETo0O13vhmtZvI44HtEjIvXbW71p/+jdZw/33i6q//G4O3h5Ej+MQ3UCgUe9ERfh9L/v/lgLmekgYdFNaUZi8C1z+O4Jb/8MbHmpJ4Hu9XtA8pI2MLZMRNgOrnFwgXTukIhyHhJ2/2wVi6gwfyxzkJMU27jyGY/gLX9NtjwqhL4NaMWG6t/6m";

    private VuforiaLocalizer vuforia;

    private TFObjectDetector tfod;

    //Vision
    PtzControl ptz;
    OpenCvWebcam webcam;
    BarcodeScanner scanner;

    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minResultConfidence = 0.8f;
        tfodParameters.isModelTensorFlow2 = true;
        tfodParameters.inputSize = 320;
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABELS);
    }

    private void initVuforia() {
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;

        vuforia = ClassFactory.getInstance().createVuforia(parameters);
    }

    @Override
    public void init() {
        scanner = new BarcodeScanner(1280,720,new double[]{1,1,1});
       initializeDashboardTelemetry(10);
        robot = new WHSRobotImpl(hardwareMap);
        robot.drivetrain.resetEncoders();
        // add outtake reset
        defineStatesEnabled();

        if(useScanner){
            webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "pycam"));

            webcam.setPipeline(scanner);

            webcam.setMillisecondsPermissionTimeout(2500);

            webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
                @Override
                public void onOpened() {
                    ptz = webcam.getPtzControl();
                    PtzControl.PanTiltHolder ptzHolder= new PtzControl.PanTiltHolder();
                    ptzHolder.pan = 2;
                    ptz.setPanTilt(ptzHolder);
                    ptz.setZoom(2);
                    webcam.startStreaming(1280, 720, OpenCvCameraRotation.UPRIGHT);
                    dashboard.startCameraStream(webcam, webcam.getCurrentPipelineMaxFps());
                    startDriverStationWebcamStream(webcam);
                }

                @Override
                public void onError(int errorCode) {
                    RobotLog.addGlobalWarningMessage("Camera failed to open with error code " + errorCode);
                    telemetry.addLine(String.valueOf(errorCode));
                    requestOpModeStop();
                }
            });
        }

        AutoSwervePositions.instantiatePaths();
        AutoSwervePositions.generateAutoPaths();

        try {
            String[] unformattedData = DataToolsLite.decode("autoConfig.txt");
            STARTING_ALLIANCE = (int) Integer.parseInt(unformattedData[0]);
            STARTING_SIDE = (int) Integer.parseInt(unformattedData[1]);
            useScanner = (boolean) Boolean.parseBoolean(unformattedData[2]);
            stateEnabled[ROTATE_CAROUSEL] = (boolean) Boolean.parseBoolean(unformattedData[3]);
            stateEnabled[PRELOAD] = (boolean) Boolean.parseBoolean(unformattedData[4]);
            stateEnabled[WAREHOUSE] = (boolean) Boolean.parseBoolean(unformattedData[5]);
            numCycles = (int) Integer.parseInt(unformattedData[6]);
            stateEnabled[PARK] = (boolean) Boolean.parseBoolean(unformattedData[7]);
            parkLocation = (int) Integer.parseInt(unformattedData[8]);
            scanner = new BarcodeScanner(1280,720,barcodeArrangements[STARTING_ALLIANCE][STARTING_SIDE]);
            if(useScanner) {

                webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "pycam"));

                webcam.setPipeline(scanner);

                webcam.setMillisecondsPermissionTimeout(2500);

                webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
                    @Override
                    public void onOpened() {
                        ptz = webcam.getPtzControl();
                        PtzControl.PanTiltHolder ptzHolder= new PtzControl.PanTiltHolder();
                        ptzHolder.pan = 2;
                        ptz.setPanTilt(ptzHolder);
                        ptz.setZoom(2);
                        webcam.startStreaming(1280, 720, OpenCvCameraRotation.UPRIGHT);
                        dashboard.startCameraStream(webcam, webcam.getCurrentPipelineMaxFps());
                        startDriverStationWebcamStream(webcam);
                    }

                    @Override
                    public void onError(int errorCode) {
                        RobotLog.addGlobalWarningMessage("Camera failed to open with error code " + errorCode);
                        telemetry.addLine(String.valueOf(errorCode));
                        requestOpModeStop();
                    }

                });
                dashboard.startCameraStream(webcam, webcam.getCurrentPipelineMaxFps());
                startDriverStationWebcamStream(webcam);
            }
        } catch(Exception e){
            telemetry.addData("Data read threw an error","Reverted back to defaults");
            telemetry.addLine(e.getLocalizedMessage());
            telemetry.addLine(e.getStackTrace()[0].toString());
        }

        robot.carousel.setAlliance(STARTING_ALLIANCE);

        // Begin Positions
        startingPositions[RED][BOTTOM] = new Position(-1676.4,774.7);
        startingPositions[RED][TOP] = new Position(-1676.4,-165.1);
        startingPositions[BLUE][BOTTOM] = new Position(-1676.4,-774.7);
        startingPositions[BLUE][TOP] = new Position(-1676.4,165.1);

        startingOffsetPositions[RED][BOTTOM] = new Position(-1476.4,774.7);
        startingOffsetPositions[RED][TOP] = new Position(-1476.4,-165.1);
        startingOffsetPositions[BLUE][BOTTOM] = new Position(-1476.4,-774.7);
        startingOffsetPositions[BLUE][TOP] = new Position(-1476.4,165.1);

        shippingHubApproach[RED] = new Position(-535,1009.6);
        shippingHubApproach[BLUE] = new Position(-535,-1009.6);

        shippingHubPosition[RED] = new Position(-689.6,600);
        shippingHubPosition[BLUE] = new Position(-689.6,-600);

        //sharedShippingHub[RED] = new Position(-152.4, -1200);
        //sharedShippingHub[BLUE] = new Position(-152.4, 1200);

        gapApproach[RED] = new Position(-1820,-100);
        gapApproach[BLUE] = new Position(-1820,100);

        gapCrossPositions[RED] = new Position(-2150,-1500);
        gapCrossPositions[BLUE] = new Position(-2150,1500);

        warehouse[RED] = new Position(-1670,-1550);
        warehouse[BLUE] = new Position(-1670,1550);

        storageUnitPositions[RED] = new Position(-1245,1375.2);
        storageUnitPositions[BLUE] = new Position(-1245,-1375.2);

        carouselApproach[RED] = new Position(-1465.2, 1350.2);
        carouselApproach[BLUE] = new Position(-1465.2,-1350.2);

        carouselPositions[RED] = new Position(-1535.6,1535.2);
        carouselPositions[BLUE] = new Position(-1535.6, -1535.2);

        shippingHubDepositApproach[RED] = new Position(-1269.2,304.8);
        shippingHubDepositApproach[BLUE] = new Position(-1269.2,-304.8);

        shippingHubDeposit[RED] = new Position(-1050.6,315.8);
        shippingHubDeposit[BLUE] = new Position(-1079.6,-304.8);

        // INIT Camera
        /*initVuforia();
        initTfod();
        if (tfod != null) {
            tfod.activate();
            tfod.setZoom(2.5, 16.0/9.0);
        }
*/
        // Camera Barcode Object Locations (Based on Red) TEST FOR THESE
        // SCAN LEVEL 0
        /*barcodeLocation[0][TESTED_LEFT] = 0; // LEFT
        barcodeLocation[0][TESTED_TOP] = 1; // TOP
        barcodeLocation[0][TESTED_RIGHT] = 2; // RIGHT
        barcodeLocation[0][TESTED_BOTTOM] = 3; // BOTTOM

        // SCAN LEVEL 1
        barcodeLocation[1][TESTED_LEFT] = 0; // LEFT
        barcodeLocation[1][TESTED_TOP] = 1; // TOP
        barcodeLocation[1][TESTED_RIGHT] = 2; // RIGHT
        barcodeLocation[1][TESTED_BOTTOM] = 3; // BOTTOM

        // SCAN LEVEL 2
        barcodeLocation[2][TESTED_LEFT] = 0; // LEFT
        barcodeLocation[2][TESTED_TOP] = 1; // TOP
        barcodeLocation[2][TESTED_RIGHT] = 2; // RIGHT
        barcodeLocation[2][TESTED_BOTTOM] = 3; // BOTTOM*/
        Coordinate initial = new Coordinate(startingPositions[STARTING_ALLIANCE][STARTING_SIDE],0);
        robot.setInitialCoordinate(initial);
    }

    @Override
    public void init_loop(){
        if(useScanner){
            result = scanner.getResult();
        }

        if(result != null && useScanner){
            scanLevel = result.ordinal();
        }
    }

    @Override
    public void loop() {
        if(gamepad1.y || gamepad2.y){
            throw new RuntimeException("bad");
        }

        robot.estimateHeading();
        robot.estimatePosition();

        if(gamepad1.a){
            robot.drivetrain.operate(0,0);
        } else {
            switch (state) {
                case INIT:
                    switch (subState) {
                        case 0:
                            /*if ((((barcodeLocation[0][TESTED_LEFT] - ERROR_MARGIN) <= CAMERA_LEFT) && ((barcodeLocation[0][TESTED_LEFT] + ERROR_MARGIN) >= CAMERA_LEFT)) && (((barcodeLocation[0][TESTED_RIGHT] - ERROR_MARGIN) <= CAMERA_RIGHT) && ((barcodeLocation[0][TESTED_RIGHT] + ERROR_MARGIN) >= CAMERA_RIGHT))){
                                scanLevel = 1;
                            } else if ((((barcodeLocation[1][TESTED_LEFT] - ERROR_MARGIN) <= CAMERA_LEFT) && ((barcodeLocation[1][TESTED_LEFT] + ERROR_MARGIN) >= CAMERA_LEFT)) && (((barcodeLocation[1][TESTED_RIGHT] - ERROR_MARGIN) <= CAMERA_RIGHT) && ((barcodeLocation[1][TESTED_RIGHT] + ERROR_MARGIN) >= CAMERA_RIGHT))){
                                scanLevel = 2;
                            } else if ((((barcodeLocation[2][TESTED_LEFT] - ERROR_MARGIN) <= CAMERA_LEFT) && ((barcodeLocation[2][TESTED_LEFT] + ERROR_MARGIN) >= CAMERA_LEFT)) && (((barcodeLocation[2][TESTED_RIGHT] - ERROR_MARGIN) <= CAMERA_RIGHT) && ((barcodeLocation[2][TESTED_RIGHT] + ERROR_MARGIN) >= CAMERA_RIGHT))){
                                scanLevel = 3;
                            } else {
                                scanLevel = 1;
                            }*/
                            subState++;
                            break;
                        case 1:
                            switch (subState) {
                                case 0:
                                    robot.drivetrain.resetEncoders();
                                    //advanceState();
                                    subState++;
                                    break;
                                case 1:
                                    //robot.driveToTarget(startingOffsetPositions[STARTING_ALLIANCE][STARTING_SIDE], false);
                                    //if (!robot.driveToTargetInProgress()) {
                                        //advanceState();
                                    //}
                                    robot.driveToTarget(startingOffsetPositions[STARTING_ALLIANCE][STARTING_SIDE],false);
                                    if(!robot.driveToTargetInProgress()){
                                        advanceState();
                                    }
                                    break;
                            }
                    }
                    break;
                case ROTATE_CAROUSEL:
                    switch (subState) {
                        case 0:
                            robot.driveToTarget(carouselApproach[STARTING_ALLIANCE], true);
                            if (!robot.driveToTargetInProgress()) {
                                subState++;
                            }
                            break;
                        case 1:
                            robot.driveToTarget(carouselPositions[STARTING_ALLIANCE], true);
                            if (!robot.driveToTargetInProgress()) {
                                subState++;
                            }
                            break;
                        case 2:
                            boolean checkBlue = ((STARTING_ALLIANCE) == BLUE) ? true : false;
                            tempHeading = robot.getCoordinate().getHeading();
                            //robot.drivetrain.operate(-0.02,-0.02);
                            robot.carousel.operateAuto();
                            if (!robot.carousel.carouselInProgress()){
                                subState++;
                            }
                            break;
                        case 3:
                            robot.drivetrain.operate(0,0);
                            //robot.setInitialCoordinate(new Coordinate(carouselPositions[STARTING_ALLIANCE],tempHeading));
                            subState++;
                            break;
                        case 4:
                            robot.driveToTarget(carouselApproach[STARTING_ALLIANCE], false);
                            if(!robot.driveToTargetInProgress()){
                                advanceState();
                            }
                            break;
                    }
                    break;
                case PRELOAD:
                    switch (subState) {
                        case 0:
                            if(STARTING_SIDE == BOTTOM){
                                robot.driveToTarget(shippingHubApproach[STARTING_ALLIANCE], true); //check if outtake is on the back
                            } else {
                                robot.driveToTarget(shippingHubDepositApproach[STARTING_ALLIANCE], true);
                            }

                            if (!robot.driveToTargetInProgress()) {
                                subState++;
                            }
                            break;
                        case 1:
                            robot.drivetrain.operate(0,0);
                            robot.outtake.operateWithoutGamepad(scanLevel);
                            if(!robot.outtake.slidingInProgress) {
                                subState++;
                            }
                            break;
                        case 2:
                            if(STARTING_SIDE == BOTTOM) {
                                robot.driveToTarget(shippingHubPosition[STARTING_ALLIANCE], true);
                            } else {
                                robot.driveToTarget(shippingHubDeposit[STARTING_ALLIANCE], true);

                            }

                            if (!robot.driveToTargetInProgress()) {
                                subState++;
                            }
                            break;
                        case 3:
                            robot.drivetrain.operate(0,0);
                            if(robot.outtake.autoDrop()){
                                subState++;
                            }
                            break;
                        case 4:
                            if(STARTING_SIDE == BOTTOM){
                                robot.driveToTarget(shippingHubApproach[STARTING_ALLIANCE], false);

                            } else {
                                robot.driveToTarget(shippingHubDepositApproach[STARTING_ALLIANCE], false);
                            }
                            if(!robot.driveToTargetInProgress()){
                                subState++;
                            }
                            break;
                        case 5:
                            robot.drivetrain.operate(0,0);
                            robot.outtake.operateWithoutGamepad(0);
                            if (!robot.outtake.slidingInProgress && stateEnabled[WAREHOUSE]){
                                advanceState();
                            } else if (!robot.outtake.slidingInProgress) {
                                subState++;
                            }
                            break;
                        case 6:
                            robot.driveToTarget(startingOffsetPositions[STARTING_ALLIANCE][BOTTOM],false);
                            if(!robot.driveToTargetInProgress()){
                                advanceState();
                            }
                            break;
                    }
                    break;
                case WAREHOUSE:
                    //advanceState(); //remove this line of code when we can autocycle
                    if(numCycles < 1){
                        advanceState();
                        break;
                    }
                    switch (subState) {
                        case 0:
                            robot.driveToTarget(gapApproach[STARTING_ALLIANCE], false);
                            if (!robot.driveToTargetInProgress()) {
                                subState++;
                            }
                            break;
                        case 1:
                            robot.driveToTarget(gapCrossPositions[STARTING_ALLIANCE], false);
                            if (!robot.driveToTargetInProgress()) {
                                subState++;
                            }
                            break;
                        case 2:
//                            robot.intake.autoOperate(1.5,false);

                            switch(superSubState){
                                case 0:
                                    robot.driveToTarget(warehouse[STARTING_ALLIANCE], false);
                                    if(!robot.driveToTargetInProgress()){
                                        superSubState++;
                                    }
                                    break;
                                case 1:
                                    robot.intake.autoOperate(1.5,false);
                                    if(!robot.intake.autoIntakeInProgress){
                                        superSubState=0;
                                        subState++;
                                        break;
                                    }
                                    break;
                            }
                            break;
                        case 3:
                            robot.driveToTarget(gapCrossPositions[STARTING_ALLIANCE], true);
                            if (!robot.driveToTargetInProgress()) {
                                subState++;
                            }
                            break;
                        case 4:
                            robot.intake.operate(false,true);
                            robot.driveToTarget(gapApproach[STARTING_ALLIANCE], true);
                            if(!robot.driveToTargetInProgress()){
                                subState++;
                            }
                            break;
                        case 5:
                            robot.intake.disable();
                            robot.driveToTarget(shippingHubDepositApproach[STARTING_ALLIANCE], false);
                            if(!robot.driveToTargetInProgress()){
                                subState++;
                            }
                            break;
                        case 6:
                            //robot.driveToTarget(startingOffsetPositions[STARTING_ALLIANCE][TOP],true);
                            //if(!robot.driveToTargetInProgress()){
                            subState++;
                            //}
                            robot.outtake.operateWithoutGamepad(3);
                            if(!robot.outtake.slidingInProgress){
                                subState++;
                            }
                            break;
                        case 7:
                            robot.driveToTarget(shippingHubDeposit[STARTING_ALLIANCE], true);
                            if(!robot.driveToTargetInProgress()){
                                subState++;
                            }
                            break;
                        case 8:
                            subState++;
                            break;
                        case 9:
                            if(robot.outtake.autoDrop()){
                                subState++;
                            }
                            break;
                        case 10:
                            robot.driveToTarget(shippingHubDepositApproach[STARTING_ALLIANCE], false);
                            if(!robot.driveToTargetInProgress){
                                subState++;
                            }
                            break;
                        case 11:
                            robot.outtake.operateWithoutGamepad(0);
                            if(!robot.outtake.slidingInProgress){
                                subState++;
                            }
                            break;
                        case 12:
                            cycleCounter++;
                            if(cycleCounter >= numCycles){
                                advanceState();
                            } else {
                                subState = 0;
                            }
                            break;

                    }
                    break;
                case PARK:
                    if (parkLocation == 1) {
                        switch(subState){
                            case 0:
                                robot.driveToTarget(storageUnitPositions[STARTING_ALLIANCE], false);
                                if (!robot.driveToTargetInProgress()) {
                                    subState++;
                                    robot.firstRotateLoop = true; //bc robot rotateToTarget sus
                                }
                                break;
                            case 1:
                                //robot.driveToTarget(new Position(-900,1700*((STARTING_ALLIANCE==1) ? -1 : 1)),false);
                                robot.rotateToTarget(90,true);
                                if(!robot.rotateToTargetInProgress()){
                                    advanceState();
                                }
                                break;
                        }
                    } else {
                        switch (subState) {
                            case 0:
                                int compareVarAdjust;
                                compareVarAdjust = (STARTING_ALLIANCE == BLUE) ? 1 : -1;
                                if (robot.getCoordinate().getY() * compareVarAdjust  < 1200) {
                                    subState++;
                                } else {
                                    subState = 5;
                                }
                                break;
                            case 1:
                                robot.driveToTarget(gapApproach[STARTING_ALLIANCE], true);
                                if (!robot.driveToTargetInProgress()) {
                                    subState++;
                                }
                                break;
                            case 2:
                                generalTimer.set(0.25);
                                subState++;
                                break;
                            case 3:
                                subState++;
                                //tempHeading = robot.getCoordinate().getHeading();
                                //robot.drivetrain.operate(new double[]{0.25,-0.25,-0.25,0.25});
                                if(generalTimer.isExpired()){
                                    subState++;
                                }
                                break;
                            case 4:
                                //robot.setInitialCoordinate(new Coordinate(gapApproach[STARTING_ALLIANCE], tempHeading));
                                subState++;
                                break;
                            case 5:
                                robot.driveToTarget(gapCrossPositions[STARTING_ALLIANCE], true);
                                if (!robot.driveToTargetInProgress()) {
                                    subState++;
                                    advanceState();
                                }
                        }
                        break;
                    }
                    break;
                case STOP:
                    robot.drivetrain.operate(0,0);
                    DataToolsLite.encode("heading.txt",robot.getCoordinate().getHeading());
                    //DataToolsLite.encode("teleOpConfig.txt",new Object[]{false, false, false});
                    break;
                default:
                    break;

            }
        }
        dashboardTelemetry.addData("Camera result", result);
        telemetry.addData("Current state: ",stateNames[state]);
        telemetry.addData("Substate: ", subState);
        telemetry.addData("Supersubstate: ", superSubState);
        telemetry.addData("Starting Side",(STARTING_SIDE==BOTTOM) ? "BOTTOM" : "TOP");
        telemetry.addData("Starting Alliance",(STARTING_ALLIANCE == 0) ? "RED" : "BLUE");
        telemetry.addData("Drop level",scanLevel);
        telemetry.addData("Estimated Position",String.format("%s,%s",robot.getCoordinate().getX(),robot.getCoordinate().getY()));
        telemetry.addData("Heading",robot.getCoordinate().getHeading());
        telemetry.addLine();
        telemetry.addData("Drive to target:", robot.driveToTargetInProgress());
        telemetry.addData("Rotate to target:", robot.rotateToTargetInProgress());
        telemetry.addData("DTT error",robot.distanceToTargetDebug);
        telemetry.addData("RTT error",robot.angleToTargetDebug);
        //telemetry.addData("Outtake extension: ", robot.robotOuttake.slidingInProgress);
        //telemetry.addData("Intaking item from warehouse: ", robot.robotIntake.intakeAutoDone);

        //lag output
        telemetry.addData("Current processing latency: ", (Math.round(System.nanoTime()-lastRecordedTime)/1E6) + "ms");
        lastRecordedTime = System.nanoTime();
    }
}
