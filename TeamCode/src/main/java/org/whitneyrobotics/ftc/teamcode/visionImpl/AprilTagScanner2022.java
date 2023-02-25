package org.whitneyrobotics.ftc.teamcode.visionImpl;


import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.apriltag.AprilTagDetection;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;
import org.whitneyrobotics.ftc.teamcode.BetterTelemetry.BetterTelemetry;


import java.util.ArrayList;
import java.util.function.Consumer;


public class AprilTagScanner2022 {
//    OpenCvCamera camera;
    public OpenCvWebcam camera;
    private AprilTagDetectionPipeline aprilTagDetectionPipeline;
    static final double FEET_PER_METER = 3.28084;
    BetterTelemetry telemetry;

    public AprilTagDetectionPipeline getPipeline(){
        return aprilTagDetectionPipeline;
    }

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
    int lastDetection = -1;

    private AprilTagDetection latestTag = null;
    public int getLatestTag() {return latestTag.id;}

    private Consumer<Integer> onDetectChangeCallback = i -> {};

    public void onDetectChange(Consumer<Integer> callback){
        this.onDetectChangeCallback = callback;
    }

    int cameraMonitorViewId = 0;
    public AprilTagScanner2022(HardwareMap hardwareMap, BetterTelemetry telemetry) {
        this.telemetry = telemetry;
        // Find camera, and make a pipeline
        try {
            cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        } catch (Exception e) {

        }
        camera = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), 2131230820);
//        camera = hardwareMap.get()
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
    }

    public int scan() {
        AprilTagDetection tagOfInterest = null;
        ArrayList<AprilTagDetection> currentDetections = aprilTagDetectionPipeline.getLatestDetections();

        if (currentDetections.size() != 0) {
            boolean tagFound = false;

            for (AprilTagDetection tag : currentDetections) {
                if (tag.id == LEFT || tag.id == MIDDLE || tag.id == RIGHT) {
                    tagOfInterest = tag;
                    tagFound = true;
                    break;
                }
            }
            if (tagFound) {
                latestTag = tagOfInterest;
                if(lastDetection != tagOfInterest.id) onDetectChangeCallback.accept(tagOfInterest.id);
                lastDetection = tagOfInterest.id;
                return lastDetection;
            } else return -1;
        } else return -1;
    }

    public void latestTagToTelemetry()
    {
        telemetry.addLine(String.format("\nDetected tag ID=%d", latestTag.id));
        telemetry.addLine(String.format("Translation X: %.2f feet", latestTag.pose.x*FEET_PER_METER));
        telemetry.addLine(String.format("Translation Y: %.2f feet", latestTag.pose.y*FEET_PER_METER));
        telemetry.addLine(String.format("Translation Z: %.2f feet", latestTag.pose.z*FEET_PER_METER));
        telemetry.addLine(String.format("Rotation Yaw: %.2f degrees", Math.toDegrees(latestTag.pose.yaw)));
        telemetry.addLine(String.format("Rotation Pitch: %.2f degrees", Math.toDegrees(latestTag.pose.pitch)));
        telemetry.addLine(String.format("Rotation Roll: %.2f degrees", Math.toDegrees(latestTag.pose.roll)));
    }

}
