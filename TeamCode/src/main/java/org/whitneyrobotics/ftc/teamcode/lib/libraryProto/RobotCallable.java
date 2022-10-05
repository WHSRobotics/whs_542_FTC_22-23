package org.whitneyrobotics.ftc.teamcode.lib.libraryProto;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Objects;
import java.util.function.Supplier;

@FunctionalInterface
@RequiresApi(api = Build.VERSION_CODES.N)
public interface RobotCallable {
    void dispatch();

    default RobotCallable andThen(RobotCallable next){
        Objects.requireNonNull(next, "You cannot pass in a null argument for a sequential operation. Use dispatch to call the operation.");
        return () -> {
            dispatch();
            next.dispatch();
        };
    }

    default RobotCallable andThenIf(Supplier<Boolean> condition, RobotCallable next){
        Objects.requireNonNull(next, "You cannot pass in a null argument for a sequential operation. Use dispatch to call the operation.");
        return () -> {
            dispatch();
            if (condition.get()) next.dispatch();
        };
    }

    default RobotCallable andThenIfElse(Supplier<Boolean> condition, RobotCallable nextTrue, RobotCallable nextFalse){
        Objects.requireNonNull(nextTrue, "You cannot pass in a null argument for a sequential operation. Use dispatch to call the operation.");
        Objects.requireNonNull(nextFalse, "You cannot pass in a null argument for a sequential operation. Use dispatch to call the operation.");
        return () -> {
            dispatch();
            if(condition.get()){
                nextTrue.dispatch();
            } else {
                nextFalse.dispatch();
            }
        };
    }

    @Documented
    @Target(value = ElementType.METHOD)
    @Retention(value = RetentionPolicy.RUNTIME)
    @interface UnsafeCaseWarningIgnore {
        String value() default "";
    }

    class MissingCaseException extends RuntimeException {
        MissingCaseException(Object reason){
            super(reason.toString());
        }
    }

    /**
     * Chain method similar to a switch case statement. The supplier will execute the case with the corresponding index, or throw an error if the index provider provided a case outside of range unless a suppression annotation is present.
     * @param caseSupplier Boolean supplier that provides the case at runtime. You can suppress warnings using {@link UnsafeCaseWarningIgnore}
     * @param cases Vararg parameter of cases to execute based on the case supplier
     * @return {@link RobotCallable} to chain the next actions to, or call {@link #dispatch()} on to execute the chain of commands.
     * @see UnsafeCaseWarningIgnore
     * @see RobotCallable
     */
    default RobotCallable andThenMatch(Supplier<Integer> caseSupplier, RobotCallable... cases){
        Objects.requireNonNull(caseSupplier, "You cannot pass in a null argument for a sequential operation. Use dispatch to call the operation.");
        for(int i = 0; i<cases.length; i++){ Objects.requireNonNull(cases[i], "You cannot pass in a null argument for a sequential operation. Use dispatch to call the operation."); }
        int caseProvided = caseSupplier.get();
        if(caseSupplier.getClass().getEnclosingMethod().getDeclaredAnnotation(UnsafeCaseWarningIgnore.class) == null) if(caseProvided > cases.length-1 || caseProvided < 0) throw new MissingCaseException("The case supplier provided case outside of the number of provided case handlers.");
        return () -> {
            dispatch();
            if(caseProvided < cases.length-1 || caseProvided >= 0){
                cases[caseProvided].dispatch();
            } else {
                cases[0].dispatch();
            }
        };
    }
}
