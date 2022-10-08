package org.whitneyrobotics.ftc.teamcode.lib.libraryProto;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.function.Consumer;

@RequiresApi(api = Build.VERSION_CODES.N)
public class TestRobotCallable {
    public void run(RobotCallable callable){
        callable.dispatch();
    }

    @RobotCallable.UnsafeCaseWarningIgnore
    private int supplier(){
        return (int)Math.floor(Math.random()*5);
    }

    public void test(){
        RobotCallable first = () -> System.out.println("1");

        first.andThen(
                () -> System.out.println("2")
        ).andThen(
                () -> System.out.println("3")
        ).andThenIf(() -> true,
                () -> System.out.println("4")
        ).andThenIfElse(() -> true,
                () -> System.out.println("5"),
                () -> System.out.println("6")
        ).andThenMatch(this::supplier,
                () -> System.out.println("Random number was 0"),
                () -> System.out.println("Random number was 1"),
                () -> System.out.println("Random number was 2"),
                () -> System.out.println("Random number was 3"),
                () -> System.out.println("Random number was 4")
                ).dispatch();
    }
}
