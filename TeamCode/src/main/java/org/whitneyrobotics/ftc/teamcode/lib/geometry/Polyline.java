package org.whitneyrobotics.ftc.teamcode.lib.geometry;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import org.whitneyrobotics.ftc.teamcode.framework.Vector;
import org.whitneyrobotics.ftc.teamcode.lib.util.Functions;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

@RequiresApi(api = Build.VERSION_CODES.N)
public class Polyline implements Trajectory, Iterable<Line> {

    private ArrayList<Position> positionsList = new ArrayList<>();
    int positionIndex;

    public Polyline(Position genesisPosition, Position... additionalPositions){
        positionsList.add(genesisPosition);
        positionsList.addAll(Arrays.asList(additionalPositions));
    }

    public Polyline(Collection<Position> positions){
        if(positionsList.size() <= 1) {
            throw new IllegalArgumentException("Polyline must have at least 2 points");
        }
        this.positionsList.addAll(positions);
    }

    public Polyline(Line initial){
        positionsList.add(initial.endpoint1);
        positionsList.add(initial.endpoint2);
    }

    public void extend(Polyline pLine) {
        this.positionsList.addAll(pLine.getPositions());
    }

    public ArrayList<Position> getPositions() {
        return positionsList;
    }
    public void add(Position p) {
        positionsList.add(p);
    }
    public void remove(int pIndex) {
        positionsList.remove(pIndex);
    }

    public Line nthSegment(int segmentIndex){
        if (segmentIndex >= positionsList.size()-1){
               throw new IllegalArgumentException(String.format("Polyline only has %d segments, but program requested %dth segment", positionsList.size()-1,segmentIndex));
        }
        return new Line(positionsList.get(segmentIndex), positionsList.get(segmentIndex+1));
    }

    public Position first(){
        return this.positionsList.get(0);
    }

    public Position next(){
        if(positionIndex <= this.positionsList.size()-2) positionIndex += 1;
        return this.positionsList.get(positionIndex);
    }

    public Position previous(){
        if(positionIndex > 0) positionIndex -= 1;
        return this.positionsList.get(positionIndex);
    }

    public Position last() {
        return positionsList.get(positionsList.size() - 1);
    }

    public Polyline injectPoints(double spacing){
        ArrayList<Position> newPoints = new ArrayList<>();
        for (Line segment : this){
            Vector start = segment.endpoint1.asVector();
            Vector end = segment.endpoint2.asVector();

            Vector v = end.subtract(start);
            double num_points = Math.ceil(v.getMagnitude() / spacing);
            v = v.normalize().multiplyByScalar(spacing);

            for (int i = 0; i<num_points; i++){
                newPoints.add(start.add(v.multiplyByScalar(i)).to2DPosition());
            }
        }
        newPoints.add(last());
        return new Polyline(newPoints);
    }

    /**
     * Length in the same unit that Position is in
     * @return
     */
    @Override
    public double length() {
        double length = 0.0d;
        for (Line segment : this){
            length += segment.length();
        }
        return length;
    }

    //Note from dev: we don't stop iteration on the first one found, since there's no guarantee that the polyline won't loop back closer to the point
    //BigO: O(n), where n is the
    @Override
    public Position closestPositionTo(Position currentPos) {
        double shortestManhattanDistance = 0.0d;
        Position shortestPosition = this.positionsList.get(0);
        for (int i = 0; i<positionsList.size(); i++){
            double distance = Functions.calculateDistance(positionsList.get(i),currentPos);
            if (distance < shortestManhattanDistance){
                shortestManhattanDistance = distance;
                shortestPosition = positionsList.get(i);
            }
        }
        return shortestPosition;
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @NonNull
    @Override
    public Iterator<Line> iterator() {
        return new Iterator<Line>() {
            int pointer = 0;

            @Override
            public boolean hasNext() {
                return pointer < positionsList.size()-1;
            }

            @Override
            public Line next() {
                Line l = nthSegment(pointer);
                pointer++;
                return l;
            }
        };
    }
}
