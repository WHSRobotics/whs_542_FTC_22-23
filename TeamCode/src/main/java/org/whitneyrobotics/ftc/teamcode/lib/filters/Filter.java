package org.whitneyrobotics.ftc.teamcode.lib.filters;

public interface Filter {
    void calculate(double newState);
    double getOutput();
}
