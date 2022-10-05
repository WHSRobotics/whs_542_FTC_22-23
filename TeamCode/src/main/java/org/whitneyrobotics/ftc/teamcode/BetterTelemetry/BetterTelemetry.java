package org.whitneyrobotics.ftc.teamcode.BetterTelemetry;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

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

    private BetterTelemetry(OpMode o){
        items = new ArrayList<>();
        currentOpMode = o;
        Autonomous auto = o.getClass().getDeclaredAnnotation(Autonomous.class);
        TeleOp tele = o.getClass().getDeclaredAnnotation(TeleOp.class);
        String name = (auto == null) ? (tele == null) ? "OpMode is missing annotation" : tele.name() : auto.name();
        this.addItem(new TextLine(String.format("OpMode %s (%s)",name, (auto == null) ? (tele == null) ? "Unknown" : "TeleOp" : "Autonomous"), LineItem.Color.AQUA));
        this.addItem(new KeyValueLine("Runtime", () ->
            String.format("%s:%s",(int)Math.floor(currentOpMode.getRuntime()/60), (int)Math.floor(currentOpMode.getRuntime()%60))
                , LineItem.Color.WHITE));
        opModeTelemetry = currentOpMode.telemetry;
        opModeTelemetry.setDisplayFormat(Telemetry.DisplayFormat.HTML);
        opModeTelemetry.speak("BetterTelemetry connected on " + name);
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
        for (LineItem item : items){
            opModeTelemetry.addLine(item.getFormatted(blink));
        }
        //opModeTelemetry.clear();
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

    public BetterTelemetry addLine(String line){
        return addLine(line, LineItem.Color.WHITE);
    }

    public BetterTelemetry addLine(String line, LineItem.Color color){
        items.add(new TextLine(line, color));
        return this;
    }

    public BetterTelemetry addData(String key, Object value){
        return addData(key, value, LineItem.Color.WHITE);
    }

    public BetterTelemetry addData(String key, Object value, LineItem.Color color){
        items.add(new KeyValueLine(key, value, color));
        return this;
    }

    public <T> BetterTelemetry addData(String key, Func<T> valueProducer){
        return addData(key, valueProducer, LineItem.Color.WHITE);
    }

    public <T> BetterTelemetry addData(String key, Func<T> valueProducer, LineItem.Color color){
        items.add(new KeyValueLine(key, valueProducer, color));
        return this;
    }
}
