package io.github.buzzxu.spuddy.objects;

import lombok.*;

import java.util.Collections;
import java.util.List;

/**
 * @author xux
 * @date 2023年03月29日 10:31:05
 */
@ToString
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class District {
    private String name;
    private String level;
    /**
     * 区域编码
     */
    private String adCode;
    /**
     * 城市编码
     */
    private String cityCode;

    private String[] geo;

    private List<District> children;

    public Pair<String,String> to(){
        return Pair.of(adCode+"000000",name);
    }

    public List<Pair<String,String>> all(){
        if(children != null){
            return children.stream().map(v-> v.to()).toList();
        }
        return Collections.emptyList();
    }

    public List<District> find(String cityCode){
        if(children != null){
            return children.stream().filter(v-> v.adCode.equals(cityCode)).map(v->v.children).findFirst().orElse(Collections.emptyList());
        }
        return Collections.emptyList();
    }

    public List<Pair<String,String>> findDistrict(String cityCode){
        return find(cityCode.substring(0,6)).stream().map(District::to).toList();
    }
}
