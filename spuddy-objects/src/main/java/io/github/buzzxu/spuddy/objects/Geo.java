package io.github.buzzxu.spuddy.objects;

import lombok.*;

/**
 * @author xux
 * @date 2023年03月21日 19:01:33
 */
@Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor @ToString
public class Geo {
    /**
     * 经度
     */
    private String longitude;
    /**
     * 纬度
     */
    private String latitude;

    /**
     * geohash
     */
    private String hash;
}
