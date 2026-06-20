package net.adamgoodridge.sequencetrackplayer.utils;


import net.adamgoodridge.sequencetrackplayer.exceptions.errors.*;

import java.text.*;
import java.time.*;
import java.util.*;

public class TimeUtils {

    public static String getDay(String inputDate) {
        //gives a day of the day when given a date
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        Date dt1;
        try {
            dt1 = format1.parse(inputDate);
        } catch (ParseException e) {
            throw new ServerError("The date of" + inputDate + "cannot be parse to a day of week, please try again");
        }
        DateFormat format2 = new SimpleDateFormat("EEEE");
        return format2.format(dt1);
    }

    public static boolean isTodayInHolidayPeriod() {
        return isInHolidayPeriod(new LocalDateUtils().now());
    }

    public static boolean isInHolidayPeriod(LocalDate date) {
        // Dec 20 – Jan 2 inclusive, handles year wrap via MonthDay comparison
        MonthDay day = MonthDay.from(date);
        MonthDay dec20 = MonthDay.of(12, 20);
        MonthDay jan2 = MonthDay.of(1, 2);
        return !day.isBefore(dec20) || !day.isAfter(jan2);
    }
    public static String getRegexWithHolidayDate() {
        return  ".*20\\d{2}-(12-(2[0-9]|3[0-1])|01-0[1-2]).*";
    }
    public static String getRegexWithDayDirectory() {
        return  ".*20\\d{2}-[0-1][0-9]-[0-3][0-9].*";
    }
}
