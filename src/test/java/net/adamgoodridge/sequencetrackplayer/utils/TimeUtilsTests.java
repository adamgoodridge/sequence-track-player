package net.adamgoodridge.sequencetrackplayer.utils;

import net.adamgoodridge.sequencetrackplayer.exceptions.errors.ServerError;
import net.adamgoodridge.sequencetrackplayer.statistic.DateRange;
import net.adamgoodridge.sequencetrackplayer.statistic.WeekDaySummary;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TimeUtilsTests {

    // --- getDay ---

    @ParameterizedTest
    @CsvSource({
            "2022-06-21, Tuesday",
            "2022-06-06, Monday",
            "2023-12-25, Monday",
            "2024-01-01, Monday",
            "2023-03-01, Wednesday"
    })
    void getDay_WithValidDate_ReturnsCorrectDayName(String input, String expected) {
        assertEquals(expected, TimeUtils.getDay(input));
    }

    @Test
    void getDay_WithInvalidDate_ThrowsServerError() {
        assertThrows(ServerError.class, () -> TimeUtils.getDay("not-a-date"));
    }

    // --- isInHolidayPeriod ---

    @ParameterizedTest(name = "{0} is in holiday period")
    @CsvSource({
            "2023-12-20",
            "2023-12-25",
            "2023-12-31",
            "2024-01-01",
            "2024-01-02"
    })
    void isInHolidayPeriod_WithDateInsidePeriod_ReturnsTrue(String date) {
        assertTrue(TimeUtils.isInHolidayPeriod(LocalDate.parse(date)));
    }

    @ParameterizedTest(name = "{0} is not in holiday period")
    @CsvSource({
            "2023-12-19",
            "2024-01-03",
            "2023-06-20",
            "2023-08-15"
    })
    void isInHolidayPeriod_WithDateOutsidePeriod_ReturnsFalse(String date) {
        assertFalse(TimeUtils.isInHolidayPeriod(LocalDate.parse(date)));
    }

    // --- isTodayInHolidayPeriod ---

    @Test
    void isTodayInHolidayPeriod_DoesNotThrow() {
        assertDoesNotThrow(TimeUtils::isTodayInHolidayPeriod);
    }

    // --- getRegexWithHolidayDate ---

    @ParameterizedTest(name = "''{0}'' matches holiday regex")
    @ValueSource(strings = {
            "/mnt/path/FeedB/2022/2022-12-20_Tuesday",
            "/mnt/path/FeedB/2022/2022-12-25_Christmas",
            "/mnt/path/FeedB/2022/2022-12-31_Saturday",
            "/mnt/path/FeedB/2023/2023-01-01_Sunday",
            "/mnt/path/FeedB/2023/2023-01-02_Monday"
    })
    void getRegexWithHolidayDate_MatchesHolidayPaths(String path) {
        assertTrue(path.matches(TimeUtils.getRegexWithHolidayDate()));
    }

    @ParameterizedTest(name = "''{0}'' does not match holiday regex")
    @ValueSource(strings = {
            "/mnt/path/FeedB/2022/2022-12-19_Monday",
            "/mnt/path/FeedB/2023/2023-01-03_Tuesday",
            "/mnt/path/FeedB/2022/2022-06-21_Tuesday",
            "/mnt/path/FeedB/2022/2022-11-25_Friday"
    })
    void getRegexWithHolidayDate_DoesNotMatchNonHolidayPaths(String path) {
        assertFalse(path.matches(TimeUtils.getRegexWithHolidayDate()));
    }

    // --- getRegexWithDayDirectory ---

    @ParameterizedTest(name = "''{0}'' matches day directory regex")
    @ValueSource(strings = {
            "/mnt/path/FeedB/2022/2022-06-21_Tuesday",
            "/mnt/path/FeedB/2022/2022-12-25_Christmas",
            "/mnt/path/FeedB/2023/2023-01-02_Monday",
            "2023-03-01_Wednesday"
    })
    void getRegexWithDayDirectory_MatchesDayDirectoryPaths(String path) {
        assertTrue(path.matches(TimeUtils.getRegexWithDayDirectory()));
    }

    @ParameterizedTest(name = "''{0}'' does not match day directory regex")
    @ValueSource(strings = {
            "/mnt/path/FeedB/2022/2022-06_June",
            "no-date-here",
            "FeedB"
    })
    void getRegexWithDayDirectory_DoesNotMatchNonDayDirectoryPaths(String path) {
        assertFalse(path.matches(TimeUtils.getRegexWithDayDirectory()));
    }

    // --- removeNotRequiredWeekDay ---

    // 2025-03-12 (Wednesday) – anchor date used across the test suite
    private static final Date WEDNESDAY = new Date(1741737600000L);
    private static final long DAY_MS    = 24 * 60 * 60 * 1000L;

    private static WeekDaySummary summary(String name) {
        return new WeekDaySummary(name, 0L, 0.0);
    }

    @Test
    void removeNotRequiredWeekDay_SingleDayRange_KeepsOnlyThatDay() {
        DateRange range = new DateRange(WEDNESDAY, WEDNESDAY);
        List<WeekDaySummary> all = List.of(
                summary("Monday"), summary("Wednesday"), summary("Friday"));

        List<WeekDaySummary> result = TimeUtils.removeNotRequiredWeekDay(range, all);

        assertEquals(1, result.size());
        assertEquals("Wednesday", result.getFirst().name());
    }

    @Test
    void removeNotRequiredWeekDay_MultiDayRange_KeepsDaysInRange() {
        // Wednesday → Friday (3 days: Wed, Thu, Fri)
        Date friday = new Date(WEDNESDAY.getTime() + 2 * DAY_MS);
        DateRange range = new DateRange(WEDNESDAY, friday);
        List<WeekDaySummary> all = List.of(
                summary("Monday"), summary("Wednesday"), summary("Thursday"),
                summary("Friday"), summary("Saturday"));

        List<WeekDaySummary> result = TimeUtils.removeNotRequiredWeekDay(range, all);

        assertEquals(3, result.size());
        assertTrue(result.stream().anyMatch(s -> s.name().equals("Wednesday")));
        assertTrue(result.stream().anyMatch(s -> s.name().equals("Thursday")));
        assertTrue(result.stream().anyMatch(s -> s.name().equals("Friday")));
    }

    @Test
    void removeNotRequiredWeekDay_FullWeekRange_KeepsAllDays() {
        Date sunday = new Date(WEDNESDAY.getTime() + 4 * DAY_MS); // Wed+4 = Sun
        Date previousSunday = new Date(WEDNESDAY.getTime() - 3 * DAY_MS); // Wed-3 = Sun
        DateRange range = new DateRange(previousSunday, sunday);
        List<WeekDaySummary> all = List.of(
                summary("Sunday"), summary("Monday"), summary("Tuesday"),
                summary("Wednesday"), summary("Thursday"), summary("Friday"), summary("Saturday"));

        List<WeekDaySummary> result = TimeUtils.removeNotRequiredWeekDay(range, all);

        assertEquals(7, result.size());
    }

    @Test
    void removeNotRequiredWeekDay_EmptyInputList_ReturnsEmpty() {
        DateRange range = new DateRange(WEDNESDAY, WEDNESDAY);

        List<WeekDaySummary> result = TimeUtils.removeNotRequiredWeekDay(range, List.of());

        assertTrue(result.isEmpty());
    }

    @Test
    void removeNotRequiredWeekDay_NoMatchingDays_ReturnsEmpty() {
        // Range is Wednesday only; list contains Saturday and Sunday
        DateRange range = new DateRange(WEDNESDAY, WEDNESDAY);
        List<WeekDaySummary> all = List.of(summary("Saturday"), summary("Sunday"));

        List<WeekDaySummary> result = TimeUtils.removeNotRequiredWeekDay(range, all);

        assertTrue(result.isEmpty());
    }
}