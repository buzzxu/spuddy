package io.github.buzzxu.spuddy.objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author 徐翔
 * @since 2021-10-13 22:07
 **/
@NoArgsConstructor @Getter @ToString @EqualsAndHashCode
public class Tuple4<T1,T2,T3,T4> implements Serializable {

    private T1 t1;
    private T2 t2;
    private T3 t3;
    private T4 t4;

    private Tuple4(T1 t1, T2 t2, T3 t3,T4 t4) {
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
        this.t4 = t4;
    }

    @JsonCreator
    public static <T1,T2,T3,T4> Tuple4<T1,T2,T3,T4> of(@JsonProperty("t1") T1 t1, @JsonProperty("t2")T2 t2, @JsonProperty("t3")T3 t3, @JsonProperty("t4")T4 t4){
        return new Tuple4<>(t1,t2,t3,t4);
    }

}
