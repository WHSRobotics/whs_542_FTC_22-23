package org.whitneyrobotics.ftc.teamcode.pathfinding;

import org.whitneyrobotics.ftc.teamcode.lib.geometry.Position;

import java.util.ArrayList;

public class TileNode extends Position {

    private ArrayList<Position> inner;

    public TileNode(double x, double y) {
        super(x, y);
        inner = new ArrayList<>();
    }

}
