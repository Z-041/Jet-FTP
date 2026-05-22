package com.ftp.util;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    private static final DateTimeFormatter MLSX_FORMATTER = DateTimeFormatter
            .ofPattern("yyyyMMddHHmmss", Locale.US)
            .withZone(ZoneId.systemDefault());

    private static final DateTimeFormatter LISTING_FORMATTER = DateTimeFormatter
            .ofPattern("MMM dd HH:mm", Locale.US)
            .withZone(ZoneId.systemDefault());

    private DateUtil() {
    }

    public static String formatMlsxTimestamp(long timestamp) {
        return MLSX_FORMATTER.format(Instant.ofEpochMilli(timestamp));
    }

    public static String formatMlsxTimestamp(Date date) {
        return formatMlsxTimestamp(date.getTime());
    }

    public static String formatListingTimestamp(long timestamp) {
        return LISTING_FORMATTER.format(Instant.ofEpochMilli(timestamp));
    }

    public static String formatListingTimestamp(Date date) {
        return formatListingTimestamp(date.getTime());
    }
}
