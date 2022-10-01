package org.whitneyrobotics.ftc.teamcode.framework;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.stream.CameraStreamServer;
import org.firstinspires.ftc.robotcore.external.stream.CameraStreamSource;
import org.firstinspires.ftc.robotcore.internal.opmode.TelemetryImpl;

/**
 * Unified class extension for creating FtcDashboard instances without having to repeat code
 */
public abstract class DashboardOpMode extends OpMode {

    /**
     * Boolean on whether or not to use colorTelemetry*/
    protected boolean useColorTelemetry = false;
    /**
     * FtcDashboard instance
     */
    protected FtcDashboard dashboard;
    /**
     * Telemetry instance to transmit packets to
     */
    protected Telemetry dashboardTelemetry;
    /**
     * Packet to add graphics to display on Dashboard
     */
    protected TelemetryPacket packet = new TelemetryPacket();

    /**
     * Reassign telemetry to the output of this function and creates a paired telemetry stream, do not use if using packets
     * @param msTransmissionInterval How often to transmit new packets
     * @return Paired telemetry stream that displays on FTC Driver Station
     * @see Telemetry
     */
    protected Telemetry initializeDashboardTelemetry(int msTransmissionInterval){
        dashboard = FtcDashboard.getInstance();
        dashboardTelemetry = dashboard.getTelemetry();
        Telemetry telemetryStream = new MultipleTelemetry(telemetry, dashboardTelemetry);
        telemetryStream.setMsTransmissionInterval(msTransmissionInterval);
        telemetry = telemetryStream;
        return telemetryStream;
    }

    public static class ColorTelemetryImpl extends TelemetryImpl implements Telemetry {


        public ColorTelemetryImpl(OpMode opMode) {
            super(opMode);
            setDisplayFormat(DisplayFormat.HTML);
        }

        public void addLine(String colorHex, String msg){
            addLine(String.format("<p style=\"color: #%s\">%s</p>",colorHex,msg));
        }

        public void addLine(RGBA color, String msg){
            addLine(color.toHex(),msg);
        }

        public static class RGBA {
            public int R;
            public int G;
            public int B;
            public int A;
            public RGBA(int R, int G, int B, int A){
                this.R = R;
                this.G = G;
                this.B = B;
                this.A = A;
            }

            public String toHex(){
                String r = padFront(Integer.toHexString(checkRange(R,0,255)),2);
                String g = padFront(Integer.toHexString(checkRange(G,0,255)),2);
                String b = padFront(Integer.toHexString(checkRange(B,0,255)),2);
                String a = padFront(Integer.toHexString(checkRange(A,0,255)),2);
                return r+g+b+a;
            }

            public static int checkRange(int value, int min, int max){
                if(value < min){
                    return min;
                } else if (value > max){
                    return max;
                }
                return value;
            }

            public static String repeat(String s, int times){
                String result = "";
                for(int i = 0; i<times; i++){
                    result += s;
                }
                return result;
            }

            public static String padFront(String val, int idealDigits){
                int diff = idealDigits - val.length();
                if (diff > 0){
                    return repeat("0",idealDigits) + val;
                }
                return val;
            }
        }
    }

    protected ColorTelemetryImpl useColorTelemetry(OpMode om){
        return new ColorTelemetryImpl(om);
    }

    protected void startDriverStationWebcamStream(CameraStreamSource source){
        CameraStreamServer.getInstance().setSource(source);
    }

    protected void refreshDashboardPacket(){
        dashboard.sendTelemetryPacket(packet);
    }

}
