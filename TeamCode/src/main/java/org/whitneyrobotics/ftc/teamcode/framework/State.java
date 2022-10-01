package org.whitneyrobotics.ftc.teamcode.framework;

import java.util.function.Consumer;

public class State<T> {
    T value;
    String name;
    public void setter(T value){
        this.value = value;
    }

    public void State(T initValue){
        value=initValue;
    }

    public Consumer<T> getSetter(){
        return this::setter;
    }
}
