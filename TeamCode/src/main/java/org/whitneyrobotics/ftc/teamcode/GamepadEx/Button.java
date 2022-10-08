package org.whitneyrobotics.ftc.teamcode.GamepadEx;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.function.Consumer;

@RequiresApi(api = Build.VERSION_CODES.N)
public class Button implements GamepadHardware{

    private Consumer<GamepadInteractionEvent> interactionEventConsumer = defaultConsumer;
    private Consumer<GamepadInteractionEvent> pressEventConsumer = defaultConsumer;
    private Consumer<GamepadInteractionEvent> releaseEventConsumer = defaultConsumer;
    private Consumer<GamepadInteractionEvent> doublePressEventConsumer = defaultConsumer;
    private Consumer<GamepadInteractionEvent> holdEventConsumer = defaultConsumer;
    private boolean lastState = false;
    private Long lastChanged = null;

    @Override
    public void onInteraction(@NonNull Consumer<GamepadInteractionEvent> callback) {
        this.interactionEventConsumer = callback;
    }

    public void onPress(@NonNull Consumer<GamepadInteractionEvent> callback){
        this.pressEventConsumer = callback;
    }

    public void onRelease(@NonNull Consumer<GamepadInteractionEvent> callback){
        this.releaseEventConsumer = callback;
    }

    public void onDoublePress(@NonNull Consumer<GamepadInteractionEvent> callback){
        this.doublePressEventConsumer = callback;
    }

    public void onButtonHold(@NonNull Consumer<GamepadInteractionEvent> callback){
        this.holdEventConsumer = callback;
    }

    public Button(){
        this(false);
    }

    public Button(boolean initialState){
        lastState = initialState;
        lastChanged = System.currentTimeMillis();
    }

    public Boolean value(){
        return lastState;
    }

    @Override
    public void update(Object newState) {
        GamepadInteractionEvent event = new GamepadInteractionEvent((boolean)newState, null, lastChanged);
        if(lastState != (boolean)newState){
            interactionEventConsumer.accept(event);
            if((boolean)newState) {
                pressEventConsumer.accept(event);
            } else {
                releaseEventConsumer.accept(event);
            }
        }
        lastChanged = System.currentTimeMillis();
        lastState = (boolean)newState;
    }

    public void removeInteractionHandler(){
        this.interactionEventConsumer = defaultConsumer;
    }

    public void removePressHandler(){
        this.pressEventConsumer = defaultConsumer;
    }

    public void removeReleaseHandler(){
        this.releaseEventConsumer = defaultConsumer;
    }

    public void removeDoublePressHandler(){
        this.releaseEventConsumer = defaultConsumer;
    }

    public void removeHoldHandler(){
        this.releaseEventConsumer = defaultConsumer;
    }

    @Override
    public void disconnectAllHandlers(){
        interactionEventConsumer = defaultConsumer;
        pressEventConsumer = defaultConsumer;
        releaseEventConsumer = defaultConsumer;
        doublePressEventConsumer = defaultConsumer;
        holdEventConsumer = doublePressEventConsumer;
    }
}
