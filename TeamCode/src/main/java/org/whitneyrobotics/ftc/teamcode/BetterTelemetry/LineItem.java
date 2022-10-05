package org.whitneyrobotics.ftc.teamcode.BetterTelemetry;

import org.whitneyrobotics.ftc.teamcode.GamepadEx.GamepadEx;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class LineItem {
    public enum Color {
        BLACK("000000"),
        SILVER("C0C0C0"),
        GRAY("808080"),
        WHITE("FFFFFF"),
        MAROON("800000"),
        RED("FF0000"),
        PURPLE("800080"),
        FUCHSIA("FF00FF"),
        GREEN("008000"),
        LIME("00FF00"),
        OLIVE("808000"),
        YELLOW("FFFF00"),
        ROBOTICS("FFDD00"),
        NAVY("000080"),
        BLUE("0000FF"),
        TEAL("008080"),
        AQUA("00FFFF");

        private final String hexCode;
        Color(String hexCode){
            this.hexCode = hexCode;
        }
        public String getHexCode(){
            return "#" + hexCode;
        }
    }

    public enum RichTextFormat {
        BOLD, ITALICS, UNDERLINE
    }

    private Set<RichTextFormat> rtfFormats = new HashSet<>();

    public static abstract class Interactable extends LineItem {
        private GamepadEx gamepad;

        public Interactable(String caption) {
            super(caption);
        }

        public Interactable(String caption, Color color){
            super(caption, color);
        }
        public void connectGamepad(GamepadEx gamepad){

        }
        public abstract void focus();

        public void disconnect(){

        }
    }

    protected String caption;

    public int getImportance() {
        return importance;
    }

    protected void setImportance(int importance) {
        this.importance = importance;
    }

    private int importance;

    protected LineItem(String caption){
        this.caption = caption;
    }
    protected LineItem(String caption, Color color, RichTextFormat... rtfFormats){
        this.caption = caption;
        this.color = color;
        this.rtfFormats.addAll(Arrays.asList(rtfFormats));
    }

    protected Color color;

    public void setColor(Color c){
        this.color = c;
    }

    public LineItem setRichTextFormat(RichTextFormat... rtf) {
        this.rtfFormats.addAll(Arrays.asList(rtf));
        return this;
    }

    public abstract void reset();

    protected abstract String format(boolean blink);

    public String getFormatted(boolean blink){
        return String.format("<font color=\"%s\">%s</font>", this.color.getHexCode(), format(blink));
    }
}
