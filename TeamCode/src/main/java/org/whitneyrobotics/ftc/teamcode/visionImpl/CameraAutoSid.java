package org.whitneyrobotics.ftc.teamcode.visionImpl;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.PtzControl;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;
import org.whitneyrobotics.ftc.teamcode.framework.DashboardOpMode;

//@TeleOp(name="Vision Test", group="New Tests")
public class CameraAutoSid extends DashboardOpMode {
    PtzControl ptz;
    OpenCvWebcam webcam;
    BarcodeScanner scanner = new BarcodeScanner(1280,720,new double[] {1,1,1});
    double scanLevel = 2;

    @Override
    public void init() {
        initializeDashboardTelemetry(50);
        //int cameraMoniterViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMoniterViewId");
        webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "pycam"));

        webcam.setPipeline(scanner);

        webcam.setMillisecondsPermissionTimeout(2500);

        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                dashboard.startCameraStream(webcam, webcam.getCurrentPipelineMaxFps());
                ptz = webcam.getPtzControl();
                PtzControl.PanTiltHolder ptzHolder= new PtzControl.PanTiltHolder();
                ptzHolder.pan = 2;
                ptz.setPanTilt(ptzHolder);
                ptz.setZoom(2);
                webcam.startStreaming(1280, 720, OpenCvCameraRotation.UPRIGHT);
                startDriverStationWebcamStream(webcam);
            }

            @Override
            public void onError(int errorCode) {
                telemetry.addLine(String.valueOf(errorCode));
            }
        });
    }

    @Override
    public void init_loop(){
        BarcodeScanner.Barcode result = scanner.getResult();
        if(result != null){
            scanLevel = result.ordinal();
        }
    }

    @Override
    public void loop() {

        telemetry.addData("fps",webcam.getCurrentPipelineMaxFps());
        telemetry.addData("scanned in init",scanLevel);
        telemetry.addData("detecting",(scanner.getResult()!=null?scanner.getResult().name():"none"));

    }
}