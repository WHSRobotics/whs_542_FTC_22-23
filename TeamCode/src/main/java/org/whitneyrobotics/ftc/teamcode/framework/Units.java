package org.whitneyrobotics.ftc.teamcode.framework;

public enum Units {
    INCH(1), FEET(1/12), MM(25.4), CM(2.54), DM(0.254), M(0.0254);
    private double ratioToInch;
    public double ratioToInch() {return ratioToInch;}
    Units(double ratioToInch){
        this.ratioToInch = ratioToInch;
    }
}
