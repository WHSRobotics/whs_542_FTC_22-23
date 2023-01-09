package org.whitneyrobotics.ftc.teamcode.autoop.AutoSetupTesting;

import com.qualcomm.hardware.rev.Rev2mDistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.whitneyrobotics.ftc.teamcode.GamepadEx.GamepadEx;

public class Tests {

    public static void assertTrue(Boolean b) throws AssertionError {if (!b) throw new AssertionError("Assertion failed"); }
    public static void assertFalse(Boolean b) throws AssertionError {if (b) throw new AssertionError("Assertion failed"); }
    public static void assertNull(Object o) throws AssertionError {if (o != null) throw new AssertionError("Assertion failed"); }
    public static void assertNotNull(Object o) throws AssertionError {if (o == null) throw new AssertionError("Assertion failed"); }
    /**
     * Asserts if two values are the same.
     * @param d1
     * @param d2
     * @throws AssertionError if the two values are not equal
     */
    public static void assertEquals(double d1, double d2) throws AssertionError{
        if (d1 != d2) throw new AssertionError(String.format("%d is not equal to %d",d1,d2));
    }

    /**
     * Asserts if two values are the within 7 decimal points of each other.
     * @param d1
     * @param d2
     * @throws AssertionError if the two values are not almost equal
     */
    public static void assertAlmostEquals(double d1, double d2) throws AssertionError{
        if (Math.abs(d1-d2) > 1e-7) throw new AssertionError(String.format("%d is not almost equal to %d",d1,d2));
    }

    /**
     * Asserts if two decimal points are within 3 decimal points of each other.
     * @param d1
     * @param d2
     * @throws AssertionError if the two values are not weakly equal
     */
    public static void assertWeakEquality(double d1, double d2) throws AssertionError{
        if (Math.abs(d1-d2) > 1e-3) throw new AssertionError(String.format("%d is not weakly equal to %d",d1,d2));
    }

    /**
     * Asserts if two decimal points are within 3 decimal points of each other.
     * @param d1
     * @param d2
     * @throws AssertionError if the two values are not almost equal
     */
    public static void assertVariableEquality(double d1, double d2, int significantFigures) throws AssertionError{
        if (Math.abs(d1 - d2) > Math.pow(1,-significantFigures)) throw new AssertionError(String.format("%d is not equal to %d by %d sig figs",d1,d2, significantFigures));
    }

    public static void assertGamepadSetup(GamepadEx gamepad, String label) throws AssertionError {
        if (gamepad.hasError) throw new AssertionError(label + " was not setup correctly. Ensure it is plugged in to the DS.");
    }

    public static void assertDistanceInRange(Rev2mDistanceSensor sensor, DistanceUnit unit, double min, double max) throws AssertionError{
        assertNotNull(sensor);
        double measurement = sensor.getDistance(unit);
        if(measurement < Math.abs(min) || measurement > Math.abs(max)) throw new AssertionError(String.format("%d was not in expected range of %d-%d. Actual: %d", sensor.getDeviceName(), Math.abs(min), Math.abs(max),measurement));
    }

}
