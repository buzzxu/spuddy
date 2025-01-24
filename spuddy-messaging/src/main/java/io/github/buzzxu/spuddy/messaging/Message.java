package io.github.buzzxu.spuddy.messaging;

import io.github.buzzxu.spuddy.jackson.Jackson;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author 徐翔
 * @since 2021-10-16 18:55
 **/
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class Message<T> {
    private String key;
    private String topic;
    private T data;
    private long delay;
    private LocalDateTime deliverTime;
    private String group;
    @Builder.Default
    private TimeUnit timeUnit = TimeUnit.SECONDS;

}
