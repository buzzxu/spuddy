package io.github.buzzxu.spuddy.objects;

import lombok.*;

/**
 * @program: 
 * @description:
 * @author: 徐翔
 * @create: 2020-02-16 22:05
 **/
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter @EqualsAndHashCode @ToString
public class KV<K,V> {

    private K key;
    private V value;

    public static <K,V> KV<K,V> of(K key,V value){
        return new KV<>(key,value);
    }
}
