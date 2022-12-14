package org.whitneyrobotics.ftc.teamcode.framework;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.whitneyrobotics.ftc.teamcode.lib.geometry.Position;

@RequiresApi(api = Build.VERSION_CODES.N)
public class Vector extends Matrix {
    public Vector(double x1, double... x) {
        super(new double[1 + x.length][1]);
        matrix[0][0] = x1;
        for(int i = 0; i<x.length; i++){
            matrix[i+1][0] = x[i];
        }
    }

    public static Vector VectorFromMagnitudeAndAngle(double m, double theta){
        return new Vector(m * Math.cos(theta), m * Math.sin(theta));
    }

    public double getMagnitude(){
        double sum = 0;
        for (int i = 0; i<rows; i++){
            sum += Math.pow(matrix[i][0],2);
        }
        return Math.sqrt(sum);
    }

    public Vector normalize(){
        return this.divideByScalar(this.getMagnitude()).toColumnVector();
    }

    public double getDirection(){
        if(rows != 2) throw new UnsupportedOperationException("Calculating direction in radians only works on vectors in 2-space.");
        double magnitude = getMagnitude();
        double x = matrix[0][0];
        double y = matrix[1][0];
        if(x > 0){
            if(y>0){
                return Math.acos(x/magnitude);
            } else {
                return (2*Math.PI) - Math.acos(x/magnitude);
            }
        } else {
            return Math.PI - Math.asin(y/magnitude);
        }
    }

    public Vector add(Vector v){
        return super.add(v).toColumnVector();
    }

    public Vector subtract(Vector v){
        return super.subtract(v).toColumnVector();
    }

    public Vector multiplyByScalar(double scalar){
        return super.multiplyByScalar(scalar).toColumnVector();
    }

    public Vector multiply(Vector v){
        return super.multiply(v).toColumnVector();
    }

    public Vector divideByScalar(double scalar){
        return super.divideByScalar(scalar).toColumnVector();
    }

    /**
     * Rotates the vector given the angle, in radians
     * @param theta Angle to rotate, in radians (clockwise)
     * @return New vector
     */
    public Vector rotate(double theta){
        if(rows != 2) return null;
        return new Matrix(new double[][] {
                {Math.cos(theta), Math.sin(theta)},
                {-Math.sin(theta), Math.cos(theta)}
        }).multiply(this).toColumnVector();
    }

    public double dotProduct(Vector v){
        double[][] matrix = this.matrix;
        double[][] otherVector = v.matrix;
        double sum = 0;
        for(int i = 0; i<rows; i++){
            sum += matrix[i][0] * otherVector[i][0];
        }
        return sum;
    }

    public Vector crossProduct(Vector v){
        return null;
    }

    /**
     * Casts the vector to a 2D position, using the first 2 rows of the vector.
     * @return Result of the casting operation. Returns null if the vector had less than 2 rows.
     */
    public Position to2DPosition(){
        if(rows < 2) return null;
        return new Position(matrix[0][0], matrix[1][0]);
    }
}
