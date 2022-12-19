package org.whitneyrobotics.ftc.teamcode.autoop;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.apriltag.AprilTagDetection;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.whitneyrobotics.ftc.teamcode.framework.opmodes.OpModeEx;
import org.whitneyrobotics.ftc.teamcode.lib.file.RobotDataUtil;
import org.whitneyrobotics.ftc.teamcode.lib.file.WHSRobotData;
import org.whitneyrobotics.ftc.teamcode.robotImpl.WHSRobotImpl;
import org.whitneyrobotics.ftc.teamcode.visionImpl.AprilTagDetectionPipeline;
import org.whitneyrobotics.ftc.teamcode.visionImpl.AprilTagScanner2022;

import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.N)
@Autonomous(name="PowerPlay Auto BLUE", group="A", preselectTeleOp = "PowerPlay TeleOp")
public class PowerPlayAutoBLUE extends OpModeEx{
    String state = "Junction Placement";
    WHSRobotImpl robot;
    AprilTagScanner2022 aprilTagScanner = new AprilTagScanner2022(hardwareMap);

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
    AprilTagDetection tagOfInterest = null;
    int cameraMonitorViewId;
    @Override
    public void initInternal() {
        // auto step/timing management
        addTemporalCallback(resolve -> {
            this.state = "Moving to InitPosition";
        }, 1500);
        addTemporalCallback(resolve -> {
            this.state = "Moving to parking position";
        }, 3000);
        addTemporalCallback(resolve -> {
            this.state = "Parking";
        }, 6500);
        addTemporalCallback(resolve -> {
            this.state = "Idle";
        }, 9500);

        addTemporalCallback(e -> {
            RobotDataUtil.save(WHSRobotData.class,true);
        },29000);
        addTemporalCallback(e -> requestOpModeStop(),31000);
        robot = new WHSRobotImpl(hardwareMap, gamepad2);

    }

    @Override
    public void init_loop(){
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
//        if (aprilTagScanner.scan() != -1) {
//            try {
//                aprilTagScanner.latestTagToTelemetry();
//            } catch (Exception e) {
//                telemetry.addData("Tag: ", String.valueOf(aprilTagScanner.getLatestTag()));
//            }
//
//            return; //loop until scan
//        }
    }

    @Override
    protected void loopInternal() {

        Log.println(Log.DEBUG, "Test", String.valueOf(cameraMonitorViewId));
        // get a list of all detected AprilTags
        ArrayList<AprilTagDetection> currentDetections = aprilTagDetectionPipeline.getLatestDetections();

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
        /*
         * The START command just came in: now work off the latest snapshot acquired
         * during the init loop.
         */

        /* Update the telemetry */
        if(tagOfInterest != null)
        {
            telemetry.addLine("Tag snapshot:\n");
            tagToTelemetry(tagOfInterest);
            telemetry.update();
        }
        else
        {
            telemetry.addLine("No tag snapshot available, it was never sighted during the init loop :(");
            telemetry.update();
        }

        /* Actually do something useful */
        if (tagOfInterest == null || tagOfInterest.id == LEFT) {
            // do something to move the robot
        }
        else if (tagOfInterest.id == MIDDLE) {
            // do something
        }
        else if (tagOfInterest.id == RIGHT) {
            // do something
        }

        switch (state) {
            case "Junction Placement":
                robot.drivetrain.operateByCommand(0.4, 0, 0);
            case "Moving to InitPosition":
                robot.drivetrain.operateByCommand(-0.4, 0, 0);
            case "Moving to Parking Position":
                robot.drivetrain.operateByCommand(0, 0.2, 0);
            case "Parking" :
                robot.drivetrain.operateByCommand(0.2 * (aprilTagScanner.getLatestTag()-2),0,0);
        }

        WHSRobotData.heading = robot.imu.getHeading();

    }


    void tagToTelemetry(AprilTagDetection detection)
    {
        telemetry.addLine(String.format("\nDetected tag ID=%d", detection.id));
        telemetry.addLine(String.format("Translation X: %.2f feet", detection.pose.x*FEET_PER_METER));
        telemetry.addLine(String.format("Translation Y: %.2f feet", detection.pose.y*FEET_PER_METER));
        telemetry.addLine(String.format("Translation Z: %.2f feet", detection.pose.z*FEET_PER_METER));
        telemetry.addLine(String.format("Rotation Yaw: %.2f degrees", Math.toDegrees(detection.pose.yaw)));
        telemetry.addLine(String.format("Rotation Pitch: %.2f degrees", Math.toDegrees(detection.pose.pitch)));
        telemetry.addLine(String.format("Rotation Roll: %.2f degrees", Math.toDegrees(detection.pose.roll)));
    }
}
