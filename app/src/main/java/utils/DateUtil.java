package utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    // Formats a Calendar date to a string
    public static String formatDate(Calendar calendar, String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        return formatter.format(calendar.getTime());
    }

    // Overloaded method for Date object
    public static String formatDate(Date date, String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        return formatter.format(date);
    }

    // Parses a formatted date string back into a Calendar
    public static Calendar parseDate(String dateStr, String pattern) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        Date date = formatter.parse(dateStr);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }
}
