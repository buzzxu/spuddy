package io.github.buzzxu.spuddy.func;

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @program: yuanmai-platform
 * @description:
 * @author: 徐翔
 * @create: 2020-02-15 18:03
 **/
public interface Tree<I extends Number> {

    int id();

    int parentId();

    int sorted();

    List<? extends Tree<I>> children();

    void initChildren();




    static <T extends Tree> List<T> tree(List<T> source){
        return tree(0,source);
    }

    static <I extends Number,T extends Tree> List<T> tree(I parentId, List<T> source){
        if ( source == null) {
            return Collections.emptyList();
        }
        return tree(parentId,source,(left, rigth) -> {
            if(left.sorted() > rigth.sorted()){
                return -1;
            }else if(left.sorted() < rigth.sorted()){
                return 1;
            }else{
                return 0;
            }
        });
    }

    static <I extends Number,T extends Tree> List<T> tree(I parentId, List<T> source, Comparator<T> comparator){
        Collections.sort(source, comparator);
        return recursive(parentId,source);
    }

    private static <I extends Number,T extends Tree> List<T> recursive(I parentId, List<T> source){
        List<T> trees = Lists.newArrayList();
        for(T tree : source){
            if(parentId.equals(tree.parentId())){
                trees.add(find(tree,source));
            }
        }
        return trees;
    }

    private static <T extends Tree> T find(T parent, List<T> nodes){
        for(T tree : nodes){
            if (parent.id() == tree.parentId()){
                if (parent.children()  == null){
                    parent.initChildren();
                }
                parent.children().add(find(tree,nodes));
            }
        }
        return parent;
    }
}
