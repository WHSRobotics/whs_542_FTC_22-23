package org.whitneyrobotics.ftc.teamcode.visionImpl;


import org.opencv.core.Rect;
import org.opencv.core.Point;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;
/**
 * Created by Sid
 * */
public class BarcodeScanner extends OpenCvPipeline {

    /**
     * Barcode labels
     * */
    public enum Barcode {
        LEFT,
        MIDDLE,
        RIGHT
    }

    /**
     * Width of the image
     * */
    public double width;

    /**
     * Height of the image
     * */
    public double height;

    /**
     * Constructs a new barcode scanner, with given resolution
     * @param w Width of the frame
     * @param h Height of the frame*/
    public BarcodeScanner(double w, double h, double[] ratios){
        super();
        if(ratios.length != 3 ) throw new IllegalArgumentException("Barcode scanner must have only 3 ratios for screen divisions");
        double ratioTotal = ratios[0] + ratios[1] + ratios[2];
        double boundaryOne = ((ratios[0] / ratioTotal)*width);
        double boundaryTwo = ((ratios[0] + ratios[1]) / ratioTotal)*width;
        width = w;
        height = h;

//        leftROI = new Rect(new Point(0,0), new Point(boundaryOne, height));
//        midROI = new Rect(new Point(boundaryOne,0), new Point(boundaryTwo, height));
//        rightROI = new Rect(new Point(boundaryTwo,0), new Point(width, height));
        leftROI = new Rect(new Point(0,0), new Point(width/3, height));
        midROI = new Rect(new Point((width/3),0), new Point((width/3)*2, height));
        rightROI = new Rect(new Point((width/3)*2,0), new Point(width, height));

    }

    // Mat is color matrix
    private Mat mat = new Mat();
    private Mat leftMat, midMat, rightMat = new Mat();
    /* coordinate system
    origin ---------------> x+
    |
    |
    |
    |
    |
    v
    y+
     */
    private Rect leftROI;
    private Rect midROI;
    private Rect rightROI;

    // Could pass in telemetry into constructor for use in class.

    private Barcode result;

    /**
     * Implicitly called in OpMode to recognize the barcode
     * @param input Current frames to process
     * @return New matrix frame with visual annotations*/
    @Override
    public Mat processFrame(Mat input) {
        Imgproc.cvtColor(input, input, Imgproc.COLOR_RGBA2RGB);
        Imgproc.cvtColor(input, mat, Imgproc.COLOR_RGB2HSV);
        // CHANGE THESE ACCORDINGLY
        // Division by two since only 180 degrees allowed
        int[] color = {39, 60, 77};
        Scalar upperBound = new Scalar(color[0] / 2.0 + 30, color[1] + 30, color[2] + 30);
        Scalar lowerBound = new Scalar(color[0] / 2.0 - 30, color[1] - 30, color[2] - 30);
        Core.inRange(mat, lowerBound, upperBound, mat);

        leftMat = mat.submat(leftROI);
        midMat = mat.submat(midROI);
        rightMat = mat.submat(rightROI);

        double leftValue = Core.sumElems(leftMat).val[0];
        double midValue = Core.sumElems(midMat).val[0];
        double rightValue = Core.sumElems(rightMat).val[0];

        double maxValue = Math.max(leftValue, Math.max(midValue, rightValue));
        Scalar matchColor = new Scalar(0,255,0);
        Scalar mismatchColor = new Scalar(255,0,0);

        if (maxValue == leftValue) {
            result = Barcode.LEFT;
            Imgproc.rectangle(input, leftROI, matchColor, 5);
            Imgproc.rectangle(input, midROI, mismatchColor, 5);
            Imgproc.rectangle(input, rightROI, mismatchColor, 5);
        } else if (maxValue == midValue) {
            result = Barcode.MIDDLE;
            Imgproc.rectangle(input, leftROI, mismatchColor, 5);
            Imgproc.rectangle(input, midROI, matchColor, 5);
            Imgproc.rectangle(input, rightROI, mismatchColor, 5);
        } else if (maxValue == rightValue) {
            result = Barcode.RIGHT;
            Imgproc.rectangle(input, leftROI, mismatchColor, 5);
            Imgproc.rectangle(input, midROI, mismatchColor, 5);
            Imgproc.rectangle(input, rightROI, matchColor, 5);
        } else {
            result = Barcode.LEFT;
            Imgproc.rectangle(input, leftROI, mismatchColor, 5);
            Imgproc.rectangle(input, midROI, mismatchColor, 5);
            Imgproc.rectangle(input, rightROI, mismatchColor, 5);
        }
        Imgproc.putText(input, result.name(), new Point(width/2,height/2),0,12.0,new Scalar(255,255,255),2);
        return input;
    }

    /**
     * Gets the last known result of the barcode
     * @return  The Barcode that is detected
     * @see Barcode*/
    public Barcode getResult() {
        // CountDownLatch could be used?
        return result;
    }
}