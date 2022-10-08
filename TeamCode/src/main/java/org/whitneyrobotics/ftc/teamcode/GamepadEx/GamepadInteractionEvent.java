package org.whitneyrobotics.ftc.teamcode.GamepadEx;

public class GamepadInteractionEvent {
    public final Boolean boolVal;
    public final Float floatVal;
    public final Long lastInteraction;

    public GamepadInteractionEvent(Boolean boolVal, Float floatVal, Long lastInteraction) {
        this.boolVal = boolVal;
        this.floatVal = floatVal;
        this.lastInteraction = lastInteraction;
    }
}
