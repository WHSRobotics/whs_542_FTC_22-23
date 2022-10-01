package org.whitneyrobotics.ftc.teamcode.lib.util.Queue;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.whitneyrobotics.ftc.teamcode.subsys.WHSRobotImpl;

import java.util.function.*;

@RequiresApi(api = Build.VERSION_CODES.N)
public class QueueItem {

    public enum ProcessMode {
        LINEAR, ITERATIVE //use Linear if OpMode, Iterative if LinearOpMode
    }

    public enum QueueItemState {
        PENDING, PROCESSING, COMPLETED
    }

    public static void setDefaultProcessMode(ProcessMode mode){
        defaultMode = mode;
    }

    public static ProcessMode defaultMode = ProcessMode.LINEAR;

    public boolean async = false;
    private boolean processed = false;
    public ProcessMode processMode;
    public QueueItemState state = QueueItemState.PENDING;

    public RobotAction action;
    public Supplier exitCondition;

    public QueueItem(RobotAction action, Supplier exitCondition, boolean async) {
        this.action = action;
        this.exitCondition = exitCondition;
        this.async = async;
        processMode = defaultMode;
    }

    public QueueItem setMode(ProcessMode mode){
        this.processMode = mode;
        return this;
    }

    public boolean process(){
        state = QueueItemState.PROCESSING;
        switch(processMode){
            case LINEAR:
                if(!processed){
                    action.invoke();
                }
                processed = (boolean)exitCondition.get();
                break;
            case ITERATIVE:
                do {
                    action.invoke();
                    processed = (boolean)exitCondition.get();
                } while(!processed);
                break;
            default:
                throw new IllegalArgumentException("Specified Process Mode does not exist for QueueItem.ProcessMode enum");
        }
        if(processed){
            state = QueueItemState.COMPLETED;
        }
        return processed;
    }

    public boolean isAlive(){return !processed;}

    public boolean isProcessed(){return processed;}

    public QueueItemState getState(){return state;}
}
