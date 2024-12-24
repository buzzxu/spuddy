package io.github.buzzxu.spuddy.util;


import com.google.common.collect.Lists;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

public class Iterables {
    public static <E> void forEach(
            Iterable<? extends E> elements, BiConsumer<Integer, ? super E> action) {
        Objects.requireNonNull(elements);
        Objects.requireNonNull(action);

        int index = 0;
        for (E element : elements) {
            action.accept(index++, element);
        }
    }

    public static List<List<String>> partition(List<String> list , int groupSize){
        int length = list.size();
        // 计算可以分成多少组
        int num = (length + groupSize - 1)/groupSize;
        List<List<String>> newList = Lists.newArrayListWithCapacity(num);
        for (int i = 0; i < num; i++) {
            // 开始位置
            int fromIndex = i * groupSize;
            // 结束位置
            int toIndex = (i+1) * groupSize < length ? ( i+1 ) * groupSize : length ;
            newList.add(list.subList(fromIndex,toIndex)) ;
        }
        return  newList ;
    }
}
