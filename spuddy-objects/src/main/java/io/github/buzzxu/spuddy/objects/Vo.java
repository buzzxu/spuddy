package io.github.buzzxu.spuddy.objects;

/**
 * @program: 
 * @description:
 * @author: 徐翔
 * @create: 2020-02-09 13:52
 **/
public interface Vo<DTO extends Id> extends Argument {


    default void from(DTO dto) {

    }


}
