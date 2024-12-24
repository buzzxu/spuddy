package io.github.buzzxu.spuddy.util;




import io.github.buzzxu.spuddy.func.Throwables;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static java.util.Optional.ofNullable;
/**
 * @author xux
 * @date 2022年07月16日 10:24
 */
public class Unchecked {
    private Unchecked() {
    }

    public static <T extends Throwable, E> E rethrow(Throwable throwable) throws T {
        return rethrow(throwable, UnaryOperator.identity());
    }

    public static <T extends Throwable, E> E rethrow(Throwable throwable, UnaryOperator<Throwable> rethrower) throws T {
        throw (T) ofNullable(rethrower.apply(throwable)).orElse(throwable);
    }

    public static <R> Supplier<R> uncheckedSupplier(Throwables.Supplier<R> supplier) {
        return () -> unchecked(supplier);
    }

    public static <R> R unchecked(Throwables.Supplier<R> supplier) {
        return unchecked(supplier, UnaryOperator.identity());
    }

    public static <R> R unchecked(Throwables.Supplier<R> supplier, UnaryOperator<Throwable> rethrower) {
        try {
            return supplier.get();
        } catch (Throwable t) {
            rethrow(t, rethrower);
        }
        throw new RuntimeException("unreachable");
    }

    public static Runnable uncheckedRunnable(Throwables.Runnable runnable) {
        return () -> unchecked(runnable);
    }

    public static void unchecked(Throwables.Runnable runnable) {
        unchecked(runnable, UnaryOperator.identity());
    }

    public static void unchecked(Throwables.Runnable runnable, UnaryOperator<Throwable> rethrower) {
        try {
            runnable.run();
        } catch (Throwable t) {
            rethrow(t, rethrower);
        }
    }

}
