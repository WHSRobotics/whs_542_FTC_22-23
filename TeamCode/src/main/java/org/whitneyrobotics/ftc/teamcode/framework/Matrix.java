package org.whitneyrobotics.ftc.teamcode.framework;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.Arrays;
import java.util.HashMap;

@RequiresApi(api = Build.VERSION_CODES.N)
public class Matrix {
    private int rows;
    private int columns;

    private double[][] matrix;

    public Matrix(double[][] matrix){
        validate(matrix);
        rows = matrix.length;
        columns = matrix[0].length;
        this.matrix = matrix;
    }

    /**
     * A validation method for the matrix. Asserts that there is at least 1 row and 1 column, and that the rows are of uniform length.
     * @param matrix The matrix to validate.
     * @throws IllegalArgumentException if the matrix has less than 1 row or 1 columnm or does not have uniform length
     */
    private void validate(double[][] matrix){
        if(matrix.length < 1) throw new IllegalArgumentException("Matrices must have at least 1 row.");
        int columns = matrix[0].length;
        if (columns < 1) throw new IllegalArgumentException("Matrices must have at least 1 column.");
        for(double[] row : matrix){
            if(row.length != columns) throw new IllegalArgumentException("Matrix does not have uniform length");
        }
    }

    public double[][] getMatrix(){
        return matrix;
    }

    private double get(int row, int column){
        return matrix[row][column];
    }

    public static double dot(double[] vector1, double[] vector2){
        double result = 0.0d;
        for(int i = 0; i<vector1.length; i++){
            result += vector1[i] * vector2[i];
        }
        return result;
    }

    public Matrix multiplyByScalar(double scalar){
        double[][] result = new double[rows][columns];
        for(int i = 0; i<rows; i++){
            for(int j = 0; j<columns; j++){
                result[i][j] = matrix[i][j] * scalar;
            }
        }
        return new Matrix(result);
    }

    public Matrix multiplyCurrentMatrixByMatrix(Matrix b){
        if((rows != b.columns) || (columns != b.rows)) return null;
        double[][] result = new double[rows][columns];
        for(int i = 0; i<rows; i++){
            for(int j = 0; j<columns; j++){
                double[] vectorB = new double[columns];
                //iteration for getting matrix values from second factor
                for(int k = 0; k<columns; k++){
                    vectorB[k] = b.get(k,i);
                }
                result[i][j] = dot(matrix[i],vectorB);
            }
        }
        return new Matrix(result);
    }

    public static Matrix multiplyMatrixByMatrix(Matrix a, Matrix b){
        return a.multiplyCurrentMatrixByMatrix(b);
    }

    /**
     * Returns a hash code value for the object. This method is
     * supported for the benefit of hash tables such as those provided by
     * {@link HashMap}.
     * <p>
     * The general contract of {@code hashCode} is:
     * <ul>
     * <li>Whenever it is invoked on the same object more than once during
     *     an execution of a Java application, the {@code hashCode} method
     *     must consistently return the same integer, provided no information
     *     used in {@code equals} comparisons on the object is modified.
     *     This integer need not remain consistent from one execution of an
     *     application to another execution of the same application.
     * <li>If two objects are equal according to the {@code equals(Object)}
     *     method, then calling the {@code hashCode} method on each of
     *     the two objects must produce the same integer result.
     * <li>It is <em>not</em> required that if two objects are unequal
     *     according to the {@link Object#equals(Object)}
     *     method, then calling the {@code hashCode} method on each of the
     *     two objects must produce distinct integer results.  However, the
     *     programmer should be aware that producing distinct integer results
     *     for unequal objects may improve the performance of hash tables.
     * </ul>
     * <p>
     * As much as is reasonably practical, the hashCode method defined by
     * class {@code Object} does return distinct integers for distinct
     * objects. (This is typically implemented by converting the internal
     * address of the object into an integer, but this implementation
     * technique is not required by the
     * Java&trade; programming language.)
     *
     * @return a hash code value for this object.
     * @see Object#equals(Object)
     * @see System#identityHashCode
     */

    @Override
    public int hashCode() {
        int sum = 0;
        for(double[] row : matrix){
            for(double d: row) sum += d;
        }
        return sum*(rows+columns);
    }

    /**
     * Returns a string representation of the object. In general, the
     * {@code toString} method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p>
     * The {@code toString} method for class {@code Object}
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `{@code @}', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return a string representation of the object.
     */
    @NonNull
    @Override
    public String toString() {
        String res = String.format("%sX%s Matrix",rows,columns);
        for(double[] row : matrix){
            res += "\n[";
            for(double cell : row){
                res += " " + cell;
            }
            res += " ]";
        }
        return res;
    }
}
