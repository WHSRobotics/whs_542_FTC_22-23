package org.whitneyrobotics.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.whitneyrobotics.ftc.teamcode.subsys.WHSRobotImplOld;

import java.util.List;

@TeleOp(name = "Shooting Test", group = "Tests")
@Disabled
public class ShootingTest extends OpMode {
    WHSRobotImplOld robot;
    String stackSize;
    private static final String TFOD_MODEL_ASSET = "UltimateGoal.tflite";
    private static final String LABEL_FIRST_ELEMENT = "Four";
    private static final String LABEL_SECOND_ELEMENT = "One";

    private static final String VUFORIA_KEY =
            "AWYX8QX/////AAABmQ8w8KJJuEsNlO9fxNmHDg1BoH/L5lzniFIDqLd+XlCF9gXWlYeddle27IIm9DH8mtLY2CLX9LW3uAzD8IH5Stmf+NoLjfm+m4jnj7KmR+v+xGuUEgP3Aj8sez5uhtsKarKiv94URMVnf39sjHW3xhiUBI30M762Ee6bEy69ZHQSOHLNxMwm9lnETo0O13vhmtZvI44HtEjIvXbW71p/+jdZw/33i6q//G4O3h5Ej+MQ3UCgUe9ERfh9L/v/lgLmekgYdFNaUZi8C1z+O4Jb/8MbHmpJ4Hu9XtA8pI2MLZMRNgOrnFwgXTukIhyHhJ2/2wVi6gwfyxzkJMU27jyGY/gLX9NtjwqhL4NaMWG6t/6m";

    public String stackLabel;

    private VuforiaLocalizer vuforia;

    private TFObjectDetector tfod;

    private boolean recognition;

    private void initVuforia() {
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraName = hardwareMap.get(WebcamName.class, "Webcam 1");

        vuforia = ClassFactory.getInstance().createVuforia(parameters);
    }

    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minResultConfidence = 0.8f;
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);

        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_FIRST_ELEMENT, LABEL_SECOND_ELEMENT);
    }
    @Override
    public void init() {
        robot = new WHSRobotImplOld(hardwareMap);
        initVuforia();
        initTfod();

        if (tfod != null) {
            tfod.activate();

            tfod.setZoom(2.0, 16.0 / 9.0);
        }

    }

    @Override
    public void loop() {
        if (tfod != null) {
            List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
            recognition = updatedRecognitions != null;
            if (updatedRecognitions != null) {
                stackLabel = "Zero";
                                /*telemetry.addData("# Object Detected", updatedRecognitions.size());
                                int i = 0;*/
                for (Recognition recognition : updatedRecognitions) {
                                    /*telemetry.addData(String.format("label (%d)", i), recognition.getLabel());
                                    telemetry.addData(String.format("  left,top (%d)", i), "%.03f , %.03f",
                                            recognition.getLeft(), recognition.getTop());
                                    telemetry.addData(String.format("  right,bottom (%d)", i), "%.03f , %.03f",
                                            recognition.getRight(), recognition.getBottom());
                                    telemetry.update();*/
                    stackLabel = recognition.getLabel();
                }
            }

            if (stackLabel == "Four") {
                stackSize = stackLabel;
            } else if (stackLabel == "One") {
                stackSize = stackLabel;
            }else {
                stackSize = "Zero";
            }
        }
        telemetry.addData("Stack Size: ", stackSize);
        telemetry.addData("Stack Label", stackLabel);
        telemetry.addData("Updated Recognitions", recognition );
    }
}
