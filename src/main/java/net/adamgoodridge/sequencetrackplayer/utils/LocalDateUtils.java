package net.adamgoodridge.sequencetrackplayer.utils;

/*
 * This wrapper class is used to avoid a dependency on java.time.LocalDate so it can be easily mocked in tests. It can be extended in the future to include more date-related functionality if needed.
 */
public class LocalDateUtils {
	public LocalDateUtils() {
		// Default constructor
	}

	public java.time.LocalDate now() {
		return java.time.LocalDate.now();
	}
}
