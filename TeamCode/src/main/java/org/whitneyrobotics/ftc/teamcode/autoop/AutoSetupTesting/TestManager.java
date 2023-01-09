package org.whitneyrobotics.ftc.teamcode.autoop.AutoSetupTesting;

import org.checkerframework.checker.units.qual.A;
import org.whitneyrobotics.ftc.teamcode.BetterTelemetry.BetterTelemetry;
import org.whitneyrobotics.ftc.teamcode.BetterTelemetry.Folder;
import org.whitneyrobotics.ftc.teamcode.BetterTelemetry.LineItem;
import org.whitneyrobotics.ftc.teamcode.BetterTelemetry.TextLine;

import java.util.ArrayList;
import java.util.LinkedList;

public class TestManager {

    BetterTelemetry telemetry;

    public static class Test {
        String label;
        Runnable func;

        boolean failed = false;
        String reason = "";

        public Test(String label, Runnable func){
            this.label = label;
            this.func = func;
        }

        public void evaluate() {
            try {
                func.run();
                failed = false;
                reason = "✓ Test passed";
            } catch(AssertionError e) {
                failed = true;
                reason = e.getLocalizedMessage();
            }
        }
    }

    LinkedList<Test> tests = new LinkedList<>();
    Folder folder;
    public TestManager(BetterTelemetry bt){
        folder = new Folder("Test Manager");
        telemetry.addItem(folder);
        this.telemetry = bt;
    }

    public TestManager addTest(String label, Runnable func){
        tests.push(new Test(label, func));
        return this;
    }

    /**
     * Call in a looping context.
     */
    public void run() {
        folder.purge();
        for (Test t: tests) {
            t.evaluate();
            folder.addChild(new TextLine(
                    t.label + (t.failed ? " FAILED" : " Passed") + "\n" + t.reason,
                    true,
                    t.failed ? LineItem.Color.RED : LineItem.Color.LIME,
                    LineItem.RichTextFormat.BOLD)
            );
        }
    }
}
