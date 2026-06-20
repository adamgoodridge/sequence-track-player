package net.adamgoodridge.sequencetrackplayer.setting;

import net.adamgoodridge.sequencetrackplayer.settings.*;
import org.junit.jupiter.api.Test;

import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

class HolidayPeriodConsiderationTests {

	@Test
	void fromString_withYes_returnsYES() {
		assertEquals(HolidayPeriodConsideration.YES, HolidayPeriodConsideration.fromString("yes"));
		assertEquals(HolidayPeriodConsideration.YES, HolidayPeriodConsideration.fromString("YES"));
		assertEquals(HolidayPeriodConsideration.YES, HolidayPeriodConsideration.fromString("Yes"));
		assertEquals(HolidayPeriodConsideration.YES, HolidayPeriodConsideration.fromString(" yes "));
	}

	@Test
	void fromString_withNo_returnsNO() {
		assertEquals(HolidayPeriodConsideration.NEVER, HolidayPeriodConsideration.fromString("never"));
		assertEquals(HolidayPeriodConsideration.NEVER, HolidayPeriodConsideration.fromString("NEVER"));
		assertEquals(HolidayPeriodConsideration.NEVER, HolidayPeriodConsideration.fromString("Never"));
	}
	@Test
	void fromString_withAlways_returnsALWAYS() {
		assertEquals(HolidayPeriodConsideration.ALWAYS, HolidayPeriodConsideration.fromString("always"));
		assertEquals(HolidayPeriodConsideration.ALWAYS, HolidayPeriodConsideration.fromString("ALWAYS"));
		assertEquals(HolidayPeriodConsideration.ALWAYS, HolidayPeriodConsideration.fromString("Always"));
		assertEquals(HolidayPeriodConsideration.ALWAYS, HolidayPeriodConsideration.fromString(" always "));
	}

	@Test
	void fromString_withNull_returnsNO() {
		assertEquals(HolidayPeriodConsideration.NEVER, HolidayPeriodConsideration.fromString(null));
	}

	@Test
	void fromString_withInvalidValue_returnsNO() {
		assertEquals(HolidayPeriodConsideration.NEVER, HolidayPeriodConsideration.fromString("invalid"));
		assertEquals(HolidayPeriodConsideration.NEVER, HolidayPeriodConsideration.fromString(""));
	}

	@Test
	void isEnabled_returnsCorrectValue() {
			LocalDate date = LocalDate.of(2022,12,24);
			assertTrue(HolidayPeriodConsideration.YES.shouldConsiderHolidayPeriod(date));
			assertFalse(HolidayPeriodConsideration.NEVER.shouldConsiderHolidayPeriod(date));
	}
	@Test
	void isEnabled_withAlways_returnsTrue() {
			LocalDate date = LocalDate.of(2022, 11, 24);
			assertTrue(HolidayPeriodConsideration.ALWAYS.shouldConsiderHolidayPeriod(date));
	}
}
