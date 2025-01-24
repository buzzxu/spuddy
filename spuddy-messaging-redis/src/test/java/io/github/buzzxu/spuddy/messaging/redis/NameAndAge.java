package io.github.buzzxu.spuddy.messaging.redis;

import lombok.*;

/**
 * @author xux
 * @date 2025年01月22日 20:33:43
 */
@ToString
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
public class NameAndAge {
    private String name;
    private int age;
}
