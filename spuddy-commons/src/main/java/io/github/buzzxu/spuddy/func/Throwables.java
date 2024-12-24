package io.github.buzzxu.spuddy.func;

/**
 * @author xux
 * @date 2022年07月16日 10:21
 */
public class Throwables {
    @FunctionalInterface
    public interface UnaryOperator<T> {

        T apply(T t) throws Throwable;

        static <T> UnaryOperator<T> identity() {
            return t -> t;
        }
    }

    @FunctionalInterface
    public interface Function<T, R> {

        R apply(T t) throws Throwable;

        static <T> Function<T, T> identity() {
            return t -> t;
        }
    }

    @FunctionalInterface
    public interface BiFunction<T, U, R> {

        R apply(T t, U u) throws Throwable;
    }

    @FunctionalInterface
    public interface Runnable {

        void run() throws Throwable;
    }

    @FunctionalInterface
    public interface Consumer<T> {

        void accept(T t) throws Throwable;
    }

    @FunctionalInterface
    public interface Supplier<R> {

        R get() throws Throwable;
    }
}
