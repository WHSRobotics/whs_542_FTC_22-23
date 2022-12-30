package org.whitneyrobotics.ftc.teamcode.PurePursuit2022;

import java.util.ArrayList;

public class PathPlanner {
    private PathPoint[] nodes;
    private double maxVelocity;
    private double lookAheadDistance;
    private int pointsInBetween;

    private ArrayList<PathPoint> finalPath;

    public PathPlanner (PathPoint[] nodes, double maxVelocity, double lookAheadDistance, int pointsInBetween){
        this.nodes = nodes;
        this.maxVelocity = maxVelocity;
        this.lookAheadDistance = lookAheadDistance;
        this.pointsInBetween = pointsInBetween;
    }

    // changes the node points (for path recalculation)
    public void changeNodes(PathPoint[] nodes){
        this.nodes = nodes;
    }

    // creates points between provided nodes
    public void createPath(){
        double xDif;
        double yDif;
        for (int i = 0; i < nodes.length - 2; i++){
            xDif = (nodes[i + 1].getX() - nodes[i].getX())/(pointsInBetween);
            yDif = (nodes[i + 1].getY() - nodes[i].getY())/(pointsInBetween);
            for (int j = 0; j < pointsInBetween; j++){
                finalPath.add(new PathPoint(nodes[i].getX() + (xDif * i), nodes[i].getY() + (yDif * i)));
            }
        }
    }

    public void calculateCurvatures(){
        // variables in research paper
        double r;
        double x;
        double y;
        double l;
        double d;
        double curvature;

        for (int i = 0; i < finalPath.size() - 3; i++){
            x = finalPath.get(i + 2).getX() - finalPath.get(i).getX();
            y = finalPath.get(i + 2).getY() - finalPath.get(i).getY();
            l = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
            r = (Math.pow(l, 2))/(2 * x);
            
        }
    }
}
