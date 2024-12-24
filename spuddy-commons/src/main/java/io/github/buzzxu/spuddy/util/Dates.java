package io.github.buzzxu.spuddy.util;

import com.google.common.collect.Lists;

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @program: 
 * @description:
 * @author: 徐翔
 * @create: 2020-02-12 23:01
 **/
public class Dates {

    public static DateTimeFormatter DATE_FORMAT_DATE_CN = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
    public static DateTimeFormatter DATE_FORMAT_DATETIME =  DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static DateTimeFormatter DATE_FORMAT_DATETIME_SSS =  DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    public static DateTimeFormatter DATE_FORMAT_DATE=  DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static DateTimeFormatter DATE_FORMAT_DATE_EN = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    public static DateTimeFormatter DATE_FORMAT_MINIFY_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");
    public static DateTimeFormatter DATE_FORMAT_MINIFY_MONTH=  DateTimeFormatter.ofPattern("yyyyMM");
    public static DateTimeFormatter DATE_MINIFY_FORMAT_DATETIME = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    public static DateTimeFormatter FORMT_YEAER = DateTimeFormatter.ofPattern("yyyy");
    public static DateTimeFormatter FORMT_MONTH = DateTimeFormatter.ofPattern("MM");
    public static String format(LocalDateTime localDateTime,DateTimeFormatter formatter){
        return localDateTime.format(formatter);
    }
    public static String format(LocalDate localDate,DateTimeFormatter formatter){
        return localDate.format(formatter);
    }


    public static Date asDate(Instant instant) {
        return Date.from(instant);
    }
    public static Date asDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 以当天的日期+LocalTime组成新的LocalDateTime转换为Date
     * @param localTime
     * @return
     */
    public static Date asDate(LocalTime localTime) {
        Objects.requireNonNull(localTime, "localTime");
        return Date.from(LocalDate.now().atTime(localTime).atZone(ZoneId.systemDefault()).toInstant());
    }
    public static Date asDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
    public static Date asDate(long epochMilli){
        Objects.requireNonNull(epochMilli, "epochMilli");
        return new Date(epochMilli);
    }

    @Deprecated
    public static LocalDateTime localDateTime(Date date){
        return asLocalDateTime(date,ZoneId.systemDefault());
    }
    @Deprecated
    public static LocalDateTime localDateTime(Date date,ZoneId zone){
        return asLocalDateTime(date, zone);
    }

    public static LocalDateTime asLocalDateTime(Timestamp timestamp){
        if(timestamp == null){
            return null;
        }
        return asLocalDateTime(timestamp.toInstant());
    }
    public static LocalDateTime asLocalDateTime(Date date){
        return asLocalDateTime(date,ZoneId.systemDefault());
    }
    public static LocalDateTime asLocalDateTime(Date date,ZoneId zone){
        return date == null ? null : LocalDateTime.ofInstant(date.toInstant(),zone);
    }
    public static LocalDateTime asLocalDateTime(LocalDate localDate) {
        Objects.requireNonNull(localDate, "localDate");
        return localDate.atStartOfDay();
    }
    /**
     * 以当天的日期+LocalTime组成新的LocalDateTime
     * @param localTime
     * @return
     */
    public static LocalDateTime asLocalDateTime(LocalTime localTime) {
        Objects.requireNonNull(localTime, "localTime");
        return LocalDate.now().atTime(localTime);
    }
    public static LocalDateTime asLocalDateTime(Instant instant) {
        return asLocalDateTime(instant,ZoneId.systemDefault());
    }
    public static LocalDateTime asLocalDateTime(Instant instant,ZoneId zone) {
        return LocalDateTime.ofInstant(instant, zone);
    }
    public static LocalDateTime asLocalDateTime(long epochMilli) {
        Objects.requireNonNull(epochMilli, "epochMilli");
        return asLocalDateTime(Instant.ofEpochMilli(epochMilli), ZoneId.systemDefault());
    }
    @Deprecated
    public static LocalDate localDate(Date date){
        return asLocalDate(date);
    }
    @Deprecated
    public static LocalDate localDate(Date date,ZoneId zone){
        return asLocalDate(date, zone);
    }
    public static LocalDate asLocalDate(Date date){
        return asLocalDate(date,ZoneId.systemDefault());
    }
    public static LocalDate asLocalDate(Date date,ZoneId zone){
        return date == null ? null : LocalDate.ofInstant(date.toInstant(),zone);
    }
    public static LocalDate asLocalDate(LocalDateTime localDateTime) {
        Objects.requireNonNull(localDateTime, "localDateTime");
        return localDateTime.toLocalDate();
    }
    public static LocalDate asLocalDate(Instant instant) {
        return asLocalDateTime(instant).toLocalDate();
    }
    public static LocalDate asLocalDate(TemporalAccessor temporal) {
        return LocalDate.from(temporal);
    }

    public static Instant asInstant(Date date) {
        return date == null ? null :Instant.ofEpochMilli(date.getTime());
    }
    public static Instant asInstant(LocalDateTime localDateTime) {
        return asInstant(localDateTime,ZoneId.systemDefault());
    }
    public static Instant asInstant(LocalDateTime localDateTime,ZoneId zone) {
        Objects.requireNonNull(localDateTime, "localDateTime");
        Objects.requireNonNull(zone, "zone");
        return localDateTime.atZone(zone).toInstant();
    }
    public static Instant asInstant(LocalDate localDate) {
        return asInstant(localDate,ZoneId.systemDefault());
    }
    public static Instant asInstant(LocalDate localDate,ZoneId zone) {
        Objects.requireNonNull(localDate, "localDate");
        Objects.requireNonNull(zone, "zone");
        return asLocalDateTime(localDate).atZone(zone).toInstant();
    }
    /**
     * 以当天的日期+LocalTime组成新的LocalDateTime转换为Instant
     * @param localTime
     * @return
     */
    public static Instant asInstant(LocalTime localTime) {
        return asInstant(localTime,ZoneId.systemDefault());
    }
    public static Instant asInstant(LocalTime localTime,ZoneId zone) {
        return asLocalDateTime(localTime).atZone(zone).toInstant();
    }
    public static Instant asInstant(long epochMilli) {
        return Instant.ofEpochMilli(epochMilli);
    }
    public static Instant toInstant(TemporalAccessor temporal) {
        return Instant.from(temporal);
    }

    public static ZonedDateTime asZonedDateTime(Date date) {
        return asZonedDateTime(date, ZoneId.systemDefault());
    }
    public static ZonedDateTime asZonedDateTime(Date date, ZoneId zone) {
        return date == null ? null : ZonedDateTime.ofInstant(date.toInstant(),zone);
    }
    public static ZonedDateTime asZonedDateTime(LocalDateTime localDateTime, String zoneId) {
        Objects.requireNonNull(localDateTime, "localDateTime");
        Objects.requireNonNull(zoneId, "zoneId");
        return localDateTime.atZone(ZoneId.of(zoneId));
    }
    public static ZonedDateTime asZonedDateTime(LocalDate localDate) {
        Objects.requireNonNull(localDate, "localDate");
        return localDate.atStartOfDay().atZone(ZoneId.systemDefault());
    }
    public static ZonedDateTime asZonedDateTime(LocalTime localTime) {
        Objects.requireNonNull(localTime, "localTime");
        return LocalDate.now().atTime(localTime).atZone(ZoneId.systemDefault());
    }
    public static ZonedDateTime asZonedDateTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).atZone(ZoneId.systemDefault());
    }
    public static ZonedDateTime asZonedDateTime(long epochMilli) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), ZoneId.systemDefault())
                .atZone(ZoneId.systemDefault());
    }
    public static ZonedDateTime asZonedDateTime(TemporalAccessor temporal) {
        return LocalDateTime.from(temporal).atZone(ZoneId.systemDefault());
    }


    public static long asEpochMilli(Date date){
        Objects.requireNonNull(date, "date");
        return date.getTime();
    }
    public static long asEpochMilli(LocalDateTime localDateTime){
        return asInstant(localDateTime).toEpochMilli();
    }
    public static long asEpochMilli(Instant instant){
        Objects.requireNonNull(instant, "instant");
        return instant.toEpochMilli();
    }
    /**
     * ZonedDateTime转毫秒值，注意，zonedDateTime时区必须和当前系统时区一致，不然会出现问题
     * 从1970-01-01T00:00:00Z开始的毫秒值
     * @param zonedDateTime
     * @return
     */
    public static long asEpochMilli(ZonedDateTime zonedDateTime) {
        Objects.requireNonNull(zonedDateTime, "zonedDateTime");
        return zonedDateTime.toInstant().toEpochMilli();
    }

    public static LocalDate startDayOfWeek(LocalDate today) {
        TemporalField fieldIso = WeekFields.of(DayOfWeek.MONDAY, 1).dayOfWeek();
        today = today.with(fieldIso, 1);
        return asLocalDate(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDate endDayOfWeek(LocalDate today) {
        TemporalField fieldIso = WeekFields.of(DayOfWeek.MONDAY, 1).dayOfWeek();
        today = today.with(fieldIso, 7);
        return asLocalDate(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDate startDayOfMonth(LocalDate today) {
        Month month = today.getMonth();
        return LocalDate.of(today.getYear(), month, 1);
    }

    public static LocalDate endDayOfMonth(LocalDate today) {
        Month month = today.getMonth();
        int length = month.length(today.isLeapYear());
        return LocalDate.of(today.getYear(), month, length);
    }

    public static LocalDate startDayOfQuarter(LocalDate today) {
        Month month = today.getMonth();
        Month firstMonthOfQuarter = month.firstMonthOfQuarter();
        return LocalDate.of(today.getYear(), firstMonthOfQuarter, 1);
    }

    public static LocalDate endDayOfQuarter(LocalDate today) {
        Month month = today.getMonth();
        Month firstMonthOfQuarter = month.firstMonthOfQuarter();
        Month endMonthOfQuarter = Month.of(firstMonthOfQuarter.getValue() + 2);
        return LocalDate.of(today.getYear(), endMonthOfQuarter, endMonthOfQuarter.length(today.isLeapYear()));
    }

    public static LocalDate startOfDayOfYear(LocalDate today) {
        return LocalDate.of(today.getYear(), Month.JANUARY, 1);
    }

    public static LocalDate endOfDayOfYear(LocalDate today) {
        return LocalDate.of(today.getYear(), Month.DECEMBER, Month.DECEMBER.length(today.isLeapYear()));
    }


    public static List<LocalDate> betweenDays(LocalDate start,LocalDate end){
        return between(start,end,now-> now.plusDays(1));
    }

    public static List<LocalDate> betweenMonths(LocalDate start,LocalDate end){
        return between(start,end,now-> now.plusMonths(1));
    }



    public static List<String> betweenDays(String start,String end,String format){
        return betweenDays(start,end,DateTimeFormatter.ofPattern(format));
    }
    public static List<String> betweenDays(LocalDate start,LocalDate end,DateTimeFormatter format){
        return between(start,end,now-> now.plusDays(1),format);
    }
    public static List<String> betweenDays(String start,String end,DateTimeFormatter format){
        return between(LocalDate.parse(start,DATE_FORMAT_DATE),LocalDate.parse(end,DATE_FORMAT_DATE),now-> now.plusDays(1),format);
    }
    public static List<String> betweenMonths(String start,String end,DateTimeFormatter format){
        return between(LocalDate.parse(start,DATE_FORMAT_DATE),LocalDate.parse(end,DATE_FORMAT_DATE),now-> now.plusMonths(1),format);
    }
    public static List<String> betweenMonths(LocalDate start,LocalDate end,DateTimeFormatter format){
        return between(start,end,now-> now.plusMonths(1),format);
    }

    public static List<LocalDate> between(LocalDate start, LocalDate end, Function<LocalDate,LocalDate> function){
        List<LocalDate> datas = Lists.newArrayListWithCapacity(10);
        datas.add(start);
        LocalDate now = start;
        while (end.isAfter(now)){
            now = function.apply(now);
            datas.add(now);
        }
        return datas;
    }

    public static List<String> between(LocalDate start,LocalDate end,Function<LocalDate,LocalDate> function,DateTimeFormatter format){
        List<String> datas = Lists.newArrayListWithCapacity(10);
        datas.add(start.format(format));
        LocalDate now = start;
        while (end.isAfter(now)){
            now = function.apply(now);
            datas.add(now.format(format));
        }
        return datas;
    }

    public static long unixtime(){
        return unixtime(Instant.now());
    }

    public static long unixtime(LocalDateTime localDateTime){
        return unixtime(asInstant(localDateTime));
    }

    public static long unixtime(Instant instant){
        return instant.getEpochSecond();
    }

    public static long utc(LocalDateTime localDateTime){
        return localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    /**
     * 间隔小时
     * @param startTime
     * @param endTime
     * @return
     */
    public static String hour(LocalDateTime startTime,LocalDateTime endTime){
        checkArgument(startTime != null , "请传入开始时间");
        checkArgument(endTime != null , "请传入结束时间");
        java.time.Duration duration = java.time.Duration.between(startTime, endTime);
        double hours = duration.toMillis() / 1000.0 / 60 / 60;
        return "%.2f".formatted(hours);
    }

    /**
     * 计算天数 同一天为1
     * @param start
     * @param end
     * @return
     */
    public static int days(Date start, Date end) {
       return daysλ(start,end)+1;
    }
    /**
     * 计算天数 同一天为1
     * @param start
     * @param end
     * @return
     */
    public static int days(LocalDateTime start, LocalDateTime end){
        return days(start.toLocalDate(),end.toLocalDate());
    }
    /**
     * 计算天数 同一天为1
     * @param start
     * @param end
     * @return
     */
    public static int days(LocalDate start, LocalDate end){
        return daysλ(start,end)+1;
    }

    /**
     * 计算天数 同一天为0
     * @param start
     * @param end
     * @return
     */
    public static int daysλ(LocalDate start, LocalDate end){
        return Period.between(start,end).getDays();
    }

    /**
     * 计算天数 同一天为0
     * @param start
     * @param end
     * @return
     */
    public static int daysλ(Date start, Date end) {
        long diff = start.getTime() - end.getTime();
        return (int) (diff / (24 * 60 * 60 * 1000));
    }


}
