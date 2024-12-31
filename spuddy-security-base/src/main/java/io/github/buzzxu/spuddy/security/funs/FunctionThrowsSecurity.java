package io.github.buzzxu.spuddy.security.funs;

import io.github.buzzxu.spuddy.errors.SecurityException;

/**
 * @author xux
 * @date 2024年12月28日 22:04:57
 */
@FunctionalInterface
public interface FunctionThrowsSecurity<T,R>{
    R apply(T input) throws SecurityException;
}
