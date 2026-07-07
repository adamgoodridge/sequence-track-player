package net.adamgoodridge.sequencetrackplayer.utils;


import net.adamgoodridge.sequencetrackplayer.constanttext.*;
import net.adamgoodridge.sequencetrackplayer.exceptions.errors.*;
import net.adamgoodridge.sequencetrackplayer.statistic.*;
import org.jspecify.annotations.*;

import java.text.*;
import java.time.*;
import java.util.*;

public class TimeUtils {
    private static TimeUtils instance;
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String MONTH_FORMAT = "MMMM";
    private static final long DAY_IN_MILLISECONDS = 24 * 60 * 60 * 1000L;
    private static final String DAY_FORMAT = "EEEE";
    private static final ZoneId MELBOURNE_ZONE = ZoneId.of("Australia/Melbourne");
    public static String getDay(String inputDate) {
        //gives a day of the day when given a date
        SimpleDateFormat format1 = new SimpleDateFormat(DATE_FORMAT);
        Date dt1;
        try {
            dt1 = format1.parse(inputDate);
        } catch (ParseException _) {
            throw new ServerError("The date of" + inputDate + "cannot be parse to a day of week, please try again");
        }
        // use DAY_FORMAT so we return the day of week (e.g., "Monday")
        DateFormat format2 = new SimpleDateFormat(DAY_FORMAT);
        return format2.format(dt1);
    }
    public static int daysBetween(DateRange dateRange) {
        long differenceInMillis = dateRange.endDate().getTime() - dateRange.startDate().getTime();
        return (int) (differenceInMillis / DAY_IN_MILLISECONDS);
    }

    public static List<WeekDaySummary> removeNotRequiredWeekDay(DateRange dateRange, List<WeekDaySummary> weekDaySummaries) {
        //removes the days that are not in the date range
        List<String> daysInRange = getWeekdaysInRange(dateRange);
        return weekDaySummaries.stream()
                .filter(weekDaySummary -> daysInRange.contains(weekDaySummary.name()))
                .toList();
    }

    public static List<String> getWeekdaysInRange(DateRange dateRange) {
        if(daysBetween(dateRange) >=7)
            return Arrays.asList(ConstantText.DAYS_OF_WEEK);
        else
            return getSubWeekday(dateRange);
    }

    private static @NonNull List<String> getSubWeekday(DateRange dateRange) {
        Date start = dateRange.startDate();
        Date end = dateRange.endDate();

        Calendar cal = createCalendarFromDate(start);
        Calendar endCal = createCalendarFromDate(end);

        List<String> weekdays = new ArrayList<>();
        while (!cal.after(endCal)) {
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            weekdays.add(getDayName(dayOfWeek));
            cal.add(Calendar.DATE, 1);
        }
        return weekdays;
    }

    private static Calendar createCalendarFromDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    private static String getDayName(int day) {
		return switch (day) {
		    case Calendar.MONDAY -> "Monday";
		    case Calendar.TUESDAY -> "Tuesday";
		    case Calendar.WEDNESDAY -> "Wednesday";
		    case Calendar.THURSDAY -> "Thursday";
		    case Calendar.FRIDAY -> "Friday";
		    case Calendar.SATURDAY -> "Saturday";
		    case Calendar.SUNDAY -> "Sunday";
		    default -> "";
	    };
    }
    public String epochToDate(long epoch) {
        return formatDateByPattern(epoch, DATE_FORMAT);
    }

    public String dateToDate(Date date) {
        return formatDateByPattern(date.getTime(), DATE_FORMAT);
    }
    public String epochToDay(long epoch) {
        return formatDateByPattern(epoch, DAY_FORMAT);
    }
    public String dateToWeekDay(Date date) {
        return formatDateByPattern(date.getTime(), DAY_FORMAT);
    }
    private static String formatDateByPattern(long epoch, String pattern) {
        Date date = new Date(epoch);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }
    public Date dateFromString(String date) {
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
        try {
            return format.parse(date);
        } catch (ParseException _) {
            throw new ServerError("The date of" + date + "cannot be parse to a date, please try again");
        }
    }
    //Get the current date in the format of "yyyy-MM-dd_DayOfWeek"
    public Date getCurrentDate() {
        LocalDate melbourneToday = LocalDate.now(MELBOURNE_ZONE);
        return Date.from(melbourneToday.atStartOfDay(MELBOURNE_ZONE).toInstant());
    }

    public Date subtractDays(int days) {
        return new Date(getCurrentDate().getTime() - days * DAY_IN_MILLISECONDS);
    }
    public static TimeUtils getInstance() {
        if(instance == null) {
            instance = new TimeUtils();
        }
        return instance;
    }

    public String dateToMonth(Date date) {
        return formatDateByPattern(date.getTime(), MONTH_FORMAT);
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
        return  ConstantText.REGEX_HOLIDAY_DATE;
    }
    public static String getRegexWithDayDirectory() {
        return ConstantText.REGEX_DAY_DIRECTORY;
    }

    public Date getStartOfWeek() {
        return subtractDays(7);
    }
}
