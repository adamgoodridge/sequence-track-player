package net.adamgoodridge.sequencetrackplayer.utils;

import net.adamgoodridge.sequencetrackplayer.exceptions.errors.ServerError;
import org.junit.jupiter.api.Test;

import java.text.*;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
/*
 This is learning test for TimeUtils, it is not meant to be exhaustive but to give confidence that the methods work as expected
 and to catch any major issues with date formatting or parsing.
 It also serves as a reference for how to use the TimeUtils methods in other parts of the codebase.
 */
public class TimeUtilsTest {

    @Test
    public void testEpochToDate_and_epochToDay() {
        // build a calendar in the default timezone to avoid timezone surprises
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2021);
        cal.set(Calendar.MONTH, Calendar.SEPTEMBER); // September
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        long epoch = cal.getTimeInMillis();
        TimeUtils tu = TimeUtils.getInstance();

        String expectedDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date(epoch));
        String expectedDay = new SimpleDateFormat("EEEE").format(new Date(epoch));

        assertEquals(expectedDate, tu.epochToDate(epoch), "epochToDate should match expected formatted date");
        assertEquals(expectedDay, tu.epochToDay(epoch), "epochToDay should match expected day of week");
    }

    @Test
    public void testGetDay_validDate() {
        // 2021-09-01 is used above; compute expected day via SimpleDateFormat to match implementation
        Long input = 1630454400000L; // This is the epoch time for 2021-09-01 00:00:00 UTC
        String expectedDay = "Wednesday"; // 2021-09-01 was a Wednesday
        assertEquals(expectedDay, TimeUtils.getInstance().epochToDay(input), "getDay should return the correct day of week for a valid date");
    }
    @Test
    public void testGetDay_invalidDateThrows() {
        assertThrows(ServerError.class, () -> TimeUtils.getDay("not-a-date"));
    }
}
