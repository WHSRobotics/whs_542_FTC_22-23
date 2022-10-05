package org.whitneyrobotics.ftc.teamcode.BetterTelemetry;

import org.firstinspires.ftc.robotcore.external.Func;
import org.firstinspires.ftc.robotcore.external.Telemetry;

public class KeyValueLine<T> extends LineItem {
    private Object value;
    private Func valueProducer = null;
    private static String separator = " : ";

    public static void setSeparator(String sep){
        separator = sep;
    }

    public KeyValueLine(String caption, Object data, Color color){
        super(caption, color);
        this.caption = caption;
        this.value = data;
    }

    public KeyValueLine(String caption, Func<T> valueProducer, Color color){
        super(caption, color);
        this.valueProducer = valueProducer;
    }

    public String getCaption(){
        return this.caption;
    }

    public void update(Object value){
        if(valueProducer == null) this.value = value;
    }

    @Override
    public void reset() {
        this.value = valueProducer.value();
    }

    @Override
    protected String format(boolean blink) {
        if(valueProducer != null) value = valueProducer.value();
        return String.format("%s%s%s",caption,separator,value.toString());
    }
}
