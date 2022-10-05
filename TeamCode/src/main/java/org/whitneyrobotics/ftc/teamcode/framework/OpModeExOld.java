package org.whitneyrobotics.ftc.teamcode.framework;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import java.util.LinkedList;

public abstract class OpModeExOld extends OpMode {

    private static class QueueableAction{
        private Callback action;
        private int timeout;

        private QueueableAction(Callback cb, int timeout){
            action = cb;
            this.timeout = timeout;
        }

        private void invoke(){ action.exec(); }

        @FunctionalInterface
        public interface Callback{
            void exec();
        }
    }

    private LinkedList<QueueableAction.Callback> actionQueue;

    abstract public void init();

    @Override
    public void loop(){
        eventLoop();
        addCallback(() -> System.out.println("hi"),2000);
    }

    public abstract void eventLoop();

    public void addCallback(QueueableAction.Callback cb, int timeout){

    }

    //for the memes
    //public State<T> useState()
}
