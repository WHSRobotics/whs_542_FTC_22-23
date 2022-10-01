package org.whitneyrobotics.ftc.teamcode.autoop;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.whitneyrobotics.ftc.teamcode.lib.geometry.Coordinate;
import org.whitneyrobotics.ftc.teamcode.lib.geometry.Position;
import org.whitneyrobotics.ftc.teamcode.lib.util.DataToolsLite;
import org.whitneyrobotics.ftc.teamcode.lib.util.SimpleTimer;
import org.whitneyrobotics.ftc.teamcode.subsys.WHSRobotImpl;

@Autonomous(name="WHS Freight Frenzy Auto TEST")
public class WHSAutoTest extends OpMode {

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

    int STARTING_ALLIANCE = RED;
    int STARTING_SIDE = BOTTOM;

    private int scanLevel = 1;
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

    private SimpleTimer generalTimer = new SimpleTimer();

    static final int INIT = 0;
    static final int ROTATE_CAROUSEL = 1;
    static final int SHIPPING_HUB = 2;
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
        stateEnabled[SHIPPING_HUB] = true;
        stateEnabled[WAREHOUSE] = false;
        stateEnabled[PARK] = true;
        stateEnabled[STOP] = true;
    }

    private int parkLocation = 0;

    public String[] stateNames = {"Init", "Rotate Carousel", "Shipping Hub", "Warehouse", "Park", "Stop"};

    public float[][] barcodeLocation;

    public void advanceState(){
        if (stateEnabled[state + 1]){
            subState = 0;
            superSubState = 0;
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
        robot = new WHSRobotImpl(hardwareMap);
        robot.drivetrain.resetEncoders();
        // add outtake reset
        defineStatesEnabled();

        try {
            String[] unformattedData = DataToolsLite.decode("autoConfig.txt");
            //Object[] formattedData = DataToolsLite.convertBackToObjects(new DataToolsLite.ObjectTypes[]{DataToolsLite.ObjectTypes.INT, DataToolsLite.ObjectTypes.INT, DataToolsLite.ObjectTypes.BOOLEAN, DataToolsLite.ObjectTypes.BOOLEAN,
                    //DataToolsLite.ObjectTypes.BOOLEAN, DataToolsLite.ObjectTypes.BOOLEAN, DataToolsLite.ObjectTypes.INT, DataToolsLite.ObjectTypes.BOOLEAN, DataToolsLite.ObjectTypes.INT, DataToolsLite.ObjectTypes.BOOLEAN}, unformattedData);
            STARTING_ALLIANCE = (int) Integer.parseInt(unformattedData[0]);
            STARTING_SIDE = (int) Integer.parseInt(unformattedData[1]);
            stateEnabled[ROTATE_CAROUSEL] = (boolean) Boolean.parseBoolean(unformattedData[3]);
            stateEnabled[SHIPPING_HUB] = (boolean) Boolean.parseBoolean(unformattedData[4]);
            stateEnabled[WAREHOUSE] = (boolean) Boolean.parseBoolean(unformattedData[5]);
            numCycles = (int) Integer.parseInt(unformattedData[6]);
            stateEnabled[PARK] = (boolean) Boolean.parseBoolean(unformattedData[7]);
            parkLocation = (int) Integer.parseInt(unformattedData[8]);
        } catch(Exception e){
            telemetry.addData("Data read sus","Reverted back to defaults");
            //delete this
            //throw new RuntimeException("sussy bussy");
        }

        // figure out actual values for this
        startingPositions[RED][BOTTOM] = new Position(-1647.6,900);
        startingPositions[RED][TOP] = new Position(-1647.6,-300);
        startingPositions[BLUE][BOTTOM] = new Position(-1647.6,-900);
        startingPositions[BLUE][TOP] = new Position(-1647.6,300);

        startingOffsetPositions[RED][BOTTOM] = new Position(-1447.6,900);
        startingOffsetPositions[RED][TOP] = new Position(-1447.6,-300);
        startingOffsetPositions[BLUE][BOTTOM] = new Position(-1447.6,-900);
        startingOffsetPositions[BLUE][TOP] = new Position(-1447.6,300);

        shippingHubApproach[RED] = new Position(-1100,800);
        shippingHubApproach[BLUE] = new Position(-1100,-800);

        shippingHubPosition[RED] = new Position(-857,547);
        shippingHubPosition[BLUE] = new Position(-857,-547);

        //sharedShippingHub[RED] = new Position(-152.4, -1200);
        //sharedShippingHub[BLUE] = new Position(-152.4, 1200);

        gapApproach[RED] = new Position(-1647,-450);
        gapApproach[BLUE] = new Position(-1647,450);

        gapCrossPositions[RED] = new Position(-1647,-900);
        gapCrossPositions[BLUE] = new Position(-1647,900);

        warehouse[RED] = new Position(-1500,-1123);
        warehouse[BLUE] = new Position(-1500,1123);

        storageUnitPositions[RED] = new Position(-900,1600);
        storageUnitPositions[BLUE] = new Position(-900,-1600);

        carouselApproach[RED] = new Position(-1500, 1350);
        carouselApproach[BLUE] = new Position(-1450,-1350);

        carouselPositions[RED] = new Position(-1512,1612);
        carouselPositions[BLUE] = new Position(-1512, -1612);

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

/*@Override
    public void init_loop() {
        if (tfod != null) {
            List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
            if (updatedRecognitions != null) {
                telemetry.addData("# Object Detected", updatedRecognitions.size());
                int i = 0;
                for (Recognition recognition : updatedRecognitions) {
                    if (recognition.getLabel() == "Cube" || recognition.getLabel() == "Duck"){
                        CAMERA_BOTTOM = recognition.getBottom();
                        CAMERA_LEFT = recognition.getLeft();
                        CAMERA_TOP = recognition.getTop();
                        CAMERA_RIGHT = recognition.getRight();
                    }
                    i++;
                }
                telemetry.update();
            }
        }
    }*/

    @Override
    public void loop() {
        if(gamepad1.y){
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
                                robot.driveToTarget(startingOffsetPositions[STARTING_ALLIANCE][STARTING_SIDE], false);
                                if (!robot.driveToTargetInProgress()) {
                                    subState++;
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
                        //robot.drivetrain.operate(-0.01,-0.01);
                        robot.carousel.operateAuto();
                        if (!robot.carousel.carouselInProgress()){
                        subState++;
                        }
                        break;
                    case 3:
                        generalTimer.set(0.2);
                        subState++;
                        break;
                    case 4:
                        robot.drivetrain.operate(0.25,0.25);
                        if(generalTimer.isExpired()){
                           subState++;
                        }
                        break;
                    case 5:
                        robot.driveToTarget(carouselApproach[STARTING_ALLIANCE], false);
                        if(!robot.driveToTargetInProgress()){
                            advanceState();
                        }
                }
                break;
            case SHIPPING_HUB:
                switch (subState) {
                    case 0:
                        robot.driveToTarget(shippingHubApproach[STARTING_ALLIANCE], true); //check if outtake is on the back
                        if (!robot.driveToTargetInProgress()) {
                            subState++;
                        }
                        break;
                    case 1:
                        robot.driveToTarget(shippingHubPosition[STARTING_ALLIANCE], true);
                        if (!robot.driveToTargetInProgress()) {
                            subState++;
                        }
                        break;
                    case 2:
                        robot.outtake.operateWithoutGamepad(scanLevel);
                        if(!robot.outtake.slidingInProgress) {
                            subState++;
                        }
                        break;
                    case 3:
                        if(robot.outtake.autoDrop()){
                            subState++;
                        }
                        break;
                    case 4:
                        robot.outtake.operateWithoutGamepad(0);
                        if (!robot.outtake.slidingInProgress){
                        subState++;
                        }
                        break;
                    case 5:
                        robot.driveToTarget(shippingHubApproach[STARTING_ALLIANCE], false);
                        advanceState();
                        break;
                }
                break;
            case WAREHOUSE:
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
                        robot.intake.autoOperate(1.5,false);
                        switch(superSubState){
                            case 0:
                                robot.driveToTarget(warehouse[STARTING_ALLIANCE], false);
                                if(!robot.driveToTargetInProgress()){
                                    superSubState++;
                                }
                                break;
                            case 1:
                                if(!robot.intake.autoIntakeInProgress){
                                    subState++;
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
                        robot.driveToTarget(gapApproach[STARTING_ALLIANCE], true);
                        if(!robot.driveToTargetInProgress()){
                            subState++;
                        }
                    case 5:
                        robot.driveToTarget(shippingHubApproach[STARTING_ALLIANCE], false);
                        if(!robot.driveToTargetInProgress()){
                            subState++;
                        }
                        break;
                    case 6:
                        robot.driveToTarget(shippingHubPosition[STARTING_ALLIANCE], true);
                        if(!robot.driveToTargetInProgress()){
                            subState++;
                        }
                        break;
                    case 7:
                        robot.outtake.operateWithoutGamepad(0);
                        if(!robot.outtake.slidingInProgress){
                            subState++;
                        }
                        break;
                    case 8:
                        if(robot.outtake.autoDrop()){
                            subState++;
                        }
                        break;
                    case 9:
                        robot.outtake.operateWithoutGamepad(0);
                        if(!robot.outtake.slidingInProgress){
                            subState++;
                        }
                        break;
                    case 10:
                        robot.driveToTarget(shippingHubApproach[STARTING_ALLIANCE], false);
                        break;
                    case 11:
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
                    robot.driveToTarget(storageUnitPositions[STARTING_ALLIANCE], false);
                    if (!robot.driveToTargetInProgress()) {
                        advanceState();
                    }
                } else {
                    switch (subState) {
                        case 0:
                            int compareVarAdjust;
                            compareVarAdjust = (STARTING_ALLIANCE == BLUE) ? -1 : 1;
                            if (robot.getCoordinate().getY() * compareVarAdjust < 600) {
                                subState = 2;
                            } else {
                                subState++;
                            }
                            break;
                        case 1:
                            robot.driveToTarget(gapApproach[STARTING_ALLIANCE], false);
                            if (!robot.driveToTargetInProgress()) {
                                subState++;
                            }
                            break;
                        case 2:
                            robot.driveToTarget(gapCrossPositions[STARTING_ALLIANCE], false);
                            if (!robot.driveToTargetInProgress()) {
                                subState++;
                                advanceState();
                            }
                    }
                    break;
                }
                break;
            case STOP:
                DataToolsLite.encode("heading.txt",robot.getCoordinate().getHeading());
                break;
            default:
                break;

        }
    }
        telemetry.addData("Current state: ",stateNames[state]);
        telemetry.addData("Substate: ", subState);
        telemetry.addData("Starting Side",(STARTING_SIDE==BOTTOM) ? "BOTTOM" : "TOP");
        telemetry.addData("Starting Alliance",(STARTING_ALLIANCE == 0) ? "RED" : "BLUE");
        telemetry.addData("Estimated Position",String.format("%s,%s",robot.getCoordinate().getX(),robot.getCoordinate().getY()));
        telemetry.addData("Drive to target:", robot.driveToTargetInProgress());
        telemetry.addData("Rotate to target:", robot.rotateToTargetInProgress());
        telemetry.addData("DTT error",robot.distanceToTargetDebug);
        //telemetry.addData("Outtake extension: ", robot.robotOuttake.slidingInProgress);
        //telemetry.addData("Intaking item from warehouse: ", robot.robotIntake.intakeAutoDone);

        //lag output
        telemetry.addData("Current processing latency: ", (Math.round(System.nanoTime()-lastRecordedTime)/1E6) + "ms");
        lastRecordedTime = System.nanoTime();
    }
}
