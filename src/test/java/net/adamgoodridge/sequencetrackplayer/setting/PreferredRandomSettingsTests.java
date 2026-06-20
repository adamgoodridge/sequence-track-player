package net.adamgoodridge.sequencetrackplayer.setting;

import net.adamgoodridge.sequencetrackplayer.*;
import net.adamgoodridge.sequencetrackplayer.constanttext.*;
import net.adamgoodridge.sequencetrackplayer.feeder.*;
import net.adamgoodridge.sequencetrackplayer.settings.*;

import org.junit.jupiter.api.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class PreferredRandomSettingsTests extends AbstractSpringBootTest {

	@Nested
	@DisplayName("Holiday Period Tests")
	class HolidayPeriodTests {
		@Test
		void holidayPeriod_duringHolidayPeriod_isDetected() {
			PreferredRandomSettings settings = PreferredRandomSettings.builder()
					.considerHolidayPeriod(HolidayPeriodConsideration.YES)
					.today(LocalDate.of(2023, 12, 25))
					.build();

			assertTrue(settings.isHolidayPeriod());
		}

		@Test
		void holidayPeriod_outsideHolidayPeriod_isNotDetected() {
			PreferredRandomSettings settings = PreferredRandomSettings.builder()
					.considerHolidayPeriod(HolidayPeriodConsideration.YES)
					.today(LocalDate.of(2023, 6, 15))
					.build();

			assertFalse(settings.isHolidayPeriod());
		}
	}


	@Nested
	@DisplayName("Integration Tests")
	class IntegrationTests {

		@Test
			void fullScenario_buildAndUseSettings() {
			PreferredRandomSettings settings = PreferredRandomSettings.builder()
					.dayOfWeek("Sunday")
					.time(500)
					.regularlyTrackChange(true)
					.trackCurrentCount(10)
					.considerHolidayPeriod(HolidayPeriodConsideration.YES)
					.today(LocalDate.of(2023, 12, 25))
					.build();

			assertEquals(DayOfWeek.SUNDAY, settings.getDayOfWeek());
			assertEquals(500, settings.getTime());
			assertTrue(settings.isRegularlyTrackChange());
			assertEquals(10, settings.getTrackCurrentCount());
			assertTrue(settings.isHolidayPeriod());

			// Test day filtering
			DataItem matchingDayItem = new DataItem("2023-01-15_Sunday.mp3");
			assertTrue(settings.shouldFilterByDayOfWeek(matchingDayItem));

			// Test month filtering
			DataItem monthItem = new DataItem("DecemberSpecial.mp3");
			assertTrue(settings.shouldFilterByMonth(monthItem));

			// Test gottenDay
			settings.gottendayOfWeek();
			assertEquals(DayOfWeek.ALL, settings.getDayOfWeek());
			assertFalse(settings.shouldFilterByDayOfWeek(new DataItem("2023-06-16_Other.mp3")));
		}

		@Test
		void fullScenario_outsideHolidayPeriod() {
			PreferredRandomSettings settings = PreferredRandomSettings.builder()
					.dayOfWeek(DayOfWeek.SUNDAY)
					.considerHolidayPeriod(HolidayPeriodConsideration.YES)
					.today(LocalDate.of(2023, 6, 15))
					.build();

			assertEquals(DayOfWeek.SUNDAY, settings.getDayOfWeek());
			assertFalse(settings.isHolidayPeriod());

			DataItem matchingDayItem = new DataItem("2023-06-15_Thursday.mp3");
			assertTrue(settings.shouldFilterByDayOfWeek(matchingDayItem));

			DataItem nonMatchingItem = new DataItem("2023-06-16_OtherFile.mp3");
			assertFalse(settings.shouldFilterByDayOfWeek(nonMatchingItem));

			DataItem monthItem = new DataItem("JuneSpecial.mp3");
			assertFalse(settings.shouldFilterByMonth(monthItem));
		}
	}
}

