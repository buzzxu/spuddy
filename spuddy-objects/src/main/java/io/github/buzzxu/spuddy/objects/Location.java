package io.github.buzzxu.spuddy.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author xux
 * @date 2023年05月11日 21:13:48
 */
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class Location {
    /**
     * 纬度
     */
    private double latitude;
    /**
     * 经度
     */
    private double longitude;
    /**
     * 省
     */
    private String province;
    /**
     * 市
     */
    private String city;
    /**
     * 区
     */
    private String district;
}
