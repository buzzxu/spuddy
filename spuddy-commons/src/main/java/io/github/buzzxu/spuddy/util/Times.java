package io.github.buzzxu.spuddy.util;

import org.apache.commons.lang3.time.DateUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * @program: 
 * @description:
 * @author: 徐翔
 * @create: 2019-10-08 20:58
 **/
public class Times {
    private static final DateTimeFormatter YYYY_MM_DD_HH_MM_SS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter HH_MM_SS = DateTimeFormatter.ofPattern("HH:mm:ss");


    /**
     * yyyy-MM-dd
     *
     * @param date
     * @return
     */
    public static String date(LocalDate date) {
        return date.format(YYYY_MM_DD);
    }

    /**
     * LocalDateTime 格式化 yyyy-MM-dd HH:mm:ss
     * @param dateTime
     * @return
     */
    public static String datetime(LocalDateTime dateTime) {
        return dateTime.format(YYYY_MM_DD_HH_MM_SS);
    }

    public static String date(Date dateTime) {
        return date(toLocalDate(dateTime));
    }

    /**
     * Date 格式化 yyyy-MM-dd HH:mm:ss
     * @param dateTime
     * @return
     */
    public static String datetime(Date dateTime) {
        return datetime(toLocalDateTime(dateTime));
    }

    /**
     * Date 转  LocalDateTime
     * @param dateTime
     * @return
     */
    public static LocalDateTime toLocalDateTime(Date dateTime){
        return dateTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * Date 转  LocalDate
     * @param dateTime
     * @return
     */
    public static LocalDate toLocalDate(Date dateTime){
        return dateTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
    /**
     * Date 转 LocalTime
     * @param dateTime
     * @return
     */
    public static LocalTime toLocalTime(Date dateTime){
        return dateTime.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
    }

    /**
     * LocalDateTime 转 Date
     * @param localDateTime
     * @return
     */
    public static Date toDate(LocalDateTime localDateTime){
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
    /**
     * @return 返回格式yyyy-MM-dd
     */
    public static String date() {
        return date(LocalDate.now());
    }
    /**
     * @return 返回格式 yyyy-MM-dd HH:mm:ss
     */
    public static String dateTime() {
        return datetime(LocalDateTime.now());
    }

    /**
     * @return 返回当前年份
     */
    public static int year() {
        return LocalDate.now().getYear();
    }

    /**
     * @return 返回当前月份
     */
    public static int month() {
        return LocalDate.now().getMonthValue();
    }

    /**
     * @return 返回今日是月份中的第几天
     */
    public static int dayOfMonth() {
        return LocalDate.now().getDayOfMonth();
    }



    /**
     * @return 返回当前时间 HH:mm:ss.xxx
     */
    public static String time() {
        return LocalTime.now().format(HH_MM_SS);
    }

    /**
     * @return 当前小时
     */
    public static int hour() {
        return LocalTime.now().getHour();
    }

    /**
     * @return 当前分钟
     */
    public static int minute() {
        return LocalTime.now().getMinute();
    }

    /**
     * @return 当前秒
     */
    public static int second() {
        return LocalTime.now().getSecond();
    }



    /**
     * @param time
     *            从1970-01-01T00:00:00到现在的毫秒数
     */
    public static LocalDateTime parseDateTime(long time) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
    }

    /**
     * @param time
     *            yyyy-MM-dd HH:mm:ss
     */
    public static LocalDateTime parseDateTime(String time) {
        return LocalDateTime.parse(time, YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * @param time
     *            从1970-01-01T00:00:00到现在的毫秒数
     * @return
     */
    public static LocalDate parseDate(long time) {
        return LocalDate.from(parseDateTime(time));
    }

    /**
     * @param time
     *            yyyy-MM-dd
     * @return
     */
    public static LocalDate parseDate(String time) {
        return LocalDate.parse(time, YYYY_MM_DD);
    }



    /**
     * 转化为毫秒，默认为
     * @param date
     * @return
     */
    public static long millis(LocalDate date) {
        return millis(date.atStartOfDay());
    }

    /**
     * 转化为毫秒
     * @param dateTime
     * @return
     */
    public static long millis(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * @return 返回今日是一年中的第几天
     */
    public static int dayOfYear() {
        return LocalDate.now().getDayOfYear();
    }

    /**
     * @return 返回今日星期几 1-7
     */
    public static int dayOfWeek() {
        return LocalDate.now().getDayOfWeek().getValue();
    }

    /**
     * @param year
     * @param month
     * @param day
     * @return 返回指定日期
     */
    public static String newDate(int year, int month, int day) {
        return LocalDate.of(year, month, day).toString();
    }

    /**
     * @param year
     *            指定年份
     * @param day
     *            指定年份中的天数
     * @return 根据条件返回日期
     */
    public static String newDate(int year, int day) {
        return LocalDate.ofYearDay(year, day).toString();
    }


    public static String[] range(int days){
        LocalDateTime now = LocalDateTime.now();
        return range(now,days);
    }
    public static String[] range(LocalDateTime endTime,int days){
        return new String[]{endTime.plusDays(-days).format(Dates.DATE_FORMAT_DATE) + " 00:00:00",endTime.format(Dates.DATE_FORMAT_DATE) + " 23:59:59"};
    }
    public static String[] range(LocalDate endDay,int days){
        return new String[]{endDay.plusDays(-days).format(Dates.DATE_FORMAT_DATE) + " 00:00:00",endDay.format(Dates.DATE_FORMAT_DATE) + " 23:59:59"};
    }


    public static String friendlyTimeSpanByNow(long timeStampMillis) {
        long now = Instant.now().toEpochMilli();
        long span = now - timeStampMillis;
        if (span < 0L) {
            return String.format("%tc", timeStampMillis);
        } else if (span < 1000L) {
            return "刚刚";
        } else if (span < 60000L) {
            return String.format("%d秒前", span / 1000L);
        } else if (span < 3600000L) {
            return String.format("%d分钟前", span / 60000L);
        } else {
            long wee = DateUtils.truncate(new Date(now), 5).getTime();
            if (timeStampMillis >= wee) {
                return String.format("今天%tR", timeStampMillis);
            } else {
                return timeStampMillis >= wee - 86400000L ? String.format("昨天%tR", timeStampMillis) : String.format("%tF", timeStampMillis);
            }
        }
    }

    /**
     * 当日剩余秒数
     * @param time
     * @return
     */
    public static long remainSecondsOneDay(LocalDateTime time) {
        LocalDateTime midnight = time.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        long seconds = ChronoUnit.SECONDS.between(time, midnight);
        return  seconds;
    }

    /**
     * 剩余描述
     * @param time
     * @param days
     * @return
     */
    public static long remainSeconds(LocalDateTime time,int days) {
        LocalDateTime midnight = time.plusDays(days+1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        long seconds = ChronoUnit.SECONDS.between(time, midnight);
        return  seconds;
    }

    public static String secondsFormt(int seconds) {
        int h = seconds / 3600;
        int m = (seconds % 3600) / 60;
        int s = (seconds % 3600) % 60;
        if (h > 0) {
            return h + "小时" + m + "分钟" + s + "秒";
        }
        if (m > 0) {
            return m + "分钟" + s + "秒";
        }
        return s + "秒";
    }

    /**
     * 不显示秒
     * @param seconds
     * @return
     */
    public static String secondsToFormtλ(int seconds) {
        int h = seconds / 3600;
        int m = (seconds % 3600) / 60;
        int s = (seconds % 3600) % 60;
        if (h > 0) {
            return h + "小时" + m + "分钟";
        }
        if (m > 0) {
            return m + "分钟";
        }
        return s + "秒";
    }

    // 判断当前时间是否在日出和日落之间
    public static boolean isDaytime() {
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        // 获取日出时间和日落时间（这里用随机值代替）
        LocalTime sunrise = LocalTime.of(6, 0); // 假设日出时间是早上6点
        LocalTime sunset = LocalTime.of(18, 0); // 假设日落时间是晚上6点
        return between(now.toLocalTime(), sunrise, sunset);
    }
    // 判断当前时间是否在开始和结束时间之间
    public static boolean between(LocalTime now, LocalTime startTime, LocalTime endTime) {
        return !now.isBefore(startTime) && !now.isAfter(endTime);
    }

    public static String hello(){
        LocalTime time = LocalTime.now();
        if (time.isBefore(LocalTime.of(5, 0))) {
            return "夜已深,注意身体";
        } else if (time.isBefore(LocalTime.NOON)) {
            return "早安";
        } else if (time.isBefore(LocalTime.of(18, 0))) {
            return "下午好";
        } else {
            return "晚上好";
        }
    }


}
