package org.whitneyrobotics.ftc.teamcode.visionImpl;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.apriltag.AprilTagDetection;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.whitneyrobotics.ftc.teamcode.GamepadEx.GamepadEx;
import org.whitneyrobotics.ftc.teamcode.lib.util.SimpleTimer;
import org.whitneyrobotics.ftc.teamcode.robotImpl.WHSRobotImpl;

import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.N)
@Autonomous(name="PowerPlay Meet2 Vision Auto", group="A", preselectTeleOp = "PowerPlay TeleOp")
public class Meet2VisionAuto extends OpMode {
    OpenCvCamera camera;
    AprilTagDetectionPipeline aprilTagDetectionPipeline;

    static final double FEET_PER_METER = 3.28084;

    // Lens intrinsics
    // UNITS ARE PIXELS
    // NOTE: this calibration is for the C920 webcam at 800x448.
    // You will need to do your own calibration for other configurations!
    double fx = 578.272;
    double fy = 578.272;
    double cx = 402.145;
    double cy = 221.506;

    // Tag ID 0,1,2 from the 36h11 family
    // these will represent tag types for different locations
    final int LEFT = 1;
    final int MIDDLE = 2;
    final int RIGHT = 3;
    // UNITS ARE METERS
    double tagsize = 0.166;

    // No tag has been sensed
    private AprilTagDetection tagOfInterest = null;
    private WHSRobotImpl robot;

    private int cameraMonitorViewId;
    private ArrayList<AprilTagDetection> currentDetections;

    private SimpleTimer timerOne = new SimpleTimer();
    private SimpleTimer timerTwo = new SimpleTimer();
    private SimpleTimer timerThree = new SimpleTimer();
    private SimpleTimer timerFour = new SimpleTimer();

    private boolean firstIteration = true;

    void tagToTelemetry (AprilTagDetection detection) {
        telemetry.addLine(String.format("\nDetected tag ID=%d", detection.id));
        telemetry.addLine(String.format("Translation X: %.2f feet", detection.pose.x*FEET_PER_METER));
        telemetry.addLine(String.format("Translation Y: %.2f feet", detection.pose.y*FEET_PER_METER));
        telemetry.addLine(String.format("Translation Z: %.2f feet", detection.pose.z*FEET_PER_METER));
        telemetry.addLine(String.format("Rotation Yaw: %.2f degrees", Math.toDegrees(detection.pose.yaw)));
        telemetry.addLine(String.format("Rotation Pitch: %.2f degrees", Math.toDegrees(detection.pose.pitch)));
        telemetry.addLine(String.format("Rotation Roll: %.2f degrees", Math.toDegrees(detection.pose.roll)));
    }

//    @Override
//    public void runOpMode() {
//        /*robot = new WHSRobotImpl(hardwareMap, new GamepadEx(gamepad2));
//        // Find camera, and make a pipeline
//        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
//        Log.println(Log.DEBUG, "Test", String.valueOf(cameraMonitorViewId));
//        camera = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
//        aprilTagDetectionPipeline = new AprilTagDetectionPipeline(tagsize, fx, fy, cx, cy);
//
//        // Set pipeline to the AprilTag detection
//        camera.setPipeline(aprilTagDetectionPipeline);
//        // Start the camera
//        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
//        {
//            @Override
//            public void onOpened()
//            {
//                camera.startStreaming(800,448, OpenCvCameraRotation.UPRIGHT);
//            }
//
//            @Override
//            public void onError(int errorCode)
//            {
//                telemetry.addData("The Camera has errored with error code", errorCode);
//            }
//        });
//        telemetry.setMsTransmissionInterval(50);*/
//
//        /*
//         * The INIT-loop:
//         * This REPLACES waitForStart!
//         */
//        while (!isStarted() && !isStopRequested())
//        {
//            Log.println(Log.DEBUG, "Test", String.valueOf(cameraMonitorViewId));
//            // get a list of all detected AprilTags
//            ArrayList<AprilTagDetection> currentDetections = aprilTagDetectionPipeline.getLatestDetections();
//
//            // loop through tags if there are any, and find which tag of interest
//            if(currentDetections.size() != 0)
//            {
//                boolean tagFound = false;
//
//                for(AprilTagDetection tag : currentDetections)
//                {
//                    if(tag.id == LEFT || tag.id == MIDDLE || tag.id == RIGHT )
//                    {
//                        tagOfInterest = tag;
//                        tagFound = true;
//                        break;
//                    }
//                }
//
//                // Notify the drivers about the status of AprilTag Detection
//                if(tagFound)
//                {
//                    telemetry.addLine("Tag of interest is in sight!\n\nLocation data:");
//                    tagToTelemetry(tagOfInterest);
//                }
//                else
//                {
//                    telemetry.addLine("Don't see tag of interest :(");
//
//                    if(tagOfInterest == null)
//                    {
//                        telemetry.addLine("(The tag has never been seen)");
//                    }
//                    else
//                    {
//                        telemetry.addLine("\nBut we HAVE seen the tag before; last seen at:");
//                        tagToTelemetry(tagOfInterest);
//                    }
//                }
//
//            }
//            else
//            {
//                telemetry.addLine("Don't see tag of interest :(");
//
//                if(tagOfInterest == null)
//                {
//                    telemetry.addLine("(The tag has never been seen)");
//                }
//                else
//                {
//                    telemetry.addLine("\nBut we HAVE seen the tag before; last seen at:");
//                    tagToTelemetry(tagOfInterest);
//                }
//
//            }
//
//            // sleep to allow for another picture to be taken
//            telemetry.update();
//            sleep(20);
//        }
//
//        /*
//         * The START command just came in: now work off the latest snapshot acquired
//         * during the init loop.
//         */
//
//        /* Update the telemetry */
//        if(tagOfInterest != null)
//        {
//            telemetry.addLine("Tag snapshot:\n");
//            tagToTelemetry(tagOfInterest);
//            telemetry.update();
//        }
//        else
//        {
//            telemetry.addLine("No tag snapshot available, it was never sighted during the init loop :(");
//            telemetry.update();
//        }
//
//        /* Actually do something useful */
//        if (tagOfInterest == null || tagOfInterest.id == LEFT) {
//            // do something to move the robot
//        }
//        else if (tagOfInterest.id == MIDDLE) {
//            // do something
//        }
//        else if (tagOfInterest.id == RIGHT) {
//            // do something
//        }
///*        *//* You wouldn't have this in your autonomous, this is just to prevent the sample from ending *//*
//        while (opModeIsActive()) {sleep(20);}*/
//    }
//
//    void tagToTelemetry(AprilTagDetection detection)
//    {
//        telemetry.addLine(String.format("\nDetected tag ID=%d", detection.id));
//        telemetry.addLine(String.format("Translation X: %.2f feet", detection.pose.x*FEET_PER_METER));
//        telemetry.addLine(String.format("Translation Y: %.2f feet", detection.pose.y*FEET_PER_METER));
//        telemetry.addLine(String.format("Translation Z: %.2f feet", detection.pose.z*FEET_PER_METER));
//        telemetry.addLine(String.format("Rotation Yaw: %.2f degrees", Math.toDegrees(detection.pose.yaw)));
//        telemetry.addLine(String.format("Rotation Pitch: %.2f degrees", Math.toDegrees(detection.pose.pitch)));
//        telemetry.addLine(String.format("Rotation Roll: %.2f degrees", Math.toDegrees(detection.pose.roll)));
//    }

    @Override
    public void init() {
        robot = new WHSRobotImpl(hardwareMap);
        // Find camera, and make a pipeline
        cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        Log.println(Log.DEBUG, "Test", String.valueOf(cameraMonitorViewId));
        camera = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
        aprilTagDetectionPipeline = new AprilTagDetectionPipeline(tagsize, fx, fy, cx, cy);

        // Set pipeline to the AprilTag detection
        camera.setPipeline(aprilTagDetectionPipeline);
        // Start the camera
        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
        {
            @Override
            public void onOpened()
            {
                camera.startStreaming(800,448, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode)
            {
                telemetry.addData("The Camera has errored with error code", errorCode);
            }
        });
        telemetry.setMsTransmissionInterval(50);
    }

    @Override
    public void init_loop(){
        Log.println(Log.DEBUG, "Test", String.valueOf(cameraMonitorViewId));
        // get a list of all detected AprilTags
        currentDetections = aprilTagDetectionPipeline.getLatestDetections();

        // loop through tags if there are any, and find which tag of interest
        if(currentDetections.size() != 0)
        {
            boolean tagFound = false;

            for(AprilTagDetection tag : currentDetections)
            {
                if(tag.id == LEFT || tag.id == MIDDLE || tag.id == RIGHT )
                {
                    tagOfInterest = tag;
                    tagFound = true;
                    break;
                }
            }

            // Notify the drivers about the status of AprilTag Detection
            if(tagFound)
            {
                telemetry.addLine("Tag of interest is in sight!\n\nLocation data:");
                tagToTelemetry(tagOfInterest);
            }
            else
            {
                telemetry.addLine("Don't see tag of interest :(");

                if(tagOfInterest == null)
                {
                    telemetry.addLine("(The tag has never been seen)");
                }
                else
                {
                    telemetry.addLine("\nBut we HAVE seen the tag before; last seen at:");
                    tagToTelemetry(tagOfInterest);
                }
            }

        }
        else
        {
            telemetry.addLine("Don't see tag of interest :(");

            if(tagOfInterest == null)
            {
                telemetry.addLine("(The tag has never been seen)");
            }
            else
            {
                telemetry.addLine("\nBut we HAVE seen the tag before; last seen at:");
                tagToTelemetry(tagOfInterest);
            }

        }

        // sleep to allow for another picture to be taken
        telemetry.update();
//        sleep(20);
    }

    /*
     * The START command just came in: now work off the latest snapshot acquired
     * during the init loop.
     */

    /* Update the telemetry */
//        if(tagOfInterest != null)
//    {
//        telemetry.addLine("Tag snapshot:\n");
//        tagToTelemetry(tagOfInterest);
//        telemetry.update();
//    }
//        else
//    {
//        telemetry.addLine("No tag snapshot available, it was never sighted during the init loop :(");
//        telemetry.update();
//    }
//
//    /* Actually do something useful */
//        if (tagOfInterest == null || tagOfInterest.id == LEFT) {
//        // do something to move the robot
//    }
//        else if (tagOfInterest.id == MIDDLE) {
//        // do something
//    }
//        else if (tagOfInterest.id == RIGHT) {
//        // do something
//    }


    @Override
    public void loop() {

        if(firstIteration && tagOfInterest != null)
        {
            telemetry.addLine("Tag snapshot:\n");
            tagToTelemetry(tagOfInterest);
            telemetry.update();
//            timerOne.set(1500);
//            timerTwo.set(3000);
            timerThree.set(1.5);
            timerFour.set(2.7);
//            robot.drivetrain.operateByCommand(0, 0.2, 0);
            firstIteration = false;

        }
        else
        {
//            telemetry.addLine("Tag Snapshot: ");
            telemetry.addData("Tag snapshot: ", String.valueOf(tagOfInterest.id));
            telemetry.update();
        }

        if (!timerThree.isExpired()) {
            robot.drivetrain.operateByCommand(0, 0.5, 0);
        } else if (!timerFour.isExpired() && timerThree.isExpired()) {
            robot.drivetrain.operateByCommand(0.5 * (tagOfInterest.id-2),0,0);
        } else if (timerThree.isExpired() && timerFour.isExpired()) {
            robot.drivetrain.operateByCommand(0,0,0);
        }

//        if (timerFour.isExpired()) {
//            robot.drivetrain.operateByCommand(0, 0, 0);
//        } else if (timerThree.isExpired()) {
//            Log.println(Log.DEBUG, "TAG ID", String.valueOf(tagOfInterest.id));
//            robot.drivetrain.operateByCommand(0.2 * (tagOfInterest.id-2),0,0);
//        }
//        if (!timerThree.isExpired()) {
//            robot.drivetrain.operateByCommand(0.2 * (tagOfInterest.id-2),0,0);
//        } else if (timerThree.isExpired() && !timerFour.isExpired()) {
//            robot.drivetrain.operateByCommand(0.2 * (tagOfInterest.id-2),0,0);
//        } else if (timerThree.isExpired() && timerFour.isExpired()) {
//            robot.drivetrain.operateByCommand(0, 0, 0);
//        }

//        if (!timerThree.isExpired()){
//            robot.drivetrain.operateByCommand(0, 0.2, 0);
//        } else if (timerThree.isExpired() && !timerFour.isExpired()){
//            robot.drivetrain.operateByCommand(0.2 * (tagOfInterest.id-2),0,0);
//        } else if (timerThree.isExpired() && timerFour.isExpired()){
//            robot.drivetrain.operateByCommand(0,0,0);
//        }

        /* Actually do something useful */
//        if (tagOfInterest == null || tagOfInterest.id == LEFT) {
//            if (timerOne.isExpired()){
//
//            }
//        }
//        else if (tagOfInterest.id == MIDDLE) {
//            // do something
//        }
//        else if (tagOfInterest.id == RIGHT) {
//            // do something
//        }
    }
}
