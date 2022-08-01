package com.example.csvtransfer.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author qyl
 * @program DateUtil.java
 * @Description 获取日期
 * @createTime 2022-07-25 13:29
 */
public class DateUtil {
    public static DateTimeFormatter FORMAT_YMDH = DateTimeFormatter.ofPattern("yyyyMMddHH");
    public static DateTimeFormatter FORMAT_YMD_F = DateTimeFormatter.ofPattern("yyyyMMdd");

    public static LocalDateTime parseYMD(String dateTimeStr) {
        return LocalDate.parse(dateTimeStr, FORMAT_YMD_F).atStartOfDay();
    }

    public static Date localDateTime2Date(LocalDateTime localDateTime) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return Date.from(instant);
    }
}
