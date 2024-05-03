package com.book.app.Utils;


import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
//lay thoi gian hien tai
public class DateUtils {
    public static String convertLocalDateToMmsString() {
        Instant instant = Instant.now();

        // Lấy milisecond từ Instant
        long milliseconds = instant.toEpochMilli();

        // Chuyển đổi thành chuỗi
        return String.valueOf(milliseconds);
    }
    public static String convertLocalDateToStringPattern(LocalDate localDate, String pattern) {
        if (localDate == null || pattern == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return localDate.format(formatter);
    }
    public static String convertLocalDateTimeToStringPattern(LocalDateTime dateTime, String pattern) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

        // Chuyển đổi LocalDateTime thành chuỗi theo mẫu đã cho
        return dateTime.format(formatter);

    }
}

