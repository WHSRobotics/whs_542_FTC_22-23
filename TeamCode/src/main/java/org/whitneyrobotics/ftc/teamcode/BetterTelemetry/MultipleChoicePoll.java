package org.whitneyrobotics.ftc.teamcode.BetterTelemetry;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

@RequiresApi(api = Build.VERSION_CODES.N)
public class MultipleChoicePoll extends LineItem.Interactable {

    @Override
    public void focus() {
        focused = true;
    }

    public static class MultipleChoiceOption<T> {
        private String caption;
        private T value;
        private boolean disabled;

        public MultipleChoiceOption(String caption, T value){
            this.caption = caption;
            this.value = value;
        }
    }

    private String prompt;
    private MultipleChoiceOption[] options;
    private ArrayList<MultipleChoiceOption> selected = new ArrayList<>();
    private Consumer<MultipleChoiceOption[]> onChangeCallback;
    private boolean multiSelectAllowed = false;
    private boolean focused = false;

    public MultipleChoicePoll(String prompt, boolean multiSelectAllowed, Color color, MultipleChoiceOption option1, MultipleChoiceOption... options){
        super(prompt, color);
        MultipleChoiceOption[] newOptions = new MultipleChoiceOption[options.length + 1];
        newOptions[0] = option1;
        for(int i = 1; i<=options.length; i++){newOptions[i] = options[i-1];}
        this.prompt = prompt;
        this.options = newOptions;
        this.multiSelectAllowed = multiSelectAllowed;
    }

    public MultipleChoicePoll(String prompt, boolean multiSelectAllowed, MultipleChoiceOption option1, MultipleChoiceOption... options){
        this(prompt, multiSelectAllowed, Color.WHITE, option1, options);
    }

    public Consumer<MultipleChoiceOption> useMultipleChoiceAdder(){
        return this::addOption;
    }

    public static Consumer<MultipleChoiceOption> useMultipleChoiceAdder(MultipleChoicePoll poll){
        return poll::addOption;
    }

    public void addOption(MultipleChoiceOption option){
        MultipleChoiceOption[] newOptions = new MultipleChoiceOption[options.length + 1];
        System.arraycopy(options,0,newOptions,0,options.length);
        newOptions[options.length] = option;
        options = newOptions;
    }

    @Override
    public void reset() {
        selected.clear();
        if(!multiSelectAllowed) selected.add(options[1]);
    }

    @Override
    protected String format(boolean blink) {
        return null;
    }

    private void select(){
        onChangeCallback.accept(options);
    }

    public void onChange(Consumer<MultipleChoiceOption[]> callback) {this.onChangeCallback = callback;}
}
