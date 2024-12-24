package io.github.buzzxu.spuddy.util;

import java.time.LocalDate;

import static io.github.buzzxu.spuddy.util.Dates.DATE_FORMAT_MINIFY_MONTH;


/**
 * @author xux
 * @date 2024年08月05日 16:31:10
 */
public class Echo {

    public static String period(LocalDate start, LocalDate end) {
        if (start.getYear() == end.getYear()) {
            // 同一年
            String startStr = DATE_FORMAT_MINIFY_MONTH.format(start);
            if (start.getMonthValue() == end.getMonthValue()) {
                // 同一年同一个月
                return startStr;
            } else {
                // 同年不同月
                return startStr + "-" + String.format("%02d", end.getMonthValue());
            }
        } else {
            // 跨年
            return DATE_FORMAT_MINIFY_MONTH.format(start) + "-" + DATE_FORMAT_MINIFY_MONTH.format(end);
        }
    }
}
