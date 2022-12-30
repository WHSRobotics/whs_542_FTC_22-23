package org.whitneyrobotics.ftc.teamcode.Field;

import com.sun.tools.javac.Main;

import org.firstinspires.ftc.robotcore.internal.android.dx.dex.code.HighRegisterPrefix;
import org.whitneyrobotics.ftc.teamcode.lib.geometry.Position;

public class Junction {
    public enum JunctionHeights {
        GROUND(0.56), LOW(13.5), MEDIUM(23.5), HIGH(33.5);
        final double height;
        JunctionHeights(double height){
            this.height = height;
        }
    }
    public Position pos;
    public JunctionHeights height;

    public Junction(Position pos, JunctionHeights height){
        this.pos = pos;
        this.height = height;
    };

    public Junction(double x, double y, JunctionHeights height){
        this.pos = new Position(x,y);
        this.height = height;
    }
}
