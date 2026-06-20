package net.adamgoodridge.sequencetrackplayer.utils;

import net.adamgoodridge.sequencetrackplayer.exceptions.errors.ServerError;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

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
}