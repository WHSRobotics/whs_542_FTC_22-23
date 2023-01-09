package org.whitneyrobotics.ftc.teamcode.BetterTelemetry;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Func;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.whitneyrobotics.ftc.teamcode.GamepadEx.GamepadEx;
import org.whitneyrobotics.ftc.teamcode.autoop.AutoSetupTesting.TestManager;
import org.whitneyrobotics.ftc.teamcode.framework.opmodes.OpModeEx;
import org.whitneyrobotics.ftc.teamcode.tests.Test;

import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.N)
public class BetterTelemetry  {
    private OpMode currentOpMode;
    private Telemetry opModeTelemetry;
    private TestManager testManager;

    private final ArrayList<LineItem> items;
    private final ArrayList<Interactable> interactables = new ArrayList<>();
    private boolean lineNumbers = false;

    private BetterTelemetry(OpMode o){
        items = new ArrayList<>();
        currentOpMode = o;
        Autonomous auto = o.getClass().getDeclaredAnnotation(Autonomous.class);
        TeleOp tele = o.getClass().getDeclaredAnnotation(TeleOp.class);
        String name = (auto == null) ? (tele == null) ? "OpMode is missing annotation" : tele.name() : auto.name();
        this.addItem(new TextLine(String.format("OpMode %s (%s)",name, (auto == null) ? (tele == null) ? "Unknown" : "TeleOp" : "Autonomous"), true, LineItem.Color.AQUA)
                .setRichTextFormat(LineItem.RichTextFormat.BOLD)
        );
        this.addItem(new KeyValueLine("Runtime", true, (() ->
            String.format("%s:%s",(int)Math.floor(currentOpMode.getRuntime()/60), (int)Math.floor(currentOpMode.getRuntime()%60))
        ), LineItem.Color.WHITE));

        Test testingAnnotation = o.getClass().getDeclaredAnnotation(Test.class);
        if(testingAnnotation != null){
            this.addItem(new TextLine(String.format("Running Test %s of [%s]\n%s%s",
                    testingAnnotation.name(),o.getClass().getPackage().getName(),
                    testingAnnotation.description(),
                    (testingAnnotation.autoTerminateAfterSeconds() > 0 && o instanceof OpModeEx) ?
                            "This test will terminate after " + testingAnnotation.autoTerminateAfterSeconds() + " seconds." :
                            "\n"),
                    true,LineItem.Color.LIME, LineItem.RichTextFormat.ITALICS));
        }
        opModeTelemetry = currentOpMode.telemetry;
        opModeTelemetry.setDisplayFormat(Telemetry.DisplayFormat.HTML);
    }

    public TestManager useTestManager(){
        TestManager testManager = new TestManager(this);
        return testManager;
    }

    private TelemetryPacket packet;
    private Telemetry dashbboardTelemetry;

    private static BetterTelemetry instance;

    public static BetterTelemetry setOpMode(OpMode o){
        instance = new BetterTelemetry(o);
        return instance;
    }

    public void useTelemetryPacket(TelemetryPacket packet){
        this.packet = packet;
        //opModeTelemetry.addLine("Packet connected");
        //addLine("Packet connected", LineItem.Color.GREEN, LineItem.RichTextFormat.BOLD).persistent();
    }

    public void useDashboardTelemetry(Telemetry dashbboardTelemetry){
        this.dashbboardTelemetry = dashbboardTelemetry;
    }

    public static BetterTelemetry getCurrentInstance(){
        return instance;
    }

    public void update(){
        //blink every second
        interact();
        boolean blink = currentOpMode.getRuntime() % 0.5 > 0.25;
        for (LineItem item : items) {
            opModeTelemetry.addLine(
                    (lineNumbers ? "   " + (items.size() < 10 ? "    " : "") + items.indexOf(item) + "| " : "") +
                            item.render(blink));
            if (dashbboardTelemetry != null && item instanceof KeyValueLine){
                KeyValueLine keyValueItem = (KeyValueLine) item;
                dashbboardTelemetry.addData(keyValueItem.caption, keyValueItem.value());
                dashbboardTelemetry.update();
            }
        }
        purge();
        //opModeTelemetry.clear();
    }

    public BetterTelemetry toggleLineNumbers(){
        lineNumbers = !lineNumbers;
        return this;
    }

    public void purge(){
        items.removeIf(item -> {
            boolean persistent = item.isPersistent();
            if(!persistent){
                if (item instanceof Interactable){
                    ((Interactable) item).disconnect();
                    interactables.remove(item);
                }
            }
            return !persistent;
        });
    }

    private Interactable focused = null;

    private Interactable getNextInteractable(boolean dec){
        if(interactables.size() < 1) return null;
        if(focused == null){
            return interactables.get(0);
        } else {
            int focusIndex = interactables.indexOf(focused);
            if(dec && focusIndex == 0) return interactables.get(interactables.size()-1);//modulo wrapping fix
            return interactables.get((focusIndex + (dec ? -1 : 1) ) % interactables.size());
        }
    }

    public void speak(String text){
        opModeTelemetry.speak(text);
    }

    public void speak(String text, String languageCode, String countryCode){
        opModeTelemetry.speak(text, languageCode, countryCode);
    }

    /**
     * TODO: Make GamepadEx with button hold signal ignore methods
     * @param gamepad
     */
    private GamepadEx interactingGamepad;

    /**
     * Creates a ghost {@link GamepadEx} instance in the telemetry to process gamepad interactions.
     */
    public void setInteractingGamepad(GamepadEx gamepad){
        interactingGamepad = new GamepadEx(gamepad.getEncapsulatedGamepad());
        interactingGamepad.DPAD_UP.onPress(e -> {
            if(focused != null) focused.disconnect();
            focused = getNextInteractable(true);
        });
        interactingGamepad.DPAD_DOWN.onPress(e -> {
            if(focused != null) focused.disconnect();
            focused = getNextInteractable(false);
        });
    }

    public void interact(){
        if(interactingGamepad != null){
            interactingGamepad.update();
            if(focused != null) {
                focused.connectGamepad(interactingGamepad);
                focused.focus();
            } else if (interactables.size() > 0){
                focused = getNextInteractable(false);
            }
        }
    }

    public BetterTelemetry removeLineByReference(LineItem item){
        for (LineItem i : items){
            if(i == item) item.setPersistent(false);
        }
        interactables.removeIf(i -> i == item);
        if(!interactables.contains(focused) && focused != null){
            focused.disconnect();
            focused = getNextInteractable(false);
        }
        return this;
    }

    public BetterTelemetry removeLineByCaption(String caption){
        for (LineItem item : items){
            if(item.caption == caption) item.setPersistent(false);
        }
        interactables.removeIf(item -> item.caption == caption);
        if(!interactables.contains(focused) && focused != null){
            focused.disconnect();
            focused = getNextInteractable(false);
        }
        return this;
    }

    public BetterTelemetry addItem(LineItem item){
        items.add(item);
        if(item instanceof Interactable) interactables.add((Interactable) item);
        return this;
    }

    public LineItem addLine(){
        SeparatorLine line = new SeparatorLine("blank line", SeparatorLine.LineStyle.NEWLINE);
        items.add(line);
        return line;
    }

    public LineItem addLine(String line, LineItem.RichTextFormat... richTextFormats){
        return addLine(line, LineItem.Color.WHITE, richTextFormats);
    }

    public LineItem addLine(String line, LineItem.Color color, LineItem.RichTextFormat... richTextFormats){
        TextLine lineItem = new TextLine(line, false, color, richTextFormats);
        items.add(lineItem);
        return lineItem;
    }

    public LineItem addData(String key, Object value, LineItem.RichTextFormat... richTextFormats){
        return addData(key, value, LineItem.Color.WHITE, richTextFormats);
    }

    public LineItem addData(String key, Object value, LineItem.Color color, LineItem.RichTextFormat... richTextFormats){
        KeyValueLine line = new KeyValueLine(key, false, value, color, richTextFormats);
        items.add(line);
        return line;
    }

    public <T> LineItem addData(String key, Func<T> valueProducer, LineItem.RichTextFormat... richTextFormats){
        return addData(key, valueProducer, LineItem.Color.WHITE, richTextFormats);
    }

    public <T> LineItem addData(String key, Func<T> valueProducer, LineItem.Color color, LineItem.RichTextFormat... richTextFormats){
        KeyValueLine line = new KeyValueLine(key, false, valueProducer, color, richTextFormats);
        items.add(line);
        return line;
    }

    public static String replaceNewLineWithLineBreaks(String s){
        return s.replaceAll("\n","<br>");
    }
}
