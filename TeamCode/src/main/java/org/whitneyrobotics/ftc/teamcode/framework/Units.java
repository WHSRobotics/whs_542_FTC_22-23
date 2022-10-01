package org.whitneyrobotics.ftc.teamcode.framework;

public enum Units {
    INCH(1);
    private int ratioToInch;
    Units(int ratioToInch){
        this.ratioToInch = ratioToInch;
    }
}
