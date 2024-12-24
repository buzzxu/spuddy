package io.github.buzzxu.spuddy.objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * @author xux
 * @date 2024年04月25日 12:18:34
 */
@Getter
@EqualsAndHashCode
public class Pairλ<K, V> implements Serializable {

    private final K key;
    private final V value;
    @Setter
    private  List<Pairλ<K,V>> childs;


    public static <K, V> Pairλ<K, V> of(K k, V v,List<Pairλ<K, V>> childs) {
        return new Pairλ<>(k, v,childs);
    }
    public static <K, V> Pairλ<K, V> of(Pair<K,V> pair) {
        return new Pairλ<>(pair.getKey(),pair.getValue(), null);
    }




    @JsonCreator
    public Pairλ(@JsonProperty("key") K k, @JsonProperty("value") V v, @JsonProperty("childs")List<Pairλ<K, V>> childs) {
        this.key = k;
        this.value = v;
        this.childs = childs;
    }

    @Override
    public String toString() {
        return "Pairλ " + key + " : " + value;

    }
}
