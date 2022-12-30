package org.whitneyrobotics.ftc.teamcode.Field;
import static org.whitneyrobotics.ftc.teamcode.Field.Junction.JunctionHeights.*;

public class Field {
    public static final double TILE = 24; // in inches

    public static Junction ground = new Junction(0,0, GROUND);
    public static Junction northTall = new Junction(0,TILE, HIGH);
    public static Junction southTall = new Junction(0, TILE * -1, HIGH);
    public static Junction westTall = new Junction(TILE*-1, TILE * -1, HIGH);
    public static Junction eastTall = new Junction(TILE, 0, HIGH);
    public static Junction northeastMedium = new Junction(TILE, TILE, MEDIUM);
    public static Junction northwestMedium = new Junction(-TILE, TILE, MEDIUM);
    public static Junction southwestMedium = new Junction(TILE, -TILE, MEDIUM);
    public static Junction southeastMedium = new Junction(-TILE, -TILE, MEDIUM);
    public static Junction northnortheastLow = new Junction(TILE, TILE * 2, LOW);
    public static Junction northeasteastLow = new Junction(TILE * 2, TILE, LOW);
    public static Junction northnorthwestLow = new Junction(-TILE, TILE*2, LOW);
    public static Junction northwestwestLow = new Junction(-TILE * 2, TILE, LOW);
    public static Junction southwestwestLow = new Junction(-TILE * 2, -TILE, LOW);
    public static Junction southsouthwestLow = new Junction(TILE, -TILE * 2, LOW);
    public static Junction southsoutheastLow = new Junction(TILE, -TILE * 2, LOW);
    public static Junction southeasteastLow = new Junction(TILE * 2, -TILE, LOW);
    public static Junction eastGround = new Junction(TILE * 2, 0, GROUND);
    public static Junction northeastGround = new Junction(TILE * 2, TILE * 2, GROUND);
    public static Junction northGround = new Junction(0, TILE * 2, GROUND);
    public static Junction northwestGround = new Junction(-TILE * 2, TILE * 2, GROUND);
    public static Junction westGround = new Junction(-TILE * 2, 0, GROUND);
    public static Junction southwestGround = new Junction(-TILE * 2, -TILE * 2, GROUND);
    public static Junction southGround = new Junction(0, -TILE * 2, GROUND);
    public static Junction southeastGround = new Junction(TILE * 2, -TILE * 2, GROUND);


}
