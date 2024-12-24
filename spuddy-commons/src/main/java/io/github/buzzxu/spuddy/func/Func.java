package io.github.buzzxu.spuddy.func;

import java.util.Objects;

public interface Func {

    @FunctionalInterface
    interface Consumer2<T1,T2> {

        void accept(T1 t,T2 e);

        default Consumer2<T1,T2> andThen(Consumer2<? super T1,? super T2> after) {
            Objects.requireNonNull(after);
            return (T1 t,T2 e) -> { accept(t,e); after.accept(t,e); };
        }
    }

    @FunctionalInterface
    interface Consumer3<T1,T2,T3> {

        void accept(T1 t,T2 e,T3 x);

        default Consumer3<T1,T2,T3> andThen(Consumer3<? super T1,? super T2,? super T3> after) {
            Objects.requireNonNull(after);
            return (T1 t,T2 e,T3 x) -> { accept(t,e,x); after.accept(t,e,x); };
        }
    }

    @FunctionalInterface
    interface ConsumerPair<T,E>{

        void accept(T t,E e);
        default ConsumerPair<T,E> andThen(ConsumerPair<? super T,? super E> after) {
            Objects.requireNonNull(after);
            return (T t,E e) -> { accept(t,e); after.accept(t,e); };
        }
    }

    @FunctionalInterface
    interface ConsumerThrow<T,E extends Exception> {

        void accept(T t) throws E;

        default ConsumerThrow<T,E> andThen(ConsumerThrow<? super T,E> after) {
            Objects.requireNonNull(after);
            return (T t) -> { accept(t); after.accept(t); };
        }
    }

    @FunctionalInterface
    interface FunctionThrow<T, R,E extends Exception> {


        R apply(T t)throws E;


        default <V> FunctionThrow<V, R,E> compose(FunctionThrow<? super V, ? extends T,E> before)throws E {
            Objects.requireNonNull(before);
            return (V v) -> apply(before.apply(v));
        }

        default <V> FunctionThrow<T, V,E> andThen(FunctionThrow<? super R, ? extends V,E> after)throws E {
            Objects.requireNonNull(after);
            return (T t) -> after.apply(apply(t));
        }
    }

}
