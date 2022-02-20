package com.xjh.common.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.FastDateFormat;

public class MyDateUtils {
    private static final DateTimeFormatter localTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final FastDateFormat HH_mm_ss_SSS = FastDateFormat.getInstance("HH:mm:ss.SSS");
    private static final DateTimeFormatter localDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter localDateSimpleFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter localDateFormatterAmPm = DateTimeFormatter.ofPattern("yyyy-MM-dda");
    private static final DateTimeFormatter localDateHourFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
    private static final DateTimeFormatter localDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter localDateTimeSimpleFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    public MyDateUtils() {
    }

    public static void main(String[] args) {
        String format = LocalDateTime.now().format(localDateTimeSimpleFormatter);
        System.out.println(format);
    }

    public static String formatLocalDateYYYYMMDDHHMMSSSSS(LocalDateTime date) {
        return date.format(localDateTimeSimpleFormatter);
    }

    public static String formatLocalDateYYYYMMDDHH() {
        return formatLocalDateYYYYMMDDHH((LocalDateTime) null);
    }

    public static String formatLocalDateYYYYMMDDHH(LocalDateTime date) {
        if (date == null) {
            date = LocalDateTime.now();
        }

        return date.format(localDateHourFormatter);
    }

    public static String formatLocalDateYYYYMMDD() {
        return formatLocalDateYYYYMMDD((LocalDate) null);
    }

    public static String formatLocalDateYYYYMMDD_AMPM() {
        LocalDate date = LocalDate.now();
        return date.format(localDateFormatterAmPm);
    }

    public static String formatLocalDateYYYYMMDD(LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }

        return date.format(localDateFormatter);
    }

    public static String formatSimpleLocalDateYYYYMMDD(LocalDate date) {
        return date.format(localDateSimpleFormatter);
    }

    public static String formatYYYYMMDDHHMMSS() {
        return formatYYYYMMDDHHMMSS((Date) null);
    }

    public static String formatYYYYMMDDHHMMSS(Date date) {
        if (date == null) {
            date = new Date();
        }

        return FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss").format(date);
    }

    public static String formatHHMMSS() {
        return formatHHMMSS((Date) null);
    }

    public static String formatHHMMSS(Date date) {
        if (date == null) {
            date = new Date();
        }

        return DateFormatUtils.ISO_8601_EXTENDED_TIME_FORMAT.format(date);
    }

    public static String formatHHMMSSSSS(Date date) {
        if (date == null) {
            date = new Date();
        }

        return HH_mm_ss_SSS.format(date);
    }

    public static String localTimeToStr(LocalTime time) {
        return time.format(localTimeFormatter);
    }

    public static boolean judgeTimeSlot(LocalTime timeStart, LocalTime timeEnd, LocalTime time) {
        return (timeStart.isBefore(time) || timeStart.equals(time)) && (timeEnd.isAfter(time) || timeEnd.equals(time));
    }

    public static boolean judgeTimeSlotNotEqual(LocalTime timeStart, LocalTime timeEnd, LocalTime time) {
        return timeStart.isBefore(time) && timeEnd.isAfter(time);
    }

    public static boolean isTimeBetween(LocalTime from, LocalTime to) {
        LocalTime now = LocalTime.now();
        return !now.isBefore(from) && !now.isAfter(to);
    }

    public static Date addDays(int offsetDay) {
        return DateUtils.addDays(new Date(), offsetDay);
    }

    public static Date addDays(Date d, int offsetDay) {
        return DateUtils.addDays(d, offsetDay);
    }

    public static String formatLocalDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            dateTime = LocalDateTime.now();
        }

        return dateTime.format(localDateTimeFormatter);
    }

    public static LocalDateTime toLocalDateTimeYYYYMMddHHmmss(String timeStr) {
        return LocalDateTime.parse(timeStr, localDateTimeFormatter);
    }

    public static LocalDate toLocalDateYYYYMMdd(String timeStr) {
        return LocalDate.parse(timeStr, localDateFormatter);
    }

    public static boolean isLeapYear(int year) {
        return year % 4 == 0 && year % 100 != 0 || year % 400 == 0;
    }

    public static int dateDiff(LocalDateTime dt1, LocalDateTime dt2) {
        long t1 = dt1.toEpochSecond(ZoneOffset.ofHours(0));
        long day1 = t1 / 86400L;
        long t2 = dt2.toEpochSecond(ZoneOffset.ofHours(0));
        long day2 = t2 / 86400L;
        return (int) (day2 - day1);
    }

    public static boolean isTimeBetween(LocalDateTime from, LocalDateTime to) {
        LocalDateTime now = LocalDateTime.now();
        return !now.isBefore(from) && !now.isAfter(to);
    }

    public static boolean isTimeBetween(LocalDateTime from, LocalDateTime to, LocalDateTime current) {
        return !current.isBefore(from) && !current.isAfter(to);
    }
}

