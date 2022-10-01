package org.whitneyrobotics.ftc.teamcode.lib.util;

public enum Alliance {
    RED(0),BLUE(1);
    private final int value;
    Alliance(int value){
        this.value = value;
    }
    public int getValue(){return value;}
}
