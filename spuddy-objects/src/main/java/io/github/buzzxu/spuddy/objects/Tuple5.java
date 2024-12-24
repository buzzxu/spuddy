package io.github.buzzxu.spuddy.objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author xux
 * @date 2023年02月02日 16:04:21
 */
@NoArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class Tuple5<T1,T2,T3,T4,T5> implements Serializable {
    private T1 t1;
    private T2 t2;
    private T3 t3;
    private T4 t4;
    private T5 t5;

    private Tuple5(T1 t1, T2 t2, T3 t3,T4 t4,T5 t5) {
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
        this.t4 = t4;
        this.t5 = t5;
    }

    @JsonCreator
    public static <T1,T2,T3,T4,T5> Tuple5<T1,T2,T3,T4,T5> of(@JsonProperty("t1")T1 t1, @JsonProperty("t2")T2 t2, @JsonProperty("t3")T3 t3, @JsonProperty("t4")T4 t4, @JsonProperty("t5")T5 t5){
        return new Tuple5<>(t1,t2,t3,t4,t5);
    }
}
