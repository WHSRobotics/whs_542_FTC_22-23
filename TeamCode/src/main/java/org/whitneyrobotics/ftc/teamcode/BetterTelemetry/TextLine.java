package org.whitneyrobotics.ftc.teamcode.BetterTelemetry;

public class TextLine extends LineItem{
    public TextLine(String text, boolean persistent, Color color, RichTextFormat... richTextFormats){

        super(text,persistent,color, richTextFormats);
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
