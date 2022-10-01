package org.whitneyrobotics.ftc.teamcode.lib.purepursuit.strafetotarget;

import org.whitneyrobotics.ftc.teamcode.lib.geometry.Coordinate;
import org.whitneyrobotics.ftc.teamcode.lib.geometry.Position;
import org.whitneyrobotics.ftc.teamcode.lib.geometry.StrafeWaypoint;
import org.whitneyrobotics.ftc.teamcode.lib.purepursuit.FollowerConstants;

import java.util.ArrayList;

public class StrafePath {

    private ArrayList<StrafeWaypoint> waypoints = new ArrayList<>();

    FollowerConstants followerConstants;

    public StrafePath(ArrayList<StrafeWaypoint> waypoints, FollowerConstants lookaheadDistance) {
        this.waypoints = waypoints;
        this.followerConstants = lookaheadDistance;
    }

    public ArrayList<StrafeWaypoint> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(ArrayList<StrafeWaypoint> waypoints) {
        this.waypoints = waypoints;
    }

    public double getFollowerConstants() {
        return followerConstants.getLookaheadDistance();
    }

    public Coordinate getCoordinateAtIndex(int index){
        return waypoints.get(index).getCoordinate();
    }

    public double getTangentialVelocityAtIndex(int index){
        return waypoints.get(index).getTangentialVelocity();
    }

    public double getAngularVelocityAtIndex(int index){
        return waypoints.get(index).getAngularVelocity();
    }

    public int size(){
        return waypoints.size();
    }

    public boolean backwards(){
        return followerConstants.backwards();
    }

}
