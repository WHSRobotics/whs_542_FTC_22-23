package org.whitneyrobotics.ftc.teamcode.BetterTelemetry;

public class TextLine extends LineItem{
    public TextLine(String text, Color color){
        super(text,color);
    }

    public void setText(String text){
        this.caption = text;
    }

    @Override
    public void reset() {

    }

    @Override
    protected String format(boolean blink) {
        return caption;
    }
}
