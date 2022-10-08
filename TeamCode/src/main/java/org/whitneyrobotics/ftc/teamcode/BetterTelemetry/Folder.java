package org.whitneyrobotics.ftc.teamcode.BetterTelemetry;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Supplier;

@RequiresApi(api = Build.VERSION_CODES.N)
public class Folder extends LineItem.Interactable {

    private ArrayList<LineItem> nested;

    private Supplier<Boolean> stateProvider = () -> true;

    public Folder(String caption, Supplier<Boolean> stateProvider, LineItem... nestedLines) {
        super(caption);
        this.setPersistent(true);
        nested = new ArrayList<>(Arrays.asList(nestedLines));
        this.stateProvider = stateProvider;
    }

    public void addChild(LineItem child){
        nested.add(child);
    }

    public void removeChildByItem(LineItem child){
        nested.remove(child);
    }

    public void removeNthChild(int index){
        nested.remove(index);
    }

    public void removeChildByCaption(String caption){
        nested.removeIf(item -> item.caption == caption);
    }


    @Override
    public void reset() {

    }

    @Override
    protected String format(boolean blink) {
        if(stateProvider.get()){
            String result = "↓ " + this.caption;
            for(LineItem item : nested) {
                item.isVisible = true;
                result += String.format("<br> |  %s", item.render(blink));
            }
            return result;
        }
        for(LineItem item : nested) {
            item.isVisible = false;
        }
        return "> " + this.caption;
    }

    @Override
    public void focus() {

    }
}
