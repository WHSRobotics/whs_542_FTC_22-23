package org.whitneyrobotics.ftc.teamcode.BetterTelemetry;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Func;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.whitneyrobotics.ftc.teamcode.GamepadEx.GamepadEx;

import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.N)
public class BetterTelemetry  {
    private OpMode currentOpMode;
    private Telemetry opModeTelemetry;

    private final ArrayList<LineItem> items;
    private final ArrayList<LineItem.Interactable> interactables = new ArrayList<>();
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
        opModeTelemetry = currentOpMode.telemetry;
        opModeTelemetry.setDisplayFormat(Telemetry.DisplayFormat.HTML);
        //opModeTelemetry.speak("BetterTelemetry connected on " + name);
    }

    private static BetterTelemetry instance;

    public static BetterTelemetry setOpMode(OpMode o){
        instance = new BetterTelemetry(o);
        return instance;
    }

    public static BetterTelemetry getCurrentInstance(){
        return instance;
    }

    public void update(){
        //blink every second
        boolean blink = currentOpMode.getRuntime() % 2 > 1;
        for (LineItem item : items) {
            opModeTelemetry.addLine(
                    (lineNumbers ? "   " + (items.size() < 10 ? "    " : "") + items.indexOf(item) + "| " : "") +
                            item.render(blink));
        }
        purge();
        //opModeTelemetry.clear();
    }

    public BetterTelemetry toggleLineNumbers(){
        lineNumbers = !lineNumbers;
        return this;
    }

    public void purge(){
        items.removeIf(item -> !item.isPersistent());
    }

    private LineItem.Interactable focused = null;

    private LineItem.Interactable getNextInteractable(LineItem current, boolean dec){
        if(interactables.size() < 1) return null;
        if(current == null){
            return interactables.get(0);
        } else {
            return interactables.get((interactables.indexOf(current) + (dec ? -1 : 1) ) % interactables.size());
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
    public void interact(GamepadEx gamepad){
        if(gamepad != null){
            focused = getNextInteractable(focused,false);
            if(focused != null) {
                focused.connectGamepad(gamepad);
                focused.focus();
            }
        }
    }

    public BetterTelemetry removeLineByCaption(String caption){
        items.removeIf(item -> item.caption == caption);
        interactables.removeIf(item -> item.caption == caption);
        return this;
    }

    public BetterTelemetry addItem(LineItem item){
        items.add(item);
        if(item instanceof LineItem.Interactable) interactables.add((LineItem.Interactable) item);
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
