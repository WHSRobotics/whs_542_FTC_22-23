package org.whitneyrobotics.ftc.teamcode.lib.geometry;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import org.whitneyrobotics.ftc.teamcode.framework.Vector;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Class for storing positions on the field
 *
 * @see Coordinate - Alternative class, with heading
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class Position extends ReadOnlyPosition{

    public Position(double x, double y) {
        super(x,y);
    }

    public void setX(double x) {
        xPos = x;
    }

    public void setY(double y) {
        yPos = y;
    }
}
