package io.github.buzzxu.spuddy.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;

import static java.util.stream.Collectors.groupingBy;

/**
 * @author xux
 * @date 2024年05月15日 14:23:23
 */

public class TodayHourRange{


    private static List<Tuple3<String, Integer, Integer>> tags = List.of(Tuple3.of("00:00", 0, 5)
            , Tuple3.of("06:00", 6, 7)
            , Tuple3.of("08:00", 8, 9)
            , Tuple3.of("10:00", 10, 11)
            , Tuple3.of("12:00", 12, 13)
            , Tuple3.of("14:00", 14, 15)
            , Tuple3.of("16:00", 16, 17)
            , Tuple3.of("18:00", 18, 19)
            , Tuple3.of("20:00", 20, 21)
            , Tuple3.of("24:00", 22, 23));

    public static <T extends Number> List<T> of(List<TodayHourData<T>> datas,T defVal, BinaryOperator<T> accumulator){
        Map<String,List<TodayHourData<T>>> _datas = datas.stream().collect(groupingBy(v -> tags.stream().filter(tag -> tag.getT2() >= v.hour && v.hour <= tag.getT3()).map(Tuple3::getT1).findFirst().orElse("-")));
        List<T> _data = new ArrayList<>(10);
        tags.forEach(tag -> {
            List<TodayHourData<T>> data = _datas.get(tag.getT1());
            T value = data != null ? data.stream().map(v->v.value).reduce(accumulator).orElse(defVal) : defVal;
            _data.add(value);
        });
        return _data;
    }

    public static <T extends Number> TodayHourData<T> data(int hour, T value){
        return TodayHourData.of(hour,value);
    }

    public static List<String> tags(){
        return tags.stream().map(Tuple3::getT1).toList();
    }
    @Getter  @NoArgsConstructor @AllArgsConstructor
    public static class TodayHourData<T extends Number>{
        private int hour;
        private T value;

        public static <T extends Number> TodayHourData<T> of(int hour, T value){
            return new TodayHourData<>(hour,value);
        }

    }
}
