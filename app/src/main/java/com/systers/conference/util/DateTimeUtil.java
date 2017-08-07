package com.systers.conference.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateTimeUtil {
    public static final String FORMAT_24H = "HH:mm";
    private static final String FORMAT_12H = "hh:mm a";
    private static final String FORMAT_DATE = "yyyy-MM-dd";
    private static final String FORMAT_DATE_DESCRIPTIVE = "MMM dd, yyyy";

    private static final Locale defaultLocale = Locale.getDefault();

    private static SimpleDateFormat TIME_12H_FORMATTER;
    private static SimpleDateFormat TIME_24H_FORMATTER;
    private static SimpleDateFormat DATE_FORMATTER;
    private static SimpleDateFormat DATE_FORMATTER_DESC;

    private static Calendar calendar;

    static {
        instantiateFormatters();
    }

    private static void instantiateFormatters() {
        calendar = Calendar.getInstance();
        TimeZone timeZone = TimeZone.getDefault();
        calendar.add(Calendar.MILLISECOND, timeZone.getOffset(calendar.getTimeInMillis()));
        TIME_12H_FORMATTER = new SimpleDateFormat(FORMAT_12H, defaultLocale);
        TIME_24H_FORMATTER = new SimpleDateFormat(FORMAT_24H, defaultLocale);
        DATE_FORMATTER = new SimpleDateFormat(FORMAT_DATE, defaultLocale);
        DATE_FORMATTER_DESC = new SimpleDateFormat(FORMAT_DATE_DESCRIPTIVE, defaultLocale);
    }

    public static String getTimeFromTimeStamp(@NonNull String format, long timeStamp) {
        calendar.setTimeInMillis(timeStamp * 1000);
        Date date = calendar.getTime();
        return getFormatter(format).format(date);
    }

    @Nullable
    public static Date getDate(@NonNull String date) {
        try {
            return DATE_FORMATTER.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getDateDescriptive(@NonNull Date date) {
        return DATE_FORMATTER_DESC.format(date);
    }

    public static String formatDate(@NonNull Date date) {
        return DATE_FORMATTER.format(date);
    }

    private static SimpleDateFormat getFormatter(@NonNull String format) {
        // Match with pre-compiled formatters and instantiate new if not matched
        switch (format) {
            case FORMAT_12H:
                return TIME_12H_FORMATTER;
            case FORMAT_24H:
                return TIME_24H_FORMATTER;
            case FORMAT_DATE:
                return DATE_FORMATTER;
            default:
                return new SimpleDateFormat(format, defaultLocale);
        }
    }
}
