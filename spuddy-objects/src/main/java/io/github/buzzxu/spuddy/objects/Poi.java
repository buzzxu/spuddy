package io.github.buzzxu.spuddy.objects;

import lombok.*;

/**
 * @author xux
 * @date 2023年03月21日 20:37:43
 */
@Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor @ToString
public class Poi {
    private String name;
    private String id;
    private String location;

    private Geo geo;
    private String type;
    private String typeCode;
    private String pName;
    private String cityName;
    private String adName;
    private String address;
    private String pCode;
    private String cityCode;
    private String adCode;
}
