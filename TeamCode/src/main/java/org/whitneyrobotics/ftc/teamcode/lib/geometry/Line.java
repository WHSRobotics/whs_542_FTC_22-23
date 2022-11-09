package org.whitneyrobotics.ftc.teamcode.lib.geometry;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.whitneyrobotics.ftc.teamcode.lib.util.Functions;

@RequiresApi(api = Build.VERSION_CODES.N)
public class Line {
    public Position endpoint1;
    public Position endpoint2;
    public Line(Position endpoint1, Position endpoint2){
        this.endpoint1 = endpoint1;
        this.endpoint2 = endpoint2;
    }

    public double length(){
        return Functions.calculateDistance(endpoint1,endpoint2);
    }
}
