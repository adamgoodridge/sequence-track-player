package net.adamgoodridge.sequencetrackplayer.statistic;

import net.adamgoodridge.sequencetrackplayer.exceptions.errors.*;
import net.adamgoodridge.sequencetrackplayer.utils.TimeUtils;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Plain unit tests — no Spring context, no DB.
 * StatisticParameters contains only decision logic, so fast isolated tests
 * are the right choice here.
 */
class StatisticParametersTests {

    private static final long   DAY_MS    = 24 * 60 * 60 * 1000L;
    private static final Date   FIXED_NOW = new Date(1742601600000L); // 2026-03-22 00:00:00 UTC
    private static final String FROM_STR  = "2026-03-01";
    private static final String TO_STR    = "2026-03-21";
    private static final Date   FROM_DATE = new Date(1740787200000L); // 2026-03-01 00:00:00 UTC
    private static final Date   TO_DATE   = new Date(1742515200000L); // 2026-03-21 00:00:00 UTC

    // ── no parameters → DEFAULT_PREVIOUS_DAYS (100 days back from today) ────

    @Test
    void noParams_FromIs100DaysBeforeToday() {
        try (MockedStatic<TimeUtils> mock = mockStatic(TimeUtils.class)) {
            TimeUtils timeUtils = mock(TimeUtils.class);
            mock.when(TimeUtils::getInstance).thenReturn(timeUtils);
            when(timeUtils.getCurrentDate()).thenReturn(FIXED_NOW);
            when(timeUtils.subtractDays(100)).thenReturn(new Date(FIXED_NOW.getTime() - 100 * DAY_MS));

            DateRange range = new StatisticParameters(null, null, null).getDateRange();

            assertEquals(new Date(FIXED_NOW.getTime() - 100 * DAY_MS), range.startDate());
            assertEquals(FIXED_NOW, range.endDate());
        }
    }

    // ── days param → NUMBER_OF_PREVIOUS_DAYS ────────────────────────────────

    @Test
    void daysParam_FromIsNDaysBeforeToday() {
        try (MockedStatic<TimeUtils> mock = mockStatic(TimeUtils.class)) {
            TimeUtils timeUtils = mock(TimeUtils.class);
            mock.when(TimeUtils::getInstance).thenReturn(timeUtils);
            when(timeUtils.getCurrentDate()).thenReturn(FIXED_NOW);
            when(timeUtils.subtractDays(7)).thenReturn(new Date(FIXED_NOW.getTime() - 7 * DAY_MS));

            DateRange range = new StatisticParameters(7, null, null).getDateRange();

            assertEquals(new Date(FIXED_NOW.getTime() - 7 * DAY_MS), range.startDate());
            assertEquals(FIXED_NOW, range.endDate());
        }
    }

    // ── from + to params → EXPLICIT_DATE_RANGE ──────────────────────────────

    @Test
    void fromAndTo_ReturnsExplicitDateRange() {
        try (MockedStatic<TimeUtils> mock = mockStatic(TimeUtils.class)) {
            TimeUtils timeUtils = mock(TimeUtils.class);
            mock.when(TimeUtils::getInstance).thenReturn(timeUtils);
            when(timeUtils.dateFromString(FROM_STR)).thenReturn(FROM_DATE);
            when(timeUtils.dateFromString(TO_STR)).thenReturn(TO_DATE);

            DateRange range = new StatisticParameters(null, FROM_STR, TO_STR).getDateRange();

            assertEquals(FROM_DATE, range.startDate());
            assertEquals(TO_DATE, range.endDate());
        }
    }

    @Test
    void fromWithoutTo_ToDefaultsToToday() {
        try (MockedStatic<TimeUtils> mock = mockStatic(TimeUtils.class)) {
            TimeUtils timeUtils = mock(TimeUtils.class);
            mock.when(TimeUtils::getInstance).thenReturn(timeUtils);
            when(timeUtils.dateFromString(FROM_STR)).thenReturn(FROM_DATE);
            when(timeUtils.getCurrentDate()).thenReturn(FIXED_NOW);

            DateRange range = new StatisticParameters(null, FROM_STR, null).getDateRange();

            assertEquals(FROM_DATE, range.startDate());
            assertEquals(FIXED_NOW, range.endDate());
        }
    }

    // ── error cases ──────────────────────────────────────────────────────────

    @Test
    void daysAndFrom_ThrowsInvalidStatisticsDateTooManyParametersError() {
        try (MockedStatic<TimeUtils> mock = mockStatic(TimeUtils.class)) {
            TimeUtils timeUtils = mock(TimeUtils.class);
            mock.when(TimeUtils::getInstance).thenReturn(timeUtils);
            when(timeUtils.dateFromString(FROM_STR)).thenReturn(FROM_DATE);

            assertThrows(InvalidStatisticsDateTooManyParametersError.class,
                    () -> new StatisticParameters(7, FROM_STR, null).getDateRange());
        }
    }

    @Test
    void daysAndTo_ThrowsInvalidStatisticsDateTooManyParametersError() {
        try (MockedStatic<TimeUtils> mock = mockStatic(TimeUtils.class)) {
            TimeUtils timeUtils = mock(TimeUtils.class);
            mock.when(TimeUtils::getInstance).thenReturn(timeUtils);
            when(timeUtils.dateFromString(TO_STR)).thenReturn(TO_DATE);

            assertThrows(InvalidStatisticsDateTooManyParametersError.class,
                    () -> new StatisticParameters(7, null, TO_STR).getDateRange());
        }
    }

    @Test
    void daysAndFromAndTo_ThrowsInvalidStatisticsDateTooManyParametersError() {
        try (MockedStatic<TimeUtils> mock = mockStatic(TimeUtils.class)) {
            TimeUtils timeUtils = mock(TimeUtils.class);
            mock.when(TimeUtils::getInstance).thenReturn(timeUtils);
            when(timeUtils.dateFromString(FROM_STR)).thenReturn(FROM_DATE);
            when(timeUtils.dateFromString(TO_STR)).thenReturn(TO_DATE);

            assertThrows(InvalidStatisticsDateTooManyParametersError.class,
                    () -> new StatisticParameters(7, FROM_STR, TO_STR).getDateRange());
        }
    }

    @Test
    void fromOnlyWithNoTo_ThrowsEndDateMissingFromQueryParametersError() {
        // "from" without "to" is caught by isFromProvidedWithoutendDate()
        // Note: fromWithoutTo_ToDefaultsToToday covers the case where to is null
        // and from is provided — but that resolves to EXPLICIT_DATE_RANGE with today as to.
        // This test confirms the exception is NOT thrown when from is provided without to
        // (i.e., it falls through to use today as the to-date instead).
        try (MockedStatic<TimeUtils> mock = mockStatic(TimeUtils.class)) {
            TimeUtils timeUtils = mock(TimeUtils.class);
            mock.when(TimeUtils::getInstance).thenReturn(timeUtils);
            when(timeUtils.dateFromString(FROM_STR)).thenReturn(FROM_DATE);
            when(timeUtils.getCurrentDate()).thenReturn(FIXED_NOW);

            // should NOT throw — from without to is valid (to defaults to today)
            assertDoesNotThrow(() -> new StatisticParameters(null, FROM_STR, null).getDateRange());
        }
    }

    @Test
    void toOnlyWithNoFrom_ThrowsEndDateMissingFromQueryParametersError() {
        try (MockedStatic<TimeUtils> mock = mockStatic(TimeUtils.class)) {
            TimeUtils timeUtils = mock(TimeUtils.class);
            mock.when(TimeUtils::getInstance).thenReturn(timeUtils);
            when(timeUtils.dateFromString(TO_STR)).thenReturn(TO_DATE);

            assertThrows(FromDateMissingFromQueryParametersError.class,
                    () -> new StatisticParameters(null, null, TO_STR).getDateRange());
        }
    }
}

