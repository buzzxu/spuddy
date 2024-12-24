package io.github.buzzxu.spuddy.objects;

/**
 * @program: 
 * @description:
 * @author: 徐翔
 * @create: 2020-02-09 13:14
 **/
public interface Model extends Argument {

    default <D extends Id> D to() {
        throw new UnsupportedOperationException();
    }

    default <D extends Id> void from(D dto) {
        throw new UnsupportedOperationException();
    }
}
