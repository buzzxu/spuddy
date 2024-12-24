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
public class Tuple3<T1,T2,T3> implements Serializable {

    private T1 t1;
    private T2 t2;
    private T3 t3;

    private Tuple3(T1 t1, T2 t2, T3 t3) {
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
    }

    @JsonCreator
    public static <T1,T2,T3> Tuple3<T1,T2,T3> of(@JsonProperty("t1")T1 t1, @JsonProperty("t2")T2 t2, @JsonProperty("t3")T3 t3){
        return new Tuple3<>(t1,t2,t3);
    }


}
