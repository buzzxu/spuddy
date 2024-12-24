package io.github.buzzxu.spuddy.objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @program: 
 * @description: 区域
 * @author: xuxiang
 * @create: 2021-01-11 21:10
 **/
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor @ToString
public class Region {
    private String provinceId;
    private String province;
    private String cityId;
    private String city;
    private String county;
    private String countyId;
    private String district;
    private String town;
    private String townId;
    private String village;
    private String[] geo;
    private String address;

    public Region(String provinceId) {
        this.provinceId = provinceId;
    }

    public Region(String provinceId, String province) {
        this(provinceId);
        this.province = province;
    }

    public Region(String provinceId, String province,String cityId,String city) {
        this(provinceId, province);
        this.cityId = cityId;
        this.city = city;
    }

    public static Region of(String provinceId) {
        return new Region(provinceId);
    }

    public static Region of(String provinceId, String province) {
        return new Region(provinceId, province);
    }
    public static Region ofID(String provinceId, String cityId,String countryId) {
        Region region = new Region(provinceId);
        region.cityId = cityId;
        region.countyId = countryId;
        return region;
    }
    public static Region ofID(String provinceId, String cityId,String countryId,String address) {
        Region region = ofID(provinceId, cityId, countryId);
        region.address = address;
        return region;
    }
    public Region provinceId(String provinceId) {
        this.provinceId = provinceId;
        return this;
    }

    public Region cityId(String cityId) {
        this.cityId = cityId;
        return this;
    }
    public Region countyId(String countyId){
        this.countyId = countyId;
        return this;
    }
    public Region townId(String townId){
        this.townId = townId;
        return this;
    }

    public Region province(String province) {
        this.province = province;
        return this;
    }

    public Region city(String city) {
        this.city = city;
        return this;
    }

    public Region county(String county) {
        this.county = county;
        return this;
    }

    public Region town(String town) {
        this.town = town;
        return this;
    }

    @JsonIgnore
    public String longitude(){
        return geo != null ? geo[0] : "";
    }
    @JsonIgnore
    public String latitude(){
        return geo != null ? geo[1] : "";
    }

    private static final String regex="(?<province>[^省]+自治区|.*?省|.*?行政区|.*?市)(?<city>[^市]+自治州|.*?地区|.*?行政单位|.+盟|市辖区|.*?市|.*?县)(?<county>[^县]+县|.+镇|[^区]+区|[^街道]+街道|.+市|.+旗|.+海域|.+岛)?(?<town>[^区]+区|.+镇|.+村)?(?<village>.*)";
//    private static final String regex="(?<province>[^省]+自治区|[^市]+市|.+?省|.+?行政区|.+?地区|.+?州)(?<city>[^市]+市|.+?地区|.+?自治州)?(?<county>[^县]+县|.+?区|.+?市|.+?旗|.+?岛)?";

    /**
     * 解析地址
     * @param address
     * @return
     */
    public static Optional<Region> parser(String address){
        Matcher m= Pattern.compile(regex).matcher(address);
        String province,city,county,town,village;
        Region region = null;
        try {
            while(m.find()){
                region = new Region();
                province=m.group("province");
                region.setProvince(province==null?"":province.trim());
                city=m.group("city");
                region.setCity(city==null?"":city.trim());
                county=m.group("county");
                region.setCounty(county==null?"":county.trim());
                town=m.group("town");
                region.setTown(town==null?"":town.trim());
                village=m.group("village");
                region.setVillage(village==null?"":village.trim());
            }
        }catch (Exception ex){
            return Optional.empty();
        }
        return Optional.ofNullable(region);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Region region)) return false;
        return Objects.equals(getProvinceId(), region.getProvinceId()) && Objects.equals(getCityId(), region.getCityId()) && Objects.equals(getCountyId(), region.getCountyId()) && Objects.equals(getTownId(), region.getTownId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProvinceId(), getCityId(), getCountyId(), getTownId());
    }
}
